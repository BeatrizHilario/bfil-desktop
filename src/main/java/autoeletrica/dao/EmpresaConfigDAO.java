package autoeletrica.dao;

import autoeletrica.model.EmpresaConfig;
import autoeletrica.util.ApiClient;
import com.google.gson.Gson;

import java.net.http.HttpResponse;
import java.sql.SQLException;

public class EmpresaConfigDAO {

    private final Gson gson = new Gson();

    public EmpresaConfig carregar() throws SQLException {
        try {
            HttpResponse<String> response = ApiClient.get("/api/empresa");
            if (response.statusCode() == 200) {
                EmpresaConfig cfg = gson.fromJson(response.body(), EmpresaConfig.class);
                return cfg != null ? cfg : new EmpresaConfig();
            }
            throw new SQLException("Erro ao carregar dados da empresa (HTTP " + response.statusCode() + ")");
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API ao carregar empresa: " + e.getMessage(), e);
        }
    }

    public void salvar(EmpresaConfig cfg) throws SQLException {
        try {
            String json = gson.toJson(cfg);
            HttpResponse<String> response = ApiClient.post("/api/empresa", json);
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new SQLException("Erro ao salvar dados da empresa (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            throw new SQLException("Erro na comunicação com a API ao salvar empresa: " + e.getMessage(), e);
        }
    }
}