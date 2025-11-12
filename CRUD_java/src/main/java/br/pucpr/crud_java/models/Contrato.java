package br.pucpr.crud_java.models;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

@Entity
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dataInicio;
    private double valorMensal;
    private boolean contratoStatus;
    private ArrayList<Boleto> boletos;
    @ManyToOne
    @JoinColumn(name = "locatario_id")
    private Locatario locatario;

    public Contrato() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Locatario getLocatario() {
        return locatario;
    }

    public void setLocatario(Locatario locatario) {
        this.locatario = locatario;
    }

    public Locatario getlocatario() {
        return locatario;
    }

    public void setNomeLocatario(Locatario locatario) {
        this.locatario = locatario;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
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

    public void setContratoStatus(boolean contratoStatus) {
        contratoStatus = contratoStatus;
    }

    public boolean isAtivo() {
        return contratoStatus;
    }

    public ArrayList<Boleto> getBoletos() {
        return boletos;
    }

    public void setBoletos(ArrayList<Boleto> boletos) {
        this.boletos = boletos;
    }
}