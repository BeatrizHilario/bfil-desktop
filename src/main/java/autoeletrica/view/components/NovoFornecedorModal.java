package autoeletrica.view.components;

import autoeletrica.dao.FornecedorDAO;
import autoeletrica.model.Fornecedor;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;

public class NovoFornecedorModal {

    private final Stage stage;
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private Fornecedor fornecedorAtual;
    private final Runnable onSalvo;

    // Cabeçalho e Botões
    private final Label lblTitulo = new Label("Registro de Fornecedor");
    private final Button btnSalvar = new Button("Salvar");
    private final Button btnAlterar = new Button("Editar");
    private final Button btnExcluir = new Button("Excluir");

    // Campos do Formulário
    private final TextField txtRazao = new TextField();
    private final TextField txtFantasia = new TextField();
    private final TextField txtCnpj = new TextField();
    private final TextField txtTel = new TextField();
    private final TextField txtContato = new TextField();

    public NovoFornecedorModal(Stage ownerStage) {
        this(ownerStage, null, null);
    }

    public NovoFornecedorModal(Stage ownerStage, Fornecedor fEmEdicao, Runnable onSalvo) {
        this.fornecedorAtual = fEmEdicao;
        this.onSalvo = onSalvo;

        stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setTitle("Registro de Fornecedor");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("modal-frame");

        // 1. Cabeçalho
        HBox topBar = new HBox();
        topBar.getStyleClass().add("modal-top-bar");
        lblTitulo.getStyleClass().add("modal-top-title");
        topBar.getChildren().add(lblTitulo);
        root.setTop(topBar);

        // 2. Formulário Responsivo (4 Colunas)
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

        estilizarInputs(txtRazao, txtFantasia, txtCnpj, txtTel, txtContato);

        // Linha 0
        grid.add(criarCampo("Razão Social *", txtRazao), 0, 0, 2, 1);
        grid.add(criarCampo("Nome Fantasia", txtFantasia), 2, 0, 2, 1);

        // Linha 1
        grid.add(criarCampo("CNPJ", txtCnpj), 0, 1, 2, 1);
        grid.add(criarCampo("Telefone / WhatsApp", txtTel), 2, 1, 2, 1);

        // Linha 2
        grid.add(criarCampo("Pessoa de Contato / Vendedor", txtContato), 0, 2, 4, 1);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        root.setCenter(scroll);

        // 3. Rodapé
        HBox footer = new HBox(12);
        footer.getStyleClass().add("modal-bottom-bar");

        btnSalvar.getStyleClass().add("btn-modal-primary");
        btnSalvar.setGraphic(criarIconeSalvar("#FFFFFF"));

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-modal-neutral");
        btnCancelar.setGraphic(criarIconeCancelar("#CBD5E1"));

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

        footer.getChildren().addAll(btnSalvar, btnCancelar, sep, btnIncluir, btnAlterar, btnExcluir, spacer, btnSair);
        root.setBottom(footer);

        // Modo Visualização vs Edição
        if (fornecedorAtual != null) {
            preencherCampos(fornecedorAtual);
            setModoEdicao(false); // Inicia bloqueado
            lblTitulo.setText("Visualizar Fornecedor");
        } else {
            setModoEdicao(true); // Inicia liberado para digitação
            lblTitulo.setText("Novo Fornecedor");
        }

        // Ações dos Botões
        btnCancelar.setOnAction(e -> stage.close());
        btnSair.setOnAction(e -> stage.close());

        btnIncluir.setOnAction(e -> {
            fornecedorAtual = null;
            limparFormulario();
            setModoEdicao(true);
            lblTitulo.setText("Novo Fornecedor");
            txtRazao.requestFocus();
        });

        btnAlterar.setOnAction(e -> {
            setModoEdicao(true); // Desbloqueia campos
            lblTitulo.setText("Editar Fornecedor");
            txtRazao.requestFocus();
        });

        btnSalvar.setOnAction(e -> executarSalvar());

        btnExcluir.setOnAction(e -> {
            if (fornecedorAtual == null || fornecedorAtual.getId() == null) {
                new Alert(Alert.AlertType.WARNING, "Selecione um fornecedor para excluir.", ButtonType.OK).showAndWait();
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este fornecedor?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    try {
                        fornecedorDAO.excluir(fornecedorAtual.getId());
                        stage.close();
                        if (onSalvo != null) onSalvo.run();
                    } catch (SQLException ex) {
                        new Alert(Alert.AlertType.ERROR, "Erro ao excluir: " + ex.getMessage(), ButtonType.OK).showAndWait();
                    }
                }
            });
        });

        Scene scene = new Scene(root, 820, 520);
        var css = getClass().getResource("/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setMinWidth(720);
        stage.setMinHeight(460);
        stage.setScene(scene);
    }

    private void setModoEdicao(boolean editavel) {
        boolean desabilitar = !editavel;

        txtRazao.setDisable(desabilitar);
        txtFantasia.setDisable(desabilitar);
        txtCnpj.setDisable(desabilitar);
        txtTel.setDisable(desabilitar);
        txtContato.setDisable(desabilitar);

        btnSalvar.setDisable(desabilitar);
        btnAlterar.setDisable(editavel || fornecedorAtual == null);
        btnExcluir.setDisable(fornecedorAtual == null);
    }

    private String textoSeguro(String val) {
        return val != null ? val : "";
    }

    private String obterTexto(TextField tf) {
        String t = tf.getText();
        return t != null ? t.trim() : "";
    }

    private void preencherCampos(Fornecedor f) {
        txtRazao.setText(textoSeguro(f.getRazaoSocial()));
        txtFantasia.setText(textoSeguro(f.getNomeFantasia()));
        txtCnpj.setText(textoSeguro(f.getCnpj()));
        txtTel.setText(textoSeguro(f.getTelefone()));
        txtContato.setText(textoSeguro(f.getContato()));
    }

    private void limparFormulario() {
        txtRazao.clear();
        txtFantasia.clear();
        txtCnpj.clear();
        txtTel.clear();
        txtContato.clear();
    }

    private void estilizarInputs(TextField... textFields) {
        for (TextField t : textFields) {
            t.getStyleClass().add("modal-input");
        }
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
        String razao = obterTexto(txtRazao);
        if (razao.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "A Razão Social é obrigatória.", ButtonType.OK).showAndWait();
            return;
        }
        try {
            if (fornecedorAtual == null) fornecedorAtual = new Fornecedor();
            fornecedorAtual.setRazaoSocial(razao);
            fornecedorAtual.setNomeFantasia(obterTexto(txtFantasia));
            fornecedorAtual.setCnpj(obterTexto(txtCnpj));
            fornecedorAtual.setTelefone(obterTexto(txtTel));
            fornecedorAtual.setContato(obterTexto(txtContato));

            if (fornecedorAtual.getId() == null) fornecedorDAO.salvar(fornecedorAtual);
            else fornecedorDAO.atualizar(fornecedorAtual);

            stage.close();
            if (onSalvo != null) onSalvo.run();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar: " + ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    // --- Ícones Vetoriais ---
    private SVGPath criarIconeSalvar(String c) { SVGPath p = new SVGPath(); p.setContent("M17 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V7l-4-4zm-5 16c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm3-10H5V5h10v4z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeCancelar(String c) { SVGPath p = new SVGPath(); p.setContent("M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeMais(String c) { SVGPath p = new SVGPath(); p.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeEditar(String c) { SVGPath p = new SVGPath(); p.setContent("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeLixeira(String c) { SVGPath p = new SVGPath(); p.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"); p.setFill(Color.web(c)); return p; }
    private SVGPath criarIconeSair(String c) { SVGPath p = new SVGPath(); p.setContent("M10.09 15.59L11.5 17l5-5-5-5-1.41 1.41L12.67 11H3v2h9.67l-2.58 2.59zM19 3H5c-1.11 0-2 .9-2 2v4h2V5h14v14H5v-4H3v4c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z"); p.setFill(Color.web(c)); return p; }

    public void show() { stage.showAndWait(); }
}