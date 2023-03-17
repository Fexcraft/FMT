package net.fexcraft.app.fmt.utils.fvtm;

import java.io.File;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import org.liquidengine.legui.component.Widget;

public class FVTMConfigEditor extends Widget {

    public FVTMConfigEditor(File file){
        getTitleTextState().setText(Translator.translate("fvtmeditor.title") + " - " + file.getName());
        setSize(700, 500);
        setPosition(FMT.WIDTH / 2 - 350, FMT.HEIGHT / 2 - 250);
        Reference ref = null;
        String[] dots = file.getName().split(".");
        switch(dots[dots.length - 1]){
            case "block":{
                ref = new BlockConfigReference();
                break;
            }
            default: return;
        }
        JsonMap map = JsonHandler.parse(file);
        for(ConfigEntry entry : ref.getEntries()){

        }
        FMT.FRAME.getContainer().add(this);
    }


}
