package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.ui.SelectorBar;
import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.lib.common.math.RGB;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorSidePanel extends Element {

	public EditorSidePanel(){
		super();
		pos(EDITOR_WIDTH, 0);
		size(40, 200);
		color(col_cd);
	}

	@Override
	public void init(Object... args){
		add(new EditorList(),0, 0, "icons/panels/editors");
		add(new Multiplier(),0, 42, "icons/panels/multiplier");
	}

	public static class Panel extends Element {

		protected Element container;
		protected boolean expanded;
		protected int ew, eh;

		public Panel(){
			super();
			size(32, 32);
			onclick(ci -> toggle());
			hoverable = true;
		}

		@Override
		public void init(Object... args){
			add(container = new Element().pos(w + 4, -4).size(ew, eh).color(col_cd).border(RGB.BLACK).hide());
			pos((int)args[0] + 4, (int)args[1] + 4);
			texture(args[2].toString());
		}

		public void toggle(){
			expanded = !expanded;
			container.visible = expanded;
		}

	}

	public static class EditorList extends Panel {

		@Override
		public void init(Object... args){
			ew = 325;
			eh = 40;
			super.init(args);
			int iinc = 35, buff = -iinc + 5, yo = 4;
			for(EditorRoot.EditorMode mode : EditorRoot.EditorMode.values()){
				container.add(new Element().pos(buff += iinc, yo).size(32, 32)
					.texture("icons/editor/" + mode.name().toLowerCase()).hoverable(true)
					.onclick(ci -> EditorRoot.setMode(mode))
					.hint("editor.mode." + mode.name().toLowerCase()));
			}
		}
	}

	public static class Multiplier extends Panel {

		private SelectorBar[] bars = new SelectorBar[3];
		private Element text;

		@Override
		public void init(Object... args){
			ew = 330;
			eh = 88;
			super.init(args);
			container.add(text = new Field(Field.FieldType.FLOAT, 90).pos(5, 28).onscroll(si -> {
				float er = Editor.RATE;
				if(si.sy() > 0) er *= 2;
				else er /= 2;
				if(er > 1024) er = 1024;
				if(er < 0.001) er = 0.001f;
				UpdateHandler.update(new UpdateEvent.EditorRate(Editor.RATE = er));
			}).text(Editor.RATE).hoverable(true).hide());
			Consumer<Float> mul = m -> {
				if(Editor.RATE != m) UpdateHandler.update(new UpdateEvent.EditorRate(Editor.RATE = m));
			};
			container.add((bars[0] = new SelectorBar()).pos(90, 10).hide(), 200, 1, 16, 1, "1 - 16", mul);
			container.add((bars[1] = new SelectorBar()).pos(90, 35).hide(), 200, 0.0625, 1, 0.0625, "0.0625 - 1", mul);
			container.add((bars[2] = new SelectorBar()).pos(90, 60).hide(), 200, 0.1, 1, 0.1, "0.1 - 1", mul);
			UpdateHandler.register(com -> {
				com.add(UpdateEvent.EditorRate.class, e -> text.text(e.rate()));
			});
		}

		@Override
		public void toggle(){
			super.toggle();
			text.visible = container.visible;
			for(SelectorBar bar : bars) bar.visible = container.visible;
		}

	}

}
