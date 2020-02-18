package net.fexcraft.app.fmt.ui.editor;

import java.util.ArrayList;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.Slider;
import org.liquidengine.legui.component.Tooltip;
import org.liquidengine.legui.component.event.slider.SliderChangeValueEventListener;
import org.liquidengine.legui.component.misc.listener.scrollablepanel.ScrollablePanelViewportScrollListener;
import org.liquidengine.legui.event.ScrollEvent;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.Static;

public class EditorBase extends Panel {

	public static String NO_POLYGON_SELECTED;
	protected ArrayList<EditorWidget> widgets = new ArrayList<>();
	protected ScrollablePanel scrollable;
	
	public EditorBase(){
		super(0, 30, 304, FMTB.HEIGHT - 30); Editors.editors.add(this);
		String[] arr = new String[]{ "normal", "sixteenth", "decimal"}; int off = 0;
		Label label = new Label(translate("editor.multiplicator"), 4, 4, 100, 24);
		super.add(label); label.getStyle().setFontSize(20f); int am = 0;
		Label current = new Label(format("editor.multiplicator.current", 1f), 4, 28, 100, 24);
		super.add(current); current.getStyle().setFontSize(20f);
		for(String string : arr){
			Slider multislider = new Slider(148, 4 + off, 150, 14);
			switch(string){
				case "normal":{
					multislider.setMinValue(0); multislider.setMaxValue(64);
					multislider.setStepSize(1); multislider.setValue(1f); am = 0;
					break;
				}
				case "sixteenth":{
					multislider.setMinValue(0); multislider.setMaxValue(1); am = 4;
					multislider.setStepSize(Static.sixteenth); multislider.setValue(1f);
					break;
				}
				case "decimal":{
					multislider.setMinValue(0); multislider.setMaxValue(1); am = 1;
					multislider.setStepSize(0.1f); multislider.setValue(1f);
					break;
				}
			}
	        final Tooltip multitip = new Tooltip();
	        multitip.setSize(100, 20); multitip.getStyle().setFontSize(20f);
	        multitip.setPosition(multislider.getSize().x + 2, 0); final String amo = "%." + am + "f";
	        multitip.getTextState().setText(translate("editor.multiplicator.value") + String.format(amo, multislider.getValue()));
	        multislider.addSliderChangeValueEventListener((SliderChangeValueEventListener) event -> {
	            multitip.getTextState().setText(translate("editor.multiplicator.value") + String.format(amo, event.getNewValue()));
	            current.getTextState().setText(format("editor.multiplicator.current", event.getNewValue()));
	            multitip.setSize(100, 28); FMTB.MODEL.rate = event.getNewValue();
	        });
	        multislider.setTooltip(multitip);
	        super.add(multislider); off += 16;
		}
        scrollable = new ScrollablePanel(0, 54, 304, FMTB.HEIGHT - 80);
        scrollable.getStyle().getBackground().setColor(1, 1, 1, 1);
        scrollable.setHorizontalScrollBarVisible(false);
        scrollable.getContainer().setSize(296, FMTB.HEIGHT - 80);
        scrollable.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
        scrollable.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL());
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
		if(com instanceof EditorWidget) widgets.add((EditorWidget)com);
		return scrollable.getContainer().add(com);
	}

	protected void reOrderWidgets(){
		float size = 0; for(EditorWidget widget : widgets) size += widget.getSize().y + 2;
		scrollable.getContainer().setSize(scrollable.getSize().x, size > FMTB.HEIGHT - 80 ? size : FMTB.HEIGHT - 80); size = 0;
		for(EditorWidget widget : widgets){ widget.setPosition(0, size); size += widget.getSize().y + 2; }
	}
	
	public static class SPVSL extends ScrollablePanelViewportScrollListener {
		
	    @Override
	    public void process(@SuppressWarnings("rawtypes") ScrollEvent event){
	    	if(FMTB.field_scrolled) return; else super.process(event);
	    }
	    
	}
	
	public static String translate(String str){
		return Translator.translate(str, "no.lang");
	}
	
	public static String format(String str, Object... objs){
		return Translator.format(str, objs);
	}
	
}
