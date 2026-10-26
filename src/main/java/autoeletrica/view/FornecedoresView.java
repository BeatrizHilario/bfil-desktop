package autoeletrica.view;

import autoeletrica.dao.FornecedorDAO;
import autoeletrica.model.Fornecedor;
import autoeletrica.view.components.NovoFornecedorModal;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class FornecedoresView extends BorderPane {

    private final TableView<Fornecedor> tabela = new TableView<>();
    private final ObservableList<Fornecedor> masterData = FXCollections.observableArrayList();
    private final FilteredList<Fornecedor> filteredData = new FilteredList<>(masterData, p -> true);
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private final Stage ownerStage;
    private final TextField txtBusca = new TextField();

    public FornecedoresView(Stage ownerStage) {
        this.ownerStage = ownerStage;

        // Navbar superior padronizada
        HBox navbar = new HBox(16);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        txtBusca.setPromptText("Buscar fornecedor por razão social, nome fantasia ou contato...");
        txtBusca.getStyleClass().add("search-field");
        HBox.setHgrow(txtBusca, Priority.ALWAYS);

        Button btnAtualizar = new Button("Atualizar");
        btnAtualizar.getStyleClass().add("btn-novo-cliente");
        btnAtualizar.setGraphic(criarIconeAtualizar("#424242"));

        Button btnNovo = new Button("Novo Fornecedor");
        btnNovo.getStyleClass().add("btn-registrar-servico");
        btnNovo.setGraphic(criarIconeMais("#FFFFFF"));

        navbar.getChildren().addAll(txtBusca, btnAtualizar, btnNovo);
        setTop(navbar);

        configurarColunas();

        txtBusca.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(f -> {
                if (newVal == null || newVal.isBlank()) return true;
                String termo = newVal.toLowerCase().trim();
                return (f.getRazaoSocial() != null && f.getRazaoSocial().toLowerCase().contains(termo))
                        || (f.getNomeFantasia() != null && f.getNomeFantasia().toLowerCase().contains(termo))
                        || (f.getContato() != null && f.getContato().toLowerCase().contains(termo));
            });
        });

        tabela.setItems(filteredData);
        setCenter(tabela);

        btnNovo.setOnAction(e -> new NovoFornecedorModal(ownerStage, null, this::carregarDados).show());
        btnAtualizar.setOnAction(e -> {
            txtBusca.clear();
            carregarDados();
        });

        carregarDados();
    }

    private SVGPath criarIconeMais(String corHex) {
        SVGPath path = new SVGPath();
        path.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        path.setFill(Color.web(corHex));
        return path;
    }

    private SVGPath criarIconeAtualizar(String corHex) {
        SVGPath path = new SVGPath();
        path.setContent("M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z");
        path.setFill(Color.web(corHex));
        return path;
    }

    private void configurarColunas() {
        TableColumn<Fornecedor, String> colRazao = new TableColumn<>("Razão Social");
        colRazao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRazaoSocial()));
        colRazao.setPrefWidth(220);

        TableColumn<Fornecedor, String> colFantasia = new TableColumn<>("Nome Fantasia");
        colFantasia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomeFantasia() != null ? c.getValue().getNomeFantasia() : "-"));
        colFantasia.setPrefWidth(180);

        TableColumn<Fornecedor, String> colCnpj = new TableColumn<>("CNPJ");
        colCnpj.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCnpj() != null ? c.getValue().getCnpj() : "-"));
        colCnpj.setPrefWidth(140);

        TableColumn<Fornecedor, String> colTel = new TableColumn<>("Telefone");
        colTel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefone() != null ? c.getValue().getTelefone() : "-"));
        colTel.setPrefWidth(130);

        TableColumn<Fornecedor, String> colContato = new TableColumn<>("Contato");
        colContato.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContato() != null ? c.getValue().getContato() : "-"));
        colContato.setPrefWidth(140);

        TableColumn<Fornecedor, Void> colAcoes = new TableColumn<>("Ações");
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
                    Fornecedor f = getTableView().getItems().get(getIndex());
                    new NovoFornecedorModal(ownerStage, f, FornecedoresView.this::carregarDados).show();
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
        tabela.getColumns().addAll(colRazao, colFantasia, colCnpj, colTel, colContato, colAcoes);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void carregarDados() {
        new Thread(() -> {
            try {
                List<Fornecedor> lista = fornecedorDAO.listarTodos();
                Platform.runLater(() -> {
                    masterData.clear();
                    masterData.addAll(lista);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Erro ao carregar fornecedores: " + e.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
    }
}