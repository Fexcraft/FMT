package net.fexcraft.app.fmt.utils.fvtm;

import java.io.File;

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
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.Widget;

public class FVTMConfigEditor extends Widget {

    private ScrollablePanel panel;
    private Reference ref;
    private JsonMap map;
    private File file;

    private static int width = 700, height = 500, pwidth = 1000;

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
        }));
        fill();
        FMT.FRAME.getContainer().add(this);
    }

    private void fill(){
        int height = 0;
        for(ConfigEntry entry : ref.getEntries()){
            EntryComponent com = new EntryComponent(entry, map.get(entry.name));
            height += com.gen(height);
            panel.getContainer().add(com);
        }
        panel.getContainer().setSize(pwidth, height);
    }

    public static class EntryComponent extends Component {

        private Label label;
        public TextInput input;

        public EntryComponent(ConfigEntry entry, JsonObject obj){
            add(label = new Label(entry.name, 10, 0, 200, 30));
            if(entry.type.subs()){

            }
            else if(entry.type.trio()){
                //
            }
            else if(entry.type.color()){
                add(new ColorField(this, (color, bool) -> {
                    //
                }, 220, 2, 300, 26, null, false).apply(obj == null ? entry.defi : obj.integer_value()));
            }
            else if(entry.type.numer()){

            }
            else if(entry.type.bool()){
                add(new BoolButton(220, 2, 300, 26, obj == null ? entry.defb : obj.bool(), bool -> {}));
            }
            else{//text
                add(input = new TextInput(obj == null ? entry.def : obj.string_value(), 220, 2, 300, 26));
                input.addTextInputContentChangeEventListener(event -> {
                    //
                });
            }
        }

        public int gen(int height){
            setSize(pwidth, 30);
            setPosition(0, height);
            return 30;
        }
    }


}
