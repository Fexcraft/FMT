package net.fexcraft.app.fmt.nui;

import java.util.function.Consumer;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Menu extends Element {

	private Consumer<Menu> onhover;
	private boolean vertical;
	private boolean open;

	public Menu(){
		super();
		hoverable = true;
	}

	@Override
	public Element root(Element elm){
		super.root(elm);
		vertical = !(elm instanceof Menu);
		if(vertical) z++;
		return this;
	}

	@Override
	public void add(Element elm){
		elm.visible = false;
		elm.hoverable = true;
		elm.size(w - 2, h);
		super.add(elm);
		elm.pos(1 + (vertical ? 0 : w), (elements.size() - (vertical ? 0 : 1)) * 30);
		elm.z += 2;
		elm.recompile();
	}

	@Override
	public void update(){
		if(hoveredx() && !open){
			open = true;
			for(Element elm : elements) elm.visible = open;
		}
		if(!hoveredx() && open){
			open = false;
			for(Element elm : elements) elm.visible = open;
		}
		super.update();
	}

	@Override
	public void hovered(boolean bool){
		super.hovered(bool);
		if(onhover != null) onhover.accept(this);
	}

	public Menu onhover(Consumer<Menu> cons){
		onhover = cons;
		return this;
	}

}
