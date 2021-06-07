package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.settings.Settings.INTERNAL_CHOOSER;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.Translator;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FileChooser {
	
	public static FileType TYPE_PNG = new FileType("Portable Network Graphics", "*.png");
	public static FileType TYPE_IMG = new FileType("Image Files", "*.png", ".jpg", ".jpeg", ".bmp");
	public static FileType TYPE_FMTB = new FileType("FMT Save File", "*.fmtb");
	public static final int INTERNAL_HEIGHT = 500;
	
	public static void chooseFile(String title, String root, FileType type, boolean save, Consumer<File> task){
		if(!root.endsWith("/")) root += "/";
		if(INTERNAL_CHOOSER.value){
			openInternalChooser(title, root, type, save, task);
			return;
		}
        try(MemoryStack stack = MemoryStack.stackPush()){
        	PointerBuffer buffer = stack.mallocPointer(type.extensions.length); String string = "";
            for(int i = 0; i < type.extensions.length; i++){
            	buffer.put(stack.UTF8(type.extensions[i]));
            	buffer.flip();
            }
    		if(save) string = TinyFileDialogs.tinyfd_saveFileDialog(title, root, buffer, type.name);
    		else string = TinyFileDialogs.tinyfd_openFileDialog(title, root, buffer, type.name, false);
    		if(string != null && string.trim().length() > 0){
    			boolean ends = false;
    			for(int i = 0; i < type.extensions.length; i++){
    				if(string.endsWith(type.extensions[i].replace("*", ""))){
    					ends = true;
    					break;
    				}
    			}
    			if(!ends) string += type.extensions[0].replace("*", "");
    			task.accept(new File(string));
    		}
    		else task.accept(null);
        }
	}
	
	private static void openInternalChooser(String title, String root, FileType type, boolean save, Consumer<File> task){
		Dialog dialog = new Dialog(Translator.translate("filechooser.title"), 500, INTERNAL_HEIGHT + (save ? 30 : 0));
		Settings.applyComponentTheme(dialog.getContainer());
        Label label = new Label(title, 10, 10, 480, 20);
        File rootfile = new File(root);
        if(!rootfile.exists()) rootfile.mkdirs();
		Button rootbutton = new Button(rootfile.getAbsolutePath(), 10, 40, 480, 20);
		rootbutton.getStyle().setBorderRadius(0);
		rootbutton.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
		rootbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){
        		if(rootfile.getParentFile() != null){
        			dialog.close();
        			openInternalChooser(title, rootfile.getParentFile().getAbsolutePath(), type, save, task);
        		}
        	}
		});
		ScrollablePanel panel = new ScrollablePanel(10, 70, 480, INTERNAL_HEIGHT - 100);
        panel.setHorizontalScrollBarVisible(false);
		ArrayList<File> filtered = new ArrayList<>();
		if(rootfile.listFiles() != null){
			for(File file : rootfile.listFiles()){
				if(file.isDirectory()){
					filtered.add(file);
					continue;
				}
				for(int i = 0; i < type.extensions.length; i++){
					if(file.getName().endsWith(type.extensions[i].replace("*", ""))){
						filtered.add(file);
						continue;
					}
				}
			}
		}
		else{
			filtered.add(new File("error.no_files_here"));
		}
		filtered.sort((file, other) -> {
			if(file.isDirectory() && !other.isDirectory()){
				return -1;
			}
			if(!file.isDirectory() && other.isDirectory()){
				return 1;
			}
			return file.compareTo(other);
		});
		int size = filtered.size() * 22;
		panel.getContainer().setSize(panel.getSize().x, size < panel.getSize().y ? panel.getSize().y : size);
		for(int i = 0; i < filtered.size(); i++){
			File file = filtered.get(i);
			Button filebutton = new Button(file.getName() + (file.isDirectory() ? "/" : ""), 5, 1 + i * 22, 470, 20);
			filebutton.getStyle().setBorderRadius(0);
			filebutton.getStyle().getBorder().setEnabled(false);
			filebutton.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
			filebutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
	        	if(CLICK == e.getAction()){
        			dialog.close();
	        		if(file.isDirectory()){
	        			openInternalChooser(title, file.getAbsolutePath(), type, save, task);
	        		}
	        		else{
	        			task.accept(file);
	        		}
	        	}
			});
			panel.getContainer().add(filebutton);
		}
		if(save){
			TextField input = new TextField(Translator.translate("filechooser.enter_name"), 10, INTERNAL_HEIGHT - 20, 390, 20);
			Button select = new Button(Translator.translate("dialog.button.select"), 410, INTERNAL_HEIGHT - 20, 80, 20);
			select.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
	        	if(CLICK == e.getAction() && input.getTextState().getText().length() > 0){
        			dialog.close();
	        		String string = input.getTextState().getText();
	    			boolean ends = false;
	    			for(int i = 0; i < type.extensions.length; i++){
	    				if(string.endsWith(type.extensions[i].replace("*", ""))){ ends = true; break; }
	    			}
	    			if(!ends) string += type.extensions[0].replace("*", "");
	    			task.accept(new File(rootfile, string));
	        	}
			});
			dialog.getContainer().add(select);
			dialog.getContainer().add(input);
		}
		dialog.setResizable(false);
		dialog.getContainer().add(panel);
		dialog.getContainer().add(rootbutton);
		dialog.getContainer().add(label);
		dialog.show(FMT.FRAME);
	}

	public static class FileType {
		
		private String name;
		private String[] extensions;
		
		public FileType(String name, String... extensions){
			this.name = name;
			this.extensions = extensions;
		}
		
	}

}
