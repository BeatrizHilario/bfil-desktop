package autoeletrica.view;

import autoeletrica.dao.ServicoDAO;
import autoeletrica.model.ServicoConsultaDTO;
import autoeletrica.view.components.NovoClienteModal;
import autoeletrica.view.components.NovoServicoModal;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class ServicosView extends BorderPane {

    private final TableView<ServicoConsultaDTO> tabela = new TableView<>();
    private final ObservableList<ServicoConsultaDTO> masterData = FXCollections.observableArrayList();
    private final FilteredList<ServicoConsultaDTO> filteredData = new FilteredList<>(masterData, p -> true);
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final Stage ownerStage;
    private final TextField txtBusca = new TextField();

    public ServicosView(Stage ownerStage) {
        this.ownerStage = ownerStage;

        // Navbar superior com altura 100px e fundo #F7F7F7
        HBox navbar = new HBox(16);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        txtBusca.setPromptText("Buscar por placa, cliente, veículo ou descrição");
        txtBusca.getStyleClass().add("search-field");
        HBox.setHgrow(txtBusca, Priority.ALWAYS);

        // Botão Novo Cliente com ícone "+"
        Button btnNovoCliente = new Button("Novo Cliente");
        btnNovoCliente.getStyleClass().add("btn-novo-cliente");
        btnNovoCliente.setGraphic(criarIconeMais("#424242"));

        // Botão Registrar Serviço com ícone "+"
        Button btnRegistrar = new Button("Registrar Serviço");
        btnRegistrar.getStyleClass().add("btn-registrar-servico");
        btnRegistrar.setGraphic(criarIconeMais("#FFFFFF"));

        navbar.getChildren().addAll(txtBusca, btnNovoCliente, btnRegistrar);
        setTop(navbar);

        // Área central com marca d'água de 471px x 266px no fundo
        StackPane centerStack = new StackPane();
        centerStack.getStyleClass().add("center-stack");
        centerStack.setAlignment(Pos.CENTER);

        configurarColunas();

        txtBusca.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(item -> {
                if (newVal == null || newVal.isBlank()) return true;
                String t = newVal.toLowerCase().trim();
                return item.getVeiculo().toLowerCase().contains(t)
                        || item.getClienteNome().toLowerCase().contains(t)
                        || item.getDescricao().toLowerCase().contains(t)
                        || item.getTelefone().toLowerCase().contains(t);
            });
        });

        txtBusca.setOnAction(e -> executarBuscaBanco());

        tabela.setItems(filteredData);
        centerStack.getChildren().add(tabela);
        setCenter(centerStack);

        btnNovoCliente.setOnAction(e -> new NovoClienteModal(ownerStage, null, this::carregarDados).show());
        btnRegistrar.setOnAction(e -> new NovoServicoModal(ownerStage, this::carregarDados).show());

        carregarDados();
    }

    private SVGPath criarIconeMais(String corHex) {
        SVGPath path = new SVGPath();
        path.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        path.setFill(Color.web(corHex));
        return path;
    }

    private void executarBuscaBanco() {
        String termo = txtBusca.getText();
        if (termo == null || termo.isBlank()) {
            carregarDados();
            return;
        }
        new Thread(() -> {
            try {
                List<ServicoConsultaDTO> lista = servicoDAO.buscarPorTermo(termo);
                Platform.runLater(() -> {
                    masterData.clear();
                    masterData.addAll(lista);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Erro ao buscar: " + e.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
    }

    private void configurarColunas() {
        TableColumn<ServicoConsultaDTO, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getData()));
        colData.setPrefWidth(95);

        TableColumn<ServicoConsultaDTO, String> colCliente = new TableColumn<>("Cliente / Razão");
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClienteNome()));
        colCliente.setPrefWidth(200);

        TableColumn<ServicoConsultaDTO, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefone()));
        colTelefone.setPrefWidth(130);

        TableColumn<ServicoConsultaDTO, String> colVeiculo = new TableColumn<>("Veículo (Placa)");
        colVeiculo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVeiculo()));
        colVeiculo.setPrefWidth(170);

        TableColumn<ServicoConsultaDTO, String> colDesc = new TableColumn<>("Serviço Realizado");
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescricao()));
        colDesc.setPrefWidth(280);

        TableColumn<ServicoConsultaDTO, String> colGarantia = new TableColumn<>("Garantia");
        colGarantia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatusGarantia()));
        colGarantia.setPrefWidth(150);

        TableColumn<ServicoConsultaDTO, String> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getValor()));
        colValor.setPrefWidth(100);

        TableColumn<ServicoConsultaDTO, Void> colAcoes = new TableColumn<>("Ações");
        colAcoes.setPrefWidth(120);
        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnVisualizar = new Button("Visualizar");

            {
                btnVisualizar.getStyleClass().add("btn-grid-view");

                SVGPath iconeOlho = new SVGPath();
                iconeOlho.setContent("M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z");
                iconeOlho.setFill(Color.web("#0052E0"));
                iconeOlho.setScaleX(0.75);
                iconeOlho.setScaleY(0.75);
                btnVisualizar.setGraphic(iconeOlho);

                btnVisualizar.setOnMouseEntered(e -> iconeOlho.setFill(Color.WHITE));
                btnVisualizar.setOnMouseExited(e -> iconeOlho.setFill(Color.web("#0052E0")));

                btnVisualizar.setOnAction(e -> {
                    ServicoConsultaDTO item = getTableView().getItems().get(getIndex());
                    new NovoServicoModal(ownerStage, item, ServicosView.this::carregarDados).show();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(btnVisualizar);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tabela.getColumns().clear();
        tabela.getColumns().addAll(colData, colCliente, colTelefone, colVeiculo, colDesc, colGarantia, colValor, colAcoes);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void carregarDados() {
        new Thread(() -> {
            try {
                List<ServicoConsultaDTO> lista = servicoDAO.listarTodos();
                Platform.runLater(() -> {
                    masterData.clear();
                    masterData.addAll(lista);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Erro ao carregar do Supabase: " + e.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
    }
}