package app;

import javax.jms.Message;

public interface MensagemRecebida {
	public void recebida(Message mensagem);
}
