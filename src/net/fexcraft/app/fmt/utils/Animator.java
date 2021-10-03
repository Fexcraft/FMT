package net.fexcraft.app.fmt.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.tree.SubTreeGroup;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.Setting.Type;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.SwivelPointLite;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class Animator {
	
	public static final ArrayList<Animation> nani = new ArrayList<>();
	static {
		nani.add(new Title("# SELECT #", null, null));
		nani.add(new Title("# Multi Purpose", null, null));
		nani.add(new Rotator("rotator", null, Arrays.asList(
			new Setting(Type.FLOAT, "x", 0f), new Setting(Type.FLOAT, "y", 0f), new Setting(Type.FLOAT, "z", 0f),
			new Setting(Type.FLOAT, "x_min", -360f), new Setting(Type.FLOAT, "y_min", -360f), new Setting(Type.FLOAT, "z_min", -360f),
			new Setting(Type.FLOAT, "x_max", 360f), new Setting(Type.FLOAT, "y_max", 360f), new Setting(Type.FLOAT, "z_max", 360f),
			new Setting(Type.BOOLEAN, "loop", true), new Setting(Type.BOOLEAN, "opposite_on_end", false),
			new Setting(Type.STRING, "fvtm:attr", ""), new Setting(Type.BOOLEAN, "fvtm:boolean_type_attr", true)
		)));
		nani.add(new Translator("translator", null, Arrays.asList(
			new Setting(Type.FLOAT, "x", 0f), new Setting(Type.FLOAT, "y", 0f), new Setting(Type.FLOAT, "z", 0f),
			new Setting(Type.FLOAT, "x_min",-1f), new Setting(Type.FLOAT, "y_min", -1f), new Setting(Type.FLOAT, "z_min", -1f),
			new Setting(Type.FLOAT, "x_max", 1f), new Setting(Type.FLOAT, "y_max", 1f), new Setting(Type.FLOAT, "z_max", 1f),
			new Setting(Type.BOOLEAN, "loop", true), new Setting(Type.BOOLEAN, "opposite_on_end", false),
			new Setting(Type.STRING, "fvtm:attr", ""), new Setting(Type.BOOLEAN, "fvtm:boolean_type_attr", true)
		)));
		nani.add(new Custom("custom", null, Arrays.asList(new Setting(Type.STRING, "fvtm:prog", "enter here program id and parameters (if required)") )));
		nani.add(new Title("# Pivot Aligner", null, null));
		nani.add(new ZVerHyd("zverhyd", null, Arrays.asList(
				new Setting(Type.FLOAT, "pos0_x", 0f), new Setting(Type.FLOAT, "pos0_y", 0f), new Setting(Type.FLOAT, "pos0_z", 0f), new Setting(Type.STRING, "pivot0", ""), new Setting(Type.FLOAT, "rot_z", 90f),
				new Setting(Type.FLOAT, "pos1_x", 0f), new Setting(Type.FLOAT, "pos1_y", 0f), new Setting(Type.FLOAT, "pos1_z", 0f), new Setting(Type.STRING, "pivot1", ""), new Setting(Type.STRING, "pivot2", "")
		)));
		nani.add(new Title("# Generic FVTM", null, null));
		nani.add(new Generic("fvtm:rgb_primary", "DefaultPrograms.RGB_PRIMARY", null, null));
		nani.add(new Generic("fvtm:rgb_primary", "DefaultPrograms.RGB_PRIMARY", null, null));
		nani.add(new Generic("fvtm:rgb_secondary", "DefaultPrograms.RGB_SECONDARY", null, null));
		nani.add(new Generic("fvtm:glow", "DefaultPrograms.ALWAYS_GLOW", null, null));
		nani.add(new Generic("fvtm:lights", "DefaultPrograms.LIGHTS", null, null));
		nani.add(new Generic("fvtm:front_lights", "DefaultPrograms.FRONT_LIGHTS", null, null));
		nani.add(new Generic("fvtm:back_lights", "DefaultPrograms.BACK_LIGHTS", null, null));
		nani.add(new Generic("fvtm:fog_lights", "DefaultPrograms.FOG_LIGHTS", null, null));
		nani.add(new Generic("fvtm:reverse_lights", "DefaultPrograms.REVERSE_LIGHTS", null, null));
		nani.add(new Generic("fvtm:turn_signal_left", "DefaultPrograms.TURN_SIGNAL_LEFT", null, null));
		nani.add(new Generic("fvtm:turn_signal_right", "DefaultPrograms.TURN_SIGNAL_RIGHT", null, null));
		nani.add(new Generic("fvtm:warning_lights", "DefaultPrograms.WARNING_LIGHTS", null, null));
		nani.add(new Generic("fvtm:back_lights_signal_left", "DefaultPrograms.BACK_LIGHTS_SIGNAL_LEFT", null, null));
		nani.add(new Generic("fvtm:back_lights_signal_right", "DefaultPrograms.BACK_LIGHTS_SIGNAL_RIGHT", null, null));
		nani.add(new Generic("fvtm:transparent", "DefaultPrograms.TRANSPARENT", null, null));
		nani.add(new Generic("fvtm:wheel_auto_all", "DefaultPrograms.WHEEL_AUTO_ALL", null, null));
		nani.add(new Generic("fvtm:wheel_auto_steering", "DefaultPrograms.WHEEL_AUTO_ALL", null, null));
		nani.add(new Generic("fvtm:no_cullface", "DefaultPrograms.NO_CULLFACE", null, null));
		nani.add(new Window(null));
		nani.add(new Title("# FVTM Trains", null, null));
		nani.add(new Generic("fvtm:lights_front_forward", "DefaultPrograms.LIGHTS_FRONT_FORWARD", null, null));
		nani.add(new Generic("fvtm:lights_front_backward", "DefaultPrograms.LIGHTS_FRONT_BACKWARD", null, null));
		nani.add(new Generic("fvtm:lights_rear_forward", "DefaultPrograms.LIGHTS_REAR_FORWARD", null, null));
		nani.add(new Generic("fvtm:lights_rear_backward", "DefaultPrograms.LIGHTS_REAR_BACKWARD", null, null));
		nani.add(new Generic("fvtm:bogie_auto", "DefaultPrograms.BOGIE_AUTO", null, null));
		//nani.add(new Transparency("glass", new Setting(Type.RGB, "color", RGB.BLUE)));
	}
	
	public static abstract class Animation {

		public boolean active = true;
		public final String id;
		public final TreeMap<String, Setting> settings;
		public final SubTreeGroup button;
		public final TurboList group;
		
		public Animation(String id, TurboList group, Collection<Setting> settings){
			this.id = id; this.settings = new TreeMap<>(); this.group = group;
			for(Setting setting : settings) this.settings.put(setting.getId(), setting.copy());
			button = new SubTreeGroup(Trees.fvtm, this);
		}
		
		public abstract void pre(TurboList list);
		public abstract void post(TurboList list);
		protected abstract Animation COPY(String id, TurboList group, Collection<Setting> settings);
		public void onSettingsUpdate(){}
		public abstract String getButtonString();
		public abstract String getExportString(String modto);
		
		public Animation copy(TurboList group){
			return this.COPY(id, group, settings.values());
		}
		
		public Setting get(String id){
			return settings.get(id);
		}
		
		@Override
		public String toString(){
			return id;
		}
		
	}
	
	public static ArrayList<Animation> get(){
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

		@Override
		public String getExportString(String modto){
			if(!modto.equals("fvtm") && !modto.equals("fmf") && !modto.equals("obj")) return "invalid_mod";
			String string = "new DefaultPrograms.AttributeRotator(\"%s\", %s, %sf, %sf, %sf, %s, %sf)";
			int axis = x.directFloat() != 0f ? 0 : y.directFloat() != 0f ? 1 : z.directFloat() != 0f ? 2 : 3;
			float min = 0, max = 0, step = 0, defrot = 0;
			switch(axis){
				case 0:{
					min = get("x_min").directFloat(); max = get("x_max").directFloat(); step = get("x").directFloat();
					defrot = group.get(0).getTurboObject(0).rotationAngleX;
					break;
				}
				case 1:{
					min = get("y_min").directFloat(); max = get("y_max").directFloat(); step = get("y").directFloat();
					defrot = group.get(0).getTurboObject(0).rotationAngleY;
					break;
				}
				case 2:{
					min = get("z_min").directFloat(); max = get("z_max").directFloat(); step = get("z").directFloat();
					defrot = group.get(0).getTurboObject(0).rotationAngleZ;
					break;
				}
				default: return "\"Could not find applicable axis.\"";
			}
			if(modto.equals("fmf") || modto.equals("obj")){
				String attr = get("fvtm:attr").getStringValue();
				String export = group.id + " ";
				export += "fvtm:attribute_rotator ";
				export += (attr.length() == 0 ? "null" : attr) + " ";
				export += get("fvtm:boolean_type_attr") + " ";
				export += min + " " + max + " " + step + " " + axis + " " + defrot;
				return export;
			}
			else return String.format(string, get("fvtm:attr"), get("fvtm:boolean_type_attr"), min, max, step, axis, defrot);
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

		@Override
		public String getExportString(String modto){
			if(!modto.equals("fvtm") && !modto.equals("fmf") && !modto.equals("obj")) return "invalid_mod";
			String string = "new DefaultPrograms.AttributeTranslator(\"%s\", %s, %sf, %sf, %sf, %s, %sf)";
			int axis = x.directFloat() != 0f ? 0 : y.directFloat() != 0f ? 1 : z.directFloat() != 0f ? 2 : 3;
			float min = 0, max = 0, step = 0;
			switch(axis){
				case 0:{
					min = get("x_min").directFloat(); max = get("x_max").directFloat(); step = get("x").directFloat();
					break;
				}
				case 1:{
					min = get("y_min").directFloat(); max = get("y_max").directFloat(); step = get("y").directFloat();
					break;
				}
				case 2:{
					min = get("z_min").directFloat(); max = get("z_max").directFloat(); step = get("z").directFloat();
					break;
				}
				default: return "\"Could not find applicable axis.\"";
			}
			if(modto.equals("fmf") || modto.equals("obj")){
				String attr = get("fvtm:attr").getStringValue();
				String export = group.id + " ";
				export += "fvtm:attribute_translator ";
				export += (attr.length() == 0 ? "null" : attr) + " ";
				export += get("fvtm:boolean_type_attr") + " ";
				export += min + " " + max + " " + step + " " + axis;
				return export;
			}
			else return String.format(string, get("fvtm:attr"), get("fvtm:boolean_type_attr"), min, max, step, axis);
		}
		
	}
	
	public static class Custom extends Animation {
		
		private Setting custom;

		public Custom(String id, TurboList group, Collection<Setting> settings){
			super(id, group, settings);
			custom = get("fvtm:prog");
		}

		@Override
		public void pre(TurboList list){}

		@Override
		public void post(TurboList list){}

		@Override
		protected Animation COPY(String id, TurboList group, Collection<Setting> settings){
			return new Custom(id, group, settings);
		}

		@Override
		public String getButtonString(){
			int l = custom.toString().length();
			return l == 0 ? "Custom Program Data" : "CPD: " + (l > 20 ? custom.toString().substring(0, 20) : custom.toString());
		}

		@Override
		public String getExportString(String modto){
			if(!modto.equals("fvtm") && !modto.equals("fmf") && !modto.equals("obj")) return "invalid_mod";
			return group.id + " " + get("fvtm:prog").toString();
		}
		
	}
	
	public static class Generic extends Animation {
		
		private String fvtmid;

		public Generic(String id, String fvtmid, TurboList group, Collection<Setting> settings){
			super(id, group, settings == null ? new ArrayList<Setting>() : settings); this.fvtmid = fvtmid;
		}

		@Override
		public void pre(TurboList list){
			//
		}

		@Override
		public void post(TurboList list){
			//
		}

		@Override
		protected Animation COPY(String id, TurboList group, Collection<Setting> settings){
			return new Generic(id, fvtmid, group, settings);
		}

		@Override
		public String getButtonString(){
			return id;
		}

		@Override
		public String getExportString(String modto){
			if(modto.equals("fvtm")) return fvtmid;
			if(modto.equals("fmf") || modto.equals("obj")) return id;
			return "null";
		}
		
	}
	
	public static class Window extends Generic {

		public Window(TurboList group){
			super("fvtm:window", null, group, Arrays.asList(new Setting("color", "default")));
		}

		@Override
		public String getButtonString(){
			return id + " - " + get("color");
		}

		@Override
		protected Animation COPY(String id, TurboList group, Collection<Setting> settings){
			return new Window(group);
		}

		@Override
		public String getExportString(String modto){
			String color = get("color").getStringValue().replace("#", "");
			if(color == null || color.equals("default") || color.equals("null")){
				return "DefaultPrograms.WINDOW";
			}//eventually validate if it's an integer?
			else return "new DefaultPrograms.Window(0x" + color + ")";
		}
		
	}
	
	public static class Title extends Animation {

		public Title(String id, TurboList group, Collection<Setting> settings){
			super(id, group, settings == null ? new ArrayList<Setting>() : settings);
		}

		@Override
		public void pre(TurboList list){
			//
		}

		@Override
		public void post(TurboList list){
			//
		}

		@Override
		protected Animation COPY(String id, TurboList group, Collection<Setting> settings){
			return new Title(id, group, settings);
		}

		@Override
		public String getButtonString(){
			return "invalid/title";
		}

		@Override
		public String getExportString(String modto){
			return "";
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
	

	
	public static class ZVerHyd extends Animation {
		
		public static final ModelRendererTurbo sp0 = new ModelRendererTurbo(null).addSphere(0, 0, 0, 2, 8, 8, 1, 1).setColor(RGB.BLUE).setTextured(false);
		public static final ModelRendererTurbo sp1 = new ModelRendererTurbo(null).addSphere(0, 0, 0, 2, 8, 8, 1, 1).setColor(RGB.GREEN).setTextured(false);
		public static final ModelRendererTurbo sp2 = new ModelRendererTurbo(null).addSphere(0, 0, 0, 0.2f, 8, 8, 1, 1).setColor(RGB.RED).setTextured(false);
		public static final ModelRendererTurbo sp = new ModelRendererTurbo(null).addSphere(0, 0, 0, 0.25f, 8, 8, 1, 1).setColor(RGB.RED).setTextured(false);
		private Setting x0, y0, z0, x1, y1, z1, pivot0, pivot1, pivot2, rz;
		private float angle;

		public ZVerHyd(String id, TurboList group, Collection<Setting> settings){
			super(id, group, settings);
			x0 = get("pos0_x");
			y0 = get("pos0_y");
			z0 = get("pos0_z");
			x1 = get("pos1_x");
			y1 = get("pos1_y");
			z1 = get("pos1_z");
			pivot0 = get("pivot0");
			pivot1 = get("pivot1");
			pivot2 = get("pivot2");
			rz = get("rot_z");
		}

		@Override
		public void pre(TurboList list){
			SwivelPointLite o0 = FMTB.MODEL.pivots.get(pivot0.getStringValue());
			SwivelPointLite o1 = FMTB.MODEL.pivots.get(pivot1.getStringValue());
			SwivelPointLite o2 = FMTB.MODEL.pivots.get(pivot2.getStringValue());
			if(o0 == null || o1 == null) return;
			Vec3f vec1 = o0.getRelVec(x0.directFloat(), y0.directFloat(), z0.directFloat());
			Vec3f vec0 = o1.getRelVec(x1.directFloat(), y1.directFloat(), z1.directFloat());
			if(Settings.pivot_polygon_markers.getBooleanValue()){
				FMTB.MODEL.markers.add(vec0);
				FMTB.MODEL.markers.add(vec1);
				float l = vec0.dis(vec1);
				for(float x = 0.2f; x < l; x += 0.4){
					FMTB.MODEL.markers.add(vec0.distance(vec1, x));
				}
			}
	        double dx = vec0.x - vec1.x, dy = vec0.y - vec1.y;
			angle = Static.toDegrees((float)Math.atan2(dy, dx)) + rz.getFloatValue();
			SwivelPointLite r = o2;
			while(r != null){
				angle -= r.rot.z;
				r = r.root;
			}
			GL11.glRotatef(angle, 0, 0, 1);
			RGB.glColorReset();
		}

		@Override
		public void post(TurboList list){
			GL11.glRotatef(-angle, 0, 0, 1);
		}

		@Override
		protected Animation COPY(String id, TurboList group, Collection<Setting> settings){
			return new ZVerHyd(id, group, settings);
		}

		@Override
		public String getButtonString(){
			return "zverhyd";
		}

		@Override
		public String getExportString(String modto){
			return "non-exportable";
		}
		
	}

}
