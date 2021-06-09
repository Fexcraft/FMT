package net.fexcraft.app.fmt.ui.fields;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.KeyEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.listener.FocusEventListener;
import org.liquidengine.legui.listener.KeyEventListener;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.lib.common.math.RGB;

public class ColorField extends TextInput implements Field {

	private PolygonValue poly_value;
	protected Integer value = null;
	private Setting<RGB> setting;
	private Panel panel;

	public ColorField(EditorComponent root, float x, float y, float w, float h, PolygonValue polyval){
		super("#ffffff", x + (root == null ? 0 : h - 2), y, root == null ? w : w - 35 - h - 2, h);
		Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		Field.setupHoverCheck(this);
		poly_value = polyval;
		Field.setupHolderAndListeners(this, root, polyval);
		addTextInputContentChangeEventListener(event -> {
			Field.validateColorString(event); value = null;
		});
		if(root != null){
			Button button = new Button("CP", x + w - 35, y, 30, h);
			button.getListenerMap().addListener(MouseClickEvent.class, event -> {
				if(event.getAction() == CLICK){
                    try(MemoryStack stack = MemoryStack.stackPush()) {
                        ByteBuffer color = stack.malloc(3);
                        String result = TinyFileDialogs.tinyfd_colorChooser("Choose A Color", "#" + Integer.toHexString((int)value()), null, color);
						if(result == null) return;
						this.getTextState().setText(result);
						value = null;
						FMT.MODEL.updateValue(polyval(), this);
                    }
				}
			});
			Settings.applyBorderless(button);
			root.add(button);
			panel = new Panel(x, y, h, h);
			Settings.applyBorderless(panel);
			panel.getStyle().getBackground().setColor(FMT.rgba((int)value()));
			root.add(panel);
		}
	}
	
	public ColorField(Component root, Setting<RGB> setting, int x, int y, int w, int h){
		super("#" + Integer.toHexString(setting.value.packed), x + (root == null ? 0 : h - 2), y, root == null ? w : w - 35 - h - 2, h);
		Settings.applyMenuTheme(this);
		Settings.applyGrayText(this);
		Field.setupHoverCheck(this);
		addTextInputContentChangeEventListener(event -> {
			Field.validateColorString(event); value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()){
				setting.value.packed = (int)value();
			}
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER){
				setting.value.packed = (int)value();
			}
		});
		if(root != null){
			Button button = new Button("CP", x + w - 35, y, 30, h);
			button.getListenerMap().addListener(MouseClickEvent.class, event -> {
				if(event.getAction() == CLICK){
                    try(MemoryStack stack = MemoryStack.stackPush()) {
                        ByteBuffer color = stack.malloc(3);
                        String result = TinyFileDialogs.tinyfd_colorChooser("Choose A Color", "#" + Integer.toHexString((int)value()), null, color);
						if(result == null) return;
						this.getTextState().setText(result);
						value = null;
						setting.value.packed = (int)value();
                    }
				}
			});
			Settings.applyBorderless(button);
			root.add(button);
			panel = new Panel(x, y, h, h);
			Settings.applyBorderless(panel);
			panel.getStyle().getBackground().setColor(FMT.rgba((int)value()));
			root.add(panel);
		}
		this.setting = setting;
	}
	
	public ColorField(Component root, BiConsumer<Integer, Boolean> update, int x, int y, int w, int h){
		super("#ffffff", x + (root == null ? 0 : h - 2), y, root == null ? w : w - 35 - h - 2, h);
		Settings.applyBorderless(this);
		Field.setupHoverCheck(this);
		addTextInputContentChangeEventListener(event -> {
			Field.validateColorString(event); value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()){
				update.accept((int)value(), null);
			}
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER){
				update.accept((int)value(), null);
			}
		});
		if(root != null){
			Button button = new Button("CP", x + w - 35, y, 30, h);
			button.getListenerMap().addListener(MouseClickEvent.class, event -> {
				if(event.getAction() == CLICK){
                    try(MemoryStack stack = MemoryStack.stackPush()) {
                        ByteBuffer color = stack.malloc(3);
                        String result = TinyFileDialogs.tinyfd_colorChooser("Choose A Color", "#" + Integer.toHexString((int)value()), null, color);
						if(result == null) return;
						this.getTextState().setText(result);
						value = null;
						update.accept((int)value(), event.getButton() == MouseButton.MOUSE_BUTTON_LEFT);
                    }
				}
			});
			Settings.applyBorderless(button);
			root.add(button);
			panel = new Panel(x, y, h, h);
			Settings.applyBorderless(panel);
			panel.getStyle().getBackground().setColor(FMT.rgba((int)value()));
			root.add(panel);
		}
	}
	
	@Override
	public float value(){
		if(value != null) return value;
		float newval = 0;
		String text = this.getTextState().getText().replace("#", "").replace("0x", "");
		try{
			newval = Integer.parseInt(text, 16);
		}
		catch(Exception e){
			log(e);
		}
		apply(newval);
		if(panel != null) panel.getStyle().getBackground().setColor(FMT.rgba((int)value));
		return value = (int)newval;
	}

	@Override
	public float test(float flat, boolean positive, float rate){
		flat += positive ? rate : -rate; return (int)flat;
	}

	@Override
	public ColorField apply(float val){
		getTextState().setText("#" + Integer.toHexString(value = (int)val));
		setCaretPosition(getTextState().getText().length());
		if(panel != null) panel.getStyle().getBackground().setColor(FMT.rgba((int)val));
		return this;
	}

	@Override
	public void scroll(double yoffset){
		apply(test(value(), yoffset > 0, Editor.RATE));
		if(poly_value != null){
			FMT.MODEL.updateValue(poly_value, this);
		}
	}

	@Override
	public String id(){
		return poly_value.toString();
	}

	@Override
	public PolygonValue polyval(){
		return poly_value;
	}

	@Override
	public Setting<?> setting(){
		return setting;
	}
	
}
