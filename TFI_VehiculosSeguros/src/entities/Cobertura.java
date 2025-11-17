/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

/**
 *
 * @author lucasgragera
 */
public enum Cobertura {
    
    RC("Responsabilidad Civil"),
    TERCEROS("Terceros Completos"),
    TODO_RIESGO("Todo Riesgo");

    private final String descripcion;

    Cobertura(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }


    @Override
    public String toString() {
        return "Cobertura{" +
                "descripcion='" + descripcion + '\'' +
                '}';
    }
}
