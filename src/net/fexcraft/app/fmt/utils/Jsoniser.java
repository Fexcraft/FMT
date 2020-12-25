package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
	
}
