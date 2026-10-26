package autoeletrica.model;

import java.time.LocalDate;

public class Servico {
    private Long id;
    private Long clienteId;
    private LocalDate dataServico;
    private String descricaoServico;
    private String placaCarro;
    private String modeloCarro;
    private boolean garantiaPropria;
    private int mesesGarantia;
    private double valorTotal;

    public Servico() {
    }

    public Servico(Long id, Long clienteId, LocalDate dataServico, String descricaoServico,
                   String placaCarro, String modeloCarro, boolean garantiaPropria,
                   int mesesGarantia, double valorTotal) {
        this.id = id;
        this.clienteId = clienteId;
        this.dataServico = dataServico;
        this.descricaoServico = descricaoServico;
        this.placaCarro = placaCarro;
        this.modeloCarro = modeloCarro;
        this.garantiaPropria = garantiaPropria;
        this.mesesGarantia = mesesGarantia;
        this.valorTotal = valorTotal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public LocalDate getDataServico() { return dataServico; }
    public void setDataServico(LocalDate dataServico) { this.dataServico = dataServico; }

    public String getDescricaoServico() { return descricaoServico; }
    public void setDescricaoServico(String descricaoServico) { this.descricaoServico = descricaoServico; }

    public String getPlacaCarro() { return placaCarro; }
    public void setPlacaCarro(String placaCarro) { this.placaCarro = placaCarro; }

    public String getModeloCarro() { return modeloCarro; }
    public void setModeloCarro(String modeloCarro) { this.modeloCarro = modeloCarro; }

    public boolean isGarantiaPropria() { return garantiaPropria; }
    public void setGarantiaPropria(boolean garantiaPropria) { this.garantiaPropria = garantiaPropria; }

    public int getMesesGarantia() { return mesesGarantia; }
    public void setMesesGarantia(int mesesGarantia) { this.mesesGarantia = mesesGarantia; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
}