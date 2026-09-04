package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import org.junit.jupiter.api.Test;

import ucu.edu.aed.tda.TDALista;
import ucu.edu.implementaciones.ListaArray;

class ListaArrayTest {

    @Test
    void listaNuevaEstaVacia() {
        ListaArray<String> lista = new ListaArray<>();

        assertTrue(lista.esVacio());
        assertEquals(0, lista.tamaño());
    }

    @Test
    void constructorRechazaCapacidadNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new ListaArray<Integer>(-1));
    }

    @Test
    void agregarAlFinalConservaElOrdenYRedimensiona() {
        ListaArray<Integer> lista = new ListaArray<>(0);

        lista.agregar(10);
        lista.agregar(20);
        lista.agregar(30);

        assertEquals(3, lista.tamaño());
        assertArrayEquals(new Integer[] {10, 20, 30}, elementosDe(lista));
    }

    @Test
    void agregarEnIndiceDesplazaLosElementosPosteriores() {
        ListaArray<String> lista = listaDe("A", "C"); 

        lista.agregar(1, "B");
        lista.agregar(3, "D");

        assertArrayEquals(new String[] {"A", "B", "C", "D"}, elementosDe(lista));
    }

    @Test
    void agregarEnIndiceInvalidoLanzaExcepcion() {
        ListaArray<Integer> lista = listaDe(1);

        assertThrows(IndexOutOfBoundsException.class, () -> lista.agregar(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.agregar(2, 2));
    }

    @Test
    void obtenerEnIndiceInvalidoLanzaExcepcion() {
        ListaArray<Integer> lista = listaDe(1);

        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(1));
    }

    @Test
    void removerPorIndiceDevuelveElementoYCompactaLaLista() {
        ListaArray<Integer> lista = listaDe(1, 2, 3, 4);

        assertEquals(2, lista.remover(1));
        assertEquals(4, lista.remover(2));
        assertArrayEquals(new Integer[] {1, 3}, elementosDe(lista));
    }

    @Test
    void removerPorElementoEliminaSoloLaPrimeraOcurrencia() {
        ListaArray<String> lista = listaDe("A", "B", "A");

        assertTrue(lista.remover("A"));
        assertFalse(lista.remover("Z"));
        assertArrayEquals(new String[] {"B", "A"}, elementosDe(lista));
    }

    @Test
    void contieneEIndiceDeSoportanValoresNulos() {
        ListaArray<String> lista = listaDe("uno", null, "dos", null);

        assertTrue(lista.contiene(null));
        assertEquals(1, lista.indiceDe(null));
        assertTrue(lista.remover(null));
        assertEquals(2, lista.indiceDe(null));
    }

    @Test
    void buscarRetornaLaPrimeraCoincidenciaONull() {
        ListaArray<Integer> lista = listaDe(3, 4, 6, 8);

        assertEquals(4, lista.buscar(numero -> numero % 2 == 0));
        assertNull(lista.buscar(numero -> numero > 10));
        assertThrows(NullPointerException.class, () -> lista.buscar(null));
    }

    @Test
    void ordenarDevuelveUnaNuevaListaOrdenadaSinModificarLaOriginal() {
        ListaArray<Integer> original = listaDe(3, 1, 2);

        TDALista<Integer> ordenada = original.ordenar(Comparator.naturalOrder());

        assertArrayEquals(new Integer[] {1, 2, 3}, elementosDe(ordenada));
        assertArrayEquals(new Integer[] {3, 1, 2}, elementosDe(original));
        assertThrows(NullPointerException.class, () -> original.ordenar(null));
    }

    @Test
    void vaciarEliminaTodosLosElementosYPermiteReutilizarLaLista() {
        ListaArray<Integer> lista = listaDe(1, 2);

        lista.vaciar();

        assertTrue(lista.esVacio());
        assertEquals(0, lista.tamaño());
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(0));

        lista.agregar(3);
        assertEquals(3, lista.obtener(0));
    }

    @SafeVarargs
    private static <T> ListaArray<T> listaDe(T... elementos) {
        ListaArray<T> lista = new ListaArray<>();
        for (T elemento : elementos) {
            lista.agregar(elemento);
        }
        return lista;
    }

    private static <T> Object[] elementosDe(TDALista<T> lista) {
        Object[] elementos = new Object[lista.tamaño()];
        for (int i = 0; i < lista.tamaño(); i++) {
            elementos[i] = lista.obtener(i);
        }
        return elementos;
    }
}
