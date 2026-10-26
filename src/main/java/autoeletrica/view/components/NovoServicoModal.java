package autoeletrica.view.components;

import autoeletrica.dao.ClienteDAO;
import autoeletrica.dao.EmpresaConfigDAO;
import autoeletrica.dao.ServicoDAO;
import autoeletrica.model.Cliente;
import autoeletrica.model.EmpresaConfig;
import autoeletrica.model.ServicoConsultaDTO;
import autoeletrica.util.GeradorOrdemServicoPDF;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NovoServicoModal {

    private final Stage stage;
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final EmpresaConfigDAO empresaConfigDAO = new EmpresaConfigDAO();
    private ServicoConsultaDTO servicoAtual;
    private final Runnable onSalvo;

    private final List<Cliente> listaBaseClientes = new ArrayList<>();
    private ObservableList<Cliente> clientesObservaveis;
    private FilteredList<Cliente> clientesFiltrados;

    // Cabeçalho e Botões
    private final Label lblTitulo = new Label("Registro de Serviço");
    private final Button btnSalvar = new Button("Salvar");
    private final Button btnAlterar = new Button("Editar");
    private final Button btnExcluir = new Button("Excluir");

    // Componentes do Formulário
    private final TextField txtOs = new TextField();
    private final DatePicker dpData = new DatePicker();
    private final TextField txtResponsavel = new TextField("Tavinho Auto Elétrica e Ar Condicionado");
    private final ComboBox<Cliente> cbCliente = new ComboBox<>();
    private final TextField txtCelular = new TextField();
    private final ComboBox<String> cbModelo = new ComboBox<>();
    private final TextField txtPlaca = new TextField();
    private final TextField txtValor = new TextField();
    private final TextField txtGarantiaFixo = new TextField("Nossa");
    private final ComboBox<String> cbTempoGarantia = new ComboBox<>();
    private final TextArea txtDescricao = new TextArea();

    public NovoServicoModal(Stage ownerStage, Runnable onSalvo) {
        this(ownerStage, null, onSalvo);
    }

    public NovoServicoModal(Stage ownerStage, ServicoConsultaDTO servicoEmEdicao, Runnable onSalvo) {
        this.servicoAtual = servicoEmEdicao;
        this.onSalvo = onSalvo;

        stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setTitle("Registro de Serviço / O.S");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("modal-frame");

        // 1. Cabeçalho
        HBox topBar = new HBox();
        topBar.getStyleClass().add("modal-top-bar");
        lblTitulo.getStyleClass().add("modal-top-title");
        topBar.getChildren().add(lblTitulo);
        root.setTop(topBar);

        // 2. Formulário com Grid Responsivo (4 Colunas)
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(32, 40, 32, 40));
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setStyle("-fx-background-color: #F7F7F7;");

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            grid.getColumnConstraints().add(col);
        }

        configurarEStilizarCampos();

        // Linha 0: Número da O.S | Data | Responsável
        grid.add(criarCampo("Número da O.S", txtOs), 0, 0, 1, 1);
        grid.add(criarCampo("Data", dpData), 1, 0, 1, 1);
        grid.add(criarCampo("Responsável", txtResponsavel), 2, 0, 2, 1);

        // Linha 1: Cliente | Celular
        grid.add(criarCampo("Cliente", cbCliente), 0, 1, 3, 1);
        grid.add(criarCampo("Celular", txtCelular), 3, 1, 1, 1);

        // Linha 2: Modelo do Veículo | Placa | Valor Total
        grid.add(criarCampo("Modelo do Veículo", cbModelo), 0, 2, 2, 1);
        grid.add(criarCampo("Placa", txtPlaca), 2, 2, 1, 1);
        grid.add(criarCampo("Valor Total (R$)", txtValor), 3, 2, 1, 1);

        // Linha 3: Garantia | Tempo de Garantia
        grid.add(criarCampo("Garantia", txtGarantiaFixo), 0, 3, 2, 1);
        grid.add(criarCampo("Tempo de Garantia", cbTempoGarantia), 2, 3, 2, 1);

        // Linha 4: Descrição do Serviço
        grid.add(criarCampo("Descrição do Serviço", txtDescricao), 0, 4, 4, 1);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        root.setCenter(scroll);

        // 3. Rodapé Monocromático com Ações
        HBox footer = new HBox(12);
        footer.getStyleClass().add("modal-bottom-bar");

        btnSalvar.getStyleClass().add("btn-modal-primary");
        btnSalvar.setGraphic(criarIconeSalvar("#FFFFFF"));

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-modal-neutral");
        btnCancelar.setGraphic(criarIconeCancelar("#CBD5E1"));

        Button btnImprimir = new Button("Imprimir");
        btnImprimir.getStyleClass().add("btn-modal-neutral");
        btnImprimir.setGraphic(criarIconeImpressora("#CBD5E1"));

        Separator sep = new Separator();
        sep.getStyleClass().add("modal-sep");

        Button btnIncluir = new Button("Novo");
        btnIncluir.getStyleClass().add("btn-modal-neutral");
        btnIncluir.setGraphic(criarIconeMais("#CBD5E1"));

        btnAlterar.getStyleClass().add("btn-modal-neutral");
        btnAlterar.setGraphic(criarIconeEditar("#CBD5E1"));

        btnExcluir.getStyleClass().add("btn-modal-danger");
        btnExcluir.setGraphic(criarIconeLixeira("#FCA5A5"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSair = new Button("Sair");
        btnSair.getStyleClass().add("btn-modal-neutral");
        btnSair.setGraphic(criarIconeSair("#CBD5E1"));

        footer.getChildren().addAll(btnSalvar, btnCancelar, btnImprimir, sep, btnIncluir, btnAlterar, btnExcluir, spacer, btnSair);
        root.setBottom(footer);

        // Modo Visualização vs Edição
        if (servicoAtual != null) {
            preencherCampos(servicoAtual);
            setModoEdicao(false); // Inicia bloqueado em modo visualização
            lblTitulo.setText("Visualizar Serviço / O.S");
        } else {
            setModoEdicao(true); // Inicia liberado para digitação
            lblTitulo.setText("Registro de Serviço / O.S");
        }

        // Eventos
        btnCancelar.setOnAction(e -> stage.close());
        btnSair.setOnAction(e -> stage.close());

        btnIncluir.setOnAction(e -> {
            servicoAtual = null;
            limparFormulario();
            setModoEdicao(true);
            lblTitulo.setText("Registro de Serviço / O.S");
            cbCliente.getEditor().requestFocus();
        });

        btnAlterar.setOnAction(e -> {
            setModoEdicao(true); // Desbloqueia os campos
            lblTitulo.setText("Editar Serviço / O.S");
            cbCliente.getEditor().requestFocus();
        });

        btnSalvar.setOnAction(e -> executarSalvar());
        btnImprimir.setOnAction(e -> executarImpressaoPdf());

        btnExcluir.setOnAction(e -> {
            if (servicoAtual == null || servicoAtual.getId() == null) {
                new Alert(Alert.AlertType.WARNING, "Selecione um serviço gravado para excluir.", ButtonType.OK).showAndWait();
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este serviço definitivamente?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    try {
                        servicoDAO.excluirServico(servicoAtual.getId());
                        stage.close();
                        if (onSalvo != null) onSalvo.run();
                    } catch (SQLException ex) {
                        new Alert(Alert.AlertType.ERROR, "Erro ao excluir: " + ex.getMessage(), ButtonType.OK).showAndWait();
                    }
                }
            });
        });

        Scene scene = new Scene(root, 960, 720);
        var css = getClass().getResource("/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setMinWidth(820);
        stage.setMinHeight(600);
        stage.setScene(scene);
    }

    private void setModoEdicao(boolean editavel) {
        boolean desabilitar = !editavel;

        txtOs.setDisable(true); // Número da O.S permanece sempre gerado pelo sistema
        dpData.setDisable(desabilitar);
        txtResponsavel.setDisable(desabilitar);
        cbCliente.setDisable(desabilitar);
        txtCelular.setDisable(desabilitar);
        cbModelo.setDisable(desabilitar);
        txtPlaca.setDisable(desabilitar);
        txtValor.setDisable(desabilitar);
        txtGarantiaFixo.setDisable(true); // Fixo do sistema
        cbTempoGarantia.setDisable(desabilitar);
        txtDescricao.setDisable(desabilitar);

        btnSalvar.setDisable(desabilitar);
        btnAlterar.setDisable(editavel || servicoAtual == null);
        btnExcluir.setDisable(servicoAtual == null);
    }

    private void configurarEStilizarCampos() {
        txtOs.getStyleClass().add("modal-input");
        dpData.getStyleClass().add("modal-date-picker");
        txtResponsavel.getStyleClass().add("modal-input");
        cbCliente.getStyleClass().add("modal-combo-box");
        cbCliente.setEditable(true);
        txtCelular.getStyleClass().add("modal-input");
        cbModelo.getStyleClass().add("modal-combo-box");
        cbModelo.setEditable(true);
        txtPlaca.getStyleClass().add("modal-input");
        txtValor.getStyleClass().add("modal-input");
        txtGarantiaFixo.getStyleClass().add("modal-input");
        txtGarantiaFixo.setEditable(false);
        cbTempoGarantia.getStyleClass().add("modal-combo-box");
        txtDescricao.getStyleClass().add("modal-text-area");

        dpData.setValue(LocalDate.now());

        // Carregamento assíncrono de clientes para autocompletar
        carregarClientesAssincrono();

        cbCliente.setConverter(new StringConverter<>() {
            @Override
            public String toString(Cliente c) {
                return c != null ? c.getNome() : "";
            }

            @Override
            public Cliente fromString(String string) {
                if (string == null || string.isBlank()) return null;
                return listaBaseClientes.stream()
                        .filter(c -> c.getNome() != null && c.getNome().equalsIgnoreCase(string.trim()))
                        .findFirst()
                        .orElse(null);
            }
        });

        cbCliente.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getTelefone() != null) {
                txtCelular.setText(newVal.getTelefone());
            }
        });

        cbCliente.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            Cliente selecionado = cbCliente.getSelectionModel().getSelectedItem();
            if (selecionado != null && selecionado.getNome().equals(newVal)) return;

            Platform.runLater(() -> {
                String termo = newVal == null ? "" : newVal.toLowerCase().trim();
                if (termo.isEmpty()) {
                    clientesFiltrados.setPredicate(c -> true);
                } else {
                    clientesFiltrados.setPredicate(c -> c.getNome() != null && c.getNome().toLowerCase().contains(termo));
                }
                if (!cbCliente.isShowing()) cbCliente.show();
            });
        });

        cbModelo.getItems().addAll(
                "Gol", "Palio", "Uno", "Onix", "HB20", "Corolla", "Civic",
                "Fox", "Celta", "Fiesta", "Sandeiro", "Ka", "Prisma", "Corsa",
                "Saveiro", "Strada", "Hilux", "Compass", "Renegade", "Creta"
        );

        cbTempoGarantia.getItems().addAll("Sem garantia", "1 Mês", "2 Meses", "3 Meses", "6 Meses", "1 Ano");
        cbTempoGarantia.setValue("3 Meses");

        txtDescricao.setWrapText(true);
        txtDescricao.setPrefRowCount(4);
    }

    private void carregarClientesAssincrono() {
        new Thread(() -> {
            try {
                List<Cliente> clientes = clienteDAO.listarTodos();
                Platform.runLater(() -> {
                    listaBaseClientes.clear();
                    listaBaseClientes.addAll(clientes);
                    clientesObservaveis = FXCollections.observableArrayList(listaBaseClientes);
                    clientesFiltrados = new FilteredList<>(clientesObservaveis, p -> true);
                    cbCliente.setItems(clientesFiltrados);

                    if (servicoAtual != null && servicoAtual.getClienteId() != null) {
                        listaBaseClientes.stream()
                                .filter(c -> c.getId().equals(servicoAtual.getClienteId()))
                                .findFirst()
                                .ifPresent(c -> {
                                    cbCliente.setValue(c);
                                    cbCliente.getEditor().setText(c.getNome());
                                });
                    }
                });
            } catch (SQLException e) {
                System.err.println("Erro ao listar clientes: " + e.getMessage());
            }
        }).start();
    }

    private String textoSeguro(String val) {
        return val != null ? val : "";
    }

    private String obterTexto(TextField tf) {
        String t = tf.getText();
        return t != null ? t.trim() : "";
    }

    private void preencherCampos(ServicoConsultaDTO s) {
        txtOs.setText(s.getId() != null ? String.valueOf(s.getId()) : "");
        dpData.setValue(s.getDataServicoOriginal() != null ? s.getDataServicoOriginal() : LocalDate.now());
        txtCelular.setText(textoSeguro(s.getTelefone()));
        cbModelo.setValue(textoSeguro(s.getModelo()));
        txtPlaca.setText(textoSeguro(s.getPlaca()));
        txtValor.setText(String.format("%.2f", s.getValorNumerico()).replace(",", "."));
        txtDescricao.setText(textoSeguro(s.getDescricao()));

        if (!s.isGarantiaPropria() || s.getMesesGarantia() <= 0) {
            cbTempoGarantia.setValue("Sem garantia");
        } else if (s.getMesesGarantia() == 1) {
            cbTempoGarantia.setValue("1 Mês");
        } else if (s.getMesesGarantia() == 2) {
            cbTempoGarantia.setValue("2 Meses");
        } else if (s.getMesesGarantia() == 3) {
            cbTempoGarantia.setValue("3 Meses");
        } else if (s.getMesesGarantia() == 6) {
            cbTempoGarantia.setValue("6 Meses");
        } else if (s.getMesesGarantia() == 12) {
            cbTempoGarantia.setValue("1 Ano");
        }

        if (s.getClienteNome() != null && !s.getClienteNome().equals("Sem Cliente")) {
            cbCliente.getEditor().setText(s.getClienteNome());
        }
    }

    private void limparFormulario() {
        txtOs.clear();
        dpData.setValue(LocalDate.now());
        cbCliente.getSelectionModel().clearSelection();
        cbCliente.getEditor().clear();
        if (clientesFiltrados != null) {
            clientesFiltrados.setPredicate(c -> true);
        }
        txtCelular.clear();
        cbModelo.setValue(null);
        cbModelo.getEditor().clear();
        txtPlaca.clear();
        txtValor.clear();
        cbTempoGarantia.setValue("3 Meses");
        txtDescricao.clear();
    }

    private VBox criarCampo(String titulo, Control input) {
        Label lbl = new Label(titulo);
        lbl.getStyleClass().add("modal-label");
        if (input instanceof Region) {
            ((Region) input).setMaxWidth(Double.MAX_VALUE);
        }
        VBox box = new VBox(6, lbl, input);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private void executarSalvar() {
        Cliente c = cbCliente.getValue();
        if (c == null && cbCliente.getEditor().getText() != null && !cbCliente.getEditor().getText().isBlank()) {
            String textoDigitado = cbCliente.getEditor().getText().trim();
            c = listaBaseClientes.stream()
                    .filter(cli -> cli.getNome() != null && cli.getNome().equalsIgnoreCase(textoDigitado))
                    .findFirst()
                    .orElse(null);
        }

        Long clienteId = c != null ? c.getId() : null;
        LocalDate data = dpData.getValue() != null ? dpData.getValue() : LocalDate.now();
        String modelo = cbModelo.getValue() != null ? cbModelo.getValue().trim() : cbModelo.getEditor().getText().trim();
        String placa = obterTexto(txtPlaca).toUpperCase();
        String descricao = txtDescricao.getText() != null ? txtDescricao.getText().trim() : "";

        String valorStr = obterTexto(txtValor).replace(",", ".");
        double valor = 0.0;
        if (!valorStr.isBlank()) {
            try {
                valor = Double.parseDouble(valorStr);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Valor total inválido. Utilize o formato 0.00", ButtonType.OK).showAndWait();
                return;
            }
        }

        String garantiaSelecionada = cbTempoGarantia.getValue() != null ? cbTempoGarantia.getValue() : "Sem garantia";
        boolean temGarantia = !garantiaSelecionada.equals("Sem garantia");
        int meses = switch (garantiaSelecionada) {
            case "1 Mês" -> 1;
            case "2 Meses" -> 2;
            case "3 Meses" -> 3;
            case "6 Meses" -> 6;
            case "1 Ano" -> 12;
            default -> 0;
        };

        try {
            if (servicoAtual == null || servicoAtual.getId() == null) {
                servicoDAO.salvarServico(clienteId, data, modelo, placa, descricao, valor, temGarantia, meses);
            } else {
                servicoDAO.atualizarServico(servicoAtual.getId(), clienteId, data, modelo, placa, descricao, valor, temGarantia, meses);
            }

            stage.close();
            if (onSalvo != null) onSalvo.run();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao gravar serviço: " + ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void executarImpressaoPdf() {
        if (servicoAtual == null) {
            new Alert(Alert.AlertType.WARNING, "Grave o serviço antes de gerar a impressão.", ButtonType.OK).showAndWait();
            return;
        }

        try {
            EmpresaConfig empresa = empresaConfigDAO.carregar();
            Cliente cliente = null;
            if (servicoAtual.getClienteId() != null) {
                cliente = listaBaseClientes.stream()
                        .filter(c -> c.getId().equals(servicoAtual.getClienteId()))
                        .findFirst()
                        .orElse(null);
            }

            GeradorOrdemServicoPDF.gerarOrdemServico(empresa, cliente, servicoAtual);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Falha ao gerar o documento PDF: " + ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    // --- Ícones Vetoriais ---
    private SVGPath criarIconeSalvar(String c) { SVGPath p = new SVGPath(); p.setContent("M17 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V7l-4-4zm-5 16c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm3-10H5V5h10v4z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeCancelar(String c) { SVGPath p = new SVGPath(); p.setContent("M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeMais(String c) { SVGPath p = new SVGPath(); p.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeEditar(String c) { SVGPath p = new SVGPath(); p.setContent("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeLixeira(String c) { SVGPath p = new SVGPath(); p.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeSair(String c) { SVGPath p = new SVGPath(); p.setContent("M10.09 15.59L11.5 17l5-5-5-5-1.41 1.41L12.67 11H3v2h9.67l-2.58 2.59zM19 3H5c-1.11 0-2 .9-2 2v4h2V5h14v14H5v-4H3v4c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeImpressora(String c) { SVGPath p = new SVGPath(); p.setContent("M19 8H5c-1.66 0-3 1.34-3 3v6h4v4h12v-4h4v-6c0-1.66-1.34-3-3-3zm-3 11H8v-5h8v5zm3-7c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm-1-9H6v4h12V3z"); p.setFill(Color.web(c)); return p; }

    public void show() { stage.showAndWait(); }
}