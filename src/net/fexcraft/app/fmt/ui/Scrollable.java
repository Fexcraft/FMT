package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.editor.ETabCom;
import net.fexcraft.app.fmt.ui.tree.TTabCom;
import net.fexcraft.app.fmt.workspace.DirElm;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_0;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_2;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Scrollable extends Element {

	public static int SCROLLBAR_WIDTH = 20;
	private boolean left;
	public final Container container;
	private Element bar;
	private Element up;
	private Element dw;
	private float scrolled;
	public float top;
	public float ih;

	public Scrollable(boolean onleft, float topoff){
		left = onleft;
		top = topoff;
		container = new Container();
		container.shape(ElmShape.NONE);
		container.check_mode(CheckMode.NONE);
	}

	@Override
	public void init(Object... args){
		color(GENERIC_BACKGROUND_0.value);
		super.add(container);
		super.add(bar = new Element().size(16, 16).color(GENERIC_BACKGROUND_2.value).hoverable(true).shape(ElmShape.RECT_ROUNDED));
		super.add(up = new Element().size(16, 16).texture("ui/arrow_up").hoverable(true).onclick(ci -> modScrolled(-1)));
		super.add(dw = new Element().size(16, 16).texture("ui/arrow_down").hoverable(true).onclick(ci -> modScrolled(1)));
		Consumer<ScrollInfo> cons = si -> modScrolled(-si.sy());
		onscroll(cons);
		container.onscroll(cons);
		bar.onscroll(cons);
		up.onscroll(cons);
		dw.onscroll(cons);
	}

	@Override
	public void add(Element elm){
		new Exception().printStackTrace();
		FMT.close(0);
	}

	@Override
	public void remElm(Element elm){
		new Exception().printStackTrace();
		FMT.close(0);
	}

	@Override
	public void remElmIf(Predicate<Element> pre){
		new Exception().printStackTrace();
		FMT.close(0);
	}

	private void modScrolled(float dir){
		if(ih < h) return;
		scrolled += (h / ih) * h * dir;
		if(scrolled < 0) scrolled = 0;
		if(scrolled > ih - h) scrolled = ih - h;
		//Logging.bar(scrolled);
		updateBar();
	}

	public void updateSize(float width, float height){
		size(width, height - top);
		container.size(w, h);
		pos(0, top);
		updateBar();
	}

	public void updateBar(){
		if(container.elements == null) return;
		ih = 5;
		for(Element elm : container.elements){
			if(!elm.visible) continue;
			if(elm instanceof ETabCom){
				ih += elm.h + 5;
			}
			else if(elm instanceof TTabCom com){
				ih += com.h + (com.container.visible ? com.container.h : 0) + 5;
			}
			else if(elm instanceof SettingsUI.SettingBlock){
				ih += elm.h + 5;
			}
			else if(elm instanceof DirElm dir){
				dir.updateContainer();
				ih += elm.h + 5 + (dir.container.visible ? dir.container.h : 0);
			}
			else{
				if(elm.x() > 5) continue;
				ih += elm.h + 2;
			}
		}
		//
		float m = h - 32;
		float bh = (h / ih) * m;
		if(bh > m) bh = m;
		if(bh < 40) bh = 40;
		float p = m * (scrolled / ih);
		if(p + bh > m) p = m - bh;
		bar.pos(left ? w - SCROLLBAR_WIDTH : 5, 16 + p);
		bar.size(16, bh).recompile();
		up.pos(left ? w - SCROLLBAR_WIDTH : 5, 0);
		dw.pos(left ? w - SCROLLBAR_WIDTH : 5 , h - 16);
		container.recompile();
		bar.recompile();
		container.pos(0, ih < h ? 0 : -scrolled);
		float incr = 5;
		for(Element elm : container.elements){
			if(elm == bar || elm == up || elm == dw) continue;
			if(elm instanceof ETabCom){
				elm.pos(5, incr);
				if(!elm.visible) continue;
				incr += elm.h + 5;
			}
			else if(elm instanceof TTabCom com){
				com.pos(SCROLLBAR_WIDTH + 5, incr);
				if(!elm.visible) continue;
				incr += com.h + (com.container.visible ? com.container.h : 0) + 5;
			}
			else if(elm instanceof SettingsUI.SettingBlock){
				elm.pos(5, incr);
				incr += elm.h + 5;
			}
			else if(elm instanceof DirElm dir){
				elm.pos(5, incr);
				incr += elm.h + 5 + (dir.container.visible ? dir.container.h : 0);
			}
			else{
				elm.pos(elm.x(), incr);
				if(elm.x() > 5) continue;
				incr += elm.h + 2;
			}
		}
	}

	public void clear(){
		container.clearElements(false);
	}

	public void scrollTo(float off){
		scrolled = off;
		if(scrolled > ih - h) scrolled = ih - h;
		updateBar();
	}

	@Override
	public boolean isContainer(){
		return true;
	}

	public Collection<Element> elements(){
		if(container.elements == null) return Collections.emptyList();
		return container.elements;
	}

}
