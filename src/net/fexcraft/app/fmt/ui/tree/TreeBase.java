package net.fexcraft.app.fmt.ui.tree;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.misc.listener.scrollablepanel.ScrollablePanelViewportScrollListener;
import org.liquidengine.legui.event.ScrollEvent;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils.Icon;
import net.fexcraft.app.fmt.ui.editor.EditorBase;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.texture.TextureManager;

public class TreeBase extends Panel {

	protected ArrayList<TreeGroup> groups = new ArrayList<>();
	public ScrollablePanel scrollable;
	public String counterlabel;
	public Label counter; 
	public final String id;
	private TreeGroup selected;
	
	public TreeBase(String name){
		super(FMTB.WIDTH - 304, 30, 304, FMTB.HEIGHT - 30); Trees.trees.add(this); id = name;
		super.add(counter = new Label((counterlabel = EditorBase.translate("tree." + id + ".counter") + " ") + "0", 4, 28, 100, 24));
		Settings.THEME_CHANGE_LISTENER.add(bool -> {
			counter.getStyle().setFontSize(24f);
		});
        scrollable = new ScrollablePanel(0, 60, 304, FMTB.HEIGHT - 90);
        scrollable.getStyle().getBackground().setColor(1, 1, 1, 1);
        scrollable.setHorizontalScrollBarVisible(false);
        scrollable.getContainer().setSize(296, scrollable.getSize().y);
        scrollable.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
        scrollable.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL(this));
        super.add(scrollable); this.hide();
	}
	
	public void addIcons(){
		for(int i = 0; i < Trees.trees.size(); i++){
			TreeBase tree = Trees.trees.get(i);
			super.add(new Icon(i, "./resources/textures/icons/tree/" + tree.id + ".png", () -> Trees.show(tree.id)));
		}
		Icon icon = new Icon(0, "./resources/textures/icons/tree/hide.png", () -> Trees.hideAll());
		icon.setPosition(304 - icon.getSize().x - 1, icon.getPosition().y);
		super.add(icon);
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
		if(com instanceof TreeGroup) groups.add((TreeGroup)com);
		return scrollable.getContainer().add(com);
	}

	public void addSub(int index, Component com){
		if(com instanceof TreeGroup) groups.add(index, (TreeGroup)com);
		for(TreeGroup group : groups) scrollable.getContainer().remove(group);
		for(TreeGroup group : groups) scrollable.getContainer().add(group);
	}

	public void reOrderGroups(){
		float size = 2; for(TreeGroup tree : groups) size += tree.getSize().y + 2;
		scrollable.getContainer().setSize(scrollable.getSize().x, size > FMTB.HEIGHT - 90 ? size : FMTB.HEIGHT - 90); size = 2;
		for(TreeGroup tree : groups){ tree.setPosition(0, size); size += tree.getSize().y + 2; }
	}

	public void updateCounter(){
		switch(this.id){
			case "polygon":{
				String str = counterlabel + FMTB.MODEL.countTotalMRTs();
				long totaf = FMTB.MODEL.countTotalFaces(false);
				long totat = FMTB.MODEL.countTotalFaces(true);
				str += " / " + totaf + " / " + totat;
				if(totaf > 0) str += " / " + nf.format((totat / (float)totaf) * 100f) + "%";
				counter.getTextState().setText(str);
				break;
			}
			case "helper": counter.getTextState().setText(counterlabel + HelperCollector.LOADED.size()); break;
			case "fvtm": counter.getTextState().setText(counterlabel + FMTB.MODEL.getGroups().size()); break;
			case "textures": counter.getTextState().setText(counterlabel + TextureManager.getGroupAmount());
			default: return;
		}
	}
	
	private static NumberFormat nf = NumberFormat.getInstance(Locale.US);
	static{
		nf.setMaximumFractionDigits(1);
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
