package autoeletrica.dao;

import autoeletrica.model.ServicoConsultaDTO;
import autoeletrica.util.ApiClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    public List<ServicoConsultaDTO> listarTodos() throws SQLException {
        try {
            HttpResponse<String> response = ApiClient.get("/api/servicos");
            if (response.statusCode() == 200) {
                return parsearListaServicos(response.body());
            }
            throw new SQLException("Erro ao consultar serviços (HTTP " + response.statusCode() + ")");
        } catch (Exception e) {
            throw new SQLException("Erro de comunicação com a API de serviços: " + e.getMessage(), e);
        }
    }

    public List<ServicoConsultaDTO> buscarPorTermo(String termo) throws SQLException {
        try {
            String termoEncoded = URLEncoder.encode(termo != null ? termo.trim() : "", StandardCharsets.UTF_8);
            HttpResponse<String> response = ApiClient.get("/api/servicos/busca?termo=" + termoEncoded);
            if (response.statusCode() == 200) {
                return parsearListaServicos(response.body());
            }
            throw new SQLException("Erro ao buscar serviços por termo (HTTP " + response.statusCode() + ")");
        } catch (Exception e) {
            throw new SQLException("Erro de comunicação com a API de busca de serviços: " + e.getMessage(), e);
        }
    }

    public void salvarServico(Long clienteId, LocalDate data, String modelo, String placa,
                              String descricao, double valor, boolean garantia, int mesesGarantia) throws SQLException {
        try {
            JsonObject json = new JsonObject();
            if (clienteId != null) json.addProperty("clienteId", clienteId);
            if (data != null) json.addProperty("dataServico", data.toString());
            json.addProperty("modeloCarro", modelo != null ? modelo.toUpperCase().trim() : "");
            json.addProperty("placaCarro", placa != null ? placa.toUpperCase().trim() : "");
            json.addProperty("descricaoServico", descricao != null ? descricao.trim() : "");
            json.addProperty("valorTotal", valor);
            json.addProperty("garantiaPropria", garantia);
            json.addProperty("mesesGarantia", mesesGarantia);

            HttpResponse<String> response = ApiClient.post("/api/servicos", json.toString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new SQLException("Erro ao salvar serviço na API (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro de comunicação com a API ao salvar serviço: " + e.getMessage(), e);
        }
    }

    public void atualizarServico(Long id, Long clienteId, LocalDate data, String modelo, String placa,
                                 String descricao, double valor, boolean garantia, int mesesGarantia) throws SQLException {
        try {
            JsonObject json = new JsonObject();
            if (clienteId != null) json.addProperty("clienteId", clienteId);
            if (data != null) json.addProperty("dataServico", data.toString());
            json.addProperty("modeloCarro", modelo != null ? modelo.toUpperCase().trim() : "");
            json.addProperty("placaCarro", placa != null ? placa.toUpperCase().trim() : "");
            json.addProperty("descricaoServico", descricao != null ? descricao.trim() : "");
            json.addProperty("valorTotal", valor);
            json.addProperty("garantiaPropria", garantia);
            json.addProperty("mesesGarantia", mesesGarantia);

            HttpResponse<String> response = ApiClient.put("/api/servicos/" + id, json.toString());
            if (response.statusCode() != 200) {
                throw new SQLException("Erro ao atualizar serviço na API (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro de comunicação com a API ao atualizar serviço: " + e.getMessage(), e);
        }
    }

    public void excluirServico(Long id) throws SQLException {
        try {
            HttpResponse<String> response = ApiClient.delete("/api/servicos/" + id);
            if (response.statusCode() != 200) {
                throw new SQLException("Erro ao excluir serviço na API (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro de comunicação com a API ao excluir serviço: " + e.getMessage(), e);
        }
    }

    private List<ServicoConsultaDTO> parsearListaServicos(String jsonString) {
        List<ServicoConsultaDTO> lista = new ArrayList<>();
        JsonArray array = JsonParser.parseString(jsonString).getAsJsonArray();

        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();

            Long id = obj.has("id") && !obj.get("id").isJsonNull() ? obj.get("id").getAsLong() : null;
            Long clienteId = obj.has("clienteId") && !obj.get("clienteId").isJsonNull() ? obj.get("clienteId").getAsLong() : null;

            LocalDate data = null;
            if (obj.has("dataServico") && !obj.get("dataServico").isJsonNull()) {
                try {
                    data = LocalDate.parse(obj.get("dataServico").getAsString());
                } catch (Exception ignored) {}
            }
            if (data == null) data = LocalDate.now();

            String clienteNome = obj.has("clienteNome") && !obj.get("clienteNome").isJsonNull() ? obj.get("clienteNome").getAsString() : "Sem Cliente";
            String telefone = obj.has("telefone") && !obj.get("telefone").isJsonNull() ? obj.get("telefone").getAsString() : "-";
            String modelo = obj.has("modeloCarro") && !obj.get("modeloCarro").isJsonNull() ? obj.get("modeloCarro").getAsString() : "";
            String placa = obj.has("placaCarro") && !obj.get("placaCarro").isJsonNull() ? obj.get("placaCarro").getAsString() : "";
            String descricao = obj.has("descricaoServico") && !obj.get("descricaoServico").isJsonNull() ? obj.get("descricaoServico").getAsString() : "";
            double valor = obj.has("valorTotal") && !obj.get("valorTotal").isJsonNull() ? obj.get("valorTotal").getAsDouble() : 0.0;
            boolean garantia = obj.has("garantiaPropria") && !obj.get("garantiaPropria").isJsonNull() && obj.get("garantiaPropria").getAsBoolean();
            int meses = obj.has("mesesGarantia") && !obj.get("mesesGarantia").isJsonNull() ? obj.get("mesesGarantia").getAsInt() : 0;

            lista.add(new ServicoConsultaDTO(id, clienteId, data, clienteNome, telefone, modelo, placa, descricao, valor, garantia, meses));
        }

        return lista;
    }
}