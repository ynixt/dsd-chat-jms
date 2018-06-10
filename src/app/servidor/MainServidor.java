package app.servidor;

import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;

import app.ControladorMensagem;
import app.MensagemRecebida;

public class MainServidor {
	private static Set<String> usuariosConectados = new HashSet<>();

	public static void main(String[] args) throws InterruptedException {
		ControladorMensagem controladorLogin = new ControladorMensagem(ControladorMensagem.TAG_MENSAGEM_LOGIN);
		ControladorMensagem controladorMensagem = new ControladorMensagem(ControladorMensagem.TAG_MENSAGEM_SERVIDOR);

		controladorLogin.receberMensagem(new MensagemRecebida() {

			@Override
			public void recebida(Message mensagem) {
				try {
					String texto = mensagem.getStringProperty(ControladorMensagem.PROPRIEDADE_TEXTO);
					usuariosConectados.add(texto);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});

		controladorMensagem.receberMensagem(new MensagemRecebida() {

			@Override
			public void recebida(Message mensagem) {
				try {
					String texto = mensagem.getStringProperty(ControladorMensagem.PROPRIEDADE_TEXTO);
					String idUsuarioDestino = null;

					try {
						idUsuarioDestino = mensagem.getStringProperty(ControladorMensagem.PROPRIEDADE_ID_DESTINO);
					} catch (JMSException exception) {
					}

					if (idUsuarioDestino == null) {
						for (String cliente : usuariosConectados) {
							controladorMensagem.enviarMensagem(texto, cliente, null);
						}
					} else {
						controladorMensagem.enviarMensagem(texto, idUsuarioDestino, null);
					}
				} catch (JMSException e) {
				}
			}
		});

	}
}
