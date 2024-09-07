package net.fexcraft.app.fmt;

import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

import java.io.File;
import java.util.Map;

import static net.fexcraft.lib.common.Static.sixteenth;

public class Converter {

	private static final File ROOT = new File(".");

	public static void run(){
		searchFolder(ROOT);
		System.exit(0);
	}

	private static void searchFolder(File root){
		for(File file : root.listFiles()){
			if(file.isDirectory()) searchFolder(file);
			else checkFile(file);
		}
	}

	private static void checkFile(File file){
		if(!file.getName().endsWith(".vehicle")) return;
		try {
			Logging.log("Found " + file + ", fixing.");
			JsonMap map = JsonHandler.parse(file).asMap();
			File old = new File(file.getParentFile(), file.getName() + ".old");
			if(!old.exists()){
				JsonHandler.print(old, map, JsonHandler.PrintOption.DEFAULT);
			}
			else{
				map = JsonHandler.parse(old);
			}
			if(map.has("RegistryName")){
				String id = map.get("RegistryName").string_value().split(":")[1];
				map.rem("RegistryName");
				map.add("ID", id);
			}
			if(map.has("Addon") && map.getString("Addon", "").contains(":")){
				map.add("Addon", map.get("Addon").string_value().split(":")[1]);
			}
			if(!map.has("ColorChannels")){
				String pri = map.getString("PrimaryColor", "#ffffff");
				String sec = map.getString("SecondaryColor", "#ffffff");
				map.add("ColorChannels", new JsonMap("primary", pri, "secondary", sec));
			}
			map.rem("PrimaryColor");
			map.rem("SecondaryColor");
			if(map.has("Attributes") && map.get("Attributes").isArray()){
				JsonArray attrs = map.getArray("Attributes");
				JsonMap attr = new JsonMap();
				for(JsonValue<?> val : attrs.value){
					if(!val.isMap()) continue;
					JsonMap av = val.asMap();
					if(!av.has("id")) continue;
					if(av.has("seat")){
						av.add("access", new JsonArray(av.get("seat").string_value()));
						av.rem("seat");
					}
					if(av.has("hitbox")){
						av.add("interact", av.get("hitbox"));
						av.rem("hitbox");
					}
					if(av.has("external") && av.get("external").bool()){
						if(!av.has("access")) av.addArray("access");
						av.getArray("access").add("external");
					}
					attr.add(av.getString("id", null), av);
					av.rem("id");
				}
				map.add("Attributes", attr);
			}
			if(map.has("WheelPositions") && map.get("WheelPositions").isArray()){
				JsonArray wparr = map.getArray("WheelPositions");
				JsonMap wmap = new JsonMap();
				for(JsonValue<?> val : wparr.value){
					if(!val.isMap()) continue;
					JsonMap wv = val.asMap();
					if(!wv.has("id")) continue;
					float x = wv.getFloat("x", 0f);
					float y = wv.getFloat("y", 0f);
					float z = wv.getFloat("z", 0f);
					wv.rem("x");
					wv.rem("y");
					wv.rem("z");
					wv.add("pos", new JsonArray(-z * sixteenth, -y * sixteenth, -x * sixteenth));
					if(wv.has("y_rot")){
						if(wv.getInteger("y_rot", 0) != 0){
							wv.add("mirror", true);
						}
						wv.rem("y_rot");
					}
					if(wv.has("drive")){
						wv.add("powered", wv.get("drive"));
						wv.rem("drive");
					}
					if(wv.has("radius")){
						wv.add("radius", wv.get("radius").float_value() * sixteenth);
					}
					if(wv.has("width")){
						wv.add("width", wv.get("width").float_value() * sixteenth);
					}
					wmap.add(wv.getString("id", null), wv);
					wv.rem("id");
				}
				map.add("WheelPositions", wmap);
			}
			if(map.has("PreInstalled")){
				JsonMap pre = map.getMap("PreInstalled");
				JsonMap inp = new JsonMap();
				for(Map.Entry<String, JsonValue<?>> entry : pre.entries()){
					if(entry.getKey().contains("front_wheel") || entry.getKey().contains("back_wheel")){
						inp.add(entry.getKey(), entry.getValue());
					}
					else inp.add("vehicle:" + entry.getKey(), entry.getValue());
				}
				map.add("InstalledParts", inp);
				map.rem("PreInstalled");
			}
			if(map.has("LegacyData")){
				map.add("SimplePhysics", map.get("LegacyData"));
				map.rem("LegacyData");
			}
			if(map.has("LiftingPoints")){
				JsonMap poi = map.getMap("LiftingPoints");
				for(Map.Entry<String, JsonValue<?>> po : poi.entries()){
					JsonArray pe = po.getValue().asArray();
					float x = pe.get(0).float_value();
					float y = pe.get(1).float_value();
					float z = pe.get(2).float_value();
					pe.set(0, new JsonValue<>(-z * sixteenth));
					pe.set(1, new JsonValue<>(-y * sixteenth));
					pe.set(2, new JsonValue<>(-x * sixteenth));
				}
			}
			if(map.has("PartSlots") && map.get("PartSlots").isArray()){
				JsonArray pas = map.getArray("PartSlots");
				JsonMap psl = new JsonMap();
				for(JsonValue<?> val : pas.value){
					JsonArray var = val.asArray();
					float x = var.get(0).float_value();
					float y = var.get(1).float_value();
					float z = var.get(2).float_value();
					String cat = var.get(4).string_value();
					var.set(0, new JsonValue<>(-z * sixteenth));
					var.set(1, new JsonValue<>(-y * sixteenth));
					var.set(2, new JsonValue<>(-x * sixteenth));
					var.rem(4);
					psl.add(cat, var);
				}

				map.add("PartSlots", psl);
			}
			if(map.has("RequiredParts")){
				JsonArray req = map.getArray("RequiredParts");
				if(!map.has("PartSlots")) map.addMap("PartSlots");
				JsonMap pas = map.getMap("PartSlots");
				for(JsonValue<?> val : req.value){
					String str = val.string_value();
					if(str.contains("front_wheel") || str.contains("back_wheel")) continue;
					pas.addMap(str);
				}
				map.rem("RequiredParts");
			}
			if(map.has("SwivelPoints") && map.get("SwivelPoints").isArray()){
				JsonArray arr = map.getArray("SwivelPoints");
				JsonMap spt = new JsonMap();
				for(JsonValue<?> val : arr.value){
					if(!val.isMap()) continue;
					JsonMap sv = val.asMap();
					if(!sv.has("id")) continue;
					if(sv.has("pos")){
						JsonArray pos = sv.getArray("pos");
						float x = pos.get(0).float_value();
						float y = pos.get(1).float_value();
						float z = pos.get(2).float_value();
						pos.set(0, new JsonValue<>(-z * sixteenth));
						pos.set(1, new JsonValue<>(-y * sixteenth));
						pos.set(2, new JsonValue<>(-x * sixteenth));
					}
					spt.add(sv.getString("id", null), sv);
					sv.rem("id");
				}
				map.add("SwivelPoints", spt);
			}
			JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
