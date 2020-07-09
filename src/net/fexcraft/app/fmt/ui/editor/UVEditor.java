package net.fexcraft.app.fmt.ui.editor;

import java.util.ArrayList;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.style.font.FontRegistry;

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
	private static String selface;

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
		tempone = new EditorWidget(this, translate("editor.uv.fields"), 0, 0, 0, 0);
		tempone.setSize(296, pass + 52 + 4);
		this.addSub(tempone);
		//pass = -20;
		//
		reOrderWidgets();
	}

	private void updateFace(SelectBoxChangeSelectionEvent<String> event){
		selface = event.getNewValue();
		refreshWidget(FMTB.MODEL.getFirstSelection());
	}

	private void updateType(SelectBoxChangeSelectionEvent<String> event){
		ArrayList<PolygonWrapper> polis = FMTB.MODEL.getSelected();
		FaceUVType type = FaceUVType.validate(event.getNewValue());
		polis.forEach(poly -> {
			poly.uvtypes.put(selface, type);
			if(type.arraylength == 0) poly.uvcoords.remove(selface);
			else poly.uvcoords.put(selface, new float[type.arraylength]);
		});
		refreshWidget(FMTB.MODEL.getFirstSelection());
	}

	public static void refreshEntries(PolygonWrapper selected){
		if((last == null && selected == null) || (selected != null && last == selected.getType())) return;
		while(!uv_face.getElements().isEmpty()) uv_face.removeElement(0);
		if(selected == null){
			uv_face.addElement(FMTB.NO_POLYGON_SELECTED);
			last = null;
			selface = null;
		}
		else{
			for(String face : selected.getTexturableFaceIDs()){
				uv_face.addElement(face);
			}
			selface = selected.getTexturableFaceIDs()[0];
			last = selected.getType();
		}
		refreshWidget(selected);
	}

	private static void refreshWidget(PolygonWrapper poly){
		int pass = -20;
		tempone.getContainer().clearChildComponents();
		//
		Label title = null, desc = null;
		if(poly != null){
			switch(poly.getFaceUVType(selface)){
				case AUTOMATIC:
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields.automatic0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc = new Label(translate("editor.uv.fields.automatic1"), 3, pass += 24, 290, 20));
					break;
				case OFFSET_ONLY:
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields.offset_only0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc = new Label(translate("editor.uv.fields.offset_only1"), 3, pass += 24, 290, 20));
					break;
				case OFFSET_ENDS:
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields.offset_ends0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc = new Label(translate("editor.uv.fields.offset_ends1"), 3, pass += 24, 290, 20));
					break;
				case OFFSET_FULL:
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields.offset_full0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc = new Label(translate("editor.uv.fields.offset_full1"), 3, pass += 24, 290, 20));
					break;
				default:
					break;
			}
		}
		else{
			tempone.getContainer().add(title = new Label(FMTB.NO_POLYGON_SELECTED, 3, pass += 24, 290, 20));
		}
		title.getStyle().setFont(FontRegistry.ROBOTO_BOLD);
		if(desc != null) desc.getStyle().setFont(FontRegistry.ROBOTO_BOLD);
		//
		tempone.setSize(296, pass + 52 + 4);
		Editors.uv.reOrderWidgets();
		
	}

}
