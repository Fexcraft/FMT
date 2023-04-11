package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuLayer;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;
import org.joml.Vector2f;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

public class JsonEditorMenu {
	
	private static MenuLayer layer;
	private static JsonEditor jsoneditor;
	private static String idxkey;
	private static JsonObject root, elm;

	static {
		ArrayList<Component> components = new ArrayList<>();
		components.add(new MenuButton(0, "jsoneditormenu.insert", () -> {
			if(elm.isMap() || elm.isArray()){
				pickType(elm.isMap(), (key, nelm) -> {
					if(elm.isMap()){
						elm.asMap().add(key, nelm);
					}
					else{
						elm.asArray().add(nelm);
					}
					jsoneditor.refill();
				});
			}
			layer.hide();
		}));
		components.add(new MenuButton(1, "jsoneditormenu.add_before", () -> {
			if(elm.isMap() || elm.isArray()){
				pickType(elm.isMap(), (key, nelm) -> {
					if(elm.isMap()){
						ArrayList<String> keys = new ArrayList<>();
						keys.addAll(root.asMap().value.keySet());
						int idx = keys.indexOf(idxkey);
						keys.add(idx, key);
						HashMap<String, JsonObject> map = new HashMap();
						map.putAll(root.asMap().value);
						map.put(key, nelm);
						root.asMap().value.clear();
						for(String kkey : keys){
							root.asMap().add(kkey, map.get(kkey));
						}
					}
					else{
						int idx = root.asArray().value.indexOf(elm);
						root.asArray().value.add(idx, nelm);
					}
					jsoneditor.refill();
				});
			}
			layer.hide();
		}));
		components.add(new MenuButton(2, "jsoneditormenu.add_after", () -> {
			if(elm.isMap() || elm.isArray()){
				pickType(elm.isMap(), (key, nelm) -> {
					if(elm.isMap()){
						ArrayList<String> keys = new ArrayList<>();
						keys.addAll(root.asMap().value.keySet());
						int idx = keys.indexOf(idxkey);
						if(idx >= keys.size() - 1) keys.add(key);
						else keys.add(idx + 1, key);
						HashMap<String, JsonObject> map = new HashMap();
						map.putAll(root.asMap().value);
						map.put(key, nelm);
						root.asMap().value.clear();
						for(String kkey : keys){
							root.asMap().add(kkey, map.get(kkey));
						}
					}
					else{
						int idx = root.asArray().value.indexOf(elm);
						if(idx >= root.asArray().size() - 1) root.asArray().add(nelm);
						else root.asArray().value.add(idx + 1, nelm);
					}
					jsoneditor.refill();
				});
			}
			layer.hide();
		}));
		components.add(new MenuButton(3, "jsoneditormenu.rename", () -> {
			if(!root.isMap()){
				layer.hide();
				return;
			}
			rename(idxkey, nkey -> {
				ArrayList<String> keys = new ArrayList<>();
				keys.addAll(root.asMap().value.keySet());
				int idx = keys.indexOf(idxkey);
				HashMap<String, JsonObject> map = new HashMap();
				map.putAll(root.asMap().value);
				map.remove(idxkey);
				map.put(nkey, elm);
				root.asMap().value.clear();
				keys.set(idx, nkey);
				for(String key : keys){
					root.asMap().add(key, map.get(key));
				}
				jsoneditor.refill();
			});
			layer.hide();
		}));
		components.add(new MenuButton(4, "jsoneditormenu.copy", () -> {
			if(root.isMap()){
				root.asMap().add(idxkey + Settings.POLYGON_SUFFIX.value, elm.copy());
				jsoneditor.refill();
			}
			else if(root.isArray()){
				root.asArray().add(elm.copy());
				jsoneditor.refill();
			}
			layer.hide();
		}));
		components.add(new MenuButton(5, "jsoneditormenu.delete", () -> {
			if(root.isMap()){
				root.asMap().rem(idxkey);
				jsoneditor.refill();
			}
			else if(root.isArray()){
				root.asArray().rem(Integer.parseInt(idxkey));
				jsoneditor.refill();
			}
			layer.hide();
		}));
		layer = new MenuLayer(null, new Vector2f((float)GGR.posx, (float)GGR.posy), components, null){
			@Override
			public boolean timed(){
				return true;
			}
		};
	}

