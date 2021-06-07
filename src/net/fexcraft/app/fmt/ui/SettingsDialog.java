package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;

import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class SettingsDialog {
	
	public static Dialog dialog;
	public static ArrayList<ScrollablePanel> panels = new ArrayList<>();

	public static final void open(){
		if(dialog != null) dialog.close();
		panels.clear();
		int width = 520, lw = 100;
		dialog = new Dialog(translate("settings.dialog.title"), width, 350);
		dialog.setResizable(false);
		ScrollablePanel tabs = new ScrollablePanel(0, 0, width, 40);
		tabs.getContainer().setSize(Settings.SETTINGS.size() * lw, 30);
		int[] i = { 0 };
		Settings.SETTINGS.keySet().forEach(key -> {
			Label label = new Label(translate("settings.category." + key), i[0]++ * lw, 0, 100, 30);
			label.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
			Settings.applyBorderless(label);
			tabs.getContainer().add(label);
		});
		Settings.applyBorderless(tabs);
		tabs.setVerticalScrollBarVisible(false);
		dialog.getContainer().add(tabs);
		//
		dialog.show(FMT.FRAME);
	}

}
