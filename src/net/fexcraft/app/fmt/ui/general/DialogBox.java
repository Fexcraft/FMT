/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import java.io.IOException;
import java.net.URISyntaxException;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class DialogBox extends Element implements Dialog {

	public static final Runnable NOTHING = () -> UserInterface.DIALOGBOX.reset();
	private Button button0, button1;
	private Runnable positive, negative;
	public RGB progresscolor;
	public int progress = -1;
	private String text;
	
	public DialogBox(){
		super(null, "dialogbox"); this.setSize(258, 128);
		this.visible = false; this.z = 90; Dialog.dialogs.add(this);
		TextureManager.loadTexture("ui/dialogbox", null);
		this.setTexPosSize("ui/dialogbox", 0, 0, 256, 128);
		this.elements.add(button0 = new Button(this, "positive", 100, 30, 0, 0, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ return onClick(true); }
		});
		this.elements.add(button1 = new Button(this, "negative", 100, 30, 0, 0, new RGB(214, 79, 79)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ return onClick(false); }
		});
	}
	
	@Override
	public void renderSelf(int rw, int rh) {
		x = (rw / 2) - (width / 2); y = (rh / 2) - (height / 2); this.renderSelfQuad();
		button0.x = x + 20; button0.y = y + 80; button1.x = x + 136; button1.y = y + 80;
		FontRenderer.drawText(text, this.x + 20, this.y + 13, 1);
		if(progress >= 0){
			this.renderQuad(x + 20, y + 64, 216, 12, "ui/background_light");
			if(progress > 0){
				(progresscolor == null ? RGB.GREEN : progresscolor).glColorApply();
				this.renderQuad(x + 20, y + 64, (int)(progress * 2.16f), 12, "ui/background_light");
				RGB.glColorReset();
			}
		}
	}
	
	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		//
		return true;
	}
	
	/** 
	 * 
	 * @param text 0 - desc, 1 - desc2, 2 - left button, 3 - right button
	 * */
	public void show(String text, String b0_text, String b1_text, Runnable positive, Runnable negative){
		this.reset(); this.positive = positive; this.negative = negative;
		if(text == null) text = "no title"; this.text = text;
		//
		button0.setEnabled(positive != null); button1.setEnabled(negative != null);
		button0.setVisible(positive != null); button1.setVisible(negative != null);
		button0.setText(button0.isVisible() ? b0_text : "", true);
		button1.setText(button1.isVisible() ? b1_text : "", true);
		//
		this.height = 128; this.width = 256; this.visible = true;
	}

	private boolean onClick(boolean positive){
		Runnable run = positive ? this.positive : this.negative;
		this.reset(); run.run(); return visible;
	}
	
	public void reset(){
		this.positive = this.negative = null; this.text = null;
		button0.setText(null, false); button1.setText(null, false); visible = false;
		progress = -1; progresscolor = null;
	}

	public static boolean notAvailableYet(){
		FMTB.showDialogbox("Feature not available yet.", "ok", "discord", NOTHING, () -> {
			try { java.awt.Desktop.getDesktop().browse(new java.net.URL("https://discord.gg/AkMAzaA").toURI()); }
			catch(IOException | URISyntaxException e){ e.printStackTrace(); }
		}); return true;
	}

	@Override
	public boolean visible(){
		return visible;
	}

}
