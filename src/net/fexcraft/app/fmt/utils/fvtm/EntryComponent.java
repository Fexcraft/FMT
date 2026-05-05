package net.fexcraft.app.fmt.utils.fvtm;

import com.spinyowl.legui.component.*;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.math.Vec3f;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.*;
import static net.fexcraft.app.fmt.utils.fvtm.FVTMConfigEditor.getEV;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EntryComponent extends Component {

	public TextInput[] input = new TextInput[1];
	public boolean minimized = false;
	private static String[] xyz = { "x", "y", "z" };
	private ArrayList<EntryComponent> coms = new ArrayList<>();
	private FVTMConfigEditor editor;
	private EntryComponent root;
	private ConfigEntry entry;
	private JsonValue val;
	private SubKey key;

	public EntryComponent(FVTMConfigEditor confeditor, EntryComponent rootcom, ConfigEntry confentry, SubKey subkey, JsonValue jval){
		editor = confeditor;
		entry = confentry;
		root = rootcom;
		key = subkey;
	}

	private void gensubs(){
		coms.clear();
		removeIf(com -> com instanceof EntryComponent);
		if(entry.type == EntryType.ARRAY && entry.subs != null){
			if(val == null){
				if(root.val.isMap()) root.val.asMap().add(key.key, val = new JsonArray());
				else root.val.asArray().set(key.idx, val = new JsonArray());
			}
			JsonArray arr = val.asArray();
			for(int i = 0; i < arr.size(); i++){
				for(ConfigEntry conf : entry.subs){
					addsub(new EntryComponent(editor, this, conf, new SubKey(i), arr.get(i)));
				}
			}
		}
		else if(entry.type == EntryType.ARRAY_SIMPLE){
			JsonArray arr = val == null || !val.isArray() ? null : val.asArray();
			if(arr == null){
				root.val.asMap().add(key.key, arr = new JsonArray());
				if(val != null) arr.add(val);
				val = arr;
			}
			if(arr != null){
				for(int i = 0; i < arr.size(); i++){
					addsub(new EntryComponent(editor, this, entry.subs.get(0), new SubKey(i), arr.get(i)));
				}
			}
		}
		else if(entry.type == EntryType.OBJECT && entry.subs != null){
			if(val == null) root.val.asMap().add(entry.name, val = new JsonMap());
			JsonMap map = val.asMap();
			for(String key : map.value.keySet()){
				JsonValue v = map.get(key);
				JsonMap sup;
				if(!v.isMap()){
					sup = entry.converter.apply(key, v).asMap();
					map.add(key, sup);
				}
				else sup = v.asMap();
				EntryComponent sub = new EntryComponent(editor, this, OBJ_SUB_ENTRY, new SubKey(key), sup);
				addsub(sub);
				for(ConfigEntry conf : entry.subs){
					sub.addsub(new EntryComponent(editor, sub, conf, conf.key(), getEV(sup, conf)));
				}
			}
		}
		else if(entry.type == EntryType.OBJECT_KEY_VAL){
			if(entry.static_){
				if(val == null){
					if(root.val.isMap()) root.val.asMap().add(entry.name, val = new JsonMap());
				}
				for(ConfigEntry conf : entry.subs){
					addsub(new EntryComponent(editor, this, conf, conf.key(), getEV(val.asMap(), conf)));
				}
			}
			else{
				if(val != null){
					val.asMap().entries().forEach(e -> {
						addsub(new EntryComponent(editor, this, entry.subs.get(0), new SubKey(e.getKey()), e.getValue()));
					});
				}
			}
		}
		if(!entry.type.subtype() && !entry.static_){
			/*add(new Icon(0, 20, 0, 220, 10, "./resources/textures/icons/configeditor/add.png", () -> {
				fillIfMissing();
				if(entry.type == EntryType.ARRAY){
					JsonMap sup = new JsonMap();
					for(ConfigEntry conf : entry.subs){
						addsub(new EntryComponent(editor, this, conf, new SubKey(val.asArray().size()), sup.get(conf.name)));
					}
					val.asArray().add(sup);
					editor.resize();
				}
				else if(entry.type == EntryType.ARRAY_SIMPLE){
					JsonArray arr = val.asArray();
					arr.add(entry.subs.get(0).gendef());
					addsub(new EntryComponent(editor, this, entry.subs.get(0), new SubKey(arr.size() - 1), arr.get(arr.size() - 1)));
					editor.resize();
				}
				else if(entry.type == EntryType.OBJECT){
					JsonMap map = val.asMap();
					JsonMap sup = new JsonMap();
					String nkey = "entry" + map.entries().size();
					map.add(nkey, sup);
					EntryComponent sub = new EntryComponent(editor, this, OBJ_SUB_ENTRY, new SubKey(nkey), sup);
					addsub(sub);
					for(ConfigEntry conf : entry.subs){
						sub.addsub(new EntryComponent(editor, sub, conf, conf.key(), getEV(sup, conf)));
					}
					editor.resize();
				}
				else if(entry.type == EntryType.OBJECT_KEY_VAL){
					JsonMap map = val.asMap();
					String nkey = null;
					if(entry.subs.get(0).type.separate()){
						for(String str : entry.subs.get(0).enums){
							if(!map.has(str)){
								nkey = str;
								break;
							}
						}
					}
					else nkey = "entry" + map.entries().size();
					if(nkey != null){
						map.add(nkey, entry.subs.isEmpty() || entry.subs.get(0).type.separate() ? new JsonMap() : entry.subs.get(0).gendef());
						addsub(new EntryComponent(editor, this, entry.subs.get(0), new SubKey(nkey), map.get(nkey)));
						editor.resize();
					}
				}
			}));*/
		}
		editor.resize();
	}

	private void addsub(EntryComponent com){
		coms.add(com);
		add(com);
	}

	public int gen(int height){
		setPosition(height == 0 ? 0 : 30, height == 0 ? FVTMConfigEditor.height_ : height + 5);
		int h = 30;
		if(!minimized){
			for(EntryComponent sub : coms){
				h += sub.gen(h);
			}
		}
		h += 10;
		setSize(FVTMConfigEditor.pwidth, h);
		return h;
	}

	public int fullheight(){
		int h = 40;
		for(EntryComponent sub : coms) h += sub.fullheight();
		return h;
	}

}
