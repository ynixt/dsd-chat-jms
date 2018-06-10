package app.cliente.telas;

import javax.jms.JMSException;
import javax.jms.Message;

import app.ControladorMensagem;
import app.MensagemRecebida;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {
	@FXML
	private TextField txtLogin;

	@FXML
	private void btnEntrarClick(ActionEvent event) {
		String login = this.txtLogin.getText();

		ControladorMensagem controlador = new ControladorMensagem(login);

		controlador.enviarMensagemDoServidor(login, ControladorMensagem.TAG_MENSAGEM_LOGIN);

		controlador.receberMensagemLogin(new MensagemRecebida() {

			@Override
			public void recebida(Message mensagemStatusLogin) {
				if (mensagemStatusLogin != null) {
					try {
						if (mensagemStatusLogin.getBooleanProperty(ControladorMensagem.PROPRIEDADE_LOGIN_STATUS)) {
							controlador.enviarMensagem("Este é um teste", ControladorMensagem.TAG_MENSAGEM_SERVIDOR,
									null);

							controlador.receberMensagem(new MensagemRecebida() {

								@Override
								public void recebida(Message mensagem) {
									try {
										String usuarioQueEnviou = mensagem
												.getStringProperty(ControladorMensagem.PROPRIEDADE_ID_REMETENTE);
										String texto = mensagem
												.getStringProperty(ControladorMensagem.PROPRIEDADE_TEXTO);
										System.out
												.println("Mensagem recebida (" + usuarioQueEnviou + ")" + " " + texto);
									} catch (JMSException e) {
										e.printStackTrace();
									}
								}
							});
						} else {
							// TODO login inválido
						}
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}
}
