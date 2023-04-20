package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.createScissor;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.resetScissor;

import java.util.ArrayList;

import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.lib.common.math.RGB;
import org.joml.Vector2f;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.event.component.ChangeSizeEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.StbBackedLoadableImage;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.border.SimpleLineBorder;
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
    private static String seltex;
    private View view;

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
        view = new View(panel, 0.0F, 0.0F, 512.0F, 512.0F, updcom);
        panel.getContainer().add(view);
        panel.getContainer().setSize(512.0F, 512.0F);
        panel.getContainer().setFocusable(false);
        con.add(panel);
        con.add(new Icon(0, 32, 8, 15, 5, "./resources/textures/icons/uvviewer/borders.png", () -> view.toggleBorders()).addTooltip("uvviewer.button.borders"));
        con.add(new Icon(1, 32, 8, 15, 5, "./resources/textures/icons/uvviewer/only_pos.png", () -> view.toggleVisibility()).addTooltip("uvviewer.button.only_pos"));
        con.add(new Icon(2, 32, 8, 15, 5, "./resources/textures/icons/uvviewer/texture.png", () -> view.toggleTexture()).addTooltip("uvviewer.button.texture"));
        con.add(new Icon(0, 32, 8, 160, 5, "./resources/textures/icons/uvviewer/zoom_in.png", () -> view.upsize()).addTooltip("uvviewer.button.zoom_in"));
        con.add(new Icon(1, 32, 8, 160, 5, "./resources/textures/icons/uvviewer/zoom_out.png", () -> view.downsize()).addTooltip("uvviewer.button.zoom_out"));
        //
        SelectBox<String> tex = new SelectBox<>(255.0F, 5.0F, 280.0F, 32.0F);
        for(TextureGroup group : TextureManager.getGroups()) tex.addElement(group.name);
        tex.setVisibleCount(8);
        tex.addSelectBoxChangeSelectionEventListener(lis -> seltex(lis.getNewValue()));
        seltex = TextureManager.getGroups().size() > 0 ? TextureManager.getGroups().get(0).name : null;
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
        });
        addWidgetCloseEventListener(lis -> {
            UpdateHandler.deregister(updcom);
            INSTANCE = null;
        });
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
        seltex = groupid;
        view.updateImg();
    }

    public static void addIfAbsent(){
       if(INSTANCE == null) INSTANCE = new UVViewer();
    }

    public static boolean visible(){
        return INSTANCE != null;
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
        private boolean borders, posi, texture;
        private ImageView image;
        private int zoom = 1;

        public View(ScrollablePanel panel, float x, float y, float w, float h, UpdateCompound updcom){
            setSize(w, h);
            setPosition(x, y);
            getStyle().setBorderRadius(0);
            getStyle().getBackground().setColor(FMT.rgba(0xffffffff));
            //getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 3));
            setFocusable(false);
            add(image = new ImageView());
            UIUtils.hide(image);
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
            updcom.add(PickFace.class, e -> {
                for(UvElm elm : elements){
                    if(elm.poly == e.polygon()){
                        elm.update();
                        break;
                    }
                }
            });
            updcom.add(ModelLoad.class, e ->  resize());
            updcom.add(ModelTextureSize.class, e ->  resize());
            root = panel;
            resize();
        }

        private void resize(){
            int x = FMT.MODEL.texSizeX * zoom, y = FMT.MODEL.texSizeY * zoom;
            setSize(x, y);
            image.setSize(x, y);
            root.getContainer().setSize(x, y);
            for(UvElm elm : elements) elm.update();
        }

        private void addElm(Polygon polygon){
            for(Face face : polygon.getUVFaces()){
                UvElm elm = new UvElm(this, polygon, face);
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

        public void toggleBorders(){
            borders = !borders;
        }

        public void toggleVisibility(){
            posi = !posi;
            for(UvElm elm : elements){
                elm.setEnabled(!posi || (elm.poly.textureX >= 0 && elm.poly.textureY >= 0));
            }
        }

        public void toggleTexture(){
            texture = !texture;
            updateImg();
        }

        public void updateImg(){
            if(texture){
                TextureGroup group = TextureManager.getGroup(seltex);
                if(group == null || group.texture == null) return;
                UIUtils.show(image);
                image.setImage(new StbBackedLoadableImage(group.texture.getFile().toPath().toString()));
                image.setSize(getSize());
            }
            else{
                UIUtils.hide(image);
            }
        }

        public void upsize(){
            if(++zoom > 16) zoom = 16;
            resize();
        }

        public void downsize(){
            if(--zoom < 1) zoom = 1;
            resize();
        }
    }

    public static class UvElm extends Component {

        private Polygon poly;
        private View view;
        private Face face;
        private RGB color;

        public UvElm(View parr, Polygon poli, Face faccia){
            poly = poli;
            face = faccia;
            view = parr;
            //update();
            getListenerMap().addListener(MouseClickEvent.class, lis -> {
                FMT.MODEL.select(poly, true);
                Picker.selected_face = face;
                UpdateHandler.update(new PickFace(poly, face));
            });
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
                    setPosition(poly.textureX + pos[0][0], poly.textureY + pos[0][1]);
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
            if(view.zoom > 1){
                setPosition(getPosition().mul(view.zoom));
                setSize(getSize().mul(view.zoom));
            }
        }

    }

    public static class UvElmRenderer extends NvgDefaultComponentRenderer<UvElm> {

        private static float[] selcol = Settings.SELECTION_LINES.value.toFloatArray();

        @Override
        protected void renderComponent(UVViewer.UvElm com, Context context, long ctx){
            if(!com.isEnabled()) return;
            createScissor(ctx, com);
            Vector2f pos = com.getAbsolutePosition();
            Vector2f siz = com.getSize();
            float[] rgb = com.color.toFloatArray();
            //
            NVGColor color = NVGColor.calloc();
            if(com.poly.selected){
                if(com.face == Picker.selected_face) color.r(selcol[0]).g(selcol[1]).b(selcol[2]).a(1);
                else color.r(selcol[0] * .5f).g(selcol[1] * .5f).b(selcol[2] * .5f).a(1);
            }
            else if(com.view.borders){
                color.r(0).g(0).b(0).a(1);
            }
            else{
                color.r(rgb[0]).g(rgb[1]).b(rgb[2]).a(1);
            }
            NanoVG.nvgBeginPath(ctx);
            NanoVG.nvgRect(ctx, pos.x, pos.y, siz.x, siz.y);
            if(com.view.borders){
                NanoVG.nvgStrokeColor(ctx, color);
                NanoVG.nvgStroke(ctx);
            }
            else{
                NanoVG.nvgFillColor(ctx, color);
                NanoVG.nvgFill(ctx);
            }
            NanoVG.nvgClosePath(ctx);
            //
            resetScissor(ctx);
        }
    }

}
