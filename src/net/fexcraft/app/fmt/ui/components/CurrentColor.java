package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.texture.TexturePainter.swapActive;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import com.spinyowl.legui.component.*;
import com.spinyowl.legui.image.Image;
import com.spinyowl.legui.image.loader.ImageLoader;
import com.spinyowl.legui.style.color.ColorConstants;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.ColorButton;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterChannel;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterColor;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterTool;
import net.fexcraft.app.fmt.update.UpdateEvent.PickMode;
import com.spinyowl.legui.event.MouseClickEvent;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.texture.TexturePainter.Tool;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.app.fmt.utils.Picker.PickTask;
import net.fexcraft.app.fmt.utils.Picker.PickType;
import net.fexcraft.app.fmt.utils.Translator;

public class CurrentColor extends EditorComponent {
	
	private static String palette_png = "./resources/textures/icons/painter/palette.png";
	private static Image picker_png = ImageLoader.loadImage("./resources/textures/icons/painter/picker.png");
	private static Image picker_act = ImageLoader.loadImage("./resources/textures/icons/painter/picker_active.png");
	private ColorField[] fields = new ColorField[TexturePainter.CHANNELS.length];
	private ColorButton preview;
	private NumberField active;
	private RunButton tool;
	private Component back;
	private Icon picker;

	public CurrentColor(){
		super("painter.current", 120 + (TexturePainter.CHANNELS.length * HEIGHT), false, true);
		add(new Label(translate(LANG_PREFIX + id + ".color"), L5, row(1), LW, HEIGHT));
		for(int i = 0; i < TexturePainter.CHANNELS.length; i++){
			int fi = i;
			this.add(fields[i] = new ColorField(this, (c, b) -> TexturePainter.updateColor(c, fi, true),
				L5, row(1), Editor.CWIDTH, (float)HEIGHT, palette_png).apply(TexturePainter.CHANNELS[i].packed));
		}
		row += 5;
		add(picker = new Icon(0, 24, 0, Editor.CWIDTH - 60, row(1), "./resources/textures/icons/painter/picker.png", () -> {
			if(Picker.TYPE == PickType.COLOR) Picker.reset();
			else Picker.pick(PickType.COLOR, PickTask.NONE, true);
		}).addTooltip("editor.component.painter.palette.picker"));
		add(new Icon(0, 24, 0, 5, row(), "./resources/textures/icons/component/move_left.png", () -> swapActive(-1)));
		add(new Icon(0, 24, 0, 100, row(), "./resources/textures/icons/component/move_right.png", () -> swapActive(1)));
		add(new Icon(0, 24, 0, Editor.CWIDTH - 90, row(), "./resources/textures/icons/toolbar/save.png", () -> PainterPalette.saveCustom())
			.addTooltip("editor.component.painter.palette.save_custom"));
		add(active = new NumberField(updcom, 35, row(), 60, HEIGHT));
		add(back = new Panel(130, row(), 50, HEIGHT));
		back.getStyle().getBackground().setColor(ColorConstants.white());
		add(preview = new ColorButton(this,  132, row() + 2, 46, HEIGHT - 4));
		active.setup(0, TexturePainter.CHANNELS.length - 1, false, field -> {
			TexturePainter.ACTIVE = (int)field.value();
			UpdateHandler.update(new PainterChannel(TexturePainter.ACTIVE));
		});
		updcom.add(PainterColor.class, event -> {
			fields[event.channel()].apply(event.value());
			if(event.channel() == TexturePainter.ACTIVE) preview.set(event.value());
		});
		updcom.add(PickMode.class, event -> {
			picker.setImage(event.type() == PickType.COLOR ? picker_act : picker_png);
		});
		updcom.add(PainterChannel.class, event -> {
			active.apply(event.channel());
			preview.set(TexturePainter.CHANNELS[event.channel()].packed);
		});
		updcom.add(PainterTool.class, event -> updateToolButton());
		row += 5;
		add(tool = new RunButton(translate(LANG_PREFIX + id + ".active_tool"), L5, row(1), LW, HEIGHT, () -> TexturePainter.setTool(Tool.NONE)));
		updateToolButton();
	}

	private void updateToolButton(){
		tool.getTextState().setText(translate(LANG_PREFIX + id + ".active_tool") + " " + TexturePainter.getToolName());
	}

}
