package net.fexcraft.app.fmt.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Logging;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class UpdateHandler {

	public static final HashMap<Class<?>, ArrayList<UpdateHolder>> HOLDERS = new HashMap<>();

	public static void register(UpdateCompound root){
		for(Class<?> event : root.holders.keySet()){
			if(!HOLDERS.containsKey(event)) HOLDERS.put(event, new ArrayList<>());
			HOLDERS.get(event).add(root.holders.get(event));
		}
	}

	public static <E> void update(E event){
		if(Settings.LOG_UPDATES.value){
			Logging.log(event.getClass().getName() + " -> " + event.toString());
		}
		if(!HOLDERS.containsKey(event.getClass())) return;
		HOLDERS.get(event.getClass()).forEach(holder -> holder.update(event));
	}

	public static void deregister(UpdateCompound root){
		for(Class<?> event : root.holders.keySet()){
			if(!HOLDERS.containsKey(event)) continue;
			HOLDERS.get(event).remove(root.holders.get(event));
		}
	}

	public static class UpdateCompound {

		public HashMap<Class<?>, UpdateHolder<?>> holders = new HashMap<>();

		public <E> UpdateHolder<E> get(Class<E> clazz){
			if(holders.containsKey(clazz)) return (UpdateHolder<E>)holders.get(clazz);
			UpdateHolder<E> holder = new UpdateHolder<E>();
			holders.put(clazz, holder);
			return holder;
		}

		public <E> UpdateHolder<E> add(Class<E> clazz, Consumer<E> cons){
			if(holders.containsKey(clazz)) return ((UpdateHolder<E>)holders.get(clazz)).add(cons);
			UpdateHolder<E> holder = new UpdateHolder<E>();
			holders.put(clazz, holder);
			holder.add(cons);
			return holder;
		}

	}

	public static class UpdateHolder<E> {

		public ArrayList<Consumer<E>> consumers = new ArrayList<>();

		public UpdateHolder add(Consumer<E> cons){
			consumers.add(cons);
			return this;
		}

		private void update(E event){
			for(Consumer<E> consumer : consumers){
				consumer.accept(event);
			}
		}

	}

}
