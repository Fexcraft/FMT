package net.fexcraft.app.fmt.ui.trees;

import static net.fexcraft.app.fmt.settings.Settings.ASK_TEXTURE_GROUP_REMOVAL;
import static net.fexcraft.app.fmt.utils.Logging.log;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.Collections;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.StbBackedLoadableImage;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.length.Length;
import org.liquidengine.legui.style.length.LengthType;

import com.google.common.io.Files;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator;

public class TexGroupComponent extends EditorComponent {

	private static final int PH = 20, PHS = 21, A = 4;
	private TextureGroup group;
	
	public TexGroupComponent(TextureGroup group){
		super(group.name, HEIGHT + A * PHS + 4, true, false);
		label.getTextState().setText((this.group = group).name);
		this.genFullheight();
		updateholder.add(UpdateType.TEXGROUP_RENAMED, wrp -> { if(wrp.objs[0] == group) label.getTextState().setText(group.name); });
		label.getStyle().setTextColor(ColorConstants.lightGray());
		label.getStyle().getBackground().setColor(FMT.rgba(Settings.TEXTURE_GROUP.value));
		label.getPosition().set(0, 0);
		label.getSize().add(4, 0);
		label.getStyle().setPaddingLeft(new Length<Float>(5f, LengthType.PIXEL));
		Settings.applyBorderless(label);
		this.add(new OptionLabel(this, 0, "texture.group.rename", () -> openRenameDialog()));
		this.add(new OptionLabel(this, 1, "texture.group.resize", () -> openResizeDialog()));
		this.add(new OptionLabel(this, 2, "texture.group.generate", () -> generate()));
		this.add(new OptionLabel(this, 3, "texture.group.select", () -> openSelectDialog()));
		pin.setImage(new StbBackedLoadableImage("./resources/textures/icons/component/edit.png"));
	}

	private void openSelectDialog(){
		FileChooser.chooseFile(translate("editor.tree.texture.select_texture.dialog"), "./", FileChooser.TYPE_IMG, false, file -> {
			if(file == null) return;
			try{
				Files.copy(file, group.texture.getFile());
			}
			catch(Throwable e){
				log(e);
			}
		});
	}

