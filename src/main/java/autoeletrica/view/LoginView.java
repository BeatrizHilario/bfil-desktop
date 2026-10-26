package autoeletrica.view;

import autoeletrica.dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.InputStream;
import java.sql.SQLException;

public class LoginView extends VBox {

    private final TextField txtUsuario = new TextField();
    private final PasswordField txtSenhaOculta = new PasswordField();
    private final TextField txtSenhaVisivel = new TextField();
    private final Button btnVerSenha = new Button();
    private final Button btnEntrar = new Button("Acessar Sistema");
    private final Hyperlink linkEsqueciSenha = new Hyperlink("Esqueci minha senha");
    private final Label lblMensagem = new Label();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Runnable onLoginSucesso;
    private boolean mostrandoSenha = false;

    public LoginView(Runnable onLoginSucesso) {
        this.onLoginSucesso = onLoginSucesso;

        setAlignment(Pos.CENTER);
        setSpacing(14);
        setPadding(new Insets(28, 36, 28, 36));
        setStyle("-fx-background-color: #FFFFFF;");

        // Contentor escuro da logo oficial
        VBox logoContainer = new VBox();
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setPadding(new Insets(16, 24, 16, 24));
        logoContainer.setStyle("-fx-background-color: #192637; -fx-background-radius: 10px;");
        logoContainer.setMaxWidth(Double.MAX_VALUE);

        try (InputStream is = getClass().getResourceAsStream("/images/logo.png")) {
            if (is != null) {
                ImageView img = new ImageView(new Image(is));
                img.setFitWidth(185);
                img.setPreserveRatio(true);
                img.setSmooth(true);
                logoContainer.getChildren().add(img);
            } else {
                Label lblFallback = new Label("BFIL CLOUD");
                lblFallback.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
                logoContainer.getChildren().add(lblFallback);
            }
        } catch (Exception e) {
            Label lblFallback = new Label("BFIL CLOUD");
            lblFallback.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
            logoContainer.getChildren().add(lblFallback);
        }

        Label lblSubtitulo = new Label("Entre com as suas credenciais");
        lblSubtitulo.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 13px; -fx-text-fill: #64748B;");

        String estiloInput = "-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 10px 12px; -fx-font-size: 14px; -fx-text-fill: #1E293B;";

        txtUsuario.setPromptText("Usuário");
        txtUsuario.setStyle(estiloInput);
        txtUsuario.setMaxWidth(Double.MAX_VALUE);

        // Campo de palavra-passe com alternância de visualização
        txtSenhaOculta.setPromptText("Senha");
        txtSenhaOculta.setStyle(estiloInput + " -fx-padding: 10px 42px 10px 12px;");
        txtSenhaOculta.setMaxWidth(Double.MAX_VALUE);

        txtSenhaVisivel.setPromptText("Senha");
        txtSenhaVisivel.setStyle(estiloInput + " -fx-padding: 10px 42px 10px 12px;");
        txtSenhaVisivel.setMaxWidth(Double.MAX_VALUE);
        txtSenhaVisivel.setVisible(false);
        txtSenhaVisivel.setManaged(false);

        txtSenhaOculta.textProperty().bindBidirectional(txtSenhaVisivel.textProperty());

        btnVerSenha.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 8px;");
        btnVerSenha.setGraphic(criarIconeOlhoAberto());
        btnVerSenha.setOnAction(e -> alternarVisibilidadeSenha());

        StackPane senhaStack = new StackPane(txtSenhaOculta, txtSenhaVisivel, btnVerSenha);
        StackPane.setAlignment(btnVerSenha, Pos.CENTER_RIGHT);
        StackPane.setMargin(btnVerSenha, new Insets(0, 6, 0, 0));

        btnEntrar.getStyleClass().add("btn-registrar-servico");
        btnEntrar.setMaxWidth(Double.MAX_VALUE);
        btnEntrar.setStyle("-fx-pref-height: 44px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");

        // Link Esqueci minha senha
        linkEsqueciSenha.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 12px; -fx-text-fill: #0052E0; -fx-border-color: transparent;");
        linkEsqueciSenha.setOnAction(e -> abrirModalRecuperacaoSenha());

        lblMensagem.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 12px; -fx-text-fill: #DC2626;");
        lblMensagem.setWrapText(true);

        getChildren().addAll(logoContainer, lblSubtitulo, txtUsuario, senhaStack, btnEntrar, linkEsqueciSenha, lblMensagem);

        btnEntrar.setOnAction(e -> executarLogin());
        txtSenhaOculta.setOnAction(e -> executarLogin());
        txtSenhaVisivel.setOnAction(e -> executarLogin());
        txtUsuario.setOnAction(e -> {
            if (mostrandoSenha) {
                txtSenhaVisivel.requestFocus();
            } else {
                txtSenhaOculta.requestFocus();
            }
        });
    }

