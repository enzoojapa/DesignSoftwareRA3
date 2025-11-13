package br.pucpr.crud_java.views;

import br.pucpr.crud_java.TelaInicial;
import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Funcionario;
import br.pucpr.crud_java.persistencias.FuncionarioDAO;
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

public class FuncionarioView {
    private final Stage stage;
    private final ObservableList<Funcionario> funcionariosObservable = observableArrayList();
    private final FuncionarioDAO funcionarioDAO;

    public FuncionarioView(Stage stage) {
        this.stage = stage;
        this.funcionarioDAO = new FuncionarioDAO();
    }

    public void mostrar() {
        criarUI();
        this.stage.show();
    }

    private void atualizarTabelaFuncionarios() {
        List<Funcionario> funcionarios = funcionarioDAO.buscarTodos();
        funcionariosObservable.setAll(funcionarios);
    }

    private void criarUI() {
        stage.setTitle("Gestão de Funcionários");

        atualizarTabelaFuncionarios();

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-padding: 10;");

        HBox navBar = criarMenuNavegacao();
        borderPane.setTop(navBar);

        VBox painelFormulario = new VBox(10);
        painelFormulario.setStyle("-fx-padding: 10;");
        painelFormulario.setPrefWidth(300);

        Label labelNome = new Label("Nome:");
        TextField txtNome = new TextField();
        txtNome.setPromptText("Digite o nome completo");

        Label labelCpf = new Label("CPF:");
        TextField txtCpf = new TextField();
        txtCpf.setPromptText("Ex: 123.456.789-00");

        Label labelCargo = new Label("Cargo:");
        ComboBox<Cargo> comboCargo = new ComboBox<>();
        comboCargo.setPromptText("Digite o cargo");
        CargoDAO cargoDAO = new CargoDAO();
        comboCargo.getItems().addAll(cargoDAO.buscarTodos());


        Label labelSalario = new Label("Salário (R$):");
        TextField txtSalario = new TextField();
        txtSalario.setPromptText("Ex: 3500.00");

        Label labelTelefone = new Label("Telefone:");
        TextField txtTelefone = new TextField();
        txtTelefone.setPromptText("Ex: (41) 99999-9999");

        Label labelEmail = new Label("E-mail:");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("exemplo@email.com");

        Button btnCadastrar = new Button("Cadastrar Funcionário");
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);

        Button btnAtualizar = new Button("Atualizar Página");
        btnAtualizar.setMaxWidth(Double.MAX_VALUE);
        btnAtualizar.setOnAction(e -> atualizarTabelaFuncionarios());

        painelFormulario.getChildren().addAll(
                labelNome, txtNome,
                labelCpf, txtCpf,
                labelCargo, comboCargo,
                labelSalario, txtSalario,
                labelTelefone, txtTelefone,
                labelEmail, txtEmail,
                btnCadastrar, btnAtualizar
        );
        borderPane.setLeft(painelFormulario);

        VBox painelTabela = new VBox(10);
        painelTabela.setStyle("-fx-padding: 10;");

        TableView<Funcionario> funcionarioTable = criarTabelaFuncionarios();

        Button btnEditar = new Button("Editar Selecionado");
        Button btnRemover = new Button("Remover Selecionado");

        HBox painelBotoesTabela = new HBox(10, btnEditar, btnRemover);
        painelBotoesTabela.setAlignment(Pos.CENTER_LEFT);

        painelTabela.getChildren().addAll(funcionarioTable, painelBotoesTabela);
        borderPane.setCenter(painelTabela);

