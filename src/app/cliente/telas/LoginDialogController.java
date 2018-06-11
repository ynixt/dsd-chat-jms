/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.cliente.telas;


import javax.jms.JMSException;

import app.ControladorMensagem;
import app.Propriedade;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Lucas
 */
public class LoginDialogController {

	@FXML
	private TextField txt_nick;

	@FXML
	private Text msg_disponivel;
	
	@FXML
	private void onKeyPressed(KeyEvent e) {
		if (e.getCode().equals(KeyCode.ENTER)) {
			btnVerificarClick(new ActionEvent());
		} else if (e.getCode().equals(KeyCode.ESCAPE)) {
			fecharJanela();
		}
	}

	@FXML
	private void btnVerificarClick(ActionEvent event) {
		String nick = this.txt_nick.getText();

		ControladorMensagem app = new ControladorMensagem(nick);
		app.enviarMensagemDoServidor(nick, ControladorMensagem.TAG_MENSAGEM_LOGIN);
		
		app.receberMensagemLogin(m -> {
			if (m != null) {
				try {
					if (m.getBooleanProperty(Propriedade.LOGIN_STATUS.toString())) {
						Platform.runLater(() -> {
							showChatDialog(app);	
						});
					} else {
						msg_disponivel.setText("Login inválido, tente alterar o nick.");
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void showChatDialog(ControladorMensagem app) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/cliente/telas/Chat.fxml"));

			Window janela = txt_nick.getScene().getWindow();
			Stage anterior = (Stage) janela;
			anterior.close();

			Stage stage = new Stage();

			Scene scene = new Scene((AnchorPane) loader.load(), 480, 600);

			ChatDialogController chat = loader.<ChatDialogController>getController();
			chat.initData(app);

			stage.setMaxHeight(600);
			stage.setMaxWidth(480);
			
			stage.setOnCloseRequest(e -> {
				app.enviarMensagem(app.getSessionId(), ControladorMensagem.TAG_MENSAGEM_LOGOUT, null);
			});

			stage.setScene(scene);

			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fecharJanela() {
		Window janela = txt_nick.getScene().getWindow();
		Stage atual = (Stage) janela;
		atual.close();
	}
}
