package ucu.edu.almacen;

import java.util.NoSuchElementException;
import java.time.LocalDateTime;

import ucu.edu.implementaciones.Cola;
import ucu.edu.implementaciones.ListaArray;
import ucu.edu.implementaciones.MonticuloMaximo;

public class AlmacenLogistico {
    private Deposito deposito;
    private Inventario inventario;
    private MonticuloMaximo<PedidoSucursal> pedidosPrioritarios;
    private ListaArray<TerminalCarga> terminales;
    private Cola<EntregaProveedor> esperaProveedores;


    public AlmacenLogistico() {
        this(false);
    }

    /**
     * Crea un almacén. Si cargarDatosBase es true, agrega datos de ejemplo
     * para probar las funciones desde un menú interactivo.
     */
    public AlmacenLogistico(boolean cargarDatosBase) {
        this.deposito = new Deposito();
        this.inventario = new Inventario();
        this.pedidosPrioritarios = new MonticuloMaximo<>();
        this.terminales = new ListaArray<>();
        this.esperaProveedores = new Cola<>(15);
        if (cargarDatosBase) {
            cargarDatosBase();
        }
    }

    /** Estructura física del depósito para las extensiones del siguiente hito. */
    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    /** Índice de inventario por producto y sus ubicaciones. */
    public Inventario getInventario() {
        return inventario;
    }

    /** Montículo previsto para los pedidos prioritarios del siguiente hito. */
    public MonticuloMaximo<PedidoSucursal> getPedidosPrioritarios() {
        return pedidosPrioritarios;
    }

    /**
     * Carga productos, una entrega pendiente y pedidos de sucursales de ejemplo.
     */
    private void cargarDatosBase() {
        registrarTerminal(new TerminalCarga(1));
        registrarTerminal(new TerminalCarga(2));

        Producto yerba = crearProductoBase("P-001", "Yerba mate");
        Producto leche = crearProductoBase("P-002", "Leche entera");
        Producto arroz = crearProductoBase("P-003", "Arroz blanco");

        registrarProducto(yerba);
        registrarProducto(leche);
        registrarProducto(arroz);
        aumentarStock("P-001", 25);
        aumentarStock("P-002", 12);
        buscarProducto("P-001").setCantidadMinima(10);
        buscarProducto("P-002").setCantidadMinima(8);
        buscarProducto("P-003").setCantidadMinima(5);

        Proveedor proveedor = new Proveedor();
        proveedor.setCodigo("PROV-001");
        proveedor.setNombre("Distribuidora del Sur");

        ListaArray<DetalleProducto> productosEntrega = new ListaArray<>();
        productosEntrega.agregar(crearDetalleBase(arroz, 30));
        EntregaProveedor entrega = new EntregaProveedor();
        entrega.setProveedor(proveedor);
        entrega.setProductos(productosEntrega);
        entrega.setFecha(LocalDateTime.now());
        registrarEntregaProveedor(entrega);

        Sucursal sucursalCentro = crearSucursalBase("SUC-001", "Centro", 400);
        Sucursal sucursalNorte = crearSucursalBase("SUC-002", "Norte", 200);
        registrarPedidoReabastecimiento(crearPedidoBase(
                sucursalCentro, crearDetalleBase(yerba, 6)));
        registrarPedidoReabastecimiento(crearPedidoBase(
                sucursalNorte, crearDetalleBase(leche, 4)));
    }

