/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import java.awt.Desktop;

import net.fexcraft.app.fmt.FMTB;
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
		super(null, "dialogbox", "dialogbox"); this.setSize(600, 480).setDraggable(true).setVisible(false).setColor(0xff80adcc);
		Dialog.dialogs.add(this); this.setBorder(0xff000000, 0xfffcba03, 5, true, true, true, true); this.setHoverColor(StyleSheet.WHITE, false);
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
	
	/** 
	 * 
	 * @param text 0 - desc, 1 - desc2, 2 - left button, 3 - right button
	 * */
	public void show(){
		export(); //this.reset(); this.setVisible(true);
	}
	
	public void export(){
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
		}, ChooserMode.EXPORT);
	}
	
	public void reset(){
		//
	}

	@Override
	public boolean visible(){
		return visible;
	}

}
