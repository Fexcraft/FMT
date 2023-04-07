package net.fexcraft.app.fmt.ui;

import java.io.File;
import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.utils.fvtm.BlockConfigReference;
import net.fexcraft.app.fmt.utils.fvtm.ConfigEntry;
import net.fexcraft.app.fmt.utils.fvtm.EntryType;
import net.fexcraft.app.fmt.utils.fvtm.Reference;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent;
import org.liquidengine.legui.style.Style;

public class UVEditor extends Widget {

    public static UVEditor INSTANCE;

    private UVEditor(){
        getTitleTextState().setText(Translator.translate("uveditor.title"));
        setSize(800, 400);
        setPosition(FMT.WIDTH / 2 - 400, FMT.HEIGHT / 2 - 200);
        //

        //
        addWidgetCloseEventListener(lis -> {});
        FMT.FRAME.getContainer().add(this);
        show();
    }

    public static void toggle(){
       if(INSTANCE == null) INSTANCE = new UVEditor();
       else if(INSTANCE.getStyle().getDisplay() == Style.DisplayType.FLEX) INSTANCE.hide();
       else INSTANCE.show();
    }

}