	public static void show(JsonEditor editor, JsonObject map, String key, JsonObject value){
		jsoneditor = editor;
		root = map;
		idxkey = key;
		elm = value;
		layer.setPosition((float)GGR.posx, (float)GGR.posy);
		layer.show();
	}

	private static void pickType(boolean askkey, BiConsumer<String, JsonObject<?>> cons){
		float width = 400, height = askkey ? 180 : 120;
		Dialog dialog = new Dialog(translate("toolbar.jsoneditormenu.picktype.title"), width, height);
		Settings.applyComponentTheme(dialog.getContainer());
		dialog.setResizable(true);
		dialog.getContainer().add(new Label(translate("toolbar.jsoneditormenu.picktype.desc"), 10, 10, width - 20, 20));
		SelectBox<String> box = new SelectBox<>(10, 35, width - 20, 20);
		box.addElement("JSON Object (Map)");
		box.addElement("JSON Array (List)");
		box.addElement("JSON Number (Decimals)");
		box.addElement("JSON String (Text)");
		box.addElement("JSON Boolean (true/false)");
		box.addElement("HEX Color (String)");
		box.setVisibleCount(box.getElements().size());
		dialog.getContainer().add(box);
		TextField field = null;
		String[] key = { "" };
		if(askkey){
			dialog.getContainer().add(new Label(translate("toolbar.jsoneditormenu.picktype.desc"), 10, 60, width - 20, 20));
			field = new TextField(key[0], 10, 85, width - 20, 20).accept(text -> {
				key[0] = text;
			});
			dialog.getContainer().add(field);
		}
		Button button0 = new Button(translate("dialog.button.confirm"), 10, height - 50, 100, 20);
		button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
			if(CLICK == e.getAction()){
				if(askkey && (key == null || key.length < 0)) return;
				String str = box.getSelection();
				if(str.contains("Object")){
					cons.accept(key[0], new JsonMap());
				}
				else if(str.contains("Array")){
					cons.accept(key[0], new JsonArray());
				}
				else if(str.contains("Number")){
					cons.accept(key[0], new JsonObject<Float>(0f));
				}
				else if(str.contains("Boolean")){
					cons.accept(key[0], new JsonObject<Boolean>(false));
				}
				else if(str.contains("Color")){
					cons.accept(key[0], new JsonObject<String>("#ffffff"));
				}
				else if(str.contains("String")){
					cons.accept(key[0], new JsonObject<String>(""));
				}
				dialog.close();
			}
		});
		dialog.getContainer().add(button0);
		Button button1 = new Button(translate("dialog.button.cancel"), 120, height - 50, 100, 20);
		button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
			if(CLICK == e.getAction()) dialog.close();
		});
		dialog.getContainer().add(button1);
		dialog.show(FMT.FRAME);
	}

	private static void rename(String curr, Consumer<String> cons){
		float width = 400, height = 120;
		Dialog dialog = new Dialog(translate("toolbar.jsoneditormenu.rename.title"), width, height);
		Settings.applyComponentTheme(dialog.getContainer());
		dialog.setResizable(true);
		dialog.getContainer().add(new Label(translate("toolbar.jsoneditormenu.picktype.desc"), 10, 10, width - 20, 20));
		String[] nkey = { curr };
		TextField field = new TextField(curr, 10, 35, width - 20, 20).accept(text -> {
			nkey[0] = text;
		});
		dialog.getContainer().add(field);;
		Button button0 = new Button(translate("dialog.button.confirm"), 10, height - 50, 100, 20);
		button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
			if(CLICK == e.getAction()){
				cons.accept(nkey[0]);
				dialog.close();
			}
		});
		dialog.getContainer().add(button0);
		Button button1 = new Button(translate("dialog.button.cancel"), 120, height - 50, 100, 20);
		button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
			if(CLICK == e.getAction()) dialog.close();
		});
		dialog.getContainer().add(button1);
		dialog.show(FMT.FRAME);
	}

}
