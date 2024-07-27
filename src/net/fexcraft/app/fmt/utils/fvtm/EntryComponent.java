package net.fexcraft.app.fmt.utils.fvtm;

import com.spinyowl.legui.component.*;
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
		if(jval == null && entry.type.subs()){
			jval = entry.gendef();
		}
		val = jval;
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
			if(edit){
				add(new Icon(0, 20, 0, 525, 10, "./resources/textures/icons/configeditor/rename.png", () -> {
					Dialog dialog = new Dialog("Enter new name.", 440, 110);
					TextField field = new TextField(key.key, 10, 10, 420, 30, true);
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

	private void addsel(){
		add(new Icon(0, 20, 0, 525, 10, "./resources/textures/icons/configeditor/select.png", () -> {
			switch(entry.type){
				case PACKID: {
					Dialog dialog = new Dialog("Select a pack.", 440, 70);
					SelectBox<String> box = new SelectBox<>(10, 10, 420, 30);
					box.setVisibleCount(8);
					for(FvtmPack pack : WorkspaceViewer.viewer.rootfolders) box.addElement(pack.id);
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
					for(FvtmPack pack : WorkspaceViewer.viewer.rootfolders){
						packbox.addElement(pack.id);
					}
					packbox.addSelectBoxChangeSelectionEventListener(lis -> {
						while(modbox.getElements().size() > 0) modbox.removeElement(0);
						modbox.addElement("(no model)");
						for(FvtmPack pack : WorkspaceViewer.viewer.rootfolders){
							if(!pack.id.equals(lis.getNewValue())) continue;
							for(DirComponent com : pack.models){
								String path = com.file.getPath();
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
					Dialog dialog = new Dialog("Select a Vector.", 440, 110);
					SelectBox<String> vecbox = new SelectBox<>(10, 50, 420, 30);
					SelectBox<String> typebox = new SelectBox<>(10, 10, 420, 30);
					typebox.setVisibleCount(8);
					typebox.setSelected("(please select a type)", true);
					typebox.addElement("marker");
					typebox.addElement("pivot");
					typebox.addSelectBoxChangeSelectionEventListener(lis -> {
						while(vecbox.getElements().size() > 0) vecbox.removeElement(0);
						vecbox.addElement("null");
						if(lis.getNewValue().equals("marker")){
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
					});
					dialog.getContainer().add(typebox);
					//
					vecbox.setVisibleCount(8);
					vecbox.addElement("(select a type)");
					vecbox.addSelectBoxChangeSelectionEventListener(lis -> {
						String val = lis.getNewValue();
						if(val.equals("select a type")) return;
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
						this.val.asArray().set(0, new JsonValue<>(vec.x));
						this.val.asArray().set(1, new JsonValue<>(vec.y));
						this.val.asArray().set(2, new JsonValue<>(vec.z));
						dialog.close();
					});
					dialog.getContainer().add(vecbox);
					dialog.setResizable(false);
					dialog.show(FMT.FRAME);
					break;
				}
			}
		}).addTooltip("select"));
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
					if(val == null){
						if(root.val.isMap()) root.val.asMap().add(key.key, val = entry.gendef());
						else root.val.asArray().set(key.idx, val = entry.gendef());
					}
					else if(val.asArray().empty()){
						val.asArray().add(0);
						val.asArray().add(0);
						val.asArray().add(0);
					}
					add(input[i] = new TextInput(val.asArray().get(i).string_value(), 220 + (i * 100), 7, 90, 26));
					input[i].addTextInputContentChangeEventListener(event -> {
						val.asArray().set(j, new JsonValue<>(get(event, entry.type)));
					});
				}
			}
		}
		else if(entry.type.color()){
			add(new ColorField(this, (color, bool) -> {
				String col = "#" + Integer.toHexString(color);
				if(val == null){
					if(root.val.isMap()) root.val.asMap().add(key.key, col);
					else root.val.asArray().value.set(key.idx, new JsonValue(col));
				}
				else val.value(col);
			}, 220, 7, 300, 26, null, false).apply(val == null ? entry.defi : Integer.parseInt(val.string_value().replace("#", ""), 16)));
		}
		else if(entry.type.bool()){
			add(new BoolButton(220, 7, 300, 26, val == null ? entry.defb : val.bool(), bool -> {
				if(val == null){
					if(root.val.isMap()) root.val.asMap().add(key.key, bool);
					else root.val.asArray().value.set(key.idx, new JsonValue<Boolean>(bool));
				}
				else val.value(bool);
			}));
		}
		else if(entry.type == EntryType.ENUM){
			SelectBox<String> box = new SelectBox<>(220, 7, 300, 26);
			box.setVisibleCount(8);
			for(String en : entry.enums) box.addElement(en);
			box.addSelectBoxChangeSelectionEventListener(lis -> {
				if(val == null){
					if(root.val.isMap()){
						root.val.asMap().add(key.key, new JsonValue<>(lis.getNewValue()));
					}
					else root.val.asArray().value.set(key.idx, new JsonValue<>(lis.getNewValue()));
				}
				else val.value(lis.getNewValue());
			});
			if(val != null) box.setSelected(val.string_value(), true);
			add(box);
		}
		else{//text
			add(input[0] = new TextInput(val == null ? entry.gendef().string_value() : val.string_value(), 220, 7, 300, 26));
			input[0].addTextInputContentChangeEventListener(event -> {
				if(val == null){
					if(notDefault(event, entry)){
						Object o = get(event, entry.type);
						if(root.val.isMap()){
							if((o.equals("null") || o.equals("")) && entry.type == EntryType.TEXT) root.val.asMap().rem(key.key);
							else root.val.asMap().add(key.key, new JsonValue<>(o));
						}
						else root.val.asArray().value.set(key.idx, new JsonValue<>(o));
					}
				}
				else val.value(get(event, entry.type));
			});
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
			if(arr == null && val != null){
				root.val.asMap().add(key.key, arr = new JsonArray());
				arr.add(val);
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
					sup = entry.converter.apply(v).asMap();
					map.add(key, sup);
				}
				else sup = v.asMap();
				EntryComponent sub = new EntryComponent(editor, this, OBJ_SUB_ENTRY, new SubKey(key), null);
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
					EntryComponent sub = new EntryComponent(editor, this, OBJ_SUB_ENTRY, new SubKey(nkey), null);
					addsub(sub);
					for(ConfigEntry conf : entry.subs){
						sub.addsub(new EntryComponent(editor, sub, conf, conf.key(), sup.get(conf.name)));
					}
					editor.resize();
				}
				else if(entry.type == EntryType.OBJECT_KEY_VAL){
					JsonMap map = val.asMap();
					String nkey = "entry" + map.entries().size();
					map.add(nkey, entry.subs.get(0).gendef());
					addsub(new EntryComponent(editor, this, entry.subs.get(0), new SubKey(nkey), map.get(nkey)));
					editor.resize();
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
			if(entry.type == EntryType.INTEGER){
				int i = Integer.parseInt(event.getNewValue());
				if(i == entry.defi) return false;
			}
			else{
				float f = Float.parseFloat(event.getNewValue());
				if(f == entry.deff) return false;
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
}
