package ucu.edu.almacen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ucu.edu.aed.tda.TDANodoNario;
import ucu.edu.implementaciones.NodoNario;

class ReorganizacionDepositoTest {

    @Test
    void mueveUnSectorConTodoSuSubarbol() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        Deposito deposito = almacen.getDeposito();

        Sector raiz = sector("DEP", TipoSector.DEPOSITO, 0);
        Sector zonaA = sector("ZA", TipoSector.ZONA, 0);
        Sector zonaB = sector("ZB", TipoSector.ZONA, 0);
        Sector posicionA = sector("PA", TipoSector.POSICION, 10);

        TDANodoNario<Sector> nodoRaiz = deposito.getSectores().agregarHijo(null, raiz);
        TDANodoNario<Sector> nodoZonaA = deposito.getSectores().agregarHijo(nodoRaiz, zonaA);
        TDANodoNario<Sector> nodoZonaB = deposito.getSectores().agregarHijo(nodoRaiz, zonaB);
        deposito.getSectores().agregarHijo(nodoZonaA, posicionA);

        assertTrue(almacen.moverSector("ZA", "ZB"));
        assertEquals(zonaB, ((NodoNario<Sector>) nodoZonaA).getPadre().getDato());
        assertEquals(2, deposito.obtenerSectoresDelSubarbol("ZA").tamaño());
    }

    @Test
    void inhabilitarSectorReubicaTodoElStockEnUnaPosicionDisponible() {
        AlmacenLogistico almacen = new AlmacenLogistico();
        Deposito deposito = almacen.getDeposito();

        Sector raiz = sector("DEP", TipoSector.DEPOSITO, 0);
        Sector zonaOrigen = sector("ZO", TipoSector.ZONA, 0);
        Sector zonaDestino = sector("ZD", TipoSector.ZONA, 0);
        Sector posicionOrigen = sector("PO", TipoSector.POSICION, 10);
        Sector posicionDestino = sector("PD", TipoSector.POSICION, 10);

        TDANodoNario<Sector> nodoRaiz = deposito.getSectores().agregarHijo(null, raiz);
        TDANodoNario<Sector> nodoOrigen = deposito.getSectores().agregarHijo(nodoRaiz, zonaOrigen);
        TDANodoNario<Sector> nodoDestino = deposito.getSectores().agregarHijo(nodoRaiz, zonaDestino);
        deposito.getSectores().agregarHijo(nodoOrigen, posicionOrigen);
        deposito.getSectores().agregarHijo(nodoDestino, posicionDestino);

        Producto producto = new Producto("P-001", "Producto");
        almacen.registrarProducto(producto, new StockUbicado(posicionOrigen, 6));

        assertTrue(almacen.inhabilitarSector("ZO"));

        RegistroInventario registro = almacen.buscarProductoInventario("P-001");
        assertFalse(zonaOrigen.isHabilitado());
        assertNull(registro.getStockUbicado(posicionOrigen));
        assertEquals(6, registro.getStockUbicado(posicionDestino).getCantidad());
    }

    private Sector sector(String codigo, TipoSector tipo, int capacidad) {
        return new Sector(codigo, codigo, tipo, capacidad);
    }
}
