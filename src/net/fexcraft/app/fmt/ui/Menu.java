package net.fexcraft.app.fmt.ui;

import net.fexcraft.lib.common.math.RGB;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.ui.FMTInterface.col_bd;
import static net.fexcraft.app.fmt.ui.FMTInterface.col_cd;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Menu extends Element {

	private boolean open;

	public Menu(float width){
		super();
		border(RGB.BLACK);
		color(col_cd);
		size(width, 2);
		hide();
	}

	public void addEntry(String text, Consumer<ClickInfo> cons){
		add(new Element().pos(1, 1 + countEntries() * 30).size(w - 2, 29).onclick(ci -> {
			if(cons != null) cons.accept(ci);
			hide();
		}).translate(text).color(col_bd).hoverable(true));
		size(w, 2 + countEntries() * 30);
		recompile();
	}

	public Menu addEntry(String text, Menu menu){
		Element elm = new Element().pos(1, 1 + countEntries() * 30).size(w - 2, 29).onclick(ci -> {
			menu.toggleVisibility();
		}).translate(text).color(col_bd).hoverable(true);
		add(elm);
		if(menu != null){
			elm.add(menu.pos(elm.w, 0));
		}
		size(w, 2 + countEntries() * 30);
		recompile();
		return menu;
	}

	private int countEntries(){
		if(elements == null) return 0;
		int entries = 0;
		for(Element elm : elements){
			if(elm instanceof Menu) continue;
			entries++;
		}
		return entries;
	}

	@Override
	public void update(){
		if(!root.hoveredx() && open) hide();
	}

	@Override
	public Element show(){
		open = true;
		return super.show();
	}

	@Override
	public Element hide(){
		open = false;
		return super.hide();
	}

}
