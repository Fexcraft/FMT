package net.fexcraft.app.fmt.port.im;

import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.polygon.uv.BoxFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.polygon.uv.UVType;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

import java.io.File;
import java.util.*;

import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_BBM;

/**
 * BlockBench Model file format importer
 *
 * @author Ferdinand Calo' (FEX___96)
 */
public class BBMImporter implements Importer {

	private static final List<String> categories = Arrays.asList("model");

	@Override
	public String _import(Model model, File file){
		try{
			JsonMap map = JsonHandler.parse(file);
			String type = map.getMap("meta").getString("model_format", "java_block");
			boolean modent = type.equals("modded_entity");
			model.name = map.getString("name", model.name);
			JsonMap res = map.getMap("resolution");
			model.texSizeX = res.getInteger("width", 16);
			model.texSizeY = res.getInteger("height", 16);
			JsonArray elms = map.getArray("elements");
			LinkedHashMap<String, Polygon> polis = new LinkedHashMap<>();
			for(JsonValue<?> jsn : elms.value){
				JsonMap elm = jsn.asMap();
				Polygon poly = null;
				if(elm.getString("type", "cube").equals("cube")){
					poly = new Box(model);
					float[] frm = elm.getArray("from").toFloatArray();
					float[] to = elm.getArray("to").toFloatArray();
					poly.pos.x = frm[0];
					poly.pos.y = frm[1];
					poly.pos.z = frm[2];
					Box box = (Box)poly;
					box.size.x = to[0] - frm[0];
					box.size.y = to[1] - frm[1];
					box.size.z = to[2] - frm[2];
					poly.pos.x -= box.size.x * 0.5;
					poly.pos.y += box.size.y * 0.5;
					poly.pos.z -= box.size.z * 0.5;
					poly.off.x += box.size.x * 0.5;
					poly.off.y -= box.size.y * 0.5;
					poly.off.z += box.size.z * 0.5;
				}
				else continue;
				float[] org = elm.getArray("origin").toFloatArray();
				//poly.pos.x = org[0];
				//poly.pos.y = org[1];
				//poly.pos.z = org[2];
				if(!modent){
					poly.pos.x -= 8;
					poly.pos.z -= 8;
				}
				if(elm.has("rotation")){
					float[] rot = elm.getArray("rotation").toFloatArray();
					poly.rot.x = rot[0];
					poly.rot.y = rot[1];
					poly.rot.z = rot[2];
				}
				poly.name(elm.getString("name", null));
				if(elm.has("uv_offset")){
					int[] uvo = elm.getArray("uv_offset").toIntegerArray();
					poly.textureX = uvo[0];
					poly.textureY = uvo[1];
				}
				else{
					poly.textureX = poly.textureY = 0;
				}
				if(elm.has("faces")){
					for(Map.Entry<String, JsonValue<?>> entry : elm.getMap("faces").entries()){
						if(!entry.getValue().asMap().has("uv")) continue;
						BoxFace face = BoxFace.TOP;
						switch(entry.getKey()){
							case "north":
								face = BoxFace.LEFT;
								break;
							case "south":
								face = BoxFace.RIGHT;
								break;
							case "west":
								face = BoxFace.FRONT;
								break;
							case "east":
								face = BoxFace.BACK;
								break;
							case "down":
								face = BoxFace.TOP;
								break;
							case "up":
							default:
								face = BoxFace.DOWN;
								break;
						}
						JsonArray uv = entry.getValue().asMap().getArray("uv");
						UVCoords uvc = poly.cuv.get(face);
						if(uvc == null) continue;
						uvc.set(UVType.OFFSET_ENDS);
						if(face.index() == 2 || face.index() == 3){
							uvc.value()[0] = uv.get(0).float_value();
							uvc.value()[1] = uv.get(1).float_value();
							uvc.value()[2] = uv.get(2).float_value();
							uvc.value()[3] = uv.get(3).float_value();
						}
						else{
							uvc.value()[0] = uv.get(2).float_value();
							uvc.value()[1] = uv.get(3).float_value();
							uvc.value()[2] = uv.get(0).float_value();
							uvc.value()[3] = uv.get(1).float_value();
						}
					}
				}
				polis.put(elm.get("uuid").string_value(), poly);
			}
			if(map.has("outliner")){
				for(JsonValue<?> jsn : map.getArray("outliner").value){
					if(jsn.isMap()) parseGroup(model, jsn.asMap(), polis);
					else model.add(null, "root", polis.get(jsn.string_value()));
				}
			}
		}
		catch(Throwable e){
			e.printStackTrace();
			return "error: " + e.getMessage();
		}
		return "";
	}

	private void parseGroup(Model model, JsonMap map, LinkedHashMap<String, Polygon> polis){
		String name = map.getString("name", "unnamed");
		if(!model.hasGroup(name)) model.addGroup(null, name);
		Group group = model.get(name);
		for(JsonValue<?> sub : map.getArray("children").value){
			if(sub.isMap()){
				parseGroup(model, sub.asMap(), polis);
			}
			else{
				group.add(polis.get(sub.string_value()));
			}
		}
	}

	@Override
	public String id() {
		return "bbmodel";
	}

	@Override
	public String name() {
		return ".bbmodel (BlockBench)";
	}

	@Override
	public FileChooser.FileType extensions() {
		return TYPE_BBM;
	}

	@Override
	public List<String> categories() {
		return categories;
	}

	@Override
	public List<Setting<?>> settings() {
		return Collections.emptyList();
	}

}
