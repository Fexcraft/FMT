package net.fexcraft.app.fmt.ui.editor;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.Map;
import java.util.Map.Entry;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.SettingsBox;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.ui.tree.TreeIcon;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.utils.texture.TextureUpdate;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.Vec3f;

public class ModelEditor extends EditorBase {

	private static final int[] texsizes = new int[]{ 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096 };// , 8192 };
	public static NumberField pos_x, pos_y, pos_z, poss_x, poss_y, poss_z;
	public static NumberField rot_x, rot_y, rot_z, scale;
	public static TextInput model_name;
	public static SelectBox<Float> m_tex_x, m_tex_y;//, m_tex_s;
	public static SelectBox<String> model_texture;
	public static AuthorsEditorWidget creators;
	private String name_cache;

	public ModelEditor(){
		super();
		int pass = -20;
		EditorWidget model = new EditorWidget(this, translate("editor.model_group.model"), 0, 0, 0, 0);
		model.getContainer().add(new Label(translate("editor.model_group.model.position_full"), 3, pass += 24, 290, 20));
		model.getContainer().add(pos_x = new NumberField(4, pass += 24, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(true)));
		model.getContainer().add(pos_y = new NumberField(102, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(true)));
		model.getContainer().add(pos_z = new NumberField(200, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(true)));
		model.getContainer().add(new Label(translate("editor.model_group.model.position_sixteenth"), 3, pass += 24, 290, 20));
		model.getContainer().add(poss_x = new NumberField(4, pass += 24, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(false)));
		model.getContainer().add(poss_y = new NumberField(102, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(false)));
		model.getContainer().add(poss_z = new NumberField(200, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateModelPos(false)));
		model.getContainer().add(new Label(translate("editor.model_group.model.rotation"), 3, pass += 24, 290, 20));
		model.getContainer().add(rot_x = new NumberField(4, pass += 24, 90, 20).setup(-360, 360, true, () -> updateModelRot()));
		model.getContainer().add(rot_y = new NumberField(102, pass, 90, 20).setup(-360, 360, true, () -> updateModelRot()));
		model.getContainer().add(rot_z = new NumberField(200, pass, 90, 20).setup(-360, 360, true, () -> updateModelRot()));
		model.getContainer().add(new Label(translate("editor.model_group.model.scale"), 3, pass += 24, 290, 20));
		model.getContainer().add(scale = new NumberField(4, pass += 24, 90, 20).setup(0, 64, true, () -> FMTB.MODEL.scale = new Vec3f(scale.getValue(), scale.getValue(), scale.getValue())));
		model.getContainer().add(new Label(translate("editor.model_group.model.texture_size"), 3, pass += 24, 290, 20));
		model.getContainer().add(m_tex_x = new SelectBox<>(4, pass += 24, 90, 20));
		for(int size : texsizes) m_tex_x.addElement((float)size);
		m_tex_x.getSelectBoxElements().forEach(elm -> elm.getStyle().setFontSize(20f));
		m_tex_x.setVisibleCount(10);
		m_tex_x.setElementHeight(20);
		m_tex_x.getSelectionButton().getStyle().setFontSize(20f);
		m_tex_x.addSelectBoxChangeSelectionEventListener(event -> updateModelTexSize(event, true));
		model.getContainer().add(m_tex_y = new SelectBox<>(102, pass, 90, 20));
		for(int size : texsizes) m_tex_y.addElement((float)size);
		m_tex_y.getSelectBoxElements().forEach(elm -> elm.getStyle().setFontSize(20f));
		m_tex_y.setVisibleCount(10);
		m_tex_y.setElementHeight(20);
		m_tex_y.getSelectionButton().getStyle().setFontSize(20f);
		m_tex_y.addSelectBoxChangeSelectionEventListener(event -> updateModelTexSize(event, false));
		/*model.getContainer().add(m_tex_s = new SelectBox<>(200, pass, 90, 20));
		m_tex_s.addElement(1f);
		m_tex_s.addElement(2f);
		m_tex_s.addElement(3f);
		m_tex_s.addElement(4f);
		m_tex_s.getSelectBoxElements().forEach(elm -> elm.getStyle().setFontSize(20f));
		m_tex_s.setVisibleCount(10);
		m_tex_s.setElementHeight(20);
		m_tex_s.getSelectionButton().getStyle().setFontSize(20f);
		m_tex_s.addSelectBoxChangeSelectionEventListener(event -> updateModelTexSize(event, null));*/
		model.getContainer().add(new Label(translate("editor.model_group.model.texture"), 3, pass += 24, 290, 20));
		model.getContainer().add(model_texture = new SelectBox<>(4, pass += 24, 290, 20));
		model_texture.addSelectBoxChangeSelectionEventListener(event -> updateModelTexture(event));
		model_texture.setVisibleCount(6);
		model.getContainer().add(new Label(translate("editor.model_group.model.name"), 3, pass += 24, 290, 20));
		model.getContainer().add(model_name = new TextField(FMTB.MODEL.name, 3, pass += 24, 290, 20));
		model_name.addTextInputContentChangeEventListener(listener -> name_cache = UserInterfaceUtils.validateString(listener));
		model_name.getListenerMap().addListener(FocusEvent.class, listener -> {
			if(name_cache == null) return;
			if(!listener.isFocused() && !name_cache.equals(FMTB.MODEL.name)) FMTB.get().setTitle(FMTB.MODEL.name = name_cache);
			FMTB.MODEL.button.update();
		});
		model.setSize(296, pass + 52);
		this.addSub(model);
		pass = -20;
		//
		creators = new AuthorsEditorWidget(this, translate("editor.model_group.authors"), 0, 0, 0, 0);
		creators.refresh();
		this.addSub(creators);
		pass = -20;
		//
		GroupEditor.updateTextureGroups();
		reOrderWidgets();
	}
	
