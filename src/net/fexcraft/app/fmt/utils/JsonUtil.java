package net.fexcraft.app.fmt.utils;

import static java.lang.String.format;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.fexcraft.app.fmt.polygon.Vector3F;
import org.joml.Vector3f;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;
import net.fexcraft.lib.common.math.Time;

/**
 * A fresher JSON Utility than the one in FCL.
 * 
 * @author Ferdinand Calo' (FEX___96)
 */
public class JsonUtil {
	
	public static Vector3F getVector(JsonMap obj, String format, float def){
		return new Vector3F(obj.get(format(format, "x"), def), obj.get(format(format, "y"), def), obj.get(format(format, "z"), def));
	}
	
	public static Vector3f getVector(JsonArray array, float def){
		if(array.empty()) return new Vector3f(def);
		return new Vector3f(array.get(0).float_value(), array.get(1).float_value(), array.get(2).float_value());
	}
	
	public static void setVector(JsonMap obj, String format, Vector3f vec){
		if(vec.x != 0f) obj.add(format(format, "x"), vec.x);
		if(vec.y != 0f) obj.add(format(format, "y"), vec.y);
		if(vec.z != 0f) obj.add(format(format, "z"), vec.z);
	}

	public static JsonObject<?> toJson(Object val){
		if(val instanceof Double) return new JsonObject<Float>((float)val);
		if(val instanceof Float) return new JsonObject<Float>((float)val);
		if(val instanceof Long) return new JsonObject<Long>((long)val);
		if(val instanceof Integer) return new JsonObject<Integer>((int)val);
		if(val instanceof Boolean) return new JsonObject<Boolean>((boolean)val);
		//
		return new JsonObject<String>((String)val);
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
			JsonMap map = response.body() == null ? new JsonMap() : JsonHandler.parse(response.body().toString(), true).asMap();
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
					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers(){ return null; }
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType){}
					@Override
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
