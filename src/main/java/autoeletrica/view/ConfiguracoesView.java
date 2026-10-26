package autoeletrica.view;

import autoeletrica.dao.EmpresaConfigDAO;
import autoeletrica.dao.UsuarioDAO;
import autoeletrica.model.EmpresaConfig;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.sql.SQLException;

public class ConfiguracoesView extends BorderPane {

    private final EmpresaConfigDAO configDAO = new EmpresaConfigDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Campos da Empresa
    private final TextField txtNomeFantasia = new TextField();
    private final TextField txtRazaoSocial = new TextField();
    private final TextField txtCnpj = new TextField();
    private final TextField txtIe = new TextField();
    private final TextField txtTelefone = new TextField();
    private final TextField txtEmail = new TextField();
    private final TextField txtLogradouro = new TextField();
    private final TextField txtNumero = new TextField();
    private final TextField txtBairro = new TextField();
    private final TextField txtCidade = new TextField();
    private final ComboBox<String> cbUf = new ComboBox<>();
    private final TextField txtCep = new TextField();
    private final TextArea txtMensagemRodape = new TextArea();

    // Campos de Segurança / Credenciais
    private final TextField txtLoginUsuario = new TextField();
    private final TextField txtSenhaAtual = new TextField();
    private final TextField txtNovaSenha = new TextField();
    private final TextField txtConfirmaSenha = new TextField();
    private final Label lblStatusSeguranca = new Label();

    public ConfiguracoesView() {
        // Barra Superior
        HBox navbar = new HBox();
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitulo = new Label("Configurações do Sistema");
        lblTitulo.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #192637;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSalvar = new Button("Salvar Dados da Empresa");
        btnSalvar.getStyleClass().add("btn-registrar-servico");
        btnSalvar.setGraphic(criarIconeSalvar());

        navbar.getChildren().addAll(lblTitulo, spacer, btnSalvar);
        setTop(navbar);

        // Formulário da Empresa (Grelha de 4 colunas)
        GridPane gridEmpresa = new GridPane();
        gridEmpresa.setPadding(new Insets(24, 32, 24, 32));
        gridEmpresa.setHgap(16);
        gridEmpresa.setVgap(16);
        gridEmpresa.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            gridEmpresa.getColumnConstraints().add(col);
        }

        cbUf.getItems().addAll("SP", "PR", "MG", "RJ", "SC", "RS", "MS", "MT", "GO", "DF", "BA", "Outro");

        estilizarCampos();

        // Linha 0: Nome Fantasia | Razão Social
        gridEmpresa.add(criarCampo("Nome Fantasia (Aparece no topo)", txtNomeFantasia), 0, 0, 2, 1);
        gridEmpresa.add(criarCampo("Nome Empresarial / Razão Social", txtRazaoSocial), 2, 0, 2, 1);

        // Linha 1: CNPJ | IE | Telefones
        gridEmpresa.add(criarCampo("CNPJ", txtCnpj), 0, 1, 1, 1);
        gridEmpresa.add(criarCampo("Inscrição Estadual (IE)", txtIe), 1, 1, 1, 1);
        gridEmpresa.add(criarCampo("Telefones / WhatsApp", txtTelefone), 2, 1, 2, 1);

        // Linha 2: E-mail | Logradouro | Número
        gridEmpresa.add(criarCampo("E-mail Comercial", txtEmail), 0, 2, 2, 1);
        gridEmpresa.add(criarCampo("Logradouro (Rua / Avenida)", txtLogradouro), 2, 2, 1, 1);
        gridEmpresa.add(criarCampo("Nº", txtNumero), 3, 2, 1, 1);

        // Linha 3: Bairro | Cidade | UF | CEP
        gridEmpresa.add(criarCampo("Bairro", txtBairro), 0, 3, 1, 1);
        gridEmpresa.add(criarCampo("Município / Cidade", txtCidade), 1, 3, 1, 1);
        gridEmpresa.add(criarCampo("UF", cbUf), 2, 3, 1, 1);
        gridEmpresa.add(criarCampo("CEP", txtCep), 3, 3, 1, 1);

