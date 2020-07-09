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
	//
	public static NumberField oo_tex_x, oo_tex_y;
	public static Button oo_reset;
	public static NumberField oe_tex_sx, oe_tex_sy, oe_tex_ex, oe_tex_ey;
	public static Button oes_reset, oee_reset;
	public static NumberField of_tex_0x, of_tex_0y, of_tex_1x, of_tex_1y, of_tex_2x, of_tex_2y, of_tex_3x, of_tex_3y;
	public static Button of0_reset, of1_reset, of2_reset, of3_reset;
	//
	public static Button openview;
	private static ShapeType last;
	private static FaceUVType lasttype = FaceUVType.AUTOMATIC;
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
			//TODO gen def values based on auto mode
		});
		refreshWidget(FMTB.MODEL.getFirstSelection());
	}

	public static void refreshEntries(PolygonWrapper selected){
		if((last == null && selected == null) || (selected != null && last == selected.getType() && lasttype == selected.getFaceUVType(selface))){
			refreshWidgetValues(selected);
			return;
		}
		while(!uv_face.getElements().isEmpty()) uv_face.removeElement(0);
		if(selected == null){
			uv_face.addElement(FMTB.NO_POLYGON_SELECTED);
			last = null;
			selface = null;
			lasttype = FaceUVType.AUTOMATIC;
		}
		else{
			for(String face : selected.getTexturableFaceIDs()){
				uv_face.addElement(face);
			}
			selface = selected.getTexturableFaceIDs()[0];
			last = selected.getType();
			lasttype = selface == null ? FaceUVType.AUTOMATIC : selected.getFaceUVType(selface);
		}
		uv_type.setSelected((selface == null ? FaceUVType.AUTOMATIC : selected.getFaceUVType(selface)).name().toLowerCase(), true);
		refreshWidget(selected);
		refreshWidgetValues(selected);
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
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_only"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(oo_tex_x = new NumberField(4, pass += 24, 90, 20).setup("oo_tex_x", -8192, 8192, true));
					tempone.getContainer().add(oo_tex_y = new NumberField(102, pass, 90, 20).setup("oo_tex_y", -8192, 8192, true));
					tempone.getContainer().add(oo_reset = new Button(translate("editor.uv.fields.oo_reset"), 200, pass, 90, 20));
					//
					pass += 20;
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields.offset_only0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc = new Label(translate("editor.uv.fields.offset_only1"), 3, pass += 24, 290, 20));
					break;
				case OFFSET_ENDS:
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_ends_0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(oe_tex_sx = new NumberField(4, pass += 24, 90, 20).setup("oe_tex_sx", -8192, 8192, true));
					tempone.getContainer().add(oe_tex_sy = new NumberField(102, pass, 90, 20).setup("oe_tex_sy", -8192, 8192, true));
					tempone.getContainer().add(oes_reset = new Button(translate("editor.uv.fields.oes_reset"), 200, pass, 90, 20));
					//
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_ends_1"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(oe_tex_ex = new NumberField(4, pass += 24, 90, 20).setup("oe_tex_ex", -8192, 8192, true));
					tempone.getContainer().add(oe_tex_ey = new NumberField(102, pass, 90, 20).setup("oe_tex_ey", -8192, 8192, true));
					tempone.getContainer().add(oee_reset = new Button(translate("editor.uv.fields.oee_reset"), 200, pass, 90, 20));
					//
					pass += 20;
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields.offset_ends0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc = new Label(translate("editor.uv.fields.offset_ends1"), 3, pass += 24, 290, 20));
					break;
				case OFFSET_FULL:
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_full_0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(of_tex_0x = new NumberField(4, pass += 24, 90, 20).setup("of_tex_0x", -8192, 8192, true));
					tempone.getContainer().add(of_tex_0y = new NumberField(102, pass, 90, 20).setup("of_tex_0y", -8192, 8192, true));
					tempone.getContainer().add(of0_reset = new Button(translate("editor.uv.fields.of0_reset"), 200, pass, 90, 20));
					//
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_full_1"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(of_tex_1x = new NumberField(4, pass += 24, 90, 20).setup("of_tex_1x", -8192, 8192, true));
					tempone.getContainer().add(of_tex_1y = new NumberField(102, pass, 90, 20).setup("of_tex_1y", -8192, 8192, true));
					tempone.getContainer().add(of1_reset = new Button(translate("editor.uv.fields.of1_reset"), 200, pass, 90, 20));
					//
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_full_2"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(of_tex_2x = new NumberField(4, pass += 24, 90, 20).setup("of_tex_2x", -8192, 8192, true));
					tempone.getContainer().add(of_tex_2y = new NumberField(102, pass, 90, 20).setup("of_tex_2y", -8192, 8192, true));
					tempone.getContainer().add(of2_reset = new Button(translate("editor.uv.fields.of2_reset"), 200, pass, 90, 20));
					//
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_full_3"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(of_tex_3x = new NumberField(4, pass += 24, 90, 20).setup("of_tex_3x", -8192, 8192, true));
					tempone.getContainer().add(of_tex_3y = new NumberField(102, pass, 90, 20).setup("of_tex_3y", -8192, 8192, true));
					tempone.getContainer().add(of3_reset = new Button(translate("editor.uv.fields.of3_reset"), 200, pass, 90, 20));
					//
					pass += 20;
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

	private static void refreshWidgetValues(PolygonWrapper poly){
		float[] vals = poly == null ? new float[lasttype.arraylength] : poly.getFaceUVCoords(selface);
		switch(poly == null ? lasttype : poly.getFaceUVType(selface)){
			case AUTOMATIC: return;
			case OFFSET_ONLY:
				oo_tex_x.apply(vals[0]);
				oo_tex_y.apply(vals[1]);
				break;
			case OFFSET_ENDS:
				oe_tex_sx.apply(vals[0]);
				oe_tex_sy.apply(vals[1]);
				oe_tex_ex.apply(vals[2]);
				oe_tex_ey.apply(vals[3]);
				break;
			case OFFSET_FULL:
				of_tex_0x.apply(vals[0]);
				of_tex_0y.apply(vals[1]);
				of_tex_1x.apply(vals[2]);
				of_tex_1y.apply(vals[3]);
				of_tex_2x.apply(vals[4]);
				of_tex_2y.apply(vals[5]);
				of_tex_3x.apply(vals[6]);
				of_tex_3y.apply(vals[7]);
				break;
			default: return;
		}
	}
	
	public static String getSelection(){
		return selface;
	}

}
