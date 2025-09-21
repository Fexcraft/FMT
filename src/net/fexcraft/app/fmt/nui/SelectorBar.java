package net.fexcraft.app.fmt.nui;

import java.util.function.Consumer;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SelectorBar extends Element {

	private float wid;
	private float min;
	private float max;
	private float inc;
	private float wdv;
	private int steps;
	private Consumer<Float> cons;

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
		wid = ((Number)args[0]).floatValue() - 16;
		min = ((Number)args[1]).floatValue();
		max = ((Number)args[2]).floatValue();
		inc = ((Number)args[3]).floatValue();
		steps = (int)(max / inc) - 1;
		wdv = wid / steps;
		cons = (Consumer<Float>)args[5];
		add(new Element().size(16, 16).texture("ui/arrow_left").pos(0, 0)
			.onclick(ci -> select(elements.get(3).x() - 16 - wdv - 1)));
		add(new Element().size(16, 16).texture("ui/arrow_right").pos(4 + wid + 32, 0)
			.onclick(ci -> select(elements.get(3).x() - 16 + wdv + 1)));
		add(new Element().size(wid + 16, 16).texture("ui/selector_bar").pos(18, 0));
		add(new Element().size(8, 16).texture("ui/selector_bar_selector").pos(20, 0).zi());
		elements.get(2).onclick(ci -> {
			select(ci.lx());
		}).onscroll(si -> {
			select(elements.get(3).x() - 16 + (si.sy() > 0 ? wdv + 1 : -wdv - 1));
		});
		elements.get(2).hint(args[4].toString());
	}

	private void select(float lx){
		float per = lx / wid;
		if(per < 0) per = 0;
		if(per > 1) per = 1;
		int cen = (int)(per * steps) + (per + 0.05 >= 1 ? 1 : 0);
		if(cen >= steps) cen = steps;
		elements.get(3).pos(cen * wdv + 20, 0);
		cons.accept(cen * inc + min);
	}

}
