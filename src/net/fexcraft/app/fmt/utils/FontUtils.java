package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.File;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.field.TextField;

public class FontUtils {
	
	private static float px, py, pz;
	private static float rx, ry, rz;
	private static File selected;
	private static int width = 300;
	private static String text;

	public static void open(){
		Dialog dialog = new Dialog(translate("font_util.dialog"), width, 0);
		int passed = 0;
		dialog.setResizable(false);
		dialog.getContainer().add(new Label(translate("font_util.dialog.text"), 10, passed += 10, width - 20, 20));
		TextField input = new TextField("test", 10, passed += 24, width - 20, 20);
		input.addTextInputContentChangeEventListener(listener -> text = UserInterfaceUtils.validateString(listener));
		dialog.getContainer().add(input);
		dialog.getContainer().add(new Label(translate("font_util.dialog.selection"), 10, passed += 28, width - 20, 20));
		SelectBox<File> selectbox = new SelectBox<File>(10, passed += 24, width - 20, 20);
		File root = new File("./resources/fonts");
		if(!root.exists()) root.mkdirs();
		if(root.listFiles().length == 0){
			selectbox.addElement(new File("no packs in ./resources/fonts"));
		}
		else{
			for(File file : root.listFiles()){
				if(!file.getName().endsWith(".fmtb")) continue;
				selectbox.addElement(file);
			}
		}
		selectbox.addSelectBoxChangeSelectionEventListener(listener -> {
			selected = listener.getNewValue();
		});
		dialog.getContainer().add(selectbox);
		//
		dialog.getContainer().add(new Label(translate("font_util.dialog.position"), 10, passed += 28, width - 20, 20));
		NumberField posx = new NumberField(10, passed += 24, 80, 20);
		posx.setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> { px = posx.getValue(); });
		NumberField posy = new NumberField(110, passed, 80, 20);
		posy.setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> { py = posy.getValue(); });
		NumberField posz = new NumberField(210, passed, 80, 20);
		posz.setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> { pz = posz.getValue(); });
		dialog.getContainer().add(posx);
		dialog.getContainer().add(posy);
		dialog.getContainer().add(posz);
		dialog.getContainer().add(new Label(translate("font_util.dialog.rotation"), 10, passed += 28, width - 20, 20));
		NumberField rotx = new NumberField(10, passed += 24, 80, 20);
		rotx.setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> { rx = rotx.getValue(); });
		NumberField roty = new NumberField(110, passed, 80, 20);
		roty.setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> { ry = roty.getValue(); });
		NumberField rotz = new NumberField(210, passed, 80, 20);
		rotz.setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> { rz = rotz.getValue(); });
		dialog.getContainer().add(rotx);
		dialog.getContainer().add(roty);
		dialog.getContainer().add(rotz);
		//
        Button button0 = new Button(translate("dialogbox.button.confirm"), 10, passed += 32, 100, 20);
        button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){
        		generate();
        		dialog.close();
        	}
        });
        dialog.setSize(width, passed + 48);
        dialog.getContainer().add(button0);
		dialog.show(FMTB.frame);
	}

	private static void generate(){
		//
	}

}
