package br.pucpr.crud_java.views;
import br.pucpr.crud_java.TelaInicial;
import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Boleto;
import br.pucpr.crud_java.models.Locatario;
import br.pucpr.crud_java.persistencias.ArquivoBoleto;
import br.pucpr.crud_java.persistencias.ArquivoLocatario;
import br.pucpr.crud_java.views.LocatarioView;

import br.pucpr.crud_java.models.Contrato;
import br.pucpr.crud_java.persistencias.ArquivoContrato;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static javafx.collections.FXCollections.observableArrayList;

public class ContratoView {

    private Stage stage;
    private ObservableList<Contrato> contratosObservable = observableArrayList();

    public ContratoView(Stage stage) {
        this.stage = stage;
    }

    public void mostrar() {
        criarUI();
        this.stage.show();
    }

    private void criarUI() {
        stage.setTitle("Gestão de Contratos");

        ArrayList<Contrato> contratos = ArquivoContrato.lerLista();
        ArrayList<Locatario> locatarios = ArquivoLocatario.lerLista();
        contratosObservable.setAll(contratos);

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-padding: 10;");

        HBox navBar = criarMenuNavegacao();
        borderPane.setTop(navBar);

        VBox painelFormulario = new VBox(10);
        painelFormulario.setStyle("-fx-padding: 10;");
        painelFormulario.setPrefWidth(250);

        // Campos do formulário
        Label labelNomeEmpresa = new Label("Nome de Locatário");
        ComboBox<String> locatarioComboBox = new ComboBox<>();
        locatarioComboBox.setPromptText("Selecione a empresa");
        Set<String> cnpjComContratos = new HashSet<>();
        for (Contrato c : contratos){
            cnpjComContratos.add(c.getlocatario().getLocatarioCnpj());
        }
        for (Locatario l : locatarios){
            if (!cnpjComContratos.contains(l.getLocatarioCnpj())){
                locatarioComboBox.getItems().add(l.getLocatarioNome());
            }

        }

        Label labelDataInicio = new Label("Data de Início");
        DatePicker datePickerInicio = new DatePicker(LocalDate.now());
        datePickerInicio.setEditable(false);

        Label labelValorMensal = new Label("Valor Mensal");
        TextField txtValorMensal = new TextField();
        txtValorMensal.setPromptText("Ex: 1500.50");
        adicionarFiltroApenasNumeros(txtValorMensal);


        Label labelStatus = new Label("Status do Contrato");
        CheckBox checkStatus = new CheckBox("Ativo");
        checkStatus.setSelected(true);

        Button btnCadastrar = new Button("Cadastrar Contrato");
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);

        Button btnAtualizar = new Button("Atualizar página");
        btnAtualizar.setMaxWidth(Double.MAX_VALUE);
        btnAtualizar.setOnAction(e -> {
            contratosObservable.setAll(ArquivoContrato.lerLista());
        });

        painelFormulario.getChildren().addAll(
                labelNomeEmpresa, locatarioComboBox,
                labelDataInicio, datePickerInicio,
                labelValorMensal, txtValorMensal,
                labelStatus, checkStatus,
                btnCadastrar, btnAtualizar
        );
        borderPane.setLeft(painelFormulario);

        VBox painelTabela = new VBox(10);
        painelTabela.setStyle("-fx-padding: 10;");

        TableView<Contrato> contratoTable = criarTabelaContratos();

        Button btnRemover = new Button("Remover Selecionado");
        Button btnVerBoletos = new Button("Ver boletos");
        painelTabela.getChildren().addAll(contratoTable, btnRemover,
                btnVerBoletos);
        borderPane.setCenter(painelTabela);

