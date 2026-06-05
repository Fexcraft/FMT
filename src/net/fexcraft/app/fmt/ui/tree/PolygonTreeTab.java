package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.tree.GroupCom.GroupComSubElm;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.utils.Translator;

import static net.fexcraft.app.fmt.ui.editor.EditorTab.FF;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonTreeTab extends TreeTab {

	private static PolygonValue[] TRACKED = new PolygonValue[]{
		CurvePolyCom.CUR_AMOUNT, CurvePolyCom.CUR_ACTIVE, CurvePolyCom.COLOR,
		CurvePolyCom.CUR_AMT_PNT, CurvePolyCom.CUR_ACT_PNT,
		CurvePolyCom.CUR_AMT_PLN, CurvePolyCom.CUR_ACT_PLN,
		ObjPolyCom.VERTICES, ObjPolyCom.VERT_ACT,
		ObjPolyCom.OBJ_FACES, ObjPolyCom.OBJ_FACE_ACT, ObjPolyCom.OBJ_FACE_TRI
	};
	public static String TOTALS_FORMAT;
	public Element totals;

	public PolygonTreeTab(){
		super(TreeRoot.TreeMode.POLYGON);
	}

	@Override
	public void init(Object... objs){
		super.init(30);
		TOTALS_FORMAT = Translator.translate("tree.info.polygon_count");
		over.add(totals = new Element().pos(5, 0).size(FF, 30).translate(TOTALS_FORMAT, "...").text_autoscale());
		updcom.add(UpdateEvent.PivotAdded.class, event -> addPivot(event.pivot()));
		updcom.add(UpdateEvent.PivotRemoved.class, event -> remPivot(event.pivot()));
		updcom.add(UpdateEvent.PivotRenamed.class, event -> {
			for(Element elm : container.elements){
				if(elm instanceof PivotCom com && com.pivot == event.pivot()){
					com.text(event.pivot().id);
				}
			}
		});
		updcom.add(UpdateEvent.PivotSelected.class, event -> {
			for(Element elm : container.elements){
				if(elm instanceof PivotCom com){
					com.updateTextColor();
				}
			}
		});
		updcom.add(UpdateEvent.PivotVisibility.class, event -> {
			for(Element elm : container.elements){
				if(elm instanceof PivotCom com && com.pivot == event.pivot()){
					com.updateTextColor();
				}
			}
		});
		updcom.add(UpdateEvent.GroupAdded.class, event -> addGroup(event.group()));
		updcom.add(UpdateEvent.GroupRemoved.class, event -> remGroup(event.group()));
		updcom.add(UpdateEvent.GroupPivotChanged.class, event -> {
			PivotCom pc = getPivotCom(event.old_pivot());
			boolean v = pc.getGroupCom(event.group()).container.visible;
			pc.remGroup(event.group());
			addGroup(event.group());
			if(!v) getGroupCom(event.group()).hide();
		});
		updcom.add(UpdateEvent.ModelLoad.class, event -> reinsertComponents());
		updcom.add(UpdateEvent.ModelUnload.class, event -> removePivots());
		updcom.add(UpdateEvent.GroupRenamed.class, event -> {
			GroupCom com = getGroupCom(event.group());
			if(com != null) com.text(event.group().id);
		});
		updcom.add(UpdateEvent.GroupSelected.class, event -> {
			for(Element elm : container.elements){
				if(elm instanceof PivotCom com){
					if(com.elements == null) continue;
					for(Element ge : com.container.elements){
						((GroupCom)ge).updateTextColor();
					}
				}
			}
		});
		updcom.add(UpdateEvent.GroupVisibility.class, event -> {
			GroupCom com = getGroupCom(event.group());
			if(com != null) com.updateTextColor();
		});
		updcom.add(UpdateEvent.PolygonAdded.class, event -> {
			GroupCom com = getGroupCom(event.group());
			if(com != null) com.addPolygon(event.polygon());
		});
		updcom.add(UpdateEvent.PolygonRenamed.class, event -> {
			GroupComSubElm com = getPolyCom(event.polygon());
			if(com != null) ((Element)com).text(event.polygon().name());
		});
		updcom.add(UpdateEvent.PolygonValueEvent.class, event -> {
			for(PolygonValue val : TRACKED){
				if(val == event.value()){
					GroupComSubElm com = getPolyCom(event.polygon());
					if(com != null){
						float c = com.height();
						com.refresh();
						if(c != com.height()) getGroupCom(event.polygon().group()).orderComponents();
					}
					return;
				}
			}
		});
		updcom.add(UpdateEvent.PolygonRemoved.class, event -> getGroupCom(event.group()).remPolygon(event.polygon()));
		updcom.add(UpdateEvent.PolygonSelected.class, event -> updatePolyLabel(event.polygon()));
		updcom.add(UpdateEvent.PolygonVisibility.class, event -> updatePolyLabel(event.polygon()));
	}

	private PivotCom getPivotCom(Pivot pivot){
		for(Element elm : container.elements){
			if(elm instanceof PivotCom com && com.pivot == pivot) return com;
		}
		return null;
	}

	private PivotCom getPivotCom(String pivot){
		for(Element elm : container.elements){
			if(elm instanceof PivotCom com && com.pivot.id.equals(pivot)) return com;
		}
		return null;
	}

	private GroupCom getGroupCom(Group group){
		PivotCom com = getPivotCom(FMT.MODEL.getP(group.pivot));
		return com == null ? null : com.getGroupCom(group);
	}

	private GroupComSubElm getPolyCom(Polygon poly){
		if(poly.group() == null) return null;
		GroupCom com = getGroupCom(poly.group());
		return com == null ? null : com.getPolyCom(poly);
	}

	private void updatePolyLabel(Polygon poly) {
		GroupComSubElm com = getPolyCom(poly);
		if(com != null) com.updateLabelColor();
	}

	private void addPivot(Pivot pivot){
		container.add(new PivotCom(pivot));
		reorderComponents();
	}

	private void remPivot(Pivot pivot){
		container.remElmIf(e -> e instanceof PivotCom com && com.pivot == pivot);
		reorderComponents();
	}

	private void addGroup(Group group){
		getPivotCom(FMT.MODEL.getP(group.pivot)).addGroup(group);
	}

	private void remGroup(Group group){
		getPivotCom(FMT.MODEL.getP(group.pivot)).remGroup(group);
	}

	private void removePivots(){
		container.remElmIf(e -> e instanceof PivotCom);
		reorderComponents();
	}

	@Override
	public void reinsertComponents(){
		container.remElmIf(e -> e instanceof PivotCom);
		for(Pivot pivot : FMT.MODEL.pivots()){
			container.add(new PivotCom(pivot));
		}
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

	public static void focusSelected(){
		Polygon poly = FMT.MODEL.first_selected();
		if(poly == null) return;
		PolygonTreeTab tab = (PolygonTreeTab)TreeRoot.TREES[TreeRoot.TreeMode.POLYGON.ordinal()];
		GroupComSubElm sub = tab.getPolyCom(poly);
		if(sub == null) return;
		PivotCom pivot = tab.getPivotCom(poly.group().pivot);
		if(!pivot.visible) pivot.show();
		GroupCom group = tab.getGroupCom(poly.group());
		if(!group.visible) group.show();
		if(sub instanceof TTabCom com){
			com.show();
			com.minimized_changed();
		}
		tab.container.scrollTo(pivot, group.y() + ((Element)sub).y());
	}

}
