package app;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ControladorMensagem {

	public static final String ENDERECO = "tcp://localhost:61616";
	public static final String TAG_MENSAGEM_SERVIDOR = "servidor";

	public static final String TAG_MENSAGEM_LOGIN = "login";

	public static final String TAG_MENSAGEM_GLOBAL = "global";

	public static final String PROPRIEDADE_TEXTO = "texto";
	public static final String PROPRIEDADE_ID = "id";

	private String idUSuario;

	public ControladorMensagem(String idUSuario) {
		this.idUSuario = idUSuario;
	}

	public void enviarMensagem(final String texto, final String tag) {
		new Thread(new Runnable() {

			public void run() {
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(tag);

					MessageProducer producer = session.createProducer(destination);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

					Message mensagem = session.createMessage();
					mensagem.setStringProperty(PROPRIEDADE_ID, idUSuario);
					mensagem.setStringProperty(PROPRIEDADE_TEXTO, texto);

					producer.send(mensagem);

					session.close();
					connection.close();
				} catch (Exception e) {
					System.out.println("Caught: " + e);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void receberMensagem(final MensagemRecebida event) {
		new Thread(new Runnable() {

			public void run() {
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(idUSuario);

					MessageConsumer consumer = session.createConsumer(destination);

					consumer.setMessageListener(new MessageListener() {

						@Override
						public void onMessage(Message message) {
							if (message != null) {
								event.recebida(message);
							}
						}
					});

				} catch (Exception e) {
					System.out.println("Caught: " + e);
					e.printStackTrace();
				}
			}
		}).start();
	}
}
