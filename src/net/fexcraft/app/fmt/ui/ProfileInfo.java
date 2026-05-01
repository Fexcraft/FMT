package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Dialog.DialogButton;
import net.fexcraft.app.fmt.ui.Field.FieldType;
import net.fexcraft.app.fmt.utils.SessionHandler;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ProfileInfo {

	public static void open(){
		Dialog dia = FMT.UI.createDialog(500, 150, "profile.title")
			.addText(0, "profile.username", SessionHandler.getUserName())
			.addText(1, "profile.userid", SessionHandler.getUserId())
			.addText(2, "profile.license", SessionHandler.getLicenseTitle());
		if(SessionHandler.isLoggedIn()){
			dia.set_confirm(d -> openLogin());
			dia.buttons(100, DialogButton.LOGOUT, DialogButton.CLOSE);
		}
		else{
			dia.set_confirm(d -> openLogin());
			dia.set_cancel(d -> SessionHandler.openRegister());
			dia.buttons(100, DialogButton.LOGIN, DialogButton.REGISTER);
		}
	}

	private static void openLogin(){
		if(SessionHandler.isLoggedIn()) SessionHandler.tryLogout();
		Field mail = new Field(FieldType.TEXT, 590);
		Field pass = new PassField(590);
		Dialog dia = FMT.UI.createDialog(600, 240, "profile.login.title")
			.addText(0, "profile.login.mail")
			.addRowElm(1, mail)
			.addText(2, "profile.login.password")
			.addRowElm(3, pass)
			.addText(4, "profile.login.info");
		mail.text(SessionHandler.getUserId());
		pass.text(SessionHandler.getPassWord());
		BoolElm elm = dia.addBoolText(5, "profile.login.encrypt");
		elm.set(() -> SessionHandler.shouldEncrypt(), b -> SessionHandler.toggleEncrypt(b)).updtexcol();
		dia.set_confirm(d -> {
			SessionHandler.updateUserMail(mail.get_text());
			SessionHandler.updatePassword(pass.get_pass());
			SessionHandler.encrypt();
			SessionHandler.tryLogin(resp -> {
				Dialog nd = FMT.UI.createDialog(600, 120, "profile.login.title").addText(0, resp);
				if(!SessionHandler.isLoggedIn()){
					nd.set_confirm(c -> openLogin());
					nd.buttons(100, DialogButton.RETRY, DialogButton.CANCEL);
				}
				else{
					nd.buttons(100, DialogButton.OK);
				}
				SessionHandler.save();
			});
		});
		dia.set_cancel(d -> SessionHandler.openRegister());
		dia.buttons(100, DialogButton.LOGIN, DialogButton.REGISTER);
	}

}
