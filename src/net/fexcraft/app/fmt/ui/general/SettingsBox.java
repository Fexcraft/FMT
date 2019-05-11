/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.TextureManager;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class SettingsBox extends Element implements Dialog {
	
	private int page = 0;
	private static final int perpage = 7;
	
	public SettingsBox(){
		super(null, "settingsbox"); this.setSize(258, 128);
		this.visible = false; this.z = 90; Dialog.dialogs.add(this);
		TextureManager.loadTexture("ui/settingsbox", null);
		this.setTexPosSize("ui/settingsbox", 0, 0, 512, 256);
	}
	
	@Override
	public void renderSelf(int rw, int rh) {
		x = (rw / 2) - (width / 2); y = (rh / 2) - (height / 2); this.renderSelfQuad();
		//button0.x = x + 20; button0.y = y + 80; button1.x = x + 136; button1.y = y + 80;
		FontRenderer.drawText("FMT Settings [Page: " + (page + 1) + "/" + (Settings.getMap().size() / perpage + 1) + "]", this.x + 12, this.y + 12, 1);
		for(int i = 0; i < perpage; i++){
			int j = (page * perpage) + i; if(j >= Settings.getMap().size()) break;
			Setting setting = Settings.getMap().getAt(j);
			FontRenderer.drawText("[" + j + "] " + setting.getId(), this.x + 12, this.y + 40 + (i * 30), 1);
		}
	}
	
	@Override
	public boolean onScrollWheel(int wheel){
		int oldpage = page; if(wheel > 0) page++; else page--; if(page < 0) page = 0;
		else if(page >= Settings.getMap().size() / perpage) page = Settings.getMap().size() / perpage;
		if(oldpage != page) updateFields(); return true;
	}
	
	private void updateFields(){
		this.elements.clear();
		for(int i = 0; i < perpage; i++){
			int j = (page * perpage) + i; if(j >= Settings.getMap().size()) break;
			Setting setting = Settings.getMap().getAt(j);
			if(setting.getType().isBoolean()){
				this.elements.add(new BoolButton(this, "field" + i, 256, 36 + (i * 30), setting));
			} else {
				this.elements.add(new Field(this, "field" + i, 256, 36 + (i * 30), setting));
			}
		}
	}
	
	private static class Field extends TextField {
		
		private int relx, rely;
		private Settings.Setting setting;

		public Field(Element root, String id, int x, int y, Setting setting){
			super(root, id, 240, x, y); relx = x; rely = y; this.setting = setting;
			this.setWithCommas(true).setText(setting.toString(), false);
		}
		
		public void renderSelf(int rw, int rh){
			this.x = root.x + relx; this.y = root.y + rely;
			super.renderSelf(rw, rh);
		}
		
		@Override
		protected void updateTextField(){
			setting.validateAndApply(this.getText());
			this.setText(setting.toString(), false);
		}
		
	}
	
	public static class BoolButton extends Button {
		
		private int relx, rely;
		private Settings.Setting setting;

		public BoolButton(Element root, String id, int x, int y, Setting setting){
			super(root, id, 240, 26, x, y); relx = x; rely = y; this.setting = setting;
			this.setText(setting.toString(), false);
		}
		
		public void renderSelf(int rw, int rh){
			this.x = root.x + relx; this.y = root.y + rely;
			super.renderSelf(rw, rh);
		}
		
		protected boolean processButtonClick(int x, int y, boolean left){
			if(left) setting.validateAndApply(!setting.getBooleanValue() + "");
			this.setText(setting.toString(), false); return true;
		}
		
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		//
		return true;
	}

	@Override
	public boolean visible(){
		return visible;
	}
	
	public void show(){
		this.reset(); this.height = 256; this.width = 512; this.visible = true; this.updateFields();
	}

	@Override
	public void reset(){
		this.visible = false; this.elements.clear();
	}

}
