package app.cliente.telas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;

import app.ControladorMensagem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class ChatController {

	@FXML
	private TextArea txt_msg;
	@FXML
	private Text msg_enviado;
	@FXML
	private TextField txt_destinatario;

	@FXML
	private TableView<String> tabela;
	@FXML
	private TableColumn<String, String> t_nick;
	@FXML
	private TableColumn<String, String> t_msg;
	
	private ControladorMensagem app;

	@FXML
	private void btnEnviarClick(ActionEvent event) {
		enviarMensagem();
	}

	@FXML
	private void buttonPressed(KeyEvent e) {
		if (e.getCode().equals(KeyCode.ENTER)) {
			enviarMensagem();
		}
	}

	@FXML
	private void enviarMensagem() {
		String msg = txt_msg.getText();
		
		if (isMensagemValida(msg)) {
			String destinatario = txt_destinatario.getText();

			if (destinatario.isEmpty()) {
				destinatario = null;
				msg_enviado.setText("Mensagem enviada para todos os usuários.");
			} else {
				msg_enviado.setText("Mensagem enviada para: " + destinatario + ".");
			}
			
			app.enviarMensagem(msg, ControladorMensagem.TAG_MENSAGEM_SERVIDOR, destinatario);
			txt_msg.setText("");
		} else {
			msg_enviado.setText("Mensagem inválida.");
		}
	}
	
	private boolean isMensagemValida(final String mensagem) {
		if (mensagem.isEmpty()) {
			return false;
		}
		
		Pattern pattern = Pattern.compile("^("+ControladorMensagem.RESERVADO_NICK+").*");
		Matcher matcher = pattern.matcher(mensagem);
		return !matcher.find();
	}

	@FXML
	private void attMsg(ActionEvent event) {

		t_nick.setCellValueFactory(new PropertyValueFactory<>("nick"));
		t_msg.setCellValueFactory(new PropertyValueFactory<>("msg"));
		
		app.receberMensagem(m -> {
			try {
				String mensagem = m.getStringProperty(ControladorMensagem.PROPRIEDADE_TEXTO);
				
				System.out.println(mensagem);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		tabela.setItems(listaDeClientes());

	}

	private ObservableList<String> listaDeClientes() {

		// lista

		return FXCollections.observableArrayList(

		// lista
		);
	}

	public void initData(ControladorMensagem app) {
		this.app = app;
	}
}
