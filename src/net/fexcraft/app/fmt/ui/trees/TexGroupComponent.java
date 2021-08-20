package net.fexcraft.app.fmt.ui.trees;

import static net.fexcraft.app.fmt.settings.Settings.ASK_TEXTURE_GROUP_REMOVAL;
import static net.fexcraft.app.fmt.settings.Settings.GROUP_NORMAL;
import static net.fexcraft.app.fmt.settings.Settings.POLYGON_NORMAL;

import java.util.Collections;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.length.Length;
import org.liquidengine.legui.style.length.LengthType;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.GenericDialog;
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
		label.getStyle().getBackground().setColor(FMT.rgba(0x7f7f7f));
		label.getPosition().set(0, 0);
		label.getSize().add(4, 0);
		label.getStyle().setPaddingLeft(new Length<Float>(5f, LengthType.PIXEL));
		Settings.applyBorderless(label);
		this.getStyle().getBackground().setColor(FMT.rgba(GROUP_NORMAL.value));
		this.add(new OptionLabel(this, 0, "texture.group.rename", () -> {}));
		this.add(new OptionLabel(this, 1, "texture.group.resize", () -> {}));
		this.add(new OptionLabel(this, 2, "texture.group.generate", () -> {}));
		this.add(new OptionLabel(this, 3, "texture.group.select", () -> {}));
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
		//
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
			this.getStyle().getBackground().setColor(FMT.rgba(POLYGON_NORMAL.value));
			this.getStyle().setPaddingLeft(new Length<Float>(5f, LengthType.PIXEL));
			Settings.applyBorderless(this);
			setPosition(4, index * 21 + 26);
			setSize(Editor.CWIDTH - 8, PH);
		}
		
	}

}
