package autoeletrica.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;

public class AtualizadorApp {

    public static final String VERSAO_LOCAL = "1.0.2";

    public static void checarAtualizacaoEmSegundoPlano(Stage parentStage) {
        new Thread(() -> {
            try {
                HttpResponse<String> response = ApiClient.get("/api/sistema/versao");
                if (response.statusCode() == 200) {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    String versaoRemota = json.has("versaoSistema") && !json.get("versaoSistema").isJsonNull()
                            ? json.get("versaoSistema").getAsString() : null;
                    String urlDownload = json.has("urlInstalador") && !json.get("urlInstalador").isJsonNull()
                            ? json.get("urlInstalador").getAsString() : null;

                    if (versaoRemota != null && ehVersaoSuperior(versaoRemota, VERSAO_LOCAL)
                            && urlDownload != null && !urlDownload.isBlank()) {
                        Platform.runLater(() -> exibirDialogoAtualizacao(parentStage, versaoRemota, urlDownload));
                    }
                }
            } catch (Exception e) {
                System.err.println("Não foi possível verificar atualizações: " + e.getMessage());
            }
        }).start();
    }

    private static boolean ehVersaoSuperior(String remota, String local) {
        return !remota.trim().equalsIgnoreCase(local.trim());
    }

    private static void exibirDialogoAtualizacao(Stage parentStage, String novaVersao, String urlDownload) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(parentStage);
        alert.setTitle("Atualização Disponível");
        alert.setHeaderText("Nova versão " + novaVersao + " disponível!");
        alert.setContentText("Uma nova versão do sistema está pronta. Deseja atualizar agora de forma automática?");

        ButtonType btnAtualizar = new ButtonType("Atualizar Agora", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnDepois = new ButtonType("Lembrar Depois", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnAtualizar, btnDepois);

        alert.showAndWait().ifPresent(tipo -> {
            if (tipo == btnAtualizar) {
                iniciarDownloadEInstalacao(parentStage, urlDownload);
            }
        });
    }

    private static void iniciarDownloadEInstalacao(Stage parentStage, String urlDownload) {
        Stage progressStage = new Stage();
        progressStage.initModality(Modality.APPLICATION_MODAL);
        progressStage.initOwner(parentStage);
        progressStage.setTitle("Atualizando BFIL Cloud");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(320);

        Label lblStatus = new Label("Conectando ao servidor...");
        lblStatus.setStyle("-fx-font-family: 'Inter'; -fx-font-size: 13px; -fx-text-fill: #192637;");

        VBox layout = new VBox(14, lblStatus, progressBar);
        layout.setPadding(new Insets(24));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FFFFFF;");

        progressStage.setScene(new Scene(layout, 380, 130));
        progressStage.setResizable(false);
        progressStage.show();

        Task<File> downloadTask = new Task<>() {
            @Override
            protected File call() throws Exception {
                File tempExe = new File(System.getProperty("java.io.tmpdir"), "BFIL_Cloud_Update.exe");

                URL url = URI.create(urlDownload).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setInstanceFollowRedirects(true);
                conn.connect();

                int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == 307 || status == 308) {
                    String novaUrl = conn.getHeaderField("Location");
                    conn = (HttpURLConnection) URI.create(novaUrl).toURL().openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.connect();
                }

                int fileSize = conn.getContentLength();

                try (InputStream in = new BufferedInputStream(conn.getInputStream());
                     FileOutputStream out = new FileOutputStream(tempExe)) {

                    byte[] buffer = new byte[8192];
                    int bytesLidos;
                    long totalBytesLidos = 0;

                    while ((bytesLidos = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesLidos);
                        totalBytesLidos += bytesLidos;

                        if (fileSize > 0) {
                            updateProgress(totalBytesLidos, fileSize);
                            long percent = (totalBytesLidos * 100) / fileSize;
                            updateMessage("Baixando atualização: " + percent + "%");
                        } else {
                            updateMessage("Baixando: " + (totalBytesLidos / (1024 * 1024)) + " MB");
                        }
                    }
                }
                return tempExe;
            }
        };

        lblStatus.textProperty().bind(downloadTask.messageProperty());
        progressBar.progressProperty().bind(downloadTask.progressProperty());

        downloadTask.setOnSucceeded(e -> {
            File instaladorBaixado = downloadTask.getValue();
            progressStage.close();

            try {
                new ProcessBuilder(instaladorBaixado.getAbsolutePath()).start();
                Platform.exit();
                System.exit(0);
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Erro ao iniciar o instalador: " + ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });

        downloadTask.setOnFailed(e -> {
            progressStage.close();
            Throwable erro = downloadTask.getException();
            new Alert(Alert.AlertType.ERROR, "Falha ao baixar atualização: " + erro.getMessage(), ButtonType.OK).showAndWait();
        });

        new Thread(downloadTask).start();
    }
}