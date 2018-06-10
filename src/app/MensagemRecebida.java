package app;

import javax.jms.Message;

@FunctionalInterface
public interface MensagemRecebida {
	public void recebida(Message mensagem);
}
