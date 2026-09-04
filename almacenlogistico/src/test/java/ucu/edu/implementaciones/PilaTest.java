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

class PilaTest {

    @Test
    void pilaNuevaEstaVacia() {
        Pila<String> pila = new Pila<>();

        assertTrue(pila.esVacio());
        assertEquals(0, pila.tamaño());
    }

    @Test
    void pilaVaciaLanzaExcepcionAlConsultarOQuitarElTope() {
        Pila<Integer> pila = new Pila<>();

        assertThrows(NoSuchElementException.class, () -> pila.tope());
        assertThrows(NoSuchElementException.class, () -> pila.saca());
    }

    @Test
    void sacaDevuelveLosElementosEnOrdenInverso() {
        Pila<String> pila = pilaDe("A", "B", "C");

        assertEquals("C", pila.saca());
        assertEquals("B", pila.saca());
        assertEquals("A", pila.saca());
        assertTrue(pila.esVacio());
    }

    @Test
    void topeDevuelveElUltimoSinRemoverlo() {
        Pila<String> pila = pilaDe("A", "B");

        assertEquals("B", pila.tope());
        assertEquals("B", pila.tope());
        assertEquals(2, pila.tamaño());
    }

    @Test
    void meteSobreUnaPilaVaciaDejaUnUnicoElemento() {
        Pila<String> pila = new Pila<>();

        pila.mete("solo");

        assertFalse(pila.esVacio());
        assertEquals(1, pila.tamaño());
        assertEquals("solo", pila.tope());
    }

    @Test
    void vaciarPorCompletoYVolverAUsarlaFunciona() {
        Pila<Integer> pila = pilaDe(1, 2, 3);

        pila.saca();
        pila.saca();
        pila.saca();
        assertTrue(pila.esVacio());

        pila.mete(9);
        pila.mete(8);

        assertEquals(8, pila.saca());
        assertEquals(9, pila.tope());
    }

    @Test
    void agregarEsEquivalenteAMete() {
        Pila<String> conAgregar = new Pila<>();
        Pila<String> conMete = new Pila<>();

        conAgregar.agregar("A");
        conAgregar.agregar("B");
        conMete.mete("A");
        conMete.mete("B");

        assertArrayEquals(elementosDe(conMete), elementosDe(conAgregar));
        assertEquals(conMete.tope(), conAgregar.tope());
    }

    @Test
    void elIndiceCeroEsLaBaseYElUltimoIndiceEsElTope() {
        Pila<String> pila = pilaDe("base", "medio", "tope");

        assertEquals("base", pila.obtener(0));
        assertEquals("tope", pila.obtener(pila.tamaño() - 1));
        assertEquals(pila.obtener(pila.tamaño() - 1), pila.tope());
    }

    @Test
    void obtenerEnIndiceInvalidoLanzaExcepcion() {
        Pila<Integer> pila = pilaDe(1);

        assertThrows(IndexOutOfBoundsException.class, () -> pila.obtener(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> pila.obtener(1));
    }

    @Test
    void removerPorIndiceYPorElementoFuncionanSobreLaPila() {
        Pila<String> pila = pilaDe("A", "B", "C");

        assertEquals("B", pila.remover(1));
        assertTrue(pila.remover("A"));
        assertFalse(pila.remover("Z"));
        assertArrayEquals(new String[] {"C"}, elementosDe(pila));
        assertEquals("C", pila.tope());
    }

    @Test
    void contieneEIndiceDeSoportanValoresNulos() {
        Pila<String> pila = pilaDe("uno", null, "dos");

        assertTrue(pila.contiene(null));
        assertEquals(1, pila.indiceDe(null));
        assertTrue(pila.remover(null));
        assertFalse(pila.contiene(null));
    }

    @Test
    void buscarRetornaLaPrimeraCoincidenciaOnullYExigeCriterioNoNulo() {
        Pila<Integer> pila = pilaDe(3, 4, 6, 8);

        assertEquals(4, pila.buscar(numero -> numero % 2 == 0));
        assertNull(pila.buscar(numero -> numero > 10));
        assertThrows(NullPointerException.class, () -> pila.buscar(null));
    }

    @Test
    void ordenarDevuelveUnaNuevaListaSinModificarLaPila() {
        Pila<Integer> pila = pilaDe(3, 1, 2);

        TDALista<Integer> ordenada = pila.ordenar(Comparator.naturalOrder());

        assertArrayEquals(new Integer[] {1, 2, 3}, elementosDe(ordenada));
        assertArrayEquals(new Integer[] {3, 1, 2}, elementosDe(pila));
        assertEquals(2, pila.tope());
    }

    @Test
    void vaciarEliminaTodosLosElementosYPermiteReutilizarLaPila() {
        Pila<Integer> pila = pilaDe(1, 2);

        pila.vaciar();

        assertTrue(pila.esVacio());
        assertThrows(NoSuchElementException.class, () -> pila.tope());

        pila.mete(3);
        assertEquals(3, pila.tope());
    }

    @Test
    void muchasOperacionesSeguidasMantienenLaCoherencia() {
        Pila<Integer> pila = new Pila<>();

        for (int i = 0; i < 200; i++) {
            pila.mete(i);
        }
        assertEquals(200, pila.tamaño());
        assertEquals(199, pila.tope());

        for (int i = 199; i >= 0; i--) {
            assertEquals(i, pila.saca());
        }

        assertTrue(pila.esVacio());
    }

    @SafeVarargs
    private static <T> Pila<T> pilaDe(T... elementos) {
        Pila<T> pila = new Pila<>();
        for (T elemento : elementos) {
            pila.mete(elemento);
        }
        return pila;
    }

    private static <T> Object[] elementosDe(TDALista<T> lista) {
        Object[] elementos = new Object[lista.tamaño()];
        for (int i = 0; i < lista.tamaño(); i++) {
            elementos[i] = lista.obtener(i);
        }
        return elementos;
    }
}
