package net.fexcraft.app.fmt.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.spinyowl.legui.component.Panel;
import net.fexcraft.app.fmt.nui.Element;
import net.fexcraft.app.fmt.polygon.Vertoff;
import net.fexcraft.app.fmt.polygon.Vertoff.VOKey;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.PickMode;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.polygon.Arrows;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.texture.TexturePainter;

import static net.fexcraft.app.fmt.utils.Logging.log;

public class Picker {
	
	public static PickType TYPE = PickType.NONE;
	public static PickTask TASK = PickTask.NONE;
	private static ByteBuffer buffer;
	private static boolean offcenter;
	private static Polygon polygon;
	private static Consumer<Polygon> consumer;
	private static BiConsumer<Polygon, VOKey> vert_consumer;
	public static Face selected_face = NoFace.NONE;
	public static Element LAST_HOVER;

	public static void resetBuffer(boolean resize){
		if(resize){
			buffer = ByteBuffer.allocateDirect(FMT.WIDTH * FMT.HEIGHT * 4);
			buffer.order(ByteOrder.nativeOrder());
		}
	}
	
	public static enum PickType {
		
		NONE, VERTEX, FACE, POLYGON, COLOR, UI;
		
		public boolean pick(){
			return this != NONE;
		}

		public boolean polygon(){
			return this == POLYGON || this == FACE;
		}

		public boolean face(){
			return this == FACE;
		}

		public boolean vertex(){
			return this == VERTEX;
		}

		public boolean color(){
			return this == COLOR;
		}
		
	}
	
	public static enum PickTask {
		
		NONE, SELECT, RESELECT, MULTISELECT, PAINT, FUNCTION, HOVER;
		
		public boolean pick(){
			return this != NONE;
		}

		public boolean select(){
			return this == SELECT || this == RESELECT || this == MULTISELECT;
		}

		public boolean multisel(){
			return this == MULTISELECT;
		}

		public boolean paint(){
			return this == PAINT;
		}

		boolean function(){
			return this == FUNCTION;
		}

		boolean nonfunc(){
			return this != FUNCTION;
		}
		
	}

	public static void reset(){
		TYPE = PickType.NONE;
		TASK = PickTask.NONE;
		UpdateHandler.update(new PickMode(TYPE, TASK, false));
	}

	public static void pick(PickType type, PickTask task, boolean off){
		TYPE = type;
		TASK = task;
		offcenter = off;
		polygon = null;
		selected_face = NoFace.NONE;
		UpdateHandler.update(new PickMode(type, task, off));
	}

	public static void process(){
		fillBuffer();
		if(TYPE.color()){
			TexturePainter.updateColor(getPick(), TexturePainter.ACTIVE, true);
		}
		else if(TYPE.face() && polygon != null){
			int face = getPick();
			if(TASK.paint()){
				TexturePainter.paint(polygon, face);
			}
			else{
				selected_face = polygon.getFaceByColor(face);
				UpdateHandler.update(new UpdateEvent.PickFace(polygon, selected_face));
			}
		}
		else if(TYPE == PickType.UI){
			int pick = getPick();
			if(pick <= 0 || pick > Element.elmIdx) return;
			Element elm = getElm(FMT.UI.elements, pick);
			if(elm == null){
				LAST_HOVER = null;
				if(TASK.select()) pick(PickType.POLYGON, PickTask.SELECT, offcenter);
			}
			else if(TASK == PickTask.HOVER){
				elm.hovered(true);
				LAST_HOVER = elm;
			}
			else if(TASK.select()){
				elm.click(GGR.mousePosX(), GGR.mousePosY());
			}
			reset();
		}
		else if(TYPE.vertex()){
			int pick = getPick();
			Logging.bar("picked: " + pick);
			if(pick < Polygon.startIdx) return;
			Pair<Polygon, VOKey> off = Vertoff.getPicked(pick);
			if(off == null) return;
			if(TASK.select()){
				FMT.MODEL.select(off);
			}
			else if(TASK.function()){
				vert_consumer.accept(off.getLeft(), off.getRight());
				vert_consumer = null;
			}
			Selector.set(PickType.POLYGON);
		}
		else{
			if(TASK.multisel()){
				multipick();
				return;
			}
			int pick = getPick();
			Logging.bar("picked: " + pick);
			if(pick > 0 && pick < Polygon.startIdx){
				Arrows.SEL = pick;
				Arrows.DIR = Arrows.MODE == Arrows.ArrowMode.SIZE ? false : pick % 2 == 0;
				return;
			}
			for(Group group : FMT.MODEL.allgroups()){
				if(polygon != null) break;
				for(Polygon poly : group){
					if(poly.colorIdx == pick){
						polygon = poly;
						break;
					}
				}
			}
			if(polygon == null && TASK.nonfunc()) reset();
			else{
				if(TASK.select()){
					polygon.group().model.select(polygon, TASK == PickTask.RESELECT);
				}
				else if(TASK.paint()){
					if(TexturePainter.SELMODE.getPickType() == PickType.FACE) return;
					TexturePainter.paint(polygon, -1);
				}
				else if(TASK.function()){
					consumer.accept(polygon);
					consumer = null;
				}
			}
		}
	}

	private static void multipick(){
		ArrayList<Integer> picks = new ArrayList<>();
		int pick;
		for(float x = 0; x < FMT.CAM.getSelSiz().x; x++){
			for(float y = 0; y < FMT.CAM.getSelSiz().y; y++){
				pick = getPick((int)(FMT.CAM.getSelPos().x + x), -((int)(FMT.CAM.getSelPos().y + y) - FMT.HEIGHT), false);
				if(!picks.contains(pick)) picks.add(pick);
			}
		}
		Logging.bar("picked: " + picks.size() + "x");
		for(Group group : FMT.MODEL.allgroups()){
			for(Polygon poly : group){
				if(picks.contains(poly.colorIdx)){
					poly.group().model.select(poly, false);
				}
			}
		}
	}

	private static int getPick(){
		return getPick(GGR.mousePosX(), -(GGR.mousePosY() - FMT.HEIGHT), !offcenter);
	}

	private static int getPick(int x, int y, boolean center){
		byte[] picked = new byte[4];
		if(center){
			x = FMT.WIDTH / 2;
			y = FMT.HEIGHT / 2;
		}
		x = (x + y * FMT.WIDTH) * 4;
		if(x < 0 || x >= buffer.capacity()) return 0xffffffff;
		buffer.get(x, picked);
		return ByteUtils.getRGB(picked);
	}

	private static void fillBuffer(){
		GL11.glReadPixels(0, 0, FMT.WIDTH, FMT.HEIGHT, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	}

	public static Polygon polygon(){
		return polygon;
	}
	
	public static void setConsumer(Consumer<Polygon> cons){
		consumer = cons;
	}

	public static Element getElm(List<Element> elms, int color){
		if(elms == null) return null;
		for(Element elm : elms){
			if(elm.colorIdx == color) return elm;
			Element e = getElm(elm.elements, color);
			if(e != null) return e;
		}
		return null;
	}

}