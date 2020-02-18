package net.fexcraft.app.fmt.ui.tree;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Tooltip;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.BufferedImage;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMTB;

public class TreeIcon extends ImageView {
	
	public TreeIcon(int x, int y, String adress, MouseClickEventListener listener){
		super(new BufferedImage("./resources/textures/icons/" + adress + ".png"));
		setSize(20, 20); setPosition(x, y); getStyle().setBorderRadius(0);
		this.getListenerMap().addListener(MouseClickEvent.class, listener);
	}
	
	public TreeIcon(int x, int y, String adress, Runnable run){
		this(x, y, adress, (MouseClickEventListener)event -> { if(event.getAction() == CLICK){ run.run(); } });
	}

	public TreeIcon(int x, int y, String adress, Runnable run, String tooltip){
		this(x, y, adress, run); Tooltip tool = new Tooltip(tooltip);
		tool.setSize(80, 20); tool.setPosition(-60, 20); this.setTooltip(tool);
		tool.getStyle().getBackground().setColor(FMTB.rgba(161, 194, 169, 0.8f));
		tool.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
	}
	
}
