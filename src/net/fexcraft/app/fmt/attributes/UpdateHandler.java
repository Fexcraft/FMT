package net.fexcraft.app.fmt.attributes;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateHandler {
	
	public static final HashMap<UpdateType, ArrayList<UpdateHolder>> HOLDERS = new HashMap<>();
	
	public static void registerHolder(UpdateHolder holder){
		if(holder == null) return;
		for(UpdateType type : holder.consumers.keySet()){
			if(!HOLDERS.containsKey(type)) HOLDERS.put(type, new ArrayList<>());
			HOLDERS.get(type).add(holder);
		}
	}
	
	public static void update(UpdateType event, Object value){
		HOLDERS.get(event).forEach(holder -> holder.update(event, value, false));
		if(event.run_groups != null){
			for(UpdateType type : UpdateType.values()){
				if(type.containsAny(event.run_groups)){
					HOLDERS.get(type).forEach(holder -> holder.update(event, value, true));
				}
			}
		}
	}

	public static void deregisterHolder(UpdateHolder holder){
		if(holder == null) return;
		for(UpdateType type : holder.consumers.keySet()){
			if(!HOLDERS.containsKey(type)) continue;
			HOLDERS.get(type).remove(holder);
		}
	}
	
	@FunctionalInterface
	public static interface UpdateConsumer {
		
		public <T> void update(T value, UpdateType from, T _value);
		
	}
	
	public static class UpdateHolder {
		
		public HashMap<UpdateType, UpdateConsumer> consumers = new HashMap<>();
		
		public UpdateHolder add(UpdateType event, UpdateConsumer cons){
			consumers.put(event, cons);
			return this;
		}

		private void update(UpdateType event, Object value, boolean bool){
			if(bool) consumers.get(event).update(null, event, value);
			else consumers.get(event).update(value, null, null);
		}
		
	}

}
