package ucu.edu.almacen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TerminalCargaTest {

    @Test
    void terminalNuevaQuedaHabilitadaYLibreConElIdRecibido() {
        TerminalCarga terminal = new TerminalCarga(5);

        assertEquals(5, terminal.getId());
        assertTrue(terminal.isHabilitada());
        assertEquals(OperacionCarga.LIBRE, terminal.getOperacionActual());
        assertTrue(terminal.estaLibre());
    }

    @Test
    void estaLibreEsFalsoMientrasHayUnaOperacionEnCurso() {
        TerminalCarga terminal = new TerminalCarga(1);

        terminal.setOperacionActual(OperacionCarga.DESCARGANDO_PROVEEDOR);
        assertFalse(terminal.estaLibre());

        terminal.setOperacionActual(OperacionCarga.LIBRE);
        assertTrue(terminal.estaLibre());
    }

    @Test
    void estaLibreEsFalsoSiEstaDeshabilitadaAunSinOperacionEnCurso() {
        TerminalCarga terminal = new TerminalCarga(1);

        terminal.setHabilitada(false);

        assertFalse(terminal.estaLibre());
        assertEquals(OperacionCarga.LIBRE, terminal.getOperacionActual());
    }

    @Test
    void rehabilitarUnaTerminalDeshabilitadaLaVuelveALibre() {
        TerminalCarga terminal = new TerminalCarga(1);
        terminal.setHabilitada(false);

        terminal.setHabilitada(true);

        assertTrue(terminal.estaLibre());
    }
}
