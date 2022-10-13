package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox.SelectBoxLayer;
import org.liquidengine.legui.component.misc.listener.scrollablepanel.ScrollablePanelViewportScrollListener;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.ScrollEvent;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.KeyCompound;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class SettingsDialog {
	
	public static Dialog dialog;
	public static HashMap<String, Panel> panels = new HashMap<>();

	public static final void open(List<Setting<?>> list, String catid){
		if(dialog != null) dialog.close();
		panels.clear();
		UpdateHolder holder = new UpdateHolder();
		boolean hlist = list != null;
		int width = 530, height = 320, minus = hlist ? 0 : 110;
		if(!hlist) width += minus;
		dialog = new Dialog(translate("settings.dialog.title"), width, height);
		dialog.setResizable(false);
		if(!hlist){
			ScrollablePanel tabs = new ScrollablePanel(0, 2, 110, height - 4);
			tabs.getContainer().setSize(100, Settings.SETTINGS.size() * 32 + 16);
			int[] i = { 0 };
			Settings.SETTINGS.keySet().forEach(key -> {
				Button button = new Button(translate("settings.category." + key), 0, i[0]++ * 32, 100, 30);
				button.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
				Settings.applyMenuTheme(button);
				button.getListenerMap().addListener(MouseClickEvent.class, lis -> show(key));
				tabs.getContainer().add(button);
			});
			Settings.applyBorderless(tabs);
			tabs.setHorizontalScrollBarVisible(false);
			dialog.getContainer().add(tabs);
		}
		//
		if(hlist){
			addSinglePanel(list, catid, holder, width, height);
		}
		else{
			for(Entry<String, Map<String, Setting<?>>> entry : Settings.SETTINGS.entrySet()){
				addPanel(entry, holder, width, height);
			}
		}
		if(!hlist) show(Settings.GENERAL);
		UpdateHandler.registerHolder(holder);
		if(!hlist){
			dialog.addWidgetCloseEventListener(lis -> {
				UpdateHandler.deregisterHolder(holder);
				if(!hlist) Settings.refresh();
			});
		}
		dialog.show(FMT.FRAME);
	}
	
	private static void addSinglePanel(List<Setting<?>> list, String catid, UpdateHolder holder, int width, int height){
		Panel wrapper = new Panel(10, 10, width - 20, height - 40);
		ScrollablePanel panel = new ScrollablePanel(0, 0, width - 20, height - 40);
		panel.getContainer().setSize(width - 20, list.size() * 30 + 5);
		int[] j = { 0 }, w = { width };
		list.forEach(setting -> {
			Label label = new Label(translate("setting." + catid + "." + setting.id), 5, j[0] * 30 + 5, 200, 25);
			Settings.applyBorderless(label);
			label.getStyle().setHorizontalAlign(HorizontalAlign.RIGHT);
			panel.getContainer().add(label);
			Component comp = setting.createField(panel.getContainer(), holder, 215, j[0] * 30 + 5, w[0] - 250, 25);
			if(comp != null) panel.getContainer().add(comp);
			else {
				label = new Label(translate("settings.no_field_found"), 215, j[0] * 30 + 5, w[0] - 250, 25);
				Settings.applyBorderless(label);
				label.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
				panel.getContainer().add(label);
			}
			j[0]++;
		});
		Settings.applyBorderless(panel);
		Settings.applyBorderless(panel.getContainer());
		panel.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
		panel.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL());
		panel.setHorizontalScrollBarVisible(false);
		//TODO continue button
		wrapper.add(panel);
		dialog.getContainer().add(wrapper);
		panels.put("exporter", wrapper);
	}

	private static void addPanel(Entry<String, Map<String, Setting<?>>> entry, UpdateHolder holder, int width, int height){
		Panel wrapper = new Panel(120, 10, width - 130, height - 40);
		Map<String, Setting<?>> settings = entry.getValue();
		ScrollablePanel panel = new ScrollablePanel(0, 0, width - 130, height - 40);
		boolean control = entry.getKey().equals(Settings.CONTROL);
		panel.getContainer().setSize(width - 20, settings.size() * 30 + 5 + (control ? 30 : 0));
		int[] j = { 0 }, w = { width };
		settings.values().forEach(setting -> {
			Label label = new Label(translate("setting." + entry.getKey() + "." + setting.id), 5, j[0] * 30 + 5, 200, 25);
			Settings.applyBorderless(label);
			label.getStyle().setHorizontalAlign(HorizontalAlign.RIGHT);
			panel.getContainer().add(label);
			Component comp = setting.createField(panel.getContainer(), holder, 215, j[0] * 30 + 5, w[0] - 360, 25);
			if(comp != null) panel.getContainer().add(comp);
			else {
				label = new Label(translate("settings.no_field_found"), 215, j[0] * 30 + 5, w[0] - 360, 25);
				Settings.applyBorderless(label);
				label.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
				panel.getContainer().add(label);
			}
			j[0]++;
		});
		if(control){
			Label label = new Label(translate("settings.control_adjuster"), 5, j[0] * 30 + 5, 200, 25);
			Settings.applyBorderless(label);
			label.getStyle().setHorizontalAlign(HorizontalAlign.RIGHT);
			panel.getContainer().add(label);
			RunButton button = new RunButton("dialog.button.open", 215, j[0] * 30 + 5, width - 360, 25, () -> {
				dialog.close();
				KeyCompound.openAdjuster();
			});
			Settings.applyMenuTheme(button);
			button.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
			panel.getContainer().add(button);
			j[0]++;
		}
		Settings.applyBorderless(panel);
		Settings.applyBorderless(panel.getContainer());
		panel.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
		panel.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL());
		panel.setHorizontalScrollBarVisible(false);
		wrapper.add(panel);
		dialog.getContainer().add(wrapper);
		panels.put(entry.getKey(), wrapper);
		UIUtils.hide(wrapper);
	}

	private static void show(String key){
		for(Entry<String, Panel> entry : panels.entrySet()){
			if(entry.getKey().equals(key)) UIUtils.show(entry.getValue());
			else UIUtils.hide(entry.getValue());
		}
	}
	
	public static class SPVSL extends ScrollablePanelViewportScrollListener {
		
	    @Override
	    public void process(@SuppressWarnings("rawtypes") ScrollEvent event){
	    	if(FMT.SELFIELD != null || FMT.FRAME.getAllLayers().stream().filter(l -> l instanceof SelectBoxLayer).count() > 0) return;
	    	else super.process(event);
	    }
	    
	}

}
