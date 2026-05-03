package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.ui.Dialog.DialogButton;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

import java.io.File;
import java.util.Map;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_1;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class JsonEditor extends WFileEditor {

	private CheckMode je_checkmode;
	private JsonMap map;

	public JsonEditor(File file){
		super(file);
		map = JsonHandler.parse(file).asMap();
	}

	@Override
	public void init(Object... args){
		super.init(args);
		container.top = 30;
		container.updateSize(container.w, container.h);
		je_checkmode = CheckMode.gen(container);
		for(Map.Entry<String, JsonValue<?>> entry : map.entries()){
			container.add(new JsonElm(entry.getKey(), entry.getValue()), je_checkmode);
		}
		container.updateBar();
		add(new RunElm(w - 110, 2, 100, "workspace.jsoneditor.insert", ci -> {
			Field field = new Field(Field.FieldType.TEXT, 490);
			DropList<String> list = new DropList<>(490);
			FMT.UI.createDialog(500, 180, "workspace.jsoneditor")
				.addText(0, "workspace.jsoneditor.add.key")
				.addRowElm(1, field)
				.addText(2, "workspace.jsoneditor.add.type")
				.addRowElm(3, list)
				.set_confirm(d -> {
					String key = field.get_text();
					map.add(key, fromJsonTypeDrop(list));
					container.add(new JsonElm(key, map.get(key)), je_checkmode);
					container.updateBar();
				}).buttons(100, DialogButton.ADD);
			fillJsonTypeDrop(list);
		}).text_centered(true));
	}

	@Override
	protected String get_editor_name(){
		return "Json Editor";
	}

	@Override
	public void save(){
		JsonHandler.print(file, map);
	}

	public static class JsonElm extends Element {

		private JsonValue value;
		private String key;
		private int idx;

		public JsonElm(String key, JsonValue val){
			super();
			this.key = key;
			value = val;
		}

		public JsonElm(int idx, JsonValue val){
			super();
			this.idx = idx;
			value = val;
		}

		@Override
		public void init(Object... args){
			shape(ElmShape.NONE);
			check_mode((CheckMode)args[0]);
			size(root instanceof JsonElm ? root.w - 10 : root.w - 35, 30);
			pos(root instanceof  JsonElm ? 10 : 5, 0);
			Element icon = new Element().pos(0, 0).size(30, 30).check_mode(check_mode);
			float FW = w * 0.6f - 65f, FWO = FW + 65f, TW = w * 0.4f - 35;
			add(new TextElm(35, 2, TW, key == null ? idx + "" : key, GENERIC_BACKGROUND_1.value).text_autoscale().check_mode(check_mode));
			add(new HidingElm().pos(w - 30, 0).size(30, 30).texture("icons/configeditor/remove").check_mode(check_mode)
				.onclick(ci -> {
					JsonElm jr = root instanceof JsonElm elm ? elm : null;
					if(key != null){
						if(jr == null){
							JsonEditor editor = (JsonEditor)root.root;
							editor.map.rem(key);
							editor.container.remElm(this);
							editor.container.updateBar();
						}
						else{
							jr.value.asMap().rem(key);
							jr.remElm(this);
							jr.resort();
						}
					}
					else{
						jr.value.asArray().rem(idx);
						jr.remElm(this);
						jr.fillArray();
						jr.resort();
					}
				}).hint("workspace.jsoneditor.remove"));
			if(key != null){
				add(new HidingElm().pos(w - 60, 0).size(30, 30).texture("icons/configeditor/rename").check_mode(check_mode)
					.onclick(ci -> {
						Field field = new Field(Field.FieldType.TEXT, 490);
						FMT.UI.createDialog(500, 130, "workspace.jsoneditor")
							.addText(0, "workspace.jsoneditor.rename.field")
							.addRowElm(1, field)
							.set_confirm(d -> {
								JsonElm jr = root instanceof JsonElm ? (JsonElm)root : null;
								String nkey = field.get_text();
								if(jr == null){
									JsonEditor editor = (JsonEditor)root.root;
									JsonValue val = editor.map.rem(key);
									editor.container.remElm(this);
									if(val != null){
										editor.map.add(nkey, val);
										editor.container.add(new JsonElm(nkey, val), check_mode);
									}
									editor.container.updateBar();
								}
								else{
									JsonValue val = jr.value.asMap().rem(key);
									if(val != null){
										jr.value.asMap().add(field.get_text(), val);
										jr.fillMap();
									}
									jr.resort();
								}
							}).buttons(100, DialogButton.CONFIRM);
						field.text(key);
					}).hint("workspace.jsoneditor.rename"));
			}
			if(value.isMap()){
				render_sub_even_if_invisible = true;
				icon.texture("icons/configeditor/object");
				add(new Element().pos(w - 120, 0).size(30, 30).texture("icons/configeditor/add").check_mode(check_mode)
					.onclick(ci -> {
						Field field = new Field(Field.FieldType.TEXT, 490);
						DropList<String> list = new DropList<>(490);
						FMT.UI.createDialog(500, 180, "workspace.jsoneditor")
							.addText(0, "workspace.jsoneditor.add.key")
							.addRowElm(1, field)
							.addText(2, "workspace.jsoneditor.add.type")
							.addRowElm(3, list)
							.set_confirm(d -> {
								value.asMap().add(field.get_text(), fromJsonTypeDrop(list));
								fillMap();
								resort();
							}).buttons(100, DialogButton.ADD);
						fillJsonTypeDrop(list);
					}).hint("workspace.jsoneditor.add"));
				add(new HidingElm().pos(w - 90, 0).size(30, 30).texture("icons/component/remove").check_mode(check_mode)
					.onclick(ci -> {
						value.asMap().value.clear();
						fillMap();
						resort();
					}).hint("workspace.jsoneditor.clear"));
				fillMap();
			}
			else if(value.isArray()){
				render_sub_even_if_invisible = true;
				icon.texture("icons/configeditor/array");
				add(new Element().pos(w - 120, 0).size(30, 30).texture("icons/configeditor/add").check_mode(check_mode)
					.onclick(ci -> {
						DropList<String> list = new DropList<>(490);
						FMT.UI.createDialog(500, 120, "workspace.jsoneditor")
							.addText(0, "workspace.jsoneditor.add.type")
							.addRowElm(1, list)
							.set_confirm(d -> {
								value.asArray().add(fromJsonTypeDrop(list));
								fillArray();
								resort();
							}).buttons(100, DialogButton.ADD);
						fillJsonTypeDrop(list);
					}).hint("workspace.jsoneditor.add"));
				add(new HidingElm().pos(w - 90, 0).size(30, 30).texture("icons/component/remove").check_mode(check_mode)
					.onclick(ci -> {
						value.asArray().value.clear();
						fillArray();
						resort();
					}).hint("workspace.jsoneditor.clear"));
				fillArray();
			}
			else if(value.isBoolean()){
				icon.texture("icons/configeditor/bool");
				BoolElm elm = new BoolElm(w - FWO, 2, FW);
				add(elm.check_mode(check_mode));
				elm.set(() -> value.bool(), b -> value.value(b));
				elm.updtexcol();
			}
			else if(value.isNumber()){
				boolean flat = value.value instanceof Float || value.value instanceof Double;
				icon.texture("icons/configeditor/" + (flat ? "float" : "integer"));
				Field field = new Field(flat ? Field.FieldType.FLOAT : Field.FieldType.INT, FW);
				add(field.pos(w - FWO, 2).check_mode(check_mode));
				field.text(value.value);
				field.consumer(f -> value.value(flat ? f.parse_float() : f.parse_int()));
			}
			else if(value.string_value().length() > 6 && value.string_value().startsWith("#")){
				icon.texture("icons/configeditor/color");
				Field field = new Field(Field.FieldType.COLOR, FW);
				add(field.pos(w - FWO, 2).check_mode(check_mode));
				field.set(Integer.parseInt(value.string_value().substring(1), 16));
				field.consumer(f -> value.value("#" + field.get_text()));
			}
			else{
				icon.texture("icons/configeditor/text");
				Field field = new Field(Field.FieldType.TEXT, FW);
				add(field.pos(w - FWO, 2).check_mode(check_mode));
				field.text(value.value);
				field.consumer(f -> value.value(field.get_text()));
			}
			add(icon);
			resort();
		}

		private void fillMap(){
			remElmIf(elm -> elm instanceof JsonElm);
			for(Map.Entry<String, JsonValue<?>> entry : value.asMap().entries()){
				add(new JsonElm(entry.getKey(), entry.getValue()), check_mode);
			}
		}

		private void fillArray(){
			remElmIf(elm -> elm instanceof JsonElm);
			for(int i = 0; i < value.asArray().value.size(); i++){
				add(new JsonElm(i, value.asArray().get(i)), check_mode);
			}
		}

		private void resort(){
			float s = 35;
			for(Element elm : elements){
				if(elm instanceof JsonElm){
					elm.pos(elm.x(), s);
					s += elm.h + 5;
				}
			}
			size(w, s).recompile();
			//
			if(root instanceof JsonElm) ((JsonElm)root).resort();
			else ((Scrollable)root).updateBar();
		}

	}

	private static JsonValue fromJsonTypeDrop(DropList<String> list){
		switch(list.getSelVal()){
			case "map": return new JsonMap();
			case "array": return new JsonArray();
			case "int": return new JsonValue<>(0);
			case "float": return new JsonValue<>(0f);
			case "bool": return new JsonValue<>(false);
			case "color": return new JsonValue<>("#ffffff");
			case "string":
			default: return new JsonValue<>("empty");
		}
	}

	private static void fillJsonTypeDrop(DropList<String> list){
		list.addEntry("Object (Map)", "map");
		list.addEntry("Array (List)", "array");
		list.addEntry("String (Text)", "string");
		list.addEntry("Integer (Full Number)", "int");
		list.addEntry("Float (Decimal Number)", "float");
		list.addEntry("Boolean (true/false)", "bool");
		list.addEntry("Color (#hex)", "color");
		list.selectEntry(0);
	}

}
