package br.pucpr.crud_java.views;

import br.pucpr.crud_java.TelaInicial;
import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Boleto;
import br.pucpr.crud_java.models.Contrato;
import br.pucpr.crud_java.models.Locatario;
import br.pucpr.crud_java.persistencias.ArquivoBoleto;
import br.pucpr.crud_java.persistencias.ArquivoContrato;
import br.pucpr.crud_java.persistencias.ArquivoLocatario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static javafx.collections.FXCollections.observableArrayList;

public class BoletoView {
    private Stage stage;
    private Scene cena;
    private Contrato contrato;

    private ObservableList<Boleto> boletosObservable = observableArrayList();

    public BoletoView(Stage stage, Contrato contrato) {
        this.stage = stage;
        this.contrato = contrato;
    }

    public void mostrar() {
        criarUI();
        this.stage.show();
    }

    private void criarUI() {
        stage.setTitle("Boletos");
        if (contrato == null){
            System.err.println("Erro, contrato não fornecido!");
            return;
        }
        ArrayList<Boleto> boletosDoContrato = contrato.getBoletos();
        ArrayList<Contrato> contratos = ArquivoContrato.lerLista();


        boletosObservable.setAll(boletosDoContrato);

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-padding: 10;");

        HBox navBar = criarMenuNavegacao();
        borderPane.setTop(navBar);

        VBox painelFormulario = new VBox(10);
        painelFormulario.setStyle("-fx-padding: 10;");
        painelFormulario.setPrefWidth(250);

        Label labelVal = new Label("Valor");
        TextField txtVal = new TextField();
        txtVal.setPromptText("Digite o valor");

        Label labelDataVenc = new Label("Data de vencimento");
        DatePicker datePickerVenc = new DatePicker(LocalDate.now());
        datePickerVenc.setEditable(false);

        Label labelCedente = new Label("Cedente");
        TextField txtCedente = new TextField("Tijucas Open");
        txtCedente.setEditable(false);

        Label labelBanco = new Label("Banco");
        TextField txtBanco = new TextField("Banco do Brasil");
        txtBanco.setEditable(false);

        Label labelLinhaDig = new Label("Linha digitável");
        TextField txtLinhaDig = new TextField();
        txtLinhaDig.setPromptText("Preencha a linha digitável");
        txtLinhaDig.textProperty().addListener((obs, oldText, newText) -> {
            if(!newText.matches("\\d{0,13}")) {
                txtLinhaDig.setText(oldText);
            }
        });


        Button btnCad = new Button("Cadastrar");
        btnCad.setMaxWidth(Double.MAX_VALUE);
        btnCad.setOnAction(e -> {
                    try {
                        double valor = Double.parseDouble(txtVal.getText());
                        LocalDate vencimento = datePickerVenc.getValue();
                        String cedente = txtCedente.getText();
                        String banco = txtBanco.getText();
                        String linhaDig = txtLinhaDig.getText();

                        if (valor >= 0 && vencimento != null &&
                                !linhaDig.isEmpty() && linhaDig.matches("\\d" +
                                "{1,13}") && contrato != null) {

                            Boleto novoBoleto = new Boleto();
                            novoBoleto.setValor(valor);
                            novoBoleto.setVencimento(vencimento);
                            novoBoleto.setCedente(cedente);
                            novoBoleto.setBanco(banco);
                            novoBoleto.setLinhaDigitavel(linhaDig);
                            novoBoleto.setContrato(contrato);
                            ArquivoBoleto.adicionarBoleto(novoBoleto,
                                    contrato.getContratoId());
                            boletosObservable.setAll(ArquivoBoleto.lerLista(contrato.getContratoId()));
                            Alerts.alertInfo("Cadastrado",
                                    "Boleto cadastrado com sucesso");
                            txtVal.clear();
                            txtLinhaDig.clear();
                        } else {
                            Alerts.alertError("Erro", "Preencha os campos " + "corretamente");
                        }
                    } catch (NumberFormatException ex) {
                        Alerts.alertError("Erro", "Insira dados válidos!");
                    }
                }
        );

        Button btnVoltar = new Button("Voltar");
        btnVoltar.setMaxWidth(Double.MAX_VALUE);
        btnVoltar.setOnAction(e -> {
            new ContratoView(stage).mostrar();
        });

        Button btnAtualizar = new Button("Atualizar página");
        btnAtualizar.setMaxWidth(Double.MAX_VALUE);
        btnAtualizar.setOnAction(e -> {
            boletosObservable.setAll(ArquivoBoleto.lerLista(contrato.getContratoId()));
        });


        painelFormulario.getChildren().addAll(labelVal, txtVal,
                labelDataVenc, datePickerVenc, labelCedente,
                txtCedente, labelBanco, txtBanco, labelLinhaDig, txtLinhaDig,
                btnCad, btnVoltar, btnAtualizar);

        borderPane.setLeft(painelFormulario);

        VBox painelTabela = new VBox(10);
        painelTabela.setStyle("-fx-padding: 10;");

        TableView<Boleto> boletosTable = criarTabelaBoletos();

        Button btnRemover = new Button("Remover Selecionado");
        btnRemover.setOnAction(e -> {
            try {
                Boleto boletoSelecionado =
                        boletosTable.getSelectionModel().getSelectedItem();
                if (boletoSelecionado != null) {
                    int numeroDocumento =
                            boletoSelecionado.getNumeroDocumento();
                    Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja remover o boleto selecionado?", ButtonType.YES, ButtonType.NO);
                    confirmacao.showAndWait().ifPresent(resposta -> {
                        if (resposta == ButtonType.YES) {
                            ArquivoBoleto.removerBoleto(
                                    boletoSelecionado.getNumeroDocumento(), boletoSelecionado.getContrato().getContratoId());
                            boletosObservable.remove(boletoSelecionado);
                            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Boleto removido com sucesso!");
                        }
                    });
                } else {
                    Alerts.alertError("Erro", "Selecione um boleto para " +
                            "apagar");
                }
            } catch (NullPointerException ex) {
                Alerts.alertError("Erro",
                        "Nenhum boleto selecionado. Erro: " + ex.getMessage());
            }
        });

        Button btnEditar = new Button("Editar Selecionado");
        btnEditar.setOnAction(e -> {
            try {
                Boleto boletoSelecionado =
                        boletosTable.getSelectionModel().getSelectedItem();
                if (boletoSelecionado != null){
                    new ModalBoletoEdit(stage, boletoSelecionado,
                            contrato).mostrar();
                    boletosObservable.setAll(ArquivoBoleto.lerLista(contrato.getContratoId()));
                } else {
                    Alerts.alertError("Erro", "Selecione um boleto para " +
                            "editar");
                }
            } catch (NullPointerException ex){
                Alerts.alertError("Erro",
                        "Nenhum boleto selecionado. Erro: " + ex.getMessage());
            }
        });

        HBox painelBotoesTabela = new HBox(10, btnEditar, btnRemover);
        painelBotoesTabela.setAlignment(Pos.CENTER_LEFT);

        painelTabela.getChildren().addAll(boletosTable, painelBotoesTabela);
        borderPane.setCenter(painelTabela);

        this.cena = new Scene(borderPane, 900, 600);
        this.stage.setScene(this.cena);
    }

