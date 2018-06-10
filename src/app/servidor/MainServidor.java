package app.servidor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;

import app.ControladorMensagem;
import app.MensagemRecebida;

public class MainServidor {
	private static Set<String> usuariosConectados = Collections.synchronizedSet(new HashSet<>());
	
	private static final Logger LOGGER = Logger.getLogger(MainServidor.class.toString());

	public static void main(String[] args) throws InterruptedException {
		ControladorMensagem controladorLogin = new ControladorMensagem(ControladorMensagem.TAG_MENSAGEM_LOGIN);
		ControladorMensagem controladorMensagem = new ControladorMensagem(ControladorMensagem.TAG_MENSAGEM_SERVIDOR);

		controladorLogin.receberMensagem(new MensagemRecebida() {

			@Override
			public void recebida(Message mensagem) {
				LOGGER.info("Servidor recebeu mensagem de login");
				try {
					String idUsuario = mensagem.getStringProperty(ControladorMensagem.PROPRIEDADE_TEXTO);

					if (usuariosConectados.contains(idUsuario)) {
						controladorLogin.enviarMensagemLoginStatus(idUsuario, false);
					} else {
						controladorLogin.enviarMensagemLoginStatus(idUsuario, true);
						usuariosConectados.add(idUsuario);
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});

		controladorMensagem.receberMensagem(new MensagemRecebida() {

			@Override
			public void recebida(Message mensagem) {
				LOGGER.info("Servidor recebeu mensagem");
				try {
					String texto = mensagem.getStringProperty(ControladorMensagem.PROPRIEDADE_TEXTO);
					String idUsuarioDestino = null;

					try {
						idUsuarioDestino = mensagem.getStringProperty(ControladorMensagem.PROPRIEDADE_ID_DESTINO);
					} catch (JMSException exception) {
					}

					if (idUsuarioDestino == null) {
						for (String cliente : usuariosConectados) {
							controladorMensagem.enviarMensagemDoServidor(texto, cliente);
						}
					} else {
						controladorMensagem.enviarMensagemDoServidor(texto, idUsuarioDestino);
					}
				} catch (JMSException e) {
				}
			}
		});

	}
}
