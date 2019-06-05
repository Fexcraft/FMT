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
		nani.add(new Rotator("rotator", new Setting(Type.FLOAT, "x", 0f), new Setting(Type.FLOAT, "y", 0f), new Setting(Type.FLOAT, "z", 0f),
			new Setting(Type.FLOAT, "x_min", -360f), new Setting(Type.FLOAT, "y_min", -360f), new Setting(Type.FLOAT, "z_min", -360f),
			new Setting(Type.FLOAT, "x_max", 360f), new Setting(Type.FLOAT, "y_max", 360f), new Setting(Type.FLOAT, "z_max", 360f),
			new Setting(Type.BOOLEAN, "loop", true), new Setting(Type.BOOLEAN, "opposite_on_end", false)));
		nani.add(new Translator("translator", new Setting(Type.FLOAT, "x", 0f), new Setting(Type.FLOAT, "y", 0f), new Setting(Type.FLOAT, "z", 0f),
			new Setting(Type.FLOAT, "x_min",-1f), new Setting(Type.FLOAT, "y_min", -1f), new Setting(Type.FLOAT, "z_min", -1f),
			new Setting(Type.FLOAT, "x_max", 1f), new Setting(Type.FLOAT, "y_max", 1f), new Setting(Type.FLOAT, "z_max", 1f),
			new Setting(Type.BOOLEAN, "loop", true), new Setting(Type.BOOLEAN, "opposite_on_end", false)));
		//nani.add(new Transparency("glass", new Setting(Type.RGB, "color", RGB.BLUE)));
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
		public void onSettingsUpdate(){}
		
		public Animation copy(){
			Setting[] settings = new Setting[this.settings.size()];
			for(int i = 0; i < settings.length; i++) settings[i] = this.settings.get(i).copy();
			return this.COPY(id, settings);
		}
		
		public Setting get(String id, Setting[] list){
			for(Setting setting : list) if(setting.getId().equals(id)) return setting; return null;
		}
		
		public Setting get(String id, Iterable<Setting> list){
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
		
		private Setting x, y, z, x_max, y_max, z_max, x_min, y_min, z_min, loop, ooe;
		private int xdir = 1, ydir = 1, zdir = 1; private float xpass, ypass, zpass;

		public Rotator(String id, Setting... settings){
			super(id, settings); x = get("x", settings); y = get("y", settings); z = get("z", settings);
			x_min = get("x_min", settings); y_min = get("y_min", settings); z_min = get("z_min", settings);
			x_max = get("x_max", settings); y_max = get("y_max", settings); z_max = get("z_max", settings);
			loop = get("loop", settings); ooe = get("opposite_on_end", settings);
		}

		@Override
		public void pre(TurboList list){
			xpass += xdir * x.getFloatValue(); ypass += ydir * y.getFloatValue(); zpass += zdir * z.getFloatValue();
			//
			if(xpass > x_max.getFloatValue()){
				xpass = x_max.getFloatValue(); if(ooe.getBooleanValue()) xdir = -xdir;
				if(loop.getBooleanValue()) xpass = x_min.getFloatValue();
			}
			if(xpass < x_min.getFloatValue()){
				xpass = x_min.getFloatValue(); if(ooe.getBooleanValue()) xdir = -xdir;
				if(loop.getBooleanValue()) xpass = x_max.getFloatValue();
			}
			//
			if(ypass > y_max.getFloatValue()){
				ypass = y_max.getFloatValue(); if(ooe.getBooleanValue()) ydir = -ydir;
				if(loop.getBooleanValue()) ypass = y_min.getFloatValue();
			}
			if(ypass < y_min.getFloatValue()){
				ypass = y_min.getFloatValue(); if(ooe.getBooleanValue()) ydir = -ydir;
				if(loop.getBooleanValue()) ypass = y_max.getFloatValue();
			}
			//
			if(zpass > z_max.getFloatValue()){
				zpass = z_max.getFloatValue(); if(ooe.getBooleanValue()) zdir = -zdir;
				if(loop.getBooleanValue()) zpass = z_min.getFloatValue();
			}
			if(zpass < z_min.getFloatValue()){
				zpass = z_min.getFloatValue(); if(ooe.getBooleanValue()) zdir = -zdir;
				if(loop.getBooleanValue()) zpass = z_max.getFloatValue();
			}
			//
			for(PolygonWrapper wrap : list){ wrap.addPosRot(false, xpass, ypass, zpass); }
		}

		@Override
		public void post(TurboList list){
			for(PolygonWrapper wrap : list){ wrap.addPosRot(false, -xpass, -ypass, -zpass); }
		}

		@Override
		protected Animation COPY(String id, Setting[] settings){
			return new Rotator(id, settings);
		}
		
	}
	
	public static class Translator extends Animation {
		
		private Setting x, y, z, x_max, y_max, z_max, x_min, y_min, z_min, loop, ooe;
		private int xdir = 1, ydir = 1, zdir = 1; private float xpass, ypass, zpass;

		public Translator(String id, Setting... settings){
			super(id, settings); x = get("x", settings); y = get("y", settings); z = get("z", settings);
			x_min = get("x_min", settings); y_min = get("y_min", settings); z_min = get("z_min", settings);
			x_max = get("x_max", settings); y_max = get("y_max", settings); z_max = get("z_max", settings);
			loop = get("loop", settings); ooe = get("opposite_on_end", settings);
		}

		@Override
		public void pre(TurboList list){
			xpass += xdir * x.getFloatValue(); ypass += ydir * y.getFloatValue(); zpass += zdir * z.getFloatValue();
			//
			if(xpass > x_max.getFloatValue()){
				xpass = x_max.getFloatValue(); if(ooe.getBooleanValue()) xdir = -xdir;
				if(loop.getBooleanValue()) xpass = x_min.getFloatValue();
			}
			if(xpass < x_min.getFloatValue()){
				xpass = x_min.getFloatValue(); if(ooe.getBooleanValue()) xdir = -xdir;
				if(loop.getBooleanValue()) xpass = x_max.getFloatValue();
			}
			//
			if(ypass > y_max.getFloatValue()){
				ypass = y_max.getFloatValue(); if(ooe.getBooleanValue()) ydir = -ydir;
				if(loop.getBooleanValue()) ypass = y_min.getFloatValue();
			}
			if(ypass < y_min.getFloatValue()){
				ypass = y_min.getFloatValue(); if(ooe.getBooleanValue()) ydir = -ydir;
				if(loop.getBooleanValue()) ypass = y_max.getFloatValue();
			}
			//
			if(zpass > z_max.getFloatValue()){
				zpass = z_max.getFloatValue(); if(ooe.getBooleanValue()) zdir = -zdir;
				if(loop.getBooleanValue()) zpass = z_min.getFloatValue();
			}
			if(zpass < z_min.getFloatValue()){
				zpass = z_min.getFloatValue(); if(ooe.getBooleanValue()) zdir = -zdir;
				if(loop.getBooleanValue()) zpass = z_max.getFloatValue();
			}
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
	
	/*public static class Transparency extends Animation {
		
		private RGB color;

		public Transparency(String id, Setting... settings){
			super(id, settings); this.onSettingsUpdate();
		}

		@Override
		public void pre(TurboList list){
            GL11.glPushMatrix();
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            color.glColorApply();
		}

		@Override
		public void post(TurboList list){
			RGB.glColorReset();
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
		}

		@Override
		protected Animation COPY(String id, Setting[] settings){
			return new Transparency(id, settings);
		}
		
		@Override
		public void onSettingsUpdate(){
			color = get("color", settings).getValue(); color.alpha = 0.2f;
		}
		
	}*/

}
