package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.math.RGB;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SettingsUI extends Element {

	private static float WIDTH = 700;
	private static float HEIGHT = 500;
	public DropList<String> category;
	public LinkedHashMap<String, Scrollable> containers = new LinkedHashMap<>();

	public SettingsUI(){
		super();
		pos((FMT.SCALED_WIDTH - WIDTH) * 0.5f, (FMT.SCALED_HEIGHT - HEIGHT) * 0.5f);
		size(WIDTH, HEIGHT + 30);
		z += 200;
		color(col_cd);
		border(RGB.BLACK);
	}

	@Override
	public void init(Object... args){
		add(new Element().size(w - 31, 30).color(col_bd)
			.translate("settings.dialog.title").text_autoscale());
		add(new Element().size(30, 30).pos(w - 31, 0).texture("icons/component/remove")
			.hoverable(true).onclick(ci -> {
				Element.select(null);
				hide();
			}));
		add((category = new DropList<>(w - 20)).pos(10, 40));
		category.drop.z += 100;
		for(String key : Settings.SETTINGS.keySet()){
			category.addEntry(Translator.translate("settings.category." + key), key);
			fillCategory(key);
		}
		category.onchange((key, val) -> {
			for(Scrollable scr : containers.values()) scr.hide();
			containers.get(val).show();
		});
		category.selectEntry(0);
		containers.get(category.getSelVal()).show();
	}

	public void fillCategory(String key){
		Scrollable con = new Scrollable(true, 80);
		containers.put(key, con);
		add(con.pos(0, 90));
		con.updateSize(w, h - 30);
		for(Map.Entry<String, Setting<?>> entry : Settings.SETTINGS.get(key).entrySet()){
			con.add(new SettingBlock(), key, entry.getKey(), entry.getValue());
		}
		con.updateBar();
		con.hide();
	}

	@Override
	public Element show(){
		pos((FMT.SCALED_WIDTH - WIDTH) * 0.5f, (FMT.SCALED_HEIGHT - HEIGHT) * 0.5f);
		return super.show();
	}

	public static class SettingBlock extends Element {

		public SettingBlock(){
			super();
		}

		@Override
		public void init(Object... args){
			size(WIDTH - 30, 30);
			border(col_85);
			checkpickpos = false;
			checkinroot = true;
			Setting<?> setting = (Setting<?>)args[2];
			add(new TextElm(0, 0, 300, "setting." + args[0] + "." + args[1]).text_autoscale());
		}

	}

}
