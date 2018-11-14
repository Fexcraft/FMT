/**
 * 
 */
package net.fexcraft.app.fmt.ui.generic;

import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class DialogBox extends Element {

	public static final Runnable NOTHING = () -> UserInterface.DIALOGBOX.reset();
	private Button button0, button1;
	private Runnable positive, negative;
	private String[] text;
	
	public DialogBox(){
		super(null, "dialogbox");
		this.visible = false; this.z = 90;
		TextureManager.loadTexture("ui/dialogbox");
		this.elements.put("positive", button0 = new Button(this, "positive", 100, 30, 0, 0, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ return onClick(true); }
		});
		this.elements.put("negative", button1 = new Button(this, "negative", 100, 30, 0, 0, new RGB(214, 79, 79)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ return onClick(false); }
		});
		this.show(new String[]{ "Welcome to FMT!", "<version:" + FMTB.version + ">", "ok", "exit" }, NOTHING, () -> FMTB.get().close());
	}
	
	@Override
	public void renderSelf(int rw, int rh) {
		this.renderQuad(x = (rw / 2) - (width / 2), y = (rh / 2) - (height / 2), width, height, "ui/dialogbox");
		button0.x = x +  20; button0.y = y + 80; button1.x = x + 136; button1.y = y + 80;
		{
			TextureManager.unbind();
			font.drawString(this.x +  20, this.y + 20, text[0], Color.black);
			font.drawString(this.x +  20, this.y + 40, text[1], Color.black);
			RGB.glColorReset();
		}
	}
	
	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		//
		return false;
	}
	
	/** 
	 * 
	 * @param text 0 - desc, 1 - desc2, 2 - left button, 3 - right button
	 * */
	public void show(String[] ntext, Runnable positive, Runnable negative){
		this.reset(); this.positive = positive; this.negative = negative; this.text = ntext;
		if(text[0] == null) text[0] = "no title";
		if(text[1] == null) text[1] = "no desc";
		if(text[2] == null) text[2] = "ok";
		if(text[3] == null) text[3] = "cancel";
		//
		button0.enabled = button0.visible = positive != null;
		button1.enabled = button1.visible = negative != null;
		button0.setText(button0.visible ? text[2] : null, true);
		button1.setText(button1.visible ? text[3] : null, true);
		//
		this.height = 128; this.width = 256; this.visible = true;
	}

	private boolean onClick(boolean positive){
		this.visible = false; (positive ? this.positive : this.negative).run(); this.reset(); return visible;
	}
	
	public void reset(){
		this.positive = this.negative = null; this.text = null;
		button0.setText(null, false); button1.setText(null, false); visible = false;
	}

}
