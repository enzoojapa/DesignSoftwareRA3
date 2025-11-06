package br.pucpr.crud_java.models;

import java.io.Serial;
import java.io.Serializable;

public class Loja implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String lojaNome;
    private String lojaTelefone;
    private String lojaTipo;

    public Loja(String lojaNome, String lojaTelefone, String lojaTipo) {
        this.lojaNome = lojaNome;
        this.lojaTelefone = lojaTelefone;
        this.lojaTipo = lojaTipo;
    }

    @Override
    public String toString() {
        return "Nome: " + this.lojaNome +
                "\nTelefone: " + this.lojaTelefone +
                "\nTipo: " + this.lojaTipo + "\n";
    }



    public String getLojaNome() {
        return lojaNome;
    }

    public void setLojaNome(String lojaNome) {
        this.lojaNome = lojaNome;
    }

    public String getLojaTelefone() {
        return lojaTelefone;
    }

    public void setLojaTelefone(String lojaTelefone) {
        this.lojaTelefone = lojaTelefone;
    }


    public String getLojaTipo() {
        return lojaTipo;
    }

    public void setLojaTipo(String lojaTipo) {
        this.lojaTipo = lojaTipo;
    }
}