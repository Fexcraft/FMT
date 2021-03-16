package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.ScrollEvent;
import org.liquidengine.legui.input.KeyCode;
import org.liquidengine.legui.input.Keyboard;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.EditorBase.SPVSL;
import net.fexcraft.app.fmt.utils.KeyCompound;
import net.fexcraft.app.fmt.utils.KeyCompound.KeyFunction;
import net.fexcraft.app.fmt.utils.Translator;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class KeyAdjuster {

	public static final void open(){
		Dialog dialog = new Dialog(Translator.translate("keyadjuster.title"), 520, 350);
		dialog.setResizable(false);
		ScrollablePanel panel = new ScrollablePanel(10, 10, 500, 280);
		int size = 10 + (KeyCompound.keys.size() * 30);
		int index = 0;
		panel.getContainer().setSize(500, size < 280 ? 280 : size);
		for(KeyFunction func : KeyCompound.keys){
			panel.getContainer().add(new Label(func.name(), 10, 10 + (index * 30), 180, 20));
			SelectBox<String> box = new SelectBox<>(190, 10 + (index * 30), 230, 20);
			for(KeyCode code : KeyCode.values()){
				box.addElement(code.name());
			}
			box.addSelectBoxChangeSelectionEventListener(listener -> {
				int id = Keyboard.getNativeCode(KeyCode.valueOf(listener.getNewValue()));
				if(occupied(id)){
					DialogBox.showOK("keyadjuster.title", null, null, "keyadjuster.occupied");
					box.setSelected(Keyboard.getKeyCode(func.id()).name(), true);
				}
				else func.setId(id);
			});
			box.setSelected(Keyboard.getKeyCode(func.id()).name(), true);
			box.setVisibleCount(12);
			panel.getContainer().add(box);
			Button button = new Button(Translator.translate("keyadjuster.reset"), 430, 10 + (index * 30), 50, 20);
			button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
				if(CLICK == e.getAction()){
					func.setId(null);
					box.setSelected(Keyboard.getKeyCode(func.id()).toString(), true);
				}
			});
			panel.getContainer().add(button);
			index++;
		}
		panel.setHorizontalScrollBarVisible(false);
		panel.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
		panel.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL());
		Button button = new Button(Translator.translate("dialogbox.button.continue"), 10, 300, 100, 20);
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()) dialog.close();
		});
		dialog.getContainer().add(panel);
		dialog.getContainer().add(button);
		dialog.show(FMTB.frame);
	}

	private static boolean occupied(int id){
		for(KeyFunction func : KeyCompound.keys){
			if(func.id() == id) return true;
		}
		return false;
	}

}
