package net.fexcraft.app.fmt.ui;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class HidingElm extends Element {

	@Override
	public void update(){
		if(root.hoveredx()) show();
		else hide();
	}

}
