package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_0;
import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TreeRoot extends Element {

	public static TreeTab[] TREES = new TreeTab[TreeMode.values().length];
	private Element over;

	public TreeRoot(){
		super();
		pos(FMT.SCALED_WIDTH - EDITOR_WIDTH, TOOLBAR_HEIGHT);
		color(GENERIC_BACKGROUND_0.value);
		resize();
	}

	@Override
	public void init(Object... args){
		add(over = new Element().size(EDITOR_WIDTH, TOOLBAR_HEIGHT).color(GENERIC_BACKGROUND_0.value));
		over.z += 100;
		over.recompile();
		for(TreeMode mode : TreeMode.values()){
			over.add(new Element().pos(5 + mode.ordinal() * 35, 5 ).size(30, 30).texture("icons/tree/" + mode.name().toLowerCase())
				.onclick(ci -> setMode(mode)).hint("tree.mode." + mode.name().toLowerCase()));
		}
		for(int i = 0; i < TreeMode.values().length; i++){
			add(TREES[i] = TreeTab.create(TreeMode.values()[i]), 0);
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
		pos(FMT.SCALED_WIDTH - EDITOR_WIDTH, 0);
		size(EDITOR_WIDTH, FMT.HEIGHT);
	}

	public void toggle(){
		visible = !visible;
	}

	public static void updateCounters(){
		for(TreeTab tree : TREES){
			tree.updateCounter();
		}
	}

	public static enum TreeMode {

		POLYGON, TEXTURE, PREVIEW, ANIMATION

	}

}
