package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.lib.common.math.RGB;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.settings.Settings.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Dialog extends Element {

	public Element title;
	public Element container;
	public Consumer<Dialog> on_confirm;
	public Consumer<Dialog> on_cancel;
	public Consumer<Dialog> on_close;

	public Dialog(int width, int height){
		super();
		z += 200;
		pos((FMT.SCALED_WIDTH - width) * 0.5f, (FMT.SCALED_HEIGHT - height) * 0.5f);
		size(width, height + 30);
		color(GENERIC_BACKGROUND_0.value);
		border(RGB.BLACK);
	}

	@Override
	public void init(Object... args){
		add(title = new Element().size(w - 31, 30).color(GENERIC_BACKGROUND_1.value)
			.translate(args.length > 0 ? args[0].toString() : "dialog.title.default").text_autoscale());
		add(new Element().size(30, 30).pos(w - 31, 0).texture("icons/component/exit")
			.hoverable(true).onclick(ci -> close()));
		add(container = new Element().size(w, h - 30).pos(0, 30));
	}

	public void close(){
		if(on_close != null) on_close.accept(this);
		if(FMT.UI.DIALOG == this){
			FMT.UI.DIALOG = null;
		}
		FMT.UI.remElm(this);
	}

	public void onConfirm(){
		if(on_confirm != null) on_confirm.accept(this);
		close();
	}

	public void onCancel(){
		if(on_cancel != null) on_cancel.accept(this);
		close();
	}

	public Dialog consumer(Consumer<Dialog> co, Consumer<Dialog> ca){
		on_confirm = co;
		on_cancel = ca;
		return this;
	}

	public Dialog buttons(int bw, DialogButton... buttons){
		for(int i = 0; i < buttons.length; i++){
			int idx = i;
			container.add(new Element().shape(ElmShape.RECT_ROUNDED).color(GENERIC_FIELD.value).translate("dialog.button." + buttons[i].name().toLowerCase())
				.pos(w - (bw + 10 + (bw + 10) * i), container.h - 40).size(bw, 30).text_centered(true).hoverable(true)
				.onclick(ci -> {
					if(idx == 0) onConfirm();
					if(idx == 1) onCancel();
					if(idx > 1) close();
				})
			);
		}
		return this;
	}

	public Dialog addText(float row, String text, Object... format){
		container.add(new TextElm(5, 5 + row * 30, container.w - 10, text, format).text_autoscale());
		return this;
	}

	public Dialog addRowElm(float row, Element elm, Object... init){
		container.add(elm.pos(5, 5 + row * 30), init);
		return this;
	}

	public static enum DialogButton {
		CONFIRM, CANCEL, CONTINUE, SELECT,
		CLOSE, OPEN, SAVE, LOAD, SKIP, EXIT, RETRY,
		NEW, ADD, YES, NO, OK, ENABLE, DISABLE
	}

}
