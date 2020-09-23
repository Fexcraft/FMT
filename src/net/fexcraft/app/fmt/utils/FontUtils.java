package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.File;
import java.util.zip.ZipFile;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeType;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.utils.ZipUtil;

public class FontUtils {
	
	private static float px, py, pz;
	private static float rx, ry, rz;
	private static float scale;
	private static File selected;
	private static int width = 300;
	private static String text;
	//
	private static float interletter_space, space_width;
	private static Char[] chars;

	public static void open(){
		Dialog dialog = new Dialog(translate("font_util.dialog"), width, 0);
		int passed = 0;
		dialog.setResizable(false);
		dialog.getContainer().add(new Label(translate("font_util.dialog.text"), 10, passed += 10, width - 20, 20));
		TextField input = new TextField(text = "text here", 10, passed += 24, width - 20, 20);
		input.addTextInputContentChangeEventListener(listener -> text = listener.getNewValue());
		dialog.getContainer().add(input);
		dialog.getContainer().add(new Label(translate("font_util.dialog.selection"), 10, passed += 28, width - 20, 20));
		SelectBox<FileWrapper> selectbox = new SelectBox<>(10, passed += 24, width - 20, 20);
		File root = new File("./resources/fonts");
		if(!root.exists()) root.mkdirs();
		if(root.listFiles().length == 0){
			selectbox.addElement(new FileWrapper(new File("no packs in ./resources/fonts")));
		}
		else{
			for(File file : root.listFiles()){
				if(!file.getName().endsWith(".fmtb")) continue;
				selectbox.addElement(new FileWrapper(file));
			}
			selected = root.listFiles()[0];
		}
		selectbox.addSelectBoxChangeSelectionEventListener(listener -> {
			selected = listener.getNewValue().file;
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
		dialog.getContainer().add(new Label(translate("font_util.dialog.scale"), 10, passed += 28, width - 20, 20));
		NumberField scale = new NumberField(10, passed += 24, 80, 20);
		scale.setup(0, 64, true, () -> { FontUtils.scale = scale.getValue(); });
		scale.apply(1);
		dialog.getContainer().add(scale);
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
		log("Collecting data to generate font...");
		log("Font Text: " + text);
		log("Font Location: " + px + ", " + py + ", " + pz);
		log("Font Rotation: " + rx + ", " + ry + ", " + rz);
		log("Font Scale: " + scale);
		if(selected == null || !selected.exists()){
			log("Invalid or no file selected, cancelling font generation.");
			return;
		}
		log("Font File: " + selected.getPath());
		loadFontModelFile(selected);
		String textgroupid = genTextGroupId();
		char[] textchars = text.toCharArray();
		char[] upperchars = text.toUpperCase().toCharArray();
		char[] lowerchars = text.toLowerCase().toCharArray();
		float passed = 0;
		for(int i = 0; i < textchars.length; i++){
			if(textchars[i] == ' '){
				passed += space_width + interletter_space;
				continue;
			}
			Char cher = findChar(textchars[i], lowerchars[i], upperchars[i]);
			if(cher == null){
				passed += space_width + interletter_space;
				log("Model for char '" + textchars[i] + "'/#" + Integer.toHexString(textchars[i]) + " not found!");
				continue;
			}
			for(PolygonWrapper wrapper : cher.wrappers){
				PolygonWrapper clone = wrapper.clone(FMTB.MODEL);
				clone.pos.xCoord -= cher.offset_x;
				clone.pos.yCoord -= cher.offset_y;
				clone.off.xCoord += passed;
				if(scale != 1f){
					if(clone.getType() == ShapeType.BOX){
						clone = clone.convertTo(ShapeType.SHAPEBOX);
					}
					GroupCompound.scalePoly(clone, scale);
				}
				clone.pos = clone.pos.addVector(px, py, pz);
				clone.rot = clone.rot.addVector(rx, ry, rz);
				FMTB.MODEL.add(clone, textgroupid, false);
			}
			passed += cher.width + interletter_space;
		}
		FMTB.MODEL.clearSelection();
		TurboList textgroup = FMTB.MODEL.getGroups().get(textgroupid);
		if(textgroup != null) textgroup.selected = true;
		FMTB.MODEL.recompile();
		log("Font generated, group '" + textgroupid + "' created.");
	}

	private static String genTextGroupId(){
		int idint = 0;
		while(FMTB.MODEL.getGroups().contains("text" + idint)) idint++;
		return "text" + idint;
	}

	private static void loadFontModelFile(File file){
		GroupCompound compound = null;
		try{
			boolean contains = ZipUtil.contains(file, "model.jtmt");
			ZipFile zip = new ZipFile(file);
			JsonObject obj = JsonUtil.getObjectFromInputStream(zip.getInputStream(zip.getEntry("model.jtmt")));
			if(contains){
				compound = SaveLoad.getModel(file, obj, false, true);
				//if(!compound.name.startsWith("font/")) compound.name = "font/" + compound.name;
			}
			else{
				DialogBox.showOK("font_util.load_pack.title", null, null, "font_util.load_pack.title.invalid_file");
			}
			zip.close();
			//compound.recompile();
			zip.close();
		}
		catch(Exception e){
			log(e);
			DialogBox.showOK("font_util.load_pack.title", null, null, "font_util.load_pack.title.errors");
		}
		compound.clearSelection();
		for(TurboList list : compound.getGroups()){
			if(list.button != null) list.button.removeFromTree();
			if(list.abutton != null) list.abutton.removeFromTree();
			if(list.pbutton != null) list.pbutton.removeFromSubTree();
		}
		if(compound.button != null) compound.button.removeFromTree();
		//
		if(compound.name.contains("_")){
			String[] arr = compound.name.split("_");
			interletter_space = Float.parseFloat(arr[1]);
			space_width = Float.parseFloat(arr[2]);
		}
		else{
			space_width = 4;
			interletter_space = 1;
		}
		chars = new Char[compound.getGroups().size()];
		int index = 0;
		for(TurboList list : compound.getGroups()){
			Char cher = new Char();
			String[] arr = list.id.split("_");
			if(arr[0].startsWith("U+")){
				cher.id = (char)Integer.parseInt(arr[0].substring(2), 16);
			}
			else{
				cher.id = arr[0].toCharArray()[0];
			}
			if(arr.length > 1) cher.width = Float.parseFloat(arr[1]);
			if(arr.length > 2) cher.height = Float.parseFloat(arr[2]);
			if(arr.length > 3) cher.offset_x = Float.parseFloat(arr[3]);
			if(arr.length > 4) cher.offset_y = Float.parseFloat(arr[4]);
			cher.wrappers = list;
			chars[index++] = cher;
		}
	}

	public static class Char {
		
		private char id;
		@SuppressWarnings("unused")
		private float width, height, offset_x, offset_y;
		private TurboList wrappers;
		
	}

	private static Char findChar(char c, char l, char u){
		for(Char cher : chars){
			if(cher.id == c) return cher;
		}
		for(Char cher : chars){
			if(cher.id == u) return cher;
		}
		for(Char cher : chars){
			if(cher.id == l) return cher;
		}
		return null;
	}
	
	private static class FileWrapper {
		
		private File file;

		private FileWrapper(File file){
			this.file = file;
		}
		
		@Override
		public String toString(){
			return file.getName();
		}
		
	}

	public static String replaceIndex(String text, int index, String replacement){
		String start = text.substring(0, index);
		String end00 = text.substring(index + 1);
		return start + replacement + end00;
	}

}
