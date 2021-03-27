package net.fexcraft.app.fmt.ui.fieds;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;

public interface Field {
	

	public float value();

	public float test(float value, boolean additive, float rate);

	public Field apply(float value);

	public void scroll(double yoffset);

	public String id();

	public default Runnable update(){ return null; }

	public PolygonValue polyval();
	
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
			((NumberField)event.getTargetComponent()).getTextState().setText(newtext);
			((NumberField)event.getTargetComponent()).setCaretPosition(newtext.length());
		}
	}
	
}
