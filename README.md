# Fex's Modelling Toolbox
A Tool to create Models basing on the "TMT" (updated and maintained version of Fex/FCL) Library.

It is able to import:
- `mtb` - SMP Toolbox Save Files
- `jtmt` - JTMT JSON Format Models
- `java` - FlansMod Format Java Models (**currently not public**)
- `vox` - "MagicaVoxel" Format Files (see [VOXtoFMT](https://github.com/Fexcraft/VOXtoFMT))

And export to:
- `jtmt` - JTMT JSON Format Models
- `java`
  - Java Models for **FVTM** (Fex's Vehicle and Transportation Mod)
  - Java Models for **TrainsInMotion** following the FVTM standard.
  - Java Models for **FlansMod**    
    (box, shapebox and basic cylinders using existing MRT methods,    
    everything else using a legacy-mode converter)
- `obj` - valid **Wavefront OBJ** Models
- `tsiv` - small util for **MTS/IV** rotation points
- `png` - **PNG** files - template or textured
- `txt` - **Marker** list exporter
- and more to come!

Other features
- internal texture editor (beta)
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
  - texrect a/b (experimental)
  - simple quads (experimental)
  - markers
    - "biped" display
  - and maybe in the future more
- unlimited* amount of loadable helper models or (img)frames
- in-editor texture uv preview, adjustment and customisation
- and more..
  
## Some Preview Images
### Create detailed models and texture them.
![IMG0](https://cdn.discordapp.com/attachments/365628561819697162/706167477960179723/javaw_2020-05-02_17-31-23.png)
### Apply basic per-group animations and preview them.
![IMG1](https://cdn.discordapp.com/attachments/365628561819697162/706167493751865364/javaw_2020-05-02_17-32-03.png)
### "Unlimited" groups and polygons.
![IMG2](https://cdn.discordapp.com/attachments/365628561819697162/706167508197179492/javaw_2020-05-02_17-32-38.png)
### Load in additional "helper/preview" models, as many you may need.
![IMG3](https://cdn.discordapp.com/attachments/365628561819697162/706167518489739314/javaw_2020-05-02_17-34-09.png)
### Work with multiple textures at once, one per model or per any group.
![IMG4](https://cdn.discordapp.com/attachments/365628561819697162/706167535476801666/javaw_2020-05-02_17-35-10.png)



## All rights reserved until further notice.
A proper license is being written soon, till then, touch not.   
Libraries are under their respective licenses, some may have license notices bundled together in the jar/source,    
and in case not, google shall help you.   
FCL is licensed under http://fexcraft.net/license?id=mods (till/if it's respective standalone license is written)

## Installation
- Windows / Linux     
  Take the zip file from https://github.com/Fexcraft/FMT-Standalone/releases    
  And unpack it into an own folder, done. Now just run the FMT jar file.    
  **Java 8 is required.** Reported to not run on newer java yet.    
- Mac     
  Check out TurboDefender's instructions [here](https://gist.github.com/RishiMenon2004/4343dc7debbd44379a8f43e930bd3218)!

## Discord
https://discord.gg/AkMAzaA
