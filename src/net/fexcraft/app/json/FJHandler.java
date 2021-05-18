package net.fexcraft.app.json;

import java.io.File;
import java.io.IOException;
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
public class FJHandler {
	
	private static String NUMBER = "^\\d+$";
	private static String FLOATN = "^\\d\\.\\d+$";
	
	public static FJObject<?> parse(String str, boolean defmap){
		FJObject<?> root;
		str = str.trim();
		if(str.startsWith("{")){
			root = parseMap(new FJMap(), str).obj;
		}
		else if(str.startsWith("[")){
			root = parseArray(new FJArray(), str).obj;
		}
		else return defmap ? new FJMap() : new FJArray();
		return root;
	}

	public static FJObject<?> parse(File file, boolean defmap){
		try{
			return parse(Files.readString(file.toPath()), defmap);
		}
		catch(IOException e){
			e.printStackTrace();
			return new FJMap();
		}
	}

	public static FJMap parse(File file){
		return parse(file, true).asMap();
	}

	private static Ret parseMap(FJMap root, String str){
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
					Ret ret = parseMap(new FJMap(), str);
					root.add(key, ret.obj);
					str = ret.str;
				}
				else if(s == '['){
					Ret ret = parseArray(new FJArray(), str);
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

	private static Ret parseArray(FJArray root, String str){
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
				Ret ret = parseMap(new FJMap(), str);
				root.add(ret.obj);
				str = ret.str;
			}
			else if(s == '['){
				Ret ret = parseArray(new FJArray(), str);
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

	private static FJObject<?> parseValue(String val){
		val = val.trim();
		if(val.equals("null")){
			return new FJObject<Object>(null);
		}
		else if(Pattern.matches(NUMBER, val)){
			long leng = Long.parseLong(val);
			if(leng < Integer.MAX_VALUE){
				return new FJObject<>((int)leng);
			}
			else return new FJObject<>(leng);
		}
		else if(Pattern.matches(FLOATN, val)){
			return new FJObject<>(Float.parseFloat(val));
		}
		else if(val.equals("true")) return new FJObject<>(true);
		else if(val.equals("false")) return new FJObject<>(false);
		else return new FJObject<>(val);
	}

	private static String scanTill(String str, char c){
		int index = 1;
		while(index < str.length() && end(str.charAt(index), c) && str.charAt(index - 1) != '\\') index++;
		return str.substring(0, index);
	}

	private static boolean end(char e, char c){
		return e != c && e != ']' && e != '}';
	}

	public static String toString(FJObject<?> obj){
		return toString(obj, 0, false);
	}

	public static String toString(FJObject<?> obj, int depth, boolean append){
		String ret = "", tab = "", tabo = "    ", app = append ? "," : "";
		for(int j = 0; j < depth; j++){
			tab += tabo;
		}
		if(obj.isMap()){
			if(obj.asMap().empty()){
				ret += "{}" + app + "\n";
			}
			else{
				ret += "{\n";
				Iterator<Entry<String, FJObject<?>>> it = obj.asMap().value.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry<String, FJObject<?>> entry = it.next();
					ret += tab + tabo + '"' + entry.getKey() + '"' + ": " + toString(entry.getValue(), depth + 1, it.hasNext());
				}
				ret += tab + "}" + app + "\n";
			}
		}
		else if(obj.isArray()){
			if(obj.asArray().empty()){
				ret += "[]" + app + "\n";
			}
			else{
				ret += "[\n";
				Iterator<FJObject<?>> it = obj.asArray().value.iterator();
				while(it.hasNext()){
					ret += tab + tabo + toString(it.next(), depth + 1, it.hasNext());
				}
				ret += tab + "]" + app + "\n";
			}
		}
		else{
			ret += (obj.value instanceof String ? '"' + obj.value.toString() + '"' : obj.value) + app + "\n";
		}
		return ret;
	}
	
	private static class Ret {
		
		private final FJObject<?> obj;
		private final String str;
		
		public Ret(FJObject<?> obj, String str){
			this.obj = obj;
			this.str = str;
		}
		
	}

}
