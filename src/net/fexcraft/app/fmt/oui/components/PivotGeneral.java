package net.fexcraft.app.fmt.oui.components;

import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.oui.EditorComponent;
import net.fexcraft.app.fmt.oui.fields.BoolButton;
import net.fexcraft.app.fmt.oui.fields.TextField;
import net.fexcraft.app.fmt.update.UpdateEvent.*;

import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotGeneral extends EditorComponent {

	private static final String NOPIVOTSEL = "< no pivot directly selected >";
	private SelectBox<String> pivots = new SelectBox<>();
	protected static final String genid = "pivot.general";
	protected TextField[] pos = new TextField[3];
	protected TextField[] rot = new TextField[3];
	protected BoolButton rrot;
	private TextField name;

	public PivotGeneral(){
		super(genid, 610, false, true);
		this.add(new Label(translate(LANG_PREFIX + genid + ".pos_attr"), L5, row(2), LW, HEIGHT));
		this.add(pos[0] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, true, 0)));
		this.add(pos[1] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, true, 1)));
		this.add(pos[2] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, true, 2)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".rot_attr"), L5, row(2), LW, HEIGHT));
		this.add(rot[0] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, false, 0)));
		this.add(rot[1] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, false, 1)));
		this.add(rot[2] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, false, 2)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".root_rot"), L5, row(2), LW, HEIGHT));
		this.add(rrot = new BoolButton(L5, row(1), LW, HEIGHT, true, con -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.root_rot = !FMT.MODEL.sel_pivot.root_rot;
		}));
	}

	private void linkPivotAttr(String con, boolean pos, int axe){
		if(FMT.MODEL.sel_pivot == null) return;
		(pos ? FMT.MODEL.sel_pivot.pos_attr : FMT.MODEL.sel_pivot.rot_attr)[axe] = con;
	}

	private void updateFields(){
		if(FMT.MODEL.sel_pivot == null){
			for(TextField tf : pos) tf.getTextState().setText("");
			for(TextField tf : rot) tf.getTextState().setText("");
			rrot.apply(0);
		}
		else{
			for(int i = 0; i < pos.length; i++) pos[i].getTextState().setText(FMT.MODEL.sel_pivot.pos_attr[i]);
			for(int i = 0; i < rot.length; i++) rot[i].getTextState().setText(FMT.MODEL.sel_pivot.rot_attr[i]);
			rrot.apply(FMT.MODEL.sel_pivot.root_rot ? 1 : 0);
		}
	}

}
