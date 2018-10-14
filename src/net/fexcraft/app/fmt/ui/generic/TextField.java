package net.fexcraft.app.fmt.ui.generic;

import java.util.ArrayList;

import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.RGB;
import net.fexcraft.app.fmt.utils.TextureManager;

public class TextField extends Element {
	
	private static final ArrayList<TextField> fields = new ArrayList<>();
	private RGB hovercolor = new RGB(112, 255, 127), inactivecol = new RGB(235, 201, 201);
	private boolean centered, selected, number;
	private String text;
	private int width, x, y;
	private float min, max, value;

	public TextField(Element parent, String id, int width, int x, int y){
		super(parent, id); fields.add(this);
		this.width = width; this.height = 26;
		this.x = parent.x + x; this.y = parent.y + y;
	}
	
	public TextField setText(String string, boolean centered){
		this.text = string; this.centered = centered; return this;
	}
	
	public TextField setAsNumberfield(float min, float max, boolean centered){
		this.number = true; this.min = min; this.max = max; this.centered = centered; value = 0; return this;
	}

	@Override
	public void renderSelf(int rw, int rh){
		if(enabled) (selected ? hovercolor : hovered ? RGB.BLACK : inactivecol).glColorApply();
		this.renderQuad(x, y, width, height, "ui/background");
		if(enabled) RGB.glColorReset();
		if(!number && text == null) return;
		String text = number ? value + "" : this.text;
		TextureManager.unbind();
		if(centered){
			int x = width / 2 - (font.getWidth(text) / 2);
			int y = height / 2 - (font.getHeight(text) / 2);
			font.drawString(this.x + x, this.y + y, text, Color.white);
		}
		else{
			font.drawString(x + 2, y + 2, text, Color.white);
		}
		RGB.glColorReset();
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		if(this.selected){ this.selected = false; return true; }
		fields.forEach(elm -> elm.selected = false);
		return this.selected = true;
	}
	
	@Override
	public void hovered(int mouseX, int mouseY){
		this.hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	public final String getText(){
		return text;
	}

	public void validateChange(String string){
		this.text = string; //TODO
	}
	
	@Override
	protected boolean processScrollWheel(int wheel){
		return FMTB.MODEL.updateValue(this, this.id + (wheel > 0 ? "+" : "-"));
	}

	public float tryChange(boolean positive, float rate){
		if(!number) return 0; float f = value;
		f = positive ? f + rate : f - rate;
		return f >= min && f <= max ? value = f : value;
	}
	
	public void applyChange(float f){
		this.value = f;
	}

}