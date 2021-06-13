package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.utils.FontSizeUtil;
import net.fexcraft.app.fmt.utils.Translator;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class GenericDialog {

	public static final Dialog show(String title, String text0, String text1, Runnable but0, Runnable but1, String... text){
		return show(null, title, text0, text1, but0, but1, text);
	}
	
	public static final Dialog show(Integer width, String title, String text0, String text1, Runnable but0, Runnable but1, String... text){
		if(width == null) width = 400;
		if(title == null) title = "dialog.title.default";
		for(int i = 0; i < text.length; i++){
			if(text[i].startsWith("#")) text[i] = text[i].substring(1);
			else text[i] = Translator.translate(text[i]);
			float w = FontSizeUtil.getWidth(text[i]);
			if(w + 40 > width) width = (int)w + 40;
		}
        Dialog dialog = new Dialog(Translator.translate(title), width, 70 + (text.length * 25));
        dialog.setResizable(false);
        for(int i = 0; i < text.length; i++){
        	Label label = new Label(text[i], 10, 10 + (i * 25), width - 20, 20);
        	dialog.getContainer().add(label);
        }
        if(text0 != null){
            Button button0 = new Button(Translator.translate(text0 == null ? "dialog.button.confirm" : text0), 10, 20 + (text.length * 25), 100, 20);
            button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()){ if(but0 != null) but0.run(); dialog.close(); }
            });
            dialog.getContainer().add(button0);
        }
        if(text1 != null){
            Button button1 = new Button(Translator.translate(text1 == null ? "dialog.button.cancel" : text1), 120, 20 + (text.length * 25), 100, 20);
            button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()){ if(but1 != null) but1.run(); dialog.close(); }
            });
            dialog.getContainer().add(button1);
        }
        dialog.show(FMT.FRAME);
        return dialog;
	}
	
	public static final void showYN(String title, Runnable but0, Runnable but1, String... text){
		show(null, title, "dialog.button.yes", "dialog.button.no", but0, but1, text);
	}
	
	public static final void showOC(String title, Runnable but0, Runnable but1, String... text){
		show(null, title, "dialog.button.ok", "dialog.button.cancel", but0, but1, text);
	}
	
	public static final void showCC(String title, Runnable but0, Runnable but1, String... text){
		show(null, title, "dialog.button.confirm", "dialog.button.cancel", but0, but1, text);
	}
	
	public static final void showOK(String title, Runnable but0, Runnable but1, String... text){
		Dialog dialog = show(null, title, "dialog.button.ok", null, but0, but1, text);
		dialog.setCloseable(false);
	}

}