        btnCadastrar.setOnAction(e -> {
            try {
                String nomeEmpresa = locatarioComboBox.getValue();
                LocalDate dataInicio = datePickerInicio.getValue();
                String valorTexto = txtValorMensal.getText().replace(",", ".");
                double valorMensal = Double.parseDouble(valorTexto);
                boolean status = checkStatus.isSelected();
                ArrayList<Boleto> boletos = new ArrayList<>();
                String linhaDig = "1000000000000";
                BigInteger linhaDigNum = new BigInteger(linhaDig);

                Locatario empresa = null;
                for (Locatario l : locatarios){
                    if (nomeEmpresa.equals(l.getLocatarioNome())){
                        empresa = l;
                        break;
                    }
                }

                if (empresa == null || dataInicio == null) {
                    throw new IllegalArgumentException("Nome e Data de Início são obrigatórios.");
                }

                Contrato novoContrato = new Contrato();
                novoContrato.setLocatario(empresa);
                novoContrato.setDataInicio(dataInicio);
                novoContrato.setValorMensal(valorMensal);
                novoContrato.setContratoStatus(status);

                ArquivoContrato.adicionarContrato(novoContrato);

                for (int i = 1; i <= 12; i++) {
                    LocalDate vencimento = dataInicio.plusMonths(i);
                    linhaDigNum = linhaDigNum.add(BigInteger.ONE);
                    linhaDig = linhaDigNum.toString();
                    Boleto novoBoleto = new Boleto();
                    novoBoleto.setValor(3000);
                    novoBoleto.setVencimento(vencimento);
                    novoBoleto.setCedente("Tijucas Open");
                    novoBoleto.setBanco("Banco do Brasil");
                    novoBoleto.setLinhaDigitavel(linhaDig);
                    novoBoleto.setContrato(novoContrato);
                    ArquivoBoleto.adicionarBoleto(novoBoleto, novoContrato.getContratoId());
                }
                boletos = ArquivoBoleto.lerLista(novoContrato.getContratoId());
                novoContrato.setBoletos(boletos);
                ArquivoContrato.atualizarContrato(novoContrato);

                contratosObservable.add(novoContrato);

                locatarioComboBox.setValue(null);
                datePickerInicio.setValue(null);
                txtValorMensal.clear();
                checkStatus.setSelected(true);
                Alerts.alertInfo("Sucesso", "Contrato cadastrado com sucesso!");

            } catch (NumberFormatException ex) {
                Alerts.alertError("Erro de Formato", "IDs " +
                        "e Valor Mensal devem ser números válidos.");
            } catch (IllegalArgumentException ex) {
                Alerts.alertError("Erro de Validação", ex.getMessage());
            }
        });

        btnRemover.setOnAction(e -> {
            Contrato contratoSelecionado = contratoTable.getSelectionModel().getSelectedItem();
            if (contratoSelecionado == null) {
                Alerts.alertWarning("Nenhuma Seleção", "Por favor, selecione um " +
                        "contrato na " +
                        "tabela para remover.");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja remover o contrato selecionado?", ButtonType.YES, ButtonType.NO);
            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    ArquivoContrato.removerContrato(
                            contratoSelecionado.getContratoId());
                    contratosObservable.remove(contratoSelecionado);
                    exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Contrato removido com sucesso!");
                }
            });
        });

        btnVerBoletos.setOnAction(e -> {
            try {
                Contrato contratoSelecionado =
                        contratoTable.getSelectionModel().getSelectedItem();
                if (contratoSelecionado != null) {
                    new BoletoView(stage, contratoSelecionado).mostrar();
                } else {
                    Alerts.alertWarning("Nenhuma seleção",
                            "Por favor, selecione um " +
                                    "contrato na " +
                                    "tabela para ver boletos.");
                }
            } catch (NullPointerException ex){
                Alerts.alertError("Erro",
                        "Nenhum contrato selecionado. Erro: " + ex.getMessage());
            }
        });

        Scene cena = new Scene(borderPane, 900, 600);
        this.stage.setScene(cena);
    }

    private TableView<Contrato> criarTabelaContratos() {
        TableView<Contrato> table = new TableView<>();
        table.setItems(contratosObservable);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Contrato, String> colNomeEmpresa = new TableColumn<>("Nome Empresa");
        colNomeEmpresa.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getlocatario().getLocatarioNome()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        TableColumn<Contrato, String> colDataInicio = new TableColumn<>("Data de Início");
        colDataInicio.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDataInicio().format(formatter)));

        TableColumn<Contrato, String> colValor = new TableColumn<>("Valor Mensal");
        colValor.setCellValueFactory(cell -> new SimpleStringProperty(String.format("R$ %.2f", cell.getValue().getValorMensal())));

        TableColumn<Contrato, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isAtivo() ? "Ativo" : "Inativo"));

        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(item.equals("Ativo") ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });


        table.getColumns().addAll(colNomeEmpresa, colDataInicio, colValor, colStatus);
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
        btnContratos.setOnAction(e -> this.mostrar()); // Recarrega a tela atual

        Button btnLojas = new Button("Lojas");
        btnLojas.setStyle(styleBtn);
        btnLojas.setOnAction(e -> new LojaView(stage).mostrar());


        Button btnEspacos = new Button("Espaços");
        btnEspacos.setStyle(styleBtn);
        btnEspacos.setOnAction(e -> new EspacoView(stage).mostrar());

        navBar.getChildren().addAll(btnHome, btnLocatarios, btnContratos, btnLojas, btnEspacos);
        return navBar;
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }

    private void adicionarFiltroApenasNumeros(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^\\d*(\\.\\d{0,2}|,\\d{0,2})?$")) {
                textField.setText(oldValue);
            }
        });
    }
}