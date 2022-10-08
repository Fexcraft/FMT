package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.texture.Texture;
import net.fexcraft.app.fmt.ui.GenericDialog;

public class AutoUVPositioner {

	public static boolean HALT = true, ALL, SAVESPACE, DETACH;
	private static Texture texture;
	private static Group selected, resetsel;

	public static void runReset(){
		int width = 440;
		resetsel = null;
		Dialog dialog = new Dialog(translate("texture_autopos.reset.dialog"), width + 20, 180);
		Label label0 = new Label(translate("texture_autopos.reset.info"), 10, 10, width, 20);
		label0.getStyle().setFont("roboto-bold");
		Label label1 = new Label(translate("texture_autopos.reset.group"), 10, 40, width / 20, 20);
		SelectBox<String> texture = new SelectBox<>(10 + width / 2, 40, width / 2, 20);
		texture.addElement("all-groups");
		for(Group group : FMT.MODEL.groups()) texture.addElement(group.id);
		texture.addSelectBoxChangeSelectionEventListener(listener -> {
			if(listener.getNewValue().equals("all-groups")) resetsel = null;
			else resetsel = FMT.MODEL.get(listener.getNewValue());
		});
		texture.setSelected(0, true);
		texture.setVisibleCount(12);
		Button button = new Button(translate("dialog.button.confirm"), 10, 130, 100, 20);
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(e.getAction() != MouseClickAction.CLICK) return;
			if(resetsel != null){
				resetsel.forEach(poly -> {
					poly.textureX = -1;
					poly.textureY = -1;
					poly.recompile();
				});
			}
			else{
				FMT.MODEL.groups().forEach(group -> group.forEach(poly -> {
					poly.textureX = -1;
					poly.textureY = -1;
					poly.recompile();
				}));
			}
			dialog.close();
			GenericDialog.showOK(null, null, null, "texture_autopos.reset.done");
		});
		dialog.getContainer().add(label0);
		dialog.getContainer().add(label1);
		dialog.getContainer().add(texture);
		dialog.getContainer().add(button);
		dialog.show(FMT.FRAME);
	}
	
}
