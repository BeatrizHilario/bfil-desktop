package autoeletrica.dao;

import autoeletrica.model.Fornecedor;
import autoeletrica.util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {

    private final Gson gson = new Gson();

    public List<Fornecedor> listarTodos() throws SQLException {
        try {
            HttpResponse<String> response = ApiClient.get("/api/fornecedores");
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<ArrayList<Fornecedor>>(){}.getType();
                return gson.fromJson(response.body(), listType);
            }
            throw new SQLException("Erro ao consultar fornecedores (HTTP " + response.statusCode() + ")");
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API de fornecedores: " + e.getMessage(), e);
        }
    }

    public void salvar(Fornecedor f) throws SQLException {
        try {
            String json = gson.toJson(f);
            HttpResponse<String> response = ApiClient.post("/api/fornecedores", json);
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new SQLException("Erro ao salvar fornecedor (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API ao salvar: " + e.getMessage(), e);
        }
    }

    public void atualizar(Fornecedor f) throws SQLException {
        try {
            String json = gson.toJson(f);
            HttpResponse<String> response = ApiClient.put("/api/fornecedores/" + f.getId(), json);
            if (response.statusCode() != 200) {
                throw new SQLException("Erro ao atualizar fornecedor (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API ao atualizar: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) throws SQLException {
        try {
            HttpResponse<String> response = ApiClient.delete("/api/fornecedores/" + id);
            if (response.statusCode() != 200) {
                throw new SQLException("Erro ao excluir fornecedor (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API ao excluir: " + e.getMessage(), e);
        }
    }
}