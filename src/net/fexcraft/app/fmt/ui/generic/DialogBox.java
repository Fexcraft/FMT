/**
 * 
 */
package net.fexcraft.app.fmt.ui.generic;

import net.fexcraft.app.fmt.ui.Element;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class DialogBox extends Element {

	private Runnable positive, negative;
	private String text;
	
	public DialogBox(){
		super(null, "dialogbox");
		this.visible = false; this.z = 90;
		//this.show("test\ntext\ntest\ntest", null, null);
	}
	
	@Override
	public void renderSelf(int rw, int rh) {
		this.renderQuad(x = (rw / 2) - (width / 2), y = (rh / 2) - (height / 2), width, height, "ui/button_bg");
		/*GL11.glScaled(2, 2, 2);
		TextureManager.unbind(); font.drawString(this.x / 2 + 1, this.y / 2, text, Color.black); RGB.glColorReset();
		GL11.glScaled(0.5, 0.5, 0.5);*/
	}
	
	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		//
		return false;
	}
	
	public void show(String text, Runnable positive, Runnable negative){
		this.positive = positive; this.negative = negative; this.text = text;
		this.height = (text.split("\n").length * 30) + 100; this.width = 300;
		this.visible = true;
	}

}
