package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Scrollable;
import net.fexcraft.app.fmt.ui.tree.TreeRoot.TreeMode;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_0;
import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TreeTab extends Element {

	public final TreeMode mode;
	protected UpdateCompound updcom = new UpdateCompound();
	protected Scrollable container;
	protected Element over;

	public TreeTab(TreeMode emode){
		super();
		mode = emode;
		color(GENERIC_BACKGROUND_0.value);
		resize();
	}

	public static TreeTab create(TreeMode mode){
		switch(mode){
			case POLYGON: return new PolygonTreeTab();
			case PREVIEW: return new HelperTreeTab();
		}
		return new TreeTab(mode);
	}

	@Override
	public void init(Object... args){
		add(over = new Element().pos(0, TOOLBAR_HEIGHT).size(EDITOR_WIDTH, (int)args[0]).color(GENERIC_BACKGROUND_0.value));
		over.z += 100;
		over.recompile();
		add((container = new Scrollable(false, TOOLBAR_HEIGHT + over.h)));
		container.updateSize(w, h);
	}

	@Override
	public void onResize(){
		size(EDITOR_WIDTH, FMT.SCALED_HEIGHT);
		if(container == null) return;
		container.updateSize(w, h);
	}

	public void reorderComponents(){
		container.updateBar();
	}

	public void updateCounter(){}

	public void reinsertComponents(){}

}
