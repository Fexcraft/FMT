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
			super(id, settings); x = settings[0]; y = settings[1]; z = settings[2];
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

}
