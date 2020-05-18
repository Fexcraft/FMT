/**
 * 
 */
package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimerTask;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.lib.common.math.Time;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Backups extends TimerTask {

	@Override
	public void run(){
		if(FMTB.MODEL.countTotalMRTs() <= 0) return;
		String str = sdf.format(Time.getDate());
		log("Saving backup... [" + str + "];");
		File file = new File("./backups/(" + str + ") " + FMTB.MODEL.name + ".fmtb");
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		SaveLoad.toFile(FMTB.MODEL, file, false);
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	private static final SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSSS");

	public static final SimpleDateFormat getSimpleDateFormat(boolean milli){
		return milli ? ssdf : sdf;
	}

}
