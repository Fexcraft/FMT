package net.fexcraft.app.fmt.ui.editor;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.ArrayList;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.ClickListenerButton;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.ui.field.BoolButton;
import net.fexcraft.app.fmt.ui.field.ColorField;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeType;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class GeneralEditor extends EditorBase {

	public static TextField polygon_name;
	public static NumberField size_x, size_y, size_z;
	public static NumberField pos_x, pos_y, pos_z;
	public static NumberField off_x, off_y, off_z;
	public static NumberField rot_x, rot_y, rot_z;
	public static NumberField texture_x, texture_y;
	public static BoolButton side0_x, side0_y, side0_z;
	public static BoolButton side1_x, side1_y, side1_z;
	public static NumberField cyl0_x, cyl0_y, cyl0_z;
	public static NumberField cyl1_x, cyl1_y, cyl1_z;
	public static NumberField cyl2_x, cyl2_y;
	public static NumberField cyl3_x, cyl3_y, cyl3_z;
	public static BoolButton cyl4_x, cyl4_y, cyl4_z;
	public static BoolButton cyl5_x, cyl5_y, cyl5_z, cyl6_x;
	public static NumberField cyl6_y, cyl6_z;
	public static NumberField cyl7_x, cyl7_y, cyl7_z;
	public static NumberField[] corner_x, corner_y, corner_z;
	public static NumberField[][] texrect_a = new NumberField[6][8], texrect_b = new NumberField[6][4];
	public static ColorField marker_color;
	public static NumberField marker_scale, marker_angle;
	public static BoolButton marker_biped, marker_detached;
	public static SelectBox<String> polygon_group, polygon_type;
	public static Button painttotex, resettex;
	//
	public static final String NEWGROUP = "> new group <";

	public GeneralEditor(){
		super();
		int pass = -20;
		EditorWidget attributes = new EditorWidget(this, translate("editor.general.attributes"), 0, 0, 0, 0);
		attributes.getContainer().add(new Label(translate("editor.general.attributes.group"), 3, pass += 24, 290, 20));
		attributes.getContainer().add(polygon_group = new SelectBox<>(3, pass += 24, 290, 20));
		polygon_group.addElement(NEWGROUP);
		polygon_group.setVisibleCount(12);
		polygon_group.setElementHeight(20);
		polygon_group.addSelectBoxChangeSelectionEventListener(event -> {
			if(event.getNewValue().toString().equals(NEWGROUP)){
				Dialog dialog = new Dialog(translate("editor.general.attributes.new_group.title"), 300, 120);
				Label label = new Label(translate("editor.general.attributes.new_group.desc"), 10, 10, 280, 20);
				TextField input = new TextField("new_group", 10, 40, 280, 20);
				Button confirm = new Button(translate("editor.general.attributes.new_group.confirm"), 10, 70, 70, 20);
				Button cancel = new Button(translate("editor.general.attributes.new_group.cancel"), 90, 70, 70, 20);
				dialog.getContainer().add(input);
				dialog.getContainer().add(label);
				dialog.getContainer().add(confirm);
				dialog.getContainer().add(cancel);
				confirm.getListenerMap().addListener(MouseClickEvent.class, e -> {
					if(CLICK == e.getAction()){
						String text = GroupEditor.validateGroupName(input.getTextState().getText());
						if(text.equals("new_group")) text += "0";
						while(FMTB.MODEL.getGroups().contains(text))
							text += "_";
						FMTB.MODEL.getGroups().add(new TurboList(text));
						FMTB.MODEL.changeGroupOfSelected(FMTB.MODEL.getSelected(), text);
						dialog.close();
					}
				});
				cancel.getListenerMap().addListener(MouseClickEvent.class, e -> {
					if(CLICK == e.getAction()) dialog.close();
				});
				dialog.setResizable(false);
				dialog.show(event.getFrame());
			}
			else{
				FMTB.MODEL.changeGroupOfSelected(FMTB.MODEL.getSelected(), event.getNewValue().toString());
			}
		});
		attributes.getContainer().add(new Label(translate("editor.general.attributes.name"), 3, pass += 24, 290, 20));
		attributes.getContainer().add(polygon_name = new TextField(FMTB.NO_POLYGON_SELECTED, 3, pass += 24, 290, 20));
		polygon_name.addTextInputContentChangeEventListener(event -> {
			String validated = UserInterfaceUtils.validateString(event);
			if(FMTB.MODEL.getSelected().isEmpty()) return;
			PolygonWrapper wrapper;
			if(FMTB.MODEL.getSelected().size() == 1){
				wrapper = FMTB.MODEL.getFirstSelection();
				if(wrapper != null) wrapper.name = validated;
				wrapper.button.update();
			}
			else{
				ArrayList<PolygonWrapper> polis = FMTB.MODEL.getSelected();
				for(int i = 0; i < polis.size(); i++){
					wrapper = polis.get(i);
					if(wrapper != null){
						String str = validated.contains("_") ? "_" + i : validated.contains("-") ? "-" + i : validated.contains(" ") ? " " + i : validated.contains(".") ? "." + i : i + "";
						wrapper.name = validated + str;
						wrapper.button.update();
					}
				}
			}
		});
		attributes.getContainer().add(new Label(translate("editor.general.attributes.type"), 3, pass += 24, 290, 20));
		attributes.getContainer().add(polygon_type = new SelectBox<>(3, pass += 24, 290, 20));
		for(ShapeType type : ShapeType.getSupportedValues())
			polygon_type.addElement(type.name().toLowerCase());
		polygon_type.getSelectBoxElements().forEach(elm -> elm.getStyle().setFontSize(20f));
		polygon_type.setVisibleCount(12);
		polygon_type.setElementHeight(20);
		polygon_type.getSelectionButton().getStyle().setFontSize(20f);
		polygon_type.addSelectBoxChangeSelectionEventListener(event -> {
			FMTB.MODEL.changeTypeOfSelected(FMTB.MODEL.getSelected(), event.getNewValue().toString());
		});
		painttotex = new Button(translate("editor.general.attributes.painttotexture"), 3, 8 + (pass += 24), 290, 20);
		painttotex.getListenerMap().addListener(MouseClickEvent.class, event -> {
			if(event.getAction() != CLICK) return;
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
					poly.burnToTexture(texgroup.texture, null);
					poly.recompile();
					//TXO texgroup.texture.save();
					texgroup.texture.rebind();
					log("Polygon (" + poly.getTurboList().id + ":" + poly.name()  + ") painted into Texture.");
				}
				return;
			}
		});
		attributes.getContainer().add(painttotex);
		attributes.setSize(296, pass + 52 + 4);
		this.addSub(attributes);
		pass = -20;
		//
		EditorWidget shape = new EditorWidget(this, translate("editor.general.shape"), 0, 0, 0, 0);
		shape.getContainer().add(new Label(translate("editor.general.shape.size"), 3, pass += 24, 290, 20));
		shape.getContainer().add(size_x = new NumberField(4, pass += 24, 90, 20).setup("sizex", 0, Integer.MAX_VALUE, Settings.decimal_sizes()));
		shape.getContainer().add(size_y = new NumberField(102, pass, 90, 20).setup("sizey", 0, Integer.MAX_VALUE, Settings.decimal_sizes()));
		shape.getContainer().add(size_z = new NumberField(200, pass, 90, 20).setup("sizez", 0, Integer.MAX_VALUE, Settings.decimal_sizes()));
		shape.getContainer().add(new Label(translate("editor.general.shape.position"), 3, pass += 24, 290, 20));
		shape.getContainer().add(pos_x = new NumberField(4, pass += 24, 90, 20).setup("posx", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		shape.getContainer().add(pos_y = new NumberField(102, pass, 90, 20).setup("posy", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		shape.getContainer().add(pos_z = new NumberField(200, pass, 90, 20).setup("posz", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		shape.getContainer().add(new Label(translate("editor.general.shape.offset"), 3, pass += 24, 290, 20));
		shape.getContainer().add(off_x = new NumberField(4, pass += 24, 90, 20).setup("offx", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		shape.getContainer().add(off_y = new NumberField(102, pass, 90, 20).setup("offy", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		shape.getContainer().add(off_z = new NumberField(200, pass, 90, 20).setup("offz", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		shape.getContainer().add(new Label(translate("editor.general.shape.rotation"), 3, pass += 24, 290, 20));
		shape.getContainer().add(rot_x = new NumberField(4, pass += 24, 90, 20).setup("rotx", -360, 360, true));
		shape.getContainer().add(rot_y = new NumberField(102, pass, 90, 20).setup("roty", -360, 360, true));
		shape.getContainer().add(rot_z = new NumberField(200, pass, 90, 20).setup("rotz", -360, 360, true));
		shape.getContainer().add(new Label(translate("editor.general.shape.texture"), 3, pass += 24, 290, 20));
		shape.getContainer().add(texture_x = new NumberField(4, pass += 24, 90, 20).setup("texx", -1, 8192, true));
		shape.getContainer().add(texture_y = new NumberField(102, pass, 90, 20).setup("texy", -1, 8192, true));
		shape.getContainer().add(resettex = new ClickListenerButton(translate("editor.fields.reset.uppercase"), 200, pass, 90, 20, () -> {
			ArrayList<PolygonWrapper> polis = FMTB.MODEL.getSelected();
			for(PolygonWrapper wrapper : polis){
				wrapper.textureX = -1;
				wrapper.textureY = -1;
			}
			FMTB.MODEL.updateFields();
		}));
		shape.getContainer().add(new Label(translate("editor.general.shape.visibility_toggle"), 3, pass += 24, 290, 20));
		shape.getContainer().add(side0_x = new BoolButton("side0x", 4, pass += 24, 44, 20));
		shape.getContainer().add(side0_y = new BoolButton("side0y", 53, pass, 44, 20));
		shape.getContainer().add(side0_z = new BoolButton("side0z", 102, pass, 44, 20));
		shape.getContainer().add(side1_x = new BoolButton("side1x", 151, pass, 44, 20));
		shape.getContainer().add(side1_y = new BoolButton("side1y", 200, pass, 44, 20));
		shape.getContainer().add(side1_z = new BoolButton("side1z", 249, pass, 44, 20));
		shape.setSize(296, pass + 52);
		this.addSub(shape);
		pass = -20;
		//
		EditorWidget shapebox = new EditorWidget(this, translate("editor.general.shapebox"), 0, 0, 0, 0);
		corner_x = new NumberField[8];
		corner_y = new NumberField[8];
		corner_z = new NumberField[8];
		for(int i = 0; i < 8; i++){
			shapebox.getContainer().add(new ShapeboxLabel(i, shapebox, translate("editor.general.shapebox.corner" + i), 3, pass += 24, 290, 20));
			shapebox.getContainer().add(corner_x[i] = new NumberField(4, pass += 24, 90, 20).setup("cor" + i + "x", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
			shapebox.getContainer().add(corner_y[i] = new NumberField(102, pass, 90, 20).setup("cor" + i + "y", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
			shapebox.getContainer().add(corner_z[i] = new NumberField(200, pass, 90, 20).setup("cor" + i + "z", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		}
		shapebox.setSize(296, pass + 52);
		this.addSub(shapebox);
		pass = -20;
		//
		EditorWidget cylinder = new EditorWidget(this, translate("editor.general.cylinder"), 0, 0, 0, 0);
		cylinder.getContainer().add(new Label(translate("editor.general.cylinder.radius_length"), 3, pass += 24, 290, 20));
		cylinder.getContainer().add(cyl0_x = new NumberField(4, pass += 24, 90, 20).setup("cyl0x", 0.5f, Integer.MAX_VALUE, Settings.decimal_sizes()));
		cylinder.getContainer().add(cyl0_y = new NumberField(102, pass, 90, 20).setup("cyl0y", 1, Integer.MAX_VALUE, Settings.decimal_sizes()));
		cylinder.getContainer().add(cyl0_z = new NumberField(200, pass, 90, 20).setup("cyl0z", 0, Integer.MAX_VALUE, true));
		cylinder.getContainer().add(new Label(translate("editor.general.cylinder.segments_direction"), 3, pass += 24, 290, 20));
		cylinder.getContainer().add(cyl1_x = new NumberField(4, pass += 24, 90, 20).setup("cyl1x", 3, Integer.MAX_VALUE, false));
		cylinder.getContainer().add(cyl1_y = new NumberField(102, pass, 90, 20).setup("cyl1y", 0, 5, false));
		cylinder.getContainer().add(cyl1_z = new NumberField(200, pass, 90, 20).setup("cyl1z", 0, Integer.MAX_VALUE, false));
		cylinder.getContainer().add(new Label(translate("editor.general.cylinder.scale"), 3, pass += 24, 290, 20));
		cylinder.getContainer().add(cyl2_x = new NumberField(4, pass += 24, 90, 20).setup("cyl2x", 0, Integer.MAX_VALUE, true));
		cylinder.getContainer().add(cyl2_y = new NumberField(102, pass, 90, 20).setup("cyl2y", 0, Integer.MAX_VALUE, true));
		cylinder.getContainer().add(new Label(translate("editor.general.cylinder.top_offset"), 3, pass += 24, 290, 20));
		cylinder.getContainer().add(cyl3_x = new NumberField(4, pass += 24, 90, 20).setup("cyl3x", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		cylinder.getContainer().add(cyl3_y = new NumberField(102, pass, 90, 20).setup("cyl3y", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		cylinder.getContainer().add(cyl3_z = new NumberField(200, pass, 90, 20).setup("cyl3z", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
		cylinder.getContainer().add(new Label(translate("editor.general.cylinder.top_rotation"), 3, pass += 24, 290, 20));
		cylinder.getContainer().add(cyl7_x = new NumberField(4, pass += 24, 90, 20).setup("cyl7x", -360, 360, true));
		cylinder.getContainer().add(cyl7_y = new NumberField(102, pass, 90, 20).setup("cyl7y", -360, 360, true));
		cylinder.getContainer().add(cyl7_z = new NumberField(200, pass, 90, 20).setup("cyl7z", -360, 360, true));
		cylinder.getContainer().add(new Label(translate("editor.general.cylinder.visibility_toggle"), 3, pass += 24, 290, 20));
		cylinder.getContainer().add(cyl4_x = new BoolButton("cyl4x", 4, pass += 24, 44, 20));
		cylinder.getContainer().add(cyl4_y = new BoolButton("cyl4y", 53, pass, 44, 20));
		cylinder.getContainer().add(cyl5_x = new BoolButton("cyl5x", 102, pass, 44, 20));
		cylinder.getContainer().add(cyl5_y = new BoolButton("cyl5y", 151, pass, 44, 20));
		cylinder.getContainer().add(cyl4_z = new BoolButton("cyl4z", 200, pass, 44, 20));
		cylinder.getContainer().add(cyl5_z = new BoolButton("cyl5z", 249, pass, 44, 20));
		cylinder.getContainer().add(new Label(translate("editor.general.cylinder.radial_texture"), 3, pass += 24, 290, 20));
		cylinder.getContainer().add(cyl6_x = new BoolButton("cyl6x", 4, pass += 24, 90, 20));
		cylinder.getContainer().add(cyl6_y = new NumberField(102, pass, 90, 20).setup("cyl6y", 0, Integer.MAX_VALUE, true));
		cylinder.getContainer().add(cyl6_z = new NumberField(200, pass, 90, 20).setup("cyl6z", 0, Integer.MAX_VALUE, true));
		cylinder.setSize(296, pass + 52);
		this.addSub(cylinder);
		pass = -20;
		//
		EditorWidget marker = new EditorWidget(this, translate("editor.general.marker"), 0, 0, 0, 0);
		marker.getContainer().add(new Label(translate("editor.general.marker.color"), 3, pass += 24, 290, 20));
		marker.getContainer().add(marker_color = new ColorField(marker.getContainer(), "marker_colorx", 3, pass += 24, 290, 20));
		marker.getContainer().add(new Label(translate("editor.general.marker.biped_display"), 3, pass += 24, 290, 20));
		marker.getContainer().add(marker_biped = new BoolButton("marker_bipedx", 4, pass += 24, 90, 20));
		marker.getContainer().add(marker_angle = new NumberField(102, pass, 90, 20).setup("marker_anglex", -360, 360, true));
		marker.getContainer().add(marker_scale = new NumberField(200, pass, 90, 20).setup("marker_scalex", 0, 1024f, true));
		marker.getContainer().add(new Label(translate("editor.general.marker.detached"), 3, pass += 24, 290, 20));
		marker.getContainer().add(marker_detached = new BoolButton("marker_detachedx", 4, pass += 24, 90, 20));
		marker.setSize(296, pass + 52);
		this.addSub(marker);
		pass = -20;
		//
		/*final String[] faces = new String[]{ translate("editor.general.texrect.front"), translate("editor.general.texrect.back"), translate("editor.general.texrect.up"), translate("editor.general.texrect.down"), translate("editor.general.texrect.right"), translate("editor.general.texrect.left") };
		EditorWidget texrectA = new EditorWidget(this, translate("editor.general.texrect_a"), 0, 0, 0, 0);
		int[] tra = new int[24];
		for(int i = 0; i < 12; i++){
			tra[i * 2] = 1;
			tra[i * 2 + 1] = 4;
		}
		for(int r = 0; r < 12; r++){
			texrectA.getContainer().add(new Label(format("editor.general.texrect_a.face_" + (r % 2 == 0 ? "x" : "y"), faces[r / 2]), 3, pass += 24, 290, 20));
			for(int i = 0; i < 4; i++){
				String id = "texpos" + (r / 2) + ":" + ((i * 2) + (r % 2 == 1 ? 1 : 0)) + (r % 2 == 0 ? "x" : "y");
				if(i == 0) pass += 24;
				texrectA.getContainer().add(texrect_a[r % 6][r >= 6 ? i + 4 : i] = new NumberField(6 + (i * 72), pass, 66, 20).setup(id, 0, Integer.MAX_VALUE, true));
			}
		}
		texrectA.setSize(296, pass + 52);
		this.addSub(texrectA);
		pass = -20;
		EditorWidget texrectB = new EditorWidget(this, translate("editor.general.texrect_b"), 0, 0, 0, 0);
		for(int r = 0; r < 6; r++){
			texrectB.getContainer().add(new Label(format("editor.general.texrect_a.face_" + (r % 2 == 0 ? "x" : "y"), faces[r]), 3, pass += 24, 290, 20));
			for(int i = 0; i < 4; i++){
				String id = "texpos" + r + (i < 2 ? "s" : "e") + (i % 2 == 0 ? "x" : "y");
				if(i == 0) pass += 24;
				texrectB.getContainer().add(texrect_b[r][i] = new NumberField(6 + (i * 72), pass, 66, 20).setup(id, 0, Integer.MAX_VALUE, true));
			}
		}
		texrectB.setSize(296, pass + 52);
		this.addSub(texrectB);
		pass = -20;*/
		//
		reOrderWidgets();
		//texrectA.setMinimized(true);
		//texrectB.setMinimized(true);
	}

	public void refreshGroups(){
		while(!polygon_group.getElements().isEmpty())
			polygon_group.removeElement(0);
		for(TurboList list : FMTB.MODEL.getGroups())
			polygon_group.addElement(list.id);
		polygon_group.addElement(NEWGROUP);
	}

}
