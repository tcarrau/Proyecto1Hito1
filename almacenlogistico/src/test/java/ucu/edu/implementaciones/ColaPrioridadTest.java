package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

import ucu.edu.aed.tda.TDACola;
import ucu.edu.aed.tda.TDALista;

class ColaPrioridadTest {

    @Test
    void colaNuevaEstaVacia() {
        ColaPrioridad<Integer> cola = new ColaPrioridad<>(Comparator.naturalOrder());

        assertTrue(cola.esVacio());
        assertEquals(0, cola.tamaño());
    }

    @Test
    void constructorRechazaComparadorNulo() {
        assertThrows(NullPointerException.class, () -> new ColaPrioridad<Integer>(null));
    }

    @Test
    void colaVaciaLanzaExcepcionAlConsultarOQuitarElFrente() {
        ColaPrioridad<Integer> cola = new ColaPrioridad<>(Comparator.naturalOrder());

        assertThrows(NoSuchElementException.class, () -> cola.frente());
        assertThrows(NoSuchElementException.class, () -> cola.quitaDeCola());
    }

    @Test
    void saleElMenorSegunElComparadorSinImportarElOrdenDeLlegada() {
        ColaPrioridad<Integer> cola = colaDe(Comparator.naturalOrder(), 5, 1, 9, 3);

        assertEquals(1, cola.quitaDeCola());
        assertEquals(3, cola.quitaDeCola());
        assertEquals(5, cola.quitaDeCola());
        assertEquals(9, cola.quitaDeCola());
        assertTrue(cola.esVacio());
    }

    @Test
    void conComparadorInvertidoSaleElMayor() {
        ColaPrioridad<Integer> cola = colaDe(Comparator.<Integer>naturalOrder().reversed(),
                5, 1, 9, 3);

        assertEquals(9, cola.quitaDeCola());
        assertEquals(5, cola.quitaDeCola());
        assertEquals(3, cola.quitaDeCola());
        assertEquals(1, cola.quitaDeCola());
    }

    @Test
    void losElementosQuedanOrdenadosPorPrioridadAlInsertar() {
        ColaPrioridad<Integer> cola = colaDe(Comparator.naturalOrder(), 5, 1, 9, 3);

        assertArrayEquals(new Integer[] {1, 3, 5, 9}, elementosDe(cola));
    }

    @Test
    void insertarElDeMayorPrioridadAlFinalIgualLoDejaAdelante() {
        ColaPrioridad<Integer> cola = colaDe(Comparator.naturalOrder(), 5, 7);

        cola.poneEnCola(1);

        assertEquals(1, cola.frente());
        assertArrayEquals(new Integer[] {1, 5, 7}, elementosDe(cola));
    }

    @Test
    void entreElementosDeIgualPrioridadSeRespetaElOrdenDeLlegada() {
        // Prioridad por longitud: las tres palabras de 3 letras empatan.
        ColaPrioridad<String> cola = colaDe(Comparator.comparingInt(String::length),
                "uno", "cuatro", "dos", "tres", "no");

        assertEquals("no", cola.quitaDeCola());
        assertEquals("uno", cola.quitaDeCola());
        assertEquals("dos", cola.quitaDeCola());
        assertEquals("tres", cola.quitaDeCola());
        assertEquals("cuatro", cola.quitaDeCola());
    }

    @Test
    void frenteDevuelveElDeMayorPrioridadSinRemoverlo() {
        ColaPrioridad<Integer> cola = colaDe(Comparator.naturalOrder(), 4, 2, 8);

        assertEquals(2, cola.frente());
        assertEquals(2, cola.frente());
        assertEquals(3, cola.tamaño());
    }

    @Test
    void poneEnColaSiempreAceptaElElemento() {
        ColaPrioridad<Integer> cola = new ColaPrioridad<>(Comparator.naturalOrder());

        assertTrue(cola.poneEnCola(1));
        assertTrue(cola.poneEnCola(2));
        assertEquals(2, cola.tamaño());
    }

    @Test
    void agregarEsEquivalenteAPoneEnCola() {
        ColaPrioridad<Integer> cola = new ColaPrioridad<>(Comparator.naturalOrder());

        cola.agregar(5);
        cola.agregar(1);

        assertEquals(1, cola.frente());
        assertArrayEquals(new Integer[] {1, 5}, elementosDe(cola));
    }

    @Test
    void agregarEnUnaPosicionElegidaNoEstaSoportado() {
        ColaPrioridad<Integer> cola = colaDe(Comparator.naturalOrder(), 5);

        assertThrows(UnsupportedOperationException.class, () -> cola.agregar(0, 1));
    }

    @Test
    void esIntercambiableConLaColaComunPorCompartirElTda() {
        TDACola<Integer> cola = new ColaPrioridad<>(Comparator.naturalOrder());

        cola.poneEnCola(3);
        cola.poneEnCola(1);

        assertEquals(1, cola.frente());
        assertEquals(1, cola.quitaDeCola());
    }

    @Test
    void contieneIndiceDeYRemoverFuncionanSobreLaCola() {
        ColaPrioridad<Integer> cola = colaDe(Comparator.naturalOrder(), 5, 1, 9);

        assertTrue(cola.contiene(5));
        assertEquals(1, cola.indiceDe(5));
        assertTrue(cola.remover(Integer.valueOf(5)));
        assertFalse(cola.remover(Integer.valueOf(5)));
        assertArrayEquals(new Integer[] {1, 9}, elementosDe(cola));
    }

    @Test
    void ordenarDevuelveUnaNuevaListaSinModificarLaCola() {
        ColaPrioridad<Integer> cola = colaDe(Comparator.naturalOrder(), 5, 1, 9);

        TDALista<Integer> ordenada = cola.ordenar(Comparator.<Integer>naturalOrder().reversed());

        assertArrayEquals(new Integer[] {9, 5, 1}, elementosDe(ordenada));
        assertArrayEquals(new Integer[] {1, 5, 9}, elementosDe(cola));
    }

    @Test
    void vaciarEliminaTodosLosElementosYPermiteReutilizarLaCola() {
        ColaPrioridad<Integer> cola = colaDe(Comparator.naturalOrder(), 1, 2);

        cola.vaciar();

        assertTrue(cola.esVacio());
        assertThrows(NoSuchElementException.class, () -> cola.frente());

        cola.poneEnCola(7);
        assertEquals(7, cola.frente());
    }

    @Test
    void unUnicoElementoEntraYSale() {
        ColaPrioridad<String> cola = new ColaPrioridad<>(Comparator.naturalOrder());

        cola.poneEnCola("solo");

        assertEquals("solo", cola.frente());
        assertEquals("solo", cola.quitaDeCola());
        assertTrue(cola.esVacio());
    }

    @Test
    void muchasInsercionesSalenSiempreOrdenadas() {
        ColaPrioridad<Integer> cola = new ColaPrioridad<>(Comparator.naturalOrder());

        // Se insertan desordenados a proposito.
        for (int i = 0; i < 200; i++) {
            cola.poneEnCola((i * 97) % 200);
        }
        assertEquals(200, cola.tamaño());

        for (int i = 0; i < 200; i++) {
            assertEquals(i, cola.quitaDeCola());
        }

        assertTrue(cola.esVacio());
    }

    @SafeVarargs
    private static <T> ColaPrioridad<T> colaDe(Comparator<T> comparador, T... elementos) {
        ColaPrioridad<T> cola = new ColaPrioridad<>(comparador);
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
