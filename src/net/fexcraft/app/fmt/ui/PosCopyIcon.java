package net.fexcraft.app.fmt.ui;

import com.spinyowl.legui.component.ImageView;
import com.spinyowl.legui.component.Tooltip;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.image.loader.ImageLoader;
import com.spinyowl.legui.listener.MouseClickEventListener;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.utils.FontSizeUtil;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator;

import java.util.function.Supplier;

import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static com.spinyowl.legui.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static net.fexcraft.app.fmt.FMT.MODEL;
import static net.fexcraft.app.fmt.update.PolyVal.PolygonValue.of;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PosCopyIcon extends ImageView {

    public PosCopyIcon(float x, float y, Supplier<float[]> supp) {
        super(ImageLoader.loadImage("./resources/textures/icons/configeditor/confirm.png"));
        setPosition(x, y);
        setSize(16, 16);
        Settings.applyBorderless(getStyle());
        Settings.applyBorderless(getFocusedStyle());
        getListenerMap().addListener(MouseClickEvent.class, event -> {
            if(event.getAction() == CLICK && event.getButton() == MOUSE_BUTTON_LEFT) process(supp);
        });
    }

    public PosCopyIcon(float x, float y, PolyVal val) {
        this(x, y, () -> {
            float[] arr = new float[3];
            if(MODEL.selected().isEmpty()) return arr;
            Polygon poly = MODEL.selected().get(0);
            arr[0] = poly.getValue(of(val, PolyVal.ValAxe.X));
            arr[1] = poly.getValue(of(val, PolyVal.ValAxe.Y));
            arr[2] = poly.getValue(of(val, PolyVal.ValAxe.Z));
            if(FMT.MODEL.orient.rect() || val == PolyVal.SIZE){
                arr[0] *= .0625f;
                arr[1] *= .0625f;
                arr[2] *= .0625f;
            }
            else{
                float v = arr[0];
                arr[0] = arr[2] * -.0625f;
                arr[1] *= -.0625f;
                arr[2] = v * -.0625f;
            }
            return arr;
        });
    }

    private void process(Supplier<float[]> supp){
        PosCopyMenu.show(supp);
    }

}
