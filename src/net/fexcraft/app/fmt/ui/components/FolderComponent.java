package net.fexcraft.app.fmt.ui.components;

import java.io.File;
import java.util.ArrayList;

import net.fexcraft.app.fmt.ui.FileEditMenu;
import net.fexcraft.app.fmt.ui.JsonEditor;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.fvtm.FVTMConfigEditor;
import net.fexcraft.app.json.JsonMap;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.style.Style.DisplayType;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Translator;

public class FolderComponent extends EditorComponent {
	
	public static final int ROWHEIGHT = 30;
	public ArrayList<DirComponent> rootfolders = new ArrayList<DirComponent>();
	public ScrollablePanel panel;
	public File folder = new File("./workspace/");
	public int scrollableheight;

	public FolderComponent(String suffix, int height){
		super("folder." + suffix, height, false, true);
		this.add(new RunButton(LANG_PREFIX + "folder.select", F31, height - 28, F3S, HEIGHT, () -> {
	        try(MemoryStack stack = MemoryStack.stackPush()){
	    		String string = TinyFileDialogs.tinyfd_selectFolderDialog(Translator.translate("editor.component.folder.select.dialog"), folder.toString());
	    		if(string != null && string.trim().length() > 0){
					folder = new File(string);
					genView();
	    		}
	        }
		}));
		this.add(new RunButton(LANG_PREFIX + "folder.refresh", F32, height - 28, F3S, HEIGHT, () -> genView()));
		panel = new ScrollablePanel(5, 30, Editor.CWIDTH - 10, fullheight - 65);
		//panel.setVerticalScrollBarVisible(false);
		panel.getContainer().setSize(height, scrollableheight = fullheight - 65);
		Settings.applyBorderless(panel);
		this.add(panel);
		if(!folder.exists()) folder.mkdirs();
		genView();
	}

	public void genView(){
		new Thread("FolderViewGenerator"){
			@Override
			public void run(){
				panel.getContainer().removeAll(rootfolders);
				rootfolders.clear();
				addFolder(folder, null, 0);
				resize();
				panel.getVerticalScrollBar().setScrollStep(0f);
			}
		}.start();
	}

	private int addFolder(File folder, DirComponent root, int rrow){
		if(!folder.isDirectory()) return rrow;
		DirComponent com = null;
		if(folder.listFiles().length == 0){
			com = new DirComponent(FileType.EMPTY_FOLDER, this, root, folder, rrow);
		}
		else{
			com = new DirComponent(FileType.NORMAL_FOLDER, this, root, folder,  rrow);
			int row = 1;
			for(File file : folder.listFiles()){
				if(file.isDirectory()) row += addFolder(file, com, row);
				else continue;
			}
			for(File file : folder.listFiles()){
				if(!file.isDirectory()){
					FileType type = FileType.fromFile(file);
					if(type == FileType.FVTM_ADDONPACKFILE){
						root.root.updateIcon(FileType.FVTMPACK, this);
					}
					com.addSub(new DirComponent(type, this, root, file, row++));
				}
				else continue;
			}
		}
		if(root == null){
			rootfolders.add(com);
			panel.getContainer().add(com);
		}
		else root.addSub(com);
		return folder.list().length;
	}

	private void resize(){
		float height = fullheight - 65;
		scrollableheight = 0;
		for(DirComponent com : rootfolders){
			scrollableheight += com.resize(scrollableheight, true);
		}
		scrollableheight = 0;
		for(DirComponent com : rootfolders){
			scrollableheight += com.fullsize();
		}
		panel.getContainer().setSize(Editor.CWIDTH - 10, scrollableheight < height ? height : scrollableheight);
	}
	
	public static class DirComponent extends Component {

		public static final int xoff = 10;
		private ArrayList<DirComponent> subcom = new ArrayList<>();
		private boolean expanded = false;
		private FileType type;
		private DirComponent root;
		private Label label;
		private Icon icon;
		//private File file;

