package ucu.edu.almacen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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

    private AlmacenLogistico almacenConProducto(String codigo) {
        AlmacenLogistico almacen = new AlmacenLogistico();
        almacen.registrarProducto(productoConCodigo(codigo));
        return almacen;
    }

    private Producto productoConCodigo(String codigo) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre("Producto de prueba");
        return producto;
    }
}
