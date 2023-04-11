package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.createScissor;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.resetScissor;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.event.component.ChangeSizeEvent;
import org.liquidengine.legui.component.misc.listener.widget.WidgetResizeButtonDragListener;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.renderer.nvg.component.NvgDefaultComponentRenderer;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.update.UpdateType;

public class UVViewer extends Widget {

    public static UVViewer INSTANCE;
    public static Face SELECTED = NoFace.NONE;

    private UVViewer(){
        getTitleTextState().setText(translate("uvviewer.title"));
        int width = 552, height = 599;
        setSize(width, height);
        setPosition(FMT.WIDTH / 2 - width / 2, FMT.HEIGHT / 2 - height / 2);
        Settings.applyComponentTheme(getContainer());
        UpdateHolder holder = new UpdateHolder();
        Component con = getContainer();
        //
        ScrollablePanel panel = new ScrollablePanel(15.0F, 42.0F, 522.0F, 522.0F);
        View view = new View(panel, 0.0F, 0.0F, 512.0F, 512.0F, holder.sub());
        panel.getContainer().add(view);
        panel.getContainer().setSize(512.0F, 512.0F);
        con.add(panel);
        con.add(new Icon(0, 32, 8, 15, 5, "./resources/textures/icons/uvviewer/borders.png", () -> {}).addTooltip("uvviewer.button.borders"));
        con.add(new Icon(1, 32, 8, 15, 5, "./resources/textures/icons/uvviewer/only_pos.png", () -> {}).addTooltip("uvviewer.button.only_pos"));
        con.add(new Icon(2, 32, 8, 15, 5, "./resources/textures/icons/uvviewer/texture.png", () -> {}).addTooltip("uvviewer.button.texture"));
        con.add(new Icon(0, 32, 8, 160, 5, "./resources/textures/icons/uvviewer/zoom_in.png", () -> {}).addTooltip("uvviewer.button.zoom_in"));
        con.add(new Icon(1, 32, 8, 160, 5, "./resources/textures/icons/uvviewer/zoom_out.png", () -> {}).addTooltip("uvviewer.button.zoom_out"));
        //
        SelectBox<String> tex = new SelectBox<>(255.0F, 5.0F, 280.0F, 32.0F);
        for(TextureGroup group : TextureManager.getGroups()) tex.addElement(group.name);
    	tex.setVisibleCount(8);
    	tex.addSelectBoxChangeSelectionEventListener(lis -> {});
    	con.add(tex);
        holder.add(UpdateType.TEXGROUP_ADDED, uw -> refreshTexGroups(tex));
        holder.add(UpdateType.TEXGROUP_REMOVED, uw -> refreshTexGroups(tex));
        holder.add(UpdateType.TEXGROUP_RENAMED, uw -> refreshTexGroups(tex));
        //
        getListenerMap().addListener(ChangeSizeEvent.class, lis -> {
            float x = lis.getNewSize().x(), y = lis.getNewSize().y();
            boolean c = false;
            if(x < 552){
                x = 552;
                c = true;
            }
            if(y < 599){
                y = 599;
                c = true;
            }
            if(c) setSize(x, y);
            panel.setSize(x - 30, y - 77);
            panel.getContainer().setSize(x - 40, y - 87);
        });
        addWidgetCloseEventListener(lis -> {});
        UpdateHandler.registerHolder(holder);
        FMT.FRAME.getContainer().add(this);
        show();
    }

    private void refreshTexGroups(SelectBox<String> tex){
    }

    public static void toggle(){
       if(INSTANCE == null) INSTANCE = new UVViewer();
       else if(INSTANCE.getStyle().getDisplay() == Style.DisplayType.FLEX) INSTANCE.hide();
       else INSTANCE.show();
    }

    public static boolean visible(){
        return INSTANCE != null && INSTANCE.getStyle().getDisplay() != Style.DisplayType.NONE;
    }

    public static Vector2f pos(){
        return INSTANCE.getPosition();
    }

    public static Vector2f size(){
        return INSTANCE.getSize();
    }

    public static class UVFields extends Component {

        public UVFields(float x, float y, float w, float h){
            setSize(w, h);
            setPosition(x, y);
            getStyle().setBorderRadius(8);
            Settings.applyComponentTheme(this);
            getStyle().setBorder(new SimpleLineBorder(FMT.rgba(0x2ea4e8), 2));
            setFocusable(false);
        }

    }

    public static class View extends Component {

        private ArrayList<UvElm> elements = new ArrayList<>();
        private ScrollablePanel root;

        public View(ScrollablePanel panel, float x, float y, float w, float h, UpdateHolder holder){
            setSize(w, h);
            setPosition(x, y);
            getStyle().setBorderRadius(0);
            getStyle().getBackground().setColor(FMT.rgba(0xffffffff));
            getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 3));
            setFocusable(false);
            for(Group group : FMT.MODEL.groups()){
                for(Polygon polygon : group){
                    UvElm elm = new UvElm(polygon);
                    elements.add(elm);
                    add(elm);
                }
            }
            holder.add(UpdateType.POLYGON_ADDED, uw -> {
                UvElm elm = new UvElm(uw.get(1));
                elements.add(elm);
                add(elm);
            });
            holder.add(UpdateType.POLYGON_REMOVED, uw -> {
                Polygon poly = uw.get(1);
                UvElm ulm = null;
                for(UvElm elm : elements){
                    if(elm.poly == poly){
                        ulm = elm;
                        break;
                    }
                }
                if(ulm != null){
                    elements.remove(ulm);
                    remove(ulm);
                }
            });
            root = panel;
        }

    }

    public static class UvElm extends Component {

        private Polygon poly;

        public UvElm(Polygon poli){
            setPosition(10, 10);
            setSize(300, 300);
            poly = poli;
        }

    }

    public static class UvElmRenderer extends NvgDefaultComponentRenderer<UvElm> {

        @Override
        protected void renderComponent(UVViewer.UvElm com, Context context, long ctx){
            createScissor(ctx, com);
            Vector2f pos = com.getAbsolutePosition();
            //
            resetScissor(ctx);
        }
    }

}
