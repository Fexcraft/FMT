package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.field.BoolButton;
import net.fexcraft.app.fmt.ui.field.ColorField;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Setting.StringArraySetting;
import net.fexcraft.app.fmt.utils.Setting.Type;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Translator;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class SettingsBox {

	public static final void open(String title, Collection<Setting> coll, boolean settings, AfterTask task){
		TreeMap<String, Setting> map = new TreeMap<>();
		for(Setting setting : coll) map.put(setting.getId(), setting);
		if(coll.isEmpty() || map.isEmpty()){
			task.process(map);
			return;
		}
		Dialog dialog = new Dialog(title, 520, 350);
		dialog.setResizable(false);
		ScrollablePanel panel = new ScrollablePanel(10, 10, 500, 280);
		int size = 10 + (coll.size() * 30);
		int index = 0;
		panel.getContainer().setSize(500, size < 280 ? 280 : size);
		for(Setting setting : coll){
			panel.getContainer().add(new Label(setting.getId(), 10, 10 + (index * 30), 180, 20));
			if(setting.getType().isBoolean()){
				panel.getContainer().add(new BoolButton(setting, 190, 10 + (index * 30), 290, 20));
			}
			else if(setting.getType() == Type.FLOAT || setting.getType() == Type.INTEGER){
				panel.getContainer().add(new NumberField(setting, 190, 10 + (index * 30), 290, 20));
			}
			else if(setting.getType() == Type.RGB){
				panel.getContainer().add(new ColorField(panel.getContainer(), setting, 190, 10 + (index * 30), 290, 20));
			}
			else if(setting.getType() == Type.STRING_ARRAY){
				SelectBox<String> box = new SelectBox<>(190, 10 + (index * 30), 290, 20);
				String[] array = setting.getValue();
				for(String str : array) box.addElement(str);
				box.addSelectBoxChangeSelectionEventListener(listener -> {
					setting.as(StringArraySetting.class).setSelected(listener.getNewValue());
				});
				box.setVisibleCount(6);
				panel.getContainer().add(box);
			}
			else{// if(setting.getType() == Type.STRING || setting.getType() == Type.FLOAT_ARRAY){
				panel.getContainer().add(new TextField(setting, 190, 10 + (index * 30), 290, 20));
			}
			index++;
		}
		panel.setHorizontalScrollBarVisible(false);
		Button button = new Button(Translator.translate("settingsbox." + (settings ? "confirm" : "continue")), 10, 300, 100, 20);
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()){
				task.process(map);
				dialog.close();
			}
		});
		dialog.getContainer().add(panel);
		dialog.getContainer().add(button);
		dialog.show(FMTB.frame);
	}

	@FunctionalInterface
	public static interface AfterTask {

		public void process(Map<String, Setting> settings);

	}

	public static void openFMTSettings(){
		SettingsBox.open(Translator.translate("toolbar.file.settings"), Settings.SETTINGS.values(), true, settings -> {});
	}

}
