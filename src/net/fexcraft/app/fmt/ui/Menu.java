package net.fexcraft.app.fmt.ui;

import net.fexcraft.lib.common.math.RGB;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_0;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_1;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Menu extends Element {

	private boolean open;

	public Menu(float width){
		super();
		border(RGB.BLACK);
		color(GENERIC_BACKGROUND_0.value);
		size(width, 2);
		hide();
	}

	public void addEntry(String text, Consumer<ClickInfo> cons){
		add(new Element().pos(1, 1 + countEntries() * 30).size(w - 2, 29).onclick(ci -> {
			if(cons != null) cons.accept(ci);
			hide();
		}).translate(text).color(GENERIC_BACKGROUND_1.value).hoverable(true));
		size(w, 2 + countEntries() * 30);
		recompile();
	}

	public Menu addEntry(String text, Menu menu){
		Element elm = new Element().pos(1, 1 + countEntries() * 30).size(w - 2, 29).onclick(ci -> {
			menu.toggleVisibility();
		}).translate(text).color(GENERIC_BACKGROUND_1.value).hoverable(true);
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
