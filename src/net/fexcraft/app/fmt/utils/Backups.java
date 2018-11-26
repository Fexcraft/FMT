/**
 * 
 */
package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimerTask;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Backups extends TimerTask {

	@Override
	public void run(){
		if(FMTB.MODEL.countTotalMRTs() <= 0) return;
		JsonObject obj = SaveLoad.modelToJTMT(null, false);
		String str = sdf.format(Time.getDate()); Print.console("Saving backup... [" + str + "];");
		File file = new File("./backups/(" + str + ") " + FMTB.MODEL.name + ".jtmt");
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		JsonUtil.write(file, obj);
		//TODO update to save in fmtb form
	}
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH.mm.ss");
	private static final SimpleDateFormat ssdf = new SimpleDateFormat("dd-MMM-yyyy HH.mm.ss.SSSS");

	public static final SimpleDateFormat getSimpleDateFormat(boolean milli){ return milli ? ssdf : sdf; }

}
