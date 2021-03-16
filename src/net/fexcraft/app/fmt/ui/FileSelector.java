package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Translator;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FileSelector {
	
	/** For general file needs. */
	public static final void select(String title, String root, String[] type, boolean save, AfterTask task){
		if(!root.endsWith("/")) root += "/";
		if(Settings.internal_filechooser()){
			openFileChooser(title, root, type, save, task);
			return;
		}
        try(MemoryStack stack = MemoryStack.stackPush()){
        	PointerBuffer buffer = stack.mallocPointer(type.length - 1); String string = "";
            for(int i = 1; i < type.length; i++) buffer.put(stack.UTF8(type[i])); buffer.flip();
    		if(save) string = TinyFileDialogs.tinyfd_saveFileDialog(title, root, buffer, type[0]);
    		else string = TinyFileDialogs.tinyfd_openFileDialog(title, root, buffer, type[0], false);//log(string);
    		if(string != null && string.trim().length() > 0){
    			boolean ends = false;
    			for(int i = 1; i < type.length; i++){
    				if(string.endsWith(type[i].replace("*", ""))){ ends = true; break; }
    			}
    			if(!ends) string += type[1].replace("*", "");
    			task.process(new File(string));
    		}
    		else task.process(null);
        }
	}
	
	private static void openFileChooser(String title, String root, String[] type, boolean save, AfterTask task){
		Dialog dialog = new Dialog(Translator.translate("saveload.title"), 500, save ? 630 : 600);
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
        			openFileChooser(title, rootfile.getParentFile().getAbsolutePath(), type, save, task);
        		}
        	}
		});
		ScrollablePanel panel = new ScrollablePanel(10, 70, 480, 500);
        panel.setHorizontalScrollBarVisible(false);
		ArrayList<File> filtered = new ArrayList<>();
		if(rootfile.listFiles() != null){
			for(File file : rootfile.listFiles()){
				if(file.isDirectory()){
					filtered.add(file);
					continue;
				}
				for(int i = 1; i < type.length; i++){
					if(file.getName().endsWith(type[i].replace("*", ""))){
						filtered.add(file);
						continue;
					}
				}
			}
		}
		else{
			filtered.add(new File("error.no.files.here"));
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
	        			openFileChooser(title, file.getAbsolutePath(), type, save, task);
	        		}
	        		else{
	        			task.process(file);
	        		}
	        	}
			});
			panel.getContainer().add(filebutton);
		}
		if(save){
			TextField input = new TextField("enter custom filename here", 10, 580, 390, 20);
			Button select = new Button(Translator.translate("dialogbox.button.select"), 410, 580, 80, 20);
			select.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
	        	if(CLICK == e.getAction() && input.getTextState().getText().length() > 0){
        			dialog.close();
	        		String string = input.getTextState().getText();
	    			boolean ends = false;
	    			for(int i = 1; i < type.length; i++){
	    				if(string.endsWith(type[i].replace("*", ""))){ ends = true; break; }
	    			}
	    			if(!ends) string += type[1].replace("*", "");
	    			task.process(new File(rootfile, string));
	        	}
			});
			dialog.getContainer().add(select);
			dialog.getContainer().add(input);
		}
		dialog.setResizable(false);
		dialog.getContainer().add(panel);
		dialog.getContainer().add(rootbutton);
		dialog.getContainer().add(label);
		dialog.show(FMTB.frame);
	}

	/** For selecting an Ex/Im-Porter first. */
	public static final void select(String title, String root, boolean export, SelectTask task){
        Dialog dialog = new Dialog(Translator.translate("eximporter." + (export ? "export" : "import") + ".select.title"), 340, 125);
        dialog.setResizable(false); if(!root.endsWith("/")) root += "/"; final String reet = root;
        Label label = new Label(Translator.translate("eximporter." + (export ? "export" : "import") + ".select.desc"), 10, 10, 320, 20);
        Button okbutton = new Button(Translator.translate("eximporter." + (export ? "export" : "import") + ".select.continue"), 10, 75, 100, 20);
        SelectBox<String> selbox = new SelectBox<>(10, 40, 320, 24);
        List<ExImPorter> eximporter = PorterManager.getPorters(export);
        selbox.setVisibleCount(12); selbox.setElementHeight(20);
        for(ExImPorter porter : eximporter){ selbox.addElement(porter.getName()); }
        selbox.getSelectionButton().getStyle().setFontSize(20f);
        selbox.getSelectBoxElements().forEach(elm -> {
        	elm.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        	elm.getStyle().setFontSize(20f);
        });
        okbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){
        		ExImPorter porter = eximporter.get(selbox.getElementIndex(selbox.getSelection()));
        		String tetle = porter.getExtensions()[0]; dialog.close();
        		SettingsBox.open((export ? "Exporter" : "Importer") + " Settings", porter.getSettings(export), false, (settings) -> {
        			if(Settings.internal_filechooser()){
        				openFileChooser(title, reet, porter.getExtensions(), export, (file) -> {
        					task.process(file, porter, settings);
        				});
        				return;
        			}
        	        try(MemoryStack stack = MemoryStack.stackPush()){
        	        	PointerBuffer buffer = stack.mallocPointer(porter.getExtensions().length);
        	            for(String pattern : porter.getExtensions()) buffer.put(stack.UTF8(pattern)); buffer.flip(); String string = "";
        	    		if(export) string = TinyFileDialogs.tinyfd_saveFileDialog(title, reet, buffer, tetle);
        	    		else string = TinyFileDialogs.tinyfd_openFileDialog(title, reet, buffer, tetle, false);//log(string);
        	    		if(string != null && string.trim().length() > 0){
        	    			boolean ends = false;
        	    			for(int i = 1; i < porter.getExtensions().length; i++){
        	    				if(string.endsWith(porter.getExtensions()[i].replace("*", ""))){ ends = true; break; }
        	    			}
        	    			if(!ends) string += porter.getExtensions()[1].replace("*", "");
        	    			task.process(new File(string), porter, settings);
        	    		}
        	    		else task.process(null, porter, settings);
        	        }
        		});
        	}
        });
        dialog.getContainer().add(label);
        dialog.getContainer().add(selbox);
        dialog.getContainer().add(okbutton);
        dialog.show(FMTB.frame);
	}
	
	@FunctionalInterface
	public static interface AfterTask {
		
		public void process(File file);
		
	}
	
	@FunctionalInterface
	public static interface SelectTask {
		
		public void process(File file, ExImPorter porter, Map<String, Setting> list);
		
	}
	
	public static String[] TYPE_PNG = { "Portable Network Graphics", "*.png" };
	public static String[] TYPE_IMG = { "Image Files", "*.png", ".jpg", ".jpeg", ".bmp" };
	public static String[] TYPE_FMTB = { "FMT Save File", "*.fmtb" };

}
