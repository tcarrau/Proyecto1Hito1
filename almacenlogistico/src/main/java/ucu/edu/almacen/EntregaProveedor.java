package ucu.edu.almacen;

import ucu.edu.implementaciones.ListaArray;
import java.time.LocalDateTime;

public class EntregaProveedor {
    private Proveedor proveedor;
    private ListaArray<DetalleProducto> productos;
    private ListaArray<RegistroInventario> mercaderiaUbicada;
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

    /**
     * Detalla en qué posiciones se guarda cada producto recibido.
     * Cada registro contiene un producto y sus cantidades por posición.
     */
    public ListaArray<RegistroInventario> getMercaderiaUbicada() {
        return mercaderiaUbicada;
    }

    public void setMercaderiaUbicada(ListaArray<RegistroInventario> mercaderiaUbicada) {
        this.mercaderiaUbicada = mercaderiaUbicada;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "EntregaProveedor{proveedor=" + proveedor + ", productos=" + productos
                + ", mercaderiaUbicada=" + mercaderiaUbicada + ", fecha=" + fecha + "}";
    }
}
