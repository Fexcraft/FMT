package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.TextField;

public class ModelGeneral extends EditorComponent {
	
	private SelectBox<String> texgroups = new SelectBox<>();
	private SelectBox<Integer> texsx = new SelectBox<>(), texsy = new SelectBox<>();
	protected static final String genid = "model.general";
	private TextField name;
	
	public ModelGeneral(){
		super(genid, 180, false, true);
		this.add(new Label(translate(LANG_PREFIX + genid + ".model_name"), L5, row(1), LW, HEIGHT));
		this.add(name = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> FMT.MODEL.name(con)));
		//
		this.add(new Label(translate(LANG_PREFIX + genid + ".tex_size"), L5, row(1), LW, HEIGHT));
		texsx.setSize(F2S, HEIGHT);
		texsx.setPosition(F20, row(1));
		this.add(texsx);
		texsy.setSize(F2S, HEIGHT);
		texsy.setPosition(F21, row(0));
		this.add(texsy);
		for(int res : TextureManager.RESOLUTIONS){
			texsx.addElement(res);
			texsy.addElement(res);
		}
		texsx.setVisibleCount(6);
		texsy.setVisibleCount(6);
		//
		this.add(new Label(translate(LANG_PREFIX + genid + ".tex_group"), L5, row(1), LW, HEIGHT));
		texgroups.setPosition(L5, row(1));
		texgroups.setSize(LW, HEIGHT);
		//
		updateholder.add(UpdateType.MODEL_LOAD, vals -> {
			name.getTextState().setText(FMT.MODEL.name);
			refreshTexGroupEntries();
		});
		updateholder.add(UpdateType.TEXGROUP_ADDED, vals -> refreshTexGroupEntries());
		updateholder.add(UpdateType.TEXGROUP_RENAMED, vals -> refreshTexGroupEntries());
		updateholder.add(UpdateType.TEXGROUP_REMOVED, vals -> refreshTexGroupEntries());
		texgroups.addSelectBoxChangeSelectionEventListener(listener -> {
			FMT.MODEL.texgroup = TextureManager.getGroup(listener.getNewValue());
			UpdateHandler.update(UpdateType.MODEL_TEXGROUP_CHANGED, FMT.MODEL.texgroup);
		});
		texgroups.setVisibleCount(6);
		refreshTexGroupEntries();
		this.add(texgroups);
	}

	private void refreshTexGroupEntries(){
		while(texgroups.getElements().size() > 0) texgroups.removeElement(0);
		texgroups.addElement("none");
		for(TextureGroup group : TextureManager.getGroups()){
			texgroups.addElement(group.name);
		}
		if(FMT.MODEL == null|| FMT.MODEL.texgroup == null) texgroups.setSelected(0, true);
		else texgroups.setSelected(FMT.MODEL.texgroup.name, true);
	}

}
