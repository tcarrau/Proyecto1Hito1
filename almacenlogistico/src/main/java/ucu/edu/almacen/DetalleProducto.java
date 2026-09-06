package ucu.edu.almacen;

public class DetalleProducto {
    private Producto producto;
    private int cantidad;
    private int cantMin;
    private RegistroInventario registroInventario;

    /** Vista compatible con las operaciones del primer hito. */
    public DetalleProducto(RegistroInventario registroInventario) {
        this.registroInventario = registroInventario;
    }

    public DetalleProducto() {
        // Constructor requerido para crear detalles de pedidos y entregas.
    }

    public Producto getProducto() {
        if (registroInventario != null) {
            return registroInventario.getProducto();
        }
        return producto;
    }

    public void setProducto(Producto producto) {
        if (registroInventario != null) {
            registroInventario.setProducto(producto);
            return;
        }
        this.producto = producto;
    }

    public int getCantidad() {
        if (registroInventario != null) {
            int total = 0;
            for (int i = 0; i < registroInventario.getUbicaciones().tamaño(); i++) {
                total += registroInventario.getUbicaciones().obtener(i).getCantidad();
            }
            return total;
        }
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (registroInventario != null) {
            throw new UnsupportedOperationException(
                    "La cantidad de un producto registrado se modifica desde Inventario");
        }
        this.cantidad = cantidad;
    }

    @Override
    public String toString(){
        return "DetalleProducto{producto=" + getProducto() + ", cantidad=" + getCantidad()
                + ", cantidadMinima=" + getCantidadMinima() + "}";
    }

    public int getCantidadMinima(){
        if (registroInventario != null) {
            return registroInventario.getCantidadMinima();
        }
        return cantMin;
    }

    public void setCantidadMinima(int cantidad){
        if (registroInventario != null) {
            registroInventario.setCantidadMinima(cantidad);
            return;
        }
        this.cantMin = cantidad;
    }
}
