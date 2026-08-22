package ucu.edu.almacen;

import java.util.NoSuchElementException;
import java.util.PriorityQueue;

import ucu.edu.implementaciones.Cola;
import ucu.edu.implementaciones.ListaArray;

public class AlmacenLogistico {
    private ListaArray<DetalleProducto> productos;
    private ListaArray<TerminalCarga> terminales;
    private PriorityQueue<PedidoSucursal> pedidosPendientes;
    private Cola<EntregaProveedor> esperaProveedores;


    public AlmacenLogistico() {
        this.productos = new ListaArray<>();
        this.terminales = new ListaArray<>();
        this.esperaProveedores = new Cola<>(15);
        this.pedidosPendientes = new PriorityQueue<>((pedido1, pedido2) -> Integer.compare(pedido2.getPrioridad(), pedido1.getPrioridad()));

    }

/*
        ==================================
        ====== Cosas con inventario ======
        ==================================
*/


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
        if (detalleProducto == null) {
            return false;
        }

        int cant = detalleProducto.getCantidad();
        if (cant >= cantidad) {
            detalleProducto.setCantidad(cant - cantidad);
            return true;
        }

        return false;
    }

    public boolean hayCantNecesarias(String codigo, int cantidad){
        if (cantidad <= 0) {
            return false;
        }

        DetalleProducto detalleProducto = buscarProducto(codigo);
        if (detalleProducto == null) {
            return false;
        }

        int cant = detalleProducto.getCantidad();
        if (cant >= cantidad) {
            return true;
        }

        return false;
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


    /*
            ==============================================
            ====== Cosas con entrega de proveedores ======
            ==============================================
    */

    /*
     Registra una entrega de proveedor en la cola de espera.
     */
    public void registrarEntregaProveedor(EntregaProveedor entrega) {
        // validar y encolar la entrega.

        if(entrega == null){
            throw new UnsupportedOperationException("no se puede agregar una entrega vacia");
        }

        if (entrega.getProveedor() == null
                || entrega.getProductos() == null
                || entrega.getFecha() == null) {
            throw new UnsupportedOperationException("Alguno de los datos de la entrega no es valido");
        }


        esperaProveedores.poneEnCola(entrega);
        
    }

    /**
     * Obtiene la próxima entrega pendiente sin retirarla de la cola.
     */
    public EntregaProveedor obtenerSiguienteEntregaProveedor() {
        // consultar la primera entrega en espera.
        if (esperaProveedores.esVacia()) {
            throw new NoSuchElementException("No hay entregas pendientes");
        }

        return esperaProveedores.frente();
    }

    /**
     * Descarga la próxima entrega pendiente y actualiza el stock.
     */
    public EntregaProveedor descargarSiguienteEntregaProveedor() {
        //  desencolar la entrega y actualizar el inventario.
        EntregaProveedor entrega = esperaProveedores.quitaDeCola();
        actualizarStockEntrega(entrega);
        return entrega;
    }

    /**
     * Indica si existen entregas de proveedores pendientes.
     */
    public boolean hayEntregasPendientes() {
        // consultar el estado de la cola de proveedores.
        return !esperaProveedores.esVacia();
    }

    /**
     * Devuelve la cantidad de entregas de proveedores pendientes.
     */
    public int cantidadEntregasPendientes() {
        //  obtener la cantidad de elementos de la cola.
        if (esperaProveedores == null) {
            return 0;
        }
        else {
            return esperaProveedores.tamaño();
        }
    }

    /**
     * Actualiza el stock de todos los productos incluidos en una entrega.
     */
    private void actualizarStockEntrega(EntregaProveedor entrega) {

        for(int i = 0; i < entrega.getProductos().tamaño(); i++){
            DetalleProducto detalle = entrega.getProductos().obtener(i);
            aumentarStock(detalle.getProducto().getCodigo(), detalle.getCantidad());
        }
    }
    

/*
        ============================================================
        ============= Reabastecimientos de sucursales ==============
        ============================================================
*/

/**
 * Registra un pedido para despachar a una sucursal.
 * La prioridad debe basarse en los clientes promedio de la sucursal.
 */
public void registrarPedidoReabastecimiento(PedidoSucursal pedido) {

    if(pedido == null || pedido.getSucursal() == null || pedido.getProductos() == null || pedido.getFecha() == null){
        throw new UnsupportedOperationException("El pedido no puede ser null");
    }
    pedido.setPrioridad(calcularPrioridadPedido(pedido));
    pedidosPendientes.offer(pedido);
}

/**
 * Obtiene el pedido de mayor prioridad sin retirarlo de la cola.
 */
public PedidoSucursal obtenerSiguientePedidoReabastecimiento() {

    if(pedidosPendientes.isEmpty()){
        return null;
    }
    return pedidosPendientes.peek();
}

/**
 * Despacha el pedido de mayor prioridad y descuenta los productos del inventario.
 * Tiene que haber la cantidad de productos necesarios si no no se hace el despacho
 */
public PedidoSucursal despacharSiguientePedidoReabastecimiento() {

    if(!hayPedidosReabastecimientoPendientes()){
        return null;
    }

    PedidoSucursal pedidoADespachar = pedidosPendientes.peek();
    if(!hayStockSuficienteParaPedido(pedidoADespachar)){
        return null;
    }

    actualizarStockDespacho(pedidoADespachar);

    return pedidosPendientes.poll();

}

/**
 * Indica si hay pedidos de reabastecimiento pendientes.
 */
public boolean hayPedidosReabastecimientoPendientes() {

    return pedidosPendientes.isEmpty() ? false : true;
}

/**
 * Devuelve la cantidad de pedidos de reabastecimiento pendientes.
 */
public int cantidadPedidosReabastecimientoPendientes() {

    return pedidosPendientes.size();
}

/**
 * Calcula la prioridad de un pedido usando los clientes promedio de su sucursal.
 */
private int calcularPrioridadPedido(PedidoSucursal pedido) {

    return pedido.getSucursal().getClientesPromedio();

}

/**
 * Verifica que exista stock suficiente para todos los productos del pedido.
 */
private boolean hayStockSuficienteParaPedido(PedidoSucursal pedido) {
    for(int i = 0; i < pedido.getProductos().tamaño(); i++){
        DetalleProducto detalleActual = pedido.getProductos().obtener(i);
        String codigo = detalleActual.getProducto().getCodigo();
        boolean productoYaProcesado = false;

        // Evita validar más de una vez un producto que aparece repetido.
        for (int j = 0; j < i; j++) {
            DetalleProducto detalleAnterior = pedido.getProductos().obtener(j);

            if (detalleAnterior.getProducto().getCodigo().equals(codigo)) {
                productoYaProcesado = true;
                break;
            }
        }

        if (productoYaProcesado) {
            continue;
        }

        int cantidadTotalSolicitada = 0;

        // Suma todas las líneas del pedido que corresponden al mismo producto.
        for (int j = i; j < pedido.getProductos().tamaño(); j++) {
            DetalleProducto detalle = pedido.getProductos().obtener(j);

            if (detalle.getProducto().getCodigo().equals(codigo)) {
                cantidadTotalSolicitada += detalle.getCantidad();
            }
        }

        boolean hayCantNecesaria = hayCantNecesarias(codigo, cantidadTotalSolicitada);

        if(!hayCantNecesaria){
            return false;
        }
    }

    return true;
}

/**
 * Descuenta del inventario los productos incluidos en un pedido despachado.
 */
private void actualizarStockDespacho(PedidoSucursal pedido) {

    for(int i = 0; i < pedido.getProductos().tamaño(); i++){
        disminuirStock(pedido.getProductos().obtener(i).getProducto().getCodigo(), pedido.getProductos().obtener(i).getCantidad());
    }
}

}
