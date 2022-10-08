package net.fexcraft.app.fmt.ui.trees;

import static net.fexcraft.app.fmt.settings.Settings.ASK_TEXTURE_GROUP_REMOVAL;
import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.Collections;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.StbBackedLoadableImage;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.length.Length;
import org.liquidengine.legui.style.length.LengthType;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
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
		label.getStyle().getBackground().setColor(FMT.rgba(Settings.GROUP_NORMAL.value));
		label.getPosition().set(0, 0);
		label.getSize().add(4, 0);
		label.getStyle().setPaddingLeft(new Length<Float>(5f, LengthType.PIXEL));
		Settings.applyBorderless(label);
		this.getStyle().getBackground().setColor(FMT.rgba(Settings.GROUP_INVISIBLE.value));
		this.add(new OptionLabel(this, 0, "texture.group.rename", () -> openRenameDialog()));
		this.add(new OptionLabel(this, 1, "texture.group.resize", () -> {}));
		this.add(new OptionLabel(this, 2, "texture.group.generate", () -> {}));
		this.add(new OptionLabel(this, 3, "texture.group.select", () -> {}));
		pin.setImage(new StbBackedLoadableImage("./resources/textures/icons/component/edit.png"));
	}

	private void openRenameDialog(){
		float width = 400;
        Dialog dialog = new Dialog(Translator.translate("editor.tree.texture.rename_group.title"), width, 130);
        Settings.applyComponentTheme(dialog.getContainer());
        dialog.setResizable(true);
    	Label label0 = new Label(Translator.translate("editor.tree.texture.rename_group.desc"), 10, 10, width - 20, 20);
    	dialog.getContainer().add(label0);
    	TextField field = new TextField(group.name, 10, 35, width - 20, 20);
    	dialog.getContainer().add(field);
    	//
        Button button0 = new Button(Translator.translate("dialog.button.confirm"), 10, 80, 100, 20);
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
        Button button1 = new Button(Translator.translate("dialog.button.cancel"), 120, 80, 100, 20);
        button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()) dialog.close();
        });
        dialog.getContainer().add(button1);
        //
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
			this.getTextState().setText(Translator.translate(string));
			this.getStyle().setTextColor(ColorConstants.lightGray());
			this.getStyle().getBackground().setColor(FMT.rgba(Settings.POLYGON_NORMAL.value));
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