		public DirComponent(FileType type, FolderComponent folcom, DirComponent root, File file, int row){
			//this.file = file;
			this.type = type;
			this.root = root;
			add(label = new Label(ROWHEIGHT + 2, 0, LW, ROWHEIGHT));
			label.getTextState().setText(file.getName());
			Settings.applyBorderless(label);
			Settings.applyBorderless(this);
			label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() == MouseClickAction.CLICK){
					if(listener.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
						if(type.directory){
							expanded = !expanded;
							folcom.resize();
						}
						else if(type.editable){
							switch(type){
								case FVTM_ADDONPACKFILE:{
									new JsonEditor(file);//temporary
									break;
								}
								case FVTM_CONFIGFILE:{
									new FVTMConfigEditor(file);//temporary
									break;
								}
								case JSON:{
									new JsonEditor(file);
									break;
								}
								default: break;
							}
						}
					}
					else if(listener.getButton() == MouseButton.MOUSE_BUTTON_RIGHT){
						FileEditMenu.show(folcom, this, file);
					}
				}
			});
			updateIcon(type, folcom);
		}

		public void addSub(DirComponent com){
			subcom.add(com);
			this.add(com);
		}

		public int resize(int offset, boolean noff){
			int size = ROWHEIGHT;
			getPosition().set(noff ? 0 : xoff, noff ? offset : size + offset);
			if(expanded){
				int off = 0;
				for(DirComponent com : subcom){
					int siz = com.resize(off, false);
					size += siz;
					off += siz;
					com.getStyle().setDisplay(DisplayType.MANUAL);
				}
			}
			else{
				for(DirComponent com : subcom){
					com.getStyle().setDisplay(DisplayType.NONE);
				}
			}
			getSize().set(300, size);
			return (int)getSize().y;
		}

		public int fullsize(){
			int size = ROWHEIGHT;
			for(DirComponent com : subcom){
				size += com.fullsize();
			}
			return size;
		}
		
		public void updateIcon(FileType type, FolderComponent folcom){
			this.type = type;
			this.remove(icon);
			this.add(icon = new Icon(0, 32, 0, 0, -1, "./resources/textures/icons/filetree/" + type.filename() + ".png", () -> {
				expanded = !expanded;
				folcom.resize();
			}));
		}
		
	}
	
	public static enum FileType {
		
		NORMAL_FOLDER(true, false),
		EMPTY_FOLDER(true, false),
		FVTMPACK(true, false),
		FILE(false, false),
		FVTM_ADDONPACKFILE(false, true),
		FVTM_CONFIGFILE(false, true),
		JSON(false, true),
		FMTB(false, false);

		private boolean editable, directory;

		FileType(boolean dir, boolean edit){
			editable = edit;
			directory = dir;
		}

		public String filename(){
			switch(this){
				case EMPTY_FOLDER: return "folder_empty";
				case FVTMPACK: return "folder_fvtmpack";
				case NORMAL_FOLDER: return "folder";
				case FVTM_ADDONPACKFILE: return "file_fvtmaddonpack";
				case FVTM_CONFIGFILE: return "file_fvtmcfg";
				case JSON: return "file_json";
				case FILE:
				default: return "file";
			}
		}

		static FileType fromFile(File file){
			if(file.getName().equals("addonpack.fvtm")) return FVTM_ADDONPACKFILE;
			String name = file.getName();
			if(name.contains(".")){
				String[] split = name.split("\\.");
				if(split.length > 1){
					String suffix = split[split.length - 1];
					switch(suffix){
						case "json":
							return JSON;
						case "block":
						case "multiblock":
						case "cloth":
						case "container":
						case "material":
						case "vehicle":
						case "part":
						case "gauge":
						case "wire":
						case "consumable":
						case "fuel":
						case "recipe":
							return FVTM_CONFIGFILE;
						case "fmtb":
							return FMTB;
						default: break;
					}
				}
			}
			return FILE;
		}
	}

	@Override
	public FolderComponent load(JsonMap map){
		folder = new File(map.getString("root", "./workspace/"));
		minimize(map.getBoolean("minimized", false));
		//Logging.log(folder, this);
		return this;
	}

	@Override
	public JsonMap save(){
		JsonMap map = new JsonMap();
		map.add("root", folder.getPath().toString());
		if(minimized )map.add("minimized", minimized);
		map.add("id", id);
		return map;
	}

	public static class Small extends FolderComponent {

		public Small(){
			super("small", 200);
		}

	}

	public static class Medium extends FolderComponent {

		public Medium(){
			super("medium", 400);
		}

	}

	public static class Large extends FolderComponent {

		public Large(){
			super("large", 600);
		}

	}

}
