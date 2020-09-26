package net.fexcraft.app.fmt.ui.tree;

import static net.fexcraft.app.fmt.ui.UserInterfaceUtils.hide;
import static net.fexcraft.app.fmt.ui.UserInterfaceUtils.show;
import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.color.ColorConstants;

import com.google.common.io.Files;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.FileSelector;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.ui.editor.GroupEditor;
import net.fexcraft.app.fmt.ui.editor.PreviewEditor;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.mc.utils.Static;

public class TreeGroup extends Panel {

	protected GroupCompound compound;
	private boolean animations;
	private TurboList list;
	private TreeBase tree;
	private Label label;
	private TextureGroup texgroup;

	public TreeGroup(TreeBase base){
		super(0, 0, base.getSize().x - 12, 20);
		tree = base;
		this.add(label = new Label("group-label", 0, 0, (int)getSize().x, 20));
		Consumer<Boolean> con;
		Settings.THEME_CHANGE_LISTENER.add(con = bool -> {
			label.getStyle().setFont("roboto-bold");
			label.getStyle().setPadding(0, 0, 0, 5);
			label.getStyle().setBorderRadius(0);
			//if(bool){
			label.getStyle().setTextColor(ColorConstants.darkGray());
			//}
			//else{
			//	label.getStyle().setTextColor(ColorConstants.lightGray());
			//}
			getStyle().getBorder().setEnabled(false);
			if(compound != null || list != null || texgroup != null) updateColor();
		});
		con.accept(Settings.darktheme());
	}

