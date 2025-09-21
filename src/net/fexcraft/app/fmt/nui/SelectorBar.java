package net.fexcraft.app.fmt.nui;

import java.util.function.Consumer;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SelectorBar extends Element {

	public SelectorBar(){
		super();
	}

	/**
	 * default height: 16
	 * @param args <br>
	 *             [0] num: bar width <br>
	 *             [1] num: min val <br>
	 *             [2] num: max val <br>
	 *             [3] num: increment <br>
	 *             [4] String: hint <br>
	 *             [5] Consumer<Float>: ran on click <br>
	 */
	@Override
	public void init(Object... args){
		float wid = ((Number)args[0]).floatValue();
		float min = ((Number)args[1]).floatValue();
		float max = ((Number)args[2]).floatValue();
		float inc = ((Number)args[3]).floatValue();
		int steps = (int)(max / inc) - 1;
		float wdv = (wid - 16) / steps;
		Consumer<Float> cons = (Consumer<Float>)args[5];
		add(new Element().size(16, 16).texture("ui/arrow_left").pos(0, 0));
		add(new Element().size(16, 16).texture("ui/arrow_right").pos(4 + wid + 16, 0));
		add(new Element().size(wid, 16).texture("ui/selector_bar").pos(18, 0));
		add(new Element().size(8, 16).texture("ui/selector_bar_selector").pos(14, 0).zi());
		elements.get(2).onclick = ci -> {
			float per = ci.lx() / wid;
			int cen = (int)(per * steps) + (per + 0.05 >= 1 ? 1 : 0);
			elements.get(3).pos(cen * wdv + 20, 0);
			cons.accept(cen * inc + min);
		};
		elements.get(2).hint(args[4].toString());
	}

}
