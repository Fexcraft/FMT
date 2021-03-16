package net.fexcraft.app.fmt.ui.editor;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.ArrayList;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.style.font.FontRegistry;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.TexViewBox;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.FaceUVType;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;

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
	private static PolygonWrapper last = null;
	private static FaceUVType lasttype = FaceUVType.AUTOMATIC;
	public static ArrayList<Component> tempcomp = new ArrayList<>();
	public static EditorWidget tempone;
	public static Face selface;

	public UVEditor(){
		super();
		int pass = -20;
		EditorWidget general = new EditorWidget(this, translate("editor.uv.general"), 0, 0, 0, 0);
		general.getContainer().add(new Label(translate("editor.general.attributes.name"), 3, pass += 24, 290, 20));
		general.getContainer().add(polygon_name = new TextField(FMTB.NO_POLYGON_SELECTED, 3, pass += 24, 290, 20));
		polygon_name.addTextInputContentChangeEventListener(GeneralEditor.polygon_name.getTextInputContentChangeEvents().get(0));
		general.getContainer().add(new Label(translate("editor.general.shape.texture"), 3, pass += 24, 290, 20));
		general.getContainer().add(texture_x = new NumberField(4, pass += 24, 90, 20).setup("texx", -1, 8192, true));
		general.getContainer().add(texture_y = new NumberField(102, pass, 90, 20).setup("texy", -1, 8192, true));
		general.getContainer().add(openview = new Button(translate("editor.uv.general.openview"), 200, pass, 90, 20));
		openview.getListenerMap().addListener(MouseClickEvent.class, event -> {
			if(TextureManager.getGroupAmount() > 0 && event.getAction() == MouseClickAction.CLICK){
				String modeltex = FMTB.MODEL.texgroup == null ? null : FMTB.MODEL.texgroup.group;
				TexViewBox.open(modeltex == null ? TextureManager.getGroupsFE().get(0).group : modeltex);
			}
		});
		general.getContainer().add(new Label(translate("editor.uv.general.face"), 3, pass += 24, 290, 20));
		general.getContainer().add(uv_face = new SelectBox<>(3, pass += 24, 290, 20));
		uv_face.addElement(FMTB.NO_POLYGON_SELECTED);
		uv_face.setElementHeight(20);
		uv_face.setVisibleCount(12);
		uv_face.addSelectBoxChangeSelectionEventListener(event -> updateFace(event.getNewValue()));
		general.getContainer().add(new Label(translate("editor.uv.general.type"), 3, pass += 24, 290, 20));
		general.getContainer().add(uv_type = new SelectBox<>(3, pass += 24, 290, 20));
		for(FaceUVType type : FaceUVType.values()){
			uv_type.addElement(type.name().toLowerCase());
		}
		uv_type.setElementHeight(20);
		uv_type.setVisibleCount(7);
		uv_type.addSelectBoxChangeSelectionEventListener(event -> updateType(event.getNewValue()));
		Button painttotex = new Button(translate("editor.general.attributes.painttotexture"), 3, 8 + (pass += 24), 290, 20);
		painttotex.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(selface == null || listener.getAction() != CLICK) return;
			ArrayList<PolygonWrapper> selection = FMTB.MODEL.getSelected();
			if(FMTB.MODEL.texgroup == null && selection.size() < 2){
				DialogBox.showOK(null, null, null, "editor.general.attributes.painttotexture.notex");
			}
			else{
				for(PolygonWrapper poly : selection){
					TextureGroup texgroup = poly.getTurboList().getTextureGroup() == null ? FMTB.MODEL.texgroup : poly.getTurboList().getTextureGroup();
					if(texgroup == null || texgroup.texture == null){
						DialogBox.showOK(null, null, null, "editor.general.attributes.painttotexture.tex_not_found", "#" + (texgroup == null ? "no_group" : "group_no_tex"));
						return;
					}
					UVCoords coord = poly.getUVCoords(selface);
					float[][][] coords = new float[][][]{ poly.newTexturePosition(true, false)[coord.side().index()]};
					poly.burnToTexture(texgroup.texture, null, coords, coord.absolute(), coord.side().index());
					poly.recompile();
					//TXO texgroup.texture.save();
					texgroup.texture.rebind();
					//log(coords[0][0][0] + " " + coords[0][0][1]);
					//log(coords[0][1][0] + " " + coords[0][1][1]);
					log("Polygon (" + poly.getTurboList().id + ":" + poly.name() + ":" + selface.id()  + ") painted into Texture.");
				}
				return;
			}
		});
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

	public static void updateFace(String newval){
		try{
			refreshEntries(last, Face.byId(newval, false));
		}
		catch(Exception e){
			Logging.log("t: " + newval + "/" + Face.byId(newval, false), e);
		}
	}

	public static void updateType(String newval){
		if(selface == null) selface = Face.byId(uv_face.getSelection(), false);
		ArrayList<PolygonWrapper> polis = FMTB.MODEL.getSelected();
		FaceUVType type = FaceUVType.validate(newval);
		polis.forEach(poly -> {
			try{
				poly.getUVCoords(selface).set(type).value(poly.getDefAutoUVCoords(selface));
			}
			catch(Exception e){
				Logging.log("s/t: " + selface + "/" + type, e);
			}
		});
		refreshEntries(last, selface);
	}

	public static void refreshEntries(PolygonWrapper selected, Face self){
		if(selected == null){
			clearUVFaceSelBox();
			uv_face.addElement(FMTB.NO_POLYGON_SELECTED);
			selface = null;
			last = null;
			lasttype = FaceUVType.AUTOMATIC;
		}
		else{
			if(last == selected){
				if(self != null) selface = self;
			}
			else{
				last = selected;
				if(!last.isValidFace(selface)){
					selface = selected.getTexturableFaces()[0];
					clearUVFaceSelBox();
					for(Face face : selected.getTexturableFaces()){
						uv_face.addElement(face.id());
					}
				}
			}
			lasttype = selface == null ? FaceUVType.AUTOMATIC : selected.getUVCoords(selface).type();
		}
		uv_face.setSelected((selface == null ? "none" : selface.id()), true);
		uv_type.setSelected((selface == null ? FaceUVType.AUTOMATIC : selected.getUVCoords(selface).type()).name().toLowerCase(), true);
		refreshWidget(selected);
	}

	private static void clearUVFaceSelBox(){
		while(!uv_face.getElements().isEmpty()) uv_face.removeElement(0);
	}

	private static void refreshWidget(PolygonWrapper poly){
		int pass = -20;
		tempone.getContainer().clearChildComponents();
		//
		Label title = null, desc0 = null, desc1 = null;
		if(poly != null){
			FaceUVType type = poly.isValidFace(selface) ? poly.getUVCoords(selface).type() : FaceUVType.AUTOMATIC;
			String typestr = type.name().toLowerCase();
			switch(type){
				case AUTOMATIC:
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields.automatic0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc0 = new Label(translate("editor.uv.fields.automatic1"), 3, pass += 24, 290, 20));
					break;
				case ABSOLUTE:
				case OFFSET_ONLY:
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_only"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(oo_tex_x = new NumberField(4, pass += 24, 90, 20).setup("oo_tex_x", -8192, 8192, true));
					tempone.getContainer().add(oo_tex_y = new NumberField(102, pass, 90, 20).setup("oo_tex_y", -8192, 8192, true));
					tempone.getContainer().add(oo_reset = new Button(translate("editor.fields.reset.uppercase"), 200, pass, 90, 20));
					oo_reset.getListenerMap().addListener(MouseClickEvent.class, listener -> {
						if(listener.getAction() == MouseClickAction.CLICK){
							resetFields(type, 0);
						}
					});
					//
					pass += 20;
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields." + typestr + "0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc0 = new Label(translate("editor.uv.fields." + typestr + "1"), 3, pass += 24, 290, 20));
					break;
				case ABSOLUTE_ENDS:
				case OFFSET_ENDS:
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_ends_0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(oe_tex_sx = new NumberField(4, pass += 24, 90, 20).setup("oe_tex_sx", -8192, 8192, true));
					tempone.getContainer().add(oe_tex_sy = new NumberField(102, pass, 90, 20).setup("oe_tex_sy", -8192, 8192, true));
					tempone.getContainer().add(oes_reset = new Button(translate("editor.fields.reset.uppercase"), 200, pass, 90, 20));
					oes_reset.getListenerMap().addListener(MouseClickEvent.class, listener -> {
						if(listener.getAction() == MouseClickAction.CLICK){
							resetFields(type, 0);
						}
					});
					//
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_ends_1"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(oe_tex_ex = new NumberField(4, pass += 24, 90, 20).setup("oe_tex_ex", -8192, 8192, true));
					tempone.getContainer().add(oe_tex_ey = new NumberField(102, pass, 90, 20).setup("oe_tex_ey", -8192, 8192, true));
					tempone.getContainer().add(oee_reset = new Button(translate("editor.fields.reset.uppercase"), 200, pass, 90, 20));
					oee_reset.getListenerMap().addListener(MouseClickEvent.class, listener -> {
						if(listener.getAction() == MouseClickAction.CLICK){
							resetFields(type, 1);
						}
					});
					//
					pass += 20;
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields." + typestr + "0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc0 = new Label(translate("editor.uv.fields." + typestr + "1"), 3, pass += 24, 290, 20));
					break;
				case ABSOLUTE_FULL:
				case OFFSET_FULL:
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_full_0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(of_tex_0x = new NumberField(4, pass += 24, 90, 20).setup("of_tex_0x", -8192, 8192, true));
					tempone.getContainer().add(of_tex_0y = new NumberField(102, pass, 90, 20).setup("of_tex_0y", -8192, 8192, true));
					tempone.getContainer().add(of0_reset = new Button(translate("editor.fields.reset.uppercase"), 200, pass, 90, 20));
					of0_reset.getListenerMap().addListener(MouseClickEvent.class, listener -> {
						if(listener.getAction() == MouseClickAction.CLICK){
							resetFields(type, 0);
						}
					});
					//
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_full_1"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(of_tex_1x = new NumberField(4, pass += 24, 90, 20).setup("of_tex_1x", -8192, 8192, true));
					tempone.getContainer().add(of_tex_1y = new NumberField(102, pass, 90, 20).setup("of_tex_1y", -8192, 8192, true));
					tempone.getContainer().add(of1_reset = new Button(translate("editor.fields.reset.uppercase"), 200, pass, 90, 20));
					of1_reset.getListenerMap().addListener(MouseClickEvent.class, listener -> {
						if(listener.getAction() == MouseClickAction.CLICK){
							resetFields(type, 1);
						}
					});
					//
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_full_2"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(of_tex_2x = new NumberField(4, pass += 24, 90, 20).setup("of_tex_2x", -8192, 8192, true));
					tempone.getContainer().add(of_tex_2y = new NumberField(102, pass, 90, 20).setup("of_tex_2y", -8192, 8192, true));
					tempone.getContainer().add(of2_reset = new Button(translate("editor.fields.reset.uppercase"), 200, pass, 90, 20));
					of2_reset.getListenerMap().addListener(MouseClickEvent.class, listener -> {
						if(listener.getAction() == MouseClickAction.CLICK){
							resetFields(type, 2);
						}
					});
					//
					tempone.getContainer().add(new Label(translate("editor.uv.fields.offset_full_3"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(of_tex_3x = new NumberField(4, pass += 24, 90, 20).setup("of_tex_3x", -8192, 8192, true));
					tempone.getContainer().add(of_tex_3y = new NumberField(102, pass, 90, 20).setup("of_tex_3y", -8192, 8192, true));
					tempone.getContainer().add(of3_reset = new Button(translate("editor.fields.reset.uppercase"), 200, pass, 90, 20));
					of3_reset.getListenerMap().addListener(MouseClickEvent.class, listener -> {
						if(listener.getAction() == MouseClickAction.CLICK){
							resetFields(type, 3);
						}
					});
					//
					pass += 20;
					tempone.getContainer().add(title = new Label(translate("editor.uv.fields." + typestr + "0"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc0 = new Label(translate("editor.uv.fields." + typestr + "1"), 3, pass += 24, 290, 20));
					tempone.getContainer().add(desc1 = new Label(translate("editor.uv.fields." + typestr + "2"), 3, pass += 24, 290, 20));
					break;
				default:
					break;
			}
		}
		else{
			tempone.getContainer().add(title = new Label(FMTB.NO_POLYGON_SELECTED, 3, pass += 24, 290, 20));
		}
		title.getStyle().setFont(FontRegistry.ROBOTO_BOLD);
		if(desc0 != null) desc0.getStyle().setFont(FontRegistry.ROBOTO_BOLD);
		if(desc1 != null) desc1.getStyle().setFont(FontRegistry.ROBOTO_BOLD);
		//
		tempone.setSize(296, pass + 52 + 4);
		Editors.uv.reOrderWidgets();
		refreshWidgetValues(poly);
	}

	private static void refreshWidgetValues(PolygonWrapper poly){
		poly = selface == null ? null : poly;
		UVCoords coords = poly == null ? null : poly.getUVCoords(selface);
		float[] vals = poly == null ? new float[lasttype.arraylength] : coords.value();
		switch(poly == null ? lasttype : coords.type()){
			case AUTOMATIC: return;
			case ABSOLUTE:
			case OFFSET_ONLY:
				oo_tex_x.apply(vals[0]);
				oo_tex_y.apply(vals[1]);
				break;
			case ABSOLUTE_ENDS:
			case OFFSET_ENDS:
				oe_tex_sx.apply(vals[0]);
				oe_tex_sy.apply(vals[1]);
				oe_tex_ex.apply(vals[2]);
				oe_tex_ey.apply(vals[3]);
				break;
			case ABSOLUTE_FULL:
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

	private static void resetFields(FaceUVType type, int row){
		float[] vals = last == null ? new float[lasttype.arraylength] : last.getDefAutoUVCoords(selface);
		switch(last == null ? lasttype : last.getUVCoords(selface).type()){
			case AUTOMATIC: return;
			case ABSOLUTE:
			case OFFSET_ONLY:
				oo_tex_x.apply(vals[0]);
				oo_tex_y.apply(vals[1]);
				FMTB.MODEL.updateValue(oo_tex_x, oo_tex_x.id());
				FMTB.MODEL.updateValue(oo_tex_y, oo_tex_y.id());
				return;
			case ABSOLUTE_ENDS:
			case OFFSET_ENDS:
				if(row == 0){
					oe_tex_sx.apply(vals[0]);
					oe_tex_sy.apply(vals[1]);
					FMTB.MODEL.updateValue(oe_tex_sx, oe_tex_sx.id());
					FMTB.MODEL.updateValue(oe_tex_sy, oe_tex_sy.id());
				}
				else{
					oe_tex_ex.apply(vals[2]);
					oe_tex_ey.apply(vals[3]);
					FMTB.MODEL.updateValue(oe_tex_ex, oe_tex_ex.id());
					FMTB.MODEL.updateValue(oe_tex_ey, oe_tex_ey.id());
				}
				return;
			case ABSOLUTE_FULL:
			case OFFSET_FULL:
				if(row == 0){
					of_tex_0x.apply(vals[0]);
					of_tex_0y.apply(vals[1]);
					FMTB.MODEL.updateValue(of_tex_0x, of_tex_0x.id());
					FMTB.MODEL.updateValue(of_tex_0y, of_tex_0y.id());
					return;
				}
				if(row == 1){
					of_tex_1x.apply(vals[2]);
					of_tex_1y.apply(vals[3]);
					FMTB.MODEL.updateValue(of_tex_1x, of_tex_1x.id());
					FMTB.MODEL.updateValue(of_tex_1y, of_tex_1y.id());
					return;
				}
				if(row == 2){
					of_tex_2x.apply(vals[4]);
					of_tex_2y.apply(vals[5]);
					FMTB.MODEL.updateValue(of_tex_2x, of_tex_2x.id());
					FMTB.MODEL.updateValue(of_tex_2y, of_tex_2y.id());
					return;
				}
				if(row == 3){
					of_tex_3x.apply(vals[6]);
					of_tex_3y.apply(vals[7]);
					FMTB.MODEL.updateValue(of_tex_3x, of_tex_3x.id());
					FMTB.MODEL.updateValue(of_tex_3y, of_tex_3y.id());
					return;
				}
				return;
			default: return;
		}
	}
	
	public static Face getSelection(){
		return selface;
	}

	public static PolygonWrapper getLast(){
		return last;
	}

}
