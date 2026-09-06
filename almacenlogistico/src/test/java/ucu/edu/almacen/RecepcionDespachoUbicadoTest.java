package ucu.edu.almacen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ucu.edu.aed.tda.TDANodoNario;
import ucu.edu.implementaciones.ListaArray;

class RecepcionDespachoUbicadoTest {

    @Test
    void recibeEnPosicionesYDespachaSiguiendoElOrdenDelArbol() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        Inventario inventario = almacen.getInventario();
        Sector posicionA = sector("PA", TipoSector.POSICION, 10);
        Sector posicionB = sector("PB", TipoSector.POSICION, 10);
        cargarPosiciones(almacen, posicionA, posicionB);

        Producto producto = new Producto("P-001", "Producto");
        EntregaProveedor entrega = entregaUbicada(producto, posicionA, posicionB);
        almacen.registrarEntregaProveedor(entrega);

        assertSame(entrega, almacen.descargarSiguienteEntregaProveedorUbicada());
        assertEquals(6, inventario.obtenerStockTotal("P-001"));

        PedidoSucursal pedido = new PedidoSucursal();
        pedido.setProductos(listaDetalles(new DetalleProducto()));
        pedido.getProductos().obtener(0).setProducto(producto);
        pedido.getProductos().obtener(0).setCantidad(4);

        ListaArray<PasoRecoleccion> pasos = inventario.prepararPedidoUbicado(
                almacen.getDeposito(), pedido);

        assertEquals(2, pasos.tamaño());
        assertSame(posicionA, pasos.obtener(0).getStockOrigen().getPosicion());
        assertEquals(2, pasos.obtener(0).getCantidad());
        assertSame(posicionB, pasos.obtener(1).getStockOrigen().getPosicion());
        assertEquals(2, pasos.obtener(1).getCantidad());

        assertTrue(inventario.despacharPedidoUbicado(almacen.getDeposito(), pedido));
        assertEquals(2, inventario.obtenerStockTotal("P-001"));
    }

    private void cargarPosiciones(AlmacenLogistico almacen, Sector posicionA, Sector posicionB) {
        Sector raiz = sector("DEP", TipoSector.DEPOSITO, 0);
        TDANodoNario<Sector> nodoRaiz = almacen.getDeposito().getSectores()
                .agregarHijo(null, raiz);
        almacen.getDeposito().getSectores().agregarHijo(nodoRaiz, posicionA);
        almacen.getDeposito().getSectores().agregarHijo(nodoRaiz, posicionB);
    }

    private EntregaProveedor entregaUbicada(Producto producto, Sector posicionA, Sector posicionB) {
        RegistroInventario recibido = new RegistroInventario(producto);
        recibido.getUbicaciones().agregar(new StockUbicado(posicionA, 2));
        recibido.getUbicaciones().agregar(new StockUbicado(posicionB, 4));

        EntregaProveedor entrega = new EntregaProveedor();
        entrega.setProveedor(new Proveedor());
        entrega.setFecha(LocalDateTime.now());
        entrega.setProductos(listaDetalles(new DetalleProducto()));
        entrega.setMercaderiaUbicada(listaRegistros(recibido));
        return entrega;
    }

    private ListaArray<DetalleProducto> listaDetalles(DetalleProducto detalle) {
        ListaArray<DetalleProducto> detalles = new ListaArray<>();
        detalles.agregar(detalle);
        return detalles;
    }

    private ListaArray<RegistroInventario> listaRegistros(RegistroInventario registro) {
        ListaArray<RegistroInventario> registros = new ListaArray<>();
        registros.agregar(registro);
        return registros;
    }

    private Sector sector(String codigo, TipoSector tipo, int capacidad) {
        return new Sector(codigo, codigo, tipo, capacidad);
    }
}
