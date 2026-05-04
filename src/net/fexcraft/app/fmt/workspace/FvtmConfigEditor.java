package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.ui.Dialog.DialogButton;
import net.fexcraft.app.fmt.ui.Field.FieldType;
import net.fexcraft.app.fmt.utils.fvtm.*;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

import java.io.File;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_1;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_2;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmConfigEditor extends WFileEditor {

	private static String[] xyz = { "x", "y", "z" };
	private ConfigReference ref;
	private CheckMode checkmode;
	private FvtmType type;
	private JsonMap map;

	public FvtmConfigEditor(File file){
		super(file);
	}

	@Override
	public void init(Object... args){
		super.init(args);
		container.top = 30;
		container.updateSize(container.w, container.h);
		checkmode = CheckMode.gen(container);
		type = FvtmType.fromFile(file);
		map = JsonHandler.parse(file).asMap();
		ref = getReference();
		if(ref == null){
			add(new TextElm(5, 5, w - 100, "Reference not found. May be WIP.").text_autoscale());
			return;
		}
		fill();
	}

	private void fill(){
		container.remElmIf(EntryElm.class::isInstance);
		for(ConfigEntry entry : ref.entries){
			JsonValue val = null;
			if(map.has(entry.name)) val = map.get(entry.name);
			if(val == null && map.has(entry.alt)) val = map.get(entry.alt);
			container.add(new EntryElm(entry, val), this);
		}
		container.updateBar();
	}

	private ConfigReference getReference(){
		switch(type){
			case VEHICLE: return VehicleConfigReference.INSTANCE;
			case PART: return PartConfigReference.INSTANCE;
			case MATERIAL: return MaterialConfigReference.INSTANCE;
			case CONSUMABLE: return ConsumableConfigReference.INSTANCE;
			case BLOCK: return BlockConfigReference.INSTANCE;
			case DECORATION: return DecorationConfigReference.INSTANCE;
			case SIGN: return SignConfigReference.INSTANCE;
		}
		return null;
	}

	private ConfigReference getSubRef(String type, String arg){
		switch(type){
			case "modeldata": return ModelDataReference.INSTANCE;
			case "installation":{
				switch(arg){
					case "default": return PartInstallConfigReference.DEFAULT;
					case "wheel": return PartInstallConfigReference.WHEEL;
					case "tire": return PartInstallConfigReference.TIRE;
					case "bogie": return PartInstallConfigReference.BOGIE;
				}
				return null;
			}
			case "functions":{
				return PartFunctionConfigReference.REFERENCES.get(arg);
			}
		}
		return null;
	}

	@Override
	protected String get_editor_name(){
		return "Fvtm Config Editor";
	}

	@Override
	public void save(){
		JsonHandler.print(file, map);
	}

	public static class EntryElm extends Element {

		private EntryElmCon container;
		private ConfigEntry entry;
		private JsonValue value;
		private Field[] input;

		public EntryElm(ConfigEntry entry, JsonValue val){
			super();
			this.entry = entry;
			value = val == null ? entry.gendef() : val;
		}

		@Override
		public void init(Object... args){
			shape(ElmShape.NONE);
			FvtmConfigEditor editor = (FvtmConfigEditor)args[0];
			check_mode(editor.checkmode);
			boolean incon = root instanceof EntryElmCon;
			size(incon ? root.w - 10 : root.w - 35, 30);
			pos(incon ? 10 : 5, 0);
			add(new Element().size(30, 30).check_mode(check_mode)
				.texture("icons/configeditor/" + entry.type.icon()));
			float FW = w * 0.6f - 65f, FWO = FW + 65f, TW = w * 0.4f - 35;
			add(new TextElm(35, 2, TW, entry.name + (entry.required ? "*" : ""), GENERIC_BACKGROUND_1.value)
				.text_autoscale().check_mode(check_mode).onclick(ci -> toggleContainer(editor, null)));
			if(entry.type.subs()){
				add(container = new EntryElmCon());
				toggleContainer(editor, true);
			}
			ConfigEntry ren = incon ? ((EntryElm)root.root).entry : ConfigEntry.TEXT_ENTRY;
			JsonValue ral = incon ? ((EntryElm)root.root).value : ((FvtmConfigEditor)root.root).map;
			if(incon){
				boolean edit = ren.type.map() && !ren.type.subtype() && !ren.type.static_();
				if(!ren.type.separate() && !ren.type.static_()){
					add(new HidingElm().pos(w - 30, 0).size(30, 30).texture("icons/component/remove").check_mode(check_mode)
						.onclick(ci -> {
							if(ren.type.subs() && !ren.type.subtype()){
								if(ral.isMap()) ral.asMap().rem(entry.key().key);
								else ral.asArray().rem(entry.key().idx);
								root_refill();
							}
							else{
								if(entry.type.vector()){
									input[0].clear_text();
									input[1].clear_text();
									input[2].clear_text();
								}
								else if(input[0] != null) input[0].text(entry.gendef().string_value());
							}
						}).hint("workspace.configeditor.remove"));
				}
				if(edit){
					add(new HidingElm().pos(w - 60, 0).size(30, 30).texture("icons/component/rename").check_mode(check_mode)
						.onclick(ci -> {
							Field field = new Field(FieldType.TEXT, 490);
							FMT.UI.createDialog(500, 120, "workspace.configeditor")
								.addText(0, "workspace.configeditor.rename.field")
								.addRowElm(1, field)
								.set_confirm(d -> {
									if(ral.isMap() && ren.type.subs() && !ren.type.subtype()){
										JsonValue value = ral.asMap().get(entry.key().key);
										ral.asMap().rem(entry.key().key);
										ral.asMap().add(field.get_text(), value);
										root_refill();
									}
									else{
										value.value(field.get_text());
										updateValue(ren, ral);
									}
								}).buttons(100, DialogButton.CONFIRM);
							field.text(entry.key().key);
						}).hint("workspace.configeditor.rename"));
				}
			}
			if(entry.type.select()) addSelector();
			if(entry.type.subs()){
				if(!entry.type.subtype()) refill();
			}
			else addField(ren, ral, FW, FWO);
		}

		private void addSelector(){

		}

		private void addField(ConfigEntry ren, JsonValue ral, float FW, float FWO){
			if(entry.type.vector()){
				input = new Field[3];
				for(int v = 0; v < 3; v++){
					int iv = v;
					input[v] = new Field(FieldType.FLOAT, FW * 0.33f);
					add(input[v].pos(w - FWO * (0.33f * (v + 1)), 2).check_mode(check_mode));
					if(entry.type == EntryType.VECTOR_MAP){
						if(ral.asMap().has(xyz[iv])) input[v].set(ral.asMap().get(xyz[iv]).float_value());
						input[v].consumer(f -> ral.asMap().add(xyz[iv], f.parse_float()));
					}
					else{
						while(value.asArray().size() < v) value.asArray().add(0f);
						input[v].set(value.asArray().get(v).float_value());
						input[v].consumer(f -> {
							fillMissing();
							value.asArray().set(iv, value = new JsonValue(f.parse_float()));
						});
					}
				}
			}
			else if(entry.type.color()){
				input = new Field[1];
				input[0] = new Field(FieldType.COLOR, FW);
				add(input[0].pos(w - FWO, 2).check_mode(check_mode));
				input[0].set(Integer.parseInt(value.string_value().substring(1), 16));
				input[0].consumer(f -> {
					fillMissing();
					value.value("#" + input[0].get_text());
				});
			}
			else if(entry.type.bool()){
				BoolElm elm = new BoolElm(w - FWO, 2, FW);
				add(elm.check_mode(check_mode));
				elm.set(() -> value.bool(), b -> {
					fillMissing();
					value.value(b);
				});
				elm.updtexcol();
			}
			else if(entry.type.enumerate()){
				DropList<String> list = new DropList<>(FW);
				add(list.pos(w - FWO, 2).check_mode(check_mode));
				for(String en : entry.enums) list.addEntry(en, en);
				list.onchange((key, val) -> {
					fillMissing();
					if(entry.type.separate() && value.isMap()){
						if(ren.type.map()){
							value = ral.asMap().rem(entry.key().key);
							ral.asMap().add(key, value);
							root_refill();
						}
						else value.asMap().add(entry.subs.get(0).name, key);
					}
					else value.value(key);
				});
				if(entry.type.separate() && value.isMap()){
					if(ren.type.map()){
						list.selectKey(entry.key().key);
					}
					else if(value.asMap().has(entry.subs.get(0).name)){
						list.selectKey(value.asMap().get(entry.subs.get(0).name).string_value());
					}
				}
				else list.selectKey(value.string_value());
			}
			else if(!entry.type.separate()){//text
				input = new Field[1];
				input[0] = new Field(entry.type.toFieldType(), FW);
				add(input[0].pos(w - FWO, 2).check_mode(check_mode));
				input[0].text(value.string_value());
				input[0].consumer(f -> value.value(input[0].get_text()));
				input[0].consumer(f -> {
					fillMissing();
					if(entry.type == EntryType.INTEGER) value.value(f.parse_int());
					else if(entry.type == EntryType.DECIMAL) value.value(f.parse_float());
					else value.value(f.get_text());
				});
			}
		}

		private void fillMissing(){
			fillMissing(true);
		}

		private void fillMissing(boolean check){
			if(root instanceof EntryElmCon == false) return;
			JsonValue ral = ((EntryElm)root.root).value;
			if(check){
				((EntryElm)root.root).fillMissing();
			}
			if(ral.isMap()){
				if(!ral.asMap().has(entry.key().key)){
					ral.asMap().add(entry.key().key, value);
				}
			}
			else{
				if(!ral.asArray().contains(value)){
					ral.asArray().add(value);
				}
			}
		}

		private void refill(){
			if(container == null) return;
			container.remElmIf(EntryElm.class::isInstance);
		}

		private void root_refill(){
			((EntryElm)root.root).refill();
		}

		private void updateValue(ConfigEntry ren, JsonValue ral){
			if(ral.isMap()){
				ral.asMap().add(entry.key().key, value);
			}
			else{
				ral.asArray().rem(value);
				ral.asArray().add(value);
			}
			if(ren.type.subs()) root_refill();
		}

		private void toggleContainer(FvtmConfigEditor editor, Boolean state){
			if(container == null) return;
			if(state == null) container.toggleVisibility();
			else container.visible = state;
			size(w, container.visible ? container.h + 30 : 30);
			editor.container.updateBar();
		}

	}

	public static class EntryElmCon extends Element {

		public EntryElmCon(){
			border(GENERIC_BACKGROUND_2.value);
			size(w, 0);
			pos(0, 30);
		}

	}

}
