package net.fexcraft.app.fmt.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class Animator {
	
	public static final HashSet<Animation> nani = new HashSet<>();
	static {
		nani.add(new Rotator("rotator", new Setting(Type.FLOAT, "x", 0f), new Setting(Type.FLOAT, "y", 0f), new Setting(Type.FLOAT, "z", 0f)));
		nani.add(new Translator("translator", new Setting(Type.FLOAT, "x", 0f), new Setting(Type.FLOAT, "y", 0f), new Setting(Type.FLOAT, "z", 0f),
			new Setting(Type.FLOAT, "x_min",-1f), new Setting(Type.FLOAT, "y_min", -1f), new Setting(Type.FLOAT, "z_min", -1f),
			new Setting(Type.FLOAT, "x_max", 1f), new Setting(Type.FLOAT, "y_max", 1f), new Setting(Type.FLOAT, "z_max", 1f),
			new Setting(Type.BOOLEAN, "loop", true)));
	}
	
	public static abstract class Animation {
		
		public final String id;
		public final List<Setting> settings;
		
		public Animation(String id, Setting... settings){
			this.id = id; this.settings = Arrays.asList(settings);
		}
		
		public abstract void pre(TurboList list);
		public abstract void post(TurboList list);
		protected abstract Animation COPY(String id, Setting[] settings);
		
		public Animation copy(){
			Setting[] settings = new Setting[this.settings.size()];
			for(int i = 0; i < settings.length; i++) settings[i] = this.settings.get(i).copy();
			return this.COPY(id, settings);
		}
		
		public Setting get(String id, Setting[] list){
			for(Setting setting : list) if(setting.getId().equals(id)) return setting; return null;
		}
		
	}
	
	public static Set<Animation> get(){
		return nani;
	}
	
	public static Animation get(String string){
		for(Animation ani : nani) if(ani.id.equals(string)) return ani; return null;
	}
	
	public static class Rotator extends Animation {
		
		private Setting x, y, z;

		public Rotator(String id, Setting... settings){
			super(id, settings); x = get("x", settings); y = get("y", settings); z = get("z", settings);
		}

		@Override
		public void pre(TurboList list){
			for(PolygonWrapper wrap : list){
				wrap.addPosRot(false, x.getValue(), y.getValue(), z.getValue());
			}
		}

		@Override
		public void post(TurboList list){
			//
		}

		@Override
		protected Animation COPY(String id, Setting[] settings){
			return new Rotator(id, settings);
		}
		
	}
	
	public static class Translator extends Animation {
		
		private Setting x, y, z, x_max, y_max, z_max, x_min, y_min, z_min, loop;
		private int xdir = 1, ydir = 1, zdir = 1; private float xpass, ypass, zpass;

		public Translator(String id, Setting... settings){
			super(id, settings);
			x = get("x", settings); y = get("y", settings); z = get("z", settings);
			x_min = get("x_min", settings); y_min = get("y_min", settings); z_min = get("z_min", settings);
			x_max = get("x_max", settings); y_max = get("y_max", settings); z_max = get("z_max", settings);
			loop = get("loop", settings);
		}

		@Override
		public void pre(TurboList list){
			xpass += xdir * x.getFloatValue(); ypass += ydir * y.getFloatValue(); zpass += zdir * z.getFloatValue();
			//
			if(xpass > x_max.getFloatValue()){ xpass = x_max.getFloatValue(); if(loop.getBooleanValue()) xdir = -xdir; }
			if(xpass < x_min.getFloatValue()){ xpass = x_min.getFloatValue(); if(loop.getBooleanValue()) xdir = -xdir; }
			//
			if(ypass > y_max.getFloatValue()){ ypass = y_max.getFloatValue(); if(loop.getBooleanValue()) ydir = -ydir; }
			if(ypass < y_min.getFloatValue()){ ypass = y_min.getFloatValue(); if(loop.getBooleanValue()) ydir = -ydir; }
			//
			if(zpass > z_max.getFloatValue()){ zpass = z_max.getFloatValue(); if(loop.getBooleanValue()) zdir = -zdir; }
			if(zpass < z_min.getFloatValue()){ zpass = z_min.getFloatValue(); if(loop.getBooleanValue()) zdir = -zdir; }
			//
			for(PolygonWrapper wrap : list){ wrap.addPosRot(true, xpass, ypass, zpass); }
		}

		@Override
		public void post(TurboList list){
			for(PolygonWrapper wrap : list){ wrap.addPosRot(true, -xpass, -ypass, -zpass); }
		}

		@Override
		protected Animation COPY(String id, Setting[] settings){
			return new Translator(id, settings);
		}
		
	}

}
