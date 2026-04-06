package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.ui.Element;

import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TTabCom extends Element {

	public Element container;
	public int fullheight;

	@Override
	public void init(Object... args){
		checkpickpos = false;
		size((int)args[1], 30);
		translate(args[0].toString());
		add(new Element().hoverable(true).texture("icons/component/minimize").size(28, 28).pos((int)args[1] - 30, 1)
			.onclick(ci -> {
				if(container.visible) hide(); else show();
			}));
		add(container = new Element().size(EDITOR_CONTENT, 30).pos(0, 30).border(col_85));
		updateTextColor();
	}

	@Override
	public Element hide(){
		container.hide();
		updateTextColor();
		orderComponents();
		return this;
	}

	@Override
	public Element show(){
		container.show();
		updateTextColor();
		orderComponents();
		return this;
	}

	protected void updateTextColor(){
		color(0xffffff);
	}

	protected void orderComponents(){}

}
