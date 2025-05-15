package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;

import net.fexcraft.app.fmt.env.PackDevEnv;
import net.fexcraft.app.fmt.polygon.Shape;
import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.ui.panels.QuickAddPanel;
import net.fexcraft.app.fmt.ui.workspace.WorkspaceViewer;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Panel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.utils.*;

public class Toolbar extends Panel {
	
	public static final Runnable NOTHING = () -> {};
	private UpdateCompound updcom = new UpdateCompound();

	public Toolbar(){
		super(0, 0, FMT.WIDTH, 30);
		this.setFocusable(false);
		Settings.applyBorderless(this);
		this.add(new Icon(0, "./resources/textures/icons/toolbar/info.png", () -> FMT.openLink("https://fexcraft.net/wiki/app/fmt")).addTooltip("toolbar.icon.info"));
		this.add(new Icon(1, "./resources/textures/icons/toolbar/settings.png", () -> SettingsDialog.open()).addTooltip("toolbar.icon.settings"));
		this.add(new Icon(2, "./resources/textures/icons/toolbar/profile.png", () -> ProfileDialog.open()).addTooltip("toolbar.icon.profile"));
		this.add(new Icon(3, "./resources/textures/icons/toolbar/save.png", () -> SaveHandler.save(FMT.MODEL, null, null, false, false)).addTooltip("toolbar.icon.save"));
		this.add(new Icon(4, "./resources/textures/icons/toolbar/open.png", () -> SaveHandler.openDialog(null)).addTooltip("toolbar.icon.open"));
		this.add(new Icon(5, "./resources/textures/icons/toolbar/new.png", () -> SaveHandler.newDialog()).addTooltip("toolbar.icon.new"));
		this.add(new ToolbarMenu(0, "file",
			new MenuButton(0, "file.new", () -> SaveHandler.newDialog()),
			new MenuButton(1, "file.open", () -> SaveHandler.openDialog(null)),
			new ToolbarMenu(-2, "file.recent",
				new MenuButton(0, "file.recent.none", () -> Settings.openRecent(0)),
				new MenuButton(1, "file.recent.none", () -> Settings.openRecent(1)),
				new MenuButton(2, "file.recent.none", () -> Settings.openRecent(2)),
				new MenuButton(3, "file.recent.none", () -> Settings.openRecent(3)),
				new MenuButton(4, "file.recent.none", () -> Settings.openRecent(4)),
				new MenuButton(5, "file.recent.none", () -> Settings.openRecent(5)),
				new MenuButton(6, "file.recent.none", () -> Settings.openRecent(6)),
				new MenuButton(7, "file.recent.none", () -> Settings.openRecent(7)),
				new MenuButton(8, "file.recent.none", () -> Settings.openRecent(8)),
				new MenuButton(9, "file.recent.none", () -> Settings.openRecent(9))
			).setLayerPreShow(layer -> {
				ArrayList<Component> list = new ArrayList<>();
				list.addAll(layer.getMenuComponents());
				for(int  i = 0; i < list.size(); i++){
					((MenuButton)list.get(i)).label.getTextState().setText(Settings.RECENT.get(i).getName());
				}
			}),
			new MenuButton(3, "file.save", () -> SaveHandler.saveDialogByState(null)),
			new MenuButton(4, "file.save_as", () -> SaveHandler.saveAsDialog(null)),
			new MenuButton(5, "file.import", ImportManager::_import),
			new MenuButton(6, "file.export", ExportManager::export),
			new MenuButton(7, "file.settings", SettingsDialog::open),
			new MenuButton(8, "workspace", WorkspaceViewer::show0),
			new MenuButton(9, "file.donate", () -> FMT.openLink("https://fexcraft.net/donate")),
			new MenuButton(10, "file.exit", () -> FMT.close(0))
		));
		this.add(new ToolbarMenu(1, "utils",
			new MenuButton(0, "utils.copy_selected", () -> FMT.MODEL.copySelected()),
			new ToolbarMenu(-1, "utils.clipboard",
				new MenuButton(0, "utils.clipboard.copy", () -> FMT.MODEL.copyToClipboard(false)),
				new MenuButton(1, "utils.clipboard.paste", () -> FMT.MODEL.pasteFromClipboard()),
				new MenuButton(2, "utils.clipboard.copy_grouped", () -> FMT.MODEL.copyToClipboard(true))
			),
			new MenuButton(2, "utils.undo"),
			new MenuButton(3, "utils.redo"),
			new MenuButton(4, "utils.reset_camera", () -> { FMT.CAM.reset(); }),
			new MenuButton(5, "utils.create_gif", () -> ImageHandler.createGif()),
			new MenuButton(6, "utils.screenshot", () -> ImageHandler.takeScreenshot(false)),
			new MenuButton(7, "utils.uv_viewer", () -> UVViewer.addIfAbsent()),
			new MenuButton(8, "utils.rescale", () -> FMT.MODEL.rescale()),
			new MenuButton(9, "utils.font_util", () -> FontUtils.open()),
			new ToolbarMenu(-10, "utils.converters",
					new MenuButton(0, "utils.converter.itemmodeltexjson", () -> ConverterUtils.runIMTJ()),
					new MenuButton(1, "Extract Materials", () -> ConverterUtils.exModelData())
			)
		));
		this.add(new ToolbarMenu(2, "editors",
			new MenuButton(0, "editors.polygon", () -> Editor.POLYGON_EDITOR.toggle()),
			//new MenuButton(1, "editors.group", () -> Editor.GROUP_EDITOR.toggle()),
			new MenuButton(1, "editors.pivot", () -> Editor.PIVOT_EDITOR.toggle()),
			new MenuButton(2, "editors.model", () -> Editor.MODEL_EDITOR.toggle()),
			new MenuButton(3, "editors.texture", () -> Editor.TEXTURE_EDITOR.toggle()),
			new MenuButton(4, "editors.uv", () -> Editor.UV_EDITOR.toggle()),
			new MenuButton(5, "editors.preview", () -> Editor.PREVIEW_EDITOR.toggle()),
			new MenuButton(6, "editors.config", () -> Editor.CONFIG_EDITOR.toggle())
		));
		this.add(new ToolbarMenu(3, "trees",
			new MenuButton(0, "trees.polygon", () -> Editor.POLYGON_TREE.toggle()),
			new MenuButton(1, "trees.pivot", () -> Editor.PIVOT_TREE.toggle()),
			new MenuButton(2, "trees.texture", () -> Editor.TEXTURE_TREE.toggle()),
			new MenuButton(3, "trees.helper", () -> Editor.PREVIEW_TREE.toggle())
		));
		this.add(new ToolbarMenu(4, "polygons",
			new MenuButton(0, "polygons.add_box", () -> QuickAddPanel.addBox()),
			new MenuButton(1, "polygons.add_shapebox", () -> QuickAddPanel.addShapebox()),
			new MenuButton(2, "polygons.add_cylinder", () -> QuickAddPanel.addCylinder()),
			new MenuButton(3, "polygons.add_marker", () -> QuickAddPanel.addMarker()),
			new MenuButton(4, "polygons.add_group", () -> QuickAddPanel.addGroup()),
			new MenuButton(5, "polygons.add_boundingbox", () -> QuickAddPanel.addScructBox()),
			new MenuButton(6, "polygons.add_rect_curve", () -> QuickAddPanel.addCurve(Shape.RECT_CURVE)),
			new MenuButton(7, "polygons.add_cyl_curve", () -> QuickAddPanel.addCurve(Shape.CYL_CURVE)),
			new MenuButton(8, "polygons.add_mesh_curve", () -> QuickAddPanel.addCurve(Shape.MESH_CURVE)),
			new MenuButton(9, "polygons.add_object")
		));
		this.add(new ToolbarMenu(5, "workspace", () -> WorkspaceViewer.show0()));
		this.add(new ToolbarMenu(6, "prototype", () -> PackDevEnv.toggle()));
		this.add(new ToolbarMenu(7, "exit", () -> FMT.close(0)));
		UpdateHandler.register(updcom);
	}

}
