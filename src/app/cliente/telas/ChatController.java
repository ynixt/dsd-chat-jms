package app.cliente.telas;

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
		confere();
	}

	@FXML
	private void buttonPressed(KeyEvent e) {
		if (e.getCode().equals(KeyCode.ENTER)) {
			confere();
		}
	}

	@FXML
	private void confere() {
		String msg = this.txt_msg.getText();
		
		if (msg.isEmpty()) {
			this.msg_enviado.setText("Mensagem em branco.");
		} else {

			String destinatario = this.txt_destinatario.getText();

			if (destinatario.isEmpty()) {
				destinatario = null;
				this.msg_enviado.setText("Mensagem enviada para todos.");
			} else {
				// metodo enviar particular
				this.msg_enviado.setText("Mensagem enviada para: " + destinatario + "!");
				
			}
			
			app.enviarMensagem(msg, ControladorMensagem.TAG_MENSAGEM_SERVIDOR, destinatario);
			txt_msg.setText("");
		}		
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
