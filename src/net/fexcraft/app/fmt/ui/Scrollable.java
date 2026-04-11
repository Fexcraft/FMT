package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.ui.editor.ETabCom;
import net.fexcraft.app.fmt.ui.tree.TTabCom;
import net.fexcraft.app.fmt.utils.Logging;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.ui.FMTInterface.col_85;
import static net.fexcraft.app.fmt.ui.FMTInterface.col_cd;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Scrollable extends Element {

	public static int SCROLLBAR_WIDTH = 20;
	private boolean left;
	private Element bar;
	private Element up;
	private Element dw;
	private float scrolled;
	private float top;
	public float ih;

	public Scrollable(boolean onleft, float topoff){
		left = onleft;
		top = topoff;
	}

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
		size(width, height - top);
		pos(0, top);
		updateBar();
	}

	public void updateBar(){
		if(elements == null) return;
		ih = 5;
		for(Element elm : elements){
			if(!elm.visible) continue;
			if(elm instanceof ETabCom){
				ih += elm.h + 5;
			}
			if(elm instanceof TTabCom com){
				ih += com.h + (com.container.visible ? com.container.h : 0) + 5;
			}
			if(elm instanceof SettingsUI.SettingBlock){
				ih += elm.h + 5;
			}
		}
		//
		float m = h - 32;
		float bh = (h / ih) * m;
		if(bh > m) bh = m;
		if(bh < 40) bh = 40;
		float p = (m - bh) * scrolled;
		bar.pos(left ? w - SCROLLBAR_WIDTH : 5, 16 + p);
		bar.size(16, bh).recompile();
		up.pos(left ? w - SCROLLBAR_WIDTH : 5, 0);
		dw.pos(left ? w - SCROLLBAR_WIDTH : 5 , h - 16);
		recompile();
		bh = ih < h ? 0 : (h - ih) * scrolled;
		float incr = 5;
		for(Element elm : elements){
			if(elm instanceof ETabCom){
				elm.pos(5, incr + bh);
				if(!elm.visible) continue;
				incr += elm.h + 5;
			}
			if(elm instanceof TTabCom com){
				com.pos(SCROLLBAR_WIDTH + 5, incr + bh);
				if(!elm.visible) continue;
				incr += com.h + (com.container.visible ? com.container.h : 0) + 5;
			}
			if(elm instanceof SettingsUI.SettingBlock){
				elm.pos(5, incr + bh);
				incr += elm.h + 5;
			}
		}
	}

}
