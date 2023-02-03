package net.fexcraft.app.fmt.port.ex;

import net.fexcraft.app.fmt.settings.Setting;

import java.util.ArrayList;

public class ExSetList extends ArrayList<Setting<?>> {

    public Setting<?> g(String str){
        for(Setting<?> setting : this){
            if(setting.id.equals(str)) return setting;
        }
        return null;
    }

}
