package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.settings.StringArraySetting;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.math.RGB;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.fexcraft.app.fmt.settings.Settings.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SettingsUI extends Element {

	private static float WIDTH = 650;
	private static float HEIGHT = 500;
	public DropList<String> category;
	public LinkedHashMap<String, Scrollable> containers = new LinkedHashMap<>();
	public Runnable run;
	private Element icon;

	public SettingsUI(){
		super();
		pos((FMT.SCALED_WIDTH - WIDTH) * 0.5f, (FMT.SCALED_HEIGHT - HEIGHT) * 0.5f);
		size(WIDTH, HEIGHT + 30);
		z += 200;
		color(GENERIC_BACKGROUND_0.value);
		border(RGB.BLACK);
	}

	@Override
	public void init(Object... args){
		add(new Element().size(w - 31, 30).color(GENERIC_BACKGROUND_1.value)
			.translate("settings.dialog.title").text_autoscale());
		add(icon = new Element().size(30, 30).pos(w - 31, 0).texture("icons/component/remove")
			.hoverable(true).onclick(ci -> {
				Element.select(null);
				Settings.refresh();
				hide();
				if(run != null){
					run.run();
					run = null;
				}
			}));
		add((category = new DropList<>(w - 10)).pos(5, 40));
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
		con.updateSize(w - 1, h - 30);
		for(Map.Entry<String, Setting<?>> entry : Settings.SETTINGS.get(key).entrySet()){
			con.add(new SettingBlock(), key, entry.getKey(), entry.getValue());
		}
		con.updateBar();
		con.hide();
	}

	@Override
	public Element show(){
		pos((FMT.SCALED_WIDTH - WIDTH) * 0.5f, (FMT.SCALED_HEIGHT - HEIGHT) * 0.5f);
		icon.texture("icons/component/remove");
		return super.show();
	}

	public void show(String tidx, Runnable r){
		show();
		run = r;
		icon.texture("icons/component/move_right");
		category.selectValue(tidx);
		for(Scrollable scr : containers.values()) scr.hide();
		containers.get(tidx).show();
	}

	public static class SettingBlock extends Element {

		public SettingBlock(){
			super();
		}

		@Override
		public void init(Object... args){
			size(WIDTH - 30, 30);
			border(GENERIC_BACKGROUND_2.value);
			check_mode(CheckMode.IN_ROOT);
			Setting<?> setting = (Setting<?>)args[2];
			add(new TextElm(0, 0, 310, "setting." + args[0] + "." + args[1])
				.text_autoscale().hint(args[0] + " / " + args[1]));
			if(setting.value instanceof Boolean){
				add(new BoolElm(315, 2, 300).set(() -> setting.bool(), bool -> {
					setting.value(bool);
					setting.refresh();
				}));
			}
			if(setting.value instanceof RGB rgb){
				Field field = new Field(Field.FieldType.COLOR, 300).consumer(fld -> {
					rgb.packed = (int)fld.parse_int();
					setting.refresh();
				});
				add(field.pos(315, 2));
				field.text(field.type_format(rgb.packed));
			}
			if(setting.value instanceof String && (!(setting instanceof StringArraySetting))){
				Field field = new Field(Field.FieldType.TEXT, 300).consumer(fld -> {
					setting.value(fld.get_text());
					setting.refresh();
				});
				add(field.pos(315, 2));
				field.text(setting.value.toString());
			}
			if(setting instanceof StringArraySetting sarr){
				DropList<String> list = new DropList<>(300);
				list.onchange((key, val) -> {
					setting.value(val);
					setting.refresh();
				});
				add(list.pos(315, 2));
				for(String val : sarr.vals()) list.addEntry(val, val);
				list.selectValue(setting.value.toString());
			}
			boolean flt = setting.value instanceof Float;
			if(flt || setting.value instanceof Integer){
				Field field = new Field(flt ? Field.FieldType.FLOAT : Field.FieldType.INT, 300).consumer(fld -> {
					if(flt){
						setting.value(fld.parse_float());
					}
					else{
						setting.value(fld.parse_int());
					}
					setting.refresh();
				}).range(
					setting.min == null ? Integer.MIN_VALUE : ((Number)setting.min).floatValue(),
					setting.max == null ? Integer.MAX_VALUE : ((Number)setting.max).floatValue()
				);
				add(field.pos(315, 2));
				field.text(setting.value.toString());
			}
		}

	}

}
