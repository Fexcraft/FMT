package net.fexcraft.app.fmt.ui.panels;

import com.spinyowl.legui.component.Slider;
import com.spinyowl.legui.component.Tooltip;
import com.spinyowl.legui.component.event.slider.SliderChangeValueEventListener;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.editors.EditorPanel;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.CornerUtil;
import net.fexcraft.lib.common.Static;

import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class MarkerPanel extends EditorPanel {

	private NumberField field;

	public MarkerPanel(){
		super("marker_scale", "marker_scale", "editor.component.marker_scale");
		setPos(5);
		ex_x = 330;
		ex_y = 30;
		//
		add(field = new NumberField(updcom, I_SIZE + 5, 2, 90, 26){
			@Override
			public void scroll(double scroll){
				apply(test(value(), scroll > 0, Editor.MARKER_SCALE == 0f ? 1 : scroll > 0 ? Editor.MARKER_SCALE : Editor.MARKER_SCALE / 2f));
				if(update != null) update.accept(this);
			}
		}.setup(0.00001f, Integer.MAX_VALUE, true, field -> {
			float val = field.value();
			if(Editor.MARKER_SCALE != val){
				Editor.MARKER_SCALE = field.value();
				CornerUtil.compile();
				UpdateHandler.update(new UpdateEvent.MarkerScale(Editor.MARKER_SCALE));
			}
		}).apply(Editor.RATE));
		updcom.add(UpdateEvent.EditorRate.class, event -> field.apply(event.rate()));
		//
		Slider slider = new Slider(130, 2, 190, 26);
		slider.setMinValue(0.1f);
		slider.setMaxValue(1);
		slider.setStepSize(0.1f);
		slider.setValue(1f);
		final Tooltip tooltip = new Tooltip();
		tooltip.setSize(110, 28);
		tooltip.setPosition(slider.getSize().x + 10, 0);
		tooltip.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
		final String amo = "%.1f";
		tooltip.getTextState().setText(translate("editor.component.marker_scale.value") + " " + String.format(amo, slider.getValue()));
		slider.addSliderChangeValueEventListener((SliderChangeValueEventListener)event -> {
			String formatted = String.format(amo, event.getNewValue());
			tooltip.getTextState().setText(translate("editor.component.marker_scale.value") + " " + formatted);
			field.apply(event.getNewValue());
			field.update().accept(field);
		});
		slider.setTooltip(tooltip);
		super.add(slider);
	}

}
