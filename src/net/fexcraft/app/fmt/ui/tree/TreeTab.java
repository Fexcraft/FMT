package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Scrollable;
import net.fexcraft.app.fmt.ui.tree.TreeRoot.TreeMode;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;

import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TreeTab extends Element {

	public final TreeMode mode;
	protected UpdateCompound updcom = new UpdateCompound();
	protected Scrollable container;

	public TreeTab(TreeMode emode){
		super();
		mode = emode;
		color(col_cd);
		resize();
	}

	public static TreeTab create(TreeMode mode){
		switch(mode){
			//case POLYGON: return new PolygonTreeTab();
		}
		return new TreeTab(mode);
	}

	@Override
	public void init(Object... args){
		add((container = new Scrollable(false)));
		container.updateSize(w, h);
	}

	@Override
	public void onResize(){
		size(EDITOR_WIDTH, FMT.SCALED_HEIGHT - TOOLBAR_HEIGHT);
		if(container == null) return;
		container.updateSize(w, h);
	}

	public void reorderComponents(){
		container.updateBar();
	}

}
