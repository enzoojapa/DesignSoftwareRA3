package br.pucpr.crud_java.views;

import br.pucpr.crud_java.TelaInicial;
import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Loja;
import br.pucpr.crud_java.persistencias.LojaDAO;
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

public class LojaView {

    private final Stage stage;
    private final ObservableList<Loja> lojasObservable = FXCollections.observableArrayList();

    // NOVO: Instância do DAO JPA
    private final LojaDAO lojaDAO;

    public LojaView(Stage stage) {
        this.stage = stage;
        this.lojaDAO = new LojaDAO(); // Inicializa o DAO
    }

    public void mostrar() {
        criarUI();
        this.stage.show();
    }

    // NOVO: Método auxiliar para carregar dados do JPA
    private void atualizarTabelaLojas() {
        // Usa o método JPA de busca
        List<Loja> lojas = lojaDAO.buscarTodos();
        lojasObservable.setAll(lojas);
    }

    private void criarUI() {
        stage.setTitle("Gestão de Lojas");

        // CORRIGIDO: Usa o método JPA de busca
        atualizarTabelaLojas();

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-padding: 10;");

        HBox navBar = criarMenuNavegacao();
        borderPane.setTop(navBar);


        VBox painelFormulario = new VBox(10);
        painelFormulario.setStyle("-fx-padding: 10;");
        painelFormulario.setPrefWidth(250);

        Label labelNome = new Label("Nome da Loja");
        TextField txtNome = new TextField();
        txtNome.setPromptText("Digite o nome da loja");

        Label labelTelefone = new Label("Telefone da Loja");
        TextField txtTelefone = new TextField();
        txtTelefone.setPromptText("(XX) XXXXX-XXXX");
        adicionarMascaraTelefone(txtTelefone);

        Label labelTipo = new Label("Tipo da Loja");
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Roupas", "Joias", "Esportes", "Restaurantes", "Livros");
        cbTipo.setPromptText("Selecione o tipo");

        Button btnCadastrar = new Button("Cadastrar Loja");
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);

        Button btnAtualizar = new Button("Atualizar página");
        btnAtualizar.setMaxWidth(Double.MAX_VALUE);
        btnAtualizar.setOnAction(e -> {
            // CORRIGIDO: Usa o método JPA de atualização
            atualizarTabelaLojas();
        });

        painelFormulario.getChildren().addAll(labelNome, txtNome, labelTelefone, txtTelefone, labelTipo, cbTipo, btnCadastrar, btnAtualizar);
        borderPane.setLeft(painelFormulario);


        VBox painelTabela = new VBox(10);
        painelTabela.setStyle("-fx-padding: 10;");

        TableView<Loja> lojaTable = criarTabelaLojas();
        Button btnEditar = new Button("Editar Selecionado");
        Button btnRemover = new Button("Remover Selecionado");

        HBox painelBotoes = new HBox(10, btnEditar, btnRemover);
        borderPane.setCenter(painelTabela);

        painelTabela.getChildren().addAll(lojaTable, btnEditar, btnRemover);
        borderPane.setCenter(painelTabela);


        btnCadastrar.setOnAction(e -> {
            try {
                String nome = txtNome.getText();
                String telefone = txtTelefone.getText();
                String tipo = cbTipo.getValue();

                if (nome.isEmpty() || telefone.length() < 14 || tipo == null) {
                    throw new IllegalArgumentException("Preencha todos os campos corretamente!");
                }

                // LÓGICA ANTIGA DE CHECAGEM MANUAL REMOVIDA
                // boolean lojaExistente = lojasObservable.stream().anyMatch(...);
                // if (lojaExistente) { throw new IllegalArgumentException(...); }


                Loja novaLoja = new Loja();
                novaLoja.setLojaNome(nome);
                novaLoja.setLojaTelefone(telefone);
                novaLoja.setLojaTipo(tipo);

                // CORRIGIDO: Usa o DAO JPA para salvar. O DAO fará a checagem de unicidade.
                lojaDAO.salvar(novaLoja);

                // Se salvou com sucesso, atualiza a tabela
                atualizarTabelaLojas();

                txtNome.clear();
                txtTelefone.clear();
                cbTipo.setValue(null);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Loja cadastrada com sucesso!");

            } catch (IllegalArgumentException ex) {
                // Captura exceções de validação de regra de negócio que o DAO pode lançar
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Validação", ex.getMessage());
            } catch (RuntimeException ex) {
                // NOVO: Captura exceções do JPA/DAO (Ex: Nome ou Telefone duplicado)
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Persistência", "Falha ao salvar. Detalhes: " + ex.getMessage());
            }
        });


        btnEditar.setOnAction(e -> {
            try {
                Loja lojaSelecionada =
                        lojaTable.getSelectionModel().getSelectedItem();
                if (lojaSelecionada != null){
                    // Presumindo que ModalLojaEdit usará lojaDAO.salvar() internamente
                    new ModalLojaEdit(stage, lojaSelecionada).mostrar();

                    // CORRIGIDO: Usa o método JPA para atualização após o modal fechar
                    atualizarTabelaLojas();
                } else {
                    Alerts.alertError("Erro", "Selecione uma loja para editar");
                }
            } catch (NullPointerException ex){
                Alerts.alertError("Erro", "Nenhuma loja selecionada. Erro: " + ex.getMessage());
            }
        });

        btnRemover.setOnAction(e -> {
            Loja lojaSelecionada = lojaTable.getSelectionModel().getSelectedItem();
            if (lojaSelecionada == null) {
                exibirAlerta(Alert.AlertType.WARNING, "Nenhuma Seleção", "Por favor, selecione uma loja para remover.");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza?", ButtonType.YES, ButtonType.NO);
            confirmacao.setHeaderText("Remover a loja '" + lojaSelecionada.getLojaNome() + "'?");
            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    try {
                        String lojaNome = lojaSelecionada.getLojaNome();

                        // CORRIGIDO: Usa o método remover(nome) do DAO JPA
                        lojaDAO.remover(lojaNome);

                        lojasObservable.remove(lojaSelecionada);
                        exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Loja removida.");

                    } catch (RuntimeException ex) {
                        // NOVO: Captura exceções do JPA/DAO
                        exibirAlerta(Alert.AlertType.ERROR, "Erro de Persistência", "Falha ao remover a loja: " + ex.getMessage());
                    }
                }
            });
        });

        Scene cena = new Scene(borderPane, 900, 600);
        this.stage.setScene(cena);
    }

    private TableView<Loja> criarTabelaLojas() {
        TableView<Loja> table = new TableView<>(lojasObservable);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<Loja, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLojaNome()));

        TableColumn<Loja, String> colTel = new TableColumn<>("Telefone");
        colTel.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLojaTelefone()));

        TableColumn<Loja, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLojaTipo()));

        table.getColumns().addAll(colNome, colTel, colTipo);
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
        btnLocatarios.setOnAction(e -> new LocatarioView(stage).mostrar());

        Button btnContratos = new Button("Contratos");
        btnContratos.setStyle(styleBtn);
        btnContratos.setOnAction(e -> new ContratoView(stage).mostrar());

        Button btnLojas = new Button("Lojas");
        btnLojas.setStyle(styleBtn);
        btnLojas.setOnAction(e -> this.mostrar());

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


            if (!newValue.equals(textoFormatado)) {
                textField.setText(textoFormatado);
                textField.positionCaret(textoFormatado.length());
            }
        });
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }
}