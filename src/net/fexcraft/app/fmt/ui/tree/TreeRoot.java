package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TreeRoot extends Element {

	public static TreeTab[] TREES = new TreeTab[TreeMode.values().length];

	public TreeRoot(){
		super();
		pos(FMT.SCALED_WIDTH - EDITOR_WIDTH, TOOLBAR_HEIGHT);
		color(col_cd);
		resize();
	}

	@Override
	public void init(Object... args){
		for(int i = 0; i < TreeMode.values().length; i++){
			add(TREES[i] = TreeTab.create(TreeMode.values()[i]));
			TREES[i].reorderComponents();
			UpdateHandler.register(TREES[i].updcom);
		}
		setMode(TreeMode.POLYGON);
	}

	public static void setMode(TreeMode mode){
		for(TreeTab editor : TREES) editor.hide();
		TREES[mode.ordinal()].show();
	}

	@Override
	public void onResize(){
		pos(FMT.SCALED_WIDTH - EDITOR_WIDTH, TOOLBAR_HEIGHT);
		size(EDITOR_WIDTH, FMT.HEIGHT);
	}

	public void toggle(){
		visible = !visible;
	}

	public static enum TreeMode {

		POLYGON, TEXTURE, PREVIEW, ANIMATION

	}

}
