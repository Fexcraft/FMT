package net.fexcraft.app.json;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * 
 * Fex's Json Lib
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class JsonHandler {
	
	private static String NUMBER = "^\\-?\\d+$";
	private static String FLOATN = "^\\-?\\d+\\.\\d+$";
	
	public static JsonObject<?> parse(String str, boolean defmap){
		JsonObject<?> root;
		str = str.trim();
		if(str.startsWith("{")){
			root = parseMap(new JsonMap(), str).obj;
		}
		else if(str.startsWith("[")){
			root = parseArray(new JsonArray(), str).obj;
		}
		else return defmap ? new JsonMap() : new JsonArray();
		return root;
	}

	public static JsonObject<?> parse(File file, boolean defmap){
		try{
			return parse(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8), defmap);
		}
		catch(IOException e){
			e.printStackTrace();
			return defmap ? new JsonMap() : new JsonArray();
		}
	}

	public static JsonMap parse(File file){
		return parse(file, true).asMap();
	}

	public static JsonObject<?> parse(InputStream stream, boolean defmap) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(stream);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		for(int res = bis.read(); res != -1; res = bis.read()) buf.write((byte)res);
		return parse(buf.toString(StandardCharsets.UTF_8.name()), defmap);
	}

	public static JsonMap parse(InputStream stream) throws IOException {
		return parse(stream, true).asMap();
	}

	private static Ret parseMap(JsonMap root, String str){
		if(str.startsWith("{")) str = str.substring(1);
		while(str.length() > 0){
			str = str.trim();
			char s = str.charAt(0);
			if(s == '"'){
				String key = scanTill(str, '"');
				str = str.substring(key.length() + 1);
				key = key.substring(1);
				str = str.trim().substring(1).trim();//removing colon
				s = str.charAt(0);
				if(s == '{'){
					Ret ret = parseMap(new JsonMap(), str);
					root.add(key, ret.obj);
					str = ret.str;
				}
				else if(s == '['){
					Ret ret = parseArray(new JsonArray(), str);
					root.add(key, ret.obj);
					str = ret.str;
				}
				else if(s == '"'){
					String val = scanTill(str, '"');
					str = str.substring(val.length() + 1);
					root.add(key, parseValue(val.substring(1)));
				}
				else{
					String val = scanTill(str, ',');
					str = str.substring(val.length());
					root.add(key, parseValue(val));
				}
			}
			else if(s == ',') str = str.substring(1);
			else if(s == '}') break;
			else str = str.substring(1);
		}
		if(str.startsWith("}")) str = str.substring(1);
		return new Ret(root, str);
	}

	private static Ret parseArray(JsonArray root, String str){
		if(str.startsWith("[")) str = str.substring(1);
		while(str.length() > 0){
			str = str.trim();
			char s = str.charAt(0);
			if(s == '"'){
				String val = scanTill(str, '"');
				str = str.substring(val.length() + 1);
				root.add(parseValue(val.substring(1)));
			}
			else if(s == '{'){
				Ret ret = parseMap(new JsonMap(), str);
				root.add(ret.obj);
				str = ret.str;
			}
			else if(s == '['){
				Ret ret = parseArray(new JsonArray(), str);
				root.add(ret.obj);
				str = ret.str;
			}
			else if(s == ',') str = str.substring(1);
			else if(s == ']') break;
			else {
				String val = scanTill(str, ',');
				str = str.substring(val.length());
				root.add(parseValue(val));
			}
		}
		if(str.startsWith("]")) str = str.substring(1);
		return new Ret(root, str);
	}

	private static JsonObject<?> parseValue(String val){
		val = val.trim();
		if(val.equals("null")){
			return new JsonObject<String>(val);//new JsonObject<Object>(null);
		}
		else if(Pattern.matches(NUMBER, val)){
			long leng = Long.parseLong(val);
			if(leng < Integer.MAX_VALUE){
				return new JsonObject<>((int)leng);
			}
			else return new JsonObject<>(leng);
		}
		else if(Pattern.matches(FLOATN, val)){
			return new JsonObject<>(Float.parseFloat(val));
		}
		else if(val.equals("true")) return new JsonObject<>(true);
		else if(val.equals("false")) return new JsonObject<>(false);
		else return new JsonObject<>(val);
	}

	private static String scanTill(String str, char c){
		int index = 1;
		while(index < str.length() && end(str.charAt(index), c) /*&& str.charAt(index - 1) != '\\'*/) index++;
		return str.substring(0, index);
	}

	private static boolean end(char e, char c){
		return e != c && e != ']' && e != '}';
	}

	public static String toString(JsonObject<?> obj){
		return toString(obj, 0, false, PrintOption.DEFAULT);
	}

	public static String toString(JsonObject<?> obj, PrintOption opt){
		return toString(obj, 0, false, opt);
	}

	public static String toString(JsonObject<?> obj, int depth, boolean append, PrintOption opt){
		String ret = "", tab = "", tabo = "    ", space = opt.spaced ? " " : "", colspace = !opt.flat || opt.spaced ? " " : "";
		String app = append ? "," + space : "", n = opt.flat ? "" : "\n";
		if(!opt.flat){
			for(int j = 0; j < depth; j++){
				tab += tabo;
			}
		}
		else tabo = "";
		if(obj == null){
			ret += "[ \"null\" ]";
		}
		else if(obj.isMap()){
			if(obj.asMap().empty()){
				ret += "{}" + app + n;
			}
			else{
				ret += "{" + space + n;
				Iterator<Entry<String, JsonObject<?>>> it = obj.asMap().value.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry<String, JsonObject<?>> entry = it.next();
					ret += tab + tabo + '"' + entry.getKey() + '"' + ":" + colspace + toString(entry.getValue(), depth + 1, it.hasNext(), opt);
				}
				ret += tab + space + "}" + app + n;
			}
		}
		else if(obj.isArray()){
			if(obj.asArray().empty()){
				ret += "[]" + app + n;
			}
			else{
				ret += "[" + space + n;
				Iterator<JsonObject<?>> it = obj.asArray().value.iterator();
				while(it.hasNext()){
					ret += tab + tabo + toString(it.next(), depth + 1, it.hasNext(), opt);
				}
				ret += tab + space + "]" + app + n;
			}
		}
		else{
			ret += (obj.value instanceof String ? '"' + obj.value.toString() + '"' : obj.value) + app + n;
		}
		return ret;
	}
	
	public static class PrintOption {
		
		public static final PrintOption FLAT = new PrintOption().flat(true).spaced(false);
		public static final PrintOption SPACED = new PrintOption().flat(false).spaced(true);
		public static final PrintOption DEFAULT = SPACED;
		
		boolean flat, spaced;
		
		public PrintOption(){}
		
		public PrintOption flat(boolean bool){
			flat = bool;
			return this;
		}
		
		public PrintOption spaced(boolean bool){
			spaced = bool;
			return this;
		}
		
	}
	
	private static class Ret {
		
		private final JsonObject<?> obj;
		private final String str;
		
		public Ret(JsonObject<?> obj, String str){
			this.obj = obj;
			this.str = str;
		}
		
	}

	public static void print(File file, JsonObject<?> obj, PrintOption opt){
		try{
			Files.write(file.toPath(), toString(obj, opt).getBytes(StandardCharsets.UTF_8));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public static JsonMap parseURL(String... adr){
		try{
			URL url = new URL(adr[0]);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod(adr.length > 1 ? "POST" : "GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setConnectTimeout(10000);
			connection.setDoOutput(adr.length > 1);
			if(adr.length > 1){
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(adr[1]);
				wr.flush();
				wr.close();
			}
			//
			JsonMap obj = parse(connection.getInputStream());
			connection.disconnect();
			return obj;
		}
		catch(IOException e){
			e.printStackTrace();
			return new JsonMap();
		}
	}

	public static JsonMap wrap(Map<String, Object> map, JsonMap json){
		if(json == null) json = new JsonMap();
		for(Entry<String, Object> entry : map.entrySet()){
			if(entry.getValue() instanceof Collection){
				json.add(entry.getKey(), wrap((Collection<?>)entry.getValue(), null));
			}
			else if(entry.getValue() instanceof Map){
				json.add(entry.getKey(), wrap((Map<String, Object>)entry.getValue(), null));
			}
			else if(entry.getValue() instanceof String){
				json.add(entry.getKey(), entry.getValue() + "");
			}
			else json.add(entry.getKey(), parseValue(entry.getValue() + ""));
		}
		return json;
	}

	public static JsonArray wrap(Collection<?> collection, JsonArray json){
		if(json == null) json = new JsonArray();
		for(Object obj : collection){
			if(obj instanceof Collection){
				json.add(wrap((Collection<?>)obj, null));
			}
			else if(obj instanceof Map){
				json.add(wrap((Map<String, Object>)obj, null));
			}
			else if(obj instanceof String){
				json.add(obj + "");
			}
			else json.add(parseValue(obj + ""));
		}
		return json;
	}

	public static Object dewrap(String obj){
		return dewrap(parse(obj, true).asMap());
	}

	public static HashMap<String, Object> dewrap(JsonMap map){
		HashMap<String, Object> hashmap = new HashMap<>();
		for(Entry<String, JsonObject<?>> entry : map.entries()){
			if(entry.getValue().isMap()){
				hashmap.put(entry.getKey(), dewrap(entry.getValue().asMap()));
			}
			else if(entry.getValue().isArray()){
				hashmap.put(entry.getKey(), dewrap(entry.getValue().asArray()));
			}
			else{
				hashmap.put(entry.getKey(), entry.getValue().value);
			}
		}
		return hashmap;
	}

	public static ArrayList<Object> dewrap(JsonArray array){
		ArrayList<Object> list = new ArrayList<>();
		for(JsonObject<?> obj : array.value){
			if(obj.isMap()){
				list.add(dewrap(obj.asMap()));
			}
			else if(obj.isArray()){
				list.add(dewrap(obj.asArray()));
			}
			else{
				list.add(obj.value);
			}
		}
		return list;
	}

	public static <T> ArrayList<T> dewrapc(JsonArray array){
		ArrayList<Object> list = new ArrayList<>();
		for(JsonObject<?> obj : array.value){
			if(obj.isMap()){
				list.add(dewrap(obj.asMap()));
			}
			else if(obj.isArray()){
				list.add(dewrap(obj.asArray()));
			}
			else{
				list.add(obj.value);
			}
		}
		return (ArrayList<T>)list;
	}

}
