package net.fexcraft.app.fmt.ui;

import java.io.File;
import java.util.Map;

import com.google.gson.JsonElement;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
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
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.listener.MouseClickEventListener;

public class JsonEditor extends Dialog {

    private static File file;
    private static JsonMap map;
    private static ScrollablePanel panel;
    private static int height;
    private static int width;
    private static JsonEditor INST;

    public JsonEditor(File file){
        map = JsonHandler.parse(this.file = file);
        getTitleTextState().setText("JsonEditor - " + file.getName());
        setResizable(false);
        setSize(700, 470);
        INST = this;
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
        container.removeIf(com -> com instanceof Resizeable);
        for(Map.Entry<String, JsonObject<?>> entry : map.entries()){
            Runnable run = () -> {
              JsonEditorMenu.show(this, map, entry.getKey(), entry.getValue());
            };
            if(entry.getValue().isMap()){
                container.add(new JMapCom(entry.getKey(), entry.getValue().asMap(), run));
            }
            else if(entry.getValue().isArray()){
                container.add(new JArrCom(entry.getKey(), entry.getValue().asArray(), run));
            }
            else{
                container.add(new JElmCom(entry.getKey(), entry.getValue(), run));
            }
        }
    }

    private static void resize(){
        height = 0;
        panel.getContainer().getChildComponents().forEach(com -> {
            if(com instanceof Resizeable) height += ((Resizeable)com).resize(0);
        });
        panel.getContainer().setSize(width, height < panel.getSize().y - 10 ? panel.getSize().y - 10 : height);
    }

    protected void refill(){
        fill(panel.getContainer(), map);
        resize();
    }

    public static class JMapCom extends Component implements Resizeable{

        public boolean minimized = false;
        public JsonMap map;
        public String key;
        public Label label;

        public JMapCom(String key, JsonMap map, Runnable run){
            add(label = new Label("{} " + (this.key = key), 10, 0, 200, 30));
            label.getListenerMap().addListener(MouseClickEvent.class, lis -> {
                if(lis.getAction() == MouseClickEvent.MouseClickAction.CLICK && lis.getButton() == Mouse.MouseButton.MOUSE_BUTTON_LEFT){
                    minimized = !minimized;
                    JsonEditor.resize();
                }
            });
            MouseClickEventListener listener = lis -> {
                if(lis.getAction() == MouseClickEvent.MouseClickAction.CLICK && lis.getButton() == Mouse.MouseButton.MOUSE_BUTTON_RIGHT){
                    run.run();
                }
            };
            label.getListenerMap().addListener(MouseClickEvent.class, listener);
            getListenerMap().addListener(MouseClickEvent.class, listener);
            this.map = map;
            for(Map.Entry<String, JsonObject<?>> entry : map.entries()){
                Runnable ran = () -> {
                    JsonEditorMenu.show(INST, map, entry.getKey(), entry.getValue());
                };
                if(entry.getValue().isMap()){
                    add(new JMapCom(entry.getKey(), entry.getValue().asMap(), ran));
                }
                else if(entry.getValue().isArray()){
                    add(new JArrCom(entry.getKey(), entry.getValue().asArray(), ran));
                }
                else{
                    add(new JElmCom(entry.getKey(), entry.getValue(), ran));
                }
            }
        }

        @Override
        public int resize(int ph){
            setPosition(ph == 0 ? 0 : 20, ph == 0 ? height : ph);
            int h = 30;
            if(!minimized){
                for(Component com : getChildComponents()){
                    if(com instanceof Resizeable) h += ((Resizeable)com).resize(h);
                }
                h += 5;
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

        public JArrCom(String key, JsonArray arr, Runnable run){
            add(label = new Label("[] " + (this.key = key), 10, 0, 200, 30));
            label.getListenerMap().addListener(MouseClickEvent.class, lis -> {
                if(lis.getAction() == MouseClickEvent.MouseClickAction.CLICK && lis.getButton() == Mouse.MouseButton.MOUSE_BUTTON_LEFT){
                    minimized = !minimized;
                    JsonEditor.resize();
                }
            });
            MouseClickEventListener listener = lis -> {
                if(lis.getAction() == MouseClickEvent.MouseClickAction.CLICK && lis.getButton() == Mouse.MouseButton.MOUSE_BUTTON_RIGHT){
                    run.run();
                }
            };
            label.getListenerMap().addListener(MouseClickEvent.class, listener);
            getListenerMap().addListener(MouseClickEvent.class, listener);
            this.array = arr;
            for(int i = 0; i < array.size(); i++){
                String idx = i + "";
                JsonObject elm = array.get(i);
                Runnable ran = () -> {
                    JsonEditorMenu.show(INST, array, idx, elm);
                };
                if(elm.isMap()){
                    add(new JMapCom(i + "", elm.asMap(), ran));
                }
                else if(elm.isArray()){
                    add(new JArrCom(i + "", elm.asArray(), ran));
                }
                else{
                    add(new JElmCom(i + "", elm, ran));
                }
            }
        }

        @Override
        public int resize(int ph){
            setPosition(ph == 0 ? 0 : 20, ph == 0 ? height : ph);
            int h = 30;
            if(!minimized){
                for(Component com : getChildComponents()){
                    if(com instanceof Resizeable) h += ((Resizeable)com).resize(h);
                }
                h += 5;
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

        public JElmCom(String key, JsonObject<?> elm, Runnable run){
            add(label = new Label(this.key = key, 10, 0, 200, 30));
            MouseClickEventListener listener = lis -> {
                if(lis.getAction() == MouseClickEvent.MouseClickAction.CLICK && lis.getButton() == Mouse.MouseButton.MOUSE_BUTTON_RIGHT){
                    run.run();
                }
            };
            label.getListenerMap().addListener(MouseClickEvent.class, listener);
            getListenerMap().addListener(MouseClickEvent.class, listener);
            this.elm = elm;
            if(elm.string_value().equals("true") || elm.string_value().equals("false")){
                add(new BoolButton(220, 2, 300, 26, elm.bool(), bool -> ((JsonObject<Boolean>)elm).value(bool)));
            }
            else if(elm.string_value().startsWith("#") && elm.string_value().length() == 7){
                add(new ColorField(this, (color, bool) -> {
                    ((JsonObject<String>)elm).value("#" + Integer.toHexString(color));
                }, 220, 2, 300, 26, null, false).apply(Integer.parseInt(elm.string_value().replace("#", ""), 16)));
            }
            else{
                add(input = new TextInput(elm.string_value(), 220, 2, 300, 26));
                input.addTextInputContentChangeEventListener(event -> {
                    if(elm.isNumber()){
                        ((JsonObject<Number>)elm).value(Float.parseFloat(event.getNewValue()));
                    }
                    else ((JsonObject<String>)elm).value(event.getNewValue());
                });
            }
        }

        @Override
        public int resize(int ph){
            setPosition(ph == 0 ? 0 : 20, ph == 0 ? height : ph);
            setSize(width - getPosition().x, 30);
            return 30;
        }

    }

    private static interface Resizeable {

        public int resize(int ph);

    }

}
