package ucu.edu.almacen;

/** Relaciona un producto con una posición física y la cantidad disponible. */
public class StockUbicado {

    private Producto producto;
    private Sector posicion;
    private int cantidad;

    public StockUbicado() {
        // Constructor requerido para el armado incremental del dominio.
    }

    public StockUbicado(Producto producto, Sector posicion, int cantidad) {
        this.producto = producto;
        this.posicion = posicion;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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
