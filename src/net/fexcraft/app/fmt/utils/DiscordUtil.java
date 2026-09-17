package net.fexcraft.app.fmt.utils;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.LogLevel;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityButton;
import de.jcm.discordgamesdk.activity.ActivityButtonsMode;
import de.jcm.discordgamesdk.activity.ActivityType;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.lib.common.math.Time;

import java.time.Instant;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/** Using https://github.com/JnCrMx/discord-game-sdk4j */
public class DiscordUtil {

	public static Core CORE;
	public static long starttime;

	public static void update(boolean updatetime){
		if(updatetime) starttime = Time.getDate();
		if(CORE == null) return;
		try{
			Activity activity = new Activity();
			activity.setType(ActivityType.PLAYING);
			activity.setActivityButtonsMode(ActivityButtonsMode.BUTTONS);
			activity.setDetails("Modelling (on v." + FMT.VERSION + ")");
			activity.setState(Settings.DISCORD_HIDE.value ? "Working on an unknown Model" : FMT.MODEL.name);
			activity.timestamps().setStart(Instant.ofEpochMilli(starttime));
			ActivityButton button0 = new ActivityButton();
			button0.setLabel("FMT Download");
			button0.setUrl("https://fexcraft.net/app/fmt");
			activity.addButton(button0);
			ActivityButton button1 = new ActivityButton();
			button1.setLabel("FMT Support");
			button1.setUrl("https://discord.gg/5evQCq2xt8");
			activity.addButton(button1);
			activity.assets().setLargeImage("icon");
			CORE.activityManager().updateActivity(activity);
		}
		catch(Throwable e){
			e.printStackTrace();
		}
	}

	public static void start(){
		new Thread(() -> {
			CreateParams params = new CreateParams();
			params.setClientID(587016218196574209L);
			params.setFlags(CreateParams.getDefaultFlags());
			try{
				CORE = new Core(params);
				CORE.setLogHook(LogLevel.VERBOSE, (a, b) -> {});
				DiscordUtil.update(true);
				while(!glfwWindowShouldClose(FMT.getWindow())){
					CORE.runCallbacks();
					try{
						Thread.sleep(100);
					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
			catch(Throwable e){
				e.printStackTrace();
			}
		}).start();
	}

}
