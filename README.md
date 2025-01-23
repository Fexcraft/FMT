# Fex's Modelling Toolbox
A Tool to create polygon/3d-shape based models.

It is able to import:
- `mtb` - SMP Toolbox Save Files
- `jtmt` - JTMT JSON Format Models
- `java` - FlansMod Format Java Models
- `vox` - "MagicaVoxel" Format Files (see [VOXtoFMT](https://github.com/Fexcraft/VOXtoFMT))
- `bbmodel` - Blockbench block models 

And export to:
- `jtmt` - JTMT JSON Format Models
- `fmf` - Fex's Model Format (for FVTM)
- `java`
  - Java Models for **FVTM** (Fex's Vehicle and Transportation Mod)
  - Java Models for **TrainsInMotion** following the FVTM standard.
  - Java Models for **FlansMod**    
    (box, shapebox and basic cylinders using existing MRT methods,    
    everything else using a legacy-mode converter)
- `obj` - valid **Wavefront OBJ** Models
- `png` - **PNG** files - template or textured
- `txt` - **Marker** list exporter
- and more

Other features
- internal texture editor
- auto texture positioner
- unlimited groups*
- unlimited polygons*
- unlimited texture groups*
- various polygon types
  - boxes
  - shapeboxes
  - cylinders
    - hollow cylinders
    - top offset and rotation
    - segments, limit, rotation 
  - markers
    - "biped" display
  - more planned 
- unlimited* amount of loadable helper models or (img)frames
- in-editor texture uv preview, adjustment and customisation
- and more..
  
## Some Preview Images
### Create detailed models and texture them.
![IMG0](http://fexcraft.net/files/app_data/fmt-page/javaw_2020-05-02_17-31-23.png)
### Apply basic per-group animations and preview them.
![IMG1](http://fexcraft.net/files/app_data/fmt-page/javaw_2020-05-02_17-32-03.png)
### "Unlimited" groups and polygons.
![IMG2](http://fexcraft.net/files/app_data/fmt-page/javaw_2020-05-02_17-32-38.png)
### Load in additional "helper/preview" models, as many you may need.
![IMG3](http://fexcraft.net/files/app_data/fmt-page/javaw_2020-05-02_17-34-09.png)
### Work with multiple textures at once, one per model or per any group.
![IMG4](http://fexcraft.net/files/app_data/fmt-page/javaw_2020-05-02_17-35-10.png)



## All rights reserved until further notice.
A proper license is being written soon, till then, touch not.   
Libraries are under their respective licenses, some may have license notices bundled together in the jar/source,    
and in case not, google shall help you.   
FCL is licensed under http://fexcraft.net/license?id=mods (till/if it's respective standalone license is written)

## Installation (v2)
- Windows / Linux     
  Take the zip file from https://github.com/Fexcraft/FMT-Standalone/releases    
  And unpack it into an own folder, done. Now just run the FMT jar file.    
- Mac     
  Check out TurboDefender's instructions [here](https://gist.github.com/RishiMenon2004/4343dc7debbd44379a8f43e930bd3218)!

### Installation (v3)
> Warning:
> - FMT3 is still in development and various issues may occur during installation/usage.
> - FMT3 save files may not be completely compatible with FMT2 save files.
> - Please make backups before using files from FMT2.
1. Download the [FMT Updater](https://fexcraft.net/files/app_data/fmt/FMT_Updater.jar)
2. Put it into an own folder.
3. Run the Updater.
4. Refresh the Catalog (do this each time you want to search for updates).
5. Click on "Update" (if there is an update available)

6. Once FMT has downloaded (or updated) you can run the FMT.jar directly

### Known Issues/Requirements
- FMT 2 does not work on Java version from 9 to 14, it does work on version 8 or 15 and up.
- FMT 3 will work on Java 17 and newer, if you have a Java 8 installation and want to keep it untouched/intact, try [this video](https://youtu.be/oSDV-xjE7YU), it shows how to have a secondary Java installation for FMT or similar.
- FMT 3 works only on 64bit (x86_64) system a this moment because that's the default LWJGL natives shipped with it.

## Discord
[https://discord.gg/J5c2zJ7uU9](https://discord.gg/J5c2zJ7uU9)
