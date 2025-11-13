package br.pucpr.crud_java.views;

import br.pucpr.crud_java.TelaInicial;
import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Cargo;
import br.pucpr.crud_java.persistencias.CargoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class CargoView {
    private final Stage stage;
    private final ObservableList<Cargo> cargosObservable = observableArrayList();

    private final CargoDAO cargoDAO;

    public CargoView(Stage stage) {
        this.stage = stage;
        this.cargoDAO = new CargoDAO();
    }

    public void mostrar() {
        criarUI();
        this.stage.show();
    }

    private void atualizarTabelaCargos() {
        List<Cargo> cargos = cargoDAO.buscarTodos();
        cargosObservable.setAll(cargos);
    }

    private void criarUI() {
        stage.setTitle("Gestão de cargos");

        atualizarTabelaCargos();

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-padding: 10;");

        HBox navBar = criarMenuNavegacao();
        borderPane.setTop(navBar);

        VBox painelFormulario = new VBox(10);
        painelFormulario.setStyle("-fx-padding: 10;");
        painelFormulario.setPrefWidth(250);

        Label labelNome = new Label("Nome");
        TextField txtNome = new TextField();
        txtNome.setPromptText("Digite o nome do cargo");

        Label labelDescricao = new Label("Descrição");
        TextField txtDescricao = new TextField();
        txtDescricao.setPromptText("Digite a descriçao do cargo");

        Button btnCad = new Button("Cadastrar Cargo");
        btnCad.setMaxWidth(Double.MAX_VALUE);

        Button btnAtualizar = new Button("Atualizar página");
        btnAtualizar.setMaxWidth(Double.MAX_VALUE);
        btnAtualizar.setOnAction(e -> {
            atualizarTabelaCargos();
        });

        painelFormulario.getChildren().addAll(labelNome, txtNome, labelDescricao, txtDescricao, btnCad, btnAtualizar);
        borderPane.setLeft(painelFormulario);

        VBox painelTabela = new VBox(10);
        painelTabela.setStyle("-fx-padding: 10;");

        TableView<Cargo> cargoTable = criarTabelaCargos();

        Button btnEditar = new Button("Editar Selecionado");
        Button btnRemover = new Button("Remover Selecionado");

        HBox painelBotoesTabela = new HBox(10, btnEditar, btnRemover);
        painelBotoesTabela.setAlignment(Pos.CENTER_LEFT);

        painelTabela.getChildren().addAll(cargoTable, painelBotoesTabela);
        borderPane.setCenter(painelTabela);

        btnCad.setOnAction(e -> {
            try {
                String nomeCargo = txtNome.getText();
                String descricaoCargo = txtDescricao.getText();

                Cargo novoCargo = new Cargo();
                novoCargo.setNome(nomeCargo);
                novoCargo.setDescricao(descricaoCargo);

                cargoDAO.criar(novoCargo);

                atualizarTabelaCargos();
                txtNome.clear();
                txtDescricao.clear();
                Alerts.alertInfo("Sucesso", "Cargo cadastrado com sucesso!");

            } catch (RuntimeException ex) {
                Alerts.alertError("Erro de Persistência", "Falha ao salvar o cargo: " + ex.getMessage());
            }
        });

        btnEditar.setOnAction(e -> {
            Cargo cargoSelecionado = cargoTable.getSelectionModel().getSelectedItem();
            if (cargoSelecionado == null) {
                Alerts.alertWarning("Nenhuma Seleção", "Por favor, selecione um cargo para editar.");
                return;
            }

            ModalCargoEdit modal = new ModalCargoEdit(this.stage, cargoSelecionado);
            modal.mostrar();

            // CORRIGIDO: Atualiza a tabela após a edição com o JPA
            atualizarTabelaCargos();
        });

        btnRemover.setOnAction(e -> {
            Cargo cargoSelecionado = cargoTable.getSelectionModel().getSelectedItem();
            if (cargoSelecionado == null) {
                Alerts.alertWarning("Nenhuma Seleção", "Por favor, selecione um cargo para remover.");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza?", ButtonType.YES, ButtonType.NO);
            // Usa getId() do JPA
            confirmacao.setHeaderText("Remover o cargo de ID '" + cargoSelecionado.getId() + "'?");
            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    try {
                        cargoDAO.excluir(cargoSelecionado.getId());
                        atualizarTabelaCargos();
                        Alerts.alertInfo("Sucesso", "Cargo removido.");
                    } catch (RuntimeException ex) {
                        Alerts.alertError("Erro de Persistência", "Falha ao remover o cargo: " + ex.getMessage());
                    }
                }
            });
        });

        Scene cena = new Scene(borderPane, 900, 600);
        this.stage.setScene(cena);
    }

    private TableView<Cargo> criarTabelaCargos() {
        TableView<Cargo> table = new TableView<>(cargosObservable);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<Cargo, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));

        TableColumn<Cargo, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        TableColumn<Cargo, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescricao()));
        table.getColumns().addAll(colId, colNome, colDescricao);
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
        btnLojas.setOnAction(e -> new LojaView(stage).mostrar());

        Button btnEspacos = new Button("Espaços");
        btnEspacos.setStyle(styleBtn);
        btnEspacos.setOnAction(e -> this.mostrar());

        Button btnCargos = new Button("Cargos");
        btnCargos.setStyle(styleBtn);
        btnCargos.setOnAction(e -> new CargoView(stage).mostrar());

        navBar.getChildren().addAll(btnHome, btnLocatarios, btnContratos, btnLojas, btnEspacos, btnCargos);

        return navBar;
    }
}
