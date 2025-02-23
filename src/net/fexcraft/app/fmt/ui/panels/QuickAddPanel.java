package net.fexcraft.app.fmt.ui.panels;

import com.google.common.io.Files;
import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.ToolbarMenu;
import net.fexcraft.app.fmt.ui.editors.EditorPanel;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;

import java.io.File;

import static net.fexcraft.app.fmt.ui.EditorComponent.HEIGHT;
import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class QuickAddPanel extends EditorPanel {

	private NumberField field;

	public QuickAddPanel(){
		super("quick", "quick_add", "editor.component.quick");
		setPosition(Editor.WIDTH, I_SIZE * 3);
		ex_x = 310;
		ex_y = 30;
		//
		int yoff = 1, xoff = 35, size = 28;
		this.add(new Icon(0, size, 2, xoff, yoff, "./resources/textures/icons/polygon/box.png", () -> addBox()).addTooltip(lang_prefix + ".add_box"));
		this.add(new Icon(1, size, 2, xoff, yoff, "./resources/textures/icons/polygon/shapebox.png", () -> addShapebox()).addTooltip(lang_prefix + ".add_shapebox"));
		this.add(new Icon(2, size, 2, xoff, yoff, "./resources/textures/icons/polygon/cylinder.png", () -> addCylinder()).addTooltip(lang_prefix + ".add_cylinder"));
		this.add(new Icon(3, size, 2, xoff, yoff, "./resources/textures/icons/polygon/boundingbox.png", () -> addScructBox()).addTooltip(lang_prefix + ".add_boundingbox"));
		this.add(new Icon(4, size, 2, xoff, yoff, "./resources/textures/icons/polygon/object.png", () -> {}).addTooltip(lang_prefix + ".add_object"));
		this.add(new Icon(5, size, 2, xoff, yoff, "./resources/textures/icons/polygon/marker.png", () -> addMarker()).addTooltip(lang_prefix + ".add_marker"));
		this.add(new Icon(6, size, 2, xoff, yoff, "./resources/textures/icons/polygon/group.png", () -> addGroup()).addTooltip(lang_prefix + ".add_group"));
		this.add(new Icon(7, size, 2, xoff, yoff, "./resources/textures/icons/polygon/voxel.png", () -> {}).addTooltip(lang_prefix + ".add_voxel"));
		this.add(new Icon(8, size, 2, xoff, yoff, "./resources/textures/icons/polygon/copy_sel.png", () -> FMT.MODEL.copySelected()).addTooltip(lang_prefix + ".copy_sel"));
	}

	public static void addBox(){
		FMT.MODEL.add(null, null, new Box(null));
		hideMenu();
	}

	public static void addShapebox(){
		FMT.MODEL.add(null, null, new Shapebox(null));
		hideMenu();
	}

	public static void addScructBox(){
		FMT.MODEL.add(null, null, new StructBox(null));
		hideMenu();
	}

	public static void addGroup(){
		String gn = "group" + FMT.MODEL.allgroups().size();
		while(FMT.MODEL.contains(gn)) gn += "0";
		hideMenu();
		Dialog dialog = new Dialog(translate("group_add.dialog"), 420, 120);
		dialog.getContainer().add(new Label(translate("group_add.dialog.name"), 10, 5, 400, 20));
		TextField name = new TextField(gn, 10, 30, 400, 30, false);
		dialog.getContainer().add(name);
		dialog.getContainer().add(new RunButton("dialog.button.confirm", 310, 70, 100, 20, () -> {
			FMT.MODEL.addGroup(null, name.getTextState().getText());
			dialog.close();
		}));
		dialog.getContainer().add(new RunButton("dialog.button.cancel", 200, 70, 100, 20, () -> dialog.close()));
		dialog.setResizable(false);
		dialog.show(FMT.FRAME);
	}

	public static void addCylinder(){
		FMT.MODEL.add(null, null, new Cylinder(null));
		hideMenu();
	}

	public static void addMarker(){
		FMT.MODEL.add(null, null, new Marker(null));
		hideMenu();
	}

	private static void hideMenu(){
		if(Settings.HIDE_MENU_AFTER_POLYGON.value) ToolbarMenu.hideAll();
	}

	public static void addCurve(Shape sha){
		if(sha.is(Shape.CYL_CURVE)){

		}
		else if(sha.is(Shape.MESH_CURVE)){
			FMT.MODEL.add(null, null, new CurvedMesh(null));
		}
		else{
			FMT.MODEL.add(null, null, new RectCurve(null));
		}
		hideMenu();
	}

}
