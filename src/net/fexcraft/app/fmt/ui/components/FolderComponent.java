package net.fexcraft.app.fmt.ui.components;

import java.io.File;
import java.util.ArrayList;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.fields.RunButton;

public class FolderComponent extends EditorComponent {
	
	public ArrayList<DirComponent> rootfolders = new ArrayList<DirComponent>();
	public ScrollablePanel panel;
	public File folder = new File("./");
	public int height = 300, scrollableheight;

	public FolderComponent(){
		super("folder", 300, false, true);
		this.add(new RunButton(LANG_PREFIX + "folder.select", F31, height - 28, F3S, HEIGHT, () -> genView()));
		this.add(new RunButton(LANG_PREFIX + "folder.refresh", F32, height - 28, F3S, HEIGHT, () -> genView()));
		panel = new ScrollablePanel(5, 30, Editor.CWIDTH - 10, fullheight - 65);
		//panel.setHorizontalScrollBarVisible(false);
		panel.getContainer().setSize(Editor.CWIDTH - 10, scrollableheight = fullheight - 65);
		Settings.applyBorderless(panel);
		this.add(panel);
		genView();
	}

	private void genView(){
		panel.getContainer().getChildComponents().removeAll(rootfolders);
		rootfolders.clear();
		float height = fullheight - 65;
		scrollableheight = 0;
		showFolder(folder, null, 0);
		panel.getContainer().setSize(Editor.CWIDTH - 10, scrollableheight < height ? height : scrollableheight);
	}

	private int showFolder(File folder, DirComponent root, int rrow){
		if(!folder.isDirectory()) return rrow;
		DirComponent com = null;
		if(folder.listFiles().length == 0){
			com = new DirComponent(FileType.EMPTY_FOLDER, folder, rrow);
		}
		else{
			com = new DirComponent(FileType.NORMAL_FOLDER, folder,  rrow);
			int row = 1;
			for(File file : folder.listFiles()){
				if(file.isDirectory()) row += showFolder(file, com, row);
				else continue;
				scrollableheight += 25;
			}
			for(File file : folder.listFiles()){
				if(!file.isDirectory()) com.add(new DirComponent(FileType.FILE, file, row++));
				else continue;
				scrollableheight += 25;
			}
		}
		if(root == null){
			com.getPosition().x -= DirComponent.xoff;
			rootfolders.add(com);
			panel.getContainer().add(com);
		}
		else root.add(com);
		scrollableheight += 25;
		return folder.list().length;
	}
	
	public static class DirComponent extends Component {
		
		public static final int xoff = 10;
		public boolean expanded;
		private Label label = new Label(30, 0, LW, 25);;
		private File file;

		public DirComponent(FileType type, File file, int row){
			this.file = file;
			label.getTextState().setText(file.getName());
			Settings.applyBorderless(label);
			getPosition().set(xoff, row * 25);
			getSize().set(300, file.isFile() ? 25 : file.list().length * 25 + 25);
			Settings.applyBorderless(this);
			this.add(label);
			this.add(new Icon(0, 24, 0, 0, 0, "./resources/textures/icons/filetree/" + type.filename() + ".png", () -> {}));
		}
		
	}
	
	public static enum FileType {
		
		NORMAL_FOLDER, EMPTY_FOLDER, FVTMPACK,
		FILE;

		public String filename(){
			switch(this){
				case FILE: return "file";
				case EMPTY_FOLDER: return "folder_empty";
				case FVTMPACK: return "folder_fvtmpack";
				case NORMAL_FOLDER:
				default: return "folder";
			}
		}
		
	}

}