        // Linha 4: Mensagem do comprovativo
        txtMensagemRodape.setPrefRowCount(3);
        txtMensagemRodape.setWrapText(true);
        gridEmpresa.add(criarCampo("Texto de Retirada / Termo do Comprovante", txtMensagemRodape), 0, 4, 4, 1);

        // Cartão de Credenciais de Acesso
        VBox cardSeguranca = criarCardAcessoSeguranca();

        // Contentor Geral com Scroll
        VBox content = new VBox(24, gridEmpresa, cardSeguranca);
        content.setPadding(new Insets(24, 32, 32, 32));
        content.setStyle("-fx-background-color: #F8FAFC;");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        setCenter(scroll);

        btnSalvar.setOnAction(e -> salvarDados());

        carregarDados();
    }

    private void estilizarCampos() {
        txtNomeFantasia.getStyleClass().add("modal-input");
        txtRazaoSocial.getStyleClass().add("modal-input");
        txtCnpj.getStyleClass().add("modal-input");
        txtIe.getStyleClass().add("modal-input");
        txtTelefone.getStyleClass().add("modal-input");
        txtEmail.getStyleClass().add("modal-input");
        txtLogradouro.getStyleClass().add("modal-input");
        txtNumero.getStyleClass().add("modal-input");
        txtBairro.getStyleClass().add("modal-input");
        txtCidade.getStyleClass().add("modal-input");
        cbUf.getStyleClass().add("modal-combo-box");
        txtCep.getStyleClass().add("modal-input");
        txtMensagemRodape.getStyleClass().add("modal-text-area");

        txtLoginUsuario.getStyleClass().add("modal-input");
        txtSenhaAtual.getStyleClass().add("modal-input");
        txtNovaSenha.getStyleClass().add("modal-input");
        txtConfirmaSenha.getStyleClass().add("modal-input");
    }

    private VBox criarCampo(String titulo, Control input) {
        Label lbl = new Label(titulo);
        lbl.getStyleClass().add("modal-label");
        if (input instanceof Region) ((Region) input).setMaxWidth(Double.MAX_VALUE);
        VBox box = new VBox(6, lbl, input);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private VBox criarCardAcessoSeguranca() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(24, 32, 24, 32));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label lblTitulo = new Label("Credenciais de Acesso");
        lblTitulo.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #192637;");

        Label lblSub = new Label("Altere o nome de utilizador ou a palavra-passe de autenticação no sistema.");
        lblSub.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12px; -fx-text-fill: #64748B;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            grid.getColumnConstraints().add(col);
        }

        grid.add(criarCampo("Utilizador", txtLoginUsuario), 0, 0, 2, 1);
        grid.add(criarCampo("Palavra-passe Atual (confirmação)", txtSenhaAtual), 2, 0, 2, 1);
        grid.add(criarCampo("Nova Palavra-passe", txtNovaSenha), 0, 1, 2, 1);
        grid.add(criarCampo("Confirmar Nova Palavra-passe", txtConfirmaSenha), 2, 1, 2, 1);

        Button btnSalvarCredenciais = new Button("Salvar Novas Credenciais");
        btnSalvarCredenciais.getStyleClass().add("btn-registrar-servico");
        btnSalvarCredenciais.setOnAction(e -> salvarNovasCredenciais());

        lblStatusSeguranca.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 13px; -fx-font-weight: bold;");

        HBox footer = new HBox(16, btnSalvarCredenciais, lblStatusSeguranca);
        footer.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(lblTitulo, lblSub, grid, footer);
        return card;
    }

    public void carregarDados() {
        new Thread(() -> {
            try {
                EmpresaConfig cfg = configDAO.carregar();
                Platform.runLater(() -> {
                    if (cfg != null) {
                        txtNomeFantasia.setText(cfg.getNomeFantasia());
                        txtRazaoSocial.setText(cfg.getRazaoSocial());
                        txtCnpj.setText(cfg.getCnpj());
                        txtIe.setText(cfg.getInscricaoEstadual());
                        txtTelefone.setText(cfg.getTelefone());
                        txtEmail.setText(cfg.getEmail());
                        txtLogradouro.setText(cfg.getLogradouro());
                        txtNumero.setText(cfg.getNumero());
                        txtBairro.setText(cfg.getBairro());
                        txtCidade.setText(cfg.getCidade());
                        cbUf.setValue(cfg.getUf() != null ? cfg.getUf() : "SP");
                        txtCep.setText(cfg.getCep());
                        txtMensagemRodape.setText(cfg.getMensagemRodape());
                    }
                });
            } catch (SQLException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Erro ao carregar configurações: " + e.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();

        new Thread(() -> {
            try {
                String usuario = usuarioDAO.buscarUsuarioPrincipal();
                Platform.runLater(() -> txtLoginUsuario.setText(usuario));
            } catch (Exception e) {}
        }).start();
    }

    private void salvarDados() {
        EmpresaConfig cfg = new EmpresaConfig();
        cfg.setNomeFantasia(txtNomeFantasia.getText().trim());
        cfg.setRazaoSocial(txtRazaoSocial.getText().trim());
        cfg.setCnpj(txtCnpj.getText().trim());
        cfg.setInscricaoEstadual(txtIe.getText().trim());
        cfg.setTelefone(txtTelefone.getText().trim());
        cfg.setEmail(txtEmail.getText().trim());
        cfg.setLogradouro(txtLogradouro.getText().trim());
        cfg.setNumero(txtNumero.getText().trim());
        cfg.setBairro(txtBairro.getText().trim());
        cfg.setCidade(txtCidade.getText().trim());
        cfg.setUf(cbUf.getValue());
        cfg.setCep(txtCep.getText().trim());
        cfg.setMensagemRodape(txtMensagemRodape.getText().trim());

        new Thread(() -> {
            try {
                configDAO.salvar(cfg);
                Platform.runLater(() -> {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Dados da empresa atualizados com sucesso!", ButtonType.OK);
                    a.showAndWait();
                });
            } catch (SQLException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Erro ao gravar configurações: " + e.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
    }

    private void salvarNovasCredenciais() {
        String novoUser = txtLoginUsuario.getText().trim();
        String passAtual = txtSenhaAtual.getText().trim();
        String novaPass = txtNovaSenha.getText().trim();
        String confirmaPass = txtConfirmaSenha.getText().trim();

        if (novoUser.isBlank() || passAtual.isBlank()) {
            lblStatusSeguranca.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            lblStatusSeguranca.setText("Preencha o utilizador e a palavra-passe atual.");
            return;
        }

        String senhaDefinitiva = novaPass.isBlank() ? passAtual : novaPass;

        if (!novaPass.isBlank() && !novaPass.equals(confirmaPass)) {
            lblStatusSeguranca.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            lblStatusSeguranca.setText("A nova palavra-passe e a confirmação não coincidem.");
            return;
        }

        new Thread(() -> {
            try {
                String usuarioAtual = usuarioDAO.buscarUsuarioPrincipal();
                boolean sucesso = usuarioDAO.atualizarCredenciais(usuarioAtual, passAtual, novoUser, senhaDefinitiva);

                Platform.runLater(() -> {
                    if (sucesso) {
                        lblStatusSeguranca.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
                        lblStatusSeguranca.setText("Credenciais atualizadas com sucesso!");
                        txtSenhaAtual.clear();
                        txtNovaSenha.clear();
                        txtConfirmaSenha.clear();
                    } else {
                        lblStatusSeguranca.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                        lblStatusSeguranca.setText("Palavra-passe atual incorreta.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblStatusSeguranca.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                    lblStatusSeguranca.setText("Erro ao salvar no banco: " + e.getMessage());
                });
            }
        }).start();
    }

    private SVGPath criarIconeSalvar() {
        SVGPath p = new SVGPath();
        p.setContent("M17 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V7l-4-4zm-5 16c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm3-10H5V5h10v4z");
        p.setFill(Color.WHITE);
        return p;
    }
}
