package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.nui.Element;

import static net.fexcraft.app.fmt.nui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorTab extends Element {

	public final String lang_prefix;
	public final EditorRoot.EditorMode mode;

	public EditorTab(EditorRoot.EditorMode emode){
		super();
		mode = emode;
		lang_prefix = "editor." + emode.name().toLowerCase() + ".";
		color(col_cd);
		onResize();
	}

	@Override
	public void init(Object... objs){
		switch(mode){
			case POLYGON -> initPolygonEditor();
			case MODEL -> {}
			case PIVOT -> {}
			case TEXTURE -> {}
			case PAINTER -> {}
			case PREVIEW -> {}
			case VARIABLE -> {}
			case ANIMATION -> {}
		}
	}

	private void initPolygonEditor(){
		add(new Element().pos(5, 5).text(lang_prefix + "name").color(col_bd).size(EDITOR_CONTENT, 30));
	}

	@Override
	public void onResize(){
		super.onResize();
		size(EDITOR_WIDTH, FMT.SCALED_HEIGHT - TOOLBAR_HEIGHT);
	}

}
