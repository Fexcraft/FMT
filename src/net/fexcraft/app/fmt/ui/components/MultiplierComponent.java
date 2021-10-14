package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Slider;
import org.liquidengine.legui.component.Tooltip;
import org.liquidengine.legui.component.event.slider.SliderChangeValueEventListener;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;

import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.lib.common.Static;

public class MultiplierComponent extends EditorComponent {
	
	private NumberField field;
	
	public MultiplierComponent(){
		super("multiplier", 65, false, true);
		this.add(field = new NumberField(this, F30, row(1), F3S, HEIGHT){
			@Override
			public void scroll(double scroll){
				apply(test(value(), scroll > 0, Editor.RATE == 0f ? 1 : scroll > 0 ? Editor.RATE : Editor.RATE / 2f));
				if(update != null) update.accept(this);
			}
		}.setup(0.00001f, Integer.MAX_VALUE, true, field -> {
			float val = field.value();
			if(Editor.RATE != val){
				Editor.RATE = field.value();
				UpdateHandler.update(UpdateType.EDITOR_RATE, Editor.RATE);
			}
		}).apply(Editor.RATE));
		updateholder.add(UpdateType.EDITOR_RATE, vals -> field.apply(vals.get(0)));
		//
		String[] arr = new String[]{ "normal", "sixteenth", "decimal"};
		int off = 0, am = 0;
		for(String string : arr){
			Slider slider = new Slider(F31, row(0) + off, 190, 12);
			switch(string){
				case "normal":{
					slider.setMinValue(1);
					slider.setMaxValue(16);
					slider.setStepSize(1);
					slider.setValue(1f);
					am = 0;
					break;
				}
				case "sixteenth":{
					slider.setMinValue(Static.sixteenth);
					slider.setMaxValue(1);
					slider.setStepSize(Static.sixteenth);
					slider.setValue(1f);
					am = 4;
					break;
				}
				case "decimal":{
					slider.setMinValue(0.1f);
					slider.setMaxValue(1);
					slider.setStepSize(0.1f);
					slider.setValue(1f);
					am = 1;
					break;
				}
			}
	        final Tooltip tooltip = new Tooltip();
	        tooltip.setSize(110, 28);
	        tooltip.setPosition(slider.getSize().x + 10, 0);
	        tooltip.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
	        final String amo = "%." + am + "f";
	        tooltip.getTextState().setText(translate("editor.multiplicator.value") + " " + String.format(amo, slider.getValue()));
	        slider.addSliderChangeValueEventListener((SliderChangeValueEventListener) event -> {
	        	String formatted = String.format(amo, event.getNewValue());
	            tooltip.getTextState().setText(translate("editor.multiplicator.value") + " " + formatted);
	            field.apply(event.getNewValue());
	            field.update().accept(field);
	        });
	        slider.setTooltip(tooltip);
	        super.add(slider);
	        off += 12;
		}
	}

}
