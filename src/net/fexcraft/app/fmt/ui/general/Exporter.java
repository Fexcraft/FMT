/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import java.awt.Desktop;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.FileChooser.AfterTask;
import net.fexcraft.app.fmt.ui.general.FileChooser.ChooserMode;
import net.fexcraft.app.fmt.ui.general.FileChooser.FileRoot;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.utils.Translator;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Exporter extends Element implements Dialog {

	//
	
	public Exporter(){
		super(null, "exporter", "exporter"); this.setSize(600, 320).setDraggable(true).setVisible(false).setColor(0xff80adcc);
		Dialog.dialogs.add(this); this.setBorder(0xff000000, 0xfffcba03, 5, true, true, true, true); this.setHoverColor(StyleSheet.WHITE, false);
		//
		this.elements.add(new Element(this, "exporter:0", "exporter:0").setSize(200, 300).setPosition(  0, 0).setColor(0xffffffff).setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true));
		this.elements.add(new Element(this, "exporter:1", "exporter:1").setSize(200, 310).setPosition(200, 0).setColor(0xffffffff).setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true));
		this.elements.add(new Element(this, "exporter:2", "exporter:2").setSize(200, 320).setPosition(400, 0).setColor(0xffffffff).setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true));
		//
		//this.show();
	}
	
	@Override
	public Element repos(){
		x = (UserInterface.width - width) / 2 + xrel; y = (UserInterface.height - height) / 2 + yrel;
		clearVertexes(); for(Element elm : elements) elm.repos(); return this;
	}
	
	@Override
	public void renderSelf(int rw, int rh) {
		this.renderSelfQuad();
		//
	}
	
	@Override
	public boolean processButtonClick(int x, int y, boolean left){
		//
		return true;
	}
	
	public void show(){
		this.reset(); this.setVisible(true);
	}
	
	public void export(ExImPorter porter){
		UserInterface.FILECHOOSER.show(new String[]{ Translator.translate("filechooser.export.title", "Select Export Location"),
			Translator.translate("filechooser.export.confirm", "Export") }, FileRoot.EXPORT, new AfterTask(){
			@Override
			public void run(){
				try{
					if(file == null){
						FMTB.showDialogbox(Translator.translate("dialog.export.nofile", "No valid file choosen.<nl>Export is cancelled."),
							Translator.translate("dialog.export.nofile.confirm", "ok.."), null, DialogBox.NOTHING, null);
						return;
					}
					String result = porter.exportModel(FMTB.MODEL, file, mapped_settings);
					FMTB.showDialogbox(Translator.format("dialog.export.success", "Export complete.<nl>%s", result),
						Translator.translate("dialog.export.success.confirm", "OK!"), null, DialogBox.NOTHING, null);
					Desktop.getDesktop().open(file.getParentFile());
				}
				catch(Exception e){
					String str = Translator.format("dialog.export.fail", "Errors while exporting Model.<nl>%s", e.getLocalizedMessage());
					FMTB.showDialogbox(str, Translator.translate("dialog.export.fail.confirm", "ok."), null, DialogBox.NOTHING, null);//TODO add "open console" as 2nd button
					e.printStackTrace();
				}
			}
		}, ChooserMode.EXPORT, porter);
	}
	
	public void reset(){
		//
	}

	@Override
	public boolean visible(){
		return visible;
	}

}
