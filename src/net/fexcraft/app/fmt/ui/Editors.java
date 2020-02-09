package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.Slider;
import org.liquidengine.legui.component.Tooltip;
import org.liquidengine.legui.component.event.slider.SliderChangeValueEventListener;
import org.liquidengine.legui.event.WindowSizeEvent;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.Static;

public class Editors {
	
	public static EditorBase root;

	public static void initializeEditors(Frame frame){
		frame.getContainer().add(root = new EditorBase());
		
	}
	
	public static class EditorBase extends Panel {
		
		public EditorBase(){
			super(0, 30, 300, FMTB.HEIGHT - 30);
			this.getListenerMap().addListener(WindowSizeEvent.class, event -> this.setSize(event.getWidth() / 5, event.getHeight() - 30));
			String[] arr = new String[]{ "normal", "sixteenth", "decimal"}; int off = 0;
			Label label = new Label(translate("editor.multiplicator"), 4, 4, 100, 24);
			this.add(label); label.getTextState().setFontSize(20); int am = 0;
			Label current = new Label(format("editor.multiplicator.current", 1f), 4, 28, 100, 24);
			this.add(current); current.getTextState().setFontSize(20);
			for(String string : arr){
				Slider multislider = new Slider(148, 4 + off, 150, 16);
				switch(string){
					case "normal":{
						multislider.setMinValue(0); multislider.setMaxValue(64);
						multislider.setStepSize(1); multislider.setValue(1f); am = 0;
						break;
					}
					case "sixteenth":{
						multislider.setMinValue(0); multislider.setMaxValue(1); am = 4;
						multislider.setStepSize(Static.sixteenth); multislider.setValue(1f);
						break;
					}
					case "decimal":{
						multislider.setMinValue(0); multislider.setMaxValue(1); am = 1;
						multislider.setStepSize(0.1f); multislider.setValue(1f);
						break;
					}
				}
		        final Tooltip multitip = new Tooltip();
		        multitip.setSize(100, 20); multitip.getTextState().setFontSize(20);
		        multitip.setPosition(multislider.getSize().x + 2, 0); final String amo = "%." + am + "f";
		        multitip.getTextState().setText(translate("editor.multiplicator.value") + String.format(amo, multislider.getValue()));
		        multislider.addSliderChangeValueEventListener((SliderChangeValueEventListener) event -> {
		            multitip.getTextState().setText(translate("editor.multiplicator.value") + String.format(amo, event.getNewValue()));
		            current.getTextState().setText(format("editor.multiplicator.current", event.getNewValue()));
		            multitip.setSize(100, 28); FMTB.MODEL.rate = event.getNewValue();
		        });
		        multislider.setTooltip(multitip);
		        this.add(multislider); off += 18;
			}
		}
		
	}
	
	public static String translate(String str){
		return Translator.translate(str, "no.lang");
	}
	
	public static String format(String str, Object... objs){
		return Translator.format(str, "no.lang.%s", objs);
	}

}