    private void alternarVisibilidadeSenha() {
        mostrandoSenha = !mostrandoSenha;
        if (mostrandoSenha) {
            txtSenhaVisivel.setVisible(true);
            txtSenhaVisivel.setManaged(true);
            txtSenhaOculta.setVisible(false);
            txtSenhaOculta.setManaged(false);
            btnVerSenha.setGraphic(criarIconeOlhoFechado());
            txtSenhaVisivel.requestFocus();
            txtSenhaVisivel.positionCaret(txtSenhaVisivel.getText().length());
        } else {
            txtSenhaOculta.setVisible(true);
            txtSenhaOculta.setManaged(true);
            txtSenhaVisivel.setVisible(false);
            txtSenhaVisivel.setManaged(false);
            btnVerSenha.setGraphic(criarIconeOlhoAberto());
            txtSenhaOculta.requestFocus();
            txtSenhaOculta.positionCaret(txtSenhaOculta.getText().length());
        }
    }

    private void executarLogin() {
        String user = txtUsuario.getText();
        String pass = txtSenhaOculta.getText();

        if (user.isBlank() || pass.isBlank()) {
            lblMensagem.setStyle("-fx-text-fill: #DC2626;");
            lblMensagem.setText("Preencha todos os campos.");
            return;
        }

        btnEntrar.setDisable(true);
        lblMensagem.setStyle("-fx-text-fill: #0052E0;");
        lblMensagem.setText("Autenticando...");

        new Thread(() -> {
            try {
                boolean sucesso = usuarioDAO.autenticar(user, pass);
                Platform.runLater(() -> {
                    btnEntrar.setDisable(false);
                    if (sucesso) {
                        lblMensagem.setText("");
                        onLoginSucesso.run();
                    } else {
                        lblMensagem.setStyle("-fx-text-fill: #DC2626;");
                        lblMensagem.setText("Usuário ou senha incorretos.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnEntrar.setDisable(false);
                    lblMensagem.setStyle("-fx-text-fill: #DC2626;");
                    lblMensagem.setText("Erro de conexão com o banco.");
                });
            }
        }).start();
    }

    private void abrirModalRecuperacaoSenha() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Recuperação de Senha");

        DialogPane dialogPane = dialog.getDialogPane();
        var css = getClass().getResource("/styles.css");
        if (css != null) {
            dialogPane.getStylesheets().add(css.toExternalForm());
        }
        dialogPane.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 8px;");

        VBox headerBox = new VBox(4);
        headerBox.setPadding(new Insets(16, 20, 8, 20));

        Label lblTituloHeader = new Label("Redefinir Palavra-passe");
        lblTituloHeader.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 17px; -fx-font-weight: 800; -fx-text-fill: #192637;");

        Label lblDescHeader = new Label("Introduza o utilizador e a nova palavra-passe pretendida.");
        lblDescHeader.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 12px; -fx-text-fill: #64748B;");

        headerBox.getChildren().addAll(lblTituloHeader, lblDescHeader);
        dialogPane.setHeader(headerBox);

        ButtonType btnSalvarType = new ButtonType("Redefinir Senha", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelarType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(btnSalvarType, btnCancelarType);

        Button btnConfirmar = (Button) dialogPane.lookupButton(btnSalvarType);
        if (btnConfirmar != null) {
            btnConfirmar.getStyleClass().add("btn-registrar-servico");
            btnConfirmar.setStyle("-fx-padding: 8px 16px; -fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand;");
        }

        Button btnCancelar = (Button) dialogPane.lookupButton(btnCancelarType);
        if (btnCancelar != null) {
            btnCancelar.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 6px; -fx-padding: 8px 16px; -fx-cursor: hand;");
        }

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.setPadding(new Insets(16, 20, 20, 20));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Nome de utilizador (ex: admin)");
        txtUser.getStyleClass().add("modal-input");

        TextField txtNovaSenha = new TextField();
        txtNovaSenha.setPromptText("Nova palavra-passe");
        txtNovaSenha.getStyleClass().add("modal-input");

        TextField txtConfirma = new TextField();
        txtConfirma.setPromptText("Confirmar nova palavra-passe");
        txtConfirma.getStyleClass().add("modal-input");

        Label lblUser = new Label("Utilizador:");
        lblUser.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #192637;");

        Label lblNova = new Label("Nova Senha:");
        lblNova.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #192637;");

        Label lblConf = new Label("Confirmar:");
        lblConf.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #192637;");

        grid.add(lblUser, 0, 0);
        grid.add(txtUser, 1, 0);
        grid.add(lblNova, 0, 1);
        grid.add(txtNovaSenha, 1, 1);
        grid.add(lblConf, 0, 2);
        grid.add(txtConfirma, 1, 2);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(100);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(240);
        grid.getColumnConstraints().addAll(col1, col2);

        dialogPane.setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvarType) {
                String u = txtUser.getText().trim();
                String p1 = txtNovaSenha.getText().trim();
                String p2 = txtConfirma.getText().trim();

                if (u.isBlank() || p1.isBlank()) {
                    mostrarAlerta("Aviso", "Preencha todos os campos.");
                    return false;
                }
                if (!p1.equals(p2)) {
                    mostrarAlerta("Erro", "As palavras-passe não coincidem.");
                    return false;
                }

                try {
                    boolean alterou = usuarioDAO.redefinirSenha(u, p1);
                    if (alterou) {
                        mostrarAlerta("Sucesso", "Palavra-passe redefinida com sucesso! Já pode aceder.");
                        return true;
                    } else {
                        mostrarAlerta("Erro", "Utilizador não encontrado.");
                        return false;
                    }
                } catch (Exception e) {
                    mostrarAlerta("Erro", "Falha de ligação: " + e.getMessage());
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);

        DialogPane pane = alert.getDialogPane();
        var css = getClass().getResource("/styles.css");
        if (css != null) {
            pane.getStylesheets().add(css.toExternalForm());
        }
        pane.setStyle("-fx-background-color: #FFFFFF;");

        Button btnOk = (Button) pane.lookupButton(ButtonType.OK);
        if (btnOk != null) {
            btnOk.getStyleClass().add("btn-registrar-servico");
            btnOk.setStyle("-fx-padding: 6px 18px; -fx-font-weight: bold; -fx-cursor: hand;");
        }

        alert.showAndWait();
    }

    private SVGPath criarIconeOlhoAberto() {
        SVGPath path = new SVGPath();
        path.setContent("M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z");
        path.setFill(Color.web("#64748B"));
        return path;
    }

    private SVGPath criarIconeOlhoFechado() {
        SVGPath path = new SVGPath();
        path.setContent("M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.44-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z");
        path.setFill(Color.web("#0052E0"));
        return path;
    }
}