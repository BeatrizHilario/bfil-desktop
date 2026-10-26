package autoeletrica.dao;

import autoeletrica.model.Cliente;
import autoeletrica.util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private final Gson gson = new Gson();

    public List<Cliente> listarTodos() throws SQLException {
        try {
            HttpResponse<String> response = ApiClient.get("/api/clientes");
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<ArrayList<Cliente>>(){}.getType();
                return gson.fromJson(response.body(), listType);
            }
            throw new SQLException("Erro ao consultar clientes (HTTP " + response.statusCode() + ")");
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API de clientes: " + e.getMessage(), e);
        }
    }

    public void salvar(Cliente c) throws SQLException {
        try {
            String json = gson.toJson(c);
            HttpResponse<String> response = ApiClient.post("/api/clientes", json);
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new SQLException("Erro ao salvar cliente (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API ao salvar: " + e.getMessage(), e);
        }
    }

    public void atualizar(Cliente c) throws SQLException {
        try {
            String json = gson.toJson(c);
            HttpResponse<String> response = ApiClient.put("/api/clientes/" + c.getId(), json);
            if (response.statusCode() != 200) {
                throw new SQLException("Erro ao atualizar cliente (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API ao atualizar: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) throws SQLException {
        try {
            HttpResponse<String> response = ApiClient.delete("/api/clientes/" + id);
            if (response.statusCode() != 200) {
                throw new SQLException("Erro ao excluir cliente (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API ao excluir: " + e.getMessage(), e);
        }
    }
}