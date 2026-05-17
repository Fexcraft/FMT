package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.ui.Dialog.DialogButton;
import net.fexcraft.app.fmt.ui.Field.FieldType;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.fvtm.*;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.math.Vec3f;

import java.io.File;
import java.util.LinkedHashSet;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_1;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.OBJ_SUB_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;

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
			container.add(new EntryElm(entry, entry.key(), val), this);
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

	@Override
	protected String get_editor_name(){
		return "Fvtm Config Editor";
	}

	@Override
	public void save(){
		JsonHandler.print(file, map);
	}

	public static class EntryElm extends Element {

		private FvtmConfigEditor editor;
		private EntryElmCon container;
		private ConfigEntry entry;
		private JsonValue value;
		private Element counter;
		private SubKey skey;
		private Field[] input;
		private boolean wn;

		public EntryElm(ConfigEntry entry, SubKey key, JsonValue val){
			super();
			this.entry = entry;
			skey = key;
			wn = val == null;
			value = wn ? entry.gendef() : val;
			render_sub_even_if_invisible = true;
		}

		@Override
		public void init(Object... args){
			shape(ElmShape.NONE);
			editor = (FvtmConfigEditor)args[0];
			check_mode(editor.checkmode);
			boolean incon = root instanceof EntryElmCon;
			size(incon ? root.w - 10 : root.w - 35, 30);
			pos(incon ? 10 : 5, 0);
			add(new Element().size(30, 30).check_mode(check_mode)
				.texture("icons/configeditor/" + entry.type.icon()));
			float FW = w * 0.6f - 65f, FWO = FW + 65f, TW = w * 0.4f - 35;
			add(new TextElm(35, 2, TW, (entry.name == null ? skey.key : entry.name) + (entry.required ? "*" : ""), GENERIC_BACKGROUND_1.value)
				.text_autoscale().check_mode(check_mode).onclick(ci -> toggleContainer(null)));
			if(entry.type.subs()){
				add(container = new EntryElmCon());
				if(!entry.type.enumerate()){
					add(counter = new TextElm(w * 0.4f, 2, w * 0.25f).check_mode(check_mode));
				}
				updateSize();
			}
			ConfigEntry ren = incon ? ((EntryElm)root.root).entry : ConfigEntry.TEXT_ENTRY;
			JsonValue ral = incon ? ((EntryElm)root.root).value : ((FvtmConfigEditor)root.root).map;
			//if(incon){
				boolean edit = ren.type.map() && !ren.type.subtype() && !ren.type.static_();
				if(!ren.type.separate() && !ren.type.static_()){
					add(new HidingElm().pos(w - 30, 0).size(30, 30).texture("icons/component/remove").check_mode(check_mode)
						.onclick(ci -> {
							if(ren.type.subs() && !ren.type.subtype()){
								if(ral.isMap()) ral.asMap().rem(skey.key);
								else ral.asArray().rem(skey.idx);
								root_refill();
							}
							else{
								if(entry.type.vector()){
									input[0].clear_text();
									input[1].clear_text();
									input[2].clear_text();
								}
								else if(input != null && input.length > 0){
									input[0].text(entry.gendef().string_value());
								}
							}
						}).hint("workspace.configeditor.remove"));
				//}
				if(edit){
					FW = w * 0.6f - 95f;
					FWO = FW + 95f;
					add(new HidingElm().pos(w - 60, 0).size(30, 30).texture("icons/configeditor/rename").check_mode(check_mode)
						.onclick(ci -> {
							Field field = new Field(FieldType.TEXT, 490);
							FMT.UI.createDialog(500, 120, "workspace.configeditor")
								.addText(0, "workspace.configeditor.rename.field")
								.addRowElm(1, field)
								.set_confirm(d -> {
									if(ral.isMap() && ren.type.subs() && !ren.type.subtype()){
										JsonValue value = ral.asMap().get(skey.key);
										ral.asMap().rem(skey.key);
										ral.asMap().add(field.get_text(), value);
										root_refill();
									}
									else{
										value.value(field.get_text());
										updateValue(ren, ral);
									}
								}).buttons(100, DialogButton.CONFIRM);
							field.text(skey.key);
						}).hint("workspace.configeditor.rename"));
				}
			}
			if(entry.type.select()) addSelector(edit);
			if(entry.type.subs()){
				if(!entry.type.subtype()) refill();
			}
			if(!entry.type.subs() || entry.type.enumerate()){
				addField(ren, ral, FW, FWO);
			}
		}

		private void addSelector(boolean edit){
			add(new Element().pos(w - (edit ? 90 : 60), 0).size(30, 30).texture("icons/configeditor/select").check_mode(check_mode)
				.onclick(ci -> {
					switch(entry.type){
						case PACKID:{
							FMT.WORKSPACE.selectPack(pack -> {
								value.value(pack.id);
								input[0].text(value.string_value());
							});
							return;
						}
						case MODELLOC:{
							FMT.WORKSPACE.selectPack(pack -> {
								DropList<String> list = new DropList<>(490);
								FMT.UI.createDialog(500, 120, "workspace.configeditor")
									.addText(0, "workspace.configeditor.select.model")
									.addRowElm(1, list)
									.set_confirm(d -> {
										String val = list.getSelVal();
										if(val == null) val = "null";
										value.value(val);
										input[0].text(value.string_value());
										fillMissing();
									})
									.buttons(100, DialogButton.SELECT);
								for(FileElm com : pack.models){
									String path = com.file.getPath().replace("\\", "/");
									if(!path.contains("/models")){
										Logging.log("invalid model path: " + path);
										continue;
									}
									String pid = path.substring(path.indexOf("/assets/") + 8, path.indexOf("/models"));
									path = path.substring(path.indexOf("/models/") + 1);
									list.addEntry(pid + ":" + path);
								}
								list.selectEntry(0);
							}, pack -> pack.models.size() > 0);
							return;
						}
						case TEXLOC:{
							FMT.WORKSPACE.selectPack(pack -> {
								DropList<String> list = new DropList<>(490);
								FMT.UI.createDialog(500, 120, "workspace.configeditor")
									.addText(0, "workspace.configeditor.select.texture")
									.addRowElm(1, list)
									.set_confirm(d -> {
										String val = list.getSelVal();
										if(val == null) val = "fvtm:textures/entity/null.png";
										String prefix = value.string_value();
										if(prefix.contains(";")) prefix = prefix.split(";")[0];
										else prefix = null;
										val = prefix + ";" + val;
										value.value(val);
										input[0].text(value.string_value());
									})
									.buttons(100, DialogButton.SELECT);
								for(FileElm com : pack.textures){
									String path = com.file.getPath().replace("\\", "/");
									if(!path.contains("/textures")){
										Logging.log("invalid texture path: " + path);
										continue;
									}
									String pid = path.substring(path.indexOf("/assets/") + 8, path.indexOf("/textures"));
									path = path.substring(path.indexOf("/textures/") + 1);
									list.addEntry(pid + ":" + path);
								}
								list.selectEntry(0);
							}, pack -> pack.textures.size() > 0);
							return;
						}
						case VECTOR_MAP:
						case VECTOR_ARRAY:{
							DropList<String> vtype = new DropList<>(490);
							FMT.UI.createDialog(500, 120, "workspace.configeditor")
								.addText(0, "workspace.configeditor.select.vector_source")
								.addRowElm(1, vtype)
								.set_confirm(d -> {
									if(vtype.getSelVal().equals("pivot")){
										DropList<Pivot> vlist = new DropList<>(490);
										FMT.UI.createDialog(500, 120, "workspace.configeditor")
											.addText(0, "workspace.configeditor.select.pivot")
											.addRowElm(1, vlist)
											.set_confirm(di -> {
												Vec3f vec = new Vec3f();
												Pivot pivot = vlist.getSelVal();
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
												input[0].set(vec.x).consumer.accept(input[0]);
												input[1].set(vec.y).consumer.accept(input[1]);
												input[2].set(vec.z).consumer.accept(input[2]);
											})
											.buttons(100, DialogButton.SELECT);
										for(Pivot pivot : FMT.MODEL.pivots()){
											vlist.addEntry(pivot.parentid + " / " + pivot.id, pivot);
										}
										vlist.selectEntry(0);
									}
									else if(vtype.getSelVal().equals("marker")){
										DropList<Polygon> vlist = new DropList<>(490);
										FMT.UI.createDialog(500, 120, "workspace.configeditor")
											.addText(0, "workspace.configeditor.select.marker")
											.addRowElm(1, vlist)
											.set_confirm(di -> {
												Vec3f vec = new Vec3f();
												Polygon poly = vlist.getSelVal();
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
												input[0].set(vec.x).consumer.accept(input[0]);
												input[1].set(vec.y).consumer.accept(input[1]);
												input[2].set(vec.z).consumer.accept(input[2]);
											})
											.buttons(100, DialogButton.SELECT);
										for(Group group : FMT.MODEL.allgroups()){
											for(Polygon poly : group){
												if(!poly.getShape().isMarker()) continue;
												if(poly.name(true) == null) continue;
												vlist.addEntry(group.id + "/" + poly.name(), poly);
											}
										}
										vlist.selectEntry(0);
									}
								})
								.buttons(100, DialogButton.CONTINUE);
							vtype.addEntry("pivot");
							vtype.addEntry("marker");
							vtype.selectEntry(1);
							return;
						}
						case ENUM_SEPARATE:
						case SEPARATE:{
							return;
						}
					}
				}).hint("workspace.configeditor.select"));
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
							value.asArray().set(iv, new JsonValue(f.parse_float()));
							fillMissing();
						});
					}
				}
			}
			else if(entry.type.color()){
				input = new Field[1];
				input[0] = new Field(FieldType.COLOR, FW);
				add(input[0].pos(w - FWO, 2).check_mode(check_mode));
				if(value != null){
					input[0].set(Integer.parseInt(value.string_value().substring(1), 16));
				}
				input[0].consumer(f -> {
					value.value("#" + input[0].get_text());
					fillMissing();
				});
			}
			else if(entry.type.bool()){
				BoolElm elm = new BoolElm(w - FWO, 2, FW);
				add(elm.check_mode(check_mode));
				elm.set(() -> value.bool(), b -> {
					value.value(b);
					fillMissing();
				});
				elm.updtexcol();
			}
			else if(entry.type.enumerate()){
				if(entry.type.separate()) FW -= 30;
				DropList<String> list = new DropList<>(FW);
				add(list.pos(w - FWO, 2).check_mode(check_mode));
				for(String en : entry.enums) list.addEntry(en, en);
				list.onchange((key, val) -> {
					if(entry.type.separate() && value.isMap()){
						if(ren.type.map()){
							value = ral.asMap().rem(skey.key);
							ral.asMap().add(key, value);
						}
						else value.asMap().add(entry.subs.get(0).name, key);
						root_refill();
					}
					else value.value(key);
					fillMissing();
				});
				if(entry.type.separate() && value.isMap()){
					if(ren.type.map()){
						list.selectKey(skey.key);
					}
					else if(value.asMap().has(entry.subs.get(0).name)){
						list.selectKey(value.asMap().get(entry.subs.get(0).name).string_value());
					}
				}
				else if(value != null) list.selectKey(value.string_value());
				else list.selectEntry(0);
			}
			else if(!entry.type.separate()){//text
				input = new Field[1];
				input[0] = new Field(entry.type.toFieldType(), FW);
				add(input[0].pos(w - FWO, 2).check_mode(check_mode));
				if(value != null) input[0].text(value.string_value());
				input[0].consumer(f -> {
					if(entry.type == EntryType.INTEGER) value.value(f.parse_int());
					else if(entry.type == EntryType.DECIMAL) value.value(f.parse_float());
					else value.value(f.get_text());
					fillMissing();
				});
			}
		}

		private void fillMissing(){
			fillMissing(true);
		}

		private void fillMissing(boolean check){
			boolean incon = root instanceof EntryElmCon;
			JsonValue ral = incon ? ((EntryElm)root.root).value : ((FvtmConfigEditor)root.root).map;
			ConfigEntry ren = incon ? ((EntryElm)root.root).entry : TEXT_ENTRY;
			if(check && incon){
				((EntryElm)root.root).fillMissing();
			}
			if(ral.isMap()){
				if(!ral.asMap().has(skey.key)){
					ral.asMap().add(skey.key, value);
				}
			}
			else{
				if(ren.type == EntryType.ARRAY){
					if(!ral.asArray().contains(value)){
						ral.asArray().set(skey.idx, value);
					}
				}
				else{
					if(!ral.asArray().contains(value)){
						ral.asArray().add(value);
					}
				}
			}
		}

		private void refill(){
			if(container == null) return;
			container.remElmIf(EntryElm.class::isInstance);
			boolean incon = root instanceof EntryElmCon;
			ConfigEntry ren = incon ? ((EntryElm)root.root).entry : ConfigEntry.TEXT_ENTRY;
			JsonValue ral = incon ? ((EntryElm)root.root).value : ((FvtmConfigEditor)root.root).map;
			if(entry.type == EntryType.ARRAY && entry.subs != null){
				/*if(value == null){
					if(ral.isMap()) ral.asMap().add(skey.key, value = entry.gendef());
					else ral.asArray().set(skey.idx, value = entry.gendef());
				}*/
				JsonArray arr = value.asArray();
				for(int i = 0; i < arr.size(); i++){
					for(ConfigEntry conf : entry.subs){
						container.add(new EntryElm(conf, new SubKey(i), arr.get(i)), editor);
					}
				}
			}
			else if(entry.type == EntryType.ARRAY_SIMPLE){
				JsonArray arr = !value.isArray() ? null : value.asArray();
				if(arr == null){
					ral.asMap().add(skey.key, arr = new JsonArray());
					if(value != null) arr.add(value);
					value = arr;
				}
				if(arr != null){
					for(int i = 0; i < arr.size(); i++){
						container.add(new EntryElm(entry.subs.get(0), new SubKey(i), arr.get(i)), editor);
					}
				}
			}
			else if(entry.type == EntryType.OBJECT && entry.subs != null){
				/*if(value == null){
					ral.asMap().add(entry.name, value = entry.gendef());
				}*/
				JsonMap map = value.asMap();
				LinkedHashSet<String> keys = new LinkedHashSet<>(map.value.keySet());
				for(String key : keys){
					JsonValue v = map.get(key);
					JsonMap sup;
					if(!v.isMap()){
						sup = entry.converter.apply(key, v).asMap();
						map.add(key, sup);
					}
					else sup = v.asMap();
					EntryElm sub = new EntryElm(OBJ_SUB_ENTRY, new SubKey(key), sup);
					container.add(sub, editor);
					for(ConfigEntry conf : entry.subs){
						sub.container.add(new EntryElm(conf, conf.key(), get(sup, conf)), editor);
					}
				}
			}
			else if(entry.type == EntryType.SEPARATE && entry.separate.entries != null){
				if(entry.static_){
					if(!value.isMap()) value = new JsonMap();
					for(ConfigEntry conf : entry.separate.entries){
						container.add(new EntryElm(conf, conf.key(), get(value.asMap(), conf)), editor);
					}
				}
				else{
					value.asMap().entries().forEach(e -> {
						container.add(new EntryElm(entry.separate.entries.get(0), new SubKey(e.getKey()), e.getValue()), editor);
					});
				}
			}
			else if(entry.type == EntryType.ENUM_SEPARATE && entry.sep_enums != null){
				String key = ren.type == EntryType.OBJECT_KEY_VAL ? skey.key : null;
				if(key == null){//EntryType.OBJECT
					if(value == null) key = entry.def;
					else if(!value.isMap()) key = value.string_value();
					else key = value.asMap().getString(entry.subs.get(0).key().key, entry.def);
				}
				ConfigReference ref = entry.sep_enums.get(key);
				if(ref.entries.size() > 0 && ref.entries.get(0).type == EntryType.OBJECT){
					value.asMap().entries().forEach(e -> {
						EntryElm sub = new EntryElm(OBJ_SUB_ENTRY, new SubKey(e.getKey()), e.getValue());
						container.add(sub, editor);
						for(ConfigEntry conf : ref.entries.get(0).subs){
							sub.container.add(new EntryElm(conf, conf.key(), get(e.getValue().asMap(), conf)), editor);
						}
					});
				}
				else{
					for(ConfigEntry conf : ref.entries){
						container.add(new EntryElm(conf, conf.key(), value.isMap() ? get(value.asMap(), conf) : null), editor);
					}
				}
			}
			else if(entry.type == EntryType.OBJECT_KEY_VAL){
				/*if(value == null){
					if(ral.isMap()) ral.asMap().add(entry.name, value = entry.gendef());
				}*/
				if(entry.static_){
					for(ConfigEntry conf : entry.subs){
						container.add(new EntryElm(conf, conf.key(), get(value.asMap(), conf)), editor);
					}
				}
				else{
					value.asMap().entries().forEach(e -> {
						container.add(new EntryElm(entry.subs.get(0), new SubKey(e.getKey()), e.getValue()), editor);
					});
				}
			}
			updateSize();
			if(!entry.type.subtype() && !entry.static_ && entry.type != EntryType.SEPARATE){
				add(new Element().pos(w - 120, 0).size(30, 30).texture("icons/configeditor/add").check_mode(check_mode)
					.onclick(ci -> {
						if(entry.type == EntryType.ARRAY){
							JsonMap sup = new JsonMap();
							for(ConfigEntry conf : entry.subs){
								container.add(new EntryElm(conf, new SubKey(value.asArray().size()), sup.get(conf.name)), editor);
							}
							value.asArray().add(sup);
							updateSize();
						}
						else if(entry.type == EntryType.ARRAY_SIMPLE){
							JsonArray arr = value.asArray();
							arr.add(entry.subs.get(0).gendef());
							container.add(new EntryElm(entry.subs.get(0), new SubKey(arr.size() - 1), arr.get(arr.size() - 1)), editor);
							updateSize();
						}
						else if(entry.type == EntryType.OBJECT){
							JsonMap map = value.asMap();
							JsonMap sup = new JsonMap();
							String nkey = "entry" + map.entries().size();
							map.add(nkey, sup);
							EntryElm sub = new EntryElm(OBJ_SUB_ENTRY, new SubKey(nkey), sup);
							container.add(sub, editor);
							for(ConfigEntry conf : entry.subs){
								sub.container.add(new EntryElm(conf, conf.key(), get(sup, conf)), editor);
							}
							updateSize();
						}
						else if(entry.type == EntryType.OBJECT_KEY_VAL){
							JsonMap map = value.asMap();
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
								container.add(new EntryElm(entry.subs.get(0), new SubKey(nkey), map.get(nkey)), editor);
								updateSize();
							}
						}
						fillMissing();
					}).hint("workspace.configeditor.add"));
			}
		}

		private JsonValue get(JsonMap map, ConfigEntry conf){
			if(map.has(conf.name)) return map.get(conf.name);
			if(map.has(conf.alt)) return map.get(conf.alt);
			return null;
		}

		private void root_refill(){
			if(root instanceof EntryElmCon) ((EntryElm)root.root).refill();
			else refill();
			//else editor.fill();
		}

		private void updateValue(ConfigEntry ren, JsonValue ral){
			if(ral.isMap()){
				ral.asMap().add(skey.key, value);
			}
			else{
				ral.asArray().rem(value);
				ral.asArray().add(value);
			}
			if(ren.type.subs()) root_refill();
		}

		private void toggleContainer(Boolean state){
			if(container == null) return;
			if(state == null) container.toggleVisibility();
			else container.visible = state;
			container.render_sub_even_if_invisible = container.visible;
			updateSize();
		}

		private void updateSize(){
			if(container == null) return;
			container.updateElmAndSize();
			if(counter != null){
				counter.text(value == null ? "n" : value.isMap() ? value.asMap().size() : value.isArray() ? value.asArray().size() : 0);
			}
			if(root instanceof EntryElmCon) ((EntryElm)root.root).updateSize();
			else editor.container.updateBar();
		}

	}

	public static class EntryElmCon extends Element {

		@Override
		public void init(Object... args){
			size(root.w, 0);
			pos(0, 30);
		}

		public void updateElmAndSize(){
			if(elements == null){
				size(w, 0);
				return;
			}
			float buff = 2.5f;
			for(Element elm : elements){
				elm.pos(elm.x(), buff);
				if(elm instanceof EntryElm ee && ee.container != null) ee.container.updateElmAndSize();
				buff += elm.h;
			}
			size(w, buff + 2.5f);
			root.size(w, visible ? h + 30 : 30);
		}

	}

}
