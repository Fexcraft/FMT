package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.DisconnectedCallback;
import net.arikia.dev.drpc.callbacks.ErroredCallback;
import net.arikia.dev.drpc.callbacks.JoinGameCallback;
import net.arikia.dev.drpc.callbacks.JoinRequestCallback;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.arikia.dev.drpc.callbacks.SpectateGameCallback;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.lib.common.math.Time;

//** Using "Discord-RPC" from https://github.com/Vatuu/discord-rpc ! **//
public class DiscordUtil {
	
	public static Thread DISCORD_THREAD;
	
	public static class SpectateGameEventHandler implements SpectateGameCallback {

		@Override
		public void apply(String arg0){
			// TODO Auto-generated method stub

		}

	}

	public static class JoinRequestEventHandler implements JoinRequestCallback {

		@Override
		public void apply(DiscordUser arg0){
			// TODO Auto-generated method stub

		}

	}

	public static class JoinGameEventHandler implements JoinGameCallback {

		@Override
		public void apply(String arg0){
			// TODO Auto-generated method stub

		}

	}

	public static long starttime;
	
	public static class ErroredEventHandler implements ErroredCallback {

		@Override
		public void apply(int arg0, String arg1){
			// TODO Auto-generated method stub

		}

	}

	public static class DisconectedEventHandler implements DisconnectedCallback {

		@Override
		public void apply(int arg0, String arg1){
			// TODO Auto-generated method stub

		}

	}

	public static class ReadyEventHandler implements ReadyCallback {

		@Override
		public void apply(DiscordUser user){
			log("Received Discord ID: " + user.username + "#" + user.discriminator + "");
		}

	}
	
	public static void update(boolean updatetime){
		if(updatetime) starttime = Time.getDate();
		DiscordRichPresence veryrichnot = new DiscordRichPresence.Builder("Modelling (on " + FMT.VERSION + ")").setBigImage("icon", FMT.TITLE)
			.setStartTimestamps(starttime).setDetails(Settings.DISCORD_HIDE.value ? "Working on an unknown Model" : FMT.MODEL.name).build();
		DiscordRPC.discordUpdatePresence(veryrichnot);
	}

}
