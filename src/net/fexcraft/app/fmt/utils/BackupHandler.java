/**
 * 
 */
package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.bar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimerTask;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.lib.common.math.Time;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class BackupHandler extends TimerTask {

	@Override
	public void run(){
		if(FMT.MODEL.totalPolygons() == 0) return;
		try{
			String str = sdf.format(Time.getDate());
			bar("Saving backup... [" + str + "];", true, 10);
			File file = new File("./backups/(" + str + ") " + FMT.MODEL.name + ".fmtb");
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
			if(!SaveHandler.save(FMT.MODEL, file, null, true, false)){
				FMT.UI.createDialog(500, 80, "saveload.title")
					.addText(0, "saveload.backup.failure")
					.buttons(100, Dialog.DialogButton.OK);
				JsonHandler.print(new File("./backups/(" + str + ") " + FMT.MODEL.name + ".json"), SaveHandler.modelToJTMT(FMT.MODEL, false));
			}
		}
		catch(Exception e){
			FMT.UI.createDialog(500, 80, "saveload.title")
				.addText(0, "saveload.backup.failure")
				.buttons(100, Dialog.DialogButton.OK);
			e.printStackTrace();
			Logging.log(e);
		}
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	private static final SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSSS");

	public static final SimpleDateFormat getSimpleDateFormat(boolean milli){
		return milli ? ssdf : sdf;
	}

}
