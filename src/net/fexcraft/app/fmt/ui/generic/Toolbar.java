package net.fexcraft.app.fmt.ui.generic;

import java.awt.Desktop;

import org.lwjgl.input.Mouse;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.ModelTree;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.CylinderWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.RGB;

public class Toolbar extends Element {
	
	private RGB subhover = new RGB(218, 232, 104);
	
	public Toolbar(){
		super(null, "toolbar");
		this.height = 30;
		//
		String[] buttons = new String[]{ "Files", "Edit", "Camera", "Shapeditor", "Shapelist", "Textures", "Editor", "Helpers", "Settings", "Exit"};
		for(int i = 0; i < buttons.length; i++){
			final int j = i;
			this.elements.put(buttons[i].toLowerCase(), new Button(this, buttons[i].toLowerCase(), 100, 26, 2 + (j * 102), 2){
				@Override
				protected boolean processButtonClick(int x, int y, boolean left){
					if(left){
						switch(this.id){
							case "exit":{ FMTB.get().close(); return true; }
							case "camera":{ Mouse.setGrabbed(true); return true; }
							case "shapelist":{ ((ModelTree)FMTB.get().UI.getElement("modeltree")).toggleVisibility(); return true; }
							case "shapeditor":{ Editor.show("general_editor"); return true; }
						}
					}
					return false;
				}
				@Override
				public void setupSubmenu(){
					switch(this.id){
						case "files":{
							this.elements.put("menu", new Menulist(this, "menu", 104, 200, (j * 102), 28){
								@Override
								public void addButtons(){
									this.elements.put("new", new Button(this, "new", 100, 26, 2, 2, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											SaveLoad.openNewModel(); return true;
										}
									}.setText("New", false));
									//
									this.elements.put("open", new Button(this, "open", 100, 26, 2, 30, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											SaveLoad.openModel(); return true;
										}
									}.setText("Open..", false));
									//
									this.elements.put("save", new Button(this, "save", 100, 26, 2, 58, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											SaveLoad.saveModel(false); return true;
										}
									}.setText("Save", false));
									//
									this.elements.put("save_as", new Button(this, "save_as", 100, 26, 2, 86, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											SaveLoad.saveModel(true); return true;
										}
									}.setText("Save As..", false));
									//
									this.elements.put("export", new Button(this, "export", 100, 26, 2, 114, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											PorterManager.handleExport(); return true;
										}
									}.setText("Export >>", false));
									//
									this.elements.put("import", new Button(this, "import", 100, 26, 2, 142, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											PorterManager.handleImport(); return true;
										}
									}.setText("Import <<", false));
									this.elements.put("exit", new Button(this, "exit", 100, 26, 2, 170, subhover){
										@Override protected boolean processButtonClick(int x, int y, boolean left){ FMTB.get().close(); return true; }
									}.setText("Exit", false));
								}
							});
							break;
						}
						case "edit":{
							this.elements.put("menu", new Menulist(this, "menu", 104, 200, (j * 102), 28){
								@Override
								public void addButtons(){
									this.elements.put("undo", new Button(this, "undo", 100, 26, 2, 2, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("Undo", false));
									//
									this.elements.put("redo", new Button(this, "redo", 100, 26, 2, 30, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("Redo", false));
									//
									this.elements.put("movesel", new Button(this, "movesel", 100, 26, 2, 58, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("Move Sel.", false));
									//
									this.elements.put("delsel", new Button(this, "delsel", 100, 26, 2, 86, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("Delete Sel.", false));
								}
							});
							break;
						}
						case "editor":{
							this.elements.put("menu", new Menulist(this, "menu", 104, 200, (j * 102), 28){
								@Override
								public void addButtons(){
									this.elements.put("floor", new Button(this, "floor", 100, 26, 2, 2, subhover){
										@Override public void setupSubmenu(){ return; }
										@Override protected boolean processButtonClick(int x, int y, boolean left){ Settings.toggleFloor(); return true; }
									}.setText("Toggle Floor", false));
									//
									this.elements.put("lines", new Button(this, "lines", 100, 26, 2, 30, subhover){
										@Override public void setupSubmenu(){ return; }
										@Override protected boolean processButtonClick(int x, int y, boolean left){ Settings.toggleLines(); return true; }
									}.setText("Line/Border", false));
									//
									this.elements.put("cube", new Button(this, "cube", 100, 26, 2, 58, subhover){
										@Override public void setupSubmenu(){ return; }
										@Override protected boolean processButtonClick(int x, int y, boolean left){ Settings.toggleCube(); return true; }
									}.setText("Center Cube", false));
									//
									this.elements.put("demo", new Button(this, "demo", 100, 26, 2, 86, subhover){
										@Override public void setupSubmenu(){ return; }
										@Override protected boolean processButtonClick(int x, int y, boolean left){ Settings.toggleDemo(); return true; }
									}.setText("Demo Model", false));
									this.elements.put("reset", new Button(this, "reset", 100, 26, 2, 114, subhover){
										@Override public void setupSubmenu(){ return; }
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											FMTB.ggr = new GGR(0, 4, 4); FMTB.ggr.rotation.xCoord = 45;
											return true;
										}
									}.setText("Reset Cam.", false));
								}
							});
							break;
						}
						case "shapeditor":{
							this.elements.put("menu", new Menulist(this, "menu", 104, 200, (j * 102), 28){
								@Override
								public void addButtons(){
									this.elements.put("general", new Button(this, "general", 100, 26, 2, 2, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											Editor.toggle("general_editor"); return true;
										}
									}.setText("General", false));
									//
									this.elements.put("shapebox", new Button(this, "shapebox", 100, 26, 2, 30, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											Editor.toggle("shapebox_editor"); return true;
										}
									}.setText("Shapebox", false));
									//
									this.elements.put("cylinder", new Button(this, "cylinder", 100, 26, 2, 58, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											Editor.toggle("cylinder_editor"); return true;
										}
									}.setText("Cylinder", false));
									//
									this.elements.put("group", new Button(this, "group", 100, 26, 2, 86, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											Editor.toggle("group_editor"); return true;
										}
									}.setText("Group", false));
								}
							});
							break;
						}
						case "shapelist":{
							this.elements.put("menu", new Menulist(this, "menu", 124, 200, (j * 102), 28){
								@Override
								public void addButtons(){
									this.elements.put("add_box", new Button(this, "add_box", 120, 26, 2, 2, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											FMTB.MODEL.add(new BoxWrapper(FMTB.MODEL));
											this.parent.visible = false; return true;
										}
									}.setText("Add Box/Cube", false));
									//
									this.elements.put("add_shapebox", new Button(this, "add_shapebox", 120, 26, 2, 30, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											FMTB.MODEL.add(new ShapeboxWrapper(FMTB.MODEL));
											this.parent.visible = false; return true;
										}
									}.setText("Add Shapebox", false));
									//
									this.elements.put("add_cylinder", new Button(this, "add_cylinder", 120, 26, 2, 58, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											FMTB.MODEL.add(new CylinderWrapper(FMTB.MODEL));
											this.parent.visible = false; return true;
										}
									}.setText("Add Cylinder", false));
									//
									this.elements.put("add_group", new Button(this, "add_group", 120, 26, 2, 86, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											String string = "group" + FMTB.MODEL.getCompound().size();
											if(FMTB.MODEL.getCompound().containsKey(string)){
												string = "group0" + FMTB.MODEL.getCompound().size();
												FMTB.MODEL.getCompound().put(string, new TurboList(string));
											}
											else{
												FMTB.MODEL.getCompound().put(string, new TurboList(string));
											}
											((ModelTree)FMTB.get().UI.getElement("modeltree")).setVisible();
											this.parent.visible = false; return true;
										}
									}.setText("Add Group", false));
								}
							});
							break;
						}
						case "textures":{
							this.elements.put("menu", new Menulist(this, "menu", 104, 200, (j * 102), 28){
								@Override
								public void addButtons(){
									this.elements.put("select", new Button(this, "select", 100, 26, 2, 2, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("Select Tex.", false));
									//
									this.elements.put("edit", new Button(this, "edit", 100, 26, 2, 30, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											if(FMTB.MODEL.texture == null) return true;
											Texture texture = TextureManager.getTexture(FMTB.MODEL.texture, true);
											if(texture == null) return true;
											try{
												if(System.getProperty("os.name").toLowerCase().contains("windows")) {
													String cmd = "rundll32 url.dll,FileProtocolHandler " + texture.getFile().getCanonicalPath();
													Runtime.getRuntime().exec(cmd);
												}
												else{ Desktop.getDesktop().edit(texture.getFile()); }
											}
											catch(Exception e){
												e.printStackTrace();
											}
											return true;
										}
									}.setText("Edit Tex.", false));
									//
									this.elements.put("generate", new Button(this, "generate", 100, 26, 2, 58, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("Generate", false));
									//
									this.elements.put("autopos", new Button(this, "autopos", 100, 26, 2, 86, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("AutoPosition", false));
								}
							});
							break;
						}
						case "settings":{
							this.elements.put("menu", new Menulist(this, "menu", 104, 200, (j * 102), 28){
								@Override
								public void addButtons(){
									this.elements.put("license", new Button(this, "license", 100, 26, 2, 2, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("License", false));
									//
									this.elements.put("controls", new Button(this, "controls", 100, 26, 2, 30, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("Controls", false));
									//
									this.elements.put("console", new Button(this, "console", 100, 26, 2, 58, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("Console", false));
									//
									this.elements.put("Help", new Button(this, "Help", 100, 26, 2, 86, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											//TODO
											return true;
										}
									}.setText("Help", false));
								}
							});
							break;
						}
						case "exit":{
							/*this.elements.put("menu", new Menulist(this, "menu", 154, 200, (j * 102), 28){
								@Override
								public void addButtons(){
									this.elements.put("surr", new Button(this, "surr", 150, 26, 2, 2, subhover){
										@Override
										protected boolean processButtonClick(int x, int y, boolean left){
											this.enabled = false; return true;
										}
									}.setText("Are you sure? (Y/N)", false));
								}
							});*/
							break;
						}
					}
				}
				
			}.setText(buttons[i], true));
		}
	}

	@Override
	public void renderSelf(int rw, int rh){
		this.width = rw;
		this.renderQuad(0, 0, width, height, "ui/background");
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return false;
	}

}
