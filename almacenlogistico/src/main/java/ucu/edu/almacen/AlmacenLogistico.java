package ucu.edu.almacen;

import ucu.edu.implementaciones.ListaArray;
import ucu.edu.implementaciones.Cola;

public class AlmacenLogistico {
    private ListaArray<DetalleProducto> productos;
    private ListaArray<TerminalCarga> terminales;
    private ColaConPrioridad<PedidoSucursal> pedidosPendientes;
    private Cola<EntregaProveedor> esperaProveedores;


    public AlmacenLogistico() {
        this.productos = new ListaArray<>();
        this.terminales = new ListaArray<>();
        this.esperaProveedores = new Cola<>(15);

    }

    public void registrarProducto(Producto producto) {
        // validar que no exista y agregarlo al inventario
        if (producto == null || producto.getCodigo() == null
                 || producto.getCodigo().isBlank()) {
             throw new IllegalArgumentException("El producto debe tener un código");
        }

        if (buscarProducto(producto.getCodigo()) != null) {
           return;
         }

        
        DetalleProducto nuevoProducto = new DetalleProducto();
        nuevoProducto.setProducto(producto);
        nuevoProducto.setCantidad(0);
        nuevoProducto.setCantidadMinima(0);
        productos.agregar(nuevoProducto);

    }

    public DetalleProducto buscarProducto(String codigo) {
        // recorrer inventario y encontrarlo

        if (codigo == null) {
            return null;
        }

        for(int i = 0; i < productos.tamaño(); i++){
            DetalleProducto detalle = productos.obtener(i);
            Producto actual = detalle.getProducto();
            if(actual.getCodigo().equals(codigo)){
                return detalle;
            }
        }

        return null;
    }

    public void aumentarStock(String codigo, int cantidad) {
        // usar al recibir una entrega de proveedor
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        DetalleProducto detalleProducto = buscarProducto(codigo);

        if (detalleProducto == null) {
            throw new IllegalArgumentException("No existe un producto con ese código");
        }

        int cant = detalleProducto.getCantidad();
        detalleProducto.setCantidad(cant + cantidad);
    }

    public boolean disminuirStock(String codigo, int cantidad) {
        // usar al despachar un pedido a una sucursal
        if (cantidad <= 0) {
            return false;
        }

        DetalleProducto detalleProducto = buscarProducto(codigo);
        int cant = detalleProducto.getCantidad();
        if(cant >= cantidad && detalleProducto != null){
            detalleProducto.setCantidad(cant - cantidad);
            return true;
        }
        else {
            return false;
        }
        
    }

    public ListaArray<Producto> productosConStockBajo() {
        // devolver los que estén debajo de su stock mínimo

        ListaArray<Producto> productosStockBajo = new ListaArray<>();
        
        for(int i = 0; i < productos.tamaño(); i++){
            
            DetalleProducto detalle = productos.obtener(i);
            Producto actual = detalle.getProducto();
            int cantMin = detalle.getCantidadMinima();
            int cantActual = detalle.getCantidad();
        
            if (cantActual <= cantMin) {
                productosStockBajo.agregar(actual);
            }
        }

        return productosStockBajo;
    }

    public void listarProductosYStock(){
        for(int i = 0; i < productos.tamaño(); i++){
            DetalleProducto detalle = productos.obtener(i);
            System.out.println(detalle.toString());
        }
    }
}
