package autoeletrica;

import autoeletrica.util.AtualizadorApp;
import autoeletrica.view.ClientesView;
import autoeletrica.view.ConfiguracoesView;
import autoeletrica.view.FornecedoresView;
import autoeletrica.view.LoginView;
import autoeletrica.view.ServicosView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.InputStream;

public class MainApp extends Application {

    private BorderPane root;
    private Button btnClientes;
    private Button btnServicos;
    private Button btnFornecedores;
    private Button btnConfiguracoes;

    private ClientesView clientesView;
    private ServicosView servicosView;
    private FornecedoresView fornecedoresView;
    private ConfiguracoesView configuracoesView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BFIL CLOUD");

        try (InputStream is = getClass().getResourceAsStream("/images/app-icon.png")) {
            if (is != null) {
                primaryStage.getIcons().add(new Image(is));
            }
        } catch (Exception e) {
            System.err.println("Ícone da janela não encontrado: " + e.getMessage());
        }

        LoginView loginView = new LoginView(() -> inicializarPainelPrincipal(primaryStage));

        // Janela de login compacta e centralizada
        Scene sceneLogin = new Scene(loginView, 420, 540);
        var css = getClass().getResource("/styles.css");
        if (css != null) {
            sceneLogin.getStylesheets().add(css.toExternalForm());
        }

        // Trava o redimensionamento e desativa o botão de maximizar na tela de login
        primaryStage.setResizable(false);
        primaryStage.setScene(sceneLogin);
        primaryStage.centerOnScreen();
        primaryStage.show();

        // Linha comentada durante o desenvolvimento local
            AtualizadorApp.checarAtualizacaoEmSegundoPlano(primaryStage);
    }

    private void inicializarPainelPrincipal(Stage primaryStage) {
        root = new BorderPane();

        servicosView = new ServicosView(primaryStage);
        clientesView = new ClientesView(primaryStage);
        fornecedoresView = new FornecedoresView(primaryStage);
        configuracoesView = new ConfiguracoesView();

        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidemenu");

        VBox logoBox = criarContainerLogo();
        sidebar.getChildren().add(logoBox);

        VBox menuItems = new VBox(12);
        btnClientes = criarBotaoMenu("Clientes", criarIconeUsuario());
        btnServicos = criarBotaoMenu("Serviços", criarIconeManutencao());
        btnFornecedores = criarBotaoMenu("Fornecedores", criarIconeCaminhao());
        btnConfiguracoes = criarBotaoMenu("Configurações", criarIconeEngrenagem());

        btnServicos.getStyleClass().add("menu-btn-active");
        menuItems.getChildren().addAll(btnClientes, btnServicos, btnFornecedores, btnConfiguracoes);
        sidebar.getChildren().add(menuItems);

        root.setLeft(sidebar);
        root.setCenter(servicosView);

        btnServicos.setOnAction(e -> {
            ativarMenu(btnServicos, btnClientes, btnFornecedores, btnConfiguracoes);
            servicosView.carregarDados();
            root.setCenter(servicosView);
        });

        btnClientes.setOnAction(e -> {
            ativarMenu(btnClientes, btnServicos, btnFornecedores, btnConfiguracoes);
            clientesView.carregarDados();
            root.setCenter(clientesView);
        });

        btnFornecedores.setOnAction(e -> {
            ativarMenu(btnFornecedores, btnServicos, btnClientes, btnConfiguracoes);
            fornecedoresView.carregarDados();
            root.setCenter(fornecedoresView);
        });

        btnConfiguracoes.setOnAction(e -> {
            ativarMenu(btnConfiguracoes, btnServicos, btnClientes, btnFornecedores);
            configuracoesView.carregarDados();
            root.setCenter(configuracoesView);
        });

        Scene scenePrincipal = new Scene(root, 1280, 768);
        var css = getClass().getResource("/styles.css");
        if (css != null) {
            scenePrincipal.getStylesheets().add(css.toExternalForm());
        }

        // Reativa o redimensionamento da janela para o sistema principal
        primaryStage.setResizable(true);
        primaryStage.setScene(scenePrincipal);

        // Abre o sistema ocupando a tela inteira
        primaryStage.setMaximized(true);
    }

    private VBox criarContainerLogo() {
        VBox logoBox = new VBox();
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(24, 16, 28, 16));

        try (InputStream is = getClass().getResourceAsStream("/images/logo.png")) {
            if (is != null) {
                ImageView logoView = new ImageView(new Image(is));
                logoView.setFitWidth(180);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
                logoBox.getChildren().add(logoView);
                return logoBox;
            }
        } catch (Exception ignored) {}

        Label lblBfil = new Label("BFIL");
        lblBfil.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #FFFFFF;");
        Label lblCloud = new Label("CLOUD");
        lblCloud.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0052E0;");
        logoBox.getChildren().addAll(lblBfil, lblCloud);
        return logoBox;
    }

    private Button criarBotaoMenu(String texto, SVGPath icone) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("menu-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        if (icone != null) {
            btn.setGraphic(icone);
        }
        return btn;
    }

    private void ativarMenu(Button ativo, Button... inativos) {
        if (!ativo.getStyleClass().contains("menu-btn-active")) {
            ativo.getStyleClass().add("menu-btn-active");
        }
        for (Button b : inativos) {
            b.getStyleClass().remove("menu-btn-active");
        }
    }

    private SVGPath criarIconeUsuario() {
        SVGPath path = new SVGPath();
        path.setContent("M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z");
        path.setFill(Color.web("#0052E0"));
        return path;
    }

    private SVGPath criarIconeManutencao() {
        SVGPath path = new SVGPath();
        path.setContent("M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z");
        path.setFill(Color.web("#0052E0"));
        return path;
    }

    private SVGPath criarIconeCaminhao() {
        SVGPath path = new SVGPath();
        path.setContent("M20 8h-3V4H1v13h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4zM6 18.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm13.5-9l1.96 2.5H17V9.5h2.5zm-2 9c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z");
        path.setFill(Color.web("#0052E0"));
        return path;
    }

    private SVGPath criarIconeEngrenagem() {
        SVGPath path = new SVGPath();
        path.setContent("M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z");
        path.setFill(Color.web("#0052E0"));
        return path;
    }

    public static void main(String[] args) {
        launch(args);
    }
}