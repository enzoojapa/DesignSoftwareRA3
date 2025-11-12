package br.pucpr.crud_java.models;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Boleto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numeroDocumento;
    private double valor;
    private LocalDate vencimento;
    private String cedente;
    private String banco;
    private String linhaDigitavel;
    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    public Boleto() {
        this.valor = valor;
        this.vencimento = vencimento;
        this.cedente = cedente;
        this.banco = banco;
        this.linhaDigitavel = linhaDigitavel;
        this.contrato = contrato;
    }

    @Override
    public String toString() {
        return "Número do Documento: " + this.numeroDocumento +
                "\nValor: R$" + this.valor +
                "\nVencimento: " + this.vencimento +
                "\nCedente: " + this.cedente +
                "\nBanco: " + this.banco +
                "\nLinha digitável: " + this.linhaDigitavel +
                "\n";
    }

    public int getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(int numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public String getCedente() {
        return cedente;
    }

    public void setCedente(String cedente) {
        this.cedente = cedente;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public void setLinhaDigitavel(String linhaDigitavel) {
        this.linhaDigitavel = linhaDigitavel;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }
}