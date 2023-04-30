package net.fexcraft.app.fmt.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.PickMode;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.polygon.Arrows;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.texture.TexturePainter;

public class Picker {
	
	public static PickType TYPE = PickType.NONE;
	public static PickTask TASK = PickTask.NONE;
	private static ByteBuffer buffer;
	private static boolean filled, offcenter;
	private static Polygon polygon;
	private static Consumer<Polygon> consumer;
	public static Face selected_face = NoFace.NONE;

	public static void resetBuffer(boolean resize){
		if(resize){
			buffer = ByteBuffer.allocateDirect(FMT.WIDTH * FMT.HEIGHT * 4);
			buffer.order(ByteOrder.nativeOrder());
		}
		if(!filled) return;
		buffer.clear();
		filled = false;
	}
	
	public static enum PickType {
		
		NONE, POLYGON, FACE, COLOR1, COLOR2;
		
		public boolean pick(){
			return this != NONE;
		}

		public boolean polygon(){
			return this == POLYGON || this == FACE;
		}

		public boolean face(){
			return this == FACE;
		}
		
		public boolean color(){
			return this == COLOR1 || this == COLOR2;
		}
		
	}
	
	public static enum PickTask {
		
		NONE, SELECT, RESELECT, PAINT1, PAINT2, FUNCTION;
		
		public boolean pick(){
			return this != NONE;
		}

		public boolean select(){
			return this == SELECT || this == RESELECT;
		}

		public boolean paint(){
			return this == PAINT1 || this == PAINT2;
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
		if(!filled) fillBuffer();
		if(TYPE.color()){
			TexturePainter.updateColor(getPick(), TYPE == PickType.COLOR1, true);
		}
		else if(TYPE.face() && polygon != null){
			int face = getPick();
			if(TASK.paint()){
				TexturePainter.paint(TASK == PickTask.PAINT1, polygon, face);
			}
			else{
				selected_face = polygon.getFaceByColor(face);
				UpdateHandler.update(new UpdateEvent.PickFace(polygon, selected_face));
			}
		}
		else{
			int pick = getPick();
			Logging.bar("dir: " + pick);
			if(pick > 0 && pick < Polygon.startIdx){
				Arrows.SEL = pick;
				Arrows.DIR = Arrows.MODE == Arrows.ArrowMode.SIZE ? false : pick % 2 == 0;
				return;
			}
			for(Group group : FMT.MODEL.groups()){
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
					TexturePainter.paint(TASK == PickTask.PAINT1, polygon, -1);
				}
				else if(TASK.function()){
					consumer.accept(polygon);
					consumer = null;
				}
			}
		}
	}

	private static int getPick(){
		int x, y;
		byte[] picked = new byte[3];
		if(offcenter){
			x = GGR.mousePosX();
			y = -(GGR.mousePosY() - FMT.HEIGHT);
		}
		else{
			x = FMT.WIDTH / 2;
			y = FMT.HEIGHT / 2;
		}
		buffer.get((x + y * FMT.WIDTH) * 3, picked);
		return ByteUtils.getRGB(picked);
	}

	private static void fillBuffer(){
		GL11.glReadPixels(0, 0, FMT.WIDTH, FMT.HEIGHT, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
	}

	public static Polygon polygon(){
		return polygon;
	}
	
	public static void setConsumer(Consumer<Polygon> cons){
		consumer = cons;
	}

}