        btnCadastrar.setOnAction(e -> {
            try {
                String nome = txtNome.getText().trim();
                String cpf = txtCpf.getText().trim();
                String telefone = txtTelefone.getText().trim();
                String email = txtEmail.getText().trim();
                double salario = Double.parseDouble(txtSalario.getText().replace(",", "."));
                Cargo cargo = comboCargo.getSelectionModel().getSelectedItem();

                if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty() || email.isEmpty() || salario <= 0) {
                    throw new IllegalArgumentException("Preencha todos os campos corretamente!");
                }

                Funcionario novoFuncionario = new Funcionario();
                novoFuncionario.setNome(nome);
                novoFuncionario.setCpf(cpf);
                novoFuncionario.setSalario(salario);
                novoFuncionario.setTelefone(telefone);
                novoFuncionario.setEmail(email);
                novoFuncionario.setCargo(cargo);

                funcionarioDAO.salvar(novoFuncionario);
                atualizarTabelaFuncionarios();

                txtNome.clear();
                txtCpf.clear();
                txtSalario.clear();
                txtTelefone.clear();
                txtEmail.clear();
                comboCargo.getSelectionModel().clearSelection();

                Alerts.alertInfo("Sucesso", "Funcionário cadastrado com sucesso!");

            } catch (NumberFormatException ex) {
                Alerts.alertError("Erro de Formato", "Salário inválido. Use apenas números (ex: 2500.00).");
            } catch (IllegalArgumentException ex) {
                Alerts.alertError("Erro de Validação", ex.getMessage());
            } catch (RuntimeException ex) {
                Alerts.alertError("Erro de Persistência", "Falha ao salvar funcionário: " + ex.getMessage());
            }
        });

        btnEditar.setOnAction(e -> {
            Funcionario selecionado = funcionarioTable.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                Alerts.alertWarning("Nenhuma Seleção", "Selecione um funcionário para editar.");
                return;
            }

            ModalFuncionarioEdit modal = new ModalFuncionarioEdit(stage, selecionado);
            modal.mostrar();
            atualizarTabelaFuncionarios();
        });

        btnRemover.setOnAction(e -> {
            Funcionario selecionado = funcionarioTable.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                Alerts.alertWarning("Nenhuma Seleção", "Selecione um funcionário para remover.");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Tem certeza que deseja remover o funcionário '" + selecionado.getNome() + "'?",
                    ButtonType.YES, ButtonType.NO);
            confirmacao.setHeaderText("Remover Funcionário de ID " + selecionado.getId());
            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    try {
                        funcionarioDAO.excluir(selecionado.getId());
                        atualizarTabelaFuncionarios();
                        Alerts.alertInfo("Sucesso", "Funcionário removido.");
                    } catch (RuntimeException ex) {
                        Alerts.alertError("Erro de Persistência", "Falha ao remover: " + ex.getMessage());
                    }
                }
            });
        });

        Scene cena = new Scene(borderPane, 1000, 600);
        this.stage.setScene(cena);
    }

    private TableView<Funcionario> criarTabelaFuncionarios() {
        TableView<Funcionario> table = new TableView<>(funcionariosObservable);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<Funcionario, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));

        TableColumn<Funcionario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));

        TableColumn<Funcionario, String> colCpf = new TableColumn<>("CPF");
        colCpf.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCpf()));

        TableColumn<Funcionario, String> colCargo= new TableColumn<>("Cargo");
        colCargo.setCellValueFactory(cell -> {
            Cargo cargo = cell.getValue().getCargo();
            String nomeCargo = (cargo != null) ? cargo.getNome() : "";
            return new SimpleStringProperty(nomeCargo);
        });

        TableColumn<Funcionario, String> colSalario = new TableColumn<>("Salário (R$)");
        colSalario.setCellValueFactory(cell ->
                new SimpleStringProperty(String.format("%.2f", cell.getValue().getSalario())));

        TableColumn<Funcionario, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTelefone()));

        TableColumn<Funcionario, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));

        table.getColumns().addAll(colId, colNome, colCpf, colSalario, colTelefone, colEmail, colCargo);
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
        btnEspacos.setOnAction(e -> new EspacoView(stage).mostrar());

        Button btnFuncionarios = new Button("Funcionários");
        btnFuncionarios.setStyle(styleBtn);
        btnFuncionarios.setOnAction(e -> this.mostrar());

        navBar.getChildren().addAll(btnHome, btnLocatarios, btnContratos, btnLojas, btnEspacos, btnFuncionarios);
        return navBar;
    }
}
