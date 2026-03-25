package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.Translator;

import static net.fexcraft.app.fmt.ui.FMTInterface.TOOLBAR_HEIGHT;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FF;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonTreeTab extends TreeTab {

	public static String TOTALS_FORMAT;
	public Element totals;

	public PolygonTreeTab(){
		super(TreeRoot.TreeMode.POLYGON);
	}

	@Override
	public void init(Object... objs){
		super.init(30);
		TOTALS_FORMAT = Translator.translate("tree.info.polygon_count");
		add(totals = new Element().pos(5, TOOLBAR_HEIGHT).size(FF, 30).translate(TOTALS_FORMAT, "...").text_autoscale());
	}

	@Override
	public void updateCounter(){
		long p = 0;
		for(Group group : FMT.MODEL.allgroups()){
			p += group.size();
		}
		totals.translate(TOTALS_FORMAT, p);
	}

}
