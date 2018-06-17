package app.cliente.telas;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;

import app.ControladorMensagem;
import app.Propriedade;
import javafx.collections.FXCollections;
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

public class ChatDialogController {

	@FXML
	private TextArea mensagemTextArea;
	@FXML
	private Text alertaText;
	@FXML
	private TextField destinatarioTextField;

	@FXML
	private TableView<Mensagem> mensagensTableView;
	@FXML
	private TableColumn<Mensagem, String> apelidoTableColumn;
	@FXML
	private TableColumn<Mensagem, String> mensagemTableColumn;

	private ControladorMensagem app;

	private List<Mensagem> mensagens;

	public ChatDialogController() {
		mensagens = new ArrayList<Mensagem>();
	}

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
		String msg = mensagemTextArea.getText();

		if (isMensagemValida(msg)) {
			String destinatario = destinatarioTextField.getText();

			if (destinatario.isEmpty()) {
				destinatario = null;
				alertaText.setText("Mensagem enviada para todos os usuários.");
			} else {
				alertaText.setText("Mensagem enviada para: " + destinatario + ".");
			}

			app.enviarMensagem(msg, ControladorMensagem.TAG_MENSAGEM_SERVIDOR, destinatario);
			adicionarMensagem(msg, "Eu");
			mensagemTextArea.setText("");
		} else {
			alertaText.setText("Mensagem inválida.");
		}
	}

	private boolean isMensagemValida(final String mensagem) {
		if (mensagem.isEmpty()) {
			return false;
		}

		Pattern pattern = Pattern.compile("^(" + ControladorMensagem.RESERVADO_NICK + ").*");
		Matcher matcher = pattern.matcher(mensagem);
		return !matcher.find();
	}

	public void initData(ControladorMensagem app) {
		this.app = app;
		atualizarMensagens();
	}
	
	private void atualizarMensagens() {
		apelidoTableColumn.setCellValueFactory(new PropertyValueFactory<>("autor"));
		mensagemTableColumn.setCellValueFactory(new PropertyValueFactory<>("mensagem"));

		app.receberMensagem(m -> {
			try {
				String mensagem = m.getStringProperty(Propriedade.TEXTO.toString()),
						remetente = m.getStringProperty(Propriedade.ID_REMETENTE.toString());

				adicionarMensagem(mensagem, remetente);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		});
	}
	
	private void adicionarMensagem(String mensagem, String remetente) {
		mensagens.add(new Mensagem(remetente, mensagem));
		mensagensTableView.setItems(FXCollections.observableList(mensagens));
	}
	
}