	public TreeGroup(TreeBase base, TurboList group, boolean flag){
		this(base);
		list = group;
		updateColor();
		animations = flag;
		if(!flag) Static.halt(0);
		this.add(new TreeIcon((int)getSize().x - 42, 0, "group_visible", () -> {
			list.visible = !list.visible;
			updateColor();
		}, "visibility"));
		this.add(new TreeIcon((int)getSize().x - 64, 0, "group_edit", () -> {
			Editors.show("group");
		}, "edit"));
		this.add(new TreeIcon((int)getSize().x - 86, 0, "group_minimize", () -> toggle(!list.aminimized), "minimize"));
		label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() != CLICK || listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT) return;
			boolean sell = list.selected;
			FMTB.MODEL.clearSelection();
			list.selected = !sell;
			FMTB.MODEL.updateFields();
			FMTB.MODEL.lastselected = null;
			updateColor();
		});
		this.recalculateSize();
	}

	public TreeGroup(TreeBase base, TurboList group){
		this(base);
		list = group;
		updateColor();
		this.add(new TreeIcon((int)getSize().x - 20, 0, "group_delete", () -> {
			DialogBox.showYN(null, () -> {
				FMTB.MODEL.getGroups().remove(list.id);
			}, null, "tree.polygon.remove_group", "#" + list.id);
		}, "delete"));
		this.add(new TreeIcon((int)getSize().x - 42, 0, "group_visible", () -> {
			list.visible = !list.visible;
			updateColor();
		}, "visibility"));
		this.add(new TreeIcon((int)getSize().x - 64, 0, "group_edit", () -> {
			Editors.show("group");
		}, "edit"));
		this.add(new TreeIcon((int)getSize().x - 86, 0, "group_minimize", () -> toggle(!list.minimized), "minimize"));
		label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == CLICK && listener.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
				boolean sell = list.selected;
				if(!GGR.isShiftDown()){
					FMTB.MODEL.clearSelection();
				}
				list.selected = !sell;
				FMTB.MODEL.updateFields();
				FMTB.MODEL.lastselected = null;
				updateColor();
				GroupCompound.SELECTED_POLYGONS = FMTB.MODEL.countSelectedMRTs();
				return;
			}
			if(listener.getAction() == CLICK && listener.getButton() == MouseButton.MOUSE_BUTTON_RIGHT){
				tree.select(this);
				return;
			}
		});
		this.add(new TreeIcon((int)getSize().x - 108, 0, "group_up", () -> {
			onScroll(+1);
		}, "up"));
		this.add(new TreeIcon((int)getSize().x - 130, 0, "group_down", () -> {
			onScroll(-1);
		}, "down"));
		hide(this.getChildComponents().get(5));
		hide(this.getChildComponents().get(6));
		this.recalculateSize();
	}

	public TreeGroup(TreeBase base, GroupCompound group){
		this(base);
		compound = group;
		updateColor();
		this.add(new TreeIcon((int)getSize().x - 20, 0, "group_delete", () -> {
			HelperCollector.LOADED.remove(index());
			this.removeFromTree();
			tree.reOrderGroups();
		}, "delete"));
		this.add(new TreeIcon((int)getSize().x - 42, 0, "group_visible", () -> {
			compound.visible = !compound.visible;
			updateColor();
		}, "visibility"));
		this.add(new TreeIcon((int)getSize().x - 64, 0, "group_edit", () -> {
			Editors.show("preview");
		}, "edit"));
		this.add(new TreeIcon((int)getSize().x - 86, 0, "group_clone", () -> {
			GroupCompound newcomp = null, parent = compound;
			if(parent.name.startsWith("fmtb/")){
				newcomp = HelperCollector.loadFMTB(parent.origin);
			}
			else if(parent.name.startsWith("frame/")){
				newcomp = HelperCollector.loadFrame(parent.origin);
			}
			else{
				ExImPorter porter = compound.porter;
				HashMap<String, Setting> map = new HashMap<>();
				porter.getSettings(false).forEach(setting -> map.put(setting.getId(), setting));
				newcomp = HelperCollector.load(parent.origin, porter, map);
			}
			if(newcomp == null){
				log("Error on creating clone.");
				return;
			}
			if(parent.pos != null) newcomp.pos = new Vec3f(parent.pos);
			if(parent.rot != null) newcomp.rot = new Vec3f(parent.rot);
			if(parent.scale != null) newcomp.scale = new Vec3f(parent.scale);
		}, "clone"));
		this.add(new TreeIcon((int)getSize().x - 108, 0, "group_minimize", () -> toggle(!compound.minimized), "minimize"));
		this.add(new TreeIcon((int)getSize().x - 130, 0, "group_up", () -> {
			onScroll(+1);
		}, "up"));
		this.add(new TreeIcon((int)getSize().x - 152, 0, "group_down", () -> {
			onScroll(-1);
		}, "down"));
		hide(this.getChildComponents().get(6));
		hide(this.getChildComponents().get(7));
		label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == CLICK && listener.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
				GroupCompound model = HelperCollector.getSelected();
				if(selected()){
					HelperCollector.SELECTED = -1;
					updateColor();
					compound.setGroupsSelected(false);
				}
				else{
					if(HelperCollector.SELECTED > -1) model = HelperCollector.getSelected();
					HelperCollector.SELECTED = index();
					compound.setGroupsSelected(true);
				}
				if(model != null){
					model.button.updateColor();
					model.setGroupsSelected(false);
				}
				updateColor();
				model = HelperCollector.getSelected();
				if(model == null){
					PreviewEditor.helper_name.getTextState().setText(FMTB.NO_PREVIEW_SELECTED);
					PreviewEditor.pos_x.apply(0);
					PreviewEditor.pos_y.apply(0);
					PreviewEditor.pos_z.apply(0);
					PreviewEditor.poss_x.apply(0);
					PreviewEditor.poss_y.apply(0);
					PreviewEditor.poss_z.apply(0);
					PreviewEditor.rot_x.apply(0);
					PreviewEditor.rot_y.apply(0);
					PreviewEditor.rot_z.apply(0);
					PreviewEditor.size_x.apply(1);
					PreviewEditor.size_y.apply(1);
					PreviewEditor.size_z.apply(1);
					PreviewEditor.size16_x.apply(16);
					PreviewEditor.size16_y.apply(16);
					PreviewEditor.size16_z.apply(16);
				}
				else{
					PreviewEditor.helper_name.getTextState().setText(model.name.substring(model.name.indexOf('/') + 1));
					PreviewEditor.pos_x.apply(model.pos == null ? 0 : model.pos.xCoord);
					PreviewEditor.pos_y.apply(model.pos == null ? 0 : model.pos.yCoord);
					PreviewEditor.pos_z.apply(model.pos == null ? 0 : model.pos.zCoord);
					PreviewEditor.poss_x.apply(model.pos == null ? 0 : model.pos.xCoord * 16);
					PreviewEditor.poss_y.apply(model.pos == null ? 0 : model.pos.yCoord * 16);
					PreviewEditor.poss_z.apply(model.pos == null ? 0 : model.pos.zCoord * 16);
					PreviewEditor.rot_x.apply(model.rot == null ? 0 : model.rot.xCoord);
					PreviewEditor.rot_y.apply(model.rot == null ? 0 : model.rot.yCoord);
					PreviewEditor.rot_z.apply(model.rot == null ? 0 : model.rot.zCoord);
					PreviewEditor.size_x.apply(model.scale == null ? 1 : model.scale.xCoord);
					PreviewEditor.size_y.apply(model.scale == null ? 1 : model.scale.yCoord);
					PreviewEditor.size_z.apply(model.scale == null ? 1 : model.scale.zCoord);
					PreviewEditor.size16_x.apply(model.scale == null ? 1 : model.scale.xCoord * 16);
					PreviewEditor.size16_y.apply(model.scale == null ? 1 : model.scale.yCoord * 16);
					PreviewEditor.size16_z.apply(model.scale == null ? 1 : model.scale.zCoord * 16);
				}
				return;
			}
			if(listener.getAction() == CLICK && listener.getButton() == MouseButton.MOUSE_BUTTON_RIGHT){
				tree.select(this);
				return;
			}
		});
		this.recalculateSize();
	}

	public TreeGroup(TreeBase base, TextureGroup group){
		this(base);
		texgroup = group;
		updateColor();
		this.add(new TreeIcon((int)getSize().x - 20, 0, "group_delete", () -> {
			TextureManager.removeGroup(texgroup);
		}, "delete"));
		this.add(new TreeIcon((int)getSize().x - 42, 0, "group_edit", () -> {
			if(texgroup.texture == null) return;
			try{
				texgroup.texture.save();//TXO
				FMTB.openLink(texgroup.texture.getFile().getCanonicalPath());
			}
			catch(Exception e){
				log(e);
			}
		}, "edit"));
		this.add(new TreeIcon((int)getSize().x - 64, 0, "group_minimize", () -> toggle(!texgroup.minimized), "options"));
		new SubTreeGroup(base, 0, "tree.textures.select", () -> {
			FileSelector.select(Translator.translate("tree.textures.select.dialog"), "./", FileSelector.TYPE_PNG, false, file -> {
				if(file == null) return;
				//String name = file.getPath();
				//TextureManager.loadTextureFromFile(name, file);
				//texgroup.texture = TextureManager.getTexture(name, false);
				try{
					Files.copy(file, texgroup.texture.getFile());
					texgroup.texture.reload();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				//
				/*Texture tex = TextureManager.getTexture(name, true); if(tex == null) return;
				if(tex.getWidth() > FMTB.MODEL.textureX) FMTB.MODEL.textureX = tex.getWidth();
				if(tex.getHeight() > FMTB.MODEL.textureY) FMTB.MODEL.textureY = tex.getHeight();*/
			});
		}).setRoot(this).updateColor();
		new SubTreeGroup(base, 1, "tree.textures.generate", () -> {
			texgroup.texture.clear(null);
			/*if(FMTB.MODEL.texgroup == texgroup) FMTB.MODEL.textureScale = 1;
			for(TurboList list : FMTB.MODEL.getGroups()){
				if(list.texgroup == texgroup){
					list.textureS = 1;
				}
			}*/
			FMTB.MODEL.updateFields();
        	FMTB.MODEL.getGroups().forEach(elm -> {
        		if(elm.texgroup == null || elm.texgroup == texgroup){
            		elm.forEach(poly -> poly.burnToTexture(texgroup.texture, null));
        		}
        	});
        	texgroup.texture.getImage().position(0);
        	texgroup.texture.save();
        	texgroup.texture.reload();
        	FMTB.MODEL.recompile();
		}).setRoot(this).updateColor();
		new SubTreeGroup(base, 2, "tree.textures.resize", () -> {
			Dialog dialog = new Dialog(Translator.translate("tree.textures.resize.dialog"), 300, 120);
			dialog.setResizable(false);
			Label label = new Label(Translator.translate("tree.textures.resize.copyfrom"), 10, 10, 120, 20);
			SelectBox<String> selectbox = new SelectBox<String>(140, 10, 140, 20);
			selectbox.addElement("model");
			for(TurboList list : FMTB.MODEL.getGroups()){
				selectbox.addElement("group-" + list.id);
			}
			selectbox.setSelected(0, true);
			dialog.getContainer().add(label);
			dialog.getContainer().add(selectbox);
			Label label0 = new Label(Translator.translate("tree.textures.resize.scale"), 10, 40, 120, 20);
			SelectBox<Integer> selectbox0 = new SelectBox<Integer>(140, 40, 140, 20);
			selectbox0.addElement(1);
			selectbox0.addElement(2);
			selectbox0.addElement(3);
			selectbox0.addElement(4);
			selectbox0.setSelected(0, true);
			dialog.getContainer().add(label0);
			dialog.getContainer().add(selectbox0);
            Button button0 = new Button(Translator.translate("dialogbox.button.ok"), 10, 70, 100, 20);
            button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()){
            		dialog.close();
            		try{
                		boolean model = selectbox.getSelection().equals("model");
                		TurboList list = model ? null : FMTB.MODEL.getGroups().get(selectbox.getSelection().replace("group-", ""));
                		int scale = selectbox0.getSelection(), scale0 = scale;
                		int x = FMTB.MODEL.tx(list), xx = x;
                		int y = FMTB.MODEL.ty(list), yy = y;
                		if(scale > 1){
                			while(--scale > 0){
                				xx *= 2;
                				yy *= 2;
                			}
                		}
                		texgroup.texture.resize(xx, yy);
                		texgroup.texture.save();
                		texgroup.texture.reload();
                		log("Resized TexGroup '" + texgroup.group + "' to " + x + ", " + y + " with scale " + scale0 + " to " + xx + " " + yy + ".");
            		}
            		catch(Exception ex){
            			log(ex);
            		}
            	}
            });
            dialog.getContainer().add(button0);
			dialog.show(FMTB.frame);
		}).setRoot(this).updateColor();
		new SubTreeGroup(base, 3, "tree.textures.rename", () -> {
			Dialog dialog = new Dialog(Translator.translate("tree.textures.rename.dialog"), 300, 90);
			dialog.setResizable(false);
			TextField input = new TextField(texgroup.group, 10, 10, 280, 20);
			input.addTextInputContentChangeEventListener(listener -> UserInterfaceUtils.validateString(listener, true));
			dialog.getContainer().add(input);
            Button button0 = new Button(Translator.translate("dialogbox.button.ok"), 10, 40, 100, 20);
            button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()){
            		String newname = input.getTextState().getText();
            		if(TextureManager.hasGroup(newname)){
            			dialog.close();
            			DialogBox.showOK(null, null, null, "tree.textures.rename.duplicate");
            			return;
            		}
            		try{
            			String oldname = texgroup.group;
            			File file = new File("./temp/group-" + newname + ".png");
						FileUtils.copyFile(texgroup.texture.getFile(), file);
	            		texgroup.group = newname;
	            		texgroup.loadTexture("group-" + newname, file);
	            		TextureManager.removeTexture("group-" + oldname);
	            		texgroup.reAssignTexture();
					}
					catch(IOException e1){
						Logging.log(e1);
	            		texgroup.group = newname;
					}
            		GroupEditor.updateTextureGroups();
            		texgroup.button.update();
            		dialog.close();
            	}
            });
            dialog.getContainer().add(button0);
			dialog.show(FMTB.frame);
		}).setRoot(this).updateColor();
		this.recalculateSize();
	}

	public void toggle(boolean bool){
		if(texgroup != null) texgroup.minimized = bool;
		else if(animations) list.aminimized = bool;
		else if(list == null) compound.minimized = bool;
		else list.minimized = bool;
		recalculateSize();
		getChildComponents().forEach(con -> {
			if(con instanceof SubTreeGroup){
				((SubTreeGroup)con).toggle(!bool);
			}
		});
	}

	public void recalculateSize(){
		if(texgroup != null){
			this.setSize(this.getSize().x, texgroup.minimized ? 20 : (4 * 22) + 20);
			this.update();
			this.updateColor();
		}
		else if(animations){
			this.setSize(this.getSize().x, list.aminimized ? 20 : (list.animations.size() * 22) + 20);
			this.update();
			this.updateColor();
		}
		else if(list != null){
			this.setSize(this.getSize().x, list.minimized ? 20 : (list.size() * 22) + 20);
		}
		else{
			this.setSize(this.getSize().x, compound.minimized ? 20 : (compound.getGroups().size() * 22) + 20);
		}
		getChildComponents().forEach(con -> {
			if(con instanceof SubTreeGroup) ((SubTreeGroup)con).refreshPosition();
		});
		tree.reOrderGroups();
	}

	public void removeFromTree(){
		tree.scrollable.getContainer().remove(this);
		tree.groups.remove(this);
	}

	public TreeBase tree(){
		return tree;
	}

	public Component update(){
		label.getTextState().setText(texgroup != null ? texgroup.group : list == null ? compound.name : animations ? "[" + list.animations.size() + "] " + list.id : list.id);
		return this;
	}

	public void updateColor(){
		if(tree.isSelected(this)){
			label.getStyle().getBackground().setColor(FMTB.rgba(0xcdcdcd));
		}
		else if(texgroup != null){
			label.getStyle().getBackground().setColor(FMTB.rgba(0x28a148));
		}
		else if(animations){
			int color = 0;
			if(list.animations.isEmpty()) color = list.selected ? list.visible ? 0xfc7900 : 0xffe7d1 : list.visible ? 0xffa14a : 0xd1ac8a;
			else color = list.selected ? list.visible ? 0x2985ba : 0x7eb1cf : list.visible ? 0x28a148 : 0x6bbf81;
			label.getStyle().getBackground().setColor(FMTB.rgba(color));
		}
		else if(list == null) label.getStyle().getBackground().setColor(FMTB.rgba(selected() ? compound.visible ? 0xa37a18 : 0xd6ad4b : compound.visible ? 0x5e75e6 : 0xa4b0ed));
		else label.getStyle().getBackground().setColor(FMTB.rgba(list.selected ? list.visible ? 0xa37a18 : 0xd6ad4b : list.visible ? 0x28a148 : 0x6bbf81));
	}

	public boolean selected(){
		return HelperCollector.SELECTED > -1 && HelperCollector.SELECTED == index();
	}

	public int index(){
		return HelperCollector.LOADED.indexOf(compound);
	}

	public void onScroll(double yoffset){
		if(list != null && !animations){
			int index = FMTB.MODEL.getGroups().indexOf(list);
			int dir = index + (yoffset > 0 ? -1 : 1);
			if(dir < 0 || dir >= FMTB.MODEL.getGroups().size()) return;
			FMTB.MODEL.getGroups().remove(list);
			FMTB.MODEL.getGroups().add(dir, list);
		}
		if(compound != null){
			int index = HelperCollector.LOADED.indexOf(compound);
			int dir = index + (yoffset > 0 ? -1 : 1);
			if(dir < 0 || dir >= HelperCollector.LOADED.size()) return;
			GroupCompound com = HelperCollector.LOADED.remove(index);
			if(com != null){
				HelperCollector.LOADED.add(dir, compound);
				compound.minimized = false;
				compound.button.removeFromTree();
				Trees.helper.addSub(dir, compound.button);
				Trees.helper.reOrderGroups();
			}
			else log("Error, couldn't move compound, index not found.");
		}
	}

	public void onScrollSelect(){
		updateColor();
		show(this.getChildComponents().get(compound != null ? 7 : 5));
		show(this.getChildComponents().get(6));
	}

	public void onScrollDeselect(){
		updateColor();
		hide(this.getChildComponents().get(compound != null ? 7 : 5));
		hide(this.getChildComponents().get(6));
	}

}
