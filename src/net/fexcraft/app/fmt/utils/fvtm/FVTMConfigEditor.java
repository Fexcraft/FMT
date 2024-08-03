package net.fexcraft.app.fmt.utils.fvtm;

import java.io.File;
import java.util.ArrayList;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Logging;
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

    private EntryComponent root;
    private ScrollablePanel panel;
    private Reference ref;
    private JsonMap rmap;
    private JsonMap map;
    protected File file;

    public static int width = 700, height = 500, pwidth = 1000;
    protected static int height_;

    public FVTMConfigEditor(File file, String type){
        getTitleTextState().setText(Translator.translate("fvtmeditor.title") + " - " + file.getName() + (type == null ? "" : " / " + type));
        setSize(width, height);
        setPosition(FMT.WIDTH / 2 - (width / 2), FMT.HEIGHT / 2 - (height / 2));
        if(type == null){
            String[] dots = file.getName().split("\\.");
            type = dots[dots.length - 1];
        }
        this.file = file;
        ref = getReference(type);
        if(ref == null) return;
        rmap = JsonHandler.parse(file);
        if(type.equals("modeldata")){
            if(!rmap.has("ModelData")) rmap.addMap("ModelData");
            map = rmap.getMap("ModelData");
        }
        else map = rmap;
        Settings.applyComponentTheme(getContainer());
        getContainer().add(panel = new ScrollablePanel(10, 40, width - 20, height - 70));
        getContainer().add(new RunButton("dialog.button.save", width - 220, 10, 100, 24, () -> {
            JsonHandler.print(file, rmap, JsonHandler.PrintOption.DEFAULT);
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

    private Reference getReference(String type){
        switch(type){
            case "vehicle": return VehicleConfigReference.INSTANCE;
            case "part": return null;
            case "material": return MaterialConfigReference.INSTANCE;
            case "consumable": return ConsumableConfigReference.INSTANCE;
            case "fuel": return null;
            case "block": return BlockConfigReference.INSTANCE;
            case "wire": return null;
            case "wiredeco": return null;
            case "deco": return null;
            case "railgauge": return null;
            case "cloth": return null;
            //
            case "modeldata": return ModelDataReference.INSTANCE;
            default: return null;
        }
    }

    private void fill(){
        root = new EntryComponent(this, null, ConfigEntry.TEXT_ENTRY, null, map);
        for(ConfigEntry entry : ref.getEntries()){
            try{
                panel.getContainer().add(new EntryComponent(this, root, entry, entry.key(), getEV(map, entry)));
            }
            catch(Exception e){
                Logging.log(entry.name + " " + entry.type + " " + entry.key() + " " + map.get(entry.name));
                e.printStackTrace();
            }
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

    protected static JsonValue getEV(JsonMap map, ConfigEntry entry){
        if(map.has(entry.name)) return map.get(entry.name);
        if(map.has(entry.alt)) return map.get(entry.alt);
        return null;
    }


}
