package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import com.spinyowl.legui.style.Style;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Logging;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.joml.Vector2f;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PackDevEnv extends Widget {

	public static PackDevEnv INSTANCE;
	public static int def_width = 600;
	public static int def_height = 480;
	public static int tb_height = 30;
	public static int fp_width = 250;
	public static int fe_height = 30;
	private static ConcurrentLinkedQueue<FileViewEntry> entries = new ConcurrentLinkedQueue<>();
	protected static File envroot;
	protected ScrollablePanel filespanel;

	public PackDevEnv(){
		super(Settings.WORKSPACE_NAME.value);
		setSize(def_width, def_height);
		getContainer().setSize(getSize());
		setResizable(true);
		setPosition(40, 40);
		filespanel = new ScrollablePanel();
		filespanel.setPosition(0, tb_height);
		filespanel.setSize(fp_width, def_height);
		filespanel.getContainer().setSize(fp_width - 10, def_height);
		filespanel.getStyle().setDisplay(Style.DisplayType.FLEX);
		getContainer().add(filespanel);
		getListenerMap().addListener(ChangeSizeEvent.class, event -> {
			Vector2f vec = new Vector2f();
			event.getNewSize().get(vec);
			if(vec.x < def_width){
				setSize(def_width, vec.y);
				return;
			}
			if(vec.y < def_height){
				setSize(vec.x, def_height);
				return;
			}
			getContainer().setSize(vec);
			filespanel.setSize(fp_width, getSize().y - tb_height);
		});
		envroot = new File(Settings.WORKSPACE_ROOT.value);;
		fillFilesPanel();
		startFileMonitor();
	}

	private void fillFilesPanel(){
		new Thread("PackEnv File Tree Loader"){
			@Override
			public void run(){
				try{
					File[] arr = envroot.listFiles();
					File[] ret;
					for(File file : arr){
						ret = isPack(file);
						if(ret != null) addEntry(new FvtmPackEntry(INSTANCE, file, ret[0], ret[1]));
						else addEntry(new FileViewEntry(INSTANCE, file));
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static File[] isPack(File file){
		File fvtm = null;
		File assets = new File(file, "assets");
		if(!assets.exists() || !assets.isDirectory()) return null;
		for(File afol : assets.listFiles(File::isDirectory)){
			fvtm = new File(afol, "addonpack.fvtm");
			if(fvtm.exists()) break;
		}
		if(fvtm == null || !fvtm.exists()) return null;
		return new File[]{ assets, fvtm };
	}

	public void addEntry(FileViewEntry entry){
		entries.add(entry);
		filespanel.getContainer().add(entry);
		updateFileView();
	}

	public void remEntry(FileViewEntry entry){
		entries.remove(entry);
		filespanel.getContainer().remove(entry);
		updateFileView();
	}

	private void updateFileView(){
		int buf = 0;
		for(FileViewEntry entry : entries){
			buf += entry.updateDisplay(buf);
		}
		filespanel.getContainer().setSize(fp_width, buf);
	}

	public static void toggle(){
		if(INSTANCE == null) FMT.FRAME.getContainer().add(INSTANCE = new PackDevEnv());
		else if(visible()) INSTANCE.hide();
		else INSTANCE.show();
	}

	public static boolean visible(){
		return INSTANCE.getStyle().getDisplay() != Style.DisplayType.NONE;
	}

	private void startFileMonitor(){
		FileAlterationMonitor monitor = new FileAlterationMonitor(5000l);
		FileAlterationObserver obs = new FileAlterationObserver(envroot);
		obs.addListener(new FileChangeListener());
		monitor.addObserver(obs);
		try{
			monitor.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static class FileChangeListener implements FileAlterationListener {

		@Override
		public void onDirectoryChange(File file){
			for(FileViewEntry entry : entries){
				if(entry.onFileEvent(file, FileEvent.FILE_CHANGE)) break;
			}
		}

		@Override
		public void onDirectoryCreate(File file){
			if(file.getParentFile().equals(envroot)){
				File[] ret = isPack(file);
				if(ret != null) INSTANCE.addEntry(new FvtmPackEntry(INSTANCE, file, ret[0], ret[1]));
				else INSTANCE.addEntry(new FileViewEntry(INSTANCE, file));
			}
			else{
				for(FileViewEntry entry : entries){
					if(entry.onFileEvent(file, FileEvent.DIR_CREATE)) break;
				}
			}
		}

		@Override
		public void onDirectoryDelete(File file){
			FileViewEntry rem = null;
			for(FileViewEntry entry : entries){
				if(file.equals(entry.file)){
					rem = entry;
					break;
				}
			}
			if(rem != null) INSTANCE.remEntry(rem);
			else{
				for(FileViewEntry entry : entries){
					if(entry.onFileEvent(file, FileEvent.DIR_DELETE)) break;
				}
			}
		}

		@Override
		public void onFileChange(File file){
			for(FileViewEntry entry : entries){
				if(entry.onFileEvent(file, FileEvent.FILE_CHANGE)) break;
			}
		}

		@Override
		public void onFileCreate(File file){
			for(FileViewEntry entry : entries){
				if(entry.onFileEvent(file, FileEvent.FILE_CREATE)) break;
			}
		}

		@Override
		public void onFileDelete(File file){
			for(FileViewEntry entry : entries){
				if(entry.onFileEvent(file, FileEvent.FILE_DELETE)) break;
			}
		}

		@Override
		public void onStart(FileAlterationObserver obs){
			//
		}

		@Override
		public void onStop(FileAlterationObserver obs){
			//
		}

	}

}
