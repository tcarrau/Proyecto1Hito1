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

    public String toString(){
        return("Producto : " + getProducto() + "\nCantidad : " + cantidad);
    }

    public int getCantidadMinima(){
        return cantMin;
    }

    public void setCantidadMinima(int cantidad){
        this.cantMin = cantidad;
    }
}
