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

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_2;

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

	public PainterEditorTab(){
		super(EditorRoot.EditorMode.PAINTER);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add((current = new ETabCom()), lang_prefix + "current", 280 + Settings.PAINTER_CHANNELS.value * 30);
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
		current.add(act_text = new TextElm(0, next_y_pos(1.5f), FF - 40));
		current.add(act_col = new Element().pos(FF - 25, next_y_pos(0)).size(28, 28).border(GENERIC_BACKGROUND_2.value));
		//
		updcom.add(PainterColor.class, event -> {
			updateActiveChannel();
		});
		updcom.add(PickMode.class, event -> {
			//
		});
		updcom.add(PainterChannel.class, event -> {
			updateActiveChannel();
		});
		updcom.add(PainterTool.class, event -> {

		});
		updateActiveChannel();
	}

	private void updateActiveChannel(){
		act_text.translate(lang_prefix + "current.active_channel", TexturePainter.ACTIVE);
		act_col.color(TexturePainter.CHANNELS[TexturePainter.ACTIVE]);
	}

}
