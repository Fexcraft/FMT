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

	public Menu(int width){
		super();
		border(RGB.BLACK);
		color(col_cd);
		size(width, 2);
		hide();
	}

	@Override
	public Element root(Element elm){
		super.root(elm);
		if(elm instanceof Menu) pos(elm.w, y());
		return this;
	}


	public void addEntry(String text, Consumer<ClickInfo> cons){
		addEntry(text, cons, true);
	}

	public void addEntry(String text, Consumer<ClickInfo> cons, boolean close){
		add(new Element().pos(1, 1 + elms_size() * 30).size(w - 2, 30).onclick(ci -> {
			if(cons != null) cons.accept(ci);
			if(close) hide();
		}).translate(text).color(col_bd).hoverable(true));
		size(w, 2 + elms_size() * 30);
		recompile();
	}

	@Override
	public void update(){
		if(root.hoveredx() && open) show();
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
