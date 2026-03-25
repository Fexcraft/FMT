package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.ui.Element;

import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TTabCom extends Element {

	protected boolean minimized = false;
	protected Element label;
	protected int fullheight;

	@Override
	public void init(Object... args){
		border(col_85);
		size(EDITOR_CONTENT, fullheight = (int)args[1]);
		add(label = new Element().translate(args[0].toString()).size(EDITOR_CONTENT, 30));
		label.add(new Element().hoverable(true).texture("icons/component/minimize").size(28, 28).pos(EDITOR_CONTENT - 29, 1).onclick(ci -> {
			if(minimized) show();
			else hide();
		}));
		updateLabelColor();
	}

	@Override
	public Element hide(){
		minimized = true;
		size(EDITOR_CONTENT, 30);
		for(int i = 1; i < elements.size(); i++){
			elements.get(i).hide();
		}
		updateLabelColor();
		recompile();
		((TreeTab)root.root).reorderComponents();
		return this;
	}

	@Override
	public Element show(){
		minimized = false;
		size(EDITOR_CONTENT, fullheight);
		for(int i = 1; i < elements.size(); i++){
			elements.get(i).show();
		}
		updateLabelColor();
		recompile();
		((TreeTab)root.root).reorderComponents();
		return this;
	}

	protected void updateLabelColor(){
		label.color(0xffffff);
	}

}
