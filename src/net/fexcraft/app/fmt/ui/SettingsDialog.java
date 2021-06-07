package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
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
		tabs.getContainer().setSize(Settings.SETTINGS.size() * (lw + 2), 30);
		int[] i = { 0 };
		Settings.SETTINGS.keySet().forEach(key -> {
			Button button = new Button(translate("settings.category." + key), i[0]++ * (lw + 2), 0, 100, 30);
			button.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
			Settings.applyMenuTheme(button);
			tabs.getContainer().add(button);
		});
		Settings.applyBorderless(tabs);
		tabs.setVerticalScrollBarVisible(false);
		tabs.setFocusable(false);
		tabs.getContainer().setFocusable(false);
		dialog.getContainer().add(tabs);
		//
		dialog.show(FMT.FRAME);
	}

}
