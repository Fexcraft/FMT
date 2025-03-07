package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import com.spinyowl.legui.style.Style;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Logging;
import org.joml.Vector2f;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PackDevEnv extends Widget {

	public static PackDevEnv INSTANCE;
	public static Thread WATCH;
	public static int def_width = 600;
	public static int def_height = 480;
	public static int tb_height = 30;
	public static int fp_width = 250;
	public static int fe_height = 30;
	private static List<FileViewEntry> entries = new ArrayList<>();
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
		fillFilesPanel();
		startWatch();
	}

	private void fillFilesPanel(){
		new Thread("Workspace File Tree Loader"){
			@Override
			public void run(){
				try{
					File root = new File(Settings.WORKSPACE_ROOT.value);
					File[] arr = root.listFiles(File::isDirectory);
					File assets;
					File fvtm;
					for(File file : arr){
						fvtm = null;
						assets = new File(file, "assets");
						if(!assets.exists() || !assets.isDirectory()) continue;
						for(File afol : assets.listFiles(File::isDirectory)){
							fvtm = new File(afol, "addonpack.fvtm");
							if(fvtm.exists()) break;
						}
						if(fvtm == null) continue;
						addEntry(new FvtmPackEntry(INSTANCE, file, assets, fvtm));
						Logging.log(fvtm.toPath().toString());
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void addEntry(FileViewEntry entry){
		entries.add(entry);
		filespanel.getContainer().add(entry);
		updateFileView();
	}

	private void updateFileView(){
		int buf = 0;
		for(FileViewEntry entry : entries){
			buf += entry.updateDisplay(buf);
		}
	}

	public static void toggle(){
		if(INSTANCE == null) FMT.FRAME.getContainer().add(INSTANCE = new PackDevEnv());
		else if(visible()) INSTANCE.hide();
		else INSTANCE.show();
	}

	public static boolean visible(){
		return INSTANCE.getStyle().getDisplay() != Style.DisplayType.NONE;
	}

	private void startWatch(){
		if(WATCH != null) return;
		WATCH = new Thread("Workspace Watch Service"){
			@Override
			public void run(){
				WatchService serv;
				try{
					serv = FileSystems.getDefault().newWatchService();
					Path path = Paths.get(Settings.WORKSPACE_ROOT.value);
					Logging.log("Starting to watch files in " + path.toString());
					path.register(serv, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
					Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
						@Override
						public FileVisitResult preVisitDirectory(Path p, BasicFileAttributes a)throws IOException{
							if(p != null && a != null) p.register(serv, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
							return FileVisitResult.CONTINUE;
						}
					});
				}
				catch(IOException e){
					throw new RuntimeException(e);
				}
				WatchKey key;
				Path path;
				while(true){
					try{
						key = serv.take();
						for(WatchEvent<?> event : key.pollEvents()){
							path = (Path)event.context();
							if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE){

							}
							else if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE){

							}
							else if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){

							}
						}
						key.reset();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		};
		WATCH.start();
	}

}
