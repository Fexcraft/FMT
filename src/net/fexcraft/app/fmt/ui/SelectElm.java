package net.fexcraft.app.fmt.ui;

import com.spinyowl.legui.component.SelectBox;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SelectElm<T> extends SelectBox<T> {

	public SelectElm(int x, int y, int w, int h){
		super(x, y, w, h);
	}

	public void clearElements(){
		while(getElements().size() > 0) removeElement(0);
	}
}
