package br.pucpr.crud_java.views;

import br.pucpr.crud_java.TelaInicial;
import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Espaco;
import br.pucpr.crud_java.persistencias.EspacoDAO; // O EspacoDAO JPA
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class EspacoView {
    private final Stage stage;
    private final ObservableList<Espaco> espacosObservable = observableArrayList();

    // NOVO: Instância do DAO JPA
    private final EspacoDAO espacoDAO;

    public EspacoView(Stage stage) {
        this.stage = stage;
        this.espacoDAO = new EspacoDAO(); // Inicializa o DAO
    }

    public void mostrar() {
        criarUI();
        this.stage.show();
    }

    private void atualizarTabelaEspacos() {
        // NOVO: Usa o método JPA de busca
        List<Espaco> espacos = espacoDAO.buscarTodos();
        espacosObservable.setAll(espacos);
    }

    private void criarUI() {
        stage.setTitle("Gestão de espaços");

        // CORRIGIDO: Usa o método JPA de busca
        atualizarTabelaEspacos();

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-padding: 10;");

        HBox navBar = criarMenuNavegacao();
        borderPane.setTop(navBar);

        VBox painelFormulario = new VBox(10);
        painelFormulario.setStyle("-fx-padding: 10;");
        painelFormulario.setPrefWidth(250);

        Label labelArea = new Label("Área do espaço (m²)");
        TextField txtArea = new TextField();
        txtArea.setPromptText("Digite a área do espaço");
        Pattern pattern = Pattern.compile("\\d*([,.]\\d{0,2})?");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        txtArea.setTextFormatter(formatter);

        Label labelPiso = new Label("Piso do espaço (1 ou 2)");
        TextField txtPiso = new TextField();
        txtPiso.setPromptText("Digite o piso do espaço");

        Button btnCad = new Button("Cadastrar Espaço");
        btnCad.setMaxWidth(Double.MAX_VALUE);

        Button btnAtualizar = new Button("Atualizar página");
        btnAtualizar.setMaxWidth(Double.MAX_VALUE);
        btnAtualizar.setOnAction(e -> {
            // CORRIGIDO: Usa o método JPA de atualização
            atualizarTabelaEspacos();
        });

        painelFormulario.getChildren().addAll(labelArea, txtArea, labelPiso, txtPiso, btnCad, btnAtualizar);
        borderPane.setLeft(painelFormulario);

        VBox painelTabela = new VBox(10);
        painelTabela.setStyle("-fx-padding: 10;");

        TableView<Espaco> espacoTable = criarTabelaEspacos();

        Button btnEditar = new Button("Editar Selecionado");
        Button btnRemover = new Button("Remover Selecionado");

        HBox painelBotoesTabela = new HBox(10, btnEditar, btnRemover);
        painelBotoesTabela.setAlignment(Pos.CENTER_LEFT);

        painelTabela.getChildren().addAll(espacoTable, painelBotoesTabela);
        borderPane.setCenter(painelTabela);

        btnCad.setOnAction(e -> {
            try {
                // Tratamento de área com vírgula para double
                String areaTexto = txtArea.getText().replace(",", ".");
                double area = Double.parseDouble(areaTexto);

                int piso = Integer.parseInt(txtPiso.getText());

                if (area <= 0 || (piso != 1 && piso != 2)) {
                    throw new IllegalArgumentException("Preencha todos os campos corretamente!\n(Área > 0 e Piso = 1 ou 2)");
                }

                // NOVO: Cria o objeto Espaco e usa o DAO JPA para salvar
                Espaco novoEspaco = new Espaco();
                novoEspaco.setArea(area);
                novoEspaco.setPiso(piso);

                espacoDAO.salvar(novoEspaco); // Método salvar() do DAO JPA

                atualizarTabelaEspacos(); // Atualiza a tabela com o JPA
                txtArea.clear();
                txtPiso.clear();
                Alerts.alertInfo("Sucesso", "Espaço cadastrado com sucesso!");

            } catch (NumberFormatException ex) {
                Alerts.alertError("Erro de Formato", "Verifique se a área e o piso são números válidos.");
            } catch (IllegalArgumentException ex) {
                Alerts.alertError("Erro de Validação", ex.getMessage());
            } catch (RuntimeException ex) {
                // NOVO: Captura exceções do JPA/DAO
                Alerts.alertError("Erro de Persistência", "Falha ao salvar o espaço: " + ex.getMessage());
            }
        });

        btnEditar.setOnAction(e -> {
            Espaco espacoSelecionado = espacoTable.getSelectionModel().getSelectedItem();
            if (espacoSelecionado == null) {
                Alerts.alertWarning("Nenhuma Seleção", "Por favor, selecione um espaço para editar.");
                return;
            }

            // Presumindo que ModalEspacoEdit usará espacoDAO.salvar() internamente
            ModalEspacoEdit modal = new ModalEspacoEdit(this.stage, espacoSelecionado);
            modal.mostrar();

            // CORRIGIDO: Atualiza a tabela após a edição com o JPA
            atualizarTabelaEspacos();
        });

        btnRemover.setOnAction(e -> {
            Espaco espacoSelecionado = espacoTable.getSelectionModel().getSelectedItem();
            if (espacoSelecionado == null) {
                Alerts.alertWarning("Nenhuma Seleção", "Por favor, selecione um espaço para remover.");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza?", ButtonType.YES, ButtonType.NO);
            // Usa getId() do JPA
            confirmacao.setHeaderText("Remover o espaço de ID '" + espacoSelecionado.getId() + "'?");
            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    try {
                        // CORRIGIDO: Usa o método remover(id) do DAO JPA
                        espacoDAO.excluir(espacoSelecionado.getId());
                        atualizarTabelaEspacos(); // Atualiza a tabela com o JPA
                        Alerts.alertInfo("Sucesso", "Espaço removido.");
                    } catch (RuntimeException ex) {
                        // NOVO: Captura exceções do JPA/DAO
                        Alerts.alertError("Erro de Persistência", "Falha ao remover o espaço: " + ex.getMessage());
                    }
                }
            });
        });

        Scene cena = new Scene(borderPane, 900, 600);
        this.stage.setScene(cena);
    }

    private TableView<Espaco> criarTabelaEspacos() {
        TableView<Espaco> table = new TableView<>(espacosObservable);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Assumindo que o getId() retorna Long
        TableColumn<Espaco, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));

        TableColumn<Espaco, String> colArea = new TableColumn<>("Área (m²)");
        colArea.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getArea())));
        TableColumn<Espaco, String> colPiso = new TableColumn<>("Piso");
        colPiso.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getPiso())));
        table.getColumns().addAll(colId, colArea, colPiso);
        return table;
    }

    // ... (O método criarMenuNavegacao não precisou de alterações no conteúdo, apenas se baseia em outras views)
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
        navBar.getChildren().addAll(btnHome, btnLocatarios, btnContratos, btnLojas, btnEspacos);
        return navBar;
    }
}