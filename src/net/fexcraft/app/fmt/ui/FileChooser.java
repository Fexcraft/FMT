package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.settings.Settings.INTERNAL_CHOOSER;
import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import net.fexcraft.app.fmt.ui.fields.RunButton;
import com.spinyowl.legui.component.*;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.listener.MouseClickEventListener;
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
	public static FileType TYPE_IMG = new FileType("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp");
	public static FileType TYPE_FMTB = new FileType("FMT Save File", "*.fmtb");
	public static FileType TYPE_JSON = new FileType("JSON", "*.json");
	public static FileType TYPE_JARZIP = new FileType("Jar/Zip Archive", "*.jar", "*.zip");
	public static FileType TYPE_FMF = new FileType("Fex's Model Format", "*.fmf");
	public static FileType TYPE_BOB = new FileType("Byte Encoded Object", "*.bob", "*.beo");
	public static FileType TYPE_OBJ = new FileType("Wavefront Object (obj)", "*.obj");
	public static FileType TYPE_BBM = new FileType("BlockBench Model", "*.bbmodel");
	public static FileType TYPE_JAVA = new FileChooser.FileType("Java Model", "*.java");
	public static FileType TYPE_ANY = new FileType("Any", "*.*");
	public static final int INTERNAL_HEIGHT = 500;
	
	public static void chooseFile(String title, String root, FileType type, boolean save, Consumer<File> task){
		if(!root.endsWith("/")) root += "/";
		if(INTERNAL_CHOOSER.value){
			openInternalChooser(title, root, type, save, task);
			return;
		}
        try(MemoryStack stack = MemoryStack.stackPush()){
        	PointerBuffer buffer = stack.mallocPointer(type.extensions.length);
        	String string = "";
            for(int i = 0; i < type.extensions.length; i++){
            	buffer.put(stack.UTF8(type.extensions[i]));
            }
        	buffer.flip();
    		if(save) string = TinyFileDialogs.tinyfd_saveFileDialog(title, root, buffer, type.name);
    		else string = TinyFileDialogs.tinyfd_openFileDialog(title, root, buffer, type.name, false);
    		if(string != null && string.trim().length() > 0){
				if(type == TYPE_ANY){
					task.accept(new File(string));
					return;
				}
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

	public static void chooseDir(String title, String root, Consumer<File> task){
		if(!root.endsWith("/")) root += "/";
		if(INTERNAL_CHOOSER.value){
			GenericDialog.showOK("not-available-yet", null, null);
			return;
		}
		task.accept(new File(TinyFileDialogs.tinyfd_selectFolderDialog(title, root)));
	}
	
	private static void openInternalChooser(String title, String root, FileType type, boolean save, Consumer<File> task){
		int width = 600, lwidth = width - 220, rwidth = 200;
		Dialog dialog = new Dialog(Translator.translate("filechooser.title"), width, INTERNAL_HEIGHT + (save ? 30 : 0));
		Settings.applyComponentTheme(dialog.getContainer());
        Label label = new Label(title, 10, 10, lwidth, 20);
        File rootfile = new File(root);
        if(!rootfile.exists()) rootfile.mkdirs();
		Button rootbutton = new Button(rootfile.getAbsolutePath(), 10, 40, lwidth, 20);
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
		Tooltip tip = new Tooltip(rootfile.getAbsolutePath());
		tip.setSize(600, 40);
		tip.setPosition(0, 20);
		rootbutton.setTooltip(tip);
		ScrollablePanel panel = new ScrollablePanel(10, 70, lwidth, INTERNAL_HEIGHT - 100);
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
			Button filebutton = new Button(file.getName() + (file.isDirectory() ? "/" : ""), 5, 1 + i * 22, lwidth - 10, 20);
			filebutton.getStyle().setBorderRadius(0);
			filebutton.getStyle().setBorder(null);
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
		//
		dialog.getContainer().add(new Label(Translator.translate("filechooser.bookmarks"), width - rwidth, 50, rwidth - 60, 20));
		dialog.getContainer().add(new RunButton("dialog.button.add", width - 60, 50, 50, 20, () -> {
			dialog.close();
			Settings.BOOKMARKS.add(rootfile);
			openInternalChooser(title, rootfile.getAbsolutePath(), type, save, task);
		}));
		ScrollablePanel marks = new ScrollablePanel(width - rwidth, 70, rwidth - 10, INTERNAL_HEIGHT - 100);
		panel.setHorizontalScrollBarVisible(false);
		int rsize = Settings.BOOKMARKS.size() * 22;
		marks.getContainer().setSize(marks.getSize().x - 10, rsize < marks.getSize().y ? marks.getSize().y : rsize);
		for(int i = 0; i < Settings.BOOKMARKS.size(); i++){
			File file = Settings.BOOKMARKS.get(i);
			Button filebutton = new Button(file.getName(), 5, 1 + i * 22, marks.getSize().x - 28, 20);
			filebutton.getStyle().setBorderRadius(0);
			filebutton.getStyle().setBorder(null);
			filebutton.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
			filebutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
				if(CLICK == e.getAction()){
					dialog.close();
					openInternalChooser(title, file.getAbsolutePath(), type, save, task);
				}
			});
			tip = new Tooltip(file.getAbsolutePath());
			tip.setSize(400, 40);
			tip.setPosition(0, 20);
			filebutton.setTooltip(tip);
			marks.getContainer().add(filebutton);
			int idx = i;
			marks.getContainer().add(new Icon(0, 16, 0, (int)(marks.getSize().x - 26), 3 + i * 22, "./resources/textures/icons/component/remove.png", () -> {
				dialog.close();
				Settings.BOOKMARKS.remove(idx);
				openInternalChooser(title, file.getAbsolutePath(), type, save, task);
			}));
		}
		//marks.setHorizontalScrollBarVisible(false);
		dialog.getContainer().add(marks);
		//
		if(save){
			TextField input = new TextField(Translator.translate("filechooser.enter_name"), 10, INTERNAL_HEIGHT - 20, lwidth - 100, 20);
			Button select = new Button(Translator.translate("dialog.button.select"), lwidth - 80, INTERNAL_HEIGHT - 20, 80, 20);
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

		public boolean supports(File file){
			for(String str : extensions){
				if(file.getName().endsWith(str.replace("*", ""))) return true;
			}
			return false;
		}

	}

}
