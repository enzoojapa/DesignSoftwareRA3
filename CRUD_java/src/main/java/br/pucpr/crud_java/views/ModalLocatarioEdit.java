package br.pucpr.crud_java.views;

import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Locatario;
import br.pucpr.crud_java.persistencias.LocatarioDAO; // O LocatarioDAO JPA
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality; // Importante para modais
import javafx.stage.Stage;

public class ModalLocatarioEdit {

    private final Stage modalStage; // NOVO: Nome alterado para evitar confusão com o stage principal
    private final Locatario locatario;
    private final LocatarioDAO locatarioDAO; // NOVO: Instância do DAO JPA

    public ModalLocatarioEdit(Locatario locatario){
        this.locatario = locatario;
        this.modalStage = new Stage(); // Inicializa o novo Stage
        this.locatarioDAO = new LocatarioDAO(); // Inicializa o DAO

        // Configurações do modal
        this.modalStage.initModality(Modality.APPLICATION_MODAL);
        this.modalStage.setTitle("Editar Locatário: " + locatario.getLocatarioNome());
    }

    public void mostrar(){
        criarUI();
        modalStage.showAndWait();
    }

    private void criarUI() {

        VBox camposBol = new VBox();
        camposBol.setStyle("-fx-padding: 10;");
        camposBol.setSpacing(5);

        // O CNPJ é a chave de negócio única e não deve ser alterada facilmente
        Label labelCNPJ = new Label("CNPJ da Empresa");
        TextField txtCNPJ = new TextField(String.valueOf(locatario.getLocatarioCnpj()));
        txtCNPJ.setPromptText("Digite o CNPJ");
        txtCNPJ.setEditable(false); // Mantido como não editável para segurança

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

        Button btnEditar = new Button("Salvar");
        btnEditar.setMaxWidth(Double.MAX_VALUE);

        btnEditar.setOnAction(e -> {
            try {
                String nome = txtNome.getText();
                String email = txtEmail.getText();
                String telefone = txtTelefone.getText();

                // Validações de campos obrigatórios
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

                // 1. ATUALIZA o OBJETO em memória
                locatario.setLocatarioNome(nome);
                locatario.setLocatarioEmail(email);
                locatario.setLocatarioTelefone(telefone);

                // 2. CORRIGIDO: Usa o DAO JPA para salvar. O merge detecta o ID e faz o UPDATE.
                locatarioDAO.salvar(locatario);

                Alerts.alertInfo("Editado", "Locatário editado com sucesso");

                this.modalStage.close();

            } catch (RuntimeException ex) {
                // Captura exceções do JPA/DAO (Ex: conflito de unicidade, falha na transação)
                Alerts.alertError("Erro de Persistência", "Falha ao editar: " + ex.getMessage());
            }
        });

        Button btnVoltar = new Button("Cancelar");
        btnVoltar.setMaxWidth(Double.MAX_VALUE);
        btnVoltar.setOnAction(e -> this.modalStage.close());

        camposBol.getChildren().addAll(labelCNPJ, txtCNPJ, labelNome,
                txtNome, labelEmail, txtEmail, labelTelefone,
                txtTelefone,
                btnEditar, btnVoltar); // Adiciona o botão voltar

        Scene cena = new Scene(camposBol, 400, 350); // Tamanho ajustado
        this.modalStage.setScene(cena);
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