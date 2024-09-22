package net.fexcraft.app.fmt.utils.fvtm;

import java.io.File;
import java.util.ArrayList;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.Widget;
import org.joml.Vector2f;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FVTMConfigEditor extends Widget {

	public static ArrayList<FVTMConfigEditor> INSTANCES = new ArrayList<>();

	private EntryComponent root;
	private ScrollablePanel panel;
	private ConfigReference ref;
	private JsonMap rmap;
	private JsonMap map;
	protected File file;
	//
	private FVTMConfigEditor rooteditor;
	private ConfigEntry entry;
	private JsonValue refval;
	private String type;

	public static int width = 700, height = 500, pwidth = 1000;
	protected static int height_;

	public FVTMConfigEditor(File file, String type){
		this(null, file, type, null, null, null);
	}

	public FVTMConfigEditor(FVTMConfigEditor rootedit, File file, String type, String key, ConfigEntry entry, JsonValue val){
		rooteditor = rootedit;
		this.entry = entry;
		refval = val;
		getTitleTextState().setText(Translator.translate("fvtmeditor.title") + " - " + file.getName() + (type == null ? "" : " / " + type));
		setSize(width, height);
		setPosition(FMT.WIDTH / 2 - (width / 2), FMT.HEIGHT / 2 - (height / 2));
		if(type == null){
			String[] dots = file.getName().split("\\.");
			type = dots[dots.length - 1];
		}
		this.type = type;
		this.file = file;
		ref = getReference(type, key, entry, val);
		if(ref == null) return;
		remap();
		Settings.applyComponentTheme(getContainer());
		getContainer().add(panel = new ScrollablePanel(10, 40, width - 20, height - 70));
		getContainer().add(new RunButton("dialog.button.save", width - 220, 10, 100, 24, () -> save()));
		getContainer().add(new RunButton("dialog.button.close", width - 110, 10, 100, 24, () -> {
			FMT.FRAME.getContainer().remove(this);
			INSTANCES.remove(this);
		}));
		getListenerMap().addListener(ChangeSizeEvent.class, event -> {
			Vector2f vec = new Vector2f();
			event.getNewSize().get(vec);
			if(vec.x < width){
				setSize(width, vec.y);
				return;
			}
			if(vec.y < height){
				setSize(vec.x, height);
				return;
			}
			panel.setSize(vec.x - 20, vec.y - 70);
		});
		fill();
		FMT.FRAME.getContainer().add(this);
		INSTANCES.add(this);
		show();
	}

	private void remap(){
		rmap = JsonHandler.parse(file);
		if(entry != null){
			boolean solved = false;
			if(entry.subs != null && entry.subs.size() > 0){
				String name = entry.subs.get(0).name;
				if(rmap.has(name) && !rmap.get(name).isMap()){
					map = new JsonMap();
					map.add(name, rmap.get(name).string_value());
					rmap.add(name, map);
					solved = true;
				}
			}
			if(!solved){
				if(!rmap.has(type) || !rmap.get(type).isMap()) rmap.addMap(type);
				map = rmap.getMap(type);
			}
		}
		else map = rmap;
	}

	protected void save(){
		JsonHandler.print(file, rmap, JsonHandler.PrintOption.DEFAULT);
		if(rooteditor != null) rooteditor.refill();
	}

	private ConfigReference getReference(String type, String key, ConfigEntry entry, JsonValue val){
		type = type.toLowerCase();
		switch(type){
			case "vehicle":
				return VehicleConfigReference.INSTANCE;
			case "part":
				return PartConfigReference.INSTANCE;
			case "material":
				return MaterialConfigReference.INSTANCE;
			case "consumable":
				return ConsumableConfigReference.INSTANCE;
			case "fuel":
				return null;
			case "block":
				return BlockConfigReference.INSTANCE;
			case "wire":
				return null;
			case "wiredeco":
				return null;
			case "deco":
				return DecorationConfigReference.INSTANCE;
			case "railgauge":
				return null;
			case "cloth":
				return null;
			//
			case "modeldata":
				return ModelDataReference.INSTANCE;
			case "installation":{
				String sub = val.isMap() ? val.asMap().getString(entry.subs.get(0).name, entry.enums[0]) : val.string_value();
				switch(sub){
					case "default": return PartInstallConfigReference.DEFAULT;
					case "wheel": return PartInstallConfigReference.WHEEL;
					case "tire": return PartInstallConfigReference.TIRE;
					case "bogie": return PartInstallConfigReference.BOGIE;
				}
				return null;
			}
			case "functions":{
				return PartFunctionConfigReference.REFERENCES.get(key);
			}
			default: return null;
		}
	}

	protected void refill(){
		removeIf(com -> com instanceof EntryComponent);
		remap();
		fill();
	}

	private void fill(){
		root = new EntryComponent(this, null, ConfigEntry.TEXT_ENTRY, null, map);
		for(ConfigEntry entry : ref.entries){
			try{
				panel.getContainer().add(new EntryComponent(this, root, entry, entry.key(), getEV(map, entry)));
			}
			catch(Exception e){
				Logging.log(entry.name + " " + entry.type + " " + entry.key() + " " + map.get(entry.name));
				e.printStackTrace();
			}
		}
		resize();
	}

	public void resize(){
		height_ = 0;
		for(Component com : panel.getContainer().getChildComponents()){
			if(com instanceof EntryComponent == false) continue;
			height_ += ((EntryComponent)com).gen(0);
		}
		height_ = 0;
		for(Component com : panel.getContainer().getChildComponents()){
			if(com instanceof EntryComponent == false) continue;
			height_ += ((EntryComponent)com).fullheight();
		}
		panel.getContainer().setSize(pwidth, height_);

	}

	protected static JsonValue getEV(JsonMap map, ConfigEntry entry){
		if(map.has(entry.name)) return map.get(entry.name);
		if(map.has(entry.alt)) return map.get(entry.alt);
		return null;
	}


}
