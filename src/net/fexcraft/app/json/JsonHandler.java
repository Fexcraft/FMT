package net.fexcraft.app.json;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.lib.common.math.Time;

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
		while(index < str.length() && end(str.charAt(index), c) /*&& str.charAt(index - 1) != '\\'*/) index++;
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
		String ret = "", tab = "", tabo = "    ", space = spaced ? " " : "", colspace = !flat || spaced ? " " : "";
		String app = append ? "," + space : "", n = flat ? "" : "\n";
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
					ret += tab + tabo + '"' + entry.getKey() + '"' + ":" + colspace + toString(entry.getValue(), depth + 1, it.hasNext(), flat, spaced);
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
	
	public static JsonMap parseURLwithCookies(String adress, String parameters, String... cookies){
		return parseURLwithCookies(adress, parameters, (int)Time.SEC_MS * 10, cookies);
	}
	
	public static JsonMap parseURLwithCookies(String adress, String parameters, int timeout, String... cookies){
		try{
			String cookie = null;
			if(cookies != null && cookies.length > 0){
				String str = "";
				for(int i = 0; i < cookies.length; i++){
					str += cookies[i];
					if(i != cookies.length - 1){
						str += "; ";
					}
				}
				cookie = str;
			}
			HttpClient client = HttpClient.newBuilder().sslContext(ctx).sslParameters(param).build();
		    Builder request = HttpRequest.newBuilder().uri(URI.create(adress + "?" + parameters)).setHeader("User-Agent", "Mozilla/5.0").setHeader("Accept-Language", "en-US,en;q=0.5").GET();
		    if(cookie != null) request.setHeader("Cookie", cookie);
		    HttpResponse<?> response = client.send(request.build(), BodyHandlers.ofString());
			JsonMap cook = new JsonMap();
			if(response.headers().map().containsKey("Set-Cookie")){
				List<String> vals = response.headers().allValues("Set-Cookie");
				for(String val : vals){
					String[] fields = val.split(";\\s*"), split;
					for(String str : fields){
						if((split = str.split("=")).length >= 2){
							cook.add(split[0], split[1]);
						}
					}
				}
			}
			JsonMap map = response.body() == null ? new JsonMap() : parse(response.body().toString(), true).asMap();
			if(cook.entries().size() > 0) map.add(map.has("cookies") ? "%http:cookies%" : "cookies", cook);
		    return map;
		}
		catch(Exception e){
			Logging.log(e);
			return new JsonMap();
		}
	}
	
	private static SSLContext ctx;
	private static SSLParameters param;
	static {
		try{
			TrustManager[] tm = new TrustManager[]{
				new X509TrustManager(){
					public java.security.cert.X509Certificate[] getAcceptedIssuers(){ return null; }
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType){}
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType){}
				}
			};
			ctx = SSLContext.getInstance("SSL");
			ctx.init(null, tm, new java.security.SecureRandom());
			param = new SSLParameters();
			param.setEndpointIdentificationAlgorithm("");
		}
		catch(Exception e){
			Logging.log(e);
		}
	}

}
