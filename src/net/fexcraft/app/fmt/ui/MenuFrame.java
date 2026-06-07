package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.ui.editor.EditorSidePanel;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.GGR;

import java.util.List;
import java.util.function.Consumer;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_0;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class MenuFrame extends Frame {

	private static MenuFrame ACTIVE;
	private static Consumer<MenuFrame> DEF_OPT = frame -> {
		List<Polygon> sel = FMT.MODEL.selected();
		if(sel.isEmpty()){

		}
		else{
			frame.addOption("toolbar.polyselmenu.copy", ci -> FMT.MODEL.copySelected());
			frame.addOption("toolbar.polyselmenu.select_group", ci -> {
				FMT.MODEL.select(sel.get(0).group());
			});
			frame.addOption("toolbar.polyselmenu.visibility", ci -> {
				boolean set = !sel.get(0).visible;
				for(Polygon polygon : sel) polygon.visible = set;
				UpdateHandler.update(new UpdateEvent.PolygonVisibility(sel.get(0), set));
			});
			frame.addOption("toolbar.polyselmenu.delete", ci -> {
				FMT.MODEL.delsel();
			});
			frame.incr += 10;
		}
		frame.addOption("editor.polygon.sorting.add_group", ci -> EditorSidePanel.AddPolygon.addGroup());
	};
	private int incr = 5;

	public MenuFrame(){
		super();
		color(GENERIC_BACKGROUND_0.value);
		border(0);
	}

	public static void create(){
		create(DEF_OPT);
	}

	public static void create(Consumer<MenuFrame> options){
		if(ACTIVE != null) ACTIVE.close();
		MenuFrame frame = new MenuFrame();
		options.accept(frame);
		frame.size(310, frame.incr + 3);
		frame.pos((float)GGR.cpos_x, (float)GGR.cpos_y);
		if(GGR.cpos_x + frame.w > FMT.SCALED_WIDTH) frame.pos(FMT.SCALED_WIDTH - frame.w, frame.y());
		if(GGR.cpos_y + frame.h > FMT.SCALED_HEIGHT) frame.pos(frame.x(), FMT.SCALED_HEIGHT - frame.h);
		FMT.UI.add(ACTIVE = frame);
	}

	public void addOption(String text, Consumer<ClickInfo> cons){
		add(new RunElm(5, incr, 300, text, ci -> {
			close();
			cons.accept(ci);
		}).zi());
		incr += 30;
	}

	@Override
	public void update(){
		if(GGR.cpos_x < x() || GGR.cpos_x >= x() + w) close();
		if(GGR.cpos_y < y() || GGR.cpos_y >= y() + h) close();
	}

	private void close(){
		FMT.UI.remElm(this);
		ACTIVE = null;
	}

}
