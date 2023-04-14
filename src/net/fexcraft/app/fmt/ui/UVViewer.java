package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.createScissor;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.resetScissor;

import java.util.ArrayList;

import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.lib.common.math.RGB;
import org.joml.Vector2f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.event.component.ChangeSizeEvent;
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
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class UVViewer extends Widget {

    public static UVViewer INSTANCE;
    public static Face SELECTED = NoFace.NONE;

    private UVViewer(){
        getTitleTextState().setText(translate("uvviewer.title"));
        int width = 552, height = 599;
        setSize(width, height);
        setPosition(FMT.WIDTH / 2 - width / 2, FMT.HEIGHT / 2 - height / 2);
        Settings.applyComponentTheme(getContainer());
        UpdateCompound updcom = new UpdateCompound();
        Component con = getContainer();
        //
        ScrollablePanel panel = new ScrollablePanel(15.0F, 42.0F, 522.0F, 522.0F);
        View view = new View(panel, 0.0F, 0.0F, 512.0F, 512.0F, updcom);
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
    	tex.addSelectBoxChangeSelectionEventListener(lis -> seltex(lis.getNewValue()));
    	con.add(tex);
        updcom.add(TexGroupAdded.class, e -> refreshTexGroups(tex));
        updcom.add(TexGroupRemoved.class, e -> refreshTexGroups(tex));
        updcom.add(TexGroupRenamed.class, e -> refreshTexGroups(tex));
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
        UpdateHandler.register(updcom);
        FMT.FRAME.getContainer().add(this);
        show();
    }

    private void refreshTexGroups(SelectBox<String> tex){
        String sel = tex.getSelection();
        while(tex.getElements().size() > 0) tex.removeElement(0);
        for(TextureGroup group : TextureManager.getGroups()) tex.addElement(group.name);
        if(sel != null && TextureManager.getGroup(sel) != null){
            tex.setSelected(sel, true);
            seltex(sel);
        }
        else if(TextureManager.getGroups().size() > 0){
            seltex(tex.getElements().get(0));
        }
    }

    private void seltex(String groupid){

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

        public View(ScrollablePanel panel, float x, float y, float w, float h, UpdateCompound updcom){
            setSize(w, h);
            setPosition(x, y);
            getStyle().setBorderRadius(0);
            getStyle().getBackground().setColor(FMT.rgba(0xffffffff));
            getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 3));
            setFocusable(false);
            for(Group group : FMT.MODEL.groups()) for(Polygon polygon : group) addElm(polygon);
            updcom.add(PolygonAdded.class, e -> addElm(e.polygon()));
            updcom.add(PolygonRemoved.class, e -> remElm(e.polygon()));
            updcom.add(PolygonValueEvent.class, e -> {
                if(!e.value().val().uv()) return;
                for(UvElm elm : elements){
                    if(elm.poly == e.polygon()){
                        elm.update();
                        break;
                    }
                }
            });
            root = panel;
        }

        private void addElm(Polygon polygon){
            for(Face face : polygon.getUVFaces()){
                UvElm elm = new UvElm(polygon, face);
                elements.add(elm);
                add(elm);
            }
        }

        private void remElm(Polygon polygon){
            ArrayList<UvElm> elms = new ArrayList<>();
            for(UvElm elm : elements){
                if(elm.poly == polygon){
                    elms.add(elm);
                }
            }
            if(elms.size() > 0){
                elements.removeAll(elms);
                removeAll(elms);
            }
        }

    }

    public static class UvElm extends Component {

        private Polygon poly;
        private Face face;
        private RGB color;

        public UvElm(Polygon poli, Face faccia){
            poly = poli;
            face = faccia;
            update();
        }

        public void update(){
            color = poly.getFaceColor(face.index());
            UVCoords cuv = poly.cuv.get(face);
            if(cuv == null) return;
            float[][][] poss = poly.newUV(true, false);
            if(poss == null || poss.length == 0) return;
            float[][] pos = poss[face.index()];
            switch(cuv.type()){
                case AUTOMATIC -> {
                    setPosition(poly.textureX, poly.textureY);
                    setSize(pos[1][0] - pos[0][0], pos[1][1] - pos[0][1]);
                }
                case OFFSET -> {

                }
                case OFFSET_ENDS -> {

                }
                case OFFSET_FULL -> {

                }
                case DETACHED -> {

                }
                case DETACHED_ENDS -> {

                }
                case DETACHED_FULL -> {

                }
            }
        }
    }

    public static class UvElmRenderer extends NvgDefaultComponentRenderer<UvElm> {

        @Override
        protected void renderComponent(UVViewer.UvElm com, Context context, long ctx){
            createScissor(ctx, com);
            Vector2f pos = com.getAbsolutePosition();
            Vector2f siz = com.getSize();
            byte[] rgb = com.color.toByteArray();
            //
            NanoVG.nvgBeginPath(ctx);
            NanoVG.nvgRect(ctx, pos.x, pos.y, siz.x, siz.y);
            NanoVG.nvgFillColor(ctx, NVGColor.calloc().r(rgb[0]).g(rgb[1]).b(rgb[2]).a(1));
            NanoVG.nvgFill(ctx);
            NanoVG.nvgClosePath(ctx);
            //
            resetScissor(ctx);
        }
    }

}
