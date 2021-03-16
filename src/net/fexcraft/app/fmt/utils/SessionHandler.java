package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.util.Base64;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.utils.HttpUtil;

public class SessionHandler {
	
	private static boolean loggedin, encrypt;
	private static String sessionid, pass, usermail, username, userid;
	private static String perm = "free", permname = "Public Version";
	
	public static void load(){
		log("Loading auth data from FILE.");
		File file = new File("./auth.net");
		JsonObject obj = JsonUtil.get(file);
		sessionid = obj.has("session") ? obj.get("session").getAsString() : null;
		encrypt = JsonUtil.getIfExists(obj, "encrypt", false);
		pass = JsonUtil.getIfExists(obj, "pass", "testuser");
		usermail = JsonUtil.getIfExists(obj, "mail", "testuser@fexcraft.net");
		userid = JsonUtil.getIfExists(obj, "userid", "-1");
		username = JsonUtil.getIfExists(obj, "username", "Unregistered Account");
		if(!file.exists()) JsonUtil.write(file, obj);
	}

	public static void save(){
		log("Saving auth data to FILE.");
		JsonObject obj = new JsonObject();
		if(sessionid != null) obj.addProperty("session", sessionid);
		obj.addProperty("encrypt", encrypt);
		if(pass != null) obj.addProperty("pass", pass);
		if(usermail != null) obj.addProperty("mail", usermail);
		if(userid != null) obj.addProperty("userid", userid);
		if(username != null) obj.addProperty("username", username);
		if(perm != null) obj.addProperty("perm", perm);
		if(permname != null) obj.addProperty("perm-name", permname);
		JsonUtil.write(new File("./auth.net"), obj);
	}
	
	public static void checkIfLoggedIn(boolean retry, boolean first){
		log("Controlling session/login data...");
		if(first) load();
		JsonObject obj = HttpUtil.request("http://fexcraft.net/session/api", "r=status", getCookieArr());
		if(obj != null && obj.has("success")){
			loggedin = obj.has("guest") && !obj.get("guest").getAsBoolean();
			userid = JsonUtil.getIfExists(obj, "user", "-1");
			if(obj.has("banned") && obj.get("banned").getAsBoolean()){
				log("Banned account detected, causing a commotion.");
				System.exit(-1); System.exit(1); System.exit(1);
			}
		}
		if(loggedin){
			log("Fetching Username...");
			obj = HttpUtil.request("http://fexcraft.net/session/api", "r=username&id=" + userid, getCookieArr());
			if(obj.has("name")) username = obj.get("name").getAsString();
			log("Username updated to: " + username);
			if(first) log(">>>> Welcome back! <<<<");
			obj = HttpUtil.request("http://fexcraft.net/session/api", "r=fmt_status", getCookieArr());
			if(obj != null && obj.has("license") && obj.has("license_title")){
				perm = obj.get("license").getAsString();
				permname = obj.get("license_title").getAsString();
				log("License updated to: " + permname + " (" + perm + ")");
			}
		}
		else if(retry){
			if(!first) load();
			log("Trying to re-login...");
			sessionid = null;
			tryLogin(null);
			if(!loggedin){
				log("Relogin seems to have failed.");
				userid = "-1"; username = "Guest";
			}
		}
	}
	
	private static String[] getCookieArr(){
		return sessionid == null ? null : new String[]{ "PHPSESSID=" + sessionid };
	}
	
	public static String tryLogin(Consumer<String> cons){
		String response;
		try{
			//TODO http :: find solution with the certs javax can't process
			JsonObject obj = HttpUtil.request("http://fexcraft.net/session/api", "r=login&m=" + usermail + "&p=" + decrypt(), getCookieArr());
			if(obj == null){
				log(response = "Invalid/Empty login response, aborting.");
				return response;
			}
			if(obj.has("cookies") && obj.get("cookies").getAsJsonObject().has("PHPSESSID")){
				sessionid = obj.get("cookies").getAsJsonObject().get("PHPSESSID").getAsString();
				log("Updated Session ID to: " + sessionid);
			}
			loggedin = obj.has("success") && obj.get("success").getAsBoolean();
			response = obj.has("status") ? obj.get("status").getAsString() : "api:success=" + loggedin;
			log("Login Response: " + response);
			checkIfLoggedIn(false, false);
		}
		catch(Exception e){
			response = "Error: " + e.getMessage();
			log(e);
			loggedin = false;
		}
		if(cons != null) cons.accept(response);
		return response;
	}
	
	public static void tryLogout(){
		JsonObject obj = HttpUtil.request("http://fexcraft.net/session/api", "r=logout", getCookieArr());
		log("Logout Response: " + obj.toString());
		username = /*usermail =*/ userid = "";
		pass = perm = permname = null;
		//encrypted = false;
		save();
	}
	
	public static boolean isLoggedIn(){
		return loggedin;
	}

	public static String getUserId(){
		return userid;
	}

	public static String getUserName(){
		return username;
	}

	public static String getUserMail(){
		return usermail;
	}
	
	public static String getLicenseStatus(){
		return perm;
	}
	
	public static String getLicenseName(){
		return permname;
	}
	
	public static String getLicenseTitle(){
		return permname + " (" + perm + ")";
	}

	public static String getPassWord(){
		return pass;
	}

	public static boolean shouldEncrypt(){
		return encrypt;
	}
	
	public static void openRegister(){
		FMTB.openLink("https://fexcraft.net/register");
	}

	public static boolean toggleEncrypt(){
		return encrypt = !encrypt;
	}

	public static void updatePassword(String newValue){
		pass = newValue;
	}

	public static void updateUserMail(String newValue){
		usermail = newValue;
	}

	public static void encrypt(){
		if(!shouldEncrypt()) return;
		/*JsonObject obj = HttpUtil.request("http://fexcraft.net/session/api", "r=encrypt&raw=" + hashpw, getCookieArr());
		if(obj == null){
			log("No encryption response from server, password could not be saved encrypted locally.");
			return;
		}
		if(obj.has("success") && obj.get("success").getAsBoolean() && obj.has("result")){
			hashpw = obj.get("result").getAsString();
			log("Received Hashed/Encrypted Password version from server.");
		}
		else log(obj.has("status") ? "SRV-RESP: " + obj.get("status").getAsString() : "Unknown Error on server-side while requesting password encryption, status returned as 'success:false'!");*/
		pass = Base64.getEncoder().encodeToString(pass.getBytes());
		log("Applied client side base encryption to password.");
	}
	
	// generic code based on stackoverflow, as long it's not plain readable that's fine
	
	public static String decrypt(){
		if(!shouldEncrypt()) return pass;
		return new String(Base64.getDecoder().decode(pass));
	}

}
