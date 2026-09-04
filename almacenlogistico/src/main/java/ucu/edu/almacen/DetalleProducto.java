package ucu.edu.almacen;

public class DetalleProducto {
    private Producto producto;
    private int cantidad;
    private int cantMin;

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString(){
        return "DetalleProducto{producto=" + producto + ", cantidad=" + cantidad
                + ", cantidadMinima=" + cantMin + "}";
    }

    public int getCantidadMinima(){
        return cantMin;
    }

    public void setCantidadMinima(int cantidad){
        this.cantMin = cantidad;
    }
}
