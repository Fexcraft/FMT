package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Frame extends Element {

	@Override
	public void onDrag(float xdiff, float ydiff){
		xa(xdiff);
		ya(ydiff);
	}

}
