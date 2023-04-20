package net.fexcraft.app.fmt.update;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.ModelOrientation;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.utils.Picker.PickTask;
import net.fexcraft.app.fmt.utils.Picker.PickType;

import java.io.File;

public class UpdateEvent {

    public static record PolygonAdded(Group group, Polygon polygon){};
    public static record PolygonRenamed(Polygon polygon, String name){};
    public static record PolygonRemoved(Group group, Polygon polygon){};
    public static record PolygonSelected(Polygon polygon, int prevselected, int selected){};
    public static record PolygonVisibility(Polygon polygon, boolean visible){};
    public static record PolygonValueEvent(Polygon polygon, PolygonValue value, boolean first){};

    public static record GroupAdded(Model model, Group group){};
    public static record GroupRenamed(Group group, String name){};
    public static record GroupRemoved(Model model, Group group){};
    public static record GroupSelected(Group group, int prevselected, int selected){};
    public static record GroupVisibility(Group group, boolean visible){};

    public static record ModelAuthor(Model model, String name){};
    public static record ModelLoad(Model model){};
    public static record ModelUnload(Model model){};
    public static record ModelRenamed(Model model, String oldname, String newname){};
    public static record ModelTexGroup(Model model, TextureGroup group){};
    public static record ModelTextureSize(Model model, int x, int y){};
    public static record ModelOrientEvent(Model model, ModelOrientation orientation){};
    public static record ModelExportValue(Model model, String name, String value, boolean list){};

    public static record TexGroupAdded(TextureGroup group){};
    public static record TexGroupRenamed(TextureGroup group, String oldname, String newname){};
    public static record TexGroupRemoved(TextureGroup group){};

    public static record EditorRate(float rate){};
    public static record EditorCreated(Editor editor){};
    public static record EditorRemoved(Editor editor){};

    public static record WorkspaceName(String oldname, String newname){};
    public static record WorkspaceRoot(File oldroot, File newroot){};

    public static record PainterColor(Integer value, boolean primary){};
    public static record PainterTool(TexturePainter.Tool tool, TexturePainter.Selection selection){};
    public static record PickMode(PickType type, PickTask task, boolean offcenter){};
    public static record PickFace(Polygon polygon, Face face){};

}
