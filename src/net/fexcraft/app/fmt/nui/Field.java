package net.fexcraft.app.fmt.nui;

import net.fexcraft.lib.common.math.RGB;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Field extends Element {

	public boolean hidden;

	public Field(int width){
		super();
		size(width, 30);

	}

	@Override
	public void init(Object... args){
		add(new Element().color(RGB.BLACK).size(2, 20).pos(w - 2, 5));
	}

	@Override
	public Element text(Object ntext){
		super.text(ntext).recompile();
		if(elements != null){
			elements.get(0).pos(text.w > w ? w - 2 : text.w + 6, 5);
		}
		return this;
	}

}
