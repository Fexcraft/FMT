package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.ui.EditorComponent.F30;
import static net.fexcraft.app.fmt.ui.EditorComponent.F31;
import static net.fexcraft.app.fmt.ui.EditorComponent.F32;
import static net.fexcraft.app.fmt.ui.EditorComponent.F3S;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.createScissor;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.resetScissor;

import javax.swing.text.View;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.uv.UVType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.update.UpdateType;
import org.joml.Vector2f;
import org.liquidengine.legui.component.AbstractTextComponent;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.renderer.nvg.component.NvgDefaultComponentRenderer;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class UVEditor extends Widget {

    public static UVEditor INSTANCE;
    public static UVFields[] fields = new UVFields[4];

    private UVEditor(){
        getTitleTextState().setText(translate("uveditor.title"));
        int width = 840, height = 570;
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
        con.add(tx = new NumberField(holder, F30, 85, F3S, 20).setup(-1, Integer.MAX_VALUE, true, new PolyVal.PolygonValue(PolyVal.TEX, PolyVal.ValAxe.X)));
        con.add(ty = new NumberField(holder, F31, 85, F3S, 20).setup(-1, Integer.MAX_VALUE, true, new PolyVal.PolygonValue(PolyVal.TEX, PolyVal.ValAxe.Y)));
        con.add(new RunButton("uveditor.root_reset", F32, 85, F3S, 20, () -> {
            FMT.MODEL.updateValue(tx.polyval(), tx.apply(-1), 0);
            FMT.MODEL.updateValue(ty.polyval(), ty.apply(-1), 0);
        }));
        //
        con.add(new Label(translate("uveditor.face"), 10, 120, 280, 20));
        SelectBox<String> face = new SelectBox<>(10, 145, 280, 20);
        holder.sub().add(UpdateType.POLYGON_SELECTED, o -> {
            while(face.getElements().size() > 0) face.removeElement(0);
            face.addElement("none");
            Polygon poly = FMT.MODEL.first_selected();
            if(poly != null){
                poly.cuv.keySet().forEach(key -> face.addElement(key));
            }
        });
        face.setVisibleCount(8);
        con.add(face);
        //
        con.add(new Label(translate("uveditor.type"), 10, 170, 280, 20));
        SelectBox<String> type = new SelectBox<>(10, 195, 280, 20);
        for(UVType uvt : UVType.values()){
            type.addElement(uvt.name().toLowerCase());
        }
        type.setVisibleCount(8);
        type.addSelectBoxChangeSelectionEventListener(lis -> {
            int idx = lis.getNewValue().equals("automatic") ? 0 : -1;
            if(idx == -1){
                UVType uvt = UVType.from(lis.getNewValue());
                idx = uvt.ordinal() / 2;
            }
            UIUtils.show(fields[idx]);
        });
        con.add(type);
        //
        for(int i = 0; i < 4; i++){
            fields[i] = new UVFields(5, 230, 290, 20);

            UIUtils.hide(fields[i]);
            con.add(fields[i]);
        }
        //
        View view = new View(width - 530, 20, 512, 512, holder.sub());
        view.add(new UvElm());
        con.add(view);
        //
        addWidgetCloseEventListener(lis -> {});
        UpdateHandler.registerHolder(holder);
        FMT.FRAME.getContainer().add(this);
        show();
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
            getStyle().setBorderRadius(0);
            getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 2));
            setFocusable(false);
        }

    }

    public static class View extends Component {


        public View(float x, float y, float w, float h, UpdateHolder holder){
            setSize(w, h);
            setPosition(x, y);
            getStyle().setBorderRadius(0);
            getStyle().getBackground().setColor(FMT.rgba(0xffffffff));
            getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 3));
            setFocusable(false);
        }

    }

    public static class UvElm extends Component {

        public UvElm(){
            setPosition(10, 10);
            setSize(300, 300);

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
