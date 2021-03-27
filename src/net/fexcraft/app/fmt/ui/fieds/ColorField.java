package net.fexcraft.app.fmt.ui.fieds;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
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

import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.lib.common.math.RGB;

public class ColorField extends TextInput implements Field {

	private String fieldid;
	private Integer value = null;

	public ColorField(Component root, String field, int x, int y, int w, int h){
		super("0xffffff", x, y, root == null ? w : w - 40, h);
		Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		Field.setupHoverCheck(this);
		fieldid = field;
		addTextInputContentChangeEventListener(event -> {
			Field.validateColorString(event); value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()){
				//TODO update tracked model value/attribute
			}
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER){
				//TODO update tracked model value/attribute
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
						//TODO update tracked model value/attribute
                    }
				}
			});
			root.add(button);
		}
	}
	
	public ColorField(Component root, Setting<RGB> setting, int x, int y, int w, int h){
		super(setting.toString(), x, y, root == null ? w : w - 40, h);
		Settings.applyBorderless(this);
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
			root.add(button);
		}
	}
	
	public ColorField(Component root, BiConsumer<Integer, Boolean> update, int x, int y, int w, int h){
		super("#ffffff", x, y, root == null ? w : w - 40, h);
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
			root.add(button);
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
		return this;
	}

	@Override
	public void scroll(double yoffset){
		apply(test(value(), yoffset > 0, 1f));//TODO global rate value
		//TODO update tracked model value/attribute
		//<>.update(this, fieldid, scroll > 0);
	}

	@Override
	public String id(){
		return fieldid;
	}

	@Override
	public PolygonValue polyval(){
		// TODO Auto-generated method stub
		return null;
	}
	
}
