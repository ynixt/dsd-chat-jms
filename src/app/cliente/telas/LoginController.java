/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.cliente.telas;

import app.ControladorMensagem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Lucas
 */
public class LoginController {

	@FXML
	private TextField txt_nick;

	@FXML
	private Text msg_disponivel;

	@FXML
	private void btnVerificarClick(ActionEvent event) {

		boolean verifica = false;

		String nick = this.txt_nick.getText();

		// TODO Validar se existe o nick

		if (verifica == true) {
			msg_disponivel.setText("Este nick já está em uso.");
		} else {
			msg_disponivel.setText("Parabéns! Este agora é o seu nick.");
			// TODO atribuir nick
			showChatDialog(new ControladorMensagem(nick));
		}
	}
	
	private void showChatDialog(ControladorMensagem app) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/cliente/telas/Chat.fxml"));
			
			Window janela = txt_nick.getScene().getWindow();
			Stage anterior = (Stage) janela;
			anterior.close();

			Stage stage = new Stage();

			Scene scene = new Scene((AnchorPane) loader.load(), 480, 600);
			
			ChatController chat = loader.<ChatController>getController();
			chat.initData(app);

			stage.setMaxHeight(600);
			stage.setMaxWidth(480);

			stage.setScene(scene);

			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
