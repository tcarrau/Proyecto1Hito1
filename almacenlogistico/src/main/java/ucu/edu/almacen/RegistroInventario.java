package ucu.edu.almacen;

import ucu.edu.implementaciones.ListaArray;

/**
 * Entrada del inventario para un producto y todas sus ubicaciones físicas.
 */
public class RegistroInventario implements Comparable<RegistroInventario> {

    private Producto producto;
    private ListaArray<StockUbicado> ubicaciones;

    public RegistroInventario() {
        this.ubicaciones = new ListaArray<>();
    }

    public RegistroInventario(Producto producto) {
        this();
        this.producto = producto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ListaArray<StockUbicado> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(ListaArray<StockUbicado> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }

    @Override
    public int compareTo(RegistroInventario otro) {
        // TODO: definir validaciones de integridad del registro.
        return producto.getCodigo().compareTo(otro.producto.getCodigo());
    }
}
