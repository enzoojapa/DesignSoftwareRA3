package br.pucpr.crud_java.models;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Contrato implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public int contratoId;
    public LocalDate dataInicio;
    public double valorMensal;
    public boolean contratoStatus;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Boleto> boletos = new ArrayList<>();
    private boolean contratoAtivo;

    @ManyToOne
    @JoinColumn(name = "locatario_id")
    private Locatario locatario;

    public Contrato() {}

    // Getters e setters
    public void setContratoStatus(boolean contratoStatus) {
        this.contratoStatus = contratoStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getContratoId() {
        return contratoId;
    }

    public void setContratoId(int contratoId) {
        this.contratoId = contratoId;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public boolean isContratoAtivo() {
        return contratoAtivo;
    }

    public void setContratoAtivo(boolean contratoAtivo) {
        this.contratoAtivo = contratoAtivo;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(double valorMensal) {
        this.valorMensal = valorMensal;
    }

    public boolean isContratoStatus() {
        return contratoStatus;
    }

    public List<Boleto> getBoletos() {
        return boletos;
    }

    public void setBoletos(List<Boleto> boletos) {
        this.boletos = boletos;
    }

    public Locatario getLocatario() {
        return locatario;
    }

    public void setLocatario(Locatario locatario) {
        this.locatario = locatario;
    }

    public boolean isAtivo() {
        return contratoAtivo;
    }

}
