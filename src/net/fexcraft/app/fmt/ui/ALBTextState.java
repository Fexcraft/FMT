package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.FontSizeUtil.getWidth;

import org.liquidengine.legui.component.optional.TextState;

public class ALBTextState extends TextState {
	
	float width;

	public ALBTextState(float width){
		super();
		this.width = width;
	}
	
	@Override
    public void setText(String text){
        String[] split = text.trim().split(" ");
        text = "";
        int processed = 1;
        String str = split[0];
        while(processed < split.length){
        	if(str.endsWith("<n>")){
        		text += str.substring(0, str.length() - 3) + "\n";
        		str = split[processed++];
        	}
        	else if(getWidth(str + " " + split[processed]) >= width){
        		text += str + "\n";
        		str = split[processed++];
        	}
        	else str += " " + split[processed++];
        }
        super.setText(text + str);
    }

}
