package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

import ucu.edu.aed.tda.TDALista;
import ucu.edu.implementaciones.Cola;

class ColaTest {

    @Test
    void colaNuevaEstaVacia() {
        Cola<String> cola = new Cola<>(5);

        assertTrue(cola.esVacio());
        assertTrue(cola.esVacia());
        assertEquals(0, cola.tamaño());
    }

    @Test
    void constructorRechazaCapacidadNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new Cola<Integer>(-1));
    }

    @Test
    void colaVaciaLanzaExcepcionAlConsultarOQuitarElFrente() {
        Cola<Integer> cola = new Cola<>(3);

        assertThrows(NoSuchElementException.class, () -> cola.frente());
        assertThrows(NoSuchElementException.class, () -> cola.quitaDeCola());
    }

    @Test
    void losElementosSalenEnElOrdenEnQueEntraron() {
        Cola<String> cola = colaDe(5, "A", "B", "C");

        assertEquals("A", cola.quitaDeCola());
        assertEquals("B", cola.quitaDeCola());
        assertEquals("C", cola.quitaDeCola());
        assertTrue(cola.esVacio());
    }

    @Test
    void frenteDevuelveElPrimeroSinRemoverlo() {
        Cola<String> cola = colaDe(5, "A", "B");

        assertEquals("A", cola.frente());
        assertEquals("A", cola.frente());
        assertEquals(2, cola.tamaño());
    }

    @Test
    void unUnicoElementoEntraYSale() {
        Cola<String> cola = new Cola<>(3);

        assertTrue(cola.poneEnCola("solo"));

        assertEquals("solo", cola.frente());
        assertEquals("solo", cola.quitaDeCola());
        assertTrue(cola.esVacio());
    }

    @Test
    void poneEnColaAvisaConFalseCuandoLaColaEstaLlena() {
        Cola<String> cola = colaDe(2, "A", "B");

        assertFalse(cola.poneEnCola("C"));
        assertEquals(2, cola.tamaño());
        assertArrayEquals(new String[] {"A", "B"}, elementosDe(cola));
    }

    @Test
    void agregarLanzaExcepcionCuandoLaColaEstaLlenaEnVezDeDescartarEnSilencio() {
        Cola<String> cola = colaDe(2, "A", "B");

        assertThrows(IllegalStateException.class, () -> cola.agregar("C"));
        assertEquals(2, cola.tamaño());
    }

    @Test
    void agregarEnUnaPosicionLanzaExcepcionCuandoLaColaEstaLlena() {
        Cola<String> cola = colaDe(2, "A", "B");

        assertThrows(IllegalStateException.class, () -> cola.agregar(0, "C"));
        assertEquals(2, cola.tamaño());
    }

    @Test
    void agregarEsEquivalenteAPoneEnColaMientrasHayaLugar() {
        Cola<String> cola = new Cola<>(3);

        cola.agregar("A");
        cola.agregar("B");

        assertArrayEquals(new String[] {"A", "B"}, elementosDe(cola));
        assertEquals("A", cola.frente());
    }

    @Test
    void agregarEnIndiceDesplazaLosElementosPosteriores() {
        Cola<String> cola = colaDe(5, "A", "C");

        cola.agregar(1, "B");
        cola.agregar(3, "D");

        assertArrayEquals(new String[] {"A", "B", "C", "D"}, elementosDe(cola));
        assertEquals("A", cola.frente());
    }

    @Test
    void agregarEnIndiceInvalidoLanzaExcepcion() {
        Cola<Integer> cola = colaDe(5, 1);

        assertThrows(IndexOutOfBoundsException.class, () -> cola.agregar(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> cola.agregar(2, 2));
    }

    @Test
    void obtenerEnIndiceInvalidoLanzaExcepcion() {
        Cola<Integer> cola = colaDe(5, 1);

        assertThrows(IndexOutOfBoundsException.class, () -> cola.obtener(-1));
        // El indice igual al tamaño no existe, aunque quede lugar en el arreglo.
        assertThrows(IndexOutOfBoundsException.class, () -> cola.obtener(1));
        assertThrows(IndexOutOfBoundsException.class, () -> cola.obtener(4));
    }

    @Test
    void obtenerDevuelveLosElementosEnOrdenDeLlegada() {
        Cola<String> cola = colaDe(5, "A", "B", "C");

        assertEquals("A", cola.obtener(0));
        assertEquals("B", cola.obtener(1));
        assertEquals("C", cola.obtener(2));
    }

    @Test
    void removerPorIndiceDevuelveElementoYCompactaLaCola() {
        Cola<Integer> cola = colaDe(5, 1, 2, 3, 4);

        assertEquals(2, cola.remover(1));
        assertEquals(4, cola.remover(2));
        assertArrayEquals(new Integer[] {1, 3}, elementosDe(cola));
    }

    @Test
    void removerEnIndiceInvalidoLanzaExcepcion() {
        Cola<Integer> cola = colaDe(5, 1);

        assertThrows(IndexOutOfBoundsException.class, () -> cola.remover(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> cola.remover(1));
    }

    @Test
    void removerPorElementoEliminaSoloLaPrimeraOcurrencia() {
        Cola<String> cola = colaDe(5, "A", "B", "A");

        assertTrue(cola.remover("A"));
        assertFalse(cola.remover("Z"));
        assertArrayEquals(new String[] {"B", "A"}, elementosDe(cola));
    }

    @Test
    void contieneEIndiceDeSoportanValoresNulos() {
        Cola<String> cola = colaDe(5, "uno", null, "dos", null);

        assertTrue(cola.contiene(null));
        assertEquals(1, cola.indiceDe(null));
        assertTrue(cola.remover(null));
        assertEquals(2, cola.indiceDe(null));
    }

    @Test
    void buscarNoRompeConElementosNulosEnLaCola() {
        Cola<String> cola = colaDe(5, null, "dato");

        assertEquals("dato", cola.buscar(valor -> "dato".equals(valor)));
        assertNull(cola.buscar(valor -> "otro".equals(valor)));
    }

    @Test
    void buscarRetornaLaPrimeraCoincidenciaOnullYExigeCriterioNoNulo() {
        Cola<Integer> cola = colaDe(5, 3, 4, 6, 8);

        assertEquals(4, cola.buscar(numero -> numero % 2 == 0));
        assertNull(cola.buscar(numero -> numero > 10));
        assertThrows(NullPointerException.class, () -> cola.buscar(null));
    }

    @Test
    void ordenarDevuelveUnaNuevaListaOrdenadaSinModificarLaOriginalYExigeComparadorNoNulo() {
        Cola<Integer> original = colaDe(5, 3, 1, 2);

        TDALista<Integer> ordenada = original.ordenar(Comparator.naturalOrder());

        assertArrayEquals(new Integer[] {1, 2, 3}, elementosDe(ordenada));
        assertArrayEquals(new Integer[] {3, 1, 2}, elementosDe(original));
        assertThrows(NullPointerException.class, () -> original.ordenar(null));
    }

    @Test
    void ordenarSobreUnaColaVaciaExigeComparadorIgual() {
        Cola<Integer> vacia = new Cola<>(5);

        assertEquals(0, vacia.ordenar(Comparator.naturalOrder()).tamaño());
        assertThrows(NullPointerException.class, () -> vacia.ordenar(null));
    }

    @Test
    void vaciarEliminaTodosLosElementosYPermiteReutilizarLaCola() {
        Cola<Integer> cola = colaDe(5, 1, 2);

        cola.vaciar();

        assertTrue(cola.esVacio());
        assertEquals(0, cola.tamaño());
        assertThrows(NoSuchElementException.class, () -> cola.frente());

        cola.poneEnCola(3);
        assertEquals(3, cola.frente());
    }

    @Test
    void unaColaLlenaSeVaciaYVuelveAAceptarElementos() {
        Cola<String> cola = colaDe(3, "A", "B", "C");

        assertFalse(cola.poneEnCola("D"));

        assertEquals("A", cola.quitaDeCola());
        assertTrue(cola.poneEnCola("D"));

        assertArrayEquals(new String[] {"B", "C", "D"}, elementosDe(cola));
        assertFalse(cola.poneEnCola("E"));
    }

    @Test
    void muchasEntradasYSalidasAlternadasMantienenElOrdenFifo() {
        Cola<Integer> cola = new Cola<>(10);

        for (int i = 0; i < 10; i++) {
            assertTrue(cola.poneEnCola(i));
        }

        for (int i = 0; i < 100; i++) {
            assertEquals(i, cola.quitaDeCola());
            assertTrue(cola.poneEnCola(i + 10));
        }

        assertEquals(10, cola.tamaño());
        assertEquals(100, cola.frente());
    }

    @SafeVarargs
    private static <T> Cola<T> colaDe(int capacidad, T... elementos) {
        Cola<T> cola = new Cola<>(capacidad);
        for (T elemento : elementos) {
            cola.poneEnCola(elemento);
        }
        return cola;
    }

    private static <T> Object[] elementosDe(TDALista<T> lista) {
        Object[] elementos = new Object[lista.tamaño()];
        for (int i = 0; i < lista.tamaño(); i++) {
            elementos[i] = lista.obtener(i);
        }
        return elementos;
    }
}
