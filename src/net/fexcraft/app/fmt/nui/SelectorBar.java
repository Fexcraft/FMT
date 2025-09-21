package net.fexcraft.app.fmt.nui;

import static net.fexcraft.app.fmt.nui.FMTInterface.col_85;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SelectorBar extends Element {

	public SelectorBar(){
		super();
	}

	/**
	 * default height: 20
	 * @param args [0] int: bar width
	 */
	@Override
	public void init(Object... args){
		add(new Element().size(16, 16).texture("ui/arrow_left").pos(0, 2));
		add(new Element().size(16, 16).texture("ui/arrow_right").pos(4 + (int)args[0] + 16, 2));
		add(new Element().size((int)args[0], 8).color(col_85).pos(18, 6));
	}

}
