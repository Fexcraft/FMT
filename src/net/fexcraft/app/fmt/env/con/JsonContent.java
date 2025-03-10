package net.fexcraft.app.fmt.env.con;

import com.spinyowl.legui.component.*;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.input.Mouse;
import com.spinyowl.legui.listener.MouseClickEventListener;
import net.fexcraft.app.fmt.env.EnvContent;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

import java.io.File;
import java.util.Map;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class JsonContent extends EnvContent {

    private JsonMap map;
    private ScrollablePanel panel;
    private int width = 1000;
    private int height;

    public JsonContent(File file){
        map = JsonHandler.parse(file);
        add(panel = new ScrollablePanel());
        fill(panel.getContainer(), map);
        resize();
    }

    @Override
    public void onResize(){
        panel.setSize(getSize().x - 10, getSize().y - 10);
    }

    private void fill(Component container, JsonMap map){
        container.removeIf(com -> com instanceof Resizeable);
        for(Map.Entry<String, JsonValue<?>> entry : map.entries()){
            Runnable run = () -> {};//TODO show menu
            if(entry.getValue().isMap()){
                container.add(new JMapCom(this, entry.getKey(), entry.getValue().asMap(), run));
            }
            else if(entry.getValue().isArray()){
                container.add(new JArrCom(this, entry.getKey(), entry.getValue().asArray(), run));
            }
            else{
                container.add(new JElmCom(this, entry.getKey(), entry.getValue(), run));
            }
        }
    }

    private void resize(){
        height = 0;
        panel.getContainer().getChildComponents().forEach(com -> {
            if(com instanceof Resizeable) height += ((Resizeable)com).resize(this, 0);
        });
        height = 0;
        panel.getContainer().getChildComponents().forEach(com -> {
            if(com instanceof Resizeable) height += ((Resizeable)com).fullheight();
        });
        panel.getContainer().setSize(width, height < panel.getSize().y - 10 ? panel.getSize().y - 10 : height);
    }

    protected void refill(){
        fill(panel.getContainer(), map);
        resize();
    }

    public static class JMapCom extends Component implements Resizeable {

        public boolean minimized = false;
        public JsonMap map;
        public String key;
        public Label label;

        public JMapCom(JsonContent con, String key, JsonMap map, Runnable run){
            add(label = new Label(this.key = key, 30, 0, 200, 30));
            add(new Icon(0, 20, 0, 5, 5, "./resources/textures/icons/configeditor/object_kv.png", () -> {}).addTooltip("JSON Object/Map"));
            label.getListenerMap().addListener(MouseClickEvent.class, lis -> {
                if(lis.getAction() == MouseClickEvent.MouseClickAction.CLICK && lis.getButton() == Mouse.MouseButton.MOUSE_BUTTON_LEFT){
                    minimized = !minimized;
                    con.resize();
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
            for(Map.Entry<String, JsonValue<?>> entry : map.entries()){
                Runnable ran = () -> {};//TODO show menu
                if(entry.getValue().isMap()){
                    add(new JMapCom(con, entry.getKey(), entry.getValue().asMap(), ran));
                }
                else if(entry.getValue().isArray()){
                    add(new JArrCom(con, entry.getKey(), entry.getValue().asArray(), ran));
                }
                else{
                    add(new JElmCom(con, entry.getKey(), entry.getValue(), ran));
                }
            }
        }

        @Override
        public int resize(JsonContent con, int ph){
            setPosition(ph == 0 ? 0 : 20, ph == 0 ? con.height : ph);
            int h = 30;
            if(!minimized){
                for(Component com : getChildComponents()){
                    if(com instanceof Resizeable) h += ((Resizeable)com).resize(con, h);
                }
                h += 5;
            }
            setSize(con.width - getPosition().x, h);
            return h;
        }

        @Override
        public int fullheight(){
            int h = 30;
            boolean c = false;
            for(Component com : getChildComponents()){
                if(com instanceof Resizeable){
                    h += ((Resizeable)com).fullheight();
                    c = true;
                }
            }
            return h + (c ? 5 : 0);
        }
    }

    public static class JArrCom extends Component implements Resizeable {

        public boolean minimized = false;
        public JsonArray array;
        public String key;
        public Label label;

        public JArrCom(JsonContent con, String key, JsonArray arr, Runnable run){
            add(label = new Label(this.key = key, 30, 0, 200, 30));
            add(new Icon(0, 20, 0, 5, 5, "./resources/textures/icons/configeditor/array_s.png", () -> {}).addTooltip("JSON Array"));
            label.getListenerMap().addListener(MouseClickEvent.class, lis -> {
                if(lis.getAction() == MouseClickEvent.MouseClickAction.CLICK && lis.getButton() == Mouse.MouseButton.MOUSE_BUTTON_LEFT){
                    minimized = !minimized;
                    con.resize();
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
                JsonValue elm = array.get(i);
                Runnable ran = () -> {};//TODO show menu
                if(elm.isMap()){
                    add(new JMapCom(con, i + "", elm.asMap(), ran));
                }
                else if(elm.isArray()){
                    add(new JArrCom(con, i + "", elm.asArray(), ran));
                }
                else{
                    add(new JElmCom(con, i + "", elm, ran));
                }
            }
        }

        @Override
        public int resize(JsonContent con, int ph){
            setPosition(ph == 0 ? 0 : 20, ph == 0 ? con.height : ph);
            int h = 30;
            if(!minimized){
                for(Component com : getChildComponents()){
                    if(com instanceof Resizeable) h += ((Resizeable)com).resize(con, h);
                }
                h += 5;
            }
            setSize(con.width - getPosition().x, h);
            return h;
        }

        @Override
        public int fullheight(){
            int h = 30;
            boolean c = false;
            for(Component com : getChildComponents()){
                if(com instanceof Resizeable){
                    h += ((Resizeable)com).fullheight();
                    c = true;
                }
            }
            return h + (c ? 5 : 0);
        }

    }

    public static class JElmCom extends Component implements Resizeable {

        public JsonValue<?> elm;
        public String key;
        public Label label;
        public TextInput input;

        public JElmCom(JsonContent con, String key, JsonValue<?> elm, Runnable run){
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
                add(new BoolButton(220, 2, 300, 26, elm.bool(), bool -> ((JsonValue<Boolean>)elm).value(bool)));
            }
            else if(elm.string_value().startsWith("#") && elm.string_value().length() == 7){
                add(new ColorField(this, (color, bool) -> {
                    ((JsonValue<String>)elm).value("#" + Integer.toHexString(color));
                }, 220, 2, 300, 26, null, false).apply(Integer.parseInt(elm.string_value().replace("#", ""), 16)));
            }
            else{
                add(input = new TextInput(elm.string_value(), 220, 2, 300, 26));
                input.addTextInputContentChangeEventListener(event -> {
                    if(elm.isNumber()){
                        ((JsonValue<Number>)elm).value(Float.parseFloat(event.getNewValue()));
                    }
                    else ((JsonValue<String>)elm).value(event.getNewValue());
                });
            }
        }

        @Override
        public int resize(JsonContent con, int ph){
            setPosition(ph == 0 ? 0 : 20, ph == 0 ? con.height : ph);
            setSize(con.width - getPosition().x, 30);
            return 30;
        }

        @Override
        public int fullheight(){
            return 30;
        }

    }

    private static interface Resizeable {

        public int resize(JsonContent con, int ph);

        public int fullheight();

    }

}
