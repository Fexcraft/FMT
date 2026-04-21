package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.ui.TextElm;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterChannel;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterColor;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterTool;
import net.fexcraft.app.fmt.update.UpdateEvent.PickMode;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Picker;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_2;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_FIELD;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PainterEditorTab extends EditorTab {

	public ETabCom current;
	public ETabCom palette;
	public ETabCom tools;
	private Field[] channels = new Field[Settings.PAINTER_CHANNELS.value];
	private Element act_text;
	private Element act_col;
	private Element picker;
	private Element act_tool;

	public PainterEditorTab(){
		super(EditorRoot.EditorMode.PAINTER);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add((current = new ETabCom()), lang_prefix + "current", 160 + Settings.PAINTER_CHANNELS.value * 30);
		current.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "current.channels"));
		//
		for(int i = 0; i < Settings.PAINTER_CHANNELS.value; i++){
			int idx = i;
			channels[i] = new Field(Field.FieldType.COLOR, FF - 35, f -> TexturePainter.updateColor(f.parse_int(), idx, true));
			current.add(channels[i].pos(FO, next_y_pos(1)));
			channels[i].set(TexturePainter.CHANNELS[i].packed);
			current.add(new Element().pos(FF - 25, next_y_pos(0)).size(28, 28)
				.texture("icons/component/move_left")
				.hint(lang_prefix + "current.select_channel")
				.onclick(ci -> UpdateHandler.update(new PainterChannel(TexturePainter.ACTIVE = idx)))
			);
		}
		current.add(act_text = new Element().pos(FO, next_y_pos(1.5f)).size(FF - 110, 30).color(GENERIC_FIELD.value)
			.hint(lang_prefix + "current.change_channel")
			.onclick(ci -> changeChannel(ci.button() == 0 ? 1 : -1))
			.onscroll(si -> changeChannel(si.sy() > 0 ? 1 : -1))
		);
		current.add(act_col = new Element().pos(FF - 100, next_y_pos(0)).size(30, 30).border(GENERIC_BACKGROUND_2.value));
		current.add(new Element().pos(FF - 65, next_y_pos(0) - 1).size(32, 32).texture("icons/toolbar/save")
			.hint(lang_prefix + "current.save_channel").onclick(ci -> saveToPalette()));
		current.add(picker = new Element().pos(FF - 30, next_y_pos(0) - 1).size(32, 32).texture("icons/painter/picker")
			.hint(lang_prefix + "current.color_picker").onclick(ci -> {
				if(Picker.TYPE == Picker.PickType.COLOR) Picker.reset();
				else Picker.pick(Picker.PickType.COLOR, Picker.PickTask.NONE, true);
			}));
		current.add(act_tool = new Element().pos(FO, next_y_pos(1.5f)).size(FF, 30).color(GENERIC_FIELD.value)
			.hint(lang_prefix + "current.tool_reset").onclick(ci -> TexturePainter.setTool(TexturePainter.Tool.NONE)));
		//
		updcom.add(PainterColor.class, event -> {
			updateActiveChannel();
		});
		updcom.add(PickMode.class, event -> {
			picker.texture(event.type() == Picker.PickType.COLOR ? "icons/painter/picker_active" : "icons/painter/picker");
		});
		updcom.add(PainterChannel.class, event -> {
			updateActiveChannel();
		});
		updcom.add(PainterTool.class, event -> {
			updateActiveTool();
		});
		updateActiveChannel();
		updateActiveTool();
	}

	private void updateActiveChannel(){
		act_text.translate(lang_prefix + "current.active_channel", TexturePainter.ACTIVE);
		act_col.color(TexturePainter.CHANNELS[TexturePainter.ACTIVE]);
	}

	private void updateActiveTool(){
		act_tool.translate(lang_prefix + "current.active_tool", TexturePainter.getToolName());
	}

	private void changeChannel(int by){
		int idx = TexturePainter.ACTIVE + by;
		if(idx < 0) idx = Settings.PAINTER_CHANNELS.value - 1;
		if(idx >= Settings.PAINTER_CHANNELS.value) idx = 0;
		UpdateHandler.update(new PainterChannel(TexturePainter.ACTIVE = idx));
	}

	private void saveToPalette(){

	}

}
