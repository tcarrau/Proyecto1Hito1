package ucu.edu.almacen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ucu.edu.implementaciones.ListaArray;

class AlmacenLogisticoTest {

    @Test
    void registrarProductoLoAgregaConStockInicialCero() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        Producto producto = productoConCodigo("P-001");

        almacen.registrarProducto(producto);

        DetalleProducto detalle = almacen.buscarProducto("P-001");
        assertNotNull(detalle);
        assertEquals(producto, detalle.getProducto());
        assertEquals(0, detalle.getCantidad());
    }

    @Test
    void aumentarStockIncrementaLaCantidadDelProducto() {
        AlmacenLogistico almacen = almacenConProducto("P-001");

        almacen.aumentarStock("P-001", 12);

        assertEquals(12, almacen.buscarProducto("P-001").getCantidad());
    }

    @Test
    void disminuirStockDescuentaCuandoHayCantidadSuficiente() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        almacen.aumentarStock("P-001", 12);

        boolean seDespacho = almacen.disminuirStock("P-001", 5);

        assertTrue(seDespacho);
        assertEquals(7, almacen.buscarProducto("P-001").getCantidad());
    }

    @Test
    void disminuirStockNoModificaElStockCuandoNoAlcanza() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        almacen.aumentarStock("P-001", 4);

        boolean seDespacho = almacen.disminuirStock("P-001", 5);

        assertFalse(seDespacho);
        assertEquals(4, almacen.buscarProducto("P-001").getCantidad());
    }

    @Test
    void aumentarStockRechazaCantidadesNoPositivas() {
        AlmacenLogistico almacen = almacenConProducto("P-001");

        assertThrows(IllegalArgumentException.class,
                () -> almacen.aumentarStock("P-001", 0));
        assertThrows(IllegalArgumentException.class,
                () -> almacen.aumentarStock("P-001", -1));
    }

    @Test
    void buscarProductoConCodigoNullDevuelveNull() {
        AlmacenLogistico almacen = almacenConProducto("P-001");

        assertNull(almacen.buscarProducto(null));
    }

    @Test
    void aumentarStockRechazaProductoNoRegistrado() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertThrows(IllegalArgumentException.class,
                () -> almacen.aumentarStock("NO-EXISTE", 2));
    }

    @Test
    void disminuirStockDeProductoNoRegistradoDevuelveFalse() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertFalse(almacen.disminuirStock("NO-EXISTE", 1));
    }

    @Test
    void disminuirStockConCantidadExactaDejaElStockEnCero() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        almacen.aumentarStock("P-001", 5);

        boolean seDespacho = almacen.disminuirStock("P-001", 5);

        assertTrue(seDespacho);
        assertEquals(0, almacen.buscarProducto("P-001").getCantidad());
    }

    @Test
    void registrarProductoDuplicadoNoReemplazaElProductoOriginal() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        Producto productoOriginal = productoConCodigo("P-001");
        Producto productoDuplicado = productoConCodigo("P-001");
        productoDuplicado.setNombre("Producto duplicado");
        almacen.registrarProducto(productoOriginal);

        almacen.registrarProducto(productoDuplicado);

        assertSame(productoOriginal, almacen.buscarProducto("P-001").getProducto());
    }

    @Test
    void registrarProductoRechazaProductoONombreDeCodigoInvalidos() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        Producto productoSinCodigo = new Producto();
        Producto productoConCodigoVacio = productoConCodigo("");
        Producto productoConCodigoEnBlanco = productoConCodigo("   ");

        assertThrows(IllegalArgumentException.class,
                () -> almacen.registrarProducto(null));
        assertThrows(IllegalArgumentException.class,
                () -> almacen.registrarProducto(productoSinCodigo));
        assertThrows(IllegalArgumentException.class,
                () -> almacen.registrarProducto(productoConCodigoVacio));
        assertThrows(IllegalArgumentException.class,
                () -> almacen.registrarProducto(productoConCodigoEnBlanco));
    }

    @Test
    void productosConStockBajoIncluyeStockIgualAlMinimo() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        almacen.aumentarStock("P-001", 4);
        almacen.buscarProducto("P-001").setCantidadMinima(4);

        ListaArray<Producto> productos = almacen.productosConStockBajo();

        assertEquals(1, productos.tamaño());
        assertEquals("P-001", productos.obtener(0).getCodigo());
    }

    @Test
    void productosConStockBajoIncluyeStockMenorAlMinimo() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        almacen.aumentarStock("P-001", 3);
        almacen.buscarProducto("P-001").setCantidadMinima(4);

        ListaArray<Producto> productos = almacen.productosConStockBajo();

        assertEquals(1, productos.tamaño());
        assertEquals("P-001", productos.obtener(0).getCodigo());
    }

    @Test
    void productosConStockBajoDevuelveListaVaciaConInventarioVacio() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertEquals(0, almacen.productosConStockBajo().tamaño());
    }

    @Test
    void noDespachaPedidoSiUnProductoRepetidoSuperaElStockDisponible() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        almacen.aumentarStock("P-001", 5);

        PedidoSucursal pedido = new PedidoSucursal();
        pedido.setProductos(productosConDetalles(
                detalleConProducto("P-001", 3),
                detalleConProducto("P-001", 3)));
        pedido.setSucursal(sucursalDePrueba());
        pedido.setFecha(LocalDateTime.now());
        almacen.registrarPedidoReabastecimiento(pedido);

        PedidoSucursal pedidoDespachado = almacen.despacharSiguientePedidoReabastecimiento();

        assertNull(pedidoDespachado);
        assertEquals(5, almacen.buscarProducto("P-001").getCantidad());
        assertEquals(1, almacen.cantidadPedidosReabastecimientoPendientes());
    }

    @Test
    void registrarPedidoReabastecimientoPriorizaSucursalConMasClientes() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        PedidoSucursal pedidoPrioridadBaja = pedidoReabastecimiento("P-001", 1, 50);
        PedidoSucursal pedidoPrioridadAlta = pedidoReabastecimiento("P-002", 1, 300);

        almacen.registrarPedidoReabastecimiento(pedidoPrioridadBaja);
        almacen.registrarPedidoReabastecimiento(pedidoPrioridadAlta);

        assertEquals(50, pedidoPrioridadBaja.getPrioridad());
        assertEquals(300, pedidoPrioridadAlta.getPrioridad());
        assertSame(pedidoPrioridadAlta,
                almacen.obtenerSiguientePedidoReabastecimiento());
    }

    @Test
    void despacharPedidoReabastecimientoDescuentaStockYLoQuitaDeLaCola() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        almacen.aumentarStock("P-001", 10);
        PedidoSucursal pedido = pedidoReabastecimiento("P-001", 4, 100);
        almacen.registrarPedidoReabastecimiento(pedido);

        PedidoSucursal pedidoDespachado = almacen.despacharSiguientePedidoReabastecimiento();

        assertSame(pedido, pedidoDespachado);
        assertEquals(6, almacen.buscarProducto("P-001").getCantidad());
        assertFalse(almacen.hayPedidosReabastecimientoPendientes());
        assertEquals(0, almacen.cantidadPedidosReabastecimientoPendientes());
    }

    @Test
    void noDespachaPedidoReabastecimientoSiNoHayStockSuficiente() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        almacen.aumentarStock("P-001", 2);
        PedidoSucursal pedido = pedidoReabastecimiento("P-001", 3, 100);
        almacen.registrarPedidoReabastecimiento(pedido);

        PedidoSucursal pedidoDespachado = almacen.despacharSiguientePedidoReabastecimiento();

        assertNull(pedidoDespachado);
        assertEquals(2, almacen.buscarProducto("P-001").getCantidad());
        assertEquals(1, almacen.cantidadPedidosReabastecimientoPendientes());
    }

    @Test
    void consultaYDespachoDePedidosSinPendientesDevuelvenNull() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertNull(almacen.obtenerSiguientePedidoReabastecimiento());
        assertNull(almacen.despacharSiguientePedidoReabastecimiento());
        assertFalse(almacen.hayPedidosReabastecimientoPendientes());
    }

    @Test
    void registrarPedidoReabastecimientoRechazaPedidoIncompleto() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertThrows(UnsupportedOperationException.class,
                () -> almacen.registrarPedidoReabastecimiento(new PedidoSucursal()));
    }

    @Test
    void flujoCompletoRecibeEntregaPriorizaYDespachaReabastecimientos() {
        AlmacenLogistico almacen = almacenConProductos("P-001", "P-002");
        almacen.buscarProducto("P-002").setCantidadMinima(4);

        EntregaProveedor entrega = entregaConProductos(
                detalleConProducto("P-001", 10),
                detalleConProducto("P-002", 5));
        almacen.registrarEntregaProveedor(entrega);
        assertTrue(almacen.hayEntregasPendientes());

        assertSame(entrega, almacen.descargarSiguienteEntregaProveedor());
        assertEquals(10, almacen.buscarProducto("P-001").getCantidad());
        assertEquals(5, almacen.buscarProducto("P-002").getCantidad());

        PedidoSucursal pedidoBajaPrioridad = pedidoReabastecimiento(
                80, detalleConProducto("P-001", 3));
        PedidoSucursal pedidoAltaPrioridad = pedidoReabastecimiento(
                250,
                detalleConProducto("P-001", 4),
                detalleConProducto("P-002", 2));
        almacen.registrarPedidoReabastecimiento(pedidoBajaPrioridad);
        almacen.registrarPedidoReabastecimiento(pedidoAltaPrioridad);

        assertSame(pedidoAltaPrioridad,
                almacen.obtenerSiguientePedidoReabastecimiento());
        assertSame(pedidoAltaPrioridad,
                almacen.despacharSiguientePedidoReabastecimiento());
        assertEquals(6, almacen.buscarProducto("P-001").getCantidad());
        assertEquals(3, almacen.buscarProducto("P-002").getCantidad());

        assertSame(pedidoBajaPrioridad,
                almacen.despacharSiguientePedidoReabastecimiento());
        assertEquals(3, almacen.buscarProducto("P-001").getCantidad());
        assertFalse(almacen.hayPedidosReabastecimientoPendientes());

        ListaArray<Producto> productosConStockBajo = almacen.productosConStockBajo();
        assertEquals(1, productosConStockBajo.tamaño());
        assertEquals("P-002", productosConStockBajo.obtener(0).getCodigo());
    }

    @Test
    void proveedoresConEntregasPendientesNoRepiteProveedores() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        EntregaProveedor primeraEntrega = entregaConProducto("P-001", 2);
        EntregaProveedor segundaEntregaMismoProveedor = entregaConProducto("P-002", 3);
        EntregaProveedor terceraEntrega = entregaConProducto("P-003", 1);
        terceraEntrega.getProveedor().setCodigo("PROV-002");
        terceraEntrega.getProveedor().setNombre("Segundo proveedor");
        almacen.registrarEntregaProveedor(primeraEntrega);
        almacen.registrarEntregaProveedor(segundaEntregaMismoProveedor);
        almacen.registrarEntregaProveedor(terceraEntrega);

        ListaArray<Proveedor> proveedores = almacen.proveedoresConEntregasPendientes();

        assertEquals(2, proveedores.tamaño());
        assertEquals("PROV-001", proveedores.obtener(0).getCodigo());
        assertEquals("PROV-002", proveedores.obtener(1).getCodigo());
    }

    @Test
    void productosSinStockDevuelveSoloLosProductosSinUnidades() {
        AlmacenLogistico almacen = almacenConProductos("P-001", "P-002", "P-003");
        almacen.aumentarStock("P-002", 5);

        ListaArray<Producto> productosSinStock = almacen.productosSinStock();

        assertEquals(2, productosSinStock.tamaño());
        assertEquals("P-001", productosSinStock.obtener(0).getCodigo());
        assertEquals("P-003", productosSinStock.obtener(1).getCodigo());
    }

    @Test
    void registrarEntregaProveedorLaAgregaALaCola() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        EntregaProveedor entrega = entregaConProducto("P-001", 3);

        almacen.registrarEntregaProveedor(entrega);

        assertTrue(almacen.hayEntregasPendientes());
        assertEquals(1, almacen.cantidadEntregasPendientes());
        assertSame(entrega, almacen.obtenerSiguienteEntregaProveedor());
    }

    @Test
    void obtenerSiguienteEntregaProveedorNoLaQuitaDeLaCola() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        EntregaProveedor entrega = entregaConProducto("P-001", 3);
        almacen.registrarEntregaProveedor(entrega);

        EntregaProveedor siguiente = almacen.obtenerSiguienteEntregaProveedor();

        assertSame(entrega, siguiente);
        assertEquals(1, almacen.cantidadEntregasPendientes());
    }

    @Test
    void obtenerSiguienteEntregaProveedorLanzaExcepcionSinEntregas() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertThrows(java.util.NoSuchElementException.class,
                almacen::obtenerSiguienteEntregaProveedor);
    }

    @Test
    void descargarSiguienteEntregaProveedorActualizaStockYLaQuitaDeLaCola() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        EntregaProveedor entrega = entregaConProducto("P-001", 8);
        almacen.registrarEntregaProveedor(entrega);

        EntregaProveedor entregaDescargada = almacen.descargarSiguienteEntregaProveedor();

        assertSame(entrega, entregaDescargada);
        assertEquals(8, almacen.buscarProducto("P-001").getCantidad());
        assertFalse(almacen.hayEntregasPendientes());
        assertEquals(0, almacen.cantidadEntregasPendientes());
    }

    @Test
    void registrarEntregaProveedorRechazaEntregaSinDatosObligatorios() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        EntregaProveedor entregaIncompleta = new EntregaProveedor();

        assertThrows(UnsupportedOperationException.class,
                () -> almacen.registrarEntregaProveedor(entregaIncompleta));
    }

    @Test
    void descargarSiguienteEntregaProveedorLanzaExcepcionSinEntregas() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertThrows(java.util.NoSuchElementException.class,
                almacen::descargarSiguienteEntregaProveedor);
    }

    @Test
    void descargarEntregaConVariosProductosActualizaTodoElStock() {
        AlmacenLogistico almacen = almacenConProductos("P-001", "P-002");
        EntregaProveedor entrega = entregaConProductos(
                detalleConProducto("P-001", 4),
                detalleConProducto("P-002", 7));
        almacen.registrarEntregaProveedor(entrega);

        almacen.descargarSiguienteEntregaProveedor();

        assertEquals(4, almacen.buscarProducto("P-001").getCantidad());
        assertEquals(7, almacen.buscarProducto("P-002").getCantidad());
    }

    @Test
    void descargarEntregasRespetaElOrdenFifo() {
        AlmacenLogistico almacen = almacenConProducto("P-001");
        EntregaProveedor primera = entregaConProducto("P-001", 2);
        EntregaProveedor segunda = entregaConProducto("P-001", 5);
        almacen.registrarEntregaProveedor(primera);
        almacen.registrarEntregaProveedor(segunda);

        assertSame(primera, almacen.descargarSiguienteEntregaProveedor());
        assertEquals(2, almacen.buscarProducto("P-001").getCantidad());
        assertSame(segunda, almacen.descargarSiguienteEntregaProveedor());
        assertEquals(7, almacen.buscarProducto("P-001").getCantidad());
    }

    @Test
    void descargarEntregaConProductoNoRegistradoLanzaExcepcion() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        almacen.registrarEntregaProveedor(entregaConProducto("NO-EXISTE", 3));

        assertThrows(IllegalArgumentException.class,
                almacen::descargarSiguienteEntregaProveedor);
    }

    @Test
    void descargarEntregaRechazaCantidadesNoPositivas() {
        AlmacenLogistico almacenConCero = almacenConProducto("P-001");
        almacenConCero.registrarEntregaProveedor(entregaConProducto("P-001", 0));

        assertThrows(IllegalArgumentException.class,
                almacenConCero::descargarSiguienteEntregaProveedor);

        AlmacenLogistico almacenConNegativo = almacenConProducto("P-001");
        almacenConNegativo.registrarEntregaProveedor(entregaConProducto("P-001", -1));

        assertThrows(IllegalArgumentException.class,
                almacenConNegativo::descargarSiguienteEntregaProveedor);
    }

    @Test
    void colaDeProveedoresNoSuperaSuCapacidadMaxima() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        for (int i = 0; i < 16; i++) {
            almacen.registrarEntregaProveedor(entregaConProducto("P-" + i, 1));
        }

        assertEquals(15, almacen.cantidadEntregasPendientes());
    }

    @Test
    void registrarTerminalLaAgregaYPermiteBuscarla() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        TerminalCarga terminal = new TerminalCarga(10);

        almacen.registrarTerminal(terminal);

        assertSame(terminal, almacen.buscarTerminal(10));
        assertEquals(1, almacen.obtenerTerminales().tamaño());
        assertTrue(terminal.estaLibre());
    }

    @Test
    void registrarTerminalRechazaTerminalNulaODuplicada() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        almacen.registrarTerminal(new TerminalCarga(10));

        assertThrows(IllegalArgumentException.class,
                () -> almacen.registrarTerminal(null));
        assertThrows(IllegalArgumentException.class,
                () -> almacen.registrarTerminal(new TerminalCarga(10)));
    }

    @Test
    void noIniciaOperacionEnTerminalDeshabilitada() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        almacen.registrarTerminal(new TerminalCarga(10));
        almacen.cambiarHabilitacionTerminal(10, false);

        assertThrows(IllegalStateException.class,
                () -> almacen.iniciarOperacionEnTerminal(
                        10, OperacionCarga.DESCARGANDO_PROVEEDOR));
        assertEquals(OperacionCarga.LIBRE,
                almacen.buscarTerminal(10).getOperacionActual());
    }

    @Test
    void flujoTerminalIniciaOperacionImpideOtraYLuegoLaLibera() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        almacen.registrarTerminal(new TerminalCarga(10));

        almacen.iniciarOperacionEnTerminal(10, OperacionCarga.CARGANDO_SUCURSAL);

        assertEquals(OperacionCarga.CARGANDO_SUCURSAL,
                almacen.buscarTerminal(10).getOperacionActual());
        assertFalse(almacen.buscarTerminal(10).estaLibre());
        assertThrows(IllegalStateException.class,
                () -> almacen.iniciarOperacionEnTerminal(
                        10, OperacionCarga.DESCARGANDO_PROVEEDOR));

        almacen.liberarTerminal(10);

        assertEquals(OperacionCarga.LIBRE,
                almacen.buscarTerminal(10).getOperacionActual());
        assertTrue(almacen.buscarTerminal(10).estaLibre());
    }

    @Test
    void datosBaseIncluyenDosTerminalesLibres() {
        AlmacenLogistico almacen = new AlmacenLogistico(true);

        assertEquals(2, almacen.obtenerTerminales().tamaño());
        assertTrue(almacen.buscarTerminal(1).estaLibre());
        assertTrue(almacen.buscarTerminal(2).estaLibre());
    /*
        ==================================
        ========== Terminales ============
        ==================================
    */

    @Test
    void constructorConCantidadDeTerminalesLasCreaTodasHabilitadasYLibres() {
        AlmacenLogistico almacen = new AlmacenLogistico(3);

        assertEquals(3, almacen.terminalesLibres().tamaño());
        assertEquals(0, almacen.terminalesOcupadas().tamaño());
    }

    @Test
    void constructorSinArgumentosCreaUnaSolaTerminal() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertEquals(1, almacen.terminalesLibres().tamaño());
    }

    @Test
    void asignarYLiberarTerminalCargaSeReflejaEnLasConsultasDeVisibilidad() {
        AlmacenLogistico almacen = new AlmacenLogistico(2);

        TerminalCarga terminal = almacen.asignarTerminalCarga(OperacionCarga.CARGANDO_SUCURSAL);

        assertNotNull(terminal);
        assertEquals(OperacionCarga.CARGANDO_SUCURSAL, terminal.getOperacionActual());
        assertEquals(1, almacen.terminalesLibres().tamaño());
        assertEquals(1, almacen.terminalesOcupadas().tamaño());

        almacen.liberarTerminalCarga(terminal);

        assertEquals(2, almacen.terminalesLibres().tamaño());
        assertEquals(0, almacen.terminalesOcupadas().tamaño());
    }

    @Test
    void asignarTerminalCargaDevuelveNullSinTerminalesDisponibles() {
        AlmacenLogistico almacen = new AlmacenLogistico(0);

        assertNull(almacen.asignarTerminalCarga(OperacionCarga.DESCARGANDO_PROVEEDOR));
    }

    @Test
    void liberarTerminalCargaConNullLanzaExcepcion() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertThrows(IllegalArgumentException.class,
                () -> almacen.liberarTerminalCarga(null));
    }

    @Test
    void descargarSiguienteEntregaProveedorEsperaSiNoHayTerminalLibre() {
        AlmacenLogistico almacen = new AlmacenLogistico(0);
        almacen.registrarProducto(productoConCodigo("P-001"));
        almacen.registrarEntregaProveedor(entregaConProducto("P-001", 5));

        EntregaProveedor entregaDescargada = almacen.descargarSiguienteEntregaProveedor();

        assertNull(entregaDescargada);
        assertEquals(1, almacen.cantidadEntregasPendientes());
        assertEquals(0, almacen.buscarProducto("P-001").getCantidad());
    }

    @Test
    void despacharSiguientePedidoReabastecimientoEsperaSiNoHayTerminalLibre() {
        AlmacenLogistico almacen = new AlmacenLogistico(0);
        almacen.registrarProducto(productoConCodigo("P-001"));
        almacen.aumentarStock("P-001", 10);
        almacen.registrarPedidoReabastecimiento(pedidoReabastecimiento("P-001", 3, 100));

        PedidoSucursal pedidoDespachado = almacen.despacharSiguientePedidoReabastecimiento();

        assertNull(pedidoDespachado);
        assertEquals(1, almacen.cantidadPedidosReabastecimientoPendientes());
        assertEquals(10, almacen.buscarProducto("P-001").getCantidad());
    }

    @Test
    void deshabilitarUnaTerminalImpideQueSeLeAsigneAunEstandoLibre() {
        AlmacenLogistico almacen = new AlmacenLogistico(1);
        int id = almacen.terminalesLibres().obtener(0).getId();

        almacen.deshabilitarTerminal(id);
        assertNull(almacen.asignarTerminalCarga(OperacionCarga.CARGANDO_SUCURSAL));

        almacen.habilitarTerminal(id);
        assertNotNull(almacen.asignarTerminalCarga(OperacionCarga.CARGANDO_SUCURSAL));
    }

    @Test
    void habilitarYDeshabilitarTerminalConIdInexistenteLanzanExcepcion() {
        AlmacenLogistico almacen = new AlmacenLogistico();

        assertThrows(IllegalArgumentException.class,
                () -> almacen.deshabilitarTerminal(999));
        assertThrows(IllegalArgumentException.class,
                () -> almacen.habilitarTerminal(999));
    }

    private AlmacenLogistico almacenConProducto(String codigo) {
        AlmacenLogistico almacen = new AlmacenLogistico();
        almacen.registrarProducto(productoConCodigo(codigo));
        return almacen;
    }

    private AlmacenLogistico almacenConProductos(String... codigos) {
        AlmacenLogistico almacen = new AlmacenLogistico();

        for (String codigo : codigos) {
            almacen.registrarProducto(productoConCodigo(codigo));
        }

        return almacen;
    }

    private Producto productoConCodigo(String codigo) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre("Producto de prueba");
        return producto;
    }

    private EntregaProveedor entregaConProducto(String codigoProducto, int cantidad) {
        return entregaConProductos(detalleConProducto(codigoProducto, cantidad));
    }

    private EntregaProveedor entregaConProductos(DetalleProducto... detalles) {
        ListaArray<DetalleProducto> productos = productosConDetalles(detalles);

        Proveedor proveedor = new Proveedor();
        proveedor.setCodigo("PROV-001");
        proveedor.setNombre("Proveedor de prueba");

        EntregaProveedor entrega = new EntregaProveedor();
        entrega.setProveedor(proveedor);
        entrega.setProductos(productos);
        entrega.setFecha(LocalDateTime.now());
        return entrega;
    }

    private ListaArray<DetalleProducto> productosConDetalles(DetalleProducto... detalles) {
        ListaArray<DetalleProducto> productos = new ListaArray<>();

        for (DetalleProducto detalle : detalles) {
            productos.agregar(detalle);
        }

        return productos;
    }

    private DetalleProducto detalleConProducto(String codigoProducto, int cantidad) {
        DetalleProducto detalle = new DetalleProducto();
        detalle.setProducto(productoConCodigo(codigoProducto));
        detalle.setCantidad(cantidad);
        return detalle;
    }

    private Sucursal sucursalDePrueba() {
        return sucursalConClientes(100);
    }

    private PedidoSucursal pedidoReabastecimiento(String codigoProducto, int cantidad,
            int clientesPromedio) {
        return pedidoReabastecimiento(clientesPromedio,
                detalleConProducto(codigoProducto, cantidad));
    }

    private PedidoSucursal pedidoReabastecimiento(int clientesPromedio,
            DetalleProducto... detalles) {
        PedidoSucursal pedido = new PedidoSucursal();
        pedido.setProductos(productosConDetalles(detalles));
        pedido.setSucursal(sucursalConClientes(clientesPromedio));
        pedido.setFecha(LocalDateTime.now());
        return pedido;
    }

    private Sucursal sucursalConClientes(int clientesPromedio) {
        Sucursal sucursal = new Sucursal();
        sucursal.setCodigo("SUC-001");
        sucursal.setNombre("Sucursal de prueba");
        sucursal.setClientesPromedio(clientesPromedio);
        return sucursal;
    }
}
