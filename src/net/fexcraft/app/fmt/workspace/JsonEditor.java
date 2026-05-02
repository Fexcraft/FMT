package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

import java.io.File;
import java.util.Map;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_1;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class JsonEditor extends WFileEditor {


	private JsonMap map;

	public JsonEditor(File file){
		super(file);
		map = JsonHandler.parse(file).asMap();
	}

	@Override
	public void init(Object... args){
		super.init(args);
		for(Map.Entry<String, JsonValue<?>> entry : map.entries()){
			container.add(new JsonElm(entry.getKey(), entry.getValue()));
		}
		container.updateBar();
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
			size(root instanceof JsonElm ? root.w - 10 : root.w - 35, 30);
			pos(root instanceof  JsonElm ? 10 : 0, 0);
			Element icon = new Element().pos(0, 0).size(30, 30);
			float FW = w * 0.6f - 70f, FWO = FW + 70f, TW = w * 0.4f - 35;
			add(new TextElm(35, 2, TW, key == null ? idx + "" : key, GENERIC_BACKGROUND_1.value).text_autoscale());
			add(new HidingElm().pos(w - 30, 0).size(30, 30).texture("icons/configeditor/remove")
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
				add(new HidingElm().pos(w - 60, 0).size(30, 30).texture("icons/configeditor/rename")
					.onclick(ci -> {
						//TODO
					}).hint("workspace.jsoneditor.rename"));
			}
			if(value.isMap()){
				icon.texture("icons/configeditor/object");
				add(new Element().pos(w - 120, 0).size(30, 30).texture("icons/configeditor/add")
					.onclick(ci -> {
						//TODO
					}).hint("workspace.jsoneditor.add"));
				for(Map.Entry<String, JsonValue<?>> entry : value.asMap().entries()){
					add(new JsonElm(entry.getKey(), entry.getValue()));
				}
			}
			else if(value.isArray()){
				icon.texture("icons/configeditor/array");
				add(new Element().pos(w - 120, 0).size(30, 30).texture("icons/configeditor/add")
					.onclick(ci -> {

					}).hint("workspace.jsoneditor.add"));
				fillArray();
			}
			else if(value.isBoolean()){
				icon.texture("icons/configeditor/bool");
				BoolElm elm = new BoolElm();
				add(elm.pos(w - FWO, 2).size(FW, FS));
				elm.set(() -> value.bool(), b -> value.value(b));
				elm.updtexcol();
			}
			else if(value.isNumber()){
				boolean flat = value.value instanceof Float || value.value instanceof Double;
				icon.texture("icons/configeditor/" + (flat ? "float" : "integer"));
				Field field = new Field(flat ? Field.FieldType.FLOAT : Field.FieldType.INT, FW);
				add(field.pos(w - FWO, 2));
				field.text(value.value);
				field.consumer(f -> value.value(flat ? f.parse_float() : f.parse_int()));
			}
			else if(value.string_value().length() > 6 && value.string_value().startsWith("#")){
				icon.texture("icons/configeditor/color");
				Field field = new Field(Field.FieldType.TEXT, FW);
				add(field.pos(w - FWO, 2));
				field.set(Integer.parseInt(value.string_value().substring(1), 16));
				field.consumer(f -> value.value("#" + field.get_text()));
			}
			else{
				icon.texture("icons/configeditor/text");
				Field field = new Field(Field.FieldType.TEXT, FW);
				add(field.pos(w - FWO, 2));
				field.text(value.value);
				field.consumer(f -> value.value(field.get_text()));
			}
			add(icon);
			resort();
		}

		private void fillArray(){
			remElmIf(elm -> elm instanceof JsonElm);
			for(int i = 0; i < value.asArray().value.size(); i++){
				add(new JsonElm(i, value.asArray().get(i)));
			}
		}

		private void resort(){
			int s = 35;
			for(Element elm : elements){
				if(elm instanceof JsonElm){
					elm.pos(elm.x(), s);
					s += 35;
				}
			}
			if(s == 35) s = 30;
			size(w, s).recompile();
			//
			if(root instanceof JsonElm) ((JsonElm)root).resort();
			else ((Scrollable)root).updateBar();
		}

	}

}
