package net.fexcraft.app.fmt.nui;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Menu extends Element {

	private int ran;

	@Override
	public void add(Element elm){
		elm.visible = false;
		elm.size(w - 2, h);
		super.add(elm);
		elm.pos(0, 30);
	}

	@Override
	public void update(){
		if(hovered){
			size(200, elements.size() * 30 + 30);
			for(int i = 0; i < elements.size(); i++){
				if(ran > 30) continue;
				elements.get(i).visible = true;
				elements.get(i).ya(i);
			}
			ran++;
		}
		else{
			size(200, 30);
			for(int i = 0; i < elements.size(); i++){
				elements.get(i).visible = elements.get(i).y() > 30;
				if(elements.get(i).y() > 30) elements.get(i).ya(-2);
			}
			if(ran > 0) ran--;
		}
		super.update();
	}

}
