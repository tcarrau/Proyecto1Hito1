package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import ucu.edu.aed.tda.TDALista;

class ColaPrioridadDobleEnlazadaTest {

    @Test
    void mantieneLosElementosOrdenadosYLosQuitaDesdeElFrente() {
        ColaPrioridadDobleEnlazada<Integer> cola = new ColaPrioridadDobleEnlazada<>(
                Comparator.naturalOrder());

        cola.poneEnCola(5);
        cola.poneEnCola(1);
        cola.poneEnCola(9);
        cola.poneEnCola(3);

        assertArrayEquals(new Object[] {1, 3, 5, 9}, elementosDe(cola));
        assertEquals(1, cola.quitaDeCola());
        assertEquals(3, cola.quitaDeCola());
    }

    @Test
    void respetaElOrdenDeLlegadaCuandoLaPrioridadEmpata() {
        ColaPrioridadDobleEnlazada<String> cola = new ColaPrioridadDobleEnlazada<>(
                Comparator.comparingInt(String::length));

        cola.poneEnCola("uno");
        cola.poneEnCola("dos");
        cola.poneEnCola("sol");

        assertEquals("uno", cola.quitaDeCola());
        assertEquals("dos", cola.quitaDeCola());
        assertEquals("sol", cola.quitaDeCola());
    }

    @Test
    void colaVaciaNoTieneFrenteNiElementoParaQuitar() {
        ColaPrioridadDobleEnlazada<Integer> cola = new ColaPrioridadDobleEnlazada<>(
                Comparator.naturalOrder());

        assertThrows(NoSuchElementException.class, cola::frente);
        assertThrows(NoSuchElementException.class, cola::quitaDeCola);
    }

    private static <T> Object[] elementosDe(TDALista<T> lista) {
        Object[] elementos = new Object[lista.tamaño()];
        for (int i = 0; i < lista.tamaño(); i++) {
            elementos[i] = lista.obtener(i);
        }
        return elementos;
    }
}