	private void openRenameDialog(){
		float width = 400;
        Dialog dialog = new Dialog(translate("editor.tree.texture.rename_group.title"), width, 130);
        Settings.applyComponentTheme(dialog.getContainer());
        dialog.setResizable(true);
    	Label label0 = new Label(translate("editor.tree.texture.rename_group.desc"), 10, 10, width - 20, 20);
    	dialog.getContainer().add(label0);
    	TextField field = new TextField(group.name, 10, 35, width - 20, 20);
    	dialog.getContainer().add(field);
    	//
        Button button0 = new Button(translate("dialog.button.confirm"), 10, 80, 100, 20);
        button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){
        		String newname = field.getTextState().getText();
        		if(TextureManager.getGroup(newname) != null){
        			GenericDialog.showOK(null, null, null, "editor.tree.texture.rename_group.duplicate");
        			return;
        		}
    			UpdateHandler.update(UpdateType.TEXGROUP_RENAMED, group, group.name, group.name = newname);
        		dialog.close();
        	}
        });
        dialog.getContainer().add(button0);
        //
        Button button1 = new Button(translate("dialog.button.cancel"), 120, 80, 100, 20);
        button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()) dialog.close();
        });
        dialog.getContainer().add(button1);
        //
        dialog.show(FMT.FRAME);
	}

	private void generate(){
		group.texture.clear(null);
    	FMT.MODEL.groups().forEach(elm -> {
    		if(elm.texgroup == null || elm.texgroup == group){
        		elm.forEach(poly -> poly.paintTex(group.texture, null));
    		}
    	});
    	group.texture.getImage().position(0);
    	group.texture.save();
    	group.texture.reload();
    	FMT.MODEL.recompile();
	}

	private void openResizeDialog(){
		Dialog dialog = new Dialog(translate("editor.tree.texture.resize_group.dialog"), 380, 120);
		dialog.setResizable(false);
		dialog.getContainer().add(new Label(translate("editor.tree.texture.resize_group.from"), 10, 10, 140, 20));
		SelectBox<String> from = new SelectBox<String>(150, 10, 220, 20);
		from.addElement("model");
		for(Group list : FMT.MODEL.groups()){
			from.addElement("/ " + list.id);
		}
		from.setSelected(0, true);
		from.setVisibleCount(12);
		dialog.getContainer().add(from);
		dialog.getContainer().add(new Label(translate("editor.tree.texture.resize_group.scale"), 10, 40, 140, 20));
		SelectBox<Integer> upscale = new SelectBox<Integer>(150, 40, 220, 20);
		upscale.addElement(0);
		upscale.addElement(1);
		upscale.addElement(2);
		upscale.addElement(3);
		upscale.setVisibleCount(4);
		upscale.setSelected(0, true);
		dialog.getContainer().add(upscale);
        Button button0 = new Button(Translator.translate("dialog.button.confirm"), 10, 70, 100, 20);
        button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) lis -> {
        	if(CLICK != lis.getAction()) return;
        	dialog.close();
        	try{
        		boolean model = !from.getSelection().startsWith("/");
        		Group group = model ? null : FMT.MODEL.get(from.getSelection().substring(2));
        		int scale = upscale.getSelection();
        		int x = model ? FMT.MODEL.texSizeX : group.texSizeX, ox = x;
        		int y = model ? FMT.MODEL.texSizeY : group.texSizeY, oy = y;
        		while(scale > 0){
        			x *= 2;
        			y *= 2;
        			scale--;
        		}
        		texgroup().texture.resize(x, y);
        		texgroup().texture.save();
        		texgroup().texture.reload();
        		texgroup().genPainterTex();
        		log("Resized TextureGroup '" + texgroup().name + "' to " + ox + ", " + oy + " with " + upscale.getSelection() + " times upscaling to " + x + " " + y + ".");
        	}
        	catch(Exception e){
				log(e);
			}
        });
        dialog.getContainer().add(button0);
		dialog.show(FMT.FRAME);
	}

	private int genFullheight(){
		return fullheight = HEIGHT + A * PHS + 4;
	}

	public TextureGroup texgroup(){
		return group;
	}

	@Override
	protected boolean move(int dir){
		if(super.move(dir)){
			try{
				int index = TextureManager.getGroups().indexOf(group);
				Collections.swap(TextureManager.getGroups(), index, index + dir);
			}
			catch(Exception e){
				Logging.log(e);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void pin(){
		if(group.texture == null) return;
		try{
			group.texture.save();
			FMT.openLink(group.texture.getFile().getCanonicalPath());
		}
		catch(Exception e){
			log(e);
		}
	}
	
	@Override
	public void rem(){
		if(ASK_TEXTURE_GROUP_REMOVAL.value){
			GenericDialog.showCC("texture.manager", () -> TextureManager.remGroup(group), null, "texture.remove_group", group.name);
		}
		else TextureManager.remGroup(group);
	}
	
	public static class OptionLabel extends Label {

		public OptionLabel(TexGroupComponent com, int index, String string, Runnable run){
			this.getTextState().setText(translate(string));
			this.getStyle().setTextColor(ColorConstants.lightGray());
			this.getStyle().getBackground().setColor(FMT.rgba(Settings.TEXTURE_OPTION.value));
			this.getStyle().setPaddingLeft(new Length<Float>(5f, LengthType.PIXEL));
			Settings.applyBorderless(this);
			setPosition(4, index * 21 + 26);
			setSize(Editor.CWIDTH - 8, PH);
	        getListenerMap().addListener(MouseClickEvent.class, e -> {
	        	if(CLICK == e.getAction()) run.run();
	        });
		}
		
	}

}
