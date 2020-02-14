package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.ArrayList;

import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent;
import org.liquidengine.legui.component.event.slider.SliderChangeValueEventListener;
import org.liquidengine.legui.component.misc.listener.scrollablepanel.ScrollablePanelViewportScrollListener;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.ScrollEvent;
import org.liquidengine.legui.event.WindowSizeEvent;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.style.Background;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.color.ColorConstants;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterpanels.BoolButton;
import net.fexcraft.app.fmt.ui.UserInterpanels.Button20;
import net.fexcraft.app.fmt.ui.UserInterpanels.ColorInput20;
import net.fexcraft.app.fmt.ui.UserInterpanels.Dialog20;
import net.fexcraft.app.fmt.ui.UserInterpanels.Label20;
import net.fexcraft.app.fmt.ui.UserInterpanels.NumberInput20;
import net.fexcraft.app.fmt.ui.UserInterpanels.TextInput20;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.utils.TextureUpdate;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeType;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.common.utils.Print;

public class Editors {
	
	public static GeneralEditor general;
	public static ModelGroupEditor modelgroup;
	//
	public static final ArrayList<EditorBase> editors = new ArrayList<>();
	public static String NO_POLYGON_SELECTED;

	public static void initializeEditors(Frame frame){
		frame.getContainer().add(general = new GeneralEditor());
		frame.getContainer().add(modelgroup = new ModelGroupEditor());
		//temporary
		//general.show();
	}
	
	public static void hideAll(){
		for(EditorBase editor : editors) editor.hide();
	}
	
	public static void show(String type){
		hideAll();
		switch(type){
			case "general": general.show(); break;
			case "model": case "group":
			case "modelgroup": modelgroup.show(); break;
		}
	}
	
	public static boolean anyVisible(){
		for(EditorBase editor : editors) if(editor.isVisible()) return true; return false;
	}
	
	public static EditorBase getVisible(){
		for(EditorBase editor : editors) if(editor.isVisible()) return editor; return null;
	}
	
	public static class EditorBase extends Panel {

		protected ArrayList<EditorWidget> widgets = new ArrayList<>();
		protected ScrollablePanel scrollable;
		
		public EditorBase(){
			super(0, 30, 304, FMTB.HEIGHT - 30); editors.add(this);
			this.getListenerMap().addListener(WindowSizeEvent.class, event -> {
				this.setSize(304, event.getHeight() - 30);
				scrollable.setSize(304, event.getHeight() - 80);
				scrollable.getContainer().setSize(296, event.getHeight() - 88);
			});
			String[] arr = new String[]{ "normal", "sixteenth", "decimal"}; int off = 0;
			Label label = new Label(translate("editor.multiplicator"), 4, 4, 100, 24);
			super.add(label); label.getTextState().setFontSize(20); int am = 0;
			Label current = new Label(format("editor.multiplicator.current", 1f), 4, 28, 100, 24);
			super.add(current); current.getTextState().setFontSize(20);
			for(String string : arr){
				Slider multislider = new Slider(148, 4 + off, 150, 14);
				switch(string){
					case "normal":{
						multislider.setMinValue(0); multislider.setMaxValue(64);
						multislider.setStepSize(1); multislider.setValue(1f); am = 0;
						break;
					}
					case "sixteenth":{
						multislider.setMinValue(0); multislider.setMaxValue(1); am = 4;
						multislider.setStepSize(Static.sixteenth); multislider.setValue(1f);
						break;
					}
					case "decimal":{
						multislider.setMinValue(0); multislider.setMaxValue(1); am = 1;
						multislider.setStepSize(0.1f); multislider.setValue(1f);
						break;
					}
				}
		        final Tooltip multitip = new Tooltip();
		        multitip.setSize(100, 20); multitip.getTextState().setFontSize(20);
		        multitip.setPosition(multislider.getSize().x + 2, 0); final String amo = "%." + am + "f";
		        multitip.getTextState().setText(translate("editor.multiplicator.value") + String.format(amo, multislider.getValue()));
		        multislider.addSliderChangeValueEventListener((SliderChangeValueEventListener) event -> {
		            multitip.getTextState().setText(translate("editor.multiplicator.value") + String.format(amo, event.getNewValue()));
		            current.getTextState().setText(format("editor.multiplicator.current", event.getNewValue()));
		            multitip.setSize(100, 28); FMTB.MODEL.rate = event.getNewValue();
		        });
		        multislider.setTooltip(multitip);
		        super.add(multislider); off += 16;
			}
	        scrollable = new ScrollablePanel(0, 54, 304, FMTB.HEIGHT - 80);
	        scrollable.getStyle().getBackground().setColor(1, 1, 1, 1);
	        scrollable.setHorizontalScrollBarVisible(false);
	        scrollable.getContainer().setSize(296, FMTB.HEIGHT - 80);
	        scrollable.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
	        scrollable.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL());
	        super.add(scrollable); this.hide();
		}

