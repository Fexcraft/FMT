package net.fexcraft.app.fmt.ui.editor;

import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.style.Background;
import org.liquidengine.legui.style.color.ColorConstants;

import net.fexcraft.app.fmt.utils.Settings;

public class EditorWidget extends Widget {
	
	private EditorBase editor;

	public EditorWidget(EditorBase base, String title, int x, int y, int w, int h){
		super(x, y, w, h); editor = base;
		Background background = new Background();
		if(!Settings.darktheme()){
			background.setColor(ColorConstants.lightGray());
		}
		else{
			background.setColor(ColorConstants.darkGray());
		}
		getTitle().getStyle().setFontSize(22f); getTitleTextState().setText(title);
		getTitleContainer().getStyle().setBackground(background);
		getTitleContainer().setSize(getTitleContainer().getSize().x, 20);
		getTitle().getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        setCloseable(false); setResizable(false); setDraggable(false);
	}
	
	@Override
	public void setMinimized(boolean bool){
		super.setMinimized(bool);
		editor.reOrderWidgets();
	}

	public void toggle(){
		setMinimized(!isMinimized());
	}
	
}
