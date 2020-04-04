package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.BufferedImage;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.SessionHandler;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class ProfileBox {

	public static void open(){
        Dialog dialog = new Dialog(UserInterfaceUtils.translate("profile.title"), 400, 200);
        dialog.setResizable(false);
    	Label label0 = new Label(UserInterfaceUtils.translate("profile.username") + " " + SessionHandler.getUserName(), 10, 10, 380, 20);
    	dialog.getContainer().add(label0);
    	Label label1 = new Label(UserInterfaceUtils.translate("profile.userid") + " " + SessionHandler.getUserId(), 10, 40, 380, 20);
    	dialog.getContainer().add(label1);
    	Label label2 = new Label(UserInterfaceUtils.translate("profile.license") + " " + SessionHandler.getLicenseTitle(), 10, 70, 380, 20);
    	dialog.getContainer().add(label2);
    	//
        Button button0 = new Button(UserInterfaceUtils.translate("dialogbox.button.ok"), 10, 200 - 50, 80, 20);
        button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()) dialog.close();
        });
        dialog.getContainer().add(button0);
        //
        Button button1 = new Button(UserInterfaceUtils.translate("profile.button." + (SessionHandler.isLoggedIn() ? "logout" : "login")), 100, 200 - 50, 80, 20);
        button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){
        		dialog.close();
        		ProfileBox.openLogin();
        	}
        });
        dialog.getContainer().add(button1);
        //
        if(!SessionHandler.isLoggedIn()){
            Button button2 = new Button(UserInterfaceUtils.translate("profile.button.register"), 190, 200 - 50, 80, 20);
            button2.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()){
            		dialog.close();
					if(Desktop.isDesktopSupported()){
						Desktop desktop = Desktop.getDesktop();
						try{
							desktop.browse(new URI("https://fexcraft.net/register"));
						}
						catch(IOException | URISyntaxException er){
							// TODO Auto-generated catch block
							er.printStackTrace();
						}
					}
					else{
						Runtime runtime = Runtime.getRuntime();
						try{
							runtime.exec("xdg-open https://fexcraft.net/register");
						}
						catch(IOException er){
							er.printStackTrace();
						}
					}
            	}
            });
            dialog.getContainer().add(button2);
        }
        //
        ImageView view = new ImageView();
        view.setImage(new BufferedImage("./resources/textures/icon.png"));
        view.setPosition(400 - 10 - 120, 10);
        view.setSize(120, 120);
        dialog.getContainer().add(view);
        //
        dialog.show(FMTB.frame);
	}

	private static void openLogin(){
		if(SessionHandler.isLoggedIn()){
			SessionHandler.tryLogout();
		}
		
	}

}
