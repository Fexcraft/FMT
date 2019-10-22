package net.fexcraft.app.fmt.utils;

import java.io.File;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.ui.re.Bottombar;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.utils.HttpUtil;
import net.fexcraft.lib.common.utils.Print;

public class SessionHandler {
	
	private static boolean loggedin, encrypted;
	private static String sessionid, hashpw, usermail, username;
	private static int userid;
	
	public static void load(){
		Print.console("Loading auth data from FILE.");
		File file = new File("./auth.net");
		JsonObject obj = JsonUtil.get(file);
		sessionid = obj.has("session") ? obj.get("session").getAsString() : null;
		encrypted = JsonUtil.getIfExists(obj, "encrypted", false);
		hashpw = JsonUtil.getIfExists(obj, "hashpw", "testuser");
		usermail = JsonUtil.getIfExists(obj, "mail", "testuser@fexcraft.net");
		userid = JsonUtil.getIfExists(obj, "userid", -1).intValue();
		username = JsonUtil.getIfExists(obj, "username", "Test-User-Access");
		if(!file.exists()) JsonUtil.write(file, obj);
	}

	public static void save(){
		Print.console("Saving auth data to FILE.");
		JsonObject obj = new JsonObject();
		if(sessionid != null) obj.addProperty("session", sessionid);
		obj.addProperty("encrypted", encrypted);
		if(hashpw != null) obj.addProperty("hashpw", hashpw);
		if(usermail != null) obj.addProperty("mail", usermail);
		if(userid >= 0) obj.addProperty("userid", userid);
		if(username != null) obj.addProperty("username", username);
		JsonUtil.write(new File("./auth.net"), obj);
	}
	
	public static void checkIfLoggedIn(boolean retry, boolean first){
		Print.console("Checking login status."); if(first) load();
		JsonObject obj = HttpUtil.request("http://fexcraft.net/session/api.jsp", "r=status&nossl", getCookieArr());
		if(obj != null && obj.has("success")){
			//Print.console(obj.toString());
			loggedin = obj.has("guest") && !obj.get("guest").getAsBoolean();
			userid = JsonUtil.getIfExists(obj, "user", -1).intValue();
			if(obj.has("banned") && obj.get("banned").getAsBoolean()){
				Print.console("Banned account detected, causing a commotion.");
				System.exit(-1); System.exit(1); System.exit(1);
				Bottombar.updateLoginState("BAN-N-NED");
			}
		}
		if(loggedin){
			Print.console("Fetching Username...");
			obj = HttpUtil.request("http://fexcraft.net/session/api.jsp", "r=username&nossl&id=" + userid, getCookieArr());
			if(obj.has("name")) username = obj.get("name").getAsString();
			Print.console("Username updated to: " + username);
			Bottombar.updateLoginState("Logged In - " + username);
		}
		else if(retry){
			if(!first) load(); Print.console("Trying to re-login...");
			if(tryLogin(false)){ checkIfLoggedIn(false, false); }
			if(!loggedin){
				Print.console("Relogin seems to have failed.");
				userid = -1; username = "Guest";
				Bottombar.updateLoginState("Login Failed - GUEST");
			}
		}
		else{
			Bottombar.updateLoginState("Logged Out - GUEST");
		}
	}
	
	private static String[] getCookieArr(){
		return sessionid == null ? null : new String[]{ "JSESSIONID=" + sessionid };
	}
	
	public static boolean tryLogin(boolean show){
		try{
			//TODO http :: find solution with the certs javax can't process
			JsonObject obj = HttpUtil.request("http://fexcraft.net/session/api.jsp", "r=login&m=" + usermail + "&p=" + hashpw + "&nossl" + (encrypted ? "&encrypted" : ""), getCookieArr());
			if(obj == null){ Print.console("Invalid/Empty login response, aborting."); return false; }
			if(obj.has("cookies") && obj.get("cookies").getAsJsonObject().has("JSESSIONID")){
				sessionid = obj.get("cookies").getAsJsonObject().get("JSESSIONID").getAsString();
				Print.console("Updated Session ID to: " + sessionid);
			}
			loggedin = obj.has("success") && obj.get("success").getAsBoolean();
			if(show){
				FMTB.showDialogbox((loggedin ? "Logged in!" : obj.has("status") ? obj.get("status").getAsString() : "No Status MSG.") + 
					"api:success=" + loggedin, "ok!", "retry", DialogBox.NOTHING, () -> {
						SessionHandler.checkIfLoggedIn(true, false);
					}
				);
			}
			//else{ Print.console(obj.toString()); }
			return true;
		}
		catch(Exception e){
			e.printStackTrace(); return loggedin = false;
		}
	}
	
	public static void tryLogout(){
		//TODO add logout dialog
	}
	
	public static boolean isLoggedIn(){
		return loggedin;
	}

	public static int getUserId(){
		return userid;
	}

	public static String getUserName(){
		return username;
	}

}
