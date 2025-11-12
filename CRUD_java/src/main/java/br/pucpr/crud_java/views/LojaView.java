package br.pucpr.crud_java.views;

import br.pucpr.crud_java.TelaInicial;
import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Boleto;
import br.pucpr.crud_java.models.Loja;
import br.pucpr.crud_java.persistencias.ArquivoBoleto;
import br.pucpr.crud_java.persistencias.ArquivoContrato;
import br.pucpr.crud_java.persistencias.ArquivoLoja;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

public class LojaView {

    private Stage stage;
    private ObservableList<Loja> lojasObservable = FXCollections.observableArrayList();

    public LojaView(Stage stage) {
        this.stage = stage;
    }

    public void mostrar() {
        criarUI();
        this.stage.show();
    }

    private void criarUI() {
        stage.setTitle("Gestão de Lojas");
        lojasObservable.setAll(ArquivoLoja.lerLista());

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
            lojasObservable.setAll(ArquivoLoja.lerLista());
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

                boolean lojaExistente = lojasObservable.stream().anyMatch(
                        loja -> loja.getLojaNome().equalsIgnoreCase(nome) || loja.getLojaTelefone().equals(telefone)
                );

                if (lojaExistente) {
                    throw new IllegalArgumentException("Já existe uma loja com o mesmo nome ou telefone.");
                }

                Loja novaLoja = new Loja();
                novaLoja.setLojaNome(nome);
                novaLoja.setLojaTelefone(telefone);
                novaLoja.setLojaTipo(tipo);
                ArquivoLoja.adicionarLoja(novaLoja);
                lojasObservable.add(novaLoja);

                txtNome.clear();
                txtTelefone.clear();
                cbTipo.setValue(null);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Loja cadastrada com sucesso!");

            } catch (IllegalArgumentException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Validação", ex.getMessage());
            }
        });


        btnEditar.setOnAction(e -> {
            try {
                Loja lojaSelecionado =
                        lojaTable.getSelectionModel().getSelectedItem();
                if (lojaSelecionado != null){
                    new ModalLojaEdit(stage, lojaSelecionado).mostrar();
                    lojasObservable.setAll(ArquivoLoja.lerLista());
                } else {
                    Alerts.alertError("Erro", "Selecione uma loja para " +
                            "editar");
                }
            } catch (NullPointerException ex){
                Alerts.alertError("Erro",
                        "Nenhuma loja selecionada. Erro: " + ex.getMessage());
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
                String lojaNome = lojaSelecionada.getLojaNome();
                if (resposta == ButtonType.YES) {
                    ArquivoLoja.removerLoja(lojaNome);
                    lojasObservable.remove(lojaSelecionada);
                    exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Loja removida.");
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

