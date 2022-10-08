package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.Translator;

public class ModelExports extends EditorComponent {
	
	private SelectBox<String> values = new SelectBox<>();
	protected static final String genid = "model.export_values";
	private TextField currval;
	private String current;
	private int curridx = -3;
	
	public ModelExports(){
		super(genid, 180, false, true);
		this.add(new RunButton(LANG_PREFIX + genid + ".add_entry", L5, row(1), LW, HEIGHT, () -> addEntryDialog()));
		this.add(new RunButton(LANG_PREFIX + genid + ".add_list", L5, row(1), LW, HEIGHT, () -> addListDialog()));
		this.add(new Label(translate(LANG_PREFIX + genid + ".manage"), L5, row(1), LW, HEIGHT));
		values.setPosition(L5, row(1));
		values.setSize(LW, HEIGHT - 2);
		values.setVisibleCount(12);
		updateholder.add(UpdateType.MODEL_LOAD, vals -> updateValuesBox());
		updateholder.add(UpdateType.MODEL_EXPORT_VALUE, vals -> updateValuesBox());
		values.addSelectBoxChangeSelectionEventListener(listener -> updateCurrentField(listener.getNewValue()));
		this.add(currval = new TextField("", L5, row(1), LW, HEIGHT - 2));
		this.add(new RunButton(LANG_PREFIX + genid + ".update", F20, row(1), F2S, HEIGHT, () -> updateValue()));
		this.add(new RunButton(LANG_PREFIX + genid + ".remove", F21, row(), F2S, HEIGHT, () -> removeValue()));
		this.add(values);
	}

	private void updateValuesBox(){
		while(values.getElements().size() > 0) values.removeElement(0);
		for(Entry<String, ArrayList<String>> entry : FMT.MODEL.export_listed_values.entrySet()){
			values.addElement("[L] " + entry.getKey());
			for(int i = 0; i < entry.getValue().size(); i++){
				values.addElement("[L/" + i + "] " + entry.getKey());
			}
		}
		for(String entry : FMT.MODEL.export_values.keySet()){
			values.addElement("[V] " + entry);
		}
		current = "";
		currval.getTextState().setText("");
		curridx = -3;
		if(values.getElements().size() > 0){
			values.setSelected(0, true);
			updateCurrentField(values.getElements().get(0));
		}
	}

	private void updateCurrentField(String newval){
		if(newval.startsWith("[L]")){
			currval.getTextState().setText(current = newval.substring(4));
			curridx = -1;
		}
		else if(newval.startsWith("[L/")){
			int lidx = newval.indexOf("]");
			curridx = Integer.parseInt(newval.substring(3, lidx));
			current = newval.substring(lidx + 2);
			currval.getTextState().setText(FMT.MODEL.export_listed_values.get(current).get(curridx));
		}
		else if(newval.startsWith("[V]")){
			currval.getTextState().setText(FMT.MODEL.export_values.get(current = newval.substring(4)));
			curridx = -2;
		}
	}

	private void updateValue(){
		switch(curridx){
			case -3: return;
			case -2:{
				FMT.MODEL.export_values.put(current, currval.getTextState().getText());
				break;
			}
			case -1:{
				ArrayList<String> list = FMT.MODEL.export_listed_values.remove(current);
				if(list == null) return;
				FMT.MODEL.export_listed_values.put(currval.getTextState().getText(), list);
				break;
			}
		}
		if(curridx >= 0){
			FMT.MODEL.export_listed_values.get(current).set(curridx, currval.getTextState().getText());
		}
		UpdateHandler.update(UpdateType.MODEL_EXPORT_VALUE);
	}

	private void removeValue(){
		switch(curridx){
			case -3: return;
			case -2:{
				FMT.MODEL.export_values.remove(current);
				break;
			}
			case -1:{
				FMT.MODEL.export_listed_values.remove(current);
				break;
			}
		}
		if(curridx >= 0){
			FMT.MODEL.export_listed_values.get(current).remove(curridx);
		}
		UpdateHandler.update(UpdateType.MODEL_EXPORT_VALUE);
	}

	private void addEntryDialog(){
		float width = 400;
        Dialog dialog = new Dialog(Translator.translate("editor.component.model.export_values.add_entry.dialog"), width, 170);
        Settings.applyComponentTheme(dialog.getContainer());
        dialog.setResizable(true);
    	Label label0 = new Label(Translator.translate("editor.component.model.export_values.add_entry.name"), 10, 10, width - 20, 20);
    	dialog.getContainer().add(label0);
    	TextField field0 = new TextField("Programs", 10, 35, width - 20, 20);
    	dialog.getContainer().add(field0);
    	Label label1 = new Label(Translator.translate("editor.component.model.export_values.add_entry.value"), 10, 60, width - 20, 20);
    	dialog.getContainer().add(label1);
    	TextField field1 = new TextField("group0 fvtm:glow", 10, 85, width - 20, 20);
    	dialog.getContainer().add(field1);
    	//
        Button button0 = new Button(Translator.translate("dialog.button.confirm"), 10, 120, 100, 20);
        button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){
        		String name = field0.getTextState().getText();
        		String value = field1.getTextState().getText();
        		boolean islist = FMT.MODEL.export_listed_values.containsKey(name);
        		if(islist) FMT.MODEL.export_listed_values.get(name).add(value);
        		else FMT.MODEL.export_values.put(name, value);
    			UpdateHandler.update(UpdateType.MODEL_EXPORT_VALUE, name, value, islist);
        		dialog.close();
        	}
        });
        dialog.getContainer().add(button0);
        //
        Button button1 = new Button(Translator.translate("dialog.button.cancel"), 120, 120, 100, 20);
        button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()) dialog.close();
        });
        dialog.getContainer().add(button1);
        //
        dialog.show(FMT.FRAME);
	}

	private void addListDialog(){
		float width = 400;
        Dialog dialog = new Dialog(Translator.translate("editor.component.model.export_values.add_list.dialog"), width, 130);
        Settings.applyComponentTheme(dialog.getContainer());
        dialog.setResizable(true);
    	Label label0 = new Label(Translator.translate("editor.component.model.export_values.add_list.name"), 10, 10, width - 20, 20);
    	dialog.getContainer().add(label0);
    	TextField field = new TextField("Programs", 10, 35, width - 20, 20);
    	dialog.getContainer().add(field);
    	//
        Button button0 = new Button(Translator.translate("dialog.button.confirm"), 10, 80, 100, 20);
        button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){
        		String name = field.getTextState().getText();
        		if(!FMT.MODEL.export_listed_values.containsKey(name)){
        			FMT.MODEL.export_listed_values.put(name, new ArrayList<>());
        			UpdateHandler.update(UpdateType.MODEL_EXPORT_VALUE, name);
        		}
        		dialog.close();
        	}
        });
        dialog.getContainer().add(button0);
        //
        Button button1 = new Button(Translator.translate("dialog.button.cancel"), 120, 80, 100, 20);
        button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()) dialog.close();
        });
        dialog.getContainer().add(button1);
        //
        dialog.show(FMT.FRAME);
	}

}
