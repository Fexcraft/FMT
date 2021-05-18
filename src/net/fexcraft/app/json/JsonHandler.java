package net.fexcraft.app.json;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
	
	private static String NUMBER = "^\\d+$";
	private static String FLOATN = "^\\d\\.\\d+$";
	
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
			return parse(Files.readString(file.toPath(), StandardCharsets.UTF_8), defmap);
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
		return parse(buf.toString(StandardCharsets.UTF_8), defmap);
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
		while(index < str.length() && end(str.charAt(index), c) && str.charAt(index - 1) != '\\') index++;
		return str.substring(0, index);
	}

	private static boolean end(char e, char c){
		return e != c && e != ']' && e != '}';
	}

	public static String toString(JsonObject<?> obj){
		return toString(obj, 0, false, false, false);
	}

	public static String toString(JsonObject<?> obj, boolean flat, boolean spaced){
		return toString(obj, 0, false, flat, spaced);
	}

	public static String toString(JsonObject<?> obj, int depth, boolean append, boolean flat, boolean spaced){
		String ret = "", tab = "", tabo = "    ", space = spaced ? " " : "", app = append ? "," + space : "", n = flat ? "" : "\n";
		if(!flat){
			for(int j = 0; j < depth; j++){
				tab += tabo;
			}
		}
		else tabo = "";
		if(obj.isMap()){
			if(obj.asMap().empty()){
				ret += "{}" + app + n;
			}
			else{
				ret += "{" + space + n;
				Iterator<Entry<String, JsonObject<?>>> it = obj.asMap().value.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry<String, JsonObject<?>> entry = it.next();
					ret += tab + tabo + '"' + entry.getKey() + '"' + ":" + space + toString(entry.getValue(), depth + 1, it.hasNext(), flat, spaced);
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
					ret += tab + tabo + toString(it.next(), depth + 1, it.hasNext(), flat, spaced);
				}
				ret += tab + space + "]" + app + n;
			}
		}
		else{
			ret += (obj.value instanceof String ? '"' + obj.value.toString() + '"' : obj.value) + app + n;
		}
		return ret;
	}
	
	private static class Ret {
		
		private final JsonObject<?> obj;
		private final String str;
		
		public Ret(JsonObject<?> obj, String str){
			this.obj = obj;
			this.str = str;
		}
		
	}

	public static void print(File file, JsonObject<?> obj, boolean flat, boolean spaced){
		try{
			Files.writeString(file.toPath(), toString(obj, flat, spaced), StandardCharsets.UTF_8);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public static JsonMap parseURL(String... url){
		// TODO Auto-generated method stub
		return null;
	}

}
