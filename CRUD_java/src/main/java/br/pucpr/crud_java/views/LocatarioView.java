package br.pucpr.crud_java.views;

import br.pucpr.crud_java.TelaInicial;
import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Locatario;
import br.pucpr.crud_java.persistencias.LocatarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class LocatarioView {
    private final Stage stage;
    private final ObservableList<Locatario> locatariosObservable = FXCollections.observableArrayList();

    // NOVO: Instância do DAO JPA
    private final LocatarioDAO locatarioDAO;

    public LocatarioView(Stage stage) {
        this.stage = stage;
        this.locatarioDAO = new LocatarioDAO(); // Inicializa o DAO
    }

    public void mostrar() {
        criarUI();
        this.stage.show();
    }

    private void atualizarTabelaLocatarios() {
        // NOVO: Usa o método JPA de busca
        List<Locatario> locatarios = locatarioDAO.buscarTodos();
        locatariosObservable.setAll(locatarios);
    }


    private void criarUI() {
        stage.setTitle("Gestão de Locatários");

        // CORRIGIDO: Usa o método JPA de busca ao invés do lerLista() estático
        atualizarTabelaLocatarios();

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-padding: 10;");

        HBox navBar = criarMenuNavegacao();
        borderPane.setTop(navBar);

        VBox painelFormulario = new VBox(10);
        painelFormulario.setStyle("-fx-padding: 10;");
        painelFormulario.setPrefWidth(250);

        Label labelCNPJ = new Label("CNPJ da Empresa");
        TextField txtCNPJ = new TextField();
        txtCNPJ.setPromptText("Digite o CNPJ");
        adicionarMascaraCnpj(txtCNPJ);

        Label labelNome = new Label("Nome da Empresa");
        TextField txtNome = new TextField();
        txtNome.setPromptText("Digite o nome da Empresa");

        Label labelEmail = new Label("Email");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Digite o email da Empresa");

        Label labelTelefone = new Label("Telefone");
        TextField txtTelefone = new TextField();
        txtTelefone.setPromptText("(XX) XXXXX-XXXX");
        adicionarMascaraTelefone(txtTelefone);

        Button btnCadastrar = new Button("Cadastrar Locatário");
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);

        Button btnAtualizar = new Button("Atualizar página");
        btnAtualizar.setMaxWidth(Double.MAX_VALUE);
        btnAtualizar.setOnAction(e -> {
            // CORRIGIDO: Usa o método JPA de atualização
            atualizarTabelaLocatarios();
        });

        painelFormulario.getChildren().addAll(labelCNPJ, txtCNPJ, labelNome, txtNome, labelEmail, txtEmail, labelTelefone, txtTelefone, btnCadastrar, btnAtualizar);
        borderPane.setLeft(painelFormulario);

        VBox painelTabela = new VBox(10);
        painelTabela.setStyle("-fx-padding: 10;");

        TableView<Locatario> locatarioTable = criarTabelaLocatarios();

        Button btnEditar = new Button("Editar Selecionado");
        Button btnRemover = new Button("Remover Selecionado");
        painelTabela.getChildren().addAll(locatarioTable, btnRemover, btnEditar);
        borderPane.setCenter(painelTabela);

        btnCadastrar.setOnAction(e -> {
            try {
                String cnpj = txtCNPJ.getText();
                String telefone = txtTelefone.getText();
                String email = txtEmail.getText();
                String nome = txtNome.getText();

                // ... (Validações de campos existentes) ...
                if (cnpj.isEmpty() || cnpj.length() < 18) {
                    Alerts.alertError("Erro de Validação", "O CNPJ deve ser preenchido completamente.");
                    return;
                }
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

                Locatario novoLocatario = new Locatario();
                novoLocatario.setLocatarioCnpj(cnpj);
                novoLocatario.setLocatarioNome(nome);
                novoLocatario.setLocatarioEmail(email);
                novoLocatario.setLocatarioTelefone(telefone);

                // CORRIGIDO: Usa o DAO JPA para salvar. Não retorna mais boolean.
                locatarioDAO.salvar(novoLocatario);

                // Se o salvamento for bem-sucedido, atualiza a tabela
                atualizarTabelaLocatarios();

                txtCNPJ.clear();
                txtNome.clear();
                txtEmail.clear();
                txtTelefone.clear();
                Alerts.alertInfo("Sucesso", "Locatário cadastrado com sucesso!");

            } catch (RuntimeException ex) {
                // NOVO: Captura exceções do JPA/DAO (como CNPJ duplicado)
                Alerts.alertError("Erro de Persistência", "Falha ao cadastrar: " + ex.getMessage());
                txtCNPJ.clear(); // Limpa o CNPJ que causou o erro
            } catch (Exception ex) {
                // Captura outras exceções não tratadas.
            }
        });

        btnRemover.setOnAction(e -> {
            Locatario locatarioSelecionado = locatarioTable.getSelectionModel().getSelectedItem();
            if (locatarioSelecionado != null) {
                try {
                    // CORRIGIDO: Usa o DAO JPA para remover pelo CNPJ
                    locatarioDAO.remover(locatarioSelecionado.getLocatarioCnpj());

                    locatariosObservable.remove(locatarioSelecionado); // Remove da lista local
                    Alerts.alertInfo("Removido","Locatário removido com sucesso");

                } catch (RuntimeException ex) {
                    // NOVO: Captura exceções do JPA/DAO (ex: Locatário com Contratos ativos)
                    Alerts.alertError("Erro de Persistência", "Falha ao remover: " + ex.getMessage());
                }
            } else {
                Alerts.alertError("Erro", "Selecione um locatário para remover!");
            }
        });


        btnEditar.setOnAction(e -> {
            try {
                Locatario locatarioSelecionado =
                        locatarioTable.getSelectionModel().getSelectedItem();
                if (locatarioSelecionado != null){
                    // Presumindo que ModalLocatarioEdit usa locatarioDAO.salvar() internamente
                    new ModalLocatarioEdit(locatarioSelecionado).mostrar();

                    // CORRIGIDO: Usa o método JPA para atualização após o modal fechar
                    atualizarTabelaLocatarios();
                } else {
                    Alerts.alertError("Erro", "Selecione um locatário para editar");
                }
            } catch (NullPointerException ex){
                Alerts.alertError("Erro", "Nenhum locatário selecionado. Erro: " + ex.getMessage());
            }
        });



        Scene cena = new Scene(borderPane, 900, 600);
        this.stage.setScene(cena);
    }

    // ... (O restante dos métodos auxiliares, como criarTabelaLocatarios, criarMenuNavegacao, adicionarMascaraTelefone e adicionarMascaraCnpj) ...

    private TableView<Locatario> criarTabelaLocatarios() {
        TableView<Locatario> table = new TableView<>(locatariosObservable);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Locatario, String> colCNPJ = new TableColumn<>("CNPJ");
        colCNPJ.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocatarioCnpj()));

        TableColumn<Locatario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocatarioNome()));

        TableColumn<Locatario, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocatarioEmail()));

        TableColumn<Locatario, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocatarioTelefone()));

        table.getColumns().addAll(colCNPJ, colNome, colEmail, colTelefone);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        return table;
    }


    private HBox criarMenuNavegacao() {
        HBox navBar = new HBox(15);
        navBar.setStyle("-fx-padding: 10; -fx-alignment: center; -fx-background-color: lightgrey;");
        String styleBtn = "-fx-background-color: transparent; -fx-font-weight: bold;";

        Button btnHome = new Button("Home");
        btnHome.setStyle(styleBtn);
        btnHome.setOnAction(e -> new TelaInicial(stage).mostrar());

        Button btnLocatarios = new Button("Locatários");
        btnLocatarios.setStyle(styleBtn);
        btnLocatarios.setOnAction(e -> this.mostrar());


        Button btnContratos = new Button("Contratos");
        btnContratos.setStyle(styleBtn);
        btnContratos.setOnAction(e -> new ContratoView(stage).mostrar());

        Button btnLojas = new Button("Lojas");
        btnLojas.setStyle(styleBtn);
        btnLojas.setOnAction(e -> new LojaView(stage).mostrar());

        Button btnEspacos = new Button("Espaços");
        btnEspacos.setStyle(styleBtn);
        btnEspacos.setOnAction(e -> new EspacoView(stage).mostrar());


        navBar.getChildren().addAll(btnHome, btnLocatarios, btnContratos, btnLojas, btnEspacos);
        return navBar;
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


    private void adicionarMascaraCnpj(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String digitos = newValue.replaceAll("\\D", "");

            if (digitos.length() > 14) {
                digitos = digitos.substring(0, 14);
            }

            String textoFormatado = digitos;
            if (digitos.length() > 2) {
                textoFormatado = digitos.substring(0, 2) + "." + digitos.substring(2);
            }

            if (digitos.length() > 5) {
                textoFormatado = digitos.substring(0, 2) + "." + digitos.substring(2, 5) + "." + digitos.substring(5);
            }

            if (digitos.length() > 8) {
                textoFormatado = digitos.substring(0, 2) + "." + digitos.substring(2, 5) + "." + digitos.substring(5, 8) + "/" + digitos.substring(8);
            }
            if (digitos.length() > 12) {
                textoFormatado = digitos.substring(0, 2) + "." + digitos.substring(2, 5) + "." + digitos.substring(5, 8) + "/" + digitos.substring(8, 12) + "-" + digitos.substring(12);
            }

            if (!newValue.equals(textoFormatado)) {
                textField.setText(textoFormatado);
                textField.positionCaret(textoFormatado.length());
            }
        });
    }

}