		public void toggle(){
			if(isVisible()) hide(); else show();
		}
		
		public void hide(){
			this.getStyle().setDisplay(DisplayType.NONE);
		}
		
		public void show(){
			this.getStyle().setDisplay(DisplayType.MANUAL);
		}
		
		public boolean addSub(Component com){
			if(com instanceof EditorWidget) widgets.add((EditorWidget)com);
			return scrollable.getContainer().add(com);
		}

		protected void reOrderWidgets(){
			float size = 0; for(EditorWidget widget : widgets) size += widget.getSize().y + 2;
			scrollable.getContainer().setSize(scrollable.getSize().x, size > FMTB.HEIGHT - 80 ? size : FMTB.HEIGHT - 80); size = 0;
			for(EditorWidget widget : widgets){ widget.setPosition(0, size); size += widget.getSize().y + 2; }
		}
		
	}
	
	public static class SPVSL extends ScrollablePanelViewportScrollListener {
		
	    @Override
	    public void process(@SuppressWarnings("rawtypes") ScrollEvent event){
	    	if(FMTB.field_scrolled) return; else super.process(event);
	    }
	    
	}
	
	public static class GeneralEditor extends EditorBase {
		
		public static TextInput20 polygon_name;
		public static NumberInput20 size_x, size_y, size_z;
		public static NumberInput20 pos_x, pos_y, pos_z;
		public static NumberInput20 off_x, off_y, off_z;
		public static NumberInput20 rot_x, rot_y, rot_z;
		public static NumberInput20 texture_x, texture_y;
		public static NumberInput20 cyl0_x, cyl0_y, cyl0_z;
		public static NumberInput20 cyl1_x, cyl1_y, cyl1_z;
		public static NumberInput20 cyl2_x, cyl2_y;
		public static NumberInput20 cyl3_x, cyl3_y, cyl3_z;
		public static BoolButton cyl4_x, cyl4_y, cyl5_x, cyl5_y, cyl6_x;
		public static NumberInput20 cyl6_y, cyl6_z;
		public static NumberInput20 cyl7_x, cyl7_y, cyl7_z;
		public static NumberInput20[] corner_x, corner_y, corner_z;
		public static NumberInput20[][] texrect_a = new NumberInput20[6][8], texrect_b = new NumberInput20[6][4];
		public static ColorInput20 marker_color;
		public static NumberInput20 marker_scale, marker_angle;
		public static BoolButton marker_biped;
		public static SelectBox<Object> polygon_group, polygon_type;
		
		@SuppressWarnings("unchecked")
		public GeneralEditor(){
			super(); int pass = -20;
			EditorWidget attributes = new EditorWidget(this, translate("editor.general.attributes"), 0, 0, 0, 0);
	        attributes.getContainer().add(new Label20(translate("editor.general.attributes.group"), 3, pass += 24, 290, 20));
	        attributes.getContainer().add(polygon_group = new SelectBox<>(3, pass += 24, 290, 20));
	        polygon_group.addElement("> new_group <"); polygon_group.getSelectBoxElements().get(0).getTextState().setFontSize(20f);
	        polygon_group.setVisibleCount(12); polygon_group.setElementHeight(20);
			polygon_group.getSelectionButton().getTextState().setFontSize(20f);
	        polygon_group.addSelectBoxChangeSelectionEventListener(event -> {
	        	if(event.getNewValue().toString().equals("> new_group <")){
		            Dialog20 dialog = new Dialog20(translate("editor.general.attributes.new_group.title"), 300, 120);
		            Label20 label = new Label20(translate("editor.general.attributes.new_group.desc"), 10, 10, 280, 20);
		            TextInput20 input = new TextInput20("new_group", 10, 40, 280, 20);
		            Button20 confirm = new Button20(translate("editor.general.attributes.new_group.confirm"), 10, 70, 70, 20);
	                Button20 cancel = new Button20(translate("editor.general.attributes.new_group.cancel"), 90, 70, 70, 20);
		            dialog.getContainer().add(input); dialog.getContainer().add(label);
		            dialog.getContainer().add(confirm); dialog.getContainer().add(cancel);
	                confirm.getListenerMap().addListener(MouseClickEvent.class, e -> {
	                    if(CLICK == e.getAction()){
	                    	String text = input.getTextState().getText();
	                    	if(text.equals("new_group")) text += "0";
	                    	while(FMTB.MODEL.getGroups().contains(text)) text += "_";
	                    	FMTB.MODEL.getGroups().add(new TurboList(text));
							FMTB.MODEL.changeGroupOfSelected(FMTB.MODEL.getSelected(), text);
	                        dialog.close();
	                    }
	                });
	                cancel.getListenerMap().addListener(MouseClickEvent.class, e -> { if(CLICK == e.getAction()) dialog.close(); });
		            dialog.setResizable(false); dialog.show(event.getFrame());
	        	}
	        	else{
	        		FMTB.MODEL.changeGroupOfSelected(FMTB.MODEL.getSelected(), event.getNewValue().toString());
	        	}
	        	polygon_group.setSelected(0, false);
	        });
	        attributes.getContainer().add(new Label20(translate("editor.general.attributes.name"), 3, pass += 24, 290, 20));
	        attributes.getContainer().add(polygon_name = new TextInput20(NO_POLYGON_SELECTED = translate("error.no_polygon_selected"), 3, pass += 24, 290, 20));
	        polygon_name.addTextInputContentChangeEventListener(event -> {
				String validated = UserInterpanels.validateString(event);
				if(FMTB.MODEL.getSelected().isEmpty()) return; PolygonWrapper wrapper;
				if(FMTB.MODEL.getSelected().size() == 1){
					wrapper = FMTB.MODEL.getFirstSelection();
					if(wrapper != null) wrapper.name = validated;
				}
				else{
					ArrayList<PolygonWrapper> polis = FMTB.MODEL.getSelected();
					for(int i = 0; i < polis.size(); i++){
						wrapper = polis.get(i);
						if(wrapper != null){
							String str = validated.contains("_") ? "_" + i : validated.contains("-") ? "-" + i :
								validated.contains(" ") ? " " + i : validated.contains(".") ? "." + i : i + "";
							wrapper.name = validated + str;
						}
					}
				}
	        });
	        attributes.getContainer().add(new Label20(translate("editor.general.attributes.type"), 3, pass += 24, 290, 20));
	        attributes.getContainer().add(polygon_type = new SelectBox<>(3, pass += 24, 290, 20));
	        for(ShapeType type : ShapeType.getSupportedValues()) polygon_type.addElement(type.name().toLowerCase());
	        polygon_type.getSelectBoxElements().forEach(elm -> elm.getTextState().setFontSize(20f));
	        polygon_type.setVisibleCount(12); polygon_type.setElementHeight(20);
	        polygon_type.getSelectionButton().getTextState().setFontSize(20f);
	        polygon_type.addSelectBoxChangeSelectionEventListener(event -> {
	        	FMTB.MODEL.changeTypeOfSelected(FMTB.MODEL.getSelected(), event.getNewValue().toString());
	        });
	        Button20 painttotex = new Button20(translate("editor.general.attributes.painttotexture"), 3, 8 + (pass += 24), 290, 20);
	        painttotex.getListenerMap().addListener(MouseClickEvent.class, event -> {
	        	if(event.getAction() != CLICK) return;
				if(FMTB.MODEL.texture == null){
					DialogBox.show(null, "dialogbox.button.ok", "dialogbox.button.load", null, () -> {
						UserInterpanels.SELECT_TEXTURE.run();
					}, "editor.general.attributes.painttotexture.notex");
				}
				else{
					ArrayList<PolygonWrapper> selection = FMTB.MODEL.getSelected();
					for(PolygonWrapper poly : selection){
						String texname = poly.getTurboList().getGroupTexture() == null ? FMTB.MODEL.texture : poly.getTurboList().getGroupTexture();
						Texture tex = TextureManager.getTexture(texname, true);
						if(tex == null){//TODO group tex compensation
							DialogBox.showOK(null, () -> { UserInterpanels.SELECT_TEXTURE.run(); }, null, "editor.general.attributes.painttotexture.tex_not_found");
							return;
						}
						poly.burnToTexture(tex.getImage(), null); poly.recompile(); TextureManager.saveTexture(texname); tex.rebind();
						Print.console("Polygon painted into Texture.");
					}
					return;
				}
	        });
	        attributes.getContainer().add(painttotex);
	        attributes.setSize(296, pass + 52 + 4);
	        this.addSub(attributes); pass = -20;
	        //
			EditorWidget shape = new EditorWidget(this, translate("editor.general.shape"), 0, 0, 0, 0);
			shape.getContainer().add(new Label20(translate("editor.general.shape.size"), 3, pass += 24, 290, 20));
			shape.getContainer().add(size_x = new NumberInput20(4, pass += 24, 90, 20).setup("sizex", 0, Integer.MAX_VALUE, false));
			shape.getContainer().add(size_y = new NumberInput20(102, pass, 90, 20).setup("sizey", 0, Integer.MAX_VALUE, false));
			shape.getContainer().add(size_z = new NumberInput20(200, pass, 90, 20).setup("sizez", 0, Integer.MAX_VALUE, false));
			shape.getContainer().add(new Label20(translate("editor.general.shape.position"), 3, pass += 24, 290, 20));
			shape.getContainer().add(pos_x = new NumberInput20(4, pass += 24, 90, 20).setup("posx", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
			shape.getContainer().add(pos_y = new NumberInput20(102, pass, 90, 20).setup("posy", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
			shape.getContainer().add(pos_z = new NumberInput20(200, pass, 90, 20).setup("posz", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
			shape.getContainer().add(new Label20(translate("editor.general.shape.offset"), 3, pass += 24, 290, 20));
			shape.getContainer().add(off_x = new NumberInput20(4, pass += 24, 90, 20).setup("offx", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
			shape.getContainer().add(off_y = new NumberInput20(102, pass, 90, 20).setup("offy", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
			shape.getContainer().add(off_z = new NumberInput20(200, pass, 90, 20).setup("offz", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
			shape.getContainer().add(new Label20(translate("editor.general.shape.rotation"), 3, pass += 24, 290, 20));
			shape.getContainer().add(rot_x = new NumberInput20(4, pass += 24, 90, 20).setup("rotx", -360, 360, true));
			shape.getContainer().add(rot_y = new NumberInput20(102, pass, 90, 20).setup("roty", -360, 360, true));
			shape.getContainer().add(rot_z = new NumberInput20(200, pass, 90, 20).setup("rotz", -360, 360, true));
			shape.getContainer().add(new Label20(translate("editor.general.shape.texture"), 3, pass += 24, 290, 20));
			shape.getContainer().add(texture_x = new NumberInput20(4, pass += 24, 90, 20).setup("texx", 0, 8192, true));
			shape.getContainer().add(texture_y = new NumberInput20(102, pass, 90, 20).setup("texy", 0, 8192, true));
			shape.setSize(296, pass + 52);
	        this.addSub(shape); pass = -20;
	        //
			EditorWidget shapebox = new EditorWidget(this, translate("editor.general.shapebox"), 0, 0, 0, 0);
			corner_x = new NumberInput20[8]; corner_y = new NumberInput20[8]; corner_z = new NumberInput20[8];
	        for(int i = 0; i < 8; i++){
	        	shapebox.getContainer().add(new Label20(translate("editor.general.shapebox.corner" + i), 3, pass += 24, 290, 20));
				shapebox.getContainer().add(corner_x[i] = new NumberInput20(4, pass += 24, 90, 20).setup("cor" + i + "x", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
				shapebox.getContainer().add(corner_y[i] = new NumberInput20(102, pass, 90, 20).setup("cor" + i + "y", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
				shapebox.getContainer().add(corner_z[i] = new NumberInput20(200, pass, 90, 20).setup("cor" + i + "z", Integer.MIN_VALUE, Integer.MAX_VALUE, true));
	        }
			shapebox.setSize(296, pass + 52);
	        this.addSub(shapebox); pass = -20;
	        //
			EditorWidget cylinder = new EditorWidget(this, translate("editor.general.cylinder"), 0, 0, 0, 0);
			cylinder.getContainer().add(new Label20(translate("editor.general.cylinder.radius_length"), 3, pass += 24, 290, 20));
			cylinder.getContainer().add(cyl0_x = new NumberInput20(4, pass += 24, 90, 20).setup("cyl0x", 1, Integer.MAX_VALUE, false));
			cylinder.getContainer().add(cyl0_y = new NumberInput20(102, pass, 90, 20).setup("cyl0y", 1, Integer.MAX_VALUE, false));
			cylinder.getContainer().add(cyl0_z = new NumberInput20(200, pass, 90, 20).setup("cyl0z", 0, Integer.MAX_VALUE, true));
			cylinder.getContainer().add(new Label20(translate("editor.general.cylinder.segments_direction"), 3, pass += 24, 290, 20));
			cylinder.getContainer().add(cyl1_x = new NumberInput20(4, pass += 24, 90, 20).setup("cyl1x", 3, Integer.MAX_VALUE, false));
			cylinder.getContainer().add(cyl1_y = new NumberInput20(102, pass, 90, 20).setup("cyl1y", 0, 5, false));
			cylinder.getContainer().add(cyl1_z = new NumberInput20(200, pass, 90, 20).setup("cyl1z", 0, Integer.MAX_VALUE, false));
			cylinder.getContainer().add(new Label20(translate("editor.general.cylinder.scale"), 3, pass += 24, 290, 20));
			cylinder.getContainer().add(cyl2_x = new NumberInput20(4, pass += 24, 90, 20).setup("cyl2x", 0, Integer.MAX_VALUE, true));
			cylinder.getContainer().add(cyl2_y = new NumberInput20(102, pass, 90, 20).setup("cyl2y", 0, Integer.MAX_VALUE, true));
			cylinder.getContainer().add(new Label20(translate("editor.general.cylinder.top_offset"), 3, pass += 24, 290, 20));
			cylinder.getContainer().add(cyl3_x = new NumberInput20(4, pass += 24, 90, 20).setup("cyl0x", 1, Integer.MAX_VALUE, true));
			cylinder.getContainer().add(cyl3_y = new NumberInput20(102, pass, 90, 20).setup("cyl3y", 1, Integer.MAX_VALUE, true));
			cylinder.getContainer().add(cyl3_z = new NumberInput20(200, pass, 90, 20).setup("cyl3z", 0, Integer.MAX_VALUE, true));
			cylinder.getContainer().add(new Label20(translate("editor.general.cylinder.top_rotation"), 3, pass += 24, 290, 20));
			cylinder.getContainer().add(cyl7_x = new NumberInput20(4, pass += 24, 90, 20).setup("cyl7x", -360, 360, true));
			cylinder.getContainer().add(cyl7_y = new NumberInput20(102, pass, 90, 20).setup("cyl7y", -360, 360, true));
			cylinder.getContainer().add(cyl7_z = new NumberInput20(200, pass, 90, 20).setup("cyl7z", -360, 360, true));
			cylinder.getContainer().add(new Label20(translate("editor.general.cylinder.visibility_toggle"), 3, pass += 24, 290, 20));
			cylinder.getContainer().add(cyl4_x = new BoolButton("cyl4x", 6, pass += 24, 66, 20));
			cylinder.getContainer().add(cyl4_y = new BoolButton("cyl4y", 78, pass, 66, 20));
			cylinder.getContainer().add(cyl5_x = new BoolButton("cyl5x", 148, pass, 66, 20));
			cylinder.getContainer().add(cyl5_y = new BoolButton("cyl5y", 220, pass, 66, 20));
			cylinder.getContainer().add(new Label20(translate("editor.general.cylinder.radial_texture"), 3, pass += 24, 290, 20));
			cylinder.getContainer().add(cyl6_x = new BoolButton("cyl6x", 4, pass += 24, 90, 20));
			cylinder.getContainer().add(cyl6_y = new NumberInput20(102, pass, 90, 20).setup("cyl6y", 0, Integer.MAX_VALUE, true));
			cylinder.getContainer().add(cyl6_z = new NumberInput20(200, pass, 90, 20).setup("cyl6z", 0, Integer.MAX_VALUE, true));
			cylinder.setSize(296, pass + 52);
	        this.addSub(cylinder); pass = -20;
	        //
			EditorWidget marker = new EditorWidget(this, translate("editor.general.marker"), 0, 0, 0, 0);
			marker.getContainer().add(new Label20(translate("editor.general.marker.color"), 3, pass += 24, 290, 20));
			marker.getContainer().add(marker_color = new ColorInput20(marker.getContainer(), "marker_colorx", 3, pass += 24, 290, 20));
	        marker.getContainer().add(new Label20(translate("editor.general.marker.biped_display"), 3, pass += 24, 290, 20));
	        marker.getContainer().add(marker_biped = new BoolButton("marker_bipedx", 4, pass += 24, 90, 20));
	        marker.getContainer().add(marker_angle = new NumberInput20(102, pass, 90, 20).setup("marker_anglex", -360, 360, true));
	        marker.getContainer().add(marker_scale = new NumberInput20(200, pass, 90, 20).setup("marker_scalex", 0, 1024f, true));
			marker.setSize(296, pass + 52);
	        this.addSub(marker); pass = -20;
	        //
			final String[] faces = new String[]{
				translate("editor.general.texrect.front"), translate("editor.general.texrect.back"),
				translate("editor.general.texrect.up"), translate("editor.general.texrect.down"),
				translate("editor.general.texrect.right"), translate("editor.general.texrect.left")
			};
			EditorWidget texrectA = new EditorWidget(this, translate("editor.general.texrect_a"), 0, 0, 0, 0);
			int[] tra = new int[24]; for(int i = 0; i < 12; i++){ tra[i * 2] = 1; tra[i * 2 + 1] = 4; }
			for(int r = 0; r < 12; r++){
				texrectA.getContainer().add(new Label20(format("editor.general.texrect_a.face_" + (r % 2 == 0 ? "x" : "y"), faces[r / 2]), 3, pass += 24, 290, 20));
				for(int i = 0; i < 4; i++){
					String id = "texpos" + (r / 2) + ":" + ((i * 2) + (r % 2 == 1 ? 1 : 0)) + (r % 2 == 0 ? "x" : "y"); if(i == 0) pass += 24;
					texrectA.getContainer().add(texrect_a[r % 6][r >= 6 ? i + 4 : i] = new NumberInput20(6 + (i * 72), pass, 66, 20).setup(id, 0, Integer.MAX_VALUE, true));
				}
			}
			texrectA.setSize(296, pass + 52);
	        this.addSub(texrectA); pass = -20;
			EditorWidget texrectB = new EditorWidget(this, translate("editor.general.texrect_b"), 0, 0, 0, 0);
			for(int r = 0; r < 6; r++){
				texrectB.getContainer().add(new Label20(format("editor.general.texrect_a.face_" + (r % 2 == 0 ? "x" : "y"), faces[r]), 3, pass += 24, 290, 20));
				for(int i = 0; i < 4; i++){
					String id = "texpos" + r + (i < 2 ? "s" : "e") + (i % 2 == 0 ? "x" : "y"); if(i == 0) pass += 24;
					texrectB.getContainer().add(texrect_b[r][i] = new NumberInput20(6 + (i * 72), pass, 66, 20).setup(id, 0, Integer.MAX_VALUE, true));
				}
			}
			texrectB.setSize(296, pass + 52);
	        this.addSub(texrectB); pass = -20;
			//
	        //reOrderWidgets();
	        texrectA.setMinimized(true);
	        texrectB.setMinimized(true);
		}
		
		public void refreshGroups(){
			while(!polygon_group.getElements().isEmpty()) polygon_group.removeElement(0);
			for(TurboList list : FMTB.MODEL.getGroups()) polygon_group.addElement(list.id);
			polygon_group.addElement("> new_group <");
			polygon_group.getSelectBoxElements().forEach(elm -> elm.getTextState().setFontSize(20f));
		}
		
	}
	
	public static class ModelGroupEditor extends EditorBase {
		
		private static final int[] texsizes = new int[]{ 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096 };//, 8192 };
		public static NumberInput20 pos_x, pos_y, pos_z, poss_x, poss_y, poss_z;
		public static NumberInput20 rot_x, rot_y, rot_z;
		public static TextInput model_texture, model_name;
		public static SelectBox<Float> tex_x, tex_y, tex_s;
		private String name_cache;
		
		@SuppressWarnings("unchecked")
		public ModelGroupEditor(){
			super(); int pass = -20;
			EditorWidget model = new EditorWidget(this, translate("editor.model_group.model"), 0, 0, 0, 0);
			model.getContainer().add(new Label20(translate("editor.model_group.model.position_full"), 3, pass += 24, 290, 20));
			model.getContainer().add(pos_x = new NumberInput20(4, pass += 24, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(true)));
			model.getContainer().add(pos_y = new NumberInput20(102, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(true)));
			model.getContainer().add(pos_z = new NumberInput20(200, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(true)));
			model.getContainer().add(new Label20(translate("editor.model_group.model.position_sixteenth"), 3, pass += 24, 290, 20));
			model.getContainer().add(poss_x = new NumberInput20(4, pass += 24, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(false)));
			model.getContainer().add(poss_y = new NumberInput20(102, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(false)));
			model.getContainer().add(poss_z = new NumberInput20(200, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(false)));
			model.getContainer().add(new Label20(translate("editor.model_group.model.rotation"), 3, pass += 24, 290, 20));
			model.getContainer().add(rot_x = new NumberInput20(4, pass += 24, 90, 20).setup(-360, 360, true, () -> updateModelRot()));
			model.getContainer().add(rot_y = new NumberInput20(102, pass, 90, 20).setup(-360, 360, true, () -> updateModelRot()));
			model.getContainer().add(rot_z = new NumberInput20(200, pass, 90, 20).setup(-360, 360, true, () -> updateModelRot()));
			model.getContainer().add(new Label20(translate("editor.model_group.model.texture_size"), 3, pass += 24, 290, 20));
			model.getContainer().add(tex_x = new SelectBox<>(4, pass += 24, 90, 20));
	        for(int size : texsizes) tex_x.addElement((float)size);
	        tex_x.getSelectBoxElements().forEach(elm -> elm.getTextState().setFontSize(20f));
	        tex_x.setVisibleCount(10); tex_x.setElementHeight(20);
	        tex_x.getSelectionButton().getTextState().setFontSize(20f);
	        tex_x.addSelectBoxChangeSelectionEventListener(event -> updateModelTexSize(event, true));
			model.getContainer().add(tex_y = new SelectBox<>(102, pass, 90, 20));
	        for(int size : texsizes) tex_y.addElement((float)size);
	        tex_y.getSelectBoxElements().forEach(elm -> elm.getTextState().setFontSize(20f));
	        tex_y.setVisibleCount(10); tex_y.setElementHeight(20);
	        tex_y.getSelectionButton().getTextState().setFontSize(20f);
	        tex_y.addSelectBoxChangeSelectionEventListener(event -> updateModelTexSize(event, false));
			model.getContainer().add(tex_s = new SelectBox<>(200, pass, 90, 20));
	        tex_s.addElement(1f); tex_s.addElement(2f); tex_s.addElement(3f); tex_s.addElement(4f);
	        tex_s.getSelectBoxElements().forEach(elm -> elm.getTextState().setFontSize(20f));
	        tex_s.setVisibleCount(10); tex_s.setElementHeight(20);
	        tex_s.getSelectionButton().getTextState().setFontSize(20f);
	        tex_s.addSelectBoxChangeSelectionEventListener(event -> updateModelTexSize(event, null));
	        model.getContainer().add(new Label20(translate("editor.model_group.model.texture"), 3, pass += 24, 290, 20));
			model.getContainer().add(model_texture = new TextInput20(FMTB.MODEL.texture, 3, pass += 24, 290, 20));
			model_texture.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() == CLICK){
					if(listener.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
						FileSelector.select(translate("editor.model_group.model.texture.select"), "./", FileSelector.TYPE_PNG, false, file -> {
							if(file == null) return;
							String name = file.getPath(); TextureManager.loadTextureFromFile(name, file);
							FMTB.MODEL.setTexture(name); FMTB.MODEL.updateFields(); 
						});
					}
					else if(listener.getButton() == MouseButton.MOUSE_BUTTON_RIGHT){
						if(FMTB.MODEL.texture != null && TextureManager.getTexture(FMTB.MODEL.texture, true) != null){
							FMTB.MODEL.setTexture(null); TextureManager.removeTexture(FMTB.MODEL.texture);
						} FMTB.MODEL.updateFields(); return;
					}
				}
			});
			model.getContainer().add(new Label20(translate("editor.model_group.model.name"), 3, pass += 24, 290, 20));
			model.getContainer().add(model_name = new TextInput20(FMTB.MODEL.name, 3, pass += 24, 290, 20));
			model_name.addTextInputContentChangeEventListener(listener -> name_cache = UserInterpanels.validateString(listener));
			model_name.getListenerMap().addListener(FocusEvent.class, listener -> {
				if(!listener.isFocused() && !name_cache.equals(FMTB.MODEL.name))
					FMTB.get().setTitle(FMTB.MODEL.name = name_cache);
			});
			model.setSize(296, pass + 52);
	        this.addSub(model); pass = -20;
	        //
	        
	        //
	        reOrderWidgets();
		}

		private void updateModelPos(boolean full){
			float x = (full ? pos_x : poss_x).getValue();
			float y = (full ? pos_y : poss_y).getValue();
			float z = (full ? pos_z : poss_z).getValue();
			if(FMTB.MODEL.pos == null) FMTB.MODEL.pos = new Vec3f(0, 0, 0);
			FMTB.MODEL.pos.xCoord = full ? x : x * Static.sixteenth;
			FMTB.MODEL.pos.yCoord = full ? y : y * Static.sixteenth;
			FMTB.MODEL.pos.zCoord = full ? z : z * Static.sixteenth;
		}

		private void updateModelRot(){
			float x = rot_x.getValue(), y = rot_y.getValue(), z = rot_z.getValue();
			if(FMTB.MODEL.rot == null) FMTB.MODEL.rot = new Vec3f(0, 0, 0);
			FMTB.MODEL.rot.xCoord = x; FMTB.MODEL.rot.yCoord = y; FMTB.MODEL.rot.zCoord = z;
		}

		private void updateModelTexSize(SelectBoxChangeSelectionEvent<Float> event, Boolean bool){
			if(FMTB.MODEL == null) return; int value = (int)(event.getNewValue() + 0f);
			if(bool == null) FMTB.MODEL.textureScale = value;
			else if(bool) FMTB.MODEL.textureSizeX = value;
			else FMTB.MODEL.textureSizeY = value;
			TextureUpdate.updateSize(null); return;
		}
		
	}
	
	public static class EditorWidget extends Widget {
		
		private EditorBase editor;

		public EditorWidget(EditorBase base, String title, int x, int y, int w, int h){
			super(x, y, w, h); editor = base;
			Background background = new Background(); background.setColor(ColorConstants.lightGray());
			getTitleTextState().setFontSize(22); getTitleTextState().setText(title);
			getTitleContainer().getStyle().setBackground(background);
			getTitleContainer().setSize(getTitleContainer().getSize().x, 20);
			getTitleTextState().setHorizontalAlign(HorizontalAlign.CENTER);
	        setCloseable(false); setResizable(false); setDraggable(false);
		}
		
		@Override
		public void setMinimized(boolean bool){
			super.setMinimized(bool);
			editor.reOrderWidgets();
		}

		public void toggle(){
			setMinimized(!isMinimized());
		}
		
	}
	
	public static String translate(String str){
		return Translator.translate(str, "no.lang");
	}
	
	public static String format(String str, Object... objs){
		return Translator.format(str, objs);
	}

	public static void toggleWidget(int i){
		if(i < 0) return;
		if(anyVisible()){
			EditorBase editor = getVisible();
			if(i >= editor.widgets.size()) return;
			editor.widgets.get(i).toggle();
		}
		else{
			if(i >= editors.size()) return;
			hideAll(); editors.get(i).show();
		}
	}

	public static void resize(int width, int height){
		for(EditorBase editor : editors){
			editor.scrollable.setSize(editor.scrollable.getSize().x, FMTB.HEIGHT - 80);
		}
	}

}
