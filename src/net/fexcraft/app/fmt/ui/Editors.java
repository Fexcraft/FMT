package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.ArrayList;

import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.event.slider.SliderChangeValueEventListener;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.WindowSizeEvent;
import org.liquidengine.legui.style.Background;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.color.ColorConstants;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterpanels.Button20;
import net.fexcraft.app.fmt.ui.UserInterpanels.Dialog20;
import net.fexcraft.app.fmt.ui.UserInterpanels.Label20;
import net.fexcraft.app.fmt.ui.UserInterpanels.NumberInput20;
import net.fexcraft.app.fmt.ui.UserInterpanels.TextInput20;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeType;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.Static;

public class Editors {
	
	public static GeneralEditor general;
	public static ModelGroupEditor modelgroup;

	public static void initializeEditors(Frame frame){
		frame.getContainer().add(general = new GeneralEditor());
		frame.getContainer().add(modelgroup = new ModelGroupEditor());
		//temporary
		general.show();
	}
	
	public static void hideAll(){
		general.hide(); modelgroup.hide();
	}
	
	public static void show(String type){
		hideAll();
		switch(type){
			case "general": general.show(); break;
			case "model": case "group":
			case "modelgroup": modelgroup.show(); break;
		}
	}
	
	public static class EditorBase extends Panel {

		protected ArrayList<EditorWidget> widgets = new ArrayList<>();
		protected ScrollablePanel scrollable;
		
		public EditorBase(){
			super(0, 30, 304, FMTB.HEIGHT - 30);
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
			if(size > FMTB.HEIGHT - 80) scrollable.setSize(scrollable.getSize().x, size); size = 0;
			for(EditorWidget widget : widgets){
				widget.setPosition(0, size); size += widget.getSize().y + 2;
			}
		}
		
	}
	
	public static class GeneralEditor extends EditorBase {
		
