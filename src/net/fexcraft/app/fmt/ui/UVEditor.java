package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.createScissor;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.resetScissor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.utils.Translator;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.style.font.TextDirection;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.renderer.nvg.component.NvgDefaultComponentRenderer;
import org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils;
import org.liquidengine.legui.system.renderer.nvg.util.NvgText;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class UVEditor extends Widget {

    public static UVEditor INSTANCE;

    private UVEditor(){
        getTitleTextState().setText(Translator.translate("uveditor.title"));
        int width = 800, height = 600;
        setSize(width, height);
        setPosition(FMT.WIDTH / 2 - width / 2, FMT.HEIGHT / 2 - height / 2);
        setResizable(false);
        //
        View view = new View(width - 530, 20, 512, 512);
        view.add(new UvElm());
        getContainer().add(view);
        //
        addWidgetCloseEventListener(lis -> {});
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

    public static class View extends Component {


        public View(float x, float y, float w, float h){
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
