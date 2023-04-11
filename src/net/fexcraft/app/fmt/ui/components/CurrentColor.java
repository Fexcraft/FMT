package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import net.fexcraft.app.fmt.update.UpdateEvent.PainterColor;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterTool;
import net.fexcraft.app.fmt.update.UpdateEvent.PickMode;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.StbBackedLoadableImage;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.texture.TexturePainter.Tool;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.app.fmt.utils.Picker.PickTask;
import net.fexcraft.app.fmt.utils.Picker.PickType;

public class CurrentColor extends EditorComponent {
	
	private static String palette_png = "./resources/textures/icons/painter/palette.png";
	private static StbBackedLoadableImage picker_png = new StbBackedLoadableImage("./resources/textures/icons/painter/picker.png");
	private static StbBackedLoadableImage picker_act = new StbBackedLoadableImage("./resources/textures/icons/painter/picker_active.png");
	private ColorField field1, field2;
	private ColorPickerIcon icon1, icon2;
	private RunButton tool;

	public CurrentColor(){
		super("painter.current", 140, false, true);
		this.add(new Label(translate(LANG_PREFIX + id + ".color"), L5, row(1), LW, HEIGHT));
		this.add(field1 = new ColorField(this, (c, b) -> {
			TexturePainter.updateColor(c, true);
		}, L5, (float)row(1), Editor.CWIDTH - 27.5f, (float)HEIGHT, palette_png).apply(TexturePainter.PRIMARY.packed));
		this.add(icon1 = new ColorPickerIcon(row(), true));
		this.add(field2 = new ColorField(this, (c, b) -> {
			TexturePainter.updateColor(c, false);
		}, L5, (float)row(1), Editor.CWIDTH - 27.5f, (float)HEIGHT, palette_png).apply(TexturePainter.SECONDARY.packed));
		this.add(icon2 = new ColorPickerIcon(row(), false));
		updcom.add(PainterColor.class, event -> {
			(event.primary() ? field1 : field2).apply(event.value());
		});
		updcom.add(PickMode.class, event -> {
			icon1.setImage(event.type() == PickType.COLOR1 ? picker_act : picker_png);
			icon2.setImage(event.type() == PickType.COLOR2 ? picker_act : picker_png);
		});
		updcom.add(PainterTool.class, event -> updateToolButton());
		row += 5;
		this.add(tool = new RunButton(translate(LANG_PREFIX + id + ".active_tool"), L5, row(1), LW, HEIGHT, () -> TexturePainter.setTool(Tool.NONE)));
		updateToolButton();
	}
	
	private void updateToolButton(){
		tool.getTextState().setText(translate(LANG_PREFIX + id + ".active_tool") + " " + TexturePainter.getToolName());
	}

	private static class ColorPickerIcon extends ImageView {
		
		public ColorPickerIcon(int y, boolean primary){
			super(picker_png);
			setPosition(Editor.CWIDTH - 30, y);
			setSize(24, 24);
			getListenerMap().addListener(MouseClickEvent.class, event -> {
				if(event.getAction() != CLICK) return;
				PickType type = primary ? PickType.COLOR1 : PickType.COLOR2;
				if(Picker.TYPE == type) Picker.reset();
				else Picker.pick(type, PickTask.NONE, true);
			});
			Settings.applyBorderless(this);
		}
		
	}

}
