package net.fexcraft.app.fmt.utils;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.File;
import java.util.Map;

import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.UserInterpanels.Button20;
import net.fexcraft.app.fmt.ui.UserInterpanels.Dialog20;
import net.fexcraft.app.fmt.ui.UserInterpanels.Label20;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.lib.common.utils.Print;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FileSelector {
	
	/** For general file needs. */
	public static final void select(String title, String root, String[] type, AfterTask task){
        try(MemoryStack stack = MemoryStack.stackPush()){
        	PointerBuffer buffer = stack.mallocPointer(type.length);
            for(String pattern : type) buffer.put(stack.UTF8(pattern)); buffer.flip();
    		String string = TinyFileDialogs.tinyfd_openFileDialog(title, root, buffer, type[0], false);
    		Print.console(string); if(string != null) task.process(new File(string));
        }
	}
	
	/** For selecting an Ex/Im-Porter first. */
	public static final void select(String title, String root, boolean export, SelectTask task){
        Dialog20 dialog = new Dialog20(Translator.translate("error.dialog_title"), 300, 100);
        Label20 label = new Label20(Translator.translate("error.feature_not_reimplemented_yet"), 10, 10, 200, 20);
        Button20 okbutton = new Button20("ok", 10, 50, 50, 20);
        okbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> { if(CLICK == e.getAction()) dialog.close(); });
        dialog.getContainer().add(label); dialog.getContainer().add(okbutton); dialog.show(FMTB.frame);
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
