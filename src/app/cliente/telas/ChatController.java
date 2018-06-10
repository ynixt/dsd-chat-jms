package app.cliente.telas;

import javax.jms.JMSException;
import javax.jms.Message;

import app.ControladorMensagem;
import app.MensagemRecebida;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class ChatController {
	
        @FXML private TextArea txt_msg;
        @FXML private Text msg_enviado;
        @FXML private TextField txt_destinatario;
        
        @FXML private TableView<String> tabela;
        @FXML private TableColumn<String,String> t_nick;
        @FXML private TableColumn<String,String> t_msg;
        
       
       @FXML
       private void btnEnviarClick(ActionEvent event){
           
            String msg = this.txt_msg.getText(); 
            
            if(msg.isEmpty()){
                this.msg_enviado.setText("Mensagem em branco.");
            }else{
                
                String destinatario = this.txt_destinatario.getText();
                
                if(destinatario.isEmpty()){
                    //metodo enviar para todos
                    this.msg_enviado.setText("Mensagem enviada para todos.");
                }else{
                    //metodo enviar particular
                    this.msg_enviado.setText("Mensagem enviada para: " + destinatario + "!");
                }
            }
		
       }
       
       @FXML
       private void attMsg(ActionEvent event){
           
           System.out.println("Teste: Msgs atualizadas...");
           
           t_nick.setCellValueFactory(
                new PropertyValueFactory<>("nick"));
           t_msg.setCellValueFactory(
                new PropertyValueFactory<>("msg"));
        
           tabela.setItems(listaDeClientes());
		
       }
       
       private ObservableList<String> listaDeClientes() {
           
           //lista
           
        return FXCollections.observableArrayList(
                
               //lista       
        );
    }
}
        
       
        

