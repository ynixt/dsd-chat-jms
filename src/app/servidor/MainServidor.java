package app.servidor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;

import app.ControladorMensagem;
import app.MensagemRecebida;
import app.Propriedade;

public class MainServidor {
	private static Set<String> usuariosConectados = Collections.synchronizedSet(new HashSet<>());

	private static final Logger LOGGER = Logger.getLogger(MainServidor.class.toString());

	public static void main(String[] args) throws InterruptedException {
		ControladorMensagem controladorLogin = new ControladorMensagem(ControladorMensagem.TAG_MENSAGEM_LOGIN);
		ControladorMensagem controladorLogout = new ControladorMensagem(ControladorMensagem.TAG_MENSAGEM_LOGOUT);
		ControladorMensagem controladorMensagem = new ControladorMensagem(ControladorMensagem.TAG_MENSAGEM_SERVIDOR);

		controladorLogin.receberMensagem(new MensagemRecebida() {

			@Override
			public void recebida(Message mensagem) {
				LOGGER.info("Servidor recebeu mensagem de login");
				try {
					String idUsuario = mensagem.getStringProperty(Propriedade.TEXTO.toString());

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

		controladorLogout.receberMensagem(new MensagemRecebida() {

			@Override
			public void recebida(Message mensagem) {
				LOGGER.info("Servidor recebeu mensagem de logout");
				try {
					String idUsuario = mensagem.getStringProperty(Propriedade.TEXTO.toString());

					usuariosConectados.remove(idUsuario);
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
					String texto = mensagem.getStringProperty(Propriedade.TEXTO.toString());
					String idUsuarioDestino = null;

					String idUsuarioRemetente = mensagem.getStringProperty(Propriedade.ID_REMETENTE.toString());

					try {
						idUsuarioDestino = mensagem.getStringProperty(Propriedade.ID_DESTINO.toString());
					} catch (JMSException exception) {
					}

					if (idUsuarioDestino == null) {
						for (String cliente : usuariosConectados) {
							if (!cliente.equals(idUsuarioRemetente)) {
								controladorMensagem.enviarMensagemDoServidor(texto, cliente, idUsuarioRemetente);
							}
						}
					} else {
						controladorMensagem.enviarMensagemDoServidor(texto, idUsuarioDestino, idUsuarioRemetente);
					}
				} catch (JMSException e) {
				}
			}
		});

	}
}
