package net.fexcraft.app.fmt.ui;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class HidingElm extends Element {

	public HidingElm(){
		super();
		update_if_invisible = true;
	}

	@Override
	public void update(){
		if(root.hoveredx()) show();
		else hide();
	}

}
