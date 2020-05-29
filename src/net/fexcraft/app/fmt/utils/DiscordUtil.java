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
import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.lib.common.math.Time;

//** Using "Discord-RPC" from https://github.com/Vatuu/discord-rpc ! **//
public class DiscordUtil {
	
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
		if(updatetime) starttime = Time.getDate(); //int count = (int)ModelTree.count;
		DiscordRichPresence veryrichnot = new DiscordRichPresence.Builder("Modelling (on " + FMTB.VERSION + ")").setBigImage("icon", "Fex's Modelling Toolbox")//.setParty("Polygons", FMTB.MODEL.getCompound().size(), count)
			.setStartTimestamps(starttime).setDetails(Settings.discordrpc_showmodel() ? "Model: " + FMTB.getTitle() : "Working on an unknown Model").build();
		DiscordRPC.discordUpdatePresence(veryrichnot);
	}

}
