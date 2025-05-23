package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import com.spinyowl.legui.style.Style;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.joml.Vector2f;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PackDevEnv extends Widget {

	public static PackDevEnv INSTANCE;
	public static int def_width = 800;
	public static int def_height = 480;
	public static int tb_height = 40;
	public static int fp_width = 300;
	public static int fp_scroll = 20;
	public static int fp_inner = 500;
	public static int fe_height = 30;
	public static int fe_offset = 10;
	public static int spacer = 5;
	private static ConcurrentLinkedQueue<FileViewEntry> entries = new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<EnvTab> tabs = new ConcurrentLinkedQueue<>();
	protected static File envroot;
	protected ScrollablePanel filespanel;
	protected ScrollablePanel tabspanel;
	protected EnvContent content;
	protected boolean loaded;

	public PackDevEnv(){
		super(Settings.WORKSPACE_NAME.value);
		INSTANCE = this;
		setSize(def_width, def_height);
		getContainer().setSize(getSize());
		Settings.applyComponentTheme(getContainer());
		setResizable(true);
		setPosition(40, 40);
		filespanel = new ScrollablePanel();
		filespanel.setPosition(0, 0);
		filespanel.setSize(fp_width, def_height - fp_scroll);
		filespanel.getContainer().setSize(fp_inner, def_height - fp_scroll);
		getContainer().add(filespanel);
		tabspanel = new ScrollablePanel();
		tabspanel.setPosition(fp_width + spacer, 0);
		tabspanel.setSize(def_width - fp_width - spacer, tb_height);
		tabspanel.getContainer().setSize(def_width - fp_width - spacer, tb_height);
		tabspanel.setVerticalScrollBarVisible(false);
		getContainer().add(tabspanel);
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
			filespanel.setSize(fp_width, getSize().y - fp_scroll);
			tabspanel.setSize(vec.x - fp_width - spacer, tb_height);
			if(content != null){
				content.setSize(vec.x - fp_width - spacer, vec.y - tb_height);
				content.onResize();
			}
		});
		envroot = new File(Settings.WORKSPACE_ROOT.value);
		fillFilesPanel();
		startFileMonitor();
		load();
	}

	public static List<File> getSorted(File[] files, boolean only_vis){
		ArrayList<File> list = new ArrayList<>();
		ArrayList<File> dirs = new ArrayList<>();
		for(File file : files){
			if(only_vis && file.isHidden()) continue;
			if(file.isDirectory()) dirs.add(file);
			else list.add(file);
		}
		list.sort(Comparator.comparing(File::getName));
		dirs.sort(Comparator.comparing(File::getName));
		dirs.addAll(list);
		return dirs;
	}

	public static void load(){
		JsonMap map = JsonHandler.parse(new File("./pack_env.fmt"));
		if(map.empty()) return;
		JsonMap files = map.getMap("files");
		for(Map.Entry<String, JsonValue<?>> entry : files.entries()){
			FileViewEntry fe = INSTANCE.getEntry(entry.getKey());
			if(fe != null) fe.load(entry.getValue().asMap());
		}
		INSTANCE.loaded = true;
		INSTANCE.updateFileView();
		INSTANCE.updateTabView();
	}

	public static void save(){
		JsonMap map = new JsonMap();
		JsonMap files = new JsonMap();
		for(FileViewEntry entry : entries){
			JsonMap mep = entry.save();
			if(mep.not_empty()) files.add(entry.file.getName(), mep);
		}
		if(!files.empty()) map.add("files", files);
		if(!map.empty()) JsonHandler.print(new File("./pack_env.fmt"), map);
	}

	private void fillFilesPanel(){
		new Thread("PackEnv File Tree Loader"){
			@Override
			public void run(){
				try{
					List<File> list = getSorted(envroot.listFiles(), true);
					File[] ret;
					for(File file : list){
						if(file.isHidden()) continue;
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

	public FileViewEntry getEntry(String filename){
		for(FileViewEntry entry : entries){
			if(entry.file.getName().equals(filename)) return entry;
		}
		return null;
	}

	protected void updateFileView(){
		if(!loaded) return;
		int buf = 0;
		for(FileViewEntry entry : entries){
			buf += entry.updateDisplay(0, buf);
		}
		filespanel.getContainer().setSize(fp_inner, buf);
	}

	public static void toggle(){
		if(INSTANCE == null) FMT.FRAME.getContainer().add(new PackDevEnv());
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

	public void addTab(FileViewEntry entry){
		EnvTab tab = getTab(entry);
		if(tab == null){
			tab = new EnvTab(entry);
			tabs.add(tab);
			tabspanel.getContainer().add(tab);
			setContent(tab.getContent());
		}
		updateTabView();
	}

	public void remTab(FileViewEntry entry){
		EnvTab tab = getTab(entry);
		if(tab != null){
			tabs.remove(tab);
			tabspanel.getContainer().remove(tab);
			if(content != null && content.tab == tab){
				getContainer().remove(content);
				if(tabs.isEmpty()){
					setContent(null);
				}
				else{
					setContent(tabs.peek().getContent());
				}
			}
		}
		updateTabView();
	}

	public EnvTab getTab(FileViewEntry entry){
		for(EnvTab tab : tabs){
			if(tab.entry == entry) return tab;
		}
		return null;
	}

	public void setContent(EnvContent con){
		if(content != null) getContainer().remove(content);
		content = con;
		if(content != null){
			content.setPosition(fp_width + spacer, tb_height);
			content.setSize(getContainer().getSize().x - fp_width - spacer, getContainer().getSize().y - tb_height);
			content.onResize();
			getContainer().add(content);
		}
	}

	public void updateTabView(){
		if(!loaded) return;
		int buf = 0;
		for(EnvTab tab : tabs){
			buf += tab.updateDisplay(buf);
		}
		tabspanel.getContainer().setSize(buf, tb_height);
	}

	public static class FileChangeListener implements FileAlterationListener {

		@Override
		public void onDirectoryChange(File file){
			for(FileViewEntry entry : entries){
				if(entry.onFileEvent(file, FileEvent.CHANGE)){
					INSTANCE.updateFileView();
					break;
				}
			}
		}

		@Override
		public void onDirectoryCreate(File file){
			if(file.isHidden()) return;
			if(file.getParentFile().equals(envroot)){
				File[] ret = isPack(file);
				if(ret != null) INSTANCE.addEntry(new FvtmPackEntry(INSTANCE, file, ret[0], ret[1]));
				else INSTANCE.addEntry(new FileViewEntry(INSTANCE, file));
			}
			else{
				for(FileViewEntry entry : entries){
					if(entry.onFileEvent(file, FileEvent.CREATE)){
						INSTANCE.updateFileView();
						break;
					}
				}
			}
		}

		@Override
		public void onDirectoryDelete(File file){
			if(!removed(file)) return;
			for(FileViewEntry entry : entries){
				if(entry.onFileEvent(file, FileEvent.DELETE)){
					INSTANCE.updateFileView();
					break;
				}
			}
		}

		private boolean removed(File file){
			FileViewEntry rem = null;
			for(FileViewEntry entry : entries){
				if(file.equals(entry.file)){
					rem = entry;
					break;
				}
			}
			if(rem != null){
				INSTANCE.remEntry(rem);
				return true;
			}
			return false;
		}

		@Override
		public void onFileChange(File file){
			for(FileViewEntry entry : entries){
				if(entry.onFileEvent(file, FileEvent.CHANGE)){
					INSTANCE.updateFileView();
					break;
				}
			}
		}

		@Override
		public void onFileCreate(File file){
			if(file.isHidden()) return;
			for(FileViewEntry entry : entries){
				if(entry.onFileEvent(file, FileEvent.CREATE)){
					INSTANCE.updateFileView();
					break;
				}
			}
		}

		@Override
		public void onFileDelete(File file){
			if(removed(file)) return;
			for(FileViewEntry entry : entries){
				if(entry.onFileEvent(file, FileEvent.DELETE)){
					INSTANCE.updateFileView();
					break;
				}
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
