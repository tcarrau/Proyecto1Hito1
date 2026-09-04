package ucu.edu.almacen;

/** Paso pendiente de una futura preparación de pedidos. */
public class PasoRecoleccion {

    private PedidoSucursal pedido;
    private StockUbicado stockOrigen;
    private int cantidad;

    public PasoRecoleccion() {
        // Constructor requerido para el armado incremental del dominio.
    }

    public PasoRecoleccion(PedidoSucursal pedido, StockUbicado stockOrigen, int cantidad) {
        this.pedido = pedido;
        this.stockOrigen = stockOrigen;
        this.cantidad = cantidad;
    }

    public PedidoSucursal getPedido() {
        return pedido;
    }

    public void setPedido(PedidoSucursal pedido) {
        this.pedido = pedido;
    }

    public StockUbicado getStockOrigen() {
        return stockOrigen;
    }

    public void setStockOrigen(StockUbicado stockOrigen) {
        this.stockOrigen = stockOrigen;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void ejecutar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
