package br.pucpr.crud_java.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Loja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lojaNome;
    private String lojaTelefone;
    private String lojaTipo;

    public Loja() {
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