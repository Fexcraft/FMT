package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.ui.EditorComponent.*;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.createScissor;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.resetScissor;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.polygon.uv.UVType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.update.UpdateType;
import org.joml.Vector2f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.renderer.nvg.component.NvgDefaultComponentRenderer;

public class UVEditor extends Widget {

    public static UVEditor INSTANCE;
    public static UVFields[] fields = new UVFields[4];
    public static Face SELECTED = NoFace.NONE;
    private SelectBox<String> face, type;

    private UVEditor(){
        getTitleTextState().setText(translate("uveditor.title"));
        int width = 880, height = 580;
        setSize(width, height);
        setPosition(FMT.WIDTH / 2 - width / 2, FMT.HEIGHT / 2 - height / 2);
        setResizable(false);
        Settings.applyComponentTheme(getContainer());
        UpdateHolder holder = new UpdateHolder();
        Component con = getContainer();
        //
        con.add(new Label(translate("uveditor.selected"), 10, 10, 280, 20));
        TextField selpoly = new TextField("...", 10, 35, 280, 20);
        selpoly.setEnabled(false);
        holder.add(UpdateType.POLYGON_SELECTED, o -> {
            Polygon poly = FMT.MODEL.first_selected();
            selpoly.getTextState().setText(poly == null ? "" : poly.name());
        });
        con.add(selpoly);
        //
        con.add(new Label(translate("uveditor.root"), 10, 60, 280, 20));
        NumberField tx, ty;
        con.add(tx = new NumberField(holder, F30, 85, F3S, 20).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, PolyVal.ValAxe.X)));
        con.add(ty = new NumberField(holder, F31, 85, F3S, 20).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, PolyVal.ValAxe.Y)));
        con.add(new RunButton("uveditor.root_reset", F32, 85, F3S, 20, () -> {
            FMT.MODEL.updateValue(tx.polyval(), tx.apply(-1), 0);
            FMT.MODEL.updateValue(ty.polyval(), ty.apply(-1), 0);
        }));
        //
        con.add(new Label(translate("uveditor.face"), 10, 120, 280, 20));
        face = new SelectBox<>(10, 145, 280, 20);
        holder.sub().add(UpdateType.POLYGON_SELECTED, o -> {
            while(face.getElements().size() > 0) face.removeElement(0);
            face.addElement("none");
            Polygon poly = FMT.MODEL.first_selected();
            if(poly != null){
                poly.cuv.keySet().forEach(key -> face.addElement(key));
                if(poly.isValidUVFace(SELECTED)) face.setSelected(SELECTED.id(), true);
                else face.setSelected(poly.getUVFaces()[0].id(), true);
            }
            else face.setSelected("none", true);
            updateSelFace(poly);
        });
        face.addSelectBoxChangeSelectionEventListener(lis -> {
            SELECTED = Face.get(lis.getNewValue(), true);
            Polygon poly = FMT.MODEL.first_selected();
            updateSelFace(poly);
        });
        face.setVisibleCount(8);
        con.add(face);
        //
        con.add(new Label(translate("uveditor.type"), 10, 170, 280, 20));
        type = new SelectBox<>(10, 195, 280, 20);
        for(UVType uvt : UVType.values()){
            type.addElement(uvt.name().toLowerCase());
        }
        type.setVisibleCount(8);
        type.addSelectBoxChangeSelectionEventListener(lis -> {
            UVType uvt = UVType.from(lis.getNewValue());
            int idx = uvt.automatic() ? 0 : -1;
            if(idx == -1){
                idx = uvt.ordinal() > 3 ? uvt.ordinal() - 3 : uvt.ordinal();
            }
            Polygon poly = FMT.MODEL.first_selected();
            if(poly != null){
                UVCoords cor = poly.cuv.get(SELECTED);
                if(cor != null && cor.type() != uvt){
                    cor.set(uvt);
                    int i = FMT.MODEL.selected().size();
                    UpdateHandler.update(UpdateType.POLYGON_SELECTED, poly, i, i);
                }
            }
            showField(idx);
        });
        con.add(type);
        //
        for(int i = 0; i < 4; i++){
            int w = 294;
            fields[i] = new UVFields(8, 230, w, 20);
            if(i == 0){
                fields[i].setSize(w, 40);
                fields[i].add(new CenteredLabel(translate("uveditor.type.automatic"), 7, 10, 270, 20));
            }
            else if(i == 1){
                fields[i].setSize(w, 70);
                fields[i].add(new Label(translate("uveditor.type.offset"), 7, 10, 270, 20));
                fields[i].add(new NumberField(holder, F20 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV, PolyVal.ValAxe.X)));
                fields[i].add(new NumberField(holder, F21 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV, PolyVal.ValAxe.Y)));
            }
            else if(i == 2){
                fields[i].setSize(w, 130);
                fields[i].add(new Label(translate("uveditor.type.start"), 7, 10, 270, 20));
                fields[i].add(new NumberField(holder, F20 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_START, PolyVal.ValAxe.X)));
                fields[i].add(new NumberField(holder, F21 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_START, PolyVal.ValAxe.Y)));
                fields[i].add(new Label(translate("uveditor.type.end"), 7, 70, 270, 20));
                fields[i].add(new NumberField(holder, F20 - 3, 100, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_START, PolyVal.ValAxe.X)));
                fields[i].add(new NumberField(holder, F21 - 3, 100, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_START, PolyVal.ValAxe.Y)));
            }
            else{
                fields[i].setSize(w, 250);
                fields[i].add(new Label(translate("uveditor.type.top_right"), 7, 10, 270, 20));
                fields[i].add(new NumberField(holder, F20 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_TR, PolyVal.ValAxe.X)));
                fields[i].add(new NumberField(holder, F21 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_TR, PolyVal.ValAxe.Y)));
                fields[i].add(new Label(translate("uveditor.type.top_left"), 7, 70, 270, 20));
                fields[i].add(new NumberField(holder, F20 - 3, 100, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_TL, PolyVal.ValAxe.X)));
                fields[i].add(new NumberField(holder, F21 - 3, 100, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_TL, PolyVal.ValAxe.Y)));
                fields[i].add(new Label(translate("uveditor.type.bot_left"), 7, 130, 270, 20));
                fields[i].add(new NumberField(holder, F20 - 3, 160, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_BL, PolyVal.ValAxe.X)));
                fields[i].add(new NumberField(holder, F21 - 3, 160, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_BL, PolyVal.ValAxe.Y)));
                fields[i].add(new Label(translate("uveditor.type.bot_right"), 7, 190, 270, 20));
                fields[i].add(new NumberField(holder, F20 - 3, 220, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_BR, PolyVal.ValAxe.X)));
                fields[i].add(new NumberField(holder, F21 - 3, 220, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_BR, PolyVal.ValAxe.Y)));
            }
            UIUtils.hide(fields[i]);
            con.add(fields[i]);
        }
        //
        con.add(new Label(translate("uveditor.group"), 10, height - 90, 280, 20));
        SelectBox<String> tex = new SelectBox<>(10, height - 65, 280, 20);
        for(TextureGroup group : TextureManager.getGroups()){
            tex.addElement(group.name);
        }
        tex.setVisibleCount(8);
        tex.addSelectBoxChangeSelectionEventListener(lis -> {
            //
        });
        con.add(tex);
        //
        ScrollablePanel panel = new ScrollablePanel(width - 570, 20, 522, 522);
        View view = new View(panel, 0, 0, 4096, 4096, holder.sub());
        panel.getContainer().add(view);
        panel.getContainer().setSize(4096, 4096);
        con.add(panel);
        con.add(new Icon(0, 32, 0, width - 42, 20, "./resources/textures/icons/uveditor/borders.png", () -> {}).addTooltip("uveditor.button.borders"));
        con.add(new Icon(0, 32, 0, width - 42, 55, "./resources/textures/icons/uveditor/only_pos.png", () -> {}).addTooltip("uveditor.button.only_pos"));
        con.add(new Icon(0, 32, 0, width - 42, 90, "./resources/textures/icons/uveditor/texture.png", () -> {}).addTooltip("uveditor.button.texture"));
        con.add(new Icon(0, 32, 0, width - 42, 145, "./resources/textures/icons/uveditor/zoom_in.png", () -> {}).addTooltip("uveditor.button.zoom_in"));
        con.add(new Icon(0, 32, 0, width - 42, 180, "./resources/textures/icons/uveditor/zoom_out.png", () -> {}).addTooltip("uveditor.button.zoom_out"));
        //
        addWidgetCloseEventListener(lis -> {});
        UpdateHandler.registerHolder(holder);
        FMT.FRAME.getContainer().add(this);
        show();
    }

    private void updateSelFace(Polygon poly){
        if(poly == null) showField(0);
        else{
            UVCoords cuv = poly.cuv.get(SELECTED);
            if(cuv == null || cuv.automatic()){
                showField(0);
                type.setSelected(UVType.AUTOMATIC.name().toLowerCase(), true);
            }
            else if(cuv.detached()){
                showField(cuv.type().ordinal() - 3);
                type.setSelected(cuv.type().name().toLowerCase(), true);
            }
            else{
                showField(cuv.type().ordinal());
                type.setSelected(cuv.type().name().toLowerCase(), true);
            }
        }
    }

    private void showField(int idx){
        for(UVFields field : fields) UIUtils.hide(field);
        UIUtils.show(fields[idx]);
    }

    public static void toggle(){
       if(INSTANCE == null) INSTANCE = new UVEditor();
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
        protected void renderComponent(UVEditor.UvElm com, Context context, long ctx){
            createScissor(ctx, com);
            Vector2f pos = com.getAbsolutePosition();
            //
            resetScissor(ctx);
        }
    }

}
