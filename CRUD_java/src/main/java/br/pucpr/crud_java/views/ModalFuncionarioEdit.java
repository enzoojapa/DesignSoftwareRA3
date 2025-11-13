package br.pucpr.crud_java.views;

import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Funcionario;
import br.pucpr.crud_java.persistencias.FuncionarioDAO;
import br.pucpr.crud_java.models.Cargo;
import br.pucpr.crud_java.persistencias.CargoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ModalFuncionarioEdit {

    private final Stage stage;
    private final Funcionario funcionario;
    private final FuncionarioDAO funcionarioDAO;

    public ModalFuncionarioEdit(Stage stageOwner, Funcionario funcionario) {
        this.stage = new Stage();
        this.stage.initOwner(stageOwner);
        this.stage.initModality(Modality.WINDOW_MODAL);

        this.funcionario = funcionario;
        this.funcionarioDAO = new FuncionarioDAO();
    }

    public void mostrar() {
        criarUI();
        stage.showAndWait();
    }

    private void criarUI() {
        stage.setTitle("Editar Funcionário - ID: " + funcionario.getId());

        VBox painel = new VBox(10);
        painel.setStyle("-fx-padding: 20;");

        Label lblId = new Label("ID do Funcionário:");
        TextField txtId = new TextField(String.valueOf(funcionario.getId()));
        txtId.setEditable(false);

        Label lblNome = new Label("Nome:");
        TextField txtNome = new TextField(funcionario.getNome());
        txtNome.setPromptText("Digite o nome completo");

        Label lblCpf = new Label("CPF:");
        TextField txtCpf = new TextField(funcionario.getCpf());
        txtCpf.setPromptText("Ex: 123.456.789-00");

        Label labelCargo = new Label("Cargo:");
        ComboBox<Cargo> comboCargo = new ComboBox<>();
        comboCargo.setPromptText("Selecione o cargo");
        CargoDAO cargoDAO = new CargoDAO();
        comboCargo.getItems().addAll(cargoDAO.buscarTodos());

        comboCargo.setCellFactory(c -> new ListCell<>() {
            @Override
            protected void updateItem(Cargo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNome());
            }
        });
        comboCargo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cargo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNome());
            }
        });

        if (funcionario.getCargo() != null) {
            comboCargo.setValue(funcionario.getCargo());
        }

        Label lblSalario = new Label("Salário (R$):");
        TextField txtSalario = new TextField(String.format("%.2f", funcionario.getSalario()));
        txtSalario.setPromptText("Ex: 3500.00");

        Pattern pattern = Pattern.compile("\\d*([,.]\\d{0,2})?");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        txtSalario.setTextFormatter(new TextFormatter<>(filter));

        Label lblTelefone = new Label("Telefone:");
        TextField txtTelefone = new TextField(funcionario.getTelefone());
        txtTelefone.setPromptText("Ex: (41) 99999-9999");

        Label lblEmail = new Label("E-mail:");
        TextField txtEmail = new TextField(funcionario.getEmail());
        txtEmail.setPromptText("exemplo@email.com");

        Button btnSalvar = new Button("Salvar Alterações");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        btnSalvar.setOnAction(e -> {
            try {
                String novoNome = txtNome.getText().trim();
                String novoCpf = txtCpf.getText().trim();
                String novoTelefone = txtTelefone.getText().trim();
                String novoEmail = txtEmail.getText().trim();
                Cargo novoCargo = comboCargo.getValue();
                double novoSalario = Double.parseDouble(txtSalario.getText().replace(',', '.'));

                if (novoNome.isEmpty() || novoCpf.isEmpty() || novoTelefone.isEmpty()
                        || novoEmail.isEmpty() || novoSalario <= 0 || novoCargo == null) {
                    Alerts.alertError("Dados Inválidos", "Preencha todos os campos corretamente!");
                    return;
                }

                funcionario.setNome(novoNome);
                funcionario.setCpf(novoCpf);
                funcionario.setTelefone(novoTelefone);
                funcionario.setEmail(novoEmail);
                funcionario.setCargo(novoCargo);
                funcionario.setSalario(novoSalario);

                funcionarioDAO.salvar(funcionario);

                Alerts.alertInfo("Sucesso", "Funcionário editado com sucesso!");
                stage.close();

            } catch (NumberFormatException ex) {
                Alerts.alertError("Erro de Formato", "Verifique se o salário é um número válido.");
            } catch (RuntimeException ex) {
                Alerts.alertError("Erro de Persistência", "Falha ao salvar alterações: " + ex.getMessage());
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setOnAction(e -> stage.close());

        painel.getChildren().addAll(
                lblId, txtId,
                lblNome, txtNome,
                lblCpf, txtCpf,
                labelCargo, comboCargo,
                lblSalario, txtSalario,
                lblTelefone, txtTelefone,
                lblEmail, txtEmail,
                btnSalvar, btnCancelar
        );

        Scene cena = new Scene(painel, 400, 500);
        stage.setScene(cena);
    }
}
