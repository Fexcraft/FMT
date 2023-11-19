package net.fexcraft.app.fmt.ui;

import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;

public class CenteredLabel extends Label {

    public CenteredLabel(String text, int x, int y, int w, int h){
        super(text, x, y, w, h);
        getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
    }

}
