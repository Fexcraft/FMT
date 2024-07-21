package net.fexcraft.app.fmt.utils.fvtm;

import com.spinyowl.legui.component.*;
import com.spinyowl.legui.component.event.textinput.TextInputContentChangeEvent;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.workspace.DirComponent;
import net.fexcraft.app.fmt.ui.workspace.FvtmPack;
import net.fexcraft.app.fmt.ui.workspace.WorkspaceViewer;
import net.fexcraft.app.fmt.utils.Logging;
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
	private FVTMConfigEditor editor;
	private EntryComponent rcom;
	private ConfigEntry entry;
	private JsonValue root;
	private JsonValue obj;
	private Object key;

	public EntryComponent(FVTMConfigEditor confeditor, EntryComponent rootcom, ConfigEntry entry, JsonValue root, Object idxkey, JsonValue obj, Boolean container){
		editor = confeditor;
		this.entry = entry;
		this.root = root;
		this.obj = obj;
		rcom = rootcom;
		key = idxkey;
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
		if(rcom != null){
			add(new Icon(0, 20, 0, 525 + (entry.type.select() ? 25 : 0), 5, "./resources/textures/icons/configeditor/remove.png", () -> {
				if(rcom.entry.type.subs()){
					if(root.isMap()) root.asMap().rem((String)idxkey);
					else root.asArray().rem((int)idxkey);
					rcom.gensubs();
				}
				else{
					input.getTextState().setText(entry.gendef().string_value());
				}
			}).addTooltip("remove/reset"));
		}
		if(entry.type.select()){
			add(new Icon(0, 20, 0, 525, 5, "./resources/textures/icons/configeditor/select.png", () -> {
				switch(entry.type){
					case PACKID: {
						Dialog dialog = new Dialog("Select a pack.", 240, 70);
						SelectBox<String> box = new SelectBox<>(10, 10, 220, 30);
						box.setVisibleCount(8);
						for(FvtmPack pack : WorkspaceViewer.viewer.rootfolders) box.addElement(pack.id);
						box.addSelectBoxChangeSelectionEventListener(lis -> {
							input.getTextState().setText(lis.getNewValue());
							obj.value(lis.getNewValue());
							dialog.close();
						});
						dialog.getContainer().add(box);
						dialog.setResizable(false);
						dialog.show(FMT.FRAME);
						break;
					}
					case MODELLOC:{
						Dialog dialog = new Dialog("Select a Model.", 240, 70);
						SelectBox<String> box = new SelectBox<>(10, 10, 220, 30);
						box.setVisibleCount(8);
						box.addElement("(no model)");
						for(FvtmPack pack : WorkspaceViewer.viewer.rootfolders){
							box.addElement(pack.id);
							//
						}
						box.addSelectBoxChangeSelectionEventListener(lis -> {
							String val = lis.getNewValue();
							if(val.equals("(no model)")) val = "null";
							input.getTextState().setText(val);
							obj.value(val);
							dialog.close();
						});
						dialog.getContainer().add(box);
						dialog.setResizable(false);
						dialog.show(FMT.FRAME);
						break;
					}
					case TEXLOC:{
						Dialog dialog = new Dialog("Select a Texture.", 240, 110);
						SelectBox<String> texbox = new SelectBox<>(10, 50, 220, 30);
						SelectBox<String> packbox = new SelectBox<>(10, 10, 220, 30);
						packbox.setVisibleCount(8);
						packbox.setSelected("(no pack selected)", true);
						for(FvtmPack pack : WorkspaceViewer.viewer.rootfolders){
							packbox.addElement(pack.id);
						}
						packbox.addSelectBoxChangeSelectionEventListener(lis -> {
							while(texbox.getElements().size() > 0) texbox.removeElement(0);
							texbox.addElement("(no texture)");
							for(FvtmPack pack : WorkspaceViewer.viewer.rootfolders){
								if(!pack.id.equals(lis.getNewValue())) continue;
								for(DirComponent com : pack.textures){
									String path = com.file.getPath();
									String pid = path.substring(path.indexOf("/assets/") + 8, path.indexOf("/textures"));
									path = path.substring(path.indexOf("/textures/") + 1);
									texbox.addElement(pid + ":" + path);
								}
							}
						});
						dialog.getContainer().add(packbox);
						//
						texbox.setVisibleCount(8);
						texbox.addElement("(select a pack)");
						texbox.addSelectBoxChangeSelectionEventListener(lis -> {
							String val = lis.getNewValue();
							if(val.equals("select a pack")) return;
							if(val.equals("(no texture)")) val = "fvtm:textures/entity/null.png";
							String prefix = obj.string_value();
							if(prefix.contains(";")) prefix = prefix.split(";")[0];
							else prefix = null;
							val = prefix + ";" + val;
							input.getTextState().setText(val);
							obj.value(val);
							dialog.close();
						});
						dialog.getContainer().add(texbox);
						dialog.setResizable(false);
						dialog.show(FMT.FRAME);
						break;
					}
				}
			}).addTooltip("select"));
		}
		if(entry.type.subs()){
			gensubs();
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
			add(input = new TextInput(obj == null ? entry.gendef().string_value() : obj.string_value(), 220, 2, 300, 26));
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

	private void gensubs(){
		coms.clear();
		removeIf(com -> com instanceof EntryComponent);
		if(entry.type == EntryType.ARRAY){
			if(obj != null){
				JsonArray arr = obj.isArray() ? obj.asArray() : new JsonArray();
				for(int i = 0; i < arr.size(); i++){
					EntryComponent con = new EntryComponent(editor, this, ConfigEntry.EMPTY, arr, i + "", null, false);
					//
					addsub(con);
				}
			}
		}
		else if(entry.type == EntryType.ARRAY_SIMPLE){
			JsonArray arr = obj == null || !obj.isArray() ? null : obj.asArray();
			if(arr == null){
				root.asMap().add(key.toString(), arr = new JsonArray());
				if(obj != null) arr.add(obj);
				obj = arr;
			}
			for(int i = 0; i < arr.size(); i++){
				addsub(new EntryComponent(editor, this, entry.subs.get(0), arr, i, arr.get(i), null));
			}
		}
		else if(entry.type == EntryType.OBJECT){
			if(obj != null){
				obj.asMap().entries().forEach(e -> {
					EntryComponent con = new EntryComponent(editor, this, ConfigEntry.EMPTY, obj.asMap(), e.getKey(), null, true);
					//
					addsub(con);
				});
			}
		}
		else if(entry.type == EntryType.OBJECT_KEY_VAL){
			if(obj != null){
				obj.asMap().entries().forEach(e -> {
					addsub(new EntryComponent(editor, this, entry.subs.get(0), obj.asMap(), e.getKey(), e.getValue(), null));
				});
			}
		}
		add(new Icon(0, 20, 0, 220, 5, "./resources/textures/icons/configeditor/add.png", () -> {
			if(entry.type == EntryType.ARRAY){
				//
			}
			else if(entry.type == EntryType.ARRAY_SIMPLE){
				JsonArray arr = root.asMap().getArray(key.toString());
				arr.add(entry.subs.get(0).gendef());
				addsub(new EntryComponent(editor, this, entry.subs.get(0), arr, arr.size() - 1, arr.get(arr.size() - 1), null));
				editor.resize();
			}
			else if(entry.type == EntryType.OBJECT){
				//
			}
			else if(entry.type == EntryType.OBJECT_KEY_VAL){
				//
			}
		}));
		editor.resize();
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
