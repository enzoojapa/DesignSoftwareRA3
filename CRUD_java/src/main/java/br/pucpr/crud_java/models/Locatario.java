package br.pucpr.crud_java.models;

import java.io.Serial;
import java.io.Serializable;


public class Locatario implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String locatarioCnpj;
    private String locatarioNome;
    private String locatarioTelefone;
    private String locatarioEmail;

    public Locatario(String cnpj, String nome, String email, String telefone) {
        locatarioCnpj = cnpj;
        locatarioNome = nome;
        locatarioEmail = email;
        locatarioTelefone = telefone;
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