    private TableView<Boleto> criarTabelaBoletos() {
        TableView<Boleto> boletosTable = new TableView<>();

        TableColumn<Boleto, String> colNmrDoc = new TableColumn<>("Nº Doc");
        colNmrDoc.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumeroDocumento())));

        TableColumn<Boleto, String> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(cellData -> new SimpleStringProperty((String.format("%.2f", cellData.getValue().getValor()))));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        TableColumn<Boleto, String> colData = new TableColumn<>("Vencimento");
        colData.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVencimento().format(formatter)));

        TableColumn<Boleto, String> colCed = new TableColumn<>("Cedente");
        colCed.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCedente()));

        TableColumn<Boleto, String> colBanco = new TableColumn<>("Banco");
        colBanco.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBanco()));

        TableColumn<Boleto, String> colDig = new TableColumn<>("Linha digitável");
        colDig.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLinhaDigitavel()));

        boletosTable.getColumns().addAll(colNmrDoc, colValor, colData, colCed, colBanco, colDig);

        boletosTable.setItems(boletosObservable);

        boletosTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        boletosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return boletosTable;
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

        Button btnBoletos = new Button("Boletos");
        btnBoletos.setStyle(styleBtn);
        btnBoletos.setOnAction(e -> this.mostrar());

        // Adicione aqui os outros botões quando tiver as telas prontas
        Button btnLojas = new Button("Lojas");
        btnLojas.setStyle(styleBtn);
        btnLojas.setOnAction(e -> new LojaView(stage).mostrar());
        Button btnEspacos = new Button("Espaços");
        btnEspacos.setStyle(styleBtn);
        btnEspacos.setOnAction(e -> new EspacoView(stage).mostrar());

        navBar.getChildren().addAll(btnHome, btnLocatarios, btnContratos,
                btnLojas, btnEspacos);
        return navBar;
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }

    private void atualizarTabelaBoletos() {
        boletosObservable.setAll(ArquivoBoleto.lerLista(contrato.getContratoId()));
        // A TableView se atualizará automaticamente pois está vinculada (bound) a boletosObservable
    }










}