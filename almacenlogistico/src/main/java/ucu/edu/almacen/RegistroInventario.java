package ucu.edu.almacen;

import ucu.edu.implementaciones.ListaArray;

/**
 * Entrada del inventario para un producto y todas sus ubicaciones físicas.
 */
public class RegistroInventario implements Comparable<RegistroInventario> {

    private Producto producto;
    private ListaArray<StockUbicado> ubicaciones;
    private int cantidadMinima;

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

    public int getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(int cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public StockUbicado getStockUbicado(Sector sector) {
        if (sector == null || sector.getCodigo() == null) {
            return null;
        }

        return ubicaciones.buscar(ubicacion -> ubicacion.getPosicion() != null
                && sector.getCodigo().equals(ubicacion.getPosicion().getCodigo()));
    }

    @Override
    public int compareTo(RegistroInventario otro) {
        // TODO: definir validaciones de integridad del registro.
        return producto.getCodigo().compareTo(otro.producto.getCodigo());
    }

    
}
