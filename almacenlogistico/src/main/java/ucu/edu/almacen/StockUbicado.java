package ucu.edu.almacen;

/** Representa la cantidad disponible en una posición de un registro de inventario. */
public class StockUbicado {

    private Sector posicion;
    private int cantidad;

    public StockUbicado() {
        // Constructor requerido para el armado incremental del dominio.
    }

    public StockUbicado(Sector posicion, int cantidad) {
        this.posicion = posicion;
        this.cantidad = cantidad;
    }

    public Sector getPosicion() {
        return posicion;
    }

    public void setPosicion(Sector posicion) {
        this.posicion = posicion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
