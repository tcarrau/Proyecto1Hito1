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
        ListaArray<DetalleProducto> productos = new ListaArray<>();

        for (DetalleProducto detalle : detalles) {
            productos.agregar(detalle);
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setCodigo("PROV-001");
        proveedor.setNombre("Proveedor de prueba");

        EntregaProveedor entrega = new EntregaProveedor();
        entrega.setProveedor(proveedor);
        entrega.setProductos(productos);
        entrega.setFecha(LocalDateTime.now());
        return entrega;
    }

    private DetalleProducto detalleConProducto(String codigoProducto, int cantidad) {
        DetalleProducto detalle = new DetalleProducto();
        detalle.setProducto(productoConCodigo(codigoProducto));
        detalle.setCantidad(cantidad);
        return detalle;
    }
}
