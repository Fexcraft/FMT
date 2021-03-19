package net.fexcraft.app.fmt.utils;

import static java.lang.String.format;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * A fresher JSON Utility than the one in FCL.
 * 
 * @author Ferdinand Calo' (FEX___96)
 */
public class Jsoniser {
	
	private static final JsonParser parser = new JsonParser();
	public static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();
	public static final Gson NRGSON = new GsonBuilder().create();
	
	public static JsonElement parse(String string, boolean errprt){
		if(string == null) return null;
		try{
			return parser.parse(string);
		}
		catch(Exception e){
			if(errprt) e.printStackTrace();
			return null;
		}
	}
	
	public static JsonElement parse(String string){
		return parse(string, false);
	}
	
	public static JsonObject parseObj(String string, boolean errprt){
		JsonElement elm = parse(string, errprt);
		if(elm == null || !elm.isJsonObject()) return null;
		return elm.getAsJsonObject();
	}
	
	public static JsonObject parseObj(String string){
		return parseObj(string, false);
	}
	
	public static JsonArray parseArr(String string, boolean errprt){
		JsonElement elm = parse(string, errprt);
		if(elm == null || !elm.isJsonArray()) return null;
		return elm.getAsJsonArray();
	}
	
	public static JsonArray parseArr(String string){
		return parseArr(string, false);
	}
	
	public static JsonElement parse(File file, boolean crtprnt, boolean errprt){
		try{
			if(!file.getParentFile().exists()){
				if(crtprnt) file.getParentFile().mkdirs();
				else return null;
			}
			FileReader fr = new FileReader(file);
			JsonElement obj = parser.parse(fr);
			fr.close();
			return obj;
		}
		catch (Exception e){
			if(errprt) e.printStackTrace();
			//Print.console("Failed parsing " + file);
			return null;
		}
	}
	
	//
	
	public static JsonElement parse(File file, boolean errprt){
		return parse(file, true, errprt);
	}
	
	public static JsonElement parse(File file){
		return parse(file, true, false);
	}
	
	public static JsonObject parseObj(File file, boolean crtprnt, boolean errprt){
		JsonElement elm = parse(file, crtprnt, errprt);
		return elm == null ? null : elm.getAsJsonObject();
	}
	
	public static JsonObject parseObj(File file, boolean errprt){
		return parseObj(file, true, errprt);
	}
	
	public static JsonObject parseObj(File file){
		return parseObj(file, true, false);
	}
	
	//
	
	public static String print(JsonElement elm, boolean pretty){
		return (pretty ? PRETTY : NRGSON).toJson(elm);
	}
	
	public static void print(File file, JsonElement elm, Gson printer, boolean errprt){
		try{
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
			FileWriter fw = new FileWriter(file);
			fw.write(printer.toJson(elm));
			fw.flush();
			fw.close();
		}
		catch(IOException e){
			if(errprt) e.printStackTrace();
		}
	}
	
	public static void print(File file, JsonElement elm, boolean pretty, boolean errprt){
		print(file, elm, pretty ? PRETTY : NRGSON, errprt);
	}
	
	public static void print(File file, JsonElement elm, boolean pretty){
		print(file, elm, pretty, false);
	}
	
	public static void print(File file, JsonElement elm){
		print(file, elm, true, false);
	}
	
	//
	
	public static double get(JsonObject obj, String member, double def){
		return obj.has(member) ? obj.get(member).getAsDouble() : def;
	}
	
	public static float get(JsonObject obj, String member, float def){
		return obj.has(member) ? obj.get(member).getAsFloat() : def;
	}
	
	public static long get(JsonObject obj, String member, long def){
		return obj.has(member) ? obj.get(member).getAsLong() : def;
	}
	
	public static int get(JsonObject obj, String member, int def){
		return obj.has(member) ? obj.get(member).getAsInt() : def;
	}
	
	public static boolean get(JsonObject obj, String member, boolean def){
		return obj.has(member) ? obj.get(member).getAsBoolean() : def;
	}
	
	public static String get(JsonObject obj, String member, String def){
		return obj.has(member) ? obj.get(member).getAsString() : def;
	}

	public static Object get(JsonObject obj, String member, Object def){
		if(obj == null) return def;
		if(def instanceof Double) return get(obj, member, (double)def);
		if(def instanceof Float) return get(obj, member, (float)def);
		if(def instanceof Long) return get(obj, member, (long)def);
		if(def instanceof Integer) return get(obj, member, (int)def);
		if(def instanceof Boolean) return get(obj, member, (boolean)def);
		//
		return get(obj, member, (String)def);
	}
	
	public static Vector3f getVector(JsonObject obj, String format, float def){
		return new Vector3f(get(obj, format(format, "x"), def), get(obj, format(format, "y"), def), get(obj, format(format, "z"), def));
	}

	public static JsonElement toJson(Object val){
		if(val instanceof Double) return new JsonPrimitive((double)val);
		if(val instanceof Float) return new JsonPrimitive((float)val);
		if(val instanceof Long) return new JsonPrimitive((long)val);
		if(val instanceof Integer) return new JsonPrimitive((int)val);
		if(val instanceof Boolean) return new JsonPrimitive((boolean)val);
		//
		return new JsonPrimitive((String)val);
	}

	public static JsonObject getSubObj(JsonObject obj, String sub){
		if(!obj.has(sub)) obj.add(sub, new JsonObject());
		return obj.get(sub).getAsJsonObject();
	}
	
}
