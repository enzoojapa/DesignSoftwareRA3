package br.pucpr.crud_java.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Locatario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String locatarioCnpj;
    private String locatarioNome;
    private String locatarioTelefone;
    private String locatarioEmail;

    public Locatario() {
    }

    public String getLocatarioCnpj() {
        return locatarioCnpj;
    }

    public void setLocatarioCnpj(String locatarioCnpj) {
        this.locatarioCnpj = locatarioCnpj;
    }

    public String getLocatarioNome() {
        return locatarioNome;
    }

    public void setLocatarioNome(String locatarioNome) {
        this.locatarioNome = locatarioNome;
    }

    public String getLocatarioTelefone() {
        return locatarioTelefone;
    }

    public void setLocatarioTelefone(String locatarioTelefone) {
        this.locatarioTelefone = locatarioTelefone;
    }

    public String getLocatarioEmail() {
        return locatarioEmail;
    }

    public void setLocatarioEmail(String locatarioEmail) {
        this.locatarioEmail = locatarioEmail;
    }
}
