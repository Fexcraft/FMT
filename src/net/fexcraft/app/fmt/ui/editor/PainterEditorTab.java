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
import net.fexcraft.lib.common.math.RGB;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_2;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_FIELD;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PainterEditorTab extends EditorTab {

	private static int palette_columns = 28;
	private static int palette_rows = 12;
	private static int spectrum_columns = 35;
	public ETabCom current;
	public ETabCom palette;
	public ETabCom tools;
	private Field[] channels = new Field[Settings.PAINTER_CHANNELS.value];
	private Element act_text;
	private Element act_col;
	private Element picker;
	private Element act_tool;
	private Element[] gradient = new Element[palette_columns * palette_rows];
	private Element[] spectrum = new Element[spectrum_columns];
	private Element[] custom = new Element[palette_columns * 2];

	public PainterEditorTab(){
		super(EditorRoot.EditorMode.PAINTER);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add((current = new ETabCom()), lang_prefix + "current", 160 + Settings.PAINTER_CHANNELS.value * 30);
		current.add(new TextElm(FO, next_y_pos(1), FF).translate(lang_prefix + "current.channels"));
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
		container.add((palette = new ETabCom()), lang_prefix + "palette", 300);
		palette.add(new TextElm(FO, next_y_pos(-1), FF).translate(lang_prefix + "palette.gradient"));
		int yo = next_y_pos(1);
		for(int x = 0; x < palette_columns; x++){
			for(int y = 0; y < palette_rows; y++){
				int idx = x * palette_rows + y;
				palette.add(gradient[idx] = new Element().pos(FO + x * 10, y * 10 + yo).size(10, 10)
					.onclick(ci -> TexturePainter.updateColor(gradient[idx].col_def.packed, TexturePainter.ACTIVE, ci.button() == 0)));
			}
		}
		palette.add(new TextElm(FO, next_y_pos(4f), FF).translate(lang_prefix + "palette.spectrum"));
		yo = next_y_pos(1);
		for(int i = 0; i < spectrum_columns; i++){
			int idx = i;
			float c = i * (1f / spectrum_columns);
			int r, g, b;
			//
			if(c >= 0 && c <= (1/6.f)){
				r = 255;
				g = (int)(1530 * c);
				b = 0;
			}
			else if( c > (1/6.f) && c <= (1/3.f) ){
				r = (int)(255 - (1530 * (c - 1/6f)));
				g = 255;
				b = 0;
			}
			else if( c > (1/3.f) && c <= (1/2.f)){
				r = 0;
				g = 255;
				b = (int)(1530 * (c - 1/3f));
			}
			else if(c > (1/2f) && c <= (2/3f)) {
				r = 0;
				g = (int)(255 - ((c - 0.5f) * 1530));
				b = 255;
			}
			else if( c > (2/3f) && c <= (5/6f) ){
				r = (int)((c - (2/3f)) * 1530);
				g = 0;
				b = 255;
			}
			else if(c > (5/6f) && c <= 1 ){
				r = 255;
				g = 0;
				b = (int)(255 - ((c - (5/6f)) * 1530));
			}
			else{
				r = 127;
				g = 127;
				b = 127;
			}
			palette.add(spectrum[idx] = new Element().pos(FO + i * 8, yo).size(8, 30).color(new RGB(r, g, b))
				.onclick(ci -> TexturePainter.updateColor(spectrum[idx].col_def.packed, TexturePainter.ACTIVE, ci.button() == 0)));
		}
		palette.add(new TextElm(FO, next_y_pos(1f), FF).translate(lang_prefix + "palette.custom"));
		yo = next_y_pos(1);
		for(int y = 0; y < 2; y++){
			for(int x = 0; x < palette_columns; x++){
				int idx = x + y * palette_columns;
				palette.add(custom[idx] = new Element().pos(FO + x * 10, y * 10 + yo).size(10, 10).color(RGB.random())
					.onclick(ci -> TexturePainter.updateColor(custom[idx].col_def.packed, TexturePainter.ACTIVE, ci.button() == 0)));
			}
		}
 		//
		updcom.add(PainterColor.class, event -> {
			updateActiveChannel();
			refreshPalette(event.value(), event.upd_plt());
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
		refreshPalette(null, true);
	}

	private void updateActiveChannel(){
		act_text.translate(lang_prefix + "current.active_channel", TexturePainter.ACTIVE);
		act_col.color(TexturePainter.CHANNELS[TexturePainter.ACTIVE]);
		channels[TexturePainter.ACTIVE].set(TexturePainter.CHANNELS[TexturePainter.ACTIVE].packed);
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

	private void refreshPalette(Integer color, boolean upd){
		if(!upd) return;
		int idx = 0;
		byte[] arr = color == null ? TexturePainter.getColor() : new RGB(color).toByteArray();
		for(int x = 0; x < palette_columns; x++){
			for(int z = 0; z < palette_rows; z++){
				int y = x * palette_columns + z;
				float e = (1f / gradient.length) * y, f = (1f / palette_rows) * z, h = (255f / palette_columns) * x;
				int r = (int)Math.abs((e * (arr[0] + 128)) + ((1 - f) * h));
				int g = (int)Math.abs((e * (arr[1] + 128)) + ((1 - f) * h));
				int b = (int)Math.abs((e * (arr[2] + 128)) + ((1 - f) * h));
				gradient[idx++].color(new RGB(r, g, b));
			}
		}
	}

	private void saveToPalette(){

	}

}
