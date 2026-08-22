package ucu.edu.almacen;

import ucu.edu.implementaciones.ListaArray;
import java.time.LocalDateTime;

public class EntregaProveedor {
    private Proveedor proveedor;
    private ListaArray<DetalleProducto> productos;
    private LocalDateTime fecha;

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public ListaArray<DetalleProducto> getProductos() {
        return productos;
    }

    public void setProductos(ListaArray<DetalleProducto> productos) {
        this.productos = productos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
