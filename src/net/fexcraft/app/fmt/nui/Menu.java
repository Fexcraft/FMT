package net.fexcraft.app.fmt.nui;

import java.util.function.Consumer;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Menu extends Element {

	private Consumer<Menu> onhover;
	private boolean vertical;
	private boolean open;
	private int ran;

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
		elm.pos(1 + (vertical ? 0 : w), vertical ? 0 : (elements.size() - 1) * 30);
		elm.z -= 2;
		elm.recompile();
	}

	@Override
	public void update(){
		if(hoveredx()) open = true;
		if(open){
			if(vertical){
				for(int i = 0; i < elements.size(); i++){
					if(ran >= 10) continue;
					elements.get(i).visible = true;
					elements.get(i).ya((i + 1) * 3);
				}
				if(ran < 10) ran++;
			}
			else{
				for(Element elm : elements) elm.visible = true;
			}
		}
		else{
			if(vertical){
				for(int i = 0; i < elements.size(); i++){
					if(ran <= 0) continue;
					elements.get(i).visible = elements.get(i).y() > 0;
					elements.get(i).ya((-i - 1) * 3);
				}
				if(ran > 0) ran--;
			}
			else{
				for(Element elm : elements) elm.visible = false;
			}
		}
		if(!hoveredx()) open = false;
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
