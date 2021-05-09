package net.fexcraft.app.fmt.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;

public class Picker {
	
	public static PickType TYPE = PickType.NONE;
	public static PickTask TASK = PickTask.NONE;
	private static ByteBuffer buffer;
	private static boolean filled, offcenter;
	private static Polygon polygon;

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
		
		NONE, POLYGON, FACE, COLOR;
		
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
			return this == COLOR;
		}
		
	}
	
	public static enum PickTask {
		
		NONE, SELECT, PAINT;
		
		public boolean pick(){
			return this != NONE;
		}

		public boolean select(){
			return this == SELECT;
		}

		public boolean paint(){
			return this == PAINT;
		}
		
	}

	public static void reset(){
		TYPE = PickType.NONE;
		TASK = PickTask.NONE;
	}

	public static void pick(PickType type, PickTask task, boolean off){
		TYPE = type;
		TASK = task;
		offcenter = off;
		polygon = null;
	}

	public static void process(){
		if(!filled) fillBuffer();
		if(TYPE.color()){
			
		}
		else if(TYPE.face() && polygon != null){
			
		}
		else{
			int pick = getPick();
			for(Group group : FMT.MODEL.groups()){
				if(polygon != null) break;
				for(Polygon poly : group){
					if(poly.colorIdx == pick){
						polygon = poly;
						break;
					}
				}
			}
			if(polygon == null) reset();
			else{
				if(TASK.select()){
					polygon.group().model.select(polygon);
				}
				else if(TASK.paint()){
					//
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

}
