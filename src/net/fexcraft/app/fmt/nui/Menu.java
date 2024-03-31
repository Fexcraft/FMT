package net.fexcraft.app.fmt.nui;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Menu extends Element {

	private boolean open;
	private int ran;

	public Menu(){
		super();
		hoverable = true;
	}

	@Override
	public Element root(Element elm){
		super.root(elm);
		z++;
		return this;
	}

	@Override
	public void add(Element elm){
		elm.visible = false;
		elm.hoverable = true;
		elm.size(w - 2, h);
		super.add(elm);
		elm.pos(1, 0);
		elm.z -= 2;
		elm.recompile();
	}

	@Override
	public void update(){
		if(hovered) open = true;
		if(open){
			for(int i = 0; i < elements.size(); i++){
				if(ran >= 30) continue;
				elements.get(i).visible = true;
				elements.get(i).ya(i + 1);
			}
			if(ran < 30) ran++;
		}
		else{
			for(int i = 0; i < elements.size(); i++){
				if(ran <= 0) continue;
				elements.get(i).visible = elements.get(i).y() > 0;
				elements.get(i).ya(-i - 1);
			}
			if(ran > 0) ran--;
		}
		if(ran >= 30 && !hoveredx()) open = false;
		super.update();
	}

}
