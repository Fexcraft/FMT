package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.CheckBox;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.PasswordInput;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.StbBackedLoadableImage;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.SessionHandler;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class ProfileDialog {

	public static void open(){
		Dialog dialog = new Dialog(translate("profile.title"), 400, 200);
		dialog.setResizable(false);
		Label label0 = new Label(translate("profile.username") + " " + SessionHandler.getUserName(), 10, 10, 380, 20);
		Label label1 = new Label(translate("profile.userid") + " " + SessionHandler.getUserId(), 10, 40, 380, 20);
		Label label2 = new Label(translate("profile.license") + " " + SessionHandler.getLicenseTitle(), 10, 70, 380, 20);
		dialog.getContainer().add(label0);
		dialog.getContainer().add(label1);
		dialog.getContainer().add(label2);
		//
		Button button0 = new Button(translate("dialog.button.ok"), 10, 200 - 50, 80, 20);
		button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()) dialog.close();
		});
		dialog.getContainer().add(button0);
		//
		Button button1 = new Button(translate("profile.button." + (SessionHandler.isLoggedIn() ? "logout" : "login")), 100, 200 - 50, 80, 20);
		button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()){
				dialog.close();
				ProfileDialog.openLogin();
			}
		});
		dialog.getContainer().add(button1);
		//
		if(!SessionHandler.isLoggedIn()){
			Button button2 = new Button(translate("profile.button.register"), 190, 200 - 50, 80, 20);
			button2.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
				if(CLICK == e.getAction()){
					dialog.close();
					SessionHandler.openRegister();
				}
			});
			dialog.getContainer().add(button2);
		}
		//
		ImageView view = new ImageView();
		view.setImage(new StbBackedLoadableImage("./resources/textures/icon.png"));
		view.setPosition(400 - 10 - 120, 10);
		view.setSize(120, 120);
		Settings.applyBorderless(view);
		dialog.getContainer().add(view);
		//
		dialog.show(FMT.FRAME);
	}

	private static void openLogin(){
		if(SessionHandler.isLoggedIn()){
			SessionHandler.tryLogout();
		}
		Component mail;
		Dialog dialog = new Dialog(translate("loginbox.title"), 400, 200);
		dialog.setResizable(false);
		dialog.getContainer().add(new Label(translate("loginbox.e_mail"), 10, 5, 380, 20));
		dialog.getContainer().add(mail = new TextField(SessionHandler.getUserMail(), 10, 30, 380, 20).accept(newval -> SessionHandler.updateUserMail(newval)));
		dialog.getContainer().add(new Label(translate("loginbox.password"), 10, 60, 380, 20));
		PasswordInput passinput = new PasswordInput(SessionHandler.shouldEncrypt() ? "" : SessionHandler.getPassWord(), 10, 85, 380, 20);
		passinput.addTextInputContentChangeEventListener(event -> SessionHandler.updatePassword(event.getNewValue()));
		dialog.getContainer().add(passinput);
		Settings.applyMenuTheme(mail);
		Settings.applyMenuTheme(passinput);
		CheckBox checkbox0 = new CheckBox(10, 115, 380, 20);
		checkbox0.getStyle().setPadding(5f, 10f, 5f, 5f);
		checkbox0.setChecked(SessionHandler.shouldEncrypt());
		checkbox0.addCheckBoxChangeValueListener(listener -> SessionHandler.toggleEncrypt());
		checkbox0.getTextState().setText((translate("loginbox.encrypt")));
		dialog.getContainer().add(checkbox0);
		//
		Button button0 = new Button(translate("profile.button.login"), 10, 200 - 50, 80, 20);
		button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()){
				dialog.close();
				SessionHandler.encrypt();
				SessionHandler.tryLogin(resp -> {
					GenericDialog.show("loginbox.title", "dialog.button.ok", SessionHandler.isLoggedIn() ? null : "profile.button.retry", null, () -> openLogin(), resp);
					SessionHandler.save();
				});
			}
		});
		dialog.getContainer().add(button0);
		//
		Button button1 = new Button(translate("profile.button.register"), 100, 200 - 50, 80, 20);
		button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()){
				dialog.close();
				SessionHandler.openRegister();
			}
		});
		dialog.getContainer().add(button1);
		//
		Button button2 = new Button(translate("dialog.button.cancel"), 400 - 90, 200 - 50, 80, 20);
		button2.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()){
				dialog.close();
			}
		});
		dialog.getContainer().add(button2);
		//
		dialog.show(FMT.FRAME);
	}

}
