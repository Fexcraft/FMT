package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterpanels.BoolButton;
import net.fexcraft.app.fmt.ui.UserInterpanels.Button20;
import net.fexcraft.app.fmt.ui.UserInterpanels.Dialog20;
import net.fexcraft.app.fmt.ui.UserInterpanels.Label20;
import net.fexcraft.app.fmt.ui.UserInterpanels.NumberInput20;
import net.fexcraft.app.fmt.ui.UserInterpanels.TextInput20;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class SettingsBox {
	
	public static final void open(String title, Collection<Setting> coll, boolean settings, AfterTask task){
		TreeMap<String, Setting> map = new TreeMap<>();
		for(Setting setting : coll) map.put(setting.getId(), setting);
		if(coll.isEmpty() || map.isEmpty()){ task.process(map); return; }
        Dialog20 dialog = new Dialog20(title, 520, 350); dialog.setResizable(false);
        ScrollablePanel panel = new ScrollablePanel(10, 10, 500, 280);
        int size = 10 + (coll.size() * 30); int index = 0;
        panel.getContainer().setSize(500, size < 280 ? 280 : size);
        for(Setting setting : coll){
        	panel.getContainer().add(new Label20(setting.getId(), 10, 10 + (index * 30), 180, 20));
        	if(setting.getType().isBoolean()){
        		panel.getContainer().add(new BoolButton(setting, 190, 10 + (index * 30), 290, 20));
        	}
        	else if(setting.getType() == Type.STRING || setting.getType() == Type.FLOAT_ARRAY || setting.getType() == Type.RGB){
        		panel.getContainer().add(new TextInput20(setting, 190, 10 + (index * 30), 290, 20));
        	}
        	else if(setting.getType() == Type.FLOAT || setting.getType() == Type.INTEGER){
        		panel.getContainer().add(new NumberInput20(setting, 190, 10 + (index * 30), 290, 20));
        	}
        	index++;
        }
        panel.setHorizontalScrollBarVisible(false);
        Button20 button = new Button20(UserInterpanels.translate("settingsbox." + (settings ? "confirm" : "continue")), 10, 300, 100, 20);
        button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){ task.process(map); dialog.close(); }
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
		SettingsBox.open(UserInterpanels.translate("toolbar.file.settings"), Settings.SETTINGS.values(), true, settings -> {});
	}

}
