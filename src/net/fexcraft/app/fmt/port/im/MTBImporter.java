package net.fexcraft.app.fmt.port.im;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MTBImporter implements Importer {

    public static FileChooser.FileType TYPE_MTB = new FileChooser.FileType("SMP Toolbox V2 Save File", "*.mtb");
    private static final List<String> categories = Arrays.asList("model");

    @Override
    public String id() {
        return "mtb";
    }

    @Override
    public String name() {
        return ".MTB (SMP Toolbox V2)";
    }

    @Override
    public FileChooser.FileType extensions() {
        return TYPE_MTB;
    }

    @Override
    public List<String> categories() {
        return categories;
    }

    @Override
    public List<Setting<?>> settings() {
        return Collections.emptyList();
    }

    @Override
    public String _import(Model model, File file) {
        return "//TODO";
    }

}
