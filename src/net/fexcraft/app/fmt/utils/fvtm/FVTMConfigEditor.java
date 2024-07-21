package net.fexcraft.app.fmt.utils.fvtm;

import java.io.File;
import java.util.ArrayList;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.Widget;
import org.joml.Vector2f;

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
                ref = BlockConfigReference.INSTANCE;
                break;
            }
            case "vehicle":{
                ref = VehicleConfigReference.INSTANCE;
                break;
            }
            default: return;
        }
        map = JsonHandler.parse(file);
        Settings.applyComponentTheme(getContainer());
        getContainer().add(panel = new ScrollablePanel(10, 40, width - 20, height - 70));
        getContainer().add(new RunButton("dialog.button.save", width - 220, 10, 100, 24, () -> {
            JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
        }));
        getContainer().add(new RunButton("dialog.button.close", width - 110, 10, 100, 24, () -> {
            FMT.FRAME.getContainer().remove(this);
            INSTANCES.remove(this);
        }));
        getListenerMap().addListener(ChangeSizeEvent.class, event -> {
            Vector2f vec = new Vector2f();
            event.getNewSize().get(vec);
            if(vec.x < width){
                setSize(width, vec.y);
                return;
            }
            if(vec.y < height){
                setSize(vec.x, height);
                return;
            }
            panel.setSize(vec.x - 20, vec.y - 70);
        });
        fill();
        FMT.FRAME.getContainer().add(this);
        INSTANCES.add(this);
        show();
    }

    private void fill(){
        for(ConfigEntry entry : ref.getEntries()){
            panel.getContainer().add(new EntryComponent(this, null, entry, map, entry.name, get(map, entry)));
        }
        resize();
    }

    public void resize(){
        height_ = 0;
        for(Component com : panel.getContainer().getChildComponents()){
            if(com instanceof EntryComponent == false) continue;
            height_ += ((EntryComponent)com).gen(0);
        }
        panel.getContainer().setSize(pwidth, height_);

    }

    private JsonValue get(JsonMap map, ConfigEntry entry){
        if(map.has(entry.name)) return map.get(entry.name);
        if(map.has(entry.alt)) return map.get(entry.alt);
        return null;
    }


}