		public static TextInput polygon_name;
		public static NumberInput20 size_x, size_y, size_z;
		public static NumberInput20 pos_x, pos_y, pos_z;
		public static NumberInput20 off_x, off_y, off_z;
		public static NumberInput20 rot_x, rot_y, rot_z;
		public static NumberInput20 texture_x, texture_y;
		public static SelectBox<Object> polygon_group, polygon_type;
		
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
	        attributes.getContainer().add(polygon_name = new TextInput20(translate("error.no_polygon_selected"), 3, pass += 24, 290, 20));
	        polygon_name.addTextInputContentChangeEventListener(event -> {
				if(FMTB.MODEL.getSelected().isEmpty()) return;
				PolygonWrapper wrapper;
				if(FMTB.MODEL.getSelected().size() == 1){
					wrapper = FMTB.MODEL.getFirstSelection();
					if(wrapper != null) wrapper.name = event.getNewValue();
				}
				else{
					String text = event.getNewValue();
					ArrayList<PolygonWrapper> polis = FMTB.MODEL.getSelected();
					for(int i = 0; i < polis.size(); i++){
						wrapper = polis.get(i);
						if(wrapper != null){
							String str = text.contains("_") ? "_" + i : text.contains("-") ? "-" + i :
								text.contains(" ") ? " " + i : text.contains(".") ? "." + i : i + "";
							wrapper.name = text + str;
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
	        painttotex.getListenerMap().addListener(MouseClickEvent.class, UserInterpanels.NOT_REIMPLEMENTED_YET/*event -> {
				if(FMTB.MODEL.texture == null){
					String str = translate("dialog.editor.general.attributes.burntotex.notex", "There is no texture loaded.");
					String ok = translate("dialog.editor.general.attributes.burntotex.notex.confirm", "ok");
					FMTB.showDialogbox(str, ok, translate("dialog.editor.general.attributes.burntotex.notex.cancel", "load"), DialogBox.NOTHING, () -> {
						try{
							FMTB.get().UI.getElement("toolbar").getElement("textures").getElement("menu").getElement("select").onButtonClick(x, y, left, true);
						}
						catch(Exception e){
							e.printStackTrace();
						}
					});
				}
				else{
					ArrayList<PolygonWrapper> selection = FMTB.MODEL.getSelected();
					for(PolygonWrapper poly : selection){
						String texname = poly.getTurboList().getGroupTexture() == null ? FMTB.MODEL.texture : poly.getTurboList().getGroupTexture();
						Texture tex = TextureManager.getTexture(texname, true);
						if(tex == null){//TODO group tex compensation
							String str = translate("dialog.editor.general.attributes.burntotex.tex_not_found", "Texture not found in Memory.<nl>This rather bad.");
							FMTB.showDialogbox(str, translate("dialog.editor.general.attributes.burntotex.tex_not_found.confirm", "ok"), null, DialogBox.NOTHING, null);
							return true;
						}
						poly.burnToTexture(tex.getImage(), null); poly.recompile(); TextureManager.saveTexture(texname); tex.rebind();
						Print.console("Polygon painted into Texture.");
					}
					return true;
				}
	        }*/);//TODO
	        attributes.getContainer().add(painttotex);
	        attributes.setSize(296, pass + 52 + 4);
	        this.addSub(attributes); pass = -20;
	        //
			EditorWidget shape = new EditorWidget(this, translate("editor.general.shape"), 0, 0, 0, 0);
			shape.getContainer().add(new Label20(translate("editor.general.shape.size"), 3, pass += 24, 290, 20));
			shape.getContainer().add(size_x = new NumberInput20("x", 4, pass += 24, 90, 20));
			shape.getContainer().add(size_y = new NumberInput20("y", 102, pass, 90, 20));
			shape.getContainer().add(size_z = new NumberInput20("z", 200, pass, 90, 20));
			shape.getContainer().add(new Label20(translate("editor.general.shape.position"), 3, pass += 24, 290, 20));
			shape.getContainer().add(pos_x = new NumberInput20("x", 4, pass += 24, 90, 20));
			shape.getContainer().add(pos_y = new NumberInput20("y", 102, pass, 90, 20));
			shape.getContainer().add(pos_z = new NumberInput20("z", 200, pass, 90, 20));
			shape.getContainer().add(new Label20(translate("editor.general.shape.offset"), 3, pass += 24, 290, 20));
			shape.getContainer().add(off_x = new NumberInput20("x", 4, pass += 24, 90, 20));
			shape.getContainer().add(off_y = new NumberInput20("y", 102, pass, 90, 20));
			shape.getContainer().add(off_z = new NumberInput20("z", 200, pass, 90, 20));
			shape.getContainer().add(new Label20(translate("editor.general.shape.rotation"), 3, pass += 24, 290, 20));
			shape.getContainer().add(rot_x = new NumberInput20("x", 4, pass += 24, 90, 20));
			shape.getContainer().add(rot_y = new NumberInput20("y", 102, pass, 90, 20));
			shape.getContainer().add(rot_z = new NumberInput20("z", 200, pass, 90, 20));
			shape.getContainer().add(new Label20(translate("editor.general.shape.texture"), 3, pass += 24, 290, 20));
			shape.getContainer().add(texture_x = new NumberInput20("x", 4, pass += 24, 90, 20));
			shape.getContainer().add(texture_y = new NumberInput20("y", 102, pass, 90, 20));
			shape.setSize(296, pass + 52);
	        this.addSub(shape); pass = -20;
	        //
	        
	        //
	        reOrderWidgets();
		}
		
		public void refreshGroups(){
			while(!polygon_group.getElements().isEmpty()) polygon_group.removeElement(0);
			for(TurboList list : FMTB.MODEL.getGroups()) polygon_group.addElement(list.id);
			polygon_group.addElement("> new_group <");
			polygon_group.getSelectBoxElements().forEach(elm -> elm.getTextState().setFontSize(20f));
		}
		
	}
	
	public static class ModelGroupEditor extends EditorBase {
		
		public ModelGroupEditor(){
			super();
		}
		
	}
	
	public static class EditorWidget extends Widget {
		
		private EditorBase editor;

		public EditorWidget(EditorBase base, String title, int x, int y, int w, int h){
			super(x, y, w, h); editor = base;
			Background background = new Background(); background.setColor(ColorConstants.lightGray());
			getTitleTextState().setFontSize(22); getTitleTextState().setText(title);
			getTitleContainer().getStyle().setBackground(background);
			getTitleTextState().setHorizontalAlign(HorizontalAlign.CENTER);
	        setCloseable(false); setResizable(false); setDraggable(false);
		}
		
		@Override
		public void setMinimized(boolean bool){
			super.setMinimized(bool);
			editor.reOrderWidgets();
		}
		
	}
	
	public static String translate(String str){
		return Translator.translate(str, "no.lang");
	}
	
	public static String format(String str, Object... objs){
		return Translator.format(str, "no.lang.%s", objs);
	}

}
