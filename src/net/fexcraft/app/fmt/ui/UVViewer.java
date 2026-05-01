package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class UVViewer extends Frame {

    public static Face SELECTED = NoFace.NONE;

    @Override
    public Element show(){
        pos((FMT.SCALED_WIDTH - w) * 0.5f, (FMT.SCALED_HEIGHT - h) * 0.5f);
        FMT.UI.setFrameOnTop(this);
        return super.show();
    }

}
