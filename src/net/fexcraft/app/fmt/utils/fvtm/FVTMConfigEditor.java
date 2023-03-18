package net.fexcraft.app.fmt.utils.fvtm;

import java.io.File;
import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent;

public class FVTMConfigEditor extends Widget {

    public static ArrayList<FVTMConfigEditor> EDITORS = new ArrayList<>();

    private ScrollablePanel panel;
    private Reference ref;
    private JsonMap map;
    private File file;

    public static int width = 700, height = 500, pwidth = 1000;

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
            EDITORS.remove(this);
        }));
        fill();
        addWidgetCloseEventListener(lis -> {
            EDITORS.remove(this);
        });
        FMT.FRAME.getContainer().add(this);
        EDITORS.add(this);
    }

    private void fill(){
        int height = 0;
        for(ConfigEntry entry : ref.getEntries()){
            EntryComponent com = new EntryComponent(entry, map, entry.name, map.get(entry.name));
            height += com.gen(height);
            panel.getContainer().add(com);
        }
        panel.getContainer().setSize(pwidth, height);
    }

    public static class EntryComponent extends Component {

        private Label label;
        public TextInput input;
        private static String[] xyz = { "x", "y", "z" };

        public EntryComponent(ConfigEntry entry, JsonObject root, Object idxkey, JsonObject obj){
            add(label = new Label(entry.name + (entry.required ? "*" : ""), 10, 0, 200, 30));
            if(entry.type.subs()){
                if(entry.type == EntryType.ARRAY || entry.type == EntryType.ARRAY_OR_TEXT){

                }
                else if(entry.type == EntryType.OBJECT){

                }
                else if(entry.type == EntryType.OBJECT_KEY_VAL){

                }
            }
            else if(entry.type.trio()){
                Object ik = idxkey;
                for(int i = 0; i < 3; i++){
                    add(input = new TextInput(obj == null ? entry.def : obj.string_value(), 220 + (i * 100), 2, 90, 26));
                    if(root.isMap()){
                        ik = idxkey == null ? xyz[i] : idxkey.toString() + "_" + xyz[i];
                    }
                    int j = i;
                    Object fik = ik;
                    input.addTextInputContentChangeEventListener(event -> {
                        if(obj == null){
                            if(root.isMap()){
                                root.asMap().add(fik.toString(), new JsonObject<>(get(event, entry.type)));
                            }
                            else root.asArray().value.set(((int)fik) + j, new JsonObject<>(get(event, entry.type)));
                        }
                        else obj.value(get(event, entry.type));
                    });
                }
            }
            else if(entry.type.color()){
                add(new ColorField(this, (color, bool) -> {
                    if(obj == null){
                        if(root.isMap()) root.asMap().add(idxkey.toString(), color);
                        else root.asArray().value.set((int)idxkey, new JsonObject<Integer>(color));
                    }
                    else obj.value(color);
                }, 220, 2, 300, 26, null, false).apply(obj == null ? entry.defi : obj.integer_value()));
            }
            else if(entry.type.bool()){
                add(new BoolButton(220, 2, 300, 26, obj == null ? entry.defb : obj.bool(), bool -> {
                    if(obj == null){
                        if(root.isMap()) root.asMap().add(idxkey.toString(), bool);
                        else root.asArray().value.set((int)idxkey, new JsonObject<Boolean>(bool));
                    }
                    else obj.value(bool);
                }));
            }
            else if(entry.type == EntryType.ENUM){
                SelectBox<String> box = new SelectBox<>(220, 2, 300, 26);
                box.setVisibleCount(8);
                for(String en : entry.enums) box.addElement(en);
                box.addSelectBoxChangeSelectionEventListener(lis -> {
                    if(obj == null){
                        if(root.isMap()){
                            root.asMap().add(idxkey.toString(), new JsonObject<>(lis.getNewValue()));
                        }
                        else root.asArray().value.set((int)idxkey, new JsonObject<>(lis.getNewValue()));
                    }
                    else obj.value(lis.getNewValue());
                });
                if(obj != null) box.setSelected(obj.string_value(), true);
                add(box);
            }
            else{//text
                add(input = new TextInput(obj == null ? entry.def : obj.string_value(), 220, 2, 300, 26));
                input.addTextInputContentChangeEventListener(event -> {
                    if(obj == null){
                        if(root.isMap()){
                            root.asMap().add(idxkey.toString(), new JsonObject<>(get(event, entry.type)));
                        }
                        else root.asArray().value.set((int)idxkey, new JsonObject<>(get(event, entry.type)));
                    }
                    else obj.value(get(event, entry.type));
                });
            }
        }

        private Object get(TextInputContentChangeEvent event, EntryType type){
            if(type.numer()){
                return type == EntryType.INTEGER ? Integer.parseInt(event.getNewValue()) : Float.parseFloat(event.getNewValue());
            }
            //TODO validation for special types
            return event.getNewValue();
        }

        public int gen(int height){
            setSize(pwidth, 30);
            setPosition(0, height);
            return 30;
        }
    }


}
