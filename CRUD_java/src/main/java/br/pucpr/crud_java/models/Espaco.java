package br.pucpr.crud_java.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Espaco implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public int piso;
    public double area;
    public boolean espacoStatus;

    public Espaco() {
    }

    @Override
    public String toString() { return "Piso: " + this.piso + " Area: " + this.area; }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public int getPiso() {return piso;}

    public void setPiso(int piso) {this.piso = piso;}

    public double getArea() {return area;}

    public void setArea(double area) {this.area = area;}

    public boolean isStatus() {return espacoStatus;}

    public void setStatus(boolean status) {this.espacoStatus = status;}
}
