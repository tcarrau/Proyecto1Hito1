package ucu.edu.almacen;
import ucu.edu.implementaciones.ListaArray;
import java.time.*;

public class PedidoSucursal {
    private int prioridad;
    private ListaArray<DetalleProducto> productos;
    private ListaArray<PasoRecoleccion> pasosRecoleccion = new ListaArray<>();
    private LocalDateTime fecha;
    private Sucursal sucursal;

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public ListaArray<DetalleProducto> getProductos() {
        return productos;
    }

    public void setProductos(ListaArray<DetalleProducto> productos) {
        this.productos = productos;
    }

    /** Pasos ordenados para recolectar el pedido desde las posiciones del depósito. */
    public ListaArray<PasoRecoleccion> getPasosRecoleccion() {
        return pasosRecoleccion;
    }

    public void setPasosRecoleccion(ListaArray<PasoRecoleccion> pasosRecoleccion) {
        this.pasosRecoleccion = pasosRecoleccion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public String toString() {
        return "PedidoSucursal{prioridad=" + prioridad + ", \nproductos=" + productos
                + ", \nfecha=" + fecha + ", \nsucursal=" + sucursal + "}";
    }

}
