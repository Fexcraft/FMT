package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.ui.editor.ETabCom;
import net.fexcraft.app.fmt.utils.Logging;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.ui.FMTInterface.col_85;
import static net.fexcraft.app.fmt.ui.FMTInterface.col_cd;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Scrollable extends Element {

	public static int SCROLLBAR_WIDTH = 20;
	private Element bar;
	private Element up;
	private Element dw;
	private float scrolled;
	public float ih;

	public Scrollable(){}

	@Override
	public void init(Object... args){
		color(col_cd);
		add(bar = new Element().size(16, 16).color(col_85).hoverable(true).shape(ElmShape.RECT_ROUNDED));
		add(up = new Element().size(16, 16).texture("ui/arrow_up").hoverable(true).onclick(ci -> modScrolled(-1)));
		add(dw = new Element().size(16, 16).texture("ui/arrow_down").hoverable(true).onclick(ci -> modScrolled(1)));
		Consumer<ScrollInfo> cons = si -> modScrolled(-si.sy());
		onscroll(cons);
		bar.onscroll(cons);
		up.onscroll(cons);
		dw.onscroll(cons);
	}

	private void modScrolled(float dir){
		if(ih < h) return;
		scrolled += (h / ih) * 0.25f * dir;
		if(scrolled < 0) scrolled = 0;
		if(scrolled > 1) scrolled = 1;
		Logging.bar(scrolled);
		updateBar();
	}

	public void updateSize(float width, float height){
		size(width, height);
		updateBar();
	}

	public void updateBar(){
		if(elements == null) return;
		ih = 5;
		for(Element elm : elements){
			if(elm instanceof ETabCom){
				if(!elm.visible) continue;
				ih += elm.h + 5;
			}
		}
		//
		float m = h - 32;
		float bh = (h / ih) * m;
		if(bh > m) bh = m;
		if(bh < 40) bh = 40;
		float p = (m - bh) * scrolled;
		bar.pos(w - SCROLLBAR_WIDTH, 16 + p);
		bar.size(16, bh).recompile();
		up.pos(w - SCROLLBAR_WIDTH, 0);
		dw.pos(w - SCROLLBAR_WIDTH, h - 16);
		recompile();
		bh = ih < h ? 0 : (h - ih) * scrolled;
		int incr = 5;
		for(Element elm : elements){
			if(elm instanceof ETabCom){
				elm.pos(5, incr + bh);
				if(!elm.visible) continue;
				incr += elm.h + 5;
			}
		}
	}

}
