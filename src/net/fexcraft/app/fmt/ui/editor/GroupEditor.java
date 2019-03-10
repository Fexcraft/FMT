package net.fexcraft.app.fmt.ui.editor;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.OldTextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.RGB;

public class GroupEditor extends Editor {

	public GroupEditor(){
		super("group_editor");
		final RGB rgb = new RGB(127, 127, 255);
		//
		for(int i = 0; i < 3; i++){
			final int j = i;
			this.elements.put("rgb" + i + "-", new Button(this, "rgb" + i + "-", 12, 26, 4 + (98 * i), 30, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updateRGB(false, j); }
			}.setText(" < ", true).setTexture("ui/background").setLevel(-1));
			this.elements.put("rgb" + i, new OldTextField(this, "rgb" + i, 70, 16 + (98 * i), 30){
				@Override public void updateNumberField(){ updateRGB(null, j); }
				@Override protected boolean processScrollWheel(int wheel){ return updateRGB(wheel > 0, j); }
			}.setAsNumberfield(0, 255, true).setLevel(-1));
			this.elements.put("rgb" + i + "+", new Button(this, "rgb" + i + "+", 12, 26, 86 + (98 * i), 30, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updateRGB(true, j); }
			}.setText(" > ", true).setTexture("ui/background").setLevel(-1));
		}
		//
		this.elements.put("groupname", new OldTextField(this, "groupname", 294, 4, 80) {
			@Override
			public void updateTextField(){
				if(FMTB.MODEL.getSelected().isEmpty()) return;
				TurboList list = null;
				if(FMTB.MODEL.getDirectlySelectedGroupsAmount() == 1){
					if(FMTB.MODEL.getCompound().isEmpty()) return;
					list = FMTB.MODEL.getFirstSelectedGroup();
					list = FMTB.MODEL.getCompound().remove(list.id);
					list.id = this.getTextValue().replace(" ", "_").replace("-", "_").replace(".", "");
					while(FMTB.MODEL.getCompound().containsKey(list.id)){ list.id += "_"; }
					FMTB.MODEL.getCompound().put(list.id, list);
				}
				else{
					ArrayList<TurboList> arrlist = FMTB.MODEL.getDirectlySelectedGroups();
					for(int i = 0; i < arrlist.size(); i++){
						list = FMTB.MODEL.getCompound().remove(arrlist.get(i).id); if(list == null) continue;
						list.id = this.getTextValue().replace(" ", "_").replace("-", "_").replace(".", "");
						list.id += list.id.contains("_") ? "_" + i : i + "";
						while(FMTB.MODEL.getCompound().containsKey(list.id)){ list.id += "_"; }
						FMTB.MODEL.getCompound().put(list.id, list);
					}
				}
				FMTB.MODEL.getSelected().clear();
			}
		}.setText("null", true).setLevel(-1));
		//
		this.addMultiplicator(130);
	}
	
	protected boolean updateRGB(Boolean apply, int j){
		OldTextField field = (OldTextField)getElement("rgb" + j);
		if(apply != null) field.applyChange(field.tryChange(apply, FMTB.MODEL.rate));
		TurboList sel = FMTB.MODEL.getFirstSelectedGroup();
		if(sel != null){
			if(sel.color == null) sel.color = new RGB(RGB.WHITE);
			byte[] arr = sel.color.toByteArray();
			byte colorr = (byte)(field.getIntegerValue() - 128);
			switch(j){
				case 0: sel.color = new RGB(colorr, arr[1], arr[2]); break;
				case 1: sel.color = new RGB(arr[0], colorr, arr[2]); break;
				case 2: sel.color = new RGB(arr[0], arr[1], colorr); break;
			}
			arr = sel.color.toByteArray();
			if(arr[0] == 127 && arr[1] == 127 && arr[2] == 127) sel.color = null;
		} return true;
	}

	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh); TextureManager.unbind();
		/*font.drawString(4, 40, "Group Color", Color.black);
		font.drawString(4, 90, "Group Name", Color.black);
		font.drawString(4, 140, "Multiplicator/Rate", Color.black);*///TODO
		RGB.glColorReset();
	}

	@Override
	protected String[] getExpectedQuickButtons(){ return null; }

}
