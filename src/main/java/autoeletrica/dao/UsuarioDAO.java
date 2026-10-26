package autoeletrica.dao;

import autoeletrica.util.ApiClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class UsuarioDAO {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build();

    /**
     * Valida login via endpoint /api/auth/login.
     */
    public boolean autenticar(String usuario, String senha) {
        if (usuario == null || senha == null || usuario.isBlank() || senha.isBlank()) {
            return false;
        }

        try {
            String jsonPayload = String.format("{\"usuario\":\"%s\",\"senha\":\"%s\"}",
                    escaparJson(usuario.trim()),
                    escaparJson(senha.trim()));

            HttpResponse<String> response = ApiClient.post("/api/auth/login", jsonPayload);
            return response.statusCode() == 200 && response.body().contains("\"sucesso\":true");
        } catch (Exception e) {
            System.err.println("Erro na autenticação: " + e.getMessage());
            return false;
        }
    }

    /**
     * Redefine a senha via endpoint /api/auth/redefinir-senha (usado no LoginView).
     */
    public boolean redefinirSenha(String usuario, String novaSenha) {
        if (usuario == null || novaSenha == null || usuario.isBlank() || novaSenha.isBlank()) {
            return false;
        }

        try {
            String jsonPayload = String.format("{\"usuario\":\"%s\",\"novaSenha\":\"%s\"}",
                    escaparJson(usuario.trim()),
                    escaparJson(novaSenha.trim()));

            HttpResponse<String> response = ApiClient.post("/api/auth/redefinir-senha", jsonPayload);
            return response.statusCode() == 200 && response.body().contains("\"sucesso\":true");
        } catch (Exception e) {
            System.err.println("Erro ao redefinir palavra-passe: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtém as informações do utilizador principal (usado nas linhas 219 e 276 do ConfiguracoesView).
     * Devolve um array de Strings com { usuario, senha } para preencher os campos da tela.
     */
    public String buscarUsuarioPrincipal() {
        try {
            HttpResponse<String> response = ApiClient.get("/api/auth/principal");
            if (response.statusCode() == 200) {
                return extrairValorJson(response.body(), "usuario");
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar utilizador: " + e.getMessage());
        }
        return "admin";
    }

    /**
     * Atualiza as credenciais de acesso (usado na linha 277 do ConfiguracoesView).
     */
    public boolean atualizarCredenciais(String usuarioAtual, String senhaAtual, String novoUsuario, String novaSenha) {
        if (novoUsuario == null || novaSenha == null || novoUsuario.isBlank() || novaSenha.isBlank()) {
            return false;
        }

        try {
            String jsonPayload = String.format("{\"usuario\":\"%s\",\"senha\":\"%s\"}",
                    escaparJson(novoUsuario.trim()),
                    escaparJson(novaSenha.trim()));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiClient.BASE_URL + "/api/auth/credenciais"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && response.body().contains("\"sucesso\":true");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar credenciais: " + e.getMessage());
            return false;
        }
    }

    private String extrairValorJson(String json, String chave) {
        String padrao = "\"" + chave + "\":\"";
        int inicio = json.indexOf(padrao);
        if (inicio == -1) return "";
        inicio += padrao.length();
        int fim = json.indexOf("\"", inicio);
        return (fim != -1) ? json.substring(inicio, fim) : "";
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}