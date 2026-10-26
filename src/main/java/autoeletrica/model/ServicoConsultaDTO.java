package autoeletrica.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ServicoConsultaDTO {
    private final Long id;
    private final Long clienteId;
    private final LocalDate dataServicoOriginal;
    private final String data;
    private final String clienteNome;
    private final String telefone;
    private final String veiculo;
    private final String modelo;
    private final String placa;
    private final String descricao;
    private final double valorNumerico;
    private final String valor;
    private final boolean garantiaPropria;
    private final int mesesGarantia;
    private final String statusGarantia;

    public ServicoConsultaDTO(Long id, Long clienteId, LocalDate dataServico, String clienteNome, String telefone,
                              String modelo, String placa, String descricao,
                              double valorTotal, boolean garantiaPropria, int mesesGarantia) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.id = id;
        this.clienteId = clienteId;
        this.dataServicoOriginal = dataServico;
        this.data = dataServico != null ? dataServico.format(dtf) : "-";
        this.clienteNome = clienteNome != null ? clienteNome : "Sem Cliente";
        this.telefone = telefone != null && !telefone.isBlank() ? telefone : "-";
        this.modelo = modelo != null ? modelo : "";
        this.placa = placa != null ? placa : "";
        this.veiculo = this.modelo + " (" + this.placa + ")";
        this.descricao = descricao != null ? descricao : "";
        this.valorNumerico = valorTotal;
        this.valor = String.format("R$ %.2f", valorTotal);
        this.garantiaPropria = garantiaPropria;
        this.mesesGarantia = mesesGarantia;

        if (!garantiaPropria) {
            this.statusGarantia = "Sem garantia";
        } else if (mesesGarantia <= 0) {
            this.statusGarantia = "Nossa";
        } else {
            this.statusGarantia = mesesGarantia == 12 ? "1 ano" : mesesGarantia + " meses";
        }
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public LocalDate getDataServicoOriginal() { return dataServicoOriginal; }
    public String getData() { return data; }
    public String getClienteNome() { return clienteNome; }
    public String getTelefone() { return telefone; }
    public String getVeiculo() { return veiculo; }
    public String getModelo() { return modelo; }
    public String getPlaca() { return placa; }
    public String getDescricao() { return descricao; }
    public double getValorNumerico() { return valorNumerico; }
    public String getValor() { return valor; }
    public boolean isGarantiaPropria() { return garantiaPropria; }
    public int getMesesGarantia() { return mesesGarantia; }
    public String getStatusGarantia() { return statusGarantia; }
}