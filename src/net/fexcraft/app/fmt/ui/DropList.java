package net.fexcraft.app.fmt.ui;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import static net.fexcraft.app.fmt.ui.FMTInterface.col_85;
import static net.fexcraft.app.fmt.ui.FMTInterface.col_bd;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DropList extends Element {

	private ArrayList<Pair<String, Object>> entries = new ArrayList<>();
	private BiConsumer<String, Object> consumer;
	private int current = 0;
	private Element drop;

	public DropList(float width){
		super();
		size(width - FS * 3, FS);
		hoverable = true;
		selectable = true;
		color(0xa6b3b3);
	}

	@Override
	public void init(Object... args){
		text("");
		add(new Element().color(col_bd).size(FS, FS).pos(w, 0).text("<")
			.text_centered(true).hoverable(true).onclick(info -> change_selection(-1)));
		add(new Element().color(col_bd).size(FS, FS).pos(w + FS, 0).text(">")
			.text_centered(true).hoverable(true).onclick(info -> change_selection(1)));
		add(new Element().color(0x62b4e3).size(FS, FS).pos(w + FS + FS, 0).text("O")
			.text_centered(true).hoverable(true).onclick(info -> {
				if(entries.isEmpty() || consumer == null) return;
				Pair<String, Object> kv = entries.get(current);
				consumer.accept(kv.getLeft(), kv.getRight());
			}));
		add(drop = new Element().color(col_bd).border(col_85).pos(0, FS));
		drop.hide();
		onscroll(si -> change_selection(si.sy() < 0 ? 1 : -1));
	}

	public void drop_hide_clear(){
		drop.hide();
		drop.clearElements(false);
	}

	public DropList onchange(BiConsumer<String, Object> cons){
		consumer = cons;
		return this;
	}

	private void change_selection(int by){
		if(entries.isEmpty()) return;
		current += by;
		if(current < 0) current = entries.size() - 1;
		if(current >= entries.size()) current = 0;
		update_text();
	}

	public void addEntry(String key, Object val){
		entries.add(Pair.of(key, val));
	}

	public void clear(){
		entries.clear();
		text("");
	}

	public void selectEntry(int idx){
		current = idx;
		update_text();
	}

	public void selectEntry(String key){
		int idx = 0;
		for(Pair<String, Object> entry : entries){
			if(entry.getLeft().equals(key)) break;
			idx++;
		}
		selectEntry(idx);
	}

	private void update_text(){
		if(current < 0 || current >= entries.size()){
			text("[Array OOB] " + current);
		}
		else{
			text(entries.get(current).getLeft());
		}
	}

	@Override
	public void onSelect(){
		drop.size(w + FS * 3, entries.size() * FS);
		drop.recompile();
		int idx = 0;
		for(Pair<String, Object> entry : entries){
			drop.add(new TextElm(0, idx++ * FS, drop.w, entry.getLeft(), col_bd)
				.onclick(ci -> {
					selectEntry(entry.getLeft());
					drop_hide_clear();
					//TODO setting if should apply on click
					//TODO setting if should close dropdown
				}));
		}
		drop.show();
	}

	@Override
	protected void onDeselect(Element current){
		drop_hide_clear();
	}

}
