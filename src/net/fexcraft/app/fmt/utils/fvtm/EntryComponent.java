package net.fexcraft.app.fmt.utils.fvtm;

import com.spinyowl.legui.component.*;
import com.spinyowl.legui.component.event.selectbox.SelectBoxChangeSelectionEvent;
import com.spinyowl.legui.component.event.textinput.TextInputContentChangeEvent;
import com.spinyowl.legui.event.MouseClickEvent;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.ui.workspace.DirComponent;
import net.fexcraft.app.fmt.ui.workspace.FvtmPack;
import net.fexcraft.app.fmt.ui.workspace.WorkspaceViewer;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.math.Vec3f;

import java.util.ArrayList;

import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static com.spinyowl.legui.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.*;
import static net.fexcraft.app.fmt.utils.fvtm.FVTMConfigEditor.getEV;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EntryComponent extends Component {

	private Label label;
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
		val = jval == null ? entry.gendef() : jval;
		add(label = new Label((entry.name == null ? key : entry.name) + (entry.required ? "*" : ""), 30, 5, 200, 30));
		label.getListenerMap().addListener(MouseClickEvent.class, event -> {
			if(event.getAction() == CLICK && event.getButton() == MOUSE_BUTTON_LEFT){
				minimized = !minimized;
				editor.resize();
			}
		});
		add(new Icon(0, 20, 0, 5, 10, "./resources/textures/icons/configeditor/" + entry.type.icon() + ".png", () -> {
			minimized = !minimized;
			editor.resize();
		}).addTooltip(entry.type.icon()));
		if(root != null){
			boolean edit = root.entry.type.map() && !root.entry.type.subtype() && !root.entry.static_;
			if(!entry.type.separate() && !entry.type.static_()){
				add(new Icon(0, 20, 0, 525 + (entry.type.select() || edit ? 25 : 0), 10, "./resources/textures/icons/configeditor/remove.png", () -> {
					if(root.entry.type.subs() && !root.entry.type.subtype()){
						if(root.val.isMap()) root.val.asMap().rem(key.key);
						else root.val.asArray().rem(key.idx);
						root.gensubs();
					}
					else{
						if(entry.type.vector()){
							input[0].getTextState().setText(0 + "");
							input[1].getTextState().setText(0 + "");
							input[2].getTextState().setText(0 + "");
						}
						else if(input[0] != null) input[0].getTextState().setText(entry.gendef().string_value());
					}
				}).addTooltip("remove/reset"));
			}
			if(edit){
				add(new Icon(0, 20, 0, 525, 10, "./resources/textures/icons/configeditor/rename.png", () -> {
					Dialog dialog = new Dialog("Enter new name.", 440, 110);
					TextField field = new TextField(key.key, 10, 10, 420, 30, false);
					dialog.getContainer().add(field);
					dialog.getContainer().add(new RunButton("dialog.button.confirm", 10, 50, 200, 30, () -> {
						if(root.val.isMap() && root.entry.type.subs() && !root.entry.type.subtype()){
							JsonValue value = root.val.asMap().get(key.key);
							root.val.asMap().rem(key.key);
							root.val.asMap().add(field.getTextState().getText(), value);
							root.gensubs();
						}
						else{
							val = new JsonValue(input[0].getTextState().getText());
							updateVal();
						}
						dialog.close();
					}));
					dialog.setResizable(false);
					dialog.show(FMT.FRAME);
				}).addTooltip("rename key"));
			}
		}
		if(entry.type.select()) addsel();
		if(entry.type.subs()){
			if(!entry.type.subtype()) gensubs();
		}
		else geninput();
	}

	private void updateVal(){
		if(root.val.isMap()){
			root.val.asMap().add(key.key, val);
		}
		else{
			root.val.asArray().rem(val);
			root.val.asArray().add(val);
		}
		if(root.entry.type.subs()) root.gensubs();
	}

	private void fillIfMissing(){
		fillIfMissing(true);
	}

	private void fillIfMissing(boolean check){
		if(root == null) return;
		if(check && root != null) root.fillIfMissing(false);
		if(root.val.isMap()){
			if(!root.val.asMap().has(key.key)){
				root.val.asMap().add(key.key, val);
			}
		}
		else{
			if(!root.val.asArray().contains(val)){
				root.val.asArray().add(val);
			}
		}
	}

	private void addsel(){
		add(new Icon(0, 20, 0, 525, 10, "./resources/textures/icons/configeditor/select.png", () -> {
			switch(entry.type){
				case PACKID: {
					Dialog dialog = new Dialog("Select a pack.", 440, 70);
					SelectBox<String> box = new SelectBox<>(10, 10, 420, 30);
					box.setVisibleCount(8);
					for(FvtmPack pack : WorkspaceViewer.viewer().rootfolders) box.addElement(pack.id);
					box.addSelectBoxChangeSelectionEventListener(lis -> {
						input[0].getTextState().setText(lis.getNewValue());
						val.value(lis.getNewValue());
						dialog.close();
					});
					dialog.getContainer().add(box);
					dialog.setResizable(false);
					dialog.show(FMT.FRAME);
					break;
				}
				case MODELLOC:{
					Dialog dialog = new Dialog("Select a Model.", 440, 110);
					SelectBox<String> packbox = new SelectBox<>(10, 10, 420, 30);
					SelectBox<String> modbox = new SelectBox<>(10, 50, 420, 30);
					packbox.setVisibleCount(8);
					packbox.setSelected("(no pack selected)", true);
					for(FvtmPack pack : WorkspaceViewer.viewer().rootfolders){
						packbox.addElement(pack.id);
					}
					packbox.addSelectBoxChangeSelectionEventListener(lis -> {
						while(modbox.getElements().size() > 0) modbox.removeElement(0);
						modbox.addElement("(no model)");
						for(FvtmPack pack : WorkspaceViewer.viewer().rootfolders){
							if(!pack.id.equals(lis.getNewValue())) continue;
							for(DirComponent com : pack.models){
								String path = com.file.getPath().replace("\\", "/");
								if(!path.contains("/models")){
									Logging.log("invalid model path: " + path);
									continue;
								}
								String pid = path.substring(path.indexOf("/assets/") + 8, path.indexOf("/models"));
								path = path.substring(path.indexOf("/models/") + 1);
								modbox.addElement(pid + ":" + path);
							}
						}
					});
					dialog.getContainer().add(packbox);
					//
					modbox.setVisibleCount(8);
					modbox.addElement("(select a pack)");
					modbox.addSelectBoxChangeSelectionEventListener(lis -> {
						String val = lis.getNewValue();
						if(val.equals("select a pack")) return;
						if(val.equals("(no model)")) val = "null";
						input[0].getTextState().setText(val);
						fillIfMissing();
						this.val.value(val);
						dialog.close();
					});
					dialog.getContainer().add(modbox);
					dialog.setResizable(false);
					dialog.show(FMT.FRAME);
					break;
				}
				case TEXLOC:{
					Dialog dialog = new Dialog("Select a Texture.", 440, 110);
					SelectBox<String> texbox = new SelectBox<>(10, 50, 420, 30);
					SelectBox<String> packbox = new SelectBox<>(10, 10, 420, 30);
					packbox.setVisibleCount(8);
					packbox.setSelected("(no pack selected)", true);
					for(FvtmPack pack : WorkspaceViewer.viewer().rootfolders){
						packbox.addElement(pack.id);
					}
					packbox.addSelectBoxChangeSelectionEventListener(lis -> {
						while(texbox.getElements().size() > 0) texbox.removeElement(0);
						texbox.addElement("(no texture)");
						for(FvtmPack pack : WorkspaceViewer.viewer().rootfolders){
							if(!pack.id.equals(lis.getNewValue())) continue;
							for(DirComponent com : pack.textures){
								String path = com.file.getPath().replace("\\", "/");
								if(!path.contains("/textures")){
									Logging.log("invalid texture path: " + path);
									continue;
								}
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
						String prefix = this.val.string_value();
						if(prefix.contains(";")) prefix = prefix.split(";")[0];
						else prefix = null;
						val = prefix + ";" + val;
						input[0].getTextState().setText(val);
						this.val.value(val);
						dialog.close();
					});
					dialog.getContainer().add(texbox);
					dialog.setResizable(false);
					dialog.show(FMT.FRAME);
					break;
				}
				case VECTOR_MAP:
				case VECTOR_ARRAY: {
					int width = 440;
					Dialog dialog = new Dialog("Select a Vector.", width, 140);
					SelectBox<String> vecbox = new SelectBox<>(10, 50, width - 80, 30);
					SelectBox<String> typebox = new SelectBox<>(10, 10, width - 80, 30);
					typebox.setVisibleCount(8);
					typebox.addElement("marker");
					typebox.addElement("pivot");
					typebox.setSelected("marker", true);
					typebox.addSelectBoxChangeSelectionEventListener(lis -> updateVecBox(lis.getNewValue(), vecbox));
					dialog.getContainer().add(new Icon(20,width - 60, 15, typebox, true, () -> updateVecBox(typebox.getSelection(), vecbox)));
					dialog.getContainer().add(new Icon(20,width - 30, 15, typebox, false, () -> updateVecBox(typebox.getSelection(), vecbox)));
					dialog.getContainer().add(typebox);
					//
					vecbox.setVisibleCount(8);
					vecbox.addSelectBoxChangeSelectionEventListener(lis -> confirmVecBox(lis.getNewValue(), typebox, vecbox, dialog));
					updateVecBox("marker", vecbox);
					dialog.getContainer().add(vecbox);
					dialog.getContainer().add(new Icon(20, width - 60, 55, vecbox, true, null));
					dialog.getContainer().add(new Icon(20, width - 30, 55, vecbox, false, null));
					dialog.getContainer().add(new RunButton("dialog.button.select", width - 110, 90, 100, 20, () -> confirmVecBox(vecbox.getSelection(), typebox, vecbox, dialog)));
					dialog.setResizable(false);
					dialog.show(FMT.FRAME);
					break;
				}
				case ENUM_SEPARATE:
				case SEPARATE:{
					editor.save();
					new FVTMConfigEditor(editor, editor.file, root.entry.type.map() ? root.entry.name : entry.name, key.key, entry, val);
					break;
				}
			}
		}).addTooltip("select"));
	}

	private void confirmVecBox(String val, SelectBox<String> typebox, SelectBox<String> vecbox, Dialog dialog){
		if(val.equals("null")){
			input[0].getTextState().setText("0");
			input[1].getTextState().setText("0");
			input[2].getTextState().setText("0");
			this.val.asArray().set(0, new JsonValue<>(0f));
			this.val.asArray().set(1, new JsonValue<>(0f));
			this.val.asArray().set(2, new JsonValue<>(0f));
			dialog.close();
			return;
		}
		String type = typebox.getSelection();
		Vec3f vec = new Vec3f();
		if(type.equals("marker")){
			String[] str = val.split("/");
			Group group = FMT.MODEL.get(str[0]);
			Polygon poly = group.get(str[1]);
			if(FMT.MODEL.orient.rect()){
				vec.x = poly.pos.x * .0625f;
				vec.y = poly.pos.y * .0625f;
				vec.z = poly.pos.z * .0625f;
			}
			else{
				vec.x = -poly.pos.z * .0625f;
				vec.y = -poly.pos.y * .0625f;
				vec.z = -poly.pos.x * .0625f;
			}
		}
		else{
			Pivot pivot = FMT.MODEL.getP(val.split("/")[1]);
			if(FMT.MODEL.orient.rect()){
				vec.x = pivot.pos.x * .0625f;
				vec.y = pivot.pos.y * .0625f;
				vec.z = pivot.pos.z * .0625f;
			}
			else{
				vec.x = -pivot.pos.z * .0625f;
				vec.y = -pivot.pos.y * .0625f;
				vec.z = -pivot.pos.x * .0625f;
			}
		}
		input[0].getTextState().setText(vec.x + "");
		input[1].getTextState().setText(vec.y + "");
		input[2].getTextState().setText(vec.z + "");
		fillIfMissing();
		this.val.asArray().set(0, new JsonValue<>(vec.x));
		this.val.asArray().set(1, new JsonValue<>(vec.y));
		this.val.asArray().set(2, new JsonValue<>(vec.z));
		dialog.close();
	}

	private void updateVecBox(String val, SelectBox<String> vecbox){
		while(vecbox.getElements().size() > 0) vecbox.removeElement(0);
		vecbox.addElement("null");
		if(val.equals("marker")){
			for(Group group : FMT.MODEL.allgroups()){
				for(Polygon poly : group){
					if(!poly.getShape().isMarker()) continue;
					if(poly.name(true) == null) continue;
					vecbox.addElement(group.id + "/" + poly.name());
				}
			}
		}
		else{
			for(Pivot pivot : FMT.MODEL.pivots()){
				vecbox.addElement(pivot.parentid + "/" + pivot.id);
			}
		}
	}

	private void geninput(){
		if(entry.type.vector()){
			input = new TextInput[3];
			Object ik = key;
			if(val == null){
				val = new JsonArray.Flat();
				if(root.val.isMap()){
					root.val.asMap().add(key.key, val);
				}
				else root.val.asArray().value.set(key.idx, val);
			}
			for(int i = 0; i < 3; i++){
				int j = i;
				if(entry.type == EntryType.VECTOR_MAP){
					if(root.val.isMap()){
						ik = key == null ? xyz[i] : key + "_" + xyz[i];
					}
					Object fik = ik;
					input[i].addTextInputContentChangeEventListener(event -> {
						if(val == null){
							if(root.val.isMap()){
								root.val.asMap().add(fik.toString(), new JsonValue<>(get(event, entry.type)));
							}
							else root.val.asArray().value.set(((int)fik) + j, new JsonValue<>(get(event, entry.type)));
						}
						else val.value(get(event, entry.type));
					});
				}
				else{
					while(val.asArray().size() < 3) val.asArray().add(0);
					add(input[i] = new TextInput(val.asArray().get(i).string_value(), 220 + (i * 100), 7, 90, 26));
					input[i].addTextInputContentChangeEventListener(event -> {
						fillIfMissing();
						val.asArray().set(j, new JsonValue<>(get(event, entry.type)));
					});
				}
			}
		}
		else if(entry.type.color()){
			add(new ColorField(this, (color, bool) -> {
				fillIfMissing();
				val.value("#" + Integer.toHexString(color));
			}, 220, 7, 300, 26, null, false).apply(val == null ? entry.defi : Integer.parseInt(val.string_value().replace("#", ""), 16)));
		}
		else if(entry.type.bool()){
			add(new BoolButton(220, 7, 300, 26, val == null ? entry.defb : val.bool(), bool -> {
				fillIfMissing();
				val.value(bool);
			}));
		}
		else if(entry.type.enumerate()){
			SelectBox<String> box = new SelectBox<>(220, 7, 300, 26);
			box.setVisibleCount(8);
			for(String en : entry.enums) box.addElement(en);
			box.addSelectBoxChangeSelectionEventListener(lis -> {
				fillIfMissing();
				if(entry.type.separate() && val.isMap()){
					if(root.entry.type.map()){
						val = root.val.asMap().rem(key.key);
						root.val.asMap().add(lis.getNewValue(), val);
						root.gensubs();
					}
					else val.asMap().add(entry.subs.get(0).name, lis.getNewValue());
				}
				else val.value(lis.getNewValue());
			});
			if(val != null){
				if(entry.type.separate() && val.isMap()){
					if(root.entry.type.map()){
						box.setSelected(key.key, true);
					}
					else if(val.asMap().has(entry.subs.get(0).name)){
						box.setSelected(val.asMap().get(entry.subs.get(0).name).string_value(), true);
					}
				}
				else box.setSelected(val.string_value(), true);
			}
			add(box);
		}
		else if(!entry.type.separate()){//text
			add(input[0] = new TextInput(val == null ? entry.gendef().string_value() : val.string_value(), 220, 7, 300, 26));
			if(!entry.type.static_()){
				input[0].addTextInputContentChangeEventListener(event -> {
					if(notDefault(event, entry)){
						Object o = get(event, entry.type);
						if(root.val.isMap() && (o.equals("null") || o.equals("")) && entry.type == EntryType.TEXT){
							root.val.asMap().rem(key.key);
							return;
						}
					}
					fillIfMissing();
					val.value(get(event, entry.type));
				});
			}
			else input[0].setEditable(false);
		}
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
			add(new Icon(0, 20, 0, 220, 10, "./resources/textures/icons/configeditor/add.png", () -> {
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
			}));
		}
		editor.resize();
	}

	private Object get(TextInputContentChangeEvent event, EntryType type){
		if(type.numer() || type.vector()){
			try{
				return type == EntryType.INTEGER ? Integer.parseInt(event.getNewValue()) : Float.parseFloat(event.getNewValue());
			}
			catch(Exception e){
				e.printStackTrace();
				return type == EntryType.INTEGER ? 0 : 0f;
			}
		}
		//TODO validation for special types
		return event.getNewValue();
	}

	private boolean notDefault(TextInputContentChangeEvent event, ConfigEntry entry){
		if(entry.type.numer()){
			try{
				if(entry.type == EntryType.INTEGER){
					int i = Integer.parseInt(event.getNewValue());
					if(i == entry.defi) return false;
				}
				else{
					float f = Float.parseFloat(event.getNewValue());
					if(f == entry.deff) return false;
				}
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return !event.getNewValue().equals(entry.def);
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
