package br.pucpr.crud_java.views;

import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Locatario;
import br.pucpr.crud_java.persistencias.ArquivoLocatario;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ModalLocatarioEdit {
    private Stage stage;
    private Scene cena;
    private Locatario locatario;

    public ModalLocatarioEdit(Locatario locatario){
        this.locatario = locatario;
    }

    public void mostrar(){
        criarUI();
        stage.showAndWait();
    }

    private void criarUI() {
        this.stage = new Stage();
        Button btnVoltar = new Button("Voltar");
        btnVoltar.setOnAction(e -> {
            this.stage.close();
        });
        VBox camposBol = new VBox();
        camposBol.setStyle("-fx-padding: 10;");
        camposBol.setSpacing(5);






        Label labelCNPJ = new Label("CNPJ da Empresa");
        TextField txtCNPJ = new TextField(String.valueOf(locatario.getLocatarioCnpj()));
        txtCNPJ.setPromptText("Digite o CNPJ");
        txtCNPJ.setEditable(false);

        Label labelNome = new Label("Nome da Empresa");
        TextField txtNome = new TextField(String.valueOf(locatario.getLocatarioNome()));
        txtNome.setPromptText("Digite o nome da Empresa");

        Label labelEmail = new Label("Email");
        TextField txtEmail = new TextField(String.valueOf(locatario.getLocatarioEmail()));
        txtEmail.setPromptText("Digite o email da Empresa");

        Label labelTelefone = new Label("Telefone");
        TextField txtTelefone = new TextField(String.valueOf(locatario.getLocatarioTelefone()));
        txtTelefone.setPromptText("(XX) XXXXX-XXXX");
        adicionarMascaraTelefone(txtTelefone);

        Button btnCadastrar = new Button("Cadastrar Locatário");
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);

        Button btnEditar = new Button("Salvar");
        btnEditar.setOnAction(e -> {
                    try {
                        String cnpj = txtCNPJ.getText();
                        String nome = txtNome.getText();
                        String email = txtEmail.getText();
                        String telefone = txtTelefone.getText();

                        if (nome.isEmpty()) {
                            Alerts.alertError("Erro de Validação", "O Nome da empresa não pode ser vazio.");
                            return;
                        }

                        if (email.isEmpty() || !email.contains("@")) {
                            Alerts.alertError("Erro de Validação", "Insira um e-mail válido");
                            return;
                        }

                        if (telefone.isEmpty() || telefone.length() < 14) {
                            Alerts.alertError("Erro de Validação", "O Telefone deve ser preenchido completamente.");
                            return;
                        }
                        if (email != "" && telefone != "") {

                            ArquivoLocatario.editarLocatario(cnpj, nome, email, telefone);

                            Alerts.alertInfo("Editado",
                                    "Locatário editado com sucesso");

                            this.stage.close();
                        } else {
                            Alerts.alertError("Erro", "Preencha os campos " +
                                    "corretamente");
                        }
                    } catch (NumberFormatException ex) {
                        Alerts.alertError("Erro", "Insira dados válidos!");
                    }
                }
        );

        camposBol.getChildren().addAll(labelCNPJ, txtCNPJ, labelNome,
                txtNome, labelEmail, txtEmail, labelTelefone,
                txtTelefone,
                btnEditar);

        this.cena = new Scene(camposBol, 800, 500);
        this.stage.setScene(this.cena);
    }

    private void adicionarMascaraTelefone(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String digitos = newValue.replaceAll("\\D", "");
            if (digitos.length() > 11) digitos = digitos.substring(0, 11);

            String textoFormatado = digitos;
            if (digitos.length() > 2) textoFormatado = "(" + digitos.substring(0, 2) + ") " + digitos.substring(2);
            if (digitos.length() > 7) textoFormatado = "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, 7) + "-" + digitos.substring(7);
            else if (digitos.length() > 6) textoFormatado = "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, 6) + "-" + digitos.substring(6);

            if (!newValue.equals(textoFormatado)) {
                textField.setText(textoFormatado);
                textField.positionCaret(textoFormatado.length());
            }
        });
    }
}
