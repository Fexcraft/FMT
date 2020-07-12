package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.event.MouseDragEvent;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TexViewBox {
	
	private static Widget viewbox;
	private static ScrollablePanel canvas;

	public static final void open(String texGroup){
		viewbox = new Widget("TexView", 320, 50, FMTB.WIDTH / 2, FMTB.HEIGHT / 2);
		viewbox.addWidgetCloseEventListener(listener -> viewbox = null);
		TextureGroup group = TextureManager.getGroup(texGroup);
		canvas = new ScrollablePanel(4, 32, viewbox.getSize().x - 8, viewbox.getSize().y - 40);
		canvas.getContainer().setSize(group.texture.getWidth(), group.texture.getHeight());
		viewbox.getResizeButton().getListenerMap().addListener(MouseDragEvent.class, listner -> {
			canvas.setSize(viewbox.getSize().x - 8, viewbox.getSize().y - 40);
		});
		viewbox.getContainer().add(canvas);
		//
		FMTB.frame.getContainer().add(viewbox);
	}
	
	public static boolean isOpen(){
		return viewbox != null;
	}

}
