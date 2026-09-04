package ucu.edu.almacen;

public class TerminalCarga {
    private boolean habilitada;
    private OperacionCarga operacionActual;
    private EstadoTerminal estado;
    private int id;

    public TerminalCarga(int id) {
        this.habilitada = true;
        this.operacionActual = OperacionCarga.LIBRE;
        this.estado = EstadoTerminal.LIBRE;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isHabilitada() {
        return habilitada;
    }

    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }

    public OperacionCarga getOperacionActual() {
        return operacionActual;
    }

    public void setOperacionActual(OperacionCarga operacionActual) {
        this.operacionActual = operacionActual;
    }

    public EstadoTerminal getEstado() {
        return estado;
    }

    public void setEstado(EstadoTerminal estado) {
        this.estado = estado;
    }

    /**
     * Una terminal está libre si está habilitada (no hay obras que la afecten)
     * y no tiene ninguna operación de carga o descarga en curso.
     */
    public boolean estaLibre() {
        return habilitada && operacionActual == OperacionCarga.LIBRE;
    }

    @Override
    public String toString() {
        return "TerminalCarga{id=" + id + ", habilitada=" + habilitada
                + ", operacionActual=" + operacionActual + "}";
    }
}
