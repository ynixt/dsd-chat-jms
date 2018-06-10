package app;

import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
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
	public static final String TAG_MENSAGEM_LOGOUT = "logout";

	public static final String PROPRIEDADE_TEXTO = "texto";
	public static final String PROPRIEDADE_ID_DESTINO = "id_destino";
	public static final String PROPRIEDADE_ID_REMETENTE = "id_remetente";
	public static final String PROPRIEDADE_LOGIN_STATUS = "login_invalido";

	public static final String RESERVADO_NICK = "#LOGIN";

	private String idUSuario;

	private static final Logger LOGGER = Logger.getLogger(ControladorMensagem.class.toString());

	public ControladorMensagem(String idUSuario) {
		this.idUSuario = idUSuario;
	}

	public void enviarMensagemLoginStatus(final String idUsuarioDestino, final boolean loginValido) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(RESERVADO_NICK + idUsuarioDestino);

					MessageProducer producer = session.createProducer(destination);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

					Message mensagem = session.createMessage();
					mensagem.setBooleanProperty(PROPRIEDADE_LOGIN_STATUS, loginValido);

					producer.send(mensagem);

					session.close();
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Envia uma mensagem
	 * 
	 * @param texto
	 *            Texto da mensagem
	 * @param tag
	 *            Tag usada para inserir a mensagem na fila
	 */
	public void enviarMensagemDoServidor(final String texto, final String tag) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				LOGGER.info("Enviando mensagem do servidor.");
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(tag);

					MessageProducer producer = session.createProducer(destination);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

					Message mensagem = session.createMessage();
					mensagem.setStringProperty(PROPRIEDADE_ID_REMETENTE, ControladorMensagem.this.idUSuario);
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

	/**
	 * Envia uma mensagem
	 * 
	 * @param texto
	 *            Texto da mensagem
	 * @param tag
	 *            Tag usada para inserir a mensagem na fila
	 * @param idUsuarioDestino
	 *            Id do usu�rio que deve receber essa mensagem. Se for nulo o
	 *            servidor vai enviar para todo mundo que estiver ativo
	 */
	public void enviarMensagem(final String texto, final String tag, final String idUsuarioDestino) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				LOGGER.info("Enviando mensagem");
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(tag);

					MessageProducer producer = session.createProducer(destination);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

					Message mensagem = session.createMessage();
					mensagem.setStringProperty(PROPRIEDADE_ID_REMETENTE, ControladorMensagem.this.idUSuario);
					mensagem.setStringProperty(PROPRIEDADE_TEXTO, texto);

					if (idUsuarioDestino != null) {
						mensagem.setStringProperty(PROPRIEDADE_ID_DESTINO, idUsuarioDestino);
					}

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

			@Override
			public void run() {
				LOGGER.info("Recebendo mensagem.");
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(ControladorMensagem.this.idUSuario);

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

	public void receberMensagemLogin(final MensagemRecebida event) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(RESERVADO_NICK + ControladorMensagem.this.idUSuario);

					MessageConsumer consumer = session.createConsumer(destination);

					event.recebida(consumer.receive(1000));

					consumer.close();
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
