package net.fexcraft.app.fmt.ui;

import java.io.File;
import java.util.Map;

import com.google.gson.JsonElement;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.misc.listener.widget.WidgetResizeButtonDragListener;

public class JsonEditor extends Dialog {

    private File file;
    private JsonMap map;
    private ScrollablePanel panel;
    private static int height;
    private static int width;

    public JsonEditor(File file){
        map = JsonHandler.parse(this.file = file);
        getTitleTextState().setText("JsonEditor - " + file.getName());
        setResizable(false);
        setSize(700, 470);
        Settings.applyComponentTheme(getContainer());
        getContainer().add(panel = new ScrollablePanel(10, 10, 680, 400));
        fill(panel.getContainer(), map);
        panel.getContainer().setSize(width = 1000, 400);
        getContainer().add(new RunButton("dialog.button.save", 480, 420, 100, 24, () -> {
            JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
        }));
        getContainer().add(new RunButton("dialog.button.close", 590, 420, 100, 24, () -> {
            close();
        }));
        resize();
        this.show(FMT.FRAME);
    }

    private void fill(Component container, JsonMap map){
        removeIf(com -> com instanceof Resizeable);
        for(Map.Entry<String, JsonObject<?>> entry : map.entries()){
            if(entry.getValue().isMap()){
                container.add(new JMapCom(entry.getKey(), entry.getValue().asMap()));
            }
            else if(entry.getValue().isArray()){
                container.add(new JArrCom(entry.getKey(), entry.getValue().asArray()));
            }
            else{
                container.add(new JElmCom(entry.getKey(), entry.getValue()));
            }
        }
    }

    private void resize(){
        height = 0;
        panel.getContainer().getChildComponents().forEach(com -> {
            if(com instanceof Resizeable) height += ((Resizeable)com).resize(0);
        });
        panel.getContainer().setSize(width, height < panel.getSize().y - 10 ? panel.getSize().y - 10 : height);
    }

    public static class JMapCom extends Component implements Resizeable{

        public boolean minimized = false;
        public JsonMap map;
        public String key;
        public Label label;

        public JMapCom(String key, JsonMap map){
            add(label = new Label(this.key = key, 10, 0, 100, 30));
            this.map = map;
            for(Map.Entry<String, JsonObject<?>> entry : map.entries()){
                if(entry.getValue().isMap()){
                    add(new JMapCom(entry.getKey(), entry.getValue().asMap()));
                }
                else if(entry.getValue().isArray()){
                    add(new JArrCom(entry.getKey(), entry.getValue().asArray()));
                }
                else{
                    add(new JElmCom(entry.getKey(), entry.getValue()));
                }
            }
        }

        @Override
        public int resize(int off){
            setPosition(off == 0 ? 0 : 20, off == 0 ? height : off * 30);
            int h = 30;
            off = 1;
            if(!minimized){
                for(Component com : getChildComponents()){
                    if(com instanceof Resizeable) h += ((Resizeable)com).resize(off++);
                }
            }
            setSize(width - getPosition().x, h);
            return h;
        }
    }

    public static class JArrCom extends Component implements Resizeable {

        public boolean minimized = false;
        public JsonArray array;
        public String key;
        public Label label;

        public JArrCom(String key, JsonArray arr) {
            add(label = new Label(this.key = key, 10, 0, 100, 30));
            this.array = arr;
            for(int i = 0; i < array.size(); i++){
                JsonObject elm = array.get(i);
                if(elm.isMap()){
                    add(new JMapCom(i + "", elm.asMap()));
                }
                else if(elm.isArray()){
                    add(new JArrCom(i + "", elm.asArray()));
                }
                else{
                    add(new JElmCom(i + "", elm));
                }
            }
        }

        @Override
        public int resize(int off){
            setPosition(off == 0 ? 0 : 20, off == 0 ? height : off * 30);
            int h = 30;
            off = 1;
            if(!minimized){
                for(Component com : getChildComponents()){
                    if(com instanceof Resizeable) h += ((Resizeable)com).resize(off++);
                }
            }
            setSize(width - getPosition().x, h);
            return h;
        }

    }

    public static class JElmCom extends Component implements Resizeable {

        public JsonObject<?> elm;
        public String key;
        public Label label;
        public TextInput input;

        public JElmCom(String key, JsonObject<?> elm){
            add(label = new Label(this.key = key, 10, 0, 200, 30));
            this.elm = elm;
            add(input = new TextInput(elm.string_value(), 220, 2, 300, 26));
            input.addTextInputContentChangeEventListener(event -> {
                if(elm.isNumber()){
                    ((JsonObject<Number>)elm).value(Float.parseFloat(event.getNewValue()));
                }
                else ((JsonObject<String>)elm).value(event.getNewValue());
            });
        }

        @Override
        public int resize(int off){
            setPosition(off == 0 ? 0 : 20, off == 0 ? height : off * 30);
            setSize(width - getPosition().x, 30);
            return 30;
        }

    }

    private static interface Resizeable {

        public int resize(int off);

    }

}
