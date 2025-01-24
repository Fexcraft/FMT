package net.fexcraft.app.fmt.ui.fields;

import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Tooltip;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.MouseClickEvent.MouseClickAction;
import com.spinyowl.legui.input.Mouse.MouseButton;

import com.spinyowl.legui.style.border.SimpleLineBorder;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.utils.FontSizeUtil;
import net.fexcraft.app.fmt.utils.Translator;
import org.joml.Vector4f;

import static net.fexcraft.app.fmt.settings.Settings.DARKTHEME;

public class RunButton extends Button {

	public Runnable run;
	public boolean confirm;
	public boolean cancel;

	public RunButton(String str, float x, float y, float w, float h, Runnable run, boolean borderless){
		super(Translator.translate(str), x, y, w, h);
		if(borderless) Settings.applyBorderless(this);
		else{
			getStyle().setBorderRadius(4);
			getStyle().setBorder(new SimpleLineBorder(DARKTHEME.value ? new Vector4f(.65f, .65f, .65f, 1f) : new Vector4f(.35f, .35f, .35f, 1f), 1f));
		}
		Settings.applyGrayText(this);
		this.getListenerMap().addListener(MouseClickEvent.class, l -> {
			if(l.getAction() == MouseClickAction.CLICK && l.getButton() == MouseButton.MOUSE_BUTTON_LEFT) run.run();
		});
		parseType(str);
		this.run = run;
	}

	private void parseType(String str){
		if(!str.startsWith("dialog.button")) return;
		String[] suff = str.split("\\.");
		switch(suff[suff.length - 1]){
			case "confirm":
			case "continue":
			case "select":
			case "open":
			case "save":
			case "load":
			case "yes":
			case "ok":{
				confirm = true;
				break;
			}
			case "cancel":
			case "close":
			case "exit":
			case "no":{
				cancel = true;
				break;
			}
		}
	}

	public RunButton(String str, float x, float y, float w, float h, Runnable run){
		this(str, x, y, w, h, run, true);
	}

	public RunButton addTooltip(String string, boolean alignment){
		Tooltip tip = new Tooltip(Translator.translate(string));
		tip.setSize(FontSizeUtil.getWidth(tip.getTextState().getText()) * 2, 24);
		tip.getStyle().setPadding(2f);
		tip.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
		tip.setPosition(alignment ? getSize().x : -tip.getSize().x, (getSize().y - 24) / 2);
		tip.getStyle().setBorderRadius(0f);
		setTooltip(tip);
		return this;
	}

	public RunButton addTooltip(String string){
		return addTooltip(string, true);
	}

}
