package net.fexcraft.app.fmt.ui.fields;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.function.Consumer;

import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.GroupSelected;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonSelected;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonValueEvent;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.KeyEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.listener.FocusEventListener;
import org.liquidengine.legui.listener.KeyEventListener;
import org.lwjgl.glfw.GLFW;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.EditorComponent;

public interface Field {
	

	public float value();

	public float test(float value, boolean additive, float rate);

	public Field apply(float value);

	public void scroll(double yoffset);

	public String id();

	public default Consumer<? extends Field> update(){ return null; }

	public PolygonValue polyval();
	
	public Setting<?> setting();
	
	//
	
	public static void setupHoverCheck(Component component){
		component.getListenerMap().addListener(CursorEnterEvent.class, listener -> {
			if(listener.isEntered()) FMT.SELFIELD = (Field)component;
			else if(FMT.SELFIELD == component) FMT.SELFIELD = null;
		});
		component.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == CLICK) FMT.SELFIELD = (Field)component;
		});
	}
	
	public static final String STRING_VALIDATOR_EXTENDED = "[^A-Za-z0-9,\\.\\-_ \\(\\)\\[\\]\\{\\}\\&\\%\\$\\#\\@\\!\\?\\;\\:\\+\\=\\*\\^\\\"\\'\\>\\<\\\\\\/\\~\\`\\|]";
	public static final String STRING_VALIDATOR_BASIC = "[^A-Za-z0-9,\\.\\-_ ]";
	public static final String STRING_VALIDATOR_JAVA = "[^A-Za-z0-9,_]";
	
	public static String validateString(TextInputContentChangeEvent<TextField> event){
		return validateString(event, false);
	}

	public static String validateString(TextInputContentChangeEvent<TextField> event, boolean basic){
		String newtext = event.getNewValue().replaceAll(basic ? STRING_VALIDATOR_BASIC : STRING_VALIDATOR_EXTENDED, "");
		if(!newtext.equals(event.getNewValue())){
			event.getTargetComponent().getTextState().setText(newtext);
			event.getTargetComponent().setCaretPosition(newtext.length());
		}
		return newtext;
	}

	public static String validateColorString(TextInputContentChangeEvent<ColorField> event){
		String newtext = event.getNewValue().replaceAll("[^A-Fa-f0-9#x]", "");
		if(!newtext.equals(event.getNewValue())){
			event.getTargetComponent().getTextState().setText(newtext);
			event.getTargetComponent().setCaretPosition(newtext.length());
		}
		return newtext;
	}

	public static void validateNumber(TextInputContentChangeEvent<NumberField> event){
		String newtext = event.getNewValue().replaceAll("[^0-9,\\.\\-]", "");
		if(newtext.indexOf("-") > 0) newtext.replace("-", ""); if(newtext.length() == 0) newtext = "0";
		if(!newtext.equals(event.getNewValue())){
			event.getTargetComponent().getTextState().setText(newtext);
			event.getTargetComponent().setCaretPosition(newtext.length());
		}
	}

	public static void setupUpdatesAndListeners(Field field, UpdateCompound updcom, PolygonValue val){
		updcom.add(PolygonValueEvent.class, event -> {
			if(!event.first()) return;
			if(event.value().equals(val)){
				field.apply(event.polygon().getValue(val));
			}
		});
		updcom.add(PolygonSelected.class, event -> {
			if(event.prevselected() < 0) return;
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() == 0)){
				field.apply(FMT.MODEL.first_selected().getValue(val));
			}
			else if(event.selected() == 0) field.apply(0);
		});
		updcom.add(GroupSelected.class, event -> {
			if(event.prevselected() < 0) return;
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() == 0)){
				field.apply(FMT.MODEL.first_selected().getValue(val));
			}
			else if(event.selected() == 0) field.apply(0);
		});
		if(field instanceof NumberField || field instanceof ColorField){
			Component com = (Component)field;
			if(field instanceof NumberField){
				NumberField input = (NumberField)field;
				input.addTextInputContentChangeEventListener(event -> {
					Field.validateNumber(event);
					input.value = null;
				});
			}
			else{
				ColorField input = (ColorField)field;
				input.addTextInputContentChangeEventListener(event -> {
					Field.validateColorString(event);
					input.value = null;
				});
			}
			com.getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
				if(!listener.isFocused()){
					FMT.MODEL.updateValue(field.polyval(), field, 0);
				}
			});
			com.getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
				if(listener.getKey() == GLFW.GLFW_KEY_ENTER){
					FMT.MODEL.updateValue(field.polyval(), field, 0);
				}
			});
		}
		else if(field instanceof BoolButton){
			BoolButton button = (BoolButton)field;
			button.getListenerMap().addListener(MouseClickEvent.class, lis -> {
				if(lis.getAction() == MouseClickAction.CLICK && lis.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
					button.toggle();
				}
			});
		}
	}
	
}
