package net.fexcraft.app.fmt.attributes;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateHandler {
	
	public static final HashMap<UpdateType, ArrayList<UpdateConsumer>> LISTENERS = new HashMap<>();
	
	public static void register(UpdateType event, UpdateConsumer cons){
		if(!LISTENERS.containsKey(event)) LISTENERS.put(event, new ArrayList<>());
		LISTENERS.get(event).add(cons);
	}
	
	public static void update(UpdateType event, Object value){
		if(!LISTENERS.containsKey(event)) return;
		LISTENERS.get(event).forEach(cons -> cons.update(value, null, null));
		if(event.run_groups != null){
			for(UpdateType type : UpdateType.values()){
				if(type.containsAny(event.run_groups)){
					LISTENERS.get(type).forEach(cons -> cons.update(null, event, value));
				}
			}
		}
	}
	
	@FunctionalInterface
	public static interface UpdateConsumer {
		
		public <T> void update(T value, UpdateType from, T _value);
		
	}

}
