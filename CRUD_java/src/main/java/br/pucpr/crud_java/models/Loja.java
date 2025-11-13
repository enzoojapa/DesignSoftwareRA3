package br.pucpr.crud_java.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Loja implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // OTIMIZAÇÃO: Garante que o nome da loja é único no banco de dados.
    @Column(unique = true)
    public String lojaNome;
    public String lojaTelefone;
    public String lojaTipo;

    public Loja() {
    }



    public Long getId() {
        return id;
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