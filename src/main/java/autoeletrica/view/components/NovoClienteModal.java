package autoeletrica.view.components;

import autoeletrica.dao.ClienteDAO;
import autoeletrica.model.Cliente;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;

public class NovoClienteModal {

    private final Stage stage;
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Cliente clienteAtual;
    private final Runnable onSalvo;

    // Cabeçalho e Botões de Ação
    private final Label lblTitulo = new Label("Registro de Cliente");
    private final Button btnSalvar = new Button("Salvar");
    private final Button btnAlterar = new Button("Editar");
    private final Button btnExcluir = new Button("Excluir");

    // Campos do Formulário
    private final ComboBox<String> cbTipo = new ComboBox<>();
    private final TextField txtNome = new TextField();
    private final TextField txtNomeFantasia = new TextField();
    private final TextField txtCep = new TextField();
    private final ComboBox<String> cbUf = new ComboBox<>();
    private final TextField txtCidade = new TextField();
    private final TextField txtEndereco = new TextField();
    private final TextField txtNumero = new TextField();
    private final TextField txtBairro = new TextField();
    private final TextField txtComplemento = new TextField();
    private final TextField txtCpfCnpj = new TextField();
    private final TextField txtIe = new TextField();
    private final TextField txtTelefone = new TextField();
    private final TextField txtEmail = new TextField();

    public NovoClienteModal(Stage ownerStage) {
        this(ownerStage, null, null);
    }

    public NovoClienteModal(Stage ownerStage, Cliente clienteParaEditar, Runnable onSalvo) {
        this.clienteAtual = clienteParaEditar;
        this.onSalvo = onSalvo;

        stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setTitle("Registro de Cliente");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("modal-frame");

        // 1. Cabeçalho
        HBox topBar = new HBox();
        topBar.getStyleClass().add("modal-top-bar");
        lblTitulo.getStyleClass().add("modal-top-title");
        topBar.getChildren().add(lblTitulo);
        root.setTop(topBar);

        // 2. Formulário
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

        cbTipo.getItems().addAll("Pessoa Física", "Pessoa Jurídica");
        cbTipo.getSelectionModel().selectFirst();

        cbUf.getItems().addAll(
                "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
                "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
                "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
        );

        estilizarInputs(cbTipo, txtNome, txtNomeFantasia, txtCep, cbUf, txtCidade,
                txtEndereco, txtNumero, txtBairro, txtComplemento, txtCpfCnpj, txtIe, txtTelefone, txtEmail);

        // Montagem do Grid
        grid.add(criarCampo("Nome / Razão Social", txtNome), 0, 0, 2, 1);
        grid.add(criarCampo("Nome Fantasia", txtNomeFantasia), 2, 0, 2, 1);

        grid.add(criarCampo("CEP", txtCep), 0, 1, 1, 1);
        grid.add(criarCampo("UF", cbUf), 1, 1, 1, 1);
        grid.add(criarCampo("Cidade", txtCidade), 2, 1, 2, 1);

        grid.add(criarCampo("Endereço", txtEndereco), 0, 2, 3, 1);
        grid.add(criarCampo("Número", txtNumero), 3, 2, 1, 1);

        grid.add(criarCampo("Bairro", txtBairro), 0, 3, 2, 1);
        grid.add(criarCampo("Complemento", txtComplemento), 2, 3, 2, 1);

        grid.add(criarCampo("P. Física ou Jurídica", cbTipo), 0, 4, 1, 1);
        grid.add(criarCampo("CPF / CNPJ", txtCpfCnpj), 1, 4, 1, 1);
        grid.add(criarCampo("Inscrição Estadual - IE", txtIe), 2, 4, 2, 1);

        grid.add(criarCampo("Celular / Telefone", txtTelefone), 0, 5, 2, 1);
        grid.add(criarCampo("E-mail", txtEmail), 2, 5, 2, 1);

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

        // Inicialização de Estado (Novo vs Visualizar)
        if (clienteAtual != null) {
            preencherCampos(clienteAtual);
            setModoEdicao(false); // Inicia bloqueado em modo visualização
            lblTitulo.setText("Visualizar Cliente");
        } else {
            setModoEdicao(true); // Inicia liberado para digitação
            lblTitulo.setText("Novo Cliente");
        }

        // Eventos dos botões
        btnCancelar.setOnAction(e -> stage.close());
        btnSair.setOnAction(e -> stage.close());

        btnIncluir.setOnAction(e -> {
            clienteAtual = null;
            limparFormulario();
            setModoEdicao(true);
            lblTitulo.setText("Novo Cliente");
            txtNome.requestFocus();
        });

        btnAlterar.setOnAction(e -> {
            setModoEdicao(true); // Libera os campos para edição
            lblTitulo.setText("Editar Cliente");
            txtNome.requestFocus();
        });

        btnSalvar.setOnAction(e -> executarSalvar());

        btnExcluir.setOnAction(e -> {
            if (clienteAtual == null || clienteAtual.getId() == null) {
                new Alert(Alert.AlertType.WARNING, "Nenhum cliente selecionado.", ButtonType.OK).showAndWait();
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este cliente?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    try {
                        clienteDAO.excluir(clienteAtual.getId());
                        stage.close();
                        if (onSalvo != null) onSalvo.run();
                    } catch (SQLException ex) {
                        new Alert(Alert.AlertType.ERROR, "Erro ao excluir: " + ex.getMessage(), ButtonType.OK).showAndWait();
                    }
                }
            });
        });

        Scene scene = new Scene(root, 960, 680);
        var css = getClass().getResource("/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setMinWidth(720);
        stage.setMinHeight(560);
        stage.setScene(scene);
    }

    private void setModoEdicao(boolean editavel) {
        boolean desabilitar = !editavel;

        txtNome.setDisable(desabilitar);
        txtNomeFantasia.setDisable(desabilitar);
        txtCep.setDisable(desabilitar);
        cbUf.setDisable(desabilitar);
        txtCidade.setDisable(desabilitar);
        txtEndereco.setDisable(desabilitar);
        txtNumero.setDisable(desabilitar);
        txtBairro.setDisable(desabilitar);
        txtComplemento.setDisable(desabilitar);
        cbTipo.setDisable(desabilitar);
        txtCpfCnpj.setDisable(desabilitar);
        txtIe.setDisable(desabilitar);
        txtTelefone.setDisable(desabilitar);
        txtEmail.setDisable(desabilitar);

        btnSalvar.setDisable(desabilitar);
        btnAlterar.setDisable(editavel || clienteAtual == null);
        btnExcluir.setDisable(clienteAtual == null);
    }

    private void estilizarInputs(Control... controls) {
        for (Control c : controls) {
            if (c instanceof TextField) c.getStyleClass().add("modal-input");
            if (c instanceof ComboBox) c.getStyleClass().add("modal-combo-box");
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

    private String textoSeguro(String val) {
        return val != null ? val : "";
    }

    private String obterTexto(TextField tf) {
        String t = tf.getText();
        return t != null ? t.trim() : "";
    }

    private void preencherCampos(Cliente c) {
        cbTipo.setValue("JURIDICA".equals(c.getTipo()) ? "Pessoa Jurídica" : "Pessoa Física");

        txtNome.setText(textoSeguro(c.getNome()));
        txtNomeFantasia.setText(textoSeguro(c.getNomeFantasia()));
        txtCpfCnpj.setText(textoSeguro(c.getCpfCnpj()));
        txtIe.setText(textoSeguro(c.getInscricaoEstadual()));
        txtTelefone.setText(textoSeguro(c.getTelefone()));
        txtEmail.setText(textoSeguro(c.getEmail()));

        String end = textoSeguro(c.getEndereco());

        // 1. Extrai CEP se existir
        if (end.contains("CEP:")) {
            int idx = end.indexOf("CEP:");
            txtCep.setText(end.substring(idx + 4).trim());
            end = end.substring(0, idx).replaceAll("-\\s*$", "").trim();
        } else {
            txtCep.clear();
        }

        // 2. Extrai Cidade / UF entre parênteses "(Cidade/UF)"
        if (end.contains("(") && end.contains("/") && end.contains(")")) {
            try {
                int iOpen = end.lastIndexOf("(");
                int iClose = end.lastIndexOf(")");
                String dentro = end.substring(iOpen + 1, iClose);
                String[] partes = dentro.split("/");
                if (partes.length == 2) {
                    txtCidade.setText(partes[0].trim());
                    cbUf.setValue(partes[1].trim());
                    end = (end.substring(0, iOpen) + end.substring(iClose + 1)).trim();
                }
            } catch (Exception ignored) {}
        } else {
            txtCidade.clear();
            cbUf.getSelectionModel().clearSelection();
        }

        // 3. Extrai Complemento
        if (end.contains("Compl:")) {
            int idx = end.indexOf("Compl:");
            txtComplemento.setText(end.substring(idx + 6).replaceAll("^-\\s*", "").trim());
            end = end.substring(0, idx).replaceAll("-\\s*$", "").trim();
        } else {
            txtComplemento.clear();
        }

        // 4. Extrai Bairro
        if (end.contains("Bairro:")) {
            int idx = end.indexOf("Bairro:");
            txtBairro.setText(end.substring(idx + 7).replaceAll("^-\\s*", "").trim());
            end = end.substring(0, idx).replaceAll("-\\s*$", "").trim();
        } else {
            txtBairro.clear();
        }

        // 5. Separa Logradouro e Número
        if (end.contains(",")) {
            String[] partes = end.split(",", 2);
            txtEndereco.setText(partes[0].trim());
            txtNumero.setText(partes[1].replaceAll("^[-\\s]+", "").trim());
        } else {
            txtEndereco.setText(end.trim());
            txtNumero.clear();
        }
    }

    private void limparFormulario() {
        cbTipo.getSelectionModel().selectFirst();
        cbUf.getSelectionModel().clearSelection();
        txtNome.clear();
        txtNomeFantasia.clear();
        txtCep.clear();
        txtCidade.clear();
        txtEndereco.clear();
        txtNumero.clear();
        txtBairro.clear();
        txtComplemento.clear();
        txtCpfCnpj.clear();
        txtIe.clear();
        txtTelefone.clear();
        txtEmail.clear();
    }

    private void executarSalvar() {
        String nome = obterTexto(txtNome);
        if (nome.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "O campo Nome / Razão Social é obrigatório.", ButtonType.OK).showAndWait();
            return;
        }

        try {
            if (clienteAtual == null) {
                clienteAtual = new Cliente();
            }

            String tipoSelecionado = "Pessoa Jurídica".equals(cbTipo.getValue()) ? "JURIDICA" : "FISICA";
            clienteAtual.setTipo(tipoSelecionado);
            clienteAtual.setNome(nome);
            clienteAtual.setNomeFantasia(obterTexto(txtNomeFantasia));
            clienteAtual.setCpfCnpj(obterTexto(txtCpfCnpj));
            clienteAtual.setInscricaoEstadual(obterTexto(txtIe));
            clienteAtual.setTelefone(obterTexto(txtTelefone));
            clienteAtual.setEmail(obterTexto(txtEmail));

            // Concatena todos os elementos do endereço de forma padronizada
            StringBuilder endBuilder = new StringBuilder();
            String logradouro = obterTexto(txtEndereco);
            String numero = obterTexto(txtNumero);
            String bairro = obterTexto(txtBairro);
            String complemento = obterTexto(txtComplemento);
            String cidade = obterTexto(txtCidade);
            String uf = cbUf.getValue() != null ? cbUf.getValue().trim() : "";
            String cep = obterTexto(txtCep);

            endBuilder.append(logradouro);
            if (!numero.isBlank()) endBuilder.append(", ").append(numero);
            if (!bairro.isBlank()) endBuilder.append(" - Bairro: ").append(bairro);
            if (!complemento.isBlank()) endBuilder.append(" - Compl: ").append(complemento);
            if (!cidade.isBlank() || !uf.isBlank()) {
                endBuilder.append(" (").append(cidade).append("/").append(uf).append(")");
            }
            if (!cep.isBlank()) endBuilder.append(" - CEP: ").append(cep);

            clienteAtual.setEndereco(endBuilder.toString().trim());

            if (clienteAtual.getId() == null) {
                clienteDAO.salvar(clienteAtual);
            } else {
                clienteDAO.atualizar(clienteAtual);
            }

            stage.close();
            if (onSalvo != null) onSalvo.run();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao gravar: " + ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    // --- ÍCONES VETORIAIS ---
    private SVGPath criarIconeSalvar(String cor) {
        SVGPath p = new SVGPath();
        p.setContent("M17 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V7l-4-4zm-5 16c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm3-10H5V5h10v4z");
        p.setFill(Color.web(cor));
        return p;
    }

    private SVGPath criarIconeCancelar(String cor) {
        SVGPath p = new SVGPath();
        p.setContent("M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z");
        p.setFill(Color.web(cor));
        return p;
    }

    private SVGPath criarIconeMais(String cor) {
        SVGPath p = new SVGPath();
        p.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        p.setFill(Color.web(cor));
        return p;
    }

    private SVGPath criarIconeEditar(String cor) {
        SVGPath p = new SVGPath();
        p.setContent("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z");
        p.setFill(Color.web(cor));
        return p;
    }

    private SVGPath criarIconeLixeira(String cor) {
        SVGPath p = new SVGPath();
        p.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        p.setFill(Color.web(cor));
        return p;
    }

    private SVGPath criarIconeSair(String cor) {
        SVGPath p = new SVGPath();
        p.setContent("M10.09 15.59L11.5 17l5-5-5-5-1.41 1.41L12.67 11H3v2h9.67l-2.58 2.59zM19 3H5c-1.11 0-2 .9-2 2v4h2V5h14v14H5v-4H3v4c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z");
        p.setFill(Color.web(cor));
        return p;
    }

    public void show() {
        stage.showAndWait();
    }
}