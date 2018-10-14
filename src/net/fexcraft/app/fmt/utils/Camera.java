package net.fexcraft.app.fmt.utils;

public class Camera {

	private float[] pos;
	private String name;
	
	public Camera(){ this.name = "null"; }
	
	public Camera(float x, float y, float z, String name){
		this.name = name;
	}
	
	public void set(float x, float y, float z){
		pos[0] = x; pos[1] = y; pos[2] = z;
	}
	
	public void set(float[] pos){
		this.pos = pos;
	}
	
	public float[] get(){
		return pos;
	}
	
	public float getX(){
		return pos[0];
	}
	
	public float getY(){
		return pos[1];
	}
	
	public float getZ(){
		return pos[2];
	}
	
	public String getName(){
		return name;
	}
	
}
