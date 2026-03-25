package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.UpdateEvent;
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
		lastElement().z += 100;
		lastElement().recompile();
		updcom.add(UpdateEvent.GroupAdded.class, event -> addGroup(event.group()));
		updcom.add(UpdateEvent.GroupRemoved.class, event -> removeGroup(event.group()));
		updcom.add(UpdateEvent.ModelLoad.class, event -> reorderComponents());
		updcom.add(UpdateEvent.ModelUnload.class, event -> removeGroups());
		updcom.add(UpdateEvent.GroupRenamed.class, event -> {
			for(Element elm : container.elements){
				if(elm instanceof GroupCom com && com.group == event.group()){
					com.label.text(event.group().id);
				}
			}
		});
		updcom.add(UpdateEvent.GroupSelected.class, event -> {
			for(Element elm : container.elements){
				if(elm instanceof GroupCom com){
					com.updateLabelColor();
				}
			}
		});
		updcom.add(UpdateEvent.GroupVisibility.class, event -> {
			for(Element elm : container.elements){
				if(elm instanceof GroupCom com && com.group == event.group()){
					com.updateLabelColor();
				}
			}
		});
	}

	private void addGroup(Group group){
		container.add(new GroupCom(group));
		reorderComponents();
	}

	private void removeGroup(Group group){
		container.elements.removeIf(e -> e instanceof GroupCom && ((GroupCom)e).group == group);
		reorderComponents();
	}

	private void removeGroups(){
		container.elements.removeIf(e -> e instanceof GroupCom);
		reorderComponents();
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
