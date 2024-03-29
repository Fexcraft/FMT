package net.fexcraft.app.fmt.nui;

import net.fexcraft.lib.common.math.RGB;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FMTInterface extends Element {

	public FMTInterface(){
		super();
		elements = new ArrayList<>();
		elements.add(new Element().pos(0, 0).size(100, 200).color(RGB.BLUE).linecolor(RGB.WHITE).recompile());
		elements.add(new Element().pos(200, 0).size(500, 100).color(RGB.GREEN).linecolor(RGB.BLACK).recompile());
	}

	@Override
	public Element recompile(){
		//
		return this;
	}

	@Override
	public void delete(){
		//
	}

}
