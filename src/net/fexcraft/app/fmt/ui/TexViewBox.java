package net.fexcraft.app.fmt.ui;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.event.MouseDragEvent;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TexViewBox {
	
	private static Widget viewbox;
	private static ScrollablePanel canvas;

	public static final void open(String texGroup){
		open(texGroup, null, null, null, null);
	}

	public static final void open(String texGroup, Integer x, Integer y, Integer w, Integer h){
		if(viewbox != null){
			FMTB.frame.getContainer().remove(viewbox);
		}
		viewbox = new Widget("TexView", x == null ? 320 : x, y == null ? 50 : y, w == null ? FMTB.WIDTH / 2 : w, h == null ? FMTB.HEIGHT / 2 : h);
		viewbox.addWidgetCloseEventListener(listener -> viewbox = null);
		TextureGroup group = TextureManager.getGroup(texGroup);
		canvas = new ScrollablePanel(4, 32, viewbox.getSize().x - 8, viewbox.getSize().y - 56);
		canvas.getContainer().setSize(group.texture.getWidth(), group.texture.getHeight());
		viewbox.getResizeButton().getListenerMap().addListener(MouseDragEvent.class, listner -> {
			canvas.setSize(viewbox.getSize().x - 8, viewbox.getSize().y - 56);
		});
		canvas.getStyle().setBorder(null);
		canvas.getStyle().setBorderRadius(0f);
		canvas.getContainer().getStyle().setBorder(null);
		canvas.getContainer().getStyle().setBorderRadius(0f);
		canvas.setHorizontalScrollBarVisible(true);
		canvas.getContainer().getStyle().getBackground().setColor(new Vector4f(0.5f, 0.5f, 0.5f, 1f));
		canvas.setFocusable(false);
		viewbox.getContainer().add(canvas);
		//
		for(TurboList list : FMTB.MODEL.getGroups()){
			if(list.texgroup != null && !list.texgroup.group.equals(texGroup)) continue;
			for(PolygonWrapper wrapper : list){
				if(!wrapper.getType().isTexturable()) continue;
				float[][][] coords = wrapper.newTexturePosition(true);
				for(int i = 0; i < coords.length; i++){
					if(coords[i][1][0] - coords[i][0][0] == 0 || coords[i][1][1] - coords[i][0][1] == 0) continue;
					canvas.getContainer().add(new PolyFace(wrapper, i, coords[i]));
				}
			}
		}
		//
		FMTB.frame.getContainer().add(viewbox);
	}
	
	public static boolean isOpen(){
		return viewbox != null;
	}
	
	public static class PolyFace extends Panel {
		
		private PolygonWrapper wrapper;
		private float[][] coords;
		private int index;

		public PolyFace(PolygonWrapper wrapper, int idx, float[][] arr){
			super(wrapper.textureX + arr[0][0], wrapper.textureY + arr[0][1], arr[1][0] - arr[0][0], arr[1][1] - arr[0][1]);
			this.getStyle().setBorder(null);
			this.getStyle().setBorderRadius(0f);
			this.getStyle().getBackground().setColor(FMTB.rgba(wrapper.getTurboObject(0).getColor(idx)));
			this.wrapper = wrapper;
			this.coords = arr;
			this.index = idx;
		}
		
	}

	public static Vector2f pos(){
		return viewbox.getPosition();
	}

	public static Vector2f size(){
		return viewbox.getSize();
	}

}
