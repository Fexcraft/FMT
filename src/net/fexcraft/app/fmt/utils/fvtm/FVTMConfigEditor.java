package net.fexcraft.app.fmt.utils.fvtm;

import java.io.File;
import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.Widget;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FVTMConfigEditor extends Widget {

    public static ArrayList<FVTMConfigEditor> INSTANCES = new ArrayList<>();

    private ScrollablePanel panel;
    private Reference ref;
    private JsonMap map;
    private File file;

    public static int width = 700, height = 500, pwidth = 1000;
    protected static int height_;

    public FVTMConfigEditor(File file){
        getTitleTextState().setText(Translator.translate("fvtmeditor.title") + " - " + file.getName());
        setSize(width, height);
        setPosition(FMT.WIDTH / 2 - (width / 2), FMT.HEIGHT / 2 - (height / 2));
        String[] dots = file.getName().split("\\.");
        switch(dots[dots.length - 1]){
            case "block":{
                ref = new BlockConfigReference();
                break;
            }
            default: return;
        }
        map = JsonHandler.parse(file);
        Settings.applyComponentTheme(getContainer());
        getContainer().add(panel = new ScrollablePanel(10, 20, width - 20, height - 80));
        getContainer().add(new RunButton("dialog.button.save", width - 220, height - 50, 100, 24, () -> {
            JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
        }));
        getContainer().add(new RunButton("dialog.button.close", width - 110, height - 50, 100, 24, () -> {
            FMT.FRAME.getContainer().remove(this);
            INSTANCES.remove(this);
        }));
        fill();
        FMT.FRAME.getContainer().add(this);
        INSTANCES.add(this);
        show();
    }

    private void fill(){
        height_ = 0;
        for(ConfigEntry entry : ref.getEntries()){
            EntryComponent com = new EntryComponent(entry, map, entry.name, get(map, entry), null);
            height_ += com.gen(0);
            panel.getContainer().add(com);
        }
        panel.getContainer().setSize(pwidth, height_);
    }

    private JsonValue get(JsonMap map, ConfigEntry entry){
        if(map.has(entry.name)) return map.get(entry.name);
        if(map.has(entry.alt)) return map.get(entry.alt);
        return null;
    }


}
