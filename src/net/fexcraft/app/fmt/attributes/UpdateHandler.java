package net.fexcraft.app.fmt.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class UpdateHandler {
	
	public static final HashMap<UpdateType, ArrayList<UpdateHolder>> HOLDERS = new HashMap<>();
	static {
		for(UpdateType type : UpdateType.values()) HOLDERS.put(type, new ArrayList<>());
	}
	
	public static void registerHolder(UpdateHolder holder){
		for(UpdateType type : holder.consumers.keySet()){
			//if(!HOLDERS.containsKey(type)) HOLDERS.put(type, new ArrayList<>());
			HOLDERS.get(type).add(holder);
		}
		if(!holder.subs.isEmpty()) holder.subs.forEach(sub -> registerHolder(sub));
	}
	
	public static void update(UpdateType event, Object... value){
		if(!HOLDERS.containsKey(event)) return;
		UpdateWrapper wrapper = new UpdateWrapper(value);
		HOLDERS.get(event).forEach(holder -> holder.update(event, wrapper));
	}

	public static void deregisterHolder(UpdateHolder holder){
		for(UpdateType type : holder.consumers.keySet()){
			if(!HOLDERS.containsKey(type)) continue;
			HOLDERS.get(type).remove(holder);
		}
		if(!holder.subs.isEmpty()) holder.subs.forEach(sub -> deregisterHolder(sub));
	}
	
	public static class UpdateHolder {
		
		public HashMap<UpdateType, Consumer<UpdateWrapper>> consumers = new HashMap<>();
		public ArrayList<UpdateHolder> subs = new ArrayList<>();
		
		public UpdateHolder add(UpdateType event, Consumer<UpdateWrapper> cons){
			consumers.put(event, cons);
			return this;
		}

		private void update(UpdateType event, UpdateWrapper wrapper){
			consumers.get(event).accept(wrapper);
		}

		public UpdateHolder sub(){
			UpdateHolder holder = new UpdateHolder();
			subs.add(holder);
			return holder;
		}
		
	}
	
	public static class UpdateWrapper {
		
		public Object[] objs;

		public UpdateWrapper(Object... objs){
			this.objs = objs;
		}
		
		public <T> T get(int index){
			return (T)objs[index];
		}

		public <T> T get(int index, Class<T> clazz){
			return (T)objs[index];
		}
		
	}

}
