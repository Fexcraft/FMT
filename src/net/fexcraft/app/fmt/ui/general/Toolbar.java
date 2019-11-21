package net.fexcraft.app.fmt.ui.general;

import java.awt.Desktop;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.general.FileSelector.AfterTask;
import net.fexcraft.app.fmt.ui.general.FileSelector.ChooserMode;
import net.fexcraft.app.fmt.ui.general.FileSelector.FileRoot;
import net.fexcraft.app.fmt.ui.tree.RightTree;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.ImageHelper;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.utils.TextureUpdate;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.CylinderWrapper;
import net.fexcraft.app.fmt.wrappers.MarkerWrapper;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;
import net.fexcraft.app.fmt.wrappers.TexrectWrapperA;
import net.fexcraft.app.fmt.wrappers.TexrectWrapperB;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.utils.Print;

public class Toolbar extends Element {

	public Toolbar(){
		super(null, "toolbar", "toolbar"); hovercolor = RGB.WHITE;
		this.setPosition(0, 0).setSize(100, 30).setColor(0xff484848).setBorder(0xff32a852, 0xffeb4034, 3, false, true);
		//
		this.elements.add(new Icon(this, "info", "toolbar:icon", "icons/toolbar/info", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				DialogBox.notAvailableYet(); return true;
			}
		});
		this.elements.add(new Icon(this, "new_file", "toolbar:icon", "icons/toolbar/new", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.openNewModel(); return true;
			}
		});
		this.elements.add(new Icon(this, "open_file", "toolbar:icon", "icons/toolbar/open", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.openModel(); return true;
			}
		});
		this.elements.add(new Icon(this, "save_file", "toolbar:icon", "icons/toolbar/save", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.saveModel(false, false); return true;
			}
		});
		this.elements.add(new Icon(this, "profile", "toolbar:icon", "icons/toolbar/profile", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				DialogBox.notAvailableYet(); return true;
			}
		});
		this.elements.add(new Icon(this, "settings", "toolbar:icon", "icons/toolbar/settings", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				UserInterface.SETTINGSBOX.show(); return true;
			}
		});
		//
		int btsz = 96, bthg = 28;
		this.elements.add(new Button(this, "file", null, btsz, bthg, 0, 0){
			@Override
			public void setupSubmenu(){
				HoverMenu menu = new HoverMenu(this, "menu", 100){
					@Override
					public void addButtons(){
						this.elements.add(new Button(this, "new_model", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								SaveLoad.openNewModel(); return true;
							}
						}.setText(translate("toolbar.file.new_model", "New Model"), false));
						this.elements.add(new Button(this, "open", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								SaveLoad.openModel(); return true;
							}
						}.setText(translate("toolbar.file.open_model", "Open Model"), false));
						this.elements.add(new Button(this, "save", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								SaveLoad.saveModel(false, false); return true;
							}
						}.setText(translate("toolbar.file.save_model", "Save Model"), false));
						this.elements.add(new Button(this, "save_as", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								SaveLoad.saveModel(true, false); return true;
							}
						}.setText(translate("toolbar.file.save_as", "Save as..."), false));
						this.elements.add(new Button(this, "import", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								PorterManager.handleImport(); return true;
							}
						}.setText(translate("toolbar.file.import", "Import <<"), false));
						this.elements.add(new Button(this, "export", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								PorterManager.handleExport(); return true;
							}
						}.setText(translate("toolbar.file.export", "Export >>"), false));
						this.elements.add(new Button(this, "exit", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								SaveLoad.checkIfShouldSave(true, false); return true;
							}
						}.setText(translate("toolbar.file.exit", "Exit"), false));
					}
				};
				this.elements.add(menu);
			}
		}.setText(translate("toolbar.file", "File"), true));
		this.elements.add(new Button(this, "utils", null, btsz, bthg, 0, 0){
			@Override
			public void setupSubmenu(){
				HoverMenu menu = new HoverMenu(this, "menu", 100){
					@Override
					public void addButtons(){
						this.elements.add(new Button(this, "undo", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								DialogBox.notAvailableYet(); return true;
							}
						}.setText(translate("toolbar.utils.undo", "Undo"), false));
						this.elements.add(new Button(this, "redo", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								DialogBox.notAvailableYet(); return true;
							}
						}.setText(translate("toolbar.utils.redo", "Redo"), false));
						this.elements.add(new Button(this, "create_gif", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								ImageHelper.createGif(false); return true;
							}
						}.setText(translate("toolbar.utils.create_gif", "Create Gif"), false));
						this.elements.add(new Button(this, "screenshot", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								ImageHelper.takeScreenshot(true); return true;
							}
						}.setText(translate("toolbar.utils.sceenshot", "Screenshot"), false));
						this.elements.add(new Button(this, "reset", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								FMTB.ggr = new GGR(FMTB.get(), 0, 4, 4); FMTB.ggr.rotation.xCoord = 45;
								return true;
							}
						}.setText(translate("toolbar.utils.reset_camera", "Reset Camera"), false));
						this.elements.add(new Button(this, "calc_size", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								long l = 0, ll = 0;
								for(TurboList list : FMTB.MODEL.getGroups()){
									for(PolygonWrapper wrapper : list){
										l += jdk.nashorn.internal.ir.debug.ObjectSizeCalculator.getObjectSize(wrapper.getTurboObject(0));
									}
								}
								ll = jdk.nashorn.internal.ir.debug.ObjectSizeCalculator.getObjectSize(FMTB.MODEL);
								Print.console("MainClass: " + Settings.byteCountToString(jdk.nashorn.internal.ir.debug.ObjectSizeCalculator.getObjectSize(FMTB.get()), true));
								FMTB.showDialogbox("Size (MC / Editor): \n" + Settings.byteCountToString(l, true) + " // " + Settings.byteCountToString(ll, true), "ok", null, DialogBox.NOTHING, null);
								//
								long uis = jdk.nashorn.internal.ir.debug.ObjectSizeCalculator.getObjectSize(FMTB.get().UI);
								Print.console("UI: " + Settings.byteCountToString(uis, true));
								return true;
							}
						}.setText(translate("toolbar.utils.calc_size", "Calc. Size"), false));
					}
				};
				this.elements.add(menu);
			}
		}.setText(translate("toolbar.utils", "Utils"), true));
		this.elements.add(new Button(this, "editor", null, btsz, bthg, 0, 0){
			@Override
			public void setupSubmenu(){
				HoverMenu menu = new HoverMenu(this, "menu", 100){
					@Override
					public void addButtons(){
						this.elements.add(new Button(this, "copy_selection", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								FMTB.MODEL.copyAndSelect(); return true;
							}
						}.setText(translate("toolbar.editor.copy_selected", "Copy Selected"), false));
						for(int i = 0; i < 3; i++){
							String str = i == 0 ? "x" : i == 1 ? "y" : "z"; int j = i; String[] arr = new String[]{
								translate("toolbar.editor.flip.left_right", "[L/R]"),
								translate("toolbar.editor.flip.up_down", "[U/D]"),
								translate("toolbar.editor.flip.front_back", "[F/B]")
							};
							this.elements.add(new Button(this, "flip_" + str, null, 20, 26, 0, 0){
								@Override
								public boolean processButtonClick(int x, int y, boolean left){ FMTB.MODEL.flipShapeboxes(j); return true; }
							}.setText(format("toolbar.editor.flip", "Flip (%s) %s", str.toUpperCase(), arr[j]), false));
						}
						this.elements.add(new Button(this, "controls", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								UserInterface.CONTROLS.show(); return true;
							}
						}.setText(translate("toolbar.editor.controls", "Controls"), false));
					}
				};
				this.elements.add(menu);
			}
		}.setText(translate("toolbar.editor", "Editor"), true));
		this.elements.add(new Button(this, "shapeditor", null, btsz, bthg, 0, 0){
			@Override
			public void setupSubmenu(){
				HoverMenu menu = new HoverMenu(this, "menu", 100){
					@Override
					public void addButtons(){
						this.elements.add(new Button(this, "toggle", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								Editor.toggleAll(); return true;
							}
						}.setText(translate("toolbar.shapeditor.hide", "Hide Editors"), false));
						this.elements.add(new Button(this, "general", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								Editor.show("general_editor"); return true;
							}
						}.setText(translate("toolbar.shapeditor.general", "General Editor"), false));
						this.elements.add(new Button(this, "texture", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								Editor.show("texture_editor"); return true;
							}
						}.setText(translate("toolbar.shapeditor.texture", "Texture Editor (beta)"), false));
						this.elements.add(new Button(this, "model_group", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								Editor.show("model_group_editor"); return true;
							}
						}.setText(translate("toolbar.shapeditor.model_group", "Model/Group Editor"), false));
					}
				};
				this.elements.add(menu);
			}
		}.setText(translate("toolbar.shapeditor", "Shapeditor"), true));
		this.elements.add(new Button(this, "shapelist", null, btsz, bthg, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				RightTree.toggle("modeltree"); return true;
			}
			@Override
			public void setupSubmenu(){
				HoverMenu menu = new HoverMenu(this, "menu", 100){
					@Override
					public void addButtons(){
						this.elements.add(new Button(this, "add_box", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								FMTB.MODEL.add(new BoxWrapper(FMTB.MODEL), null, true);
								this.root.setVisible(false); return true;
							}
						}.setText(translate("toolbar.shapelist.add_box", "Add Generic Box"), false));
						this.elements.add(new Button(this, "add_shapebox", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								FMTB.MODEL.add(new ShapeboxWrapper(FMTB.MODEL), null, true);
								this.root.setVisible(false); return true;
							}
						}.setText(translate("toolbar.shapelist.add_shapebox", "Add Shapebox"), false));
						this.elements.add(new Button(this, "add_texrectb", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								FMTB.MODEL.add(new TexrectWrapperB(FMTB.MODEL), null, true);
								this.root.setVisible(false); return true;
							}
						}.setText(translate("toolbar.shapelist.add_texrect_b", "Add TexRect [Basic]"), false));
						this.elements.add(new Button(this, "add_texrecta", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								FMTB.MODEL.add(new TexrectWrapperA(FMTB.MODEL), null, true);
								this.root.setVisible(false); return true;
							}
						}.setText(translate("toolbar.shapelist.add_texrect_a", "Add TexRect [Adv.]"), false));
						this.elements.add(new Button(this, "add_cylinder", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								FMTB.MODEL.add(new CylinderWrapper(FMTB.MODEL), null, true);
								this.root.setVisible(false); return true;
							}
						}.setText(translate("toolbar.shapelist.add_cylinder", "Add Cylinder"), false));
						this.elements.add(new Button(this, "add_group", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								String string = "group" + FMTB.MODEL.getGroups().size();
								if(FMTB.MODEL.getGroups().contains(string)){
									string = "group0" + FMTB.MODEL.getGroups().size();
									FMTB.MODEL.getGroups().add(new TurboList(string));
								}
								else{
									FMTB.MODEL.getGroups().add(new TurboList(string));
								}
								RightTree.show("modeltree");
								this.root.setVisible(false); return true;
							}
						}.setText(translate("toolbar.shapelist.add_group", "Add Group"), false));
						this.elements.add(new Button(this, "add_marker", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								FMTB.MODEL.add(new MarkerWrapper(FMTB.MODEL), "markers", true);
								this.root.setVisible(false); return true;
							}
						}.setText(translate("toolbar.shapelist.add_marker", "Add Marker"), false));
					}
				};
				this.elements.add(menu);
			}
		}.setText(translate("toolbar.shapelist", "Shapelist"), true));
		this.elements.add(new Button(this, "textures", null, btsz, bthg, 0, 0){
			@Override
			public void setupSubmenu(){
				HoverMenu menu = new HoverMenu(this, "menu", 100){
					@Override
					public void addButtons(){
						this.elements.add(new Button(this, "select", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								UserInterface.FILECHOOSER.show("Select a texture file.", null, null, null, FileRoot.TEXTURES, new AfterTask(){
									@Override
									public void run(){
										String name = file.getPath(); TextureManager.loadTextureFromFile(name, file); FMTB.MODEL.setTexture(name);
										//
										/*Texture tex = TextureManager.getTexture(name, true); if(tex == null) return;
										if(tex.getWidth() > FMTB.MODEL.textureX) FMTB.MODEL.textureX = tex.getWidth();
										if(tex.getHeight() > FMTB.MODEL.textureY) FMTB.MODEL.textureY = tex.getHeight();*/
									}
								}, ChooserMode.PNG); return true;
							}
						}.setText(translate("toolbar.textures.select", "Select Texture"), false));
						this.elements.add(new Button(this, "edit", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								if(FMTB.MODEL.texture == null) return true;
								Texture texture = TextureManager.getTexture(FMTB.MODEL.texture, true);
								if(texture == null) return true;
								try{
									if(System.getProperty("os.name").toLowerCase().contains("windows")) {
										String cmd = "rundll32 url.dll,FileProtocolHandler " + texture.getFile().getCanonicalPath();
										Runtime.getRuntime().exec(cmd);
									}
									else{ Desktop.getDesktop().edit(texture.getFile()); }
								} catch(Exception e){ e.printStackTrace(); } return true;
							}
						}.setText(translate("toolbar.textures.edit", "Edit (External)"), false));
						this.elements.add(new Button(this, "remove", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								if(FMTB.MODEL.texture != null && TextureManager.getTexture(FMTB.MODEL.texture, true) != null){
									FMTB.MODEL.setTexture(null); TextureManager.removeTexture(FMTB.MODEL.texture);
								} return true;
							}
						}.setText(translate("toolbar.textures.remove", "Remove/Unload"), false));
						this.elements.add(new Button(this, "texreset", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								FMTB.MODEL.getGroups().forEach(list -> list.forEach(turbo -> {
									turbo.textureX = 0; turbo.textureY = 0; turbo.recompile();
								}));
								FMTB.showDialogbox("Texture Positions Reset.", "ok",  null, DialogBox.NOTHING, null); return true;
							}
						}.setText(translate("toolbar.textures.texpos_reset", "Reset TexPos."), false));
						this.elements.add(new Button(this, "autopos", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								TextureUpdate.tryAutoPos(null); return true;
							}
						}.setText(translate("toolbar.textures.auto_position", "Auto Position"), false));
						this.elements.add(new Button(this, "generate", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								String texname = "temp/" + FMTB.MODEL.name;
								FMTB.MODEL.setTexture(texname);
		                    	TextureManager.newBlankTexture(texname, null);
		                    	Texture tex = TextureManager.getTexture(texname, true);
		                    	FMTB.MODEL.textureScale = 1; FMTB.MODEL.updateFields();
		                    	FMTB.MODEL.getGroups().forEach(elm -> elm.forEach(poly -> poly.burnToTexture(tex.getImage(), null)));
		                    	TextureManager.saveTexture(texname); tex.reload(); FMTB.MODEL.recompile();
								return true;
							}
						}.setText(translate("toolbar.textures.generate", "Generate New"), false));
					}
				};
				this.elements.add(menu);
			}
		}.setText(translate("toolbar.textures", "Texture"), true));
		this.elements.add(new Button(this, "helpers", null, btsz, bthg, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				RightTree.toggle("helpertree"); return true;
			}
			@Override
			public void setupSubmenu(){
				HoverMenu menu = new HoverMenu(this, "menu", 100){
					@Override
					public void addButtons(){
						this.elements.add(new Button(this, "view", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								RightTree.show("helpertree"); return true;
							}
						}.setText(translate("toolbar.helpers.view", "View Loaded"), false));
						this.elements.add(new Button(this, "fmtb", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								UserInterface.FILECHOOSER.show("Select a Preview/Helper file.", null, null, null, FileRoot.HELPERS, new AfterTask(){
									@Override public void run(){ HelperCollector.loadFMTB(file); }
								}, ChooserMode.SAVEFILE_LOAD); return true;
							}
						}.setText(translate("toolbar.helpers.load_fmtb", "Load FMTB"), false));
						this.elements.add(new Button(this, "frame", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								UserInterface.FILECHOOSER.show("Select an Image file.", null, null, null, FileRoot.HELPERS, new AfterTask(){
									@Override public void run(){ HelperCollector.loadFrame(file); }
								}, ChooserMode.HELPFRAMEIMG); return true;
							}
						}.setText(translate("toolbar.helpers.load_frame", "Load Frame"), false));
						this.elements.add(new Button(this, "import", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								UserInterface.FILECHOOSER.show("Select a Preview/Helper file.", null, null, null, FileRoot.HELPERS, new AfterTask(){
									@Override public void run(){ HelperCollector.load(file, porter, mapped_settings); }
								}, ChooserMode.IMPORT); return true;
							}
						}.setText(translate("toolbar.helpers.load_imported", "Load Imported"), false));
						this.elements.add(new Button(this, "clear", null, 20, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								HelperCollector.LOADED.clear(); return true;
							}
						}.setText(translate("toolbar.helpers.unload_clear", "Clear All / Unload"), false));
					}
				};
				this.elements.add(menu);
			}
		}.setText(translate("toolbar.helpers", "Helpers"), true));
		this.elements.add(new Button(this, "exit", null, btsz, bthg, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.checkIfShouldSave(true, false); return true;
			}
		}.setText(translate("toolbar.exit", "Exit"), true));
		//
		realign();
	}

	private void realign(){
		int start = 4, high = 3;
		for(Element elm : elements){
			if(elm.getId().equals("exit") && start + elm.width >= width - 4){ elm.setVisible(false); continue; }
			if(start + elm.width >= width - 4){ start = 4; high += 34; } elm.setVisible(true);
			elm.setPosition(start, elm instanceof Icon ? high - 2 : high); start += elm.width + 2;
		}
		this.height = high + 31; for(Element elm : elements) elm.repos();
	}
	
	@Override
	public Element repos(){
		width = UserInterface.width; x = xrel; y = yrel; realign(); return this.clearVertexes();
	}

}
