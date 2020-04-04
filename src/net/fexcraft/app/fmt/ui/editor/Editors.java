package net.fexcraft.app.fmt.ui.editor;

import java.util.ArrayList;

import org.liquidengine.legui.component.Frame;

import net.fexcraft.app.fmt.FMTB;

public class Editors {
	
	public static GeneralEditor general;
	public static ModelGroupEditor modelgroup;
	public static PreviewEditor preview;
	public static TextureEditor texture;
	//
	public static final ArrayList<EditorBase> editors = new ArrayList<>();

	public static void initializeEditors(Frame frame){
		frame.getContainer().add(general = new GeneralEditor());
		frame.getContainer().add(modelgroup = new ModelGroupEditor());
		frame.getContainer().add(texture = new TextureEditor());
		frame.getContainer().add(preview = new PreviewEditor());
	}
	
	public static void hideAll(){
		for(EditorBase editor : editors) editor.hide();
	}
	
	public static void show(String type){
		hideAll();
		TextureEditor.toggleBucketMode(null);
		switch(type){
			case "general": general.show(); break;
			case "model": case "group":
			case "modelgroup": modelgroup.show(); break;
			case "helper": case "preview":
			case "helperpreview": preview.show(); break;
			case "texture": texture.show(); break;
		}
	}
	
	public static boolean anyVisible(){
		for(EditorBase editor : editors) if(editor.isVisible()) return true; return false;
	}
	
	public static EditorBase getVisible(){
		for(EditorBase editor : editors) if(editor.isVisible()) return editor; return null;
	}

	public static void toggleWidget(int i){
		if(i < 0) return;
		if(anyVisible()){
			EditorBase editor = getVisible();
			if(i >= editor.widgets.size()) return;
			editor.widgets.get(i).toggle();
		}
		else{
			if(i >= editors.size()) return;
			hideAll(); editors.get(i).show();
		}
	}

	public static void resize(int width, int height){
		for(EditorBase editor : editors){
			editor.setSize(editor.getSize().x, FMTB.HEIGHT - 30);
			editor.scrollable.setSize(editor.scrollable.getSize().x, FMTB.HEIGHT - 80);
			editor.reOrderWidgets();
		}
	}

}
