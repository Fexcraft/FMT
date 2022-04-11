package net.fexcraft.app.fmt.ui.components;

import java.io.File;

import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.RunButton;

public class FolderComponent extends EditorComponent {
	
	public File folder = new File("./");
	public int height = 300;

	public FolderComponent(){
		super("folder", 300, false, true);
		this.add(new RunButton(LANG_PREFIX + "folder.select", F31, height - 28, F3S, HEIGHT, () -> genView()));
		this.add(new RunButton(LANG_PREFIX + "folder.refresh", F32, height - 28, F3S, HEIGHT, () -> genView()));
		genView();
	}

	private void genView(){
		this.getChildComponents().removeIf(com -> com instanceof RunButton == false);
		showFolder(folder);
	}

	private void showFolder(File folder){
		//
	}

}
