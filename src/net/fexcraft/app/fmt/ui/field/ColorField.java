package net.fexcraft.app.fmt.ui.field;

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

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.lib.common.math.RGB;

public class ColorField extends TextInput implements Field {

	private String fieldid;
	private Integer value = null;

	public ColorField(Component root, String field, int x, int y, int w, int h){
		super("0xffffff", x, y, root == null ? w : w - 40, h); fieldid = field; UserInterfaceUtils.setupHoverCheck(this);
		addTextInputContentChangeEventListener(event -> {
			UserInterfaceUtils.validateColorString(event); value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()){
				FMTB.MODEL.updateValue(this, fieldid);
			}
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER){
				FMTB.MODEL.updateValue(this, fieldid);
			}
		});
		if(root != null){
			Button button = new Button("CP", x + w - 35, y, 30, h);
			button.getListenerMap().addListener(MouseClickEvent.class, event -> {
				if(event.getAction() == CLICK){
                    try(MemoryStack stack = MemoryStack.stackPush()) {
                        ByteBuffer color = stack.malloc(3);
                        String result = TinyFileDialogs.tinyfd_colorChooser("Choose A Color", "#" + Integer.toHexString((int)getValue()), null, color);
						if(result == null) return; this.getTextState().setText(result); value = null; FMTB.MODEL.updateValue(this, fieldid);
                    }
				}
			}); root.add(button);
		}
	}
	
	public ColorField(Component root, Setting setting, int x, int y, int w, int h){
		super(setting.toString(), x, y, root == null ? w : w - 40, h); getStyle().setFontSize(20f); UserInterfaceUtils.setupHoverCheck(this);
		addTextInputContentChangeEventListener(event -> {
			UserInterfaceUtils.validateColorString(event); value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()){ ((RGB)setting.getValue()).packed = (int)getValue(); }
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER){ ((RGB)setting.getValue()).packed = (int)getValue(); }
		});
		if(root != null){
			Button button = new Button("CP", x + w - 35, y, 30, h);
			button.getListenerMap().addListener(MouseClickEvent.class, event -> {
				if(event.getAction() == CLICK){
                    try(MemoryStack stack = MemoryStack.stackPush()) {
                        ByteBuffer color = stack.malloc(3);
                        String result = TinyFileDialogs.tinyfd_colorChooser("Choose A Color", "#" + Integer.toHexString((int)getValue()), null, color);
						if(result == null) return; this.getTextState().setText(result); value = null; ((RGB)setting.getValue()).packed = (int)getValue();
                    }
				}
			}); root.add(button);
		}
	}
	
	public ColorField(Component root, BiConsumer<Integer, Boolean> update, int x, int y, int w, int h){
		super("#ffffff", x, y, root == null ? w : w - 40, h); getStyle().setFontSize(20f); UserInterfaceUtils.setupHoverCheck(this);
		addTextInputContentChangeEventListener(event -> {
			UserInterfaceUtils.validateColorString(event); value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()){ update.accept((int)getValue(), null); }
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER){ update.accept((int)getValue(), null); }
		});
		if(root != null){
			Button button = new Button("CP", x + w - 35, y, 30, h);
			button.getListenerMap().addListener(MouseClickEvent.class, event -> {
				if(event.getAction() == CLICK){
                    try(MemoryStack stack = MemoryStack.stackPush()) {
                        ByteBuffer color = stack.malloc(3);
                        String result = TinyFileDialogs.tinyfd_colorChooser("Choose A Color", "#" + Integer.toHexString((int)getValue()), null, color);
						if(result == null) return; this.getTextState().setText(result); value = null; update.accept((int)getValue(), event.getButton() == MouseButton.MOUSE_BUTTON_LEFT);
                    }
				}
			}); root.add(button);
		}
	}
	
	@Override
	public float getValue(){
		if(value != null) return value; float newval = 0;
		String text = this.getTextState().getText().replace("#", "").replace("0x", "");
		try{
			newval = Integer.parseInt(text, 16);
		}
		catch(Exception e){
			log(e);
		}
		apply(newval); return value = (int)newval;
	}

	@Override
	public float tryAdd(float flat, boolean positive, float rate){
		flat += positive ? rate : -rate; return (int)flat;
	}

	@Override
	public void apply(float val){
		getTextState().setText("#" + Integer.toHexString(value = (int)val));
		setCaretPosition(getTextState().getText().length());
	}

	@Override
	public void onScroll(double yoffset){
		apply(tryAdd(getValue(), yoffset > 0, FMTB.MODEL.rate));
		FMTB.MODEL.updateValue(this, fieldid, yoffset > 0);
	}

	@Override
	public String id(){
		return fieldid;
	}
	
}
