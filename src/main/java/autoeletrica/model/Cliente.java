package autoeletrica.model;

public class Cliente {
    private Long id;
    private String tipo; // "FISICA" ou "JURIDICA"
    private String nome;
    private String nomeFantasia;
    private String cpfCnpj;
    private String inscricaoEstadual;
    private String telefone;
    private String email;
    private String endereco;

    public Cliente() {}

    public Cliente(Long id, String tipo, String nome, String nomeFantasia, String cpfCnpj,
                   String inscricaoEstadual, String telefone, String email, String endereco) {
        this.id = id;
        this.tipo = tipo != null ? tipo : "FISICA";
        this.nome = nome;
        this.nomeFantasia = nomeFantasia;
        this.cpfCnpj = cpfCnpj;
        this.inscricaoEstadual = inscricaoEstadual;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }

    public String getInscricaoEstadual() { return inscricaoEstadual; }
    public void setInscricaoEstadual(String inscricaoEstadual) { this.inscricaoEstadual = inscricaoEstadual; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    @Override
    public String toString() {
        return nome != null ? nome : "";
    }
}