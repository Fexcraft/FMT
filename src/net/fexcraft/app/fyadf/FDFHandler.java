package net.fexcraft.app.fyadf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * "Fex's Yet Another Data Format"
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FDFHandler {
	
	private static Pattern VALROW = Pattern.compile("(.*)=(.+)");
	private static String NUMBER = "^\\d+$";
	private static String FLOATN = "^\\d\\.\\d+$";
	
	/** Currently multiline arrays only into the main map. */
	public static FDFMap parse(String str){
		FDFMap map = new FDFMap();
		String[] arr = str.split("\n");
		boolean inarr = false;
		FDFArray array = null;
		for(String row : arr){
			if(row.startsWith("#") || row.startsWith("//")) continue;
			if(inarr){
				if(row.startsWith("]")){
					array = null;
					inarr = false;
				}
				else{
					array.add(parseArray(row.trim()));
				}
				continue;
			}
			Matcher matcher = VALROW.matcher(row);
			if(!matcher.matches()) continue;
			String key = matcher.group(1).trim();
			String val = matcher.group(2).trim();
			if(val.length() == 1 && val.charAt(0) == '['){
				map.add(key, array = new FDFArray());
				inarr = true;
			}
			else if(val.startsWith("[")){
				if(inarr) array.add(parseArray(val));
				else map.add(key, parseArray(val));
			}
			else{
				if(inarr) array.add(parseValue(val));
				else map.add(key, parseValue(val));
			}
		}
		return map;
	}

	public static FDFMap parse(File file){
		try{
			return parse(Files.readString(file.toPath()));
		}
		catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}

	private static FDFObject<?> parseValue(String val){
		if(val.equals("null")){
			return new FDFObject<Object>(null);
		}
		else if(Pattern.matches(NUMBER, val)){
			long leng = Long.parseLong(val);
			if(leng < Integer.MAX_VALUE){
				return new FDFObject<>((int)leng);
			}
			else return new FDFObject<>(leng);
		}
		else if(Pattern.matches(FLOATN, val)){
			return new FDFObject<>(Float.parseFloat(val));
		}
		else if(val.equals("true")) return new FDFObject<>(true);
		else if(val.equals("false")) return new FDFObject<>(false);
		else return new FDFObject<>(val);
	}

	private static FDFArray parseArray(String str){
		if(str.charAt(0) == '[') str = str.substring(1);
		if(str.charAt(str.length() - 1) == ']') str = str.substring(0, str.length() - 2);
		FDFArray array = new FDFArray();
		while(str.length() > 0){
			str = str.trim();
			char s = str.charAt(0);
			if(s == '['){
				String arr = scanTill(str, ']');
				str = str.substring(arr.length() + 1);
				array.add(parseArray(arr));
			}
			else if(s == '"'){
				String arr = scanTill(str, '"');
				str = str.substring(arr.length() + 1);
				array.add(parseValue(arr.substring(1, arr.length())));
			}
			else if(s == ',') str = str.substring(1);
			else{
				String arr = scanTill(str, ',');
				str = str.substring(arr.length());
				array.add(parseValue(arr.substring(0, arr.length())));
			}
		}
		return array;
	}

	private static String scanTill(String str, char c){
		int index = 1;
		while(index < str.length() && str.charAt(index) != c && str.charAt(index - 1) != '\\') index++;
		return str.substring(0, index);
	}

	public static String toString(FDFObject<?> obj){
		return toString(obj, 0);
	}

	public static String toString(FDFObject<?> obj, int depth){
		String ret = "", tab = "", tabo = "    ";
		for(int j = 0; j < depth; j++){
			tab += tabo;
		}
		if(obj.isMap()){
			ret += "{\n";
			for(Map.Entry<String, FDFObject<?>> entry : obj.asMap().value.entrySet()){
				ret += tab + tabo + entry.getKey() + " = " + toString(entry.getValue(), depth + 1);
			}
			ret += tab + "}\n";
		}
		else if(obj.isArray()){
			ret += "[\n";
			for(int i = 0; i < obj.asArray().size(); i++){
				ret += tab + tabo + i + " - " + toString(obj.asArray().get(i), depth + 1);
			}
			ret += tab + "]\n";
		}
		else{
			ret += obj.value + "\n";
		}
		return ret;
	}

}
