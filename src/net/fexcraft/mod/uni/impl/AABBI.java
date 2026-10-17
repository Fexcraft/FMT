package net.fexcraft.mod.uni.impl;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.mod.uni.world.AABB;

public class AABBI extends AABB {

	@Override
	public AABB set(float sx, float sy, float sz, float ex, float ey, float ez){
		return this;
	}

	@Override
	public <AB> AB local(){
		return (AB)this;
	}

	@Override
	public Object direct(){
		return this;
	}

	@Override
	public AABB offset(int x, int y, int z){
		return this;
	}

	@Override
	public AABB offset(V3D vec){
		return this;
	}

	@Override
	public boolean contains(V3D vec){
		return false;
	}

	@Override
	public boolean contains(Object vec){
		return false;
	}

	@Override
	public double minX(){
		return 0;
	}

	@Override
	public double minY(){
		return 0;
	}

	@Override
	public double minZ(){
		return 0;
	}

	@Override
	public double maxX(){
		return 0;
	}

	@Override
	public double maxY(){
		return 0;
	}

	@Override
	public double maxZ(){
		return 0;
	}

}
