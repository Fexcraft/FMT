package net.fexcraft.app.fmt.ui.tree;

import java.util.ArrayList;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.misc.listener.scrollablepanel.ScrollablePanelViewportScrollListener;
import org.liquidengine.legui.event.ScrollEvent;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.EditorBase;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.Settings;

public class TreeBase extends Panel {

	protected ArrayList<TreeGroup> groups = new ArrayList<>();
	public ScrollablePanel scrollable;
	public String counterlabel;
	public Label counter; 
	public final String id;
	private TreeGroup selected;
	
	public TreeBase(String name){
		super(FMTB.WIDTH - 304, 30, 304, FMTB.HEIGHT - 30); Trees.trees.add(this); id = name;
		super.add(counter = new Label((counterlabel = EditorBase.translate("tree." + id + ".counter")) + "0", 4, 1, 100, 24));
		Settings.THEME_CHANGE_LISTENER.add(bool -> {
			counter.getStyle().setFontSize(24f);
		});
        scrollable = new ScrollablePanel(0, 28, 304, FMTB.HEIGHT - 60);
        scrollable.getStyle().getBackground().setColor(1, 1, 1, 1);
        scrollable.setHorizontalScrollBarVisible(false);
        scrollable.getContainer().setSize(296, FMTB.HEIGHT - 60);
        scrollable.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
        scrollable.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL(this));
        super.add(scrollable); this.hide();
	}

	public void toggle(){
		if(isVisible()) hide(); else show();
	}
	
	public void hide(){
		this.getStyle().setDisplay(DisplayType.NONE);
	}
	
	public void show(){
		this.getStyle().setDisplay(DisplayType.MANUAL);
	}
	
	public boolean addSub(Component com){
		if(com instanceof TreeGroup && !groups.contains(com)) groups.add((TreeGroup)com);
		return scrollable.getContainer().add(com);
	}

	public void addSub(int index, Component com){
		if(com instanceof TreeGroup && !groups.contains(com)) groups.add(index, (TreeGroup)com);
		for(TreeGroup group : groups) scrollable.getContainer().remove(group);
		for(TreeGroup group : groups) scrollable.getContainer().add(group);
	}

	public void reOrderGroups(){
		float size = 2; for(TreeGroup tree : groups) size += tree.getSize().y + 2;
		scrollable.getContainer().setSize(scrollable.getSize().x, size > FMTB.HEIGHT - 60 ? size : FMTB.HEIGHT - 60); size = 2;
		for(TreeGroup tree : groups){ tree.setPosition(0, size); size += tree.getSize().y + 2; }
	}

	public void updateCounter(){
		switch(this.id){
			case "polygon": counter.getTextState().setText(counterlabel + FMTB.MODEL.countTotalMRTs()); break;
			case "helper": counter.getTextState().setText(counterlabel + HelperCollector.LOADED.size()); break;
			case "fvtm": counter.getTextState().setText(counterlabel + FMTB.MODEL.getGroups().size()); break;
			default: return;
		}
	}

	public void clear(){
		scrollable.getContainer().removeIf(filter -> true); groups.clear(); reOrderGroups();
	}

	public int groupAmount(){
		return groups.size();
	}
	
	public void select(TreeGroup group){
		if(selected != null){
			TreeGroup old = selected;
			selected = null;
			old.onScrollDeselect();
			if(old == group) return;
		}
		(selected = group).onScrollSelect();
	}

	public boolean isSelected(TreeGroup group){
		return selected == group;
	}
	
	public static class SPVSL extends ScrollablePanelViewportScrollListener {
		
		private TreeBase base;

		public SPVSL(TreeBase base){
			this.base = base;
		}
		
	    @Override
	    public void process(@SuppressWarnings("rawtypes") ScrollEvent event){
	    	if(FMTB.field_scrolled) return; 
	    	if(base.selected != null) base.selected.onScroll(event.getYoffset());
	    	else super.process(event);
	    }
	    
	}
	
}
