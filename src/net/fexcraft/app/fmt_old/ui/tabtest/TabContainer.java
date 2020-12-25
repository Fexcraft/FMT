package net.fexcraft.app.fmt_old.ui.tabtest;

import java.util.ArrayList;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.lib.common.Static;

public class TabContainer extends Panel {

	private ArrayList<TabButton> tabs = new ArrayList<>();
	private ArrayList<Panel> panels = new ArrayList<>();
	private ScrollablePanel scrollable;

	public TabContainer(int x, int y, int w, int h){
		super(x, y, w, h);
        scrollable = new ScrollablePanel(0, 0, w, 40);
        scrollable.getStyle().getBackground().setColor(1, 1, 1, 1);
        scrollable.setVerticalScrollBarVisible(false);
        scrollable.getContainer().setSize(w, 30);
        add(scrollable); //hide(this);
	}

	public void addTab(String title, Panel panel){
		TabButton button = new TabButton(this, panels.size(), title);
		this.panels.add(panel);
		panel.setPosition(0, 40);
		this.add(panel);
		this.tabs.add(button);
		button.setPosition(button.index * TabButton.WIDTH, 0);
		scrollable.getContainer().add(button);
		int w = tabs.size() * TabButton.WIDTH;
		scrollable.getContainer().setSize(w < this.getSize().x ? this.getSize().x : w, 30);
	}
	
	public static void hide(Component com){
		com.getStyle().setDisplay(DisplayType.NONE);
	}
	
	public static void show(Component com){
		com.getStyle().setDisplay(DisplayType.MANUAL);
	}

	public static void addTest(Frame frame){
		TabContainer container = new TabContainer(300, 200, 600, 480);
		frame.getContainer().add(container);
		for(int i = 0; i < 20; i++){
			Panel panel = new Panel(0, 0, 600, 480);
			panel.add(new Label("This is a test panel number " + i + "!", 10, 10, 100, 20));
			int rand = Static.random.nextInt(9);
			panel.add(new Label("Random position label.", 10, 40 + (rand * 25), 100, 20));
			container.addTab("Test" + i, panel);
		}
	}

	public void openTab(int index){
		for(int i = 0; i < panels.size(); i++){
			if(i == index) show(panels.get(i)); else hide(panels.get(i));
		}
	}

}
