package net.fexcraft.app.fmt.ui.editor;

import java.util.ArrayList;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent;
import org.liquidengine.legui.event.MouseClickEvent;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.wrappers.FaceUVType;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeType;

public class UVEditor extends EditorBase {

	public static TextField polygon_name;
	public static NumberField texture_x, texture_y;
	public static SelectBox<String> uv_face, uv_type;
	private static Button openview;
	private static ShapeType last;
	public static ArrayList<Component> tempcomp = new ArrayList<>();
	public static EditorWidget tempone;

	public UVEditor(){
		super();
		int pass = -20;
		EditorWidget general = new EditorWidget(this, translate("editor.uv.general"), 0, 0, 0, 0);
		general.getContainer().add(new Label(translate("editor.general.attributes.name"), 3, pass += 24, 290, 20));
		general.getContainer().add(polygon_name = new TextField(FMTB.NO_POLYGON_SELECTED, 3, pass += 24, 290, 20));
		polygon_name.addTextInputContentChangeEventListener(GeneralEditor.polygon_name.getTextInputContentChangeEvents().get(0));
		general.getContainer().add(new Label(translate("editor.general.shape.texture"), 3, pass += 24, 290, 20));
		general.getContainer().add(texture_x = new NumberField(4, pass += 24, 90, 20).setup("texx", -8192, 8192, true));
		general.getContainer().add(texture_y = new NumberField(102, pass, 90, 20).setup("texy", -8192, 8192, true));
		general.getContainer().add(openview = new Button(translate("editor.uv.general.openview"), 200, pass, 90, 20));
		general.getContainer().add(new Label(translate("editor.uv.general.face"), 3, pass += 24, 290, 20));
		general.getContainer().add(uv_face = new SelectBox<>(3, pass += 24, 290, 20));
		uv_face.addElement(FMTB.NO_POLYGON_SELECTED);
		uv_face.setElementHeight(20);
		uv_face.setVisibleCount(12);
		uv_face.addSelectBoxChangeSelectionEventListener(event -> updateFace(event));
		general.getContainer().add(new Label(translate("editor.uv.general.type"), 3, pass += 24, 290, 20));
		general.getContainer().add(uv_type = new SelectBox<>(3, pass += 24, 290, 20));
		for(FaceUVType type : FaceUVType.values()){
			uv_type.addElement(type.name().toLowerCase());
		}
		uv_type.setElementHeight(20);
		uv_type.setVisibleCount(6);
		uv_type.addSelectBoxChangeSelectionEventListener(event -> updateType(event));
		Button painttotex = new Button(translate("editor.general.attributes.painttotexture"), 3, 8 + (pass += 24), 290, 20);
		painttotex.getListenerMap().addListener(MouseClickEvent.class, GeneralEditor.painttotex.getListenerMap().getListeners(MouseClickEvent.class).get(0));
		general.getContainer().add(painttotex);
		general.setSize(296, pass + 52 + 4);
		this.addSub(general);
		pass = -20;
		//
		EditorWidget fields = new EditorWidget(this, translate("editor.uv.fields"), 0, 0, 0, 0);
		fields.setSize(296, pass + 52 + 4);
		this.addSub(fields);
		tempone = fields;
		//pass = -20;
		//
		reOrderWidgets();
	}

	private void updateFace(SelectBoxChangeSelectionEvent<String> event){
		//TODO
	}

	private void updateType(SelectBoxChangeSelectionEvent<String> event){
		//TODO
	}

	public static void refreshEntries(PolygonWrapper selected){
		if((last == null && selected == null) || (selected != null && last == selected.getType())) return;
		while(!uv_face.getElements().isEmpty()) uv_face.removeElement(0);
		if(selected == null){
			uv_face.addElement(FMTB.NO_POLYGON_SELECTED);
			last = null;
		}
		else{
			for(String face : selected.getTexturableFaceIDs()){
				uv_face.addElement(face);
			}
			last = selected.getType();
		}
		refreshWidget(selected);
	}

	private static void refreshWidget(PolygonWrapper poly){
		int pass = -20;
		//
		//TODO
		//
		tempone.setSize(296, pass + 52 + 4);
		Editors.uv.reOrderWidgets();
		
	}

}
