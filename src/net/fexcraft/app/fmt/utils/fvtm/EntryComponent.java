package net.fexcraft.app.fmt.utils.fvtm;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;
import com.spinyowl.legui.component.TextInput;
import com.spinyowl.legui.component.event.textinput.TextInputContentChangeEvent;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonValue;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EntryComponent extends Component {

	private Label label;
	public TextInput input;
	private static String[] xyz = { "x", "y", "z" };
	private ArrayList<EntryComponent> coms = new ArrayList<>();

	public EntryComponent(ConfigEntry entry, JsonValue root, Object idxkey, JsonValue obj, Boolean container){
		add(label = new Label((entry.name == null ? idxkey : entry.name) + (entry.required ? "*" : ""), 30, 0, 200, 30));
		if(container != null){
			if(container){
				add(input = new TextInput(idxkey.toString(), 220, 2, 300, 26));
				add(new Icon(0, 20, 0, 530, 5, "./resources/textures/icons/configeditor/confirm.png", () -> {

				}).addTooltip("rename"));
			}
			return;
		}
		add(new Icon(0, 20, 0, 5, 5, "./resources/textures/icons/configeditor/" + entry.type.icon() + ".png", () -> {
		}).addTooltip(entry.type.icon()));
		if(entry.type.subs()){
			if(entry.type == EntryType.ARRAY){
				if(obj != null){
					JsonArray arr = obj.isArray() ? obj.asArray() : new JsonArray();
					for(int i = 0; i < arr.size(); i++){
						EntryComponent con = new EntryComponent(ConfigEntry.EMPTY, arr, i + "", null, false);
						//
						addsub(con);
					}
				}
			}
			else if(entry.type == EntryType.ARRAY_SIMPLE){
				if(obj != null){
					JsonArray arr = obj.isArray() ? obj.asArray() : null;
					if(arr == null){
						root.asMap().add(idxkey.toString(), arr = new JsonArray());
						arr.add(obj);
					}
					for(int i = 0; i < arr.size(); i++){
						if(entry.subs == null) addsub(new EntryComponent(ConfigEntry.TEXT, arr, i, arr.get(i), null));
						else addsub(new EntryComponent(entry.subs.get(0), arr, i, arr.get(i), null));
					}
				}
			}
			else if(entry.type == EntryType.OBJECT){
				if(obj != null){
					obj.asMap().entries().forEach(e -> {
						EntryComponent con = new EntryComponent(ConfigEntry.EMPTY, obj.asMap(), e.getKey(), null, true);
						//
						addsub(con);
					});
				}
			}
			else if(entry.type == EntryType.OBJECT_KEY_VAL){
				if(obj != null){
					obj.asMap().entries().forEach(e -> {
						addsub(new EntryComponent(entry.subs.get(0), obj.asMap(), e.getKey(), e.getValue(), null));
					});
				}
			}
			add(new Icon(0, 20, 0, 220, 5, "./resources/textures/icons/configeditor/add.png", () -> {
				if(entry.type == EntryType.ARRAY){
					//
				}
				else if(entry.type == EntryType.ARRAY_SIMPLE){
					//
				}
				else if(entry.type == EntryType.OBJECT){
					//
				}
				else if(entry.type == EntryType.OBJECT_KEY_VAL){
					//
				}
			}));
		}
		else if(entry.type.trio()){
			Object ik = idxkey;
			for(int i = 0; i < 3; i++){
				add(input = new TextInput(obj == null ? entry.def : obj.string_value(), 220 + (i * 100), 2, 90, 26));
				if(root.isMap()){
					ik = idxkey == null ? xyz[i] : idxkey.toString() + "_" + xyz[i];
				}
				int j = i;
				Object fik = ik;
				input.addTextInputContentChangeEventListener(event -> {
					if(obj == null){
						if(root.isMap()){
							root.asMap().add(fik.toString(), new JsonValue<>(get(event, entry.type)));
						}
						else root.asArray().value.set(((int)fik) + j, new JsonValue<>(get(event, entry.type)));
					}
					else obj.value(get(event, entry.type));
				});
			}
		}
		else if(entry.type.color()){
			add(new ColorField(this, (color, bool) -> {
				String col = "#" + Integer.toHexString(color);
				if(obj == null){
					if(root.isMap()) root.asMap().add(idxkey.toString(), col);
					else root.asArray().value.set((int)idxkey, new JsonValue<String>(col));
				}
				else obj.value(col);
			}, 220, 2, 300, 26, null, false).apply(obj == null ? entry.defi : Integer.parseInt(obj.string_value().replace("#", ""), 16)));
		}
		else if(entry.type.bool()){
			add(new BoolButton(220, 2, 300, 26, obj == null ? entry.defb : obj.bool(), bool -> {
				if(obj == null){
					if(root.isMap()) root.asMap().add(idxkey.toString(), bool);
					else root.asArray().value.set((int)idxkey, new JsonValue<Boolean>(bool));
				}
				else obj.value(bool);
			}));
		}
		else if(entry.type == EntryType.ENUM){
			SelectBox<String> box = new SelectBox<>(220, 2, 300, 26);
			box.setVisibleCount(8);
			for(String en : entry.enums) box.addElement(en);
			box.addSelectBoxChangeSelectionEventListener(lis -> {
				if(obj == null){
					if(root.isMap()){
						root.asMap().add(idxkey.toString(), new JsonValue<>(lis.getNewValue()));
					}
					else root.asArray().value.set((int)idxkey, new JsonValue<>(lis.getNewValue()));
				}
				else obj.value(lis.getNewValue());
			});
			if(obj != null) box.setSelected(obj.string_value(), true);
			add(box);
		}
		else{//text
			add(input = new TextInput(obj == null ? entry.typedef() : obj.string_value(), 220, 2, 300, 26));
			input.addTextInputContentChangeEventListener(event -> {
				if(obj == null){
					if(root.isMap()){
						root.asMap().add(idxkey.toString(), new JsonValue<>(get(event, entry.type)));
					}
					else root.asArray().value.set((int)idxkey, new JsonValue<>(get(event, entry.type)));
				}
				else obj.value(get(event, entry.type));
			});
		}
	}

	private Object get(TextInputContentChangeEvent event, EntryType type){
		if(type.numer()){
			return type == EntryType.INTEGER ? Integer.parseInt(event.getNewValue()) : Float.parseFloat(event.getNewValue());
		}
		//TODO validation for special types
		return event.getNewValue();
	}

	private void addsub(EntryComponent com){
		coms.add(com);
		add(com);
	}

	public int gen(int height){
		setPosition(height == 0 ? 0 : 30, height == 0 ? FVTMConfigEditor.height_ : height);
		int h = label == null ? 0 : 30;
		for(EntryComponent sub : coms){
			h += sub.gen(h);
		}
		if(h > (label == null ? 0 : 30)) h += 10;
		setSize(FVTMConfigEditor.pwidth, h);
		return h;
	}
}
