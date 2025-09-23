package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.lib.common.math.RGB;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.nui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorSidePanel extends Element {

	public EditorSidePanel(){
		super();
		pos(EDITOR_WIDTH, 0);
		size(32, 200);
		color(col_cd);
	}

	@Override
	public void init(Object... args){
		add(new Multiplier(0, 0), "icons/multiplier");
	}

	public static class Panel extends Element {

		protected boolean expanded;
		protected int ew, eh;

		public Panel(int x, int y, int w, int h){
			super();
			pos(x, y);
			size(32, 32);
			color(col_cd);
			ew = w;
			eh = h;
			expanded = false;
			hoverable = true;
		}

		@Override
		public void init(Object... args){
			add(new Element().size(32, 32).texture(args[0].toString()).onclick(ci -> toggle()));
		}

		public void toggle(){
			expanded = !expanded;
			size(expanded ? ew : 32, expanded ? eh : 32);
			linecolor(RGB.BLACK);
			border = expanded;
			recompile();
		}

	}

	public static class Multiplier extends Panel {

		private SelectorBar[] bars = new SelectorBar[3];
		private Element text;

		public Multiplier(int x, int y){
			super(x, y, 380, 88);
		}

		@Override
		public void init(Object... args){
			super.init(args);
			add(text = new Element().size(80, 30).pos(38, 28).color(col_75).text(Editor.RATE).onscroll(si -> {
				float er = Editor.RATE;
				if(si.sy() > 0) er *= 2;
				else er /= 2;
				if(er > 1024) er = 1024;
				if(er < 0.001) er = 0.001f;
				UpdateHandler.update(new UpdateEvent.EditorRate(Editor.RATE = er));
			}).hoverable(true).hide());
			Consumer<Float> mul = m -> {
				if(Editor.RATE != m) UpdateHandler.update(new UpdateEvent.EditorRate(Editor.RATE = m));
			};
			add((bars[0] = new SelectorBar()).pos(130, 10).hide(), 200, 1, 16, 1, "1 - 16", mul);
			add((bars[1] = new SelectorBar()).pos(130, 35).hide(), 200, 0.0625, 1, 0.0625, "0.0625 - 1", mul);
			add((bars[2] = new SelectorBar()).pos(130, 60).hide(), 200, 0.1, 1, 0.1, "0.1 - 1", mul);
			UpdateHandler.register(com -> {
				com.add(UpdateEvent.EditorRate.class, e -> text.text(e.rate()));
			});
		}

		@Override
		public void toggle(){
			super.toggle();
			text.visible = expanded;
			for(SelectorBar bar : bars) bar.visible = expanded;
		}

	}

}
