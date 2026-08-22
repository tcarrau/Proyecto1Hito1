package ucu.edu.almacen;

public class Sucursal {
    private String codigo;
    private String nombre;
    private int clientesPromedio;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getClientesPromedio() {
        return clientesPromedio;
    }

    public void setClientesPromedio(int clientesPromedio) {
        this.clientesPromedio = clientesPromedio;
    }
}
