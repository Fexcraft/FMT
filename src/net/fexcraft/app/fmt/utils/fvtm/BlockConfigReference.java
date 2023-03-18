package net.fexcraft.app.fmt.utils.fvtm;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockConfigReference implements Reference {

    public static BlockConfigReference INSTANCE = new BlockConfigReference();
    private ArrayList entries = new ArrayList();

    public BlockConfigReference(){
        entries.add(of("Addon", PACKID).required());
        entries.add(of("RegistryName", TEXT).required());
        entries.add(of("Name", TEXT).def("Unnamed Block", true));
        entries.add(of("Description", ARRAY_OR_TEXT));
        entries.add(of("Textures", ARRAY_OR_TEXT).add(of(TEXLOC)));
        entries.add(of("Colors", OBJECT_KEY_VAL).add(of(COLOR)));
        entries.add(of("MaxItemStackSize", INTEGER).limit(64, 0, 64));
        entries.add(of("ItemBurnTime", INTEGER).limit(0, 0));
        entries.add(of("OreDictionary", TEXT));
        entries.add(of("Model", MODELLOC));
        entries.add(of("AABBs", OBJECT_KEY_VAL).add(of(TEXT)).add(of(ARRAY).size(6).add(of(TEXT), of(INTEGER), of(INTEGER), of(INTEGER), of(INTEGER), of(INTEGER))));
        entries.add(of("BlockType", ENUM).enums(BLOCK_TYPES).def("GENERIC_SIMPLE", true));
        entries.add(of("Material", TEXT).def("ROCK", true));//TODO
        entries.add(of("MapColor", TEXT).def("STONE", true));//TODO
        entries.add(of("Hardness", DECIMAL).limit(0f, 1f));
        entries.add(of("LightLevel", DECIMAL));
        entries.add(of("Resistance", DECIMAL));
        entries.add(of("LightOpacity", DECIMAL));
        entries.add(of("HarverestTool", ARRAY).size(2).add(of(TEXT).def("pickaxe", true), of(INTEGER).limit(0, 0, 5)));
        entries.add(of("CollisionDamage", DECIMAL));
        entries.add(of("WebLike", BOOLEAN).def(false));
        entries.add(of("FullBlock", BOOLEAN).def(true));
        entries.add(of("FullCube", BOOLEAN).def(true));
        entries.add(of("Opaque", BOOLEAN).def(false));
        entries.add(of("RenderTranslucent", BOOLEAN).def(false));
        entries.add(of("Invisible", BOOLEAN).def(false));
        entries.add(of("HideItem", BOOLEAN).def(false));
        entries.add(of("CreativeTab", TEXT).def("default", true));
        entries.add(of("ItemTexture", TEXT));
        entries.add(of("DisableItem3DModel", BOOLEAN).def(false));
        entries.add(of("RandomRotation", BOOLEAN).def(false));
        entries.add(of("Functions", ARRAY));//TODO
        entries.add(of("Tickable", BOOLEAN).def(false));
        entries.add(of("MultiSubBlock", BOOLEAN).def(false));
        entries.add(of("HasBlockEntity", BOOLEAN).def(false));
        entries.add(of("WireRelay", OBJECT));//TODO
    }

    @Override
    public List<ConfigEntry> getEntries() {
        return entries;
    }

    public static String[] BLOCK_TYPES = {
            "GENERIC_4ROT", "GENERIC_4X4ROT", "GENERIC_16ROT",
            "GENERIC_SIMPLE",
            "GENERIC_2VAR", "GENERIC_3VAR", "GENERIC_4VAR", "GENERIC_5VAR",
            "GENERIC_6VAR", "GENERIC_7VAR", "GENERIC_8VAR", "GENERIC_9VAR",
            "GENERIC_10VAR", "GENERIC_11VAR", "GENERIC_12VAR", "GENERIC_13VAR",
            "GENERIC_14VAR", "GENERIC_15VAR", "GENERIC_16VAR",
            "GENERIC_ROAD",
            "MULTIBLOCK_4ROT",
            "SIGNAL_4ROT", "SIGNAL_16ROT",
            "FORK2_SWITCH_4ROT", "FORK3_SWITCH_4ROT", "DOUBLE_SWITCH_4ROT"
    };

}