	public static class AuthorsEditorWidget extends EditorWidget {

		public AuthorsEditorWidget(EditorBase base, String title, int x, int y, int w, int h){
			super(base, title, x, y, w, h);
		}

		public void refresh(){
			refresh(null);
		}

		public void refresh(Map<String, Boolean> creators){
			reset();
			int pass = -20;
			if(creators == null) creators = FMTB.MODEL.getCreators();
			for(Entry<String, Boolean> entry : creators.entrySet()){
				String author = entry.getKey();
				Button button = new Button("> " + author, 3, pass += 24, 290, 20);
				button.add(new TreeIcon((int)getSize().x - 30, 0, "group_delete", () -> FMTB.MODEL.remAuthor(author), "delete"));
				button.add(new TreeIcon((int)getSize().x - 50, 0, entry.getValue() ? "locked" : "unlocked", () -> FMTB.MODEL.lockAuthor(author, !entry.getValue()), entry.getValue() ? "unlock" : "lock"));
				button.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
				this.getContainer().add(button);
			}
			Button button = new Button(Translator.translate("editor.model_group.authors.add"), 3, pass += 24, 290, 20);
			button.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
			button.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() != CLICK || listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT) return;
				Dialog dialog = new Dialog(Translator.translate("editor.model_group.authors.add.dialog"), 300, 90);
				dialog.setResizable(false);
				TextField input = new TextField("no name", 10, 10, 280, 20);
				input.addTextInputContentChangeEventListener(lis -> UserInterfaceUtils.validateString(lis, true));
				dialog.getContainer().add(input);
	            Button button0 = new Button(Translator.translate("dialogbox.button.confirm"), 10, 40, 100, 20);
	            button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
	            	if(CLICK == e.getAction()){
	            		FMTB.MODEL.addAuthor(input.getTextState().getText(), true, false);
	            		dialog.close();
	            	}
	            });
	            dialog.getContainer().add(button0);
				dialog.show(FMTB.frame);
			});
			this.getContainer().add(button);
			Button button1 = new Button(Translator.translate("editor.model_group.authors.add_self"), 3, pass += 24, 290, 20);
			button1.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
			button1.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() != CLICK || listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT) return;
				if(SessionHandler.isLoggedIn()){
					FMTB.MODEL.addAuthor(SessionHandler.getUserName(), true, false);
				}
			});
			this.getChildComponents().forEach(child -> child.getStyle().setPadding(0, 50, 0, 10));
			this.getContainer().add(button1);
			this.setSize(296, pass + 52);
			editor.reOrderWidgets();
		}

		private void reset(){
			this.getContainer().clearChildComponents();
		}

	}
	
	private void updateModelTexture(SelectBoxChangeSelectionEvent<String> event){
		if(event.getNewValue().equals("none")) FMTB.MODEL.texgroup = null;
		FMTB.MODEL.texgroup = TextureManager.getGroup(event.getNewValue());
		FMTB.MODEL.recompile();
	}

	public static class AnimationsEditorWidget extends EditorWidget {

		private TurboList group = null;

		public AnimationsEditorWidget(EditorBase base, String title, int x, int y, int w, int h){
			super(base, title, x, y, w, h);
		}

		public void refresh(TurboList list){
			if(group != list) reset();
			int pass = -20;
			if(list != null){
				for(int i = 0; i < list.animations.size(); i++){
					Button button = new Button("[" + i + "]" + list.animations.get(i).id, 3, pass += 24, 290, 20);
					final int j = i;
					button.getListenerMap().addListener(MouseClickEvent.class, listener -> {
						if(listener.getAction() != CLICK) return;
						if(listener.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
							Animation anim = list.animations.get(j);
							if(anim == null) return;
							FMTB.MODEL.updateFields();
							SettingsBox.open("[" + anim.id + "] " + translate("editor.model_group.group.animator_settings"), anim.settings.values(), false, settings -> {
								anim.onSettingsUpdate();
								FMTB.MODEL.updateFields();
							});
						}
						else if(listener.getButton() == MouseButton.MOUSE_BUTTON_RIGHT){
							Animation anim = list.animations.get(j);
							anim.button.removeFromSubTree();
							list.animations.remove(anim);
							list.abutton.recalculateSize();
							FMTB.MODEL.updateFields();
						}
					});
					button.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
					this.getContainer().add(button);
				}
			}
			this.setSize(296, pass + 52);
		}

		private void reset(){
			this.getContainer().clearChildComponents();
		}

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
		FMTB.MODEL.rot.xCoord = x;
		FMTB.MODEL.rot.yCoord = y;
		FMTB.MODEL.rot.zCoord = z;
	}

	private void updateModelTexSize(SelectBoxChangeSelectionEvent<Float> event, Boolean bool){
		if(FMTB.MODEL == null) return;
		int value = (int)(event.getNewValue() + 0f);
		/*if(bool == null) FMTB.MODEL.textureScale = value;
		else*/ if(bool) FMTB.MODEL.textureSizeX = value;
		else FMTB.MODEL.textureSizeY = value;
		TextureUpdate.updateSize(null);
		FMTB.MODEL.recompile();
		return;
	}

}
