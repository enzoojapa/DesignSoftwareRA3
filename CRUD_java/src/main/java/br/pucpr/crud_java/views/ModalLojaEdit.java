package br.pucpr.crud_java.views;

import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Loja;
import br.pucpr.crud_java.persistencias.LojaDAO; // O LojaDAO JPA
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality; // Importação adicionada
import javafx.stage.Stage;


public class ModalLojaEdit {
    private final Stage stage;
    private final Loja loja;
    private final LojaDAO lojaDAO; // NOVO: Instância do DAO JPA

    public ModalLojaEdit(Stage stageOwner, Loja loja) {
        this.loja = loja;
        this.lojaDAO = new LojaDAO(); // Inicializa o DAO

        this.stage = new Stage();
        // Configurações do modal
        this.stage.initOwner(stageOwner);
        this.stage.initModality(Modality.WINDOW_MODAL);
    }

    public void mostrar() {
        criarUI();
        stage.showAndWait();
    }

    public void criarUI() {
        stage.setTitle("Editar Loja ID: " + loja.getId()); // Usando o ID do JPA

        VBox camposLoja = new VBox();
        camposLoja.setPadding(new Insets(15));
        camposLoja.setSpacing(10);

        Label labelNomeLoja = new Label("Nome da Loja:");
        TextField textFieldNomeLoja = new TextField(loja.getLojaNome());
        textFieldNomeLoja.setPromptText("Nome da Loja");

        Label labelTel = new Label("Telefone:");
        TextField txtTel = new TextField(loja.getLojaTelefone());
        txtTel.setPromptText("Telefone");
        adicionarMascaraTelefone(txtTel);

        Label labelTipo = new Label("Tipo da Loja:");
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Roupas", "Joias", "Esportes", "Restaurantes", "Livros");
        cbTipo.setValue(loja.getLojaTipo());
        cbTipo.setMaxWidth(Double.MAX_VALUE);

        Button btnEditar = new Button("Salvar Alterações");
        btnEditar.setMaxWidth(Double.MAX_VALUE); // Adicionado para consistência

        btnEditar.setOnAction(e -> {
            try {
                String novoNome = textFieldNomeLoja.getText().trim();
                String novoTelefone = txtTel.getText().trim();
                String novoTipo = cbTipo.getValue();

                if (novoNome.isEmpty() || novoTelefone.length() < 14 || novoTipo == null) {
                    throw new IllegalArgumentException("Preencha todos os campos corretamente! O telefone deve estar completo (ex: (XX) XXXXX-XXXX).");
                }

                // 1. ATUALIZA o OBJETO Loja em memória
                loja.setLojaNome(novoNome);
                loja.setLojaTelefone(novoTelefone);
                loja.setLojaTipo(novoTipo);

                // 2. CORRIGIDO: Usa o DAO JPA para salvar. O merge faz o UPDATE.
                lojaDAO.salvar(loja);

                Alerts.alertInfo("Sucesso", "Loja editada com sucesso!");
                this.stage.close();

            } catch (IllegalArgumentException ex) {
                // Captura exceções de validação de campo (acima)
                Alerts.alertError("Erro de Validação", ex.getMessage());
            } catch (RuntimeException ex) {
                // NOVO: Captura exceções do JPA/DAO (Ex: Nome ou Telefone duplicado)
                Alerts.alertError("Erro de Persistência", "Ocorreu um erro ao editar a loja (Nome ou Telefone pode já existir): " + ex.getMessage());
            }
        });

        Button btnVoltar = new Button("Cancelar");
        btnVoltar.setMaxWidth(Double.MAX_VALUE); // Adicionado para consistência
        btnVoltar.setOnAction(e -> this.stage.close());

        camposLoja.getChildren().addAll(
                labelNomeLoja, textFieldNomeLoja,
                labelTel, txtTel,
                labelTipo, cbTipo,
                btnEditar,
                btnVoltar
        );


        Scene cena = new Scene(camposLoja, 400, 350);
        this.stage.setScene(cena);
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