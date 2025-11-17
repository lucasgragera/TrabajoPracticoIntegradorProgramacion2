package entities;

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
