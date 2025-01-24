package net.fexcraft.app.fmt.ui.panels;

import com.spinyowl.legui.component.Slider;
import com.spinyowl.legui.component.Tooltip;
import com.spinyowl.legui.component.event.slider.SliderChangeValueEventListener;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.editors.EditorPanel;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.lib.common.Static;

import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class MultiplierPanel extends EditorPanel {

	private NumberField field;

	public MultiplierPanel(){
		super("multiplier", "multiplier", "editor.component.multiplier");
		setPosition(Editor.WIDTH, I_SIZE * 2);
		ex_x = 330;
		ex_y = 60;
		//
		add(field = new NumberField(updcom, I_SIZE + 5, 15, 90, 30){
			@Override
			public void scroll(double scroll){
				apply(test(value(), scroll > 0, Editor.RATE == 0f ? 1 : scroll > 0 ? Editor.RATE : Editor.RATE / 2f));
				if(update != null) update.accept(this);
			}
		}.setup(0.00001f, Integer.MAX_VALUE, true, field -> {
			float val = field.value();
			if(Editor.RATE != val){
				Editor.RATE = field.value();
				UpdateHandler.update(new UpdateEvent.EditorRate(Editor.RATE));
			}
		}).apply(Editor.RATE));
		updcom.add(UpdateEvent.EditorRate.class, event -> field.apply(event.rate()));
		//
		String[] arr = new String[]{ "normal", "sixteenth", "decimal"};
		int off = 0, am = 0;
		for(String string : arr){
			Slider slider = new Slider(130, 3 + off, 190, 17);
			off += 17;
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
			slider.addSliderChangeValueEventListener((SliderChangeValueEventListener)event -> {
				String formatted = String.format(amo, event.getNewValue());
				tooltip.getTextState().setText(translate("editor.multiplicator.value") + " " + formatted);
				field.apply(event.getNewValue());
				field.update().accept(field);
			});
			slider.setTooltip(tooltip);
			super.add(slider);
		}
	}

}
