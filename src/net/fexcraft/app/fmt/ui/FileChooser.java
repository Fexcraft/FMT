package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.settings.Settings.INTERNAL_CHOOSER;

import java.io.File;
import java.util.function.Consumer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FileChooser {
	
	public static FileType TYPE_PNG = new FileType("Portable Network Graphics", "*.png");
	public static FileType TYPE_IMG = new FileType("Image Files", "*.png", ".jpg", ".jpeg", ".bmp");
	public static FileType TYPE_FMTB = new FileType("FMT Save File", "*.fmtb");
	
	public static void chooseFile(String title, String root, FileType type, boolean save, Consumer<File> task){
		if(!root.endsWith("/")) root += "/";
		if(INTERNAL_CHOOSER.value){
			openInternalChooser(title, root, type, save, task);
			return;
		}
        try(MemoryStack stack = MemoryStack.stackPush()){
        	PointerBuffer buffer = stack.mallocPointer(type.extensions.length); String string = "";
            for(int i = 0; i < type.extensions.length; i++){
            	buffer.put(stack.UTF8(type.extensions[i])); buffer.flip();
            }
    		if(save) string = TinyFileDialogs.tinyfd_saveFileDialog(title, root, buffer, type.name);
    		else string = TinyFileDialogs.tinyfd_openFileDialog(title, root, buffer, type.name, false);
    		if(string != null && string.trim().length() > 0){
    			boolean ends = false;
    			for(int i = 0; i < type.extensions.length; i++){
    				if(string.endsWith(type.extensions[i].replace("*", ""))){ ends = true; break; }
    			}
    			if(!ends) string += type.extensions[0].replace("*", "");
    			task.accept(new File(string));
    		}
    		else task.accept(null);
        }
	}
	
	private static void openInternalChooser(String title, String root, FileType type, boolean save, Consumer<File> task){
		// TODO Auto-generated method stub
		
	}

	public static record FileType(String name, String... extensions){}

}
