package net.fexcraft.app.fmt.ui.general;

import java.util.ArrayList;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.FontRenderer.FontType;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.lib.common.math.RGB;

public abstract class DropDownField extends Element {
	
	private RGB textcolor = new RGB(212, 212, 212);
	private boolean centered;
	private String text;

	public DropDownField(Element root, String id, String style, int width, int x, int y){
		super(root, id, style == null ? "dd_field" : style); this.setSize(width, 26);
		this.setPosition(x, y).setColor(0xff484848);
		this.setHoverColor(0xff70ff7f, false); this.setHoverColor(0xffebc9c9, true);
	}
	
	public DropDownField setText(String string, boolean centered){
		this.text = string; this.centered = centered; return this;
	}
	
	public DropDownField setTextColor(RGB newcol){
		textcolor = newcol; return this;
	}

	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh);
		if(centered){
			int x = width / 2 - (FontRenderer.getWidth(text, FontType.BOLD) / 2), y = height / 2 - 10;
			FontRenderer.drawText(text, this.x + x, this.y + y, FontType.BOLD, textcolor);
		}
		else{
			FontRenderer.drawText(text, this.x + 2, this.y + 2, FontType.BOLD, textcolor);
		}
	}

	@Override
	public boolean processButtonClick(int x, int y, boolean left){
		if(hovered) UserInterface.DROPDOWN.show(this, getDropDownButtons(DropDown.INST)); return true;
	}
	
	@Override
	public void hovered(float mouseX, float mouseY){
		this.hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	public abstract ArrayList<Element> getDropDownButtons(DropDown inst);

	public final String getText(){
		return text;
	}
	
}