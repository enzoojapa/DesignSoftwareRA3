package br.pucpr.crud_java.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Locatario implements Serializable {
    @Serial
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // Garante que o CNPJ é único no banco de dados (Regra de Negócio)
    @Column(unique = true)
    public String locatarioCnpj;

    public String locatarioNome;
    public String locatarioTelefone;
    public String locatarioEmail;

    public Locatario() {
    }


    public Long getId() {
        return id;
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