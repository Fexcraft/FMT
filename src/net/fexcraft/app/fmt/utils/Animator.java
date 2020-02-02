package net.fexcraft.app.fmt.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import net.fexcraft.app.fmt.ui.tree.FVTMTree;
import net.fexcraft.app.fmt.ui.tree.RightTree.AnimationButton;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class Animator {
	
	public static final HashSet<Animation> nani = new HashSet<>();
	static {
		nani.add(new Rotator("rotator", null, Arrays.asList(
			new Setting(Type.FLOAT, "x", 0f), new Setting(Type.FLOAT, "y", 0f), new Setting(Type.FLOAT, "z", 0f),
			new Setting(Type.FLOAT, "x_min", -360f), new Setting(Type.FLOAT, "y_min", -360f), new Setting(Type.FLOAT, "z_min", -360f),
			new Setting(Type.FLOAT, "x_max", 360f), new Setting(Type.FLOAT, "y_max", 360f), new Setting(Type.FLOAT, "z_max", 360f),
			new Setting(Type.BOOLEAN, "loop", true), new Setting(Type.BOOLEAN, "opposite_on_end", false),
			new Setting(Type.STRING, "fvtm:attr", "")
		)));
		nani.add(new Translator("translator", null, Arrays.asList(
			new Setting(Type.FLOAT, "x", 0f), new Setting(Type.FLOAT, "y", 0f), new Setting(Type.FLOAT, "z", 0f),
			new Setting(Type.FLOAT, "x_min",-1f), new Setting(Type.FLOAT, "y_min", -1f), new Setting(Type.FLOAT, "z_min", -1f),
			new Setting(Type.FLOAT, "x_max", 1f), new Setting(Type.FLOAT, "y_max", 1f), new Setting(Type.FLOAT, "z_max", 1f),
			new Setting(Type.BOOLEAN, "loop", true), new Setting(Type.BOOLEAN, "opposite_on_end", false),
			new Setting(Type.STRING, "fvtm:attr", "")
		)));
		//nani.add(new Transparency("glass", new Setting(Type.RGB, "color", RGB.BLUE)));
	}
	
	public static abstract class Animation {

		public boolean active = true;
		public final String id;
		public final TreeMap<String, Setting> settings;
		public final AnimationButton button;
		public final TurboList group;
		
		public Animation(String id, TurboList group, Collection<Setting> settings){
			this.id = id; this.settings = new TreeMap<>(); this.group = group;
			for(Setting setting : settings) this.settings.put(setting.getId(), setting.copy());
			button = new AnimationButton(FVTMTree.TREE, this);
		}
		
		public abstract void pre(TurboList list);
		public abstract void post(TurboList list);
		protected abstract Animation COPY(String id, TurboList group, Collection<Setting> settings);
		public void onSettingsUpdate(){}
		public abstract String getButtonString();
		
		public Animation copy(TurboList group){
			return this.COPY(id, group, settings.values());
		}
		
		public Setting get(String id){
			return settings.get(id);
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

		public Rotator(String id, TurboList group, Collection<Setting> settings){
			super(id, group, settings); x = get("x"); y = get("y"); z = get("z");
			x_min = get("x_min"); y_min = get("y_min"); z_min = get("z_min");
			x_max = get("x_max"); y_max = get("y_max"); z_max = get("z_max");
			loop = get("loop"); ooe = get("opposite_on_end");
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
		protected Animation COPY(String id, TurboList group, Collection<Setting> settings){
			return new Rotator(id, group, settings);
		}

		@Override
		public String getButtonString(){
			return settings.get("fvtm:attr").getStringValue().length() == 0 ? "rotator" : "ROT: " + settings.get("fvtm:attr");
		}
		
	}
	
	public static class Translator extends Animation {
		
		private Setting x, y, z, x_max, y_max, z_max, x_min, y_min, z_min, loop, ooe;
		private int xdir = 1, ydir = 1, zdir = 1; private float xpass, ypass, zpass;

		public Translator(String id, TurboList group, Collection<Setting> settings){
			super(id, group, settings); x = get("x"); y = get("y"); z = get("z");
			x_min = get("x_min"); y_min = get("y_min"); z_min = get("z_min");
			x_max = get("x_max"); y_max = get("y_max"); z_max = get("z_max");
			loop = get("loop"); ooe = get("opposite_on_end");
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
		protected Animation COPY(String id, TurboList group, Collection<Setting> settings){
			return new Translator(id, group, settings);
		}

		@Override
		public String getButtonString(){
			return settings.get("fvtm:attr").getStringValue().length() == 0 ? "translator" : "TRS: " + settings.get("fvtm:attr");
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