    private Producto crearProductoBase(String codigo, String nombre) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        return producto;
    }

    private DetalleProducto crearDetalleBase(Producto producto, int cantidad) {
        DetalleProducto detalle = new DetalleProducto();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        return detalle;
    }

    private Sucursal crearSucursalBase(String codigo, String nombre, int clientesPromedio) {
        Sucursal sucursal = new Sucursal();
        sucursal.setCodigo(codigo);
        sucursal.setNombre(nombre);
        sucursal.setClientesPromedio(clientesPromedio);
        return sucursal;
    }

    private PedidoSucursal crearPedidoBase(Sucursal sucursal, DetalleProducto detalle) {
        ListaArray<DetalleProducto> productosPedido = new ListaArray<>();
        productosPedido.agregar(detalle);

        PedidoSucursal pedido = new PedidoSucursal();
        pedido.setSucursal(sucursal);
        pedido.setProductos(productosPedido);
        pedido.setFecha(LocalDateTime.now());
        return pedido;
    }

    /*
            ==================================
            ====== Terminales de carga ======
            ==================================
    */

    public void registrarTerminal(TerminalCarga terminal) {
        if (terminal == null) {
            throw new IllegalArgumentException("La terminal no puede ser null");
        }

        if (buscarTerminal(terminal.getId()) != null) {
            throw new IllegalArgumentException("Ya existe una terminal con ese id");
        }

        terminales.agregar(terminal);
    }

    public TerminalCarga buscarTerminal(int id) {
        for (int i = 0; i < terminales.tamaño(); i++) {
            TerminalCarga terminal = terminales.obtener(i);

            if (terminal.getId() == id) {
                return terminal;
            }
        }

        return null;
    }

    public ListaArray<TerminalCarga> obtenerTerminales() {
        return terminales;
    }

    public void cambiarHabilitacionTerminal(int id, boolean habilitada) {
        TerminalCarga terminal = buscarTerminal(id);

        if (terminal == null) {
            throw new IllegalArgumentException("No existe una terminal con ese id");
        }

        terminal.setHabilitada(habilitada);
    }

    public void iniciarOperacionEnTerminal(int id, OperacionCarga operacion) {
        TerminalCarga terminal = buscarTerminal(id);

        if (terminal == null) {
            throw new IllegalArgumentException("No existe una terminal con ese id");
        }

        if (operacion == null || operacion == OperacionCarga.LIBRE) {
            throw new IllegalArgumentException("La operación debe ser de carga o descarga");
        }

        if (!terminal.estaLibre()) {
            throw new IllegalStateException("La terminal no está disponible");
        }

        terminal.setOperacionActual(operacion);
    }

    public void liberarTerminal(int id) {
        TerminalCarga terminal = buscarTerminal(id);

        if (terminal == null) {
            throw new IllegalArgumentException("No existe una terminal con ese id");
        }

        terminal.setOperacionActual(OperacionCarga.LIBRE);
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

        
        inventario.registrarProducto(producto);

    }

    public DetalleProducto buscarProducto(String codigo) {
        // recorrer inventario y encontrarlo

        if (codigo == null) {
            return null;
        }

        return inventario.buscarDetalleProducto(codigo);
    }

    public void aumentarStock(String codigo, int cantidad) {
        // usar al recibir una entrega de proveedor
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        inventario.aumentarStock(codigo, cantidad);
    }

    public boolean disminuirStock(String codigo, int cantidad) {
        // usar al despachar un pedido a una sucursal
        if (cantidad <= 0) {
            return false;
        }

        return inventario.disminuirStock(codigo, cantidad);
    }

    public boolean hayCantNecesarias(String codigo, int cantidad){
        if (cantidad <= 0) {
            return false;
        }

        return inventario.obtenerStockTotal(codigo) >= cantidad;
    }

    public void listarProductosYStock(){
        ListaArray<RegistroInventario> registros = inventario.listarInventarioOrdenado();
        for(int i = 0; i < registros.tamaño(); i++){
            System.out.println(new DetalleProducto(registros.obtener(i)));
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
     * Descarga una entrega que ya indica las posiciones físicas de guardado.
     * La cola FIFO permanece en el almacén y el inventario procesa sus registros.
     */
    public EntregaProveedor descargarSiguienteEntregaProveedorUbicada() {
        if (esperaProveedores.esVacia()) {
            throw new NoSuchElementException("No hay entregas pendientes");
        }

        EntregaProveedor entrega = esperaProveedores.frente();
        if (entrega.getMercaderiaUbicada() == null) {
            throw new IllegalStateException("La entrega no indica posiciones de guardado");
        }

        inventario.registrarMercaderiaRecibida(entrega.getMercaderiaUbicada());
        return esperaProveedores.quitaDeCola();
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
        pedidosPrioritarios.agregar(pedido);
    }

    /**
     * Obtiene el pedido de mayor prioridad sin retirarlo de la cola.
     */
    public PedidoSucursal obtenerSiguientePedidoReabastecimiento() {

        if(pedidosPrioritarios.esVacio()){
            return null;
        }
        return pedidosPrioritarios.obtenerMaximo();
    }

    /**
     * Despacha el pedido de mayor prioridad y descuenta los productos del inventario.
     * Tiene que haber la cantidad de productos necesarios si no no se hace el despacho
     */
    public PedidoSucursal despacharSiguientePedidoReabastecimiento() {

        if(!hayPedidosReabastecimientoPendientes()){
            return null;
        }

        PedidoSucursal pedidoADespachar = pedidosPrioritarios.obtenerMaximo();
        if(!hayStockSuficienteParaPedido(pedidoADespachar)){
            return null;
        }

        actualizarStockDespacho(pedidoADespachar);

        return pedidosPrioritarios.quitarMaximo();

    }

    /**
     * Indica si hay pedidos de reabastecimiento pendientes.
     */
    public boolean hayPedidosReabastecimientoPendientes() {

        return !pedidosPrioritarios.esVacio();
    }

    /**
     * Devuelve la cantidad de pedidos de reabastecimiento pendientes.
     */
    public int cantidadPedidosReabastecimientoPendientes() {

        return pedidosPrioritarios.tamaño();
    }

    /** Modifica la prioridad de un pedido pendiente y reordena el montículo. */
    public boolean modificarPrioridadPedido(PedidoSucursal pedido, int nuevaPrioridad) {
        if (pedido == null || nuevaPrioridad < 0 || !pedidosPrioritarios.actualizar(pedido)) {
            return false;
        }
        pedido.setPrioridad(nuevaPrioridad);
        return pedidosPrioritarios.actualizar(pedido);
    }

    /** Modifica la prioridad del pedido pendiente asociado a una sucursal. */
    public boolean modificarPrioridadPedido(String codigoSucursal, int nuevaPrioridad) {
        if (codigoSucursal == null || nuevaPrioridad < 0) {
            return false;
        }
        PedidoSucursal pedido = pedidosPrioritarios.buscar(actual -> actual.getSucursal() != null
                && codigoSucursal.equals(actual.getSucursal().getCodigo()));
        return modificarPrioridadPedido(pedido, nuevaPrioridad);
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


    /*
            ==================================
            ========== Consultas =============
            ==================================
    */

    // Consulta 1: cantidad total de unidades en inventario.
    public int cantidadInventarioTotal(){
        int suma = 0;
        ListaArray<RegistroInventario> registros = inventario.listarInventarioOrdenado();
        for(int i = 0; i < registros.tamaño(); i++){
            suma += inventario.obtenerStockTotal(registros.obtener(i).getProducto().getCodigo());
        }

        return suma;
    }

    // Consulta 2: productos cuyo stock es igual o menor al mínimo definido.
    public ListaArray<Producto> productosConStockBajo() {
        // devolver los que estén debajo de su stock mínimo

        ListaArray<Producto> productosStockBajo = new ListaArray<>();
        
        ListaArray<RegistroInventario> registros = inventario.listarInventarioOrdenado();
        for(int i = 0; i < registros.tamaño(); i++){
            DetalleProducto detalle = new DetalleProducto(registros.obtener(i));
            Producto actual = detalle.getProducto();
            int cantMin = detalle.getCantidadMinima();
            int cantActual = detalle.getCantidad();
        
            if (cantActual <= cantMin) {
                productosStockBajo.agregar(actual);
            }
        }

        return productosStockBajo;
    }
    
    // Consulta 3: stock total disponible de un producto por código.
    public int obtenerStockProducto(String codigo){
        return inventario.obtenerStockTotal(codigo);
    }


    // Consulta 4: proveedores que tienen entregas pendientes, sin repetidos.
    public ListaArray<Proveedor> proveedoresConEntregasPendientes(){
        ListaArray<Proveedor> proveedoresPendientes = new ListaArray<>();

        for (int i = 0; i < esperaProveedores.tamaño(); i++) {
            Proveedor proveedor = esperaProveedores.obtener(i).getProveedor();
            boolean yaAgregado = false;

            for (int j = 0; j < proveedoresPendientes.tamaño(); j++) {
                Proveedor proveedorAgregado = proveedoresPendientes.obtener(j);

                if (proveedorAgregado.getCodigo().equals(proveedor.getCodigo())) {
                    yaAgregado = true;
                    break;
                }
            }

            if (!yaAgregado) {
                proveedoresPendientes.agregar(proveedor);
            }
        }

        return proveedoresPendientes;
    }

    // Consulta 5: productos registrados que no tienen unidades en stock.
    public ListaArray<Producto> productosSinStock() {
        ListaArray<Producto> productosSinStock = new ListaArray<>();

        ListaArray<RegistroInventario> registros = inventario.listarInventarioOrdenado();
        for (int i = 0; i < registros.tamaño(); i++) {
            DetalleProducto detalle = new DetalleProducto(registros.obtener(i));

            if (detalle.getCantidad() == 0) {
                productosSinStock.agregar(detalle.getProducto());
            }
        }

        return productosSinStock;
    }

    /*
     * ============================================================
     * ============= Terminales ==============
     * ============================================================
     */

    private TerminalCarga buscarTerminalDisponible() {

        for (int i = 0; i < terminales.tamaño(); i++) {
            TerminalCarga terminal = terminales.obtener(i);
            if (terminal.estaLibre()) {
                return terminal;
            }
        }

        return null;
    }

    public TerminalCarga asignarTerminalCarga(OperacionCarga operacion) {
        TerminalCarga terminal = buscarTerminalDisponible();
        if (terminal == null) {
            return null;
        }
        terminal.setOperacionActual(operacion);
        return terminal;
    }

    public void liberarTerminalCarga(TerminalCarga terminal) {
        if (terminal == null) {
            throw new IllegalArgumentException("La terminal no puede ser null");
        }
        terminal.setOperacionActual(OperacionCarga.LIBRE);
    }

    public ListaArray<TerminalCarga> terminalesLibres() {
        ListaArray<TerminalCarga> libres = new ListaArray<>();
        for (int i = 0; i < terminales.tamaño(); i++) {
            TerminalCarga terminal = terminales.obtener(i);
            if (terminal.estaLibre()) {
                libres.agregar(terminal);
            }
        }
        return libres;
    }

    public ListaArray<TerminalCarga> terminalesOcupadas() {
        ListaArray<TerminalCarga> ocupadas = new ListaArray<>();
        for (int i = 0; i < terminales.tamaño(); i++) {
            TerminalCarga terminal = terminales.obtener(i);
            if (!terminal.estaLibre()) {
                ocupadas.agregar(terminal);
            }
        }
        return ocupadas;
    }

    private TerminalCarga buscarTerminalPorId(int id) {
        for (int i = 0; i < terminales.tamaño(); i++) {
            TerminalCarga terminal = terminales.obtener(i);
            if (terminal.getId() == id) {
                return terminal;
            }
        }
        return null;
    }

    public void deshabilitarTerminal(int id) {
        TerminalCarga terminal = buscarTerminalPorId(id);
        if (terminal == null) {
            throw new IllegalArgumentException("No existe una terminal con ese ID");
        }
        terminal.setHabilitada(false);
    }

    public void habilitarTerminal(int id) {
        TerminalCarga terminal = buscarTerminalPorId(id);
        if (terminal == null) {
            throw new IllegalArgumentException("No existe una terminal con ese ID");
        }
        terminal.setHabilitada(true);
    }
}
