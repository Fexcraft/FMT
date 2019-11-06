/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.FontRenderer.FontType;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.FileChooser.AfterTask;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Settings.Setting;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class SettingsBox extends Element implements Dialog {
	
	private int page = 0;
	private static final int perpage = 7;
	private String alttext;
	private ArrayList<Setting> settings = new ArrayList<>();
	//
	private Button Confirm, Cancel;
	private AfterTask task;
	
	public SettingsBox(){
		super(null, "settingsbox", "settingsbox"); this.setSize(258, 128).setDraggable(true).setColor(0xff80adcc);
		this.setVisible(false).setPosition(0, 0).setHoverColor(0xffffffff, false); Dialog.dialogs.add(this);
		this.setBorder(0xff000000, 0xff3458eb, 5, true, true, true, true);
		//
		this.elements.add(Confirm = new Button(this, "confirm", "settingsbox:button", 100, 20, width - 112, 12, 0xffffff00){
			@Override public boolean processButtonClick(int x, int y, boolean left){ task.run(); reset(); return true; }
		}.setText("Confirm", true));
		this.elements.add(Cancel = new Button(this, "cancel", "settingsbox:button", 100, 20, width - 214, 12, 0xffffff00){
			@Override public boolean processButtonClick(int x, int y, boolean left){ reset(); return true; }
		}.setText("Cancel", true));
		//
		alttext = translate("settingsbox.default", "FMT Settings");
	}
	
	@Override
	public Element repos(){
		x = (UserInterface.width - width) / 2 + xrel; y = (UserInterface.height - height) / 2 + yrel;
		clearVertexes(); for(Element elm : elements) elm.repos(); return this;
	}
	
	@Override
	public void renderSelf(int rw, int rh) {
		this.renderSelfQuad();
		FontRenderer.drawText(alttext + " [ " + (page + 1) + " / " + (settings.size() / perpage + 1) + " ]", this.x + 12, this.y + 12, FontType.BOLD);
		for(int i = 0; i < perpage; i++){
			int j = (page * perpage) + i; if(j >= settings.size()) break; Setting setting = settings.get(j);
			FontRenderer.drawText("[" + j + "] " + setting.getId(), this.x + 12, this.y + 40 + (i * 30), FontType.BOLD);
		}
	}
	
	@Override
	public boolean onScrollWheel(int wheel){
		int oldpage = page; if(wheel > 0) page++; else page--; if(page < 0) page = 0;
		else if(page >= settings.size() / perpage) page = settings.size() / perpage;
		if(oldpage != page) updateFields(); return true;
	}
	
	private void updateFields(){
		this.elements.removeIf(pre -> !(pre.getId().equals("confirm") || pre.getId().equals("cancel")));
		for(int i = 0; i < perpage; i++){
			int j = (page * perpage) + i; if(j >= settings.size()) break;
			Setting setting = settings.get(j);
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
			super(root, id, "settingsbox:field", 240, x, y); relx = x; rely = y; this.setting = setting;
			this.setWithCommas(true).setText(setting.toString(), false).setBorder(0xff000000, 0, 1, true, true, true, true);
		}
		
		public void renderSelf(int rw, int rh){
			this.x = root.x + relx; this.y = root.y + rely;
			super.renderSelf(rw, rh);
		}
		
		@Override
		protected void updateTextField(){
			setting.validateAndApply(this.getText());
			this.setText(setting.toString(), false);
			if(setting.getId().equals("ui_scale")) FMTB.get().UI.rescale();
			if(setting.getId().equals("bottombar")){
				UserInterface.BOTTOMBAR.setVisible(setting.getBooleanValue());
				FMTB.get().UI.rescale();
			}
		}
		
	}
	
	public static class BoolButton extends Button {
		
		private int relx, rely;
		private Settings.Setting setting;

		public BoolButton(Element root, String id, int x, int y, Setting setting){
			super(root, id, "settingsbox:boolean", 240, 26, x, y); relx = x; rely = y; this.setting = setting;
			this.setText(setting.toString(), false);
		}
		
		public void renderSelf(int rw, int rh){
			this.x = root.x + relx; this.y = root.y + rely;
			super.renderSelf(rw, rh);
		}
		
		public boolean processButtonClick(int x, int y, boolean left){
			if(left) setting.validateAndApply(!setting.getBooleanValue() + "");
			this.setText(setting.toString(), false); return true;
		}
		
	}

	@Override
	public boolean processButtonClick(int x, int y, boolean left){
		//
		return true;
	}

	@Override
	public boolean visible(){
		return visible;
	}
	
	public void show(Object... objects){
		if(objects != null && objects.length >= 2){
			this.alttext = (String)objects[0]; this.task = (AfterTask)objects[1];
			Confirm.setVisible(true); Cancel.setVisible(true);
			this.settings.addAll(task.settings);
		}
		else{
			this.alttext = "FMT Settings";
			Confirm.setVisible(false); Cancel.setVisible(false);
			this.settings.addAll(Settings.getMap().values());
		}
		this.height = 256; this.width = 512; this.updateFields(); this.visible = true; this.repos();
	}

	@Override
	public void reset(){
		this.visible = false; this.elements.removeIf(pre -> !(pre.getId().equals("confirm") || pre.getId().equals("cancel")));
		this.Confirm.setVisible(false); this.Cancel.setVisible(false); this.settings.clear(); this.updateFields();
	}

}
