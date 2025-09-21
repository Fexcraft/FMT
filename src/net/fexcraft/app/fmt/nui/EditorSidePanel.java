package net.fexcraft.app.fmt.nui;

import net.fexcraft.lib.common.math.RGB;

import static net.fexcraft.app.fmt.nui.EditorRoot.EDITOR_WIDTH;
import static net.fexcraft.app.fmt.nui.FMTInterface.col_75;
import static net.fexcraft.app.fmt.nui.FMTInterface.col_cd;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorSidePanel extends Element {

	public EditorSidePanel(){
		super();
		pos(EDITOR_WIDTH, 0);
		size(34, 64);
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
		}

		@Override
		public void init(Object... args){
			add(new Element().size(32, 32).texture(args[0].toString()).onclick(() -> toggle()));
		}

		public void toggle(){
			expanded = !expanded;
			size(expanded ? ew : 32, expanded ? eh : 32);
			recompile();
		}

	}

	public static class Multiplier extends Panel {

		private SelectorBar[] bars = new SelectorBar[1];
		private Element text;

		public Multiplier(int x, int y){
			super(x, y, 380, 90);
		}

		@Override
		public void init(Object... args){
			super.init(args);
			linecolor(RGB.BLACK);
			add(text = new Element().size(80, 30).pos(38, 20).color(col_75).text("0").hide());
			add((bars[0] = new SelectorBar()).pos(130, 10).hide(), 200);
		}

		@Override
		public void toggle(){
			super.toggle();
			text.visible = expanded;
			for(SelectorBar bar : bars) bar.visible = expanded;
		}

	}

}
