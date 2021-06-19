package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static net.fexcraft.app.json.JsonHandler.parseURLwithCookies;

import java.io.File;
import java.util.Base64;
import java.util.function.Consumer;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;

public class SessionHandler {

	private static boolean loggedin, encrypt;
	private static String sessionid, pass, usermail, username, userid;
	private static String perm = "free", permname = "Public Version";
	private static String defusername = "Unregistered Account";
	private static String defperm = perm, defpermname = permname;

	public static void load(){
		log("Loading auth data from FILE.");
		File file = new File("./auth.net");
		JsonMap map = JsonHandler.parse(file);
		sessionid = map.getString("session", null);
		encrypt = map.getBoolean("encrypt", false);
		pass = map.getString("pass", "testuser");
		usermail = map.getString("mail", "testuser@fexcraft.net");
		userid = map.getString("userid", "-1");
		username = map.getString("username", defusername);
		if(!file.exists()) JsonHandler.print(file, map, true, false);
	}

	public static void save(){
		log("Saving auth data to FILE.");
		JsonMap map = new JsonMap();
		if(sessionid != null) map.add("session", sessionid);
		map.add("encrypt", encrypt);
		if(pass != null) map.add("pass", pass);
		if(usermail != null) map.add("mail", usermail);
		if(userid != null) map.add("userid", userid);
		if(username != null) map.add("username", username);
		if(perm != null) map.add("perm", perm);
		if(permname != null) map.add("perm-name", permname);
		JsonHandler.print(new File("./auth.net"), map, true, false);
	}

	public static void checkIfLoggedIn(boolean retry, boolean first){
		log("Verifying session/login data...");
		if(first) load();
		JsonMap map = parseURLwithCookies("https://fexcraft.net/session/api", "r=status", getCookieArr());
		if(map != null && map.has("success")){
			loggedin = !map.getBoolean("guest", true);
			userid = map.getString("user", "-1");
			if(map.getBoolean("banned", false)){
				log("Banned account detected, exiting.");
				System.exit(-1); System.exit(1); System.exit(1);
			}
		}
		if(loggedin){
			log("Fetching Username...");
			map = parseURLwithCookies("https://fexcraft.net/session/api", "r=username&id=" + userid, getCookieArr());
			if(map.has("name")) username = map.getString("name", defusername);
			log("Username updated to: " + username);
			if(first) log(">>>> Welcome back! <<<<");
			map = parseURLwithCookies("https://fexcraft.net/session/api", "r=fmt_status", getCookieArr());
			if(map != null && map.has("license") && map.has("license_title")){
				perm = map.getString("license", defperm);
				permname = map.getString("license_title", defpermname);
				log("License updated to: " + permname + " (" + perm + ")");
			}
		}
		else if(retry){
			if(!first) load();
			log("Attempting to re-login...");
			sessionid = null;
			tryLogin(null);
			if(!loggedin){
				log("Relogin seems to have failed.");
				userid = "-1";
				username = "Guest";
			}
		}
	}

	private static String[] getCookieArr(){
		return sessionid == null ? null : new String[] { "PHPSESSID=" + sessionid };
	}

	public static String tryLogin(Consumer<String> cons){
		String response;
		try{
			JsonMap map = parseURLwithCookies("https://fexcraft.net/session/api", "r=login&m=" + usermail + "&p=" + decrypt(), getCookieArr());
			if(map == null){
				log(response = "Invalid/Empty login response, aborting.");
				return response;
			}
			if(map.getMap("cookies").has("PHPSESSID")){
				sessionid = map.getMap("cookies").get("PHPSESSID").string_value();
				log("Updated Session ID to: " + sessionid);
			}
			loggedin = map.getBoolean("success", false);
			response = map.getString("status", "api:success=" + loggedin);
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
		JsonMap map = parseURLwithCookies("https://fexcraft.net/session/api", "r=logout", getCookieArr());
		log("Logout Response: " + map.toString());
		username = userid = "";
		pass = perm = permname = null;
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
		FMT.openLink("https://fexcraft.net/register");
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
		pass = Base64.getEncoder().encodeToString(pass.getBytes());
		log("Applied client side base encryption to password.");
	}

	// generic code based on stackoverflow, as long it's not plain readable that's fine

	public static String decrypt(){
		if(!shouldEncrypt()) return pass;
		return new String(Base64.getDecoder().decode(pass));
	}

}
