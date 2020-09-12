package net.fexcraft.app.fmt.ui.editor;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Slider;
import org.liquidengine.legui.component.event.slider.SliderChangeValueEventListener;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.Vec3f;

public class PreviewEditor extends EditorBase {
	
	public static NumberField pos_x, pos_y, pos_z, poss_x, poss_y, poss_z;
	public static NumberField rot_x, rot_y, rot_z;
	public static NumberField size_x, size_y, size_z, size16_x, size16_y, size16_z;
	public static TextField helper_name;
	
	public PreviewEditor(){
		super(); int pass = -20;
		EditorWidget preview = new EditorWidget(this, translate("editor.preview.container"), 0, 0, 0, 0);
		preview.getContainer().add(new Label(translate("editor.preview.container.name"), 3, pass += 24, 290, 20));
		preview.getContainer().add(helper_name = new TextField(FMTB.NO_POLYGON_SELECTED, 3, pass += 24, 290, 20));
		helper_name.addTextInputContentChangeEventListener(event -> {
			String validated = UserInterfaceUtils.validateString(event);
			GroupCompound compound = HelperCollector.getSelected();
			if(compound == null || !compound.name.contains("/")) return;
			compound.name = compound.name.substring(0, compound.name.indexOf('/') + 1);
			compound.name += validated;
			compound.button.update();
        });
		preview.getContainer().add(new Label(translate("editor.preview.container.position_full"), 3, pass += 24, 290, 20));
		preview.getContainer().add(pos_x = new NumberField(4, pass += 24, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updatePos(true)));
		preview.getContainer().add(pos_y = new NumberField(102, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updatePos(true)));
		preview.getContainer().add(pos_z = new NumberField(200, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updatePos(true)));
		preview.getContainer().add(new Label(translate("editor.preview.container.position_sixteenth"), 3, pass += 24, 290, 20));
		preview.getContainer().add(poss_x = new NumberField(4, pass += 24, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updatePos(false)));
		preview.getContainer().add(poss_y = new NumberField(102, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updatePos(false)));
		preview.getContainer().add(poss_z = new NumberField(200, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updatePos(false)));
		preview.getContainer().add(new Label(translate("editor.preview.container.rotation"), 3, pass += 24, 290, 20));
		preview.getContainer().add(rot_x = new NumberField(4, pass += 24, 90, 20).setup(-360, 360, true, () -> updateRotation()));
		preview.getContainer().add(rot_y = new NumberField(102, pass, 90, 20).setup(-360, 360, true, () -> updateRotation()));
		preview.getContainer().add(rot_z = new NumberField(200, pass, 90, 20).setup(-360, 360, true, () -> updateRotation()));
		preview.getContainer().add(new Label(translate("editor.preview.container.scale_full"), 3, pass += 24, 290, 20));
		preview.getContainer().add(size_x = new NumberField(4, pass += 24, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateScale(true)));
		preview.getContainer().add(size_y = new NumberField(102, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateScale(true)));
		preview.getContainer().add(size_z = new NumberField(200, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateScale(true)));
		preview.getContainer().add(new Label(translate("editor.preview.container.scale_sixteenth"), 3, pass += 24, 290, 20));
		preview.getContainer().add(size16_x = new NumberField(4, pass += 24, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateScale(false)));
		preview.getContainer().add(size16_y = new NumberField(102, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateScale(false)));
		preview.getContainer().add(size16_z = new NumberField(200, pass, 90, 20).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, () -> updateScale(false)));
		preview.getContainer().add(new Label(translate("editor.preview.container.opacity"), 3, pass += 24, 290, 20));
		Slider slider = new Slider(3, pass += 24, 290, 20);
		slider.setMinValue(0.51f);
		slider.setMaxValue(1f);
		slider.setStepSize(0.1f);
		slider.setValue(1f);
        slider.addSliderChangeValueEventListener((SliderChangeValueEventListener) event -> {
			GroupCompound compound = HelperCollector.getSelected();
			if(compound == null) return;
			compound.opacity = event.getNewValue();
			compound.op_color = null;
        });
		preview.getContainer().add(slider);
		preview.setSize(296, pass + 52);
        this.addSub(preview); //pass = -20;
        reOrderWidgets();
	}

	private void updatePos(boolean full){
		GroupCompound compound = HelperCollector.getSelected(); if(compound == null) return;
		if(compound.pos == null) compound.pos = new Vec3f(0, 0, 0);
		compound.pos.xCoord = full ? pos_x.getValue() : poss_x.getValue() / 16f;
		compound.pos.yCoord = full ? pos_y.getValue() : poss_y.getValue() / 16f;
		compound.pos.zCoord = full ? pos_z.getValue() : poss_z.getValue() / 16f;
		pos_x.apply(compound.pos.xCoord); pos_y.apply(compound.pos.yCoord); pos_z.apply(compound.pos.zCoord);
		poss_x.apply(compound.pos.xCoord * 16); poss_y.apply(compound.pos.yCoord * 16); poss_z.apply(compound.pos.zCoord * 16);
	}

	private void updateRotation(){
		GroupCompound compound = HelperCollector.getSelected(); if(compound == null) return;
		if(compound.rot == null) compound.rot = new Vec3f(0, 0, 0);
		compound.rot.xCoord = rot_x.getValue(); compound.rot.yCoord = rot_y.getValue(); compound.rot.zCoord = rot_z.getValue();
		rot_x.apply(compound.rot.xCoord); rot_y.apply(compound.rot.yCoord); rot_z.apply(compound.rot.zCoord);
	}

	private void updateScale(boolean full){
		GroupCompound compound = HelperCollector.getSelected(); if(compound == null) return;
		if(compound.scale == null) compound.scale = new Vec3f(0, 0, 0);
		compound.scale.xCoord = full ? size_x.getValue() : size16_x.getValue() / 16f;
		compound.scale.yCoord = full ? size_y.getValue() : size16_y.getValue() / 16f;
		compound.scale.zCoord = full ? size_z.getValue() : size16_z.getValue() / 16f;
		size_x.apply(compound.scale.xCoord); size_y.apply(compound.scale.yCoord); size_z.apply(compound.scale.zCoord);
		size16_x.apply(compound.scale.xCoord * 16); size16_y.apply(compound.scale.yCoord * 16); size16_z.apply(compound.scale.zCoord * 16);
	}
	
}
