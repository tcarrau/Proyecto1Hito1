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
import ucu.edu.Implementaciones.ListaCircularDoble;

class ListaCircularDobleTest {

    @Test
    void listaNuevaEstaVacia() {
        ListaCircularDoble<String> lista = new ListaCircularDoble<>();

        assertTrue(lista.esVacio());
        assertEquals(0, lista.tamaño());
    }

    @Test
    void listaVaciaLanzaExcepcionesEnOperacionesDeAcceso() {
        ListaCircularDoble<Integer> lista = new ListaCircularDoble<>();

        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(0));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.remover(0));
        assertFalse(lista.remover((Integer) 1));
        assertFalse(lista.contiene(1));
        assertEquals(-1, lista.indiceDe(1));
        assertNull(lista.buscar(numero -> true));
    }

    @Test
    void unUnicoElementoQuedaCircularConsigoMismoEnAmbosSentidos() {
        ListaCircularDoble<String> lista = new ListaCircularDoble<>();

        lista.agregar("A");

        assertEquals(1, lista.tamaño());
        assertEquals("A", lista.obtener(0));
        assertEquals("A", lista.buscar(valor -> valor.equals("A")));
    }

    @Test
    void removerElUnicoElementoDejaLaListaVaciaYReutilizable() {
        ListaCircularDoble<Integer> lista = listaDe(42);

        assertEquals(42, lista.remover(0));

        assertTrue(lista.esVacio());
        assertEquals(0, lista.tamaño());

        lista.agregar(7);
        assertEquals(7, lista.obtener(0));
    }

    @Test
    void agregarAlFinalConservaElOrden() {
        ListaCircularDoble<Integer> lista = new ListaCircularDoble<>();

        lista.agregar(10);
        lista.agregar(20);
        lista.agregar(30);

        assertEquals(3, lista.tamaño());
        assertArrayEquals(new Integer[] {10, 20, 30}, elementosDe(lista));
    }

    @Test
    void agregarEnIndiceCeroActualizaElPrimero() {
        ListaCircularDoble<String> lista = listaDe("B", "C");

        lista.agregar(0, "A");

        assertArrayEquals(new String[] {"A", "B", "C"}, elementosDe(lista));
        assertEquals("A", lista.obtener(0));
    }

    @Test
    void agregarEnIndiceIntermedioDesplazaLosElementosPosteriores() {
        ListaCircularDoble<String> lista = listaDe("A", "C");

        lista.agregar(1, "B");
        lista.agregar(3, "D");

        assertArrayEquals(new String[] {"A", "B", "C", "D"}, elementosDe(lista));
    }

    @Test
    void agregarEnIndiceInvalidoLanzaExcepcion() {
        ListaCircularDoble<Integer> lista = listaDe(1);

        assertThrows(IndexOutOfBoundsException.class, () -> lista.agregar(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.agregar(2, 2));
    }

    @Test
    void obtenerEnIndiceInvalidoLanzaExcepcion() {
        ListaCircularDoble<Integer> lista = listaDe(1);

        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(1));
    }

    @Test
    void obtenerRecorreDesdeElPrimeroODesdeElUltimoSegunConvenga() {
        // Con 6 elementos, nodoEn debe elegir la mitad más corta para llegar al índice:
        // índices < tamaño/2 se recorren desde "primero", el resto desde "ultimo".
        ListaCircularDoble<Integer> lista = listaDe(0, 1, 2, 3, 4, 5);

        assertEquals(1, lista.obtener(1)); // recorrido desde el primero
        assertEquals(4, lista.obtener(4)); // recorrido desde el último
        assertEquals(0, lista.obtener(0));
        assertEquals(5, lista.obtener(5));
    }

    @Test
    void removerPorIndiceDevuelveElementoYMantieneElOrdenCircular() {
        ListaCircularDoble<Integer> lista = listaDe(1, 2, 3, 4);

        assertEquals(2, lista.remover(1));
        assertArrayEquals(new Integer[] {1, 3, 4}, elementosDe(lista));

        // Remueve el último elemento actual: debe actualizar correctamente "ultimo".
        assertEquals(4, lista.remover(2));
        assertArrayEquals(new Integer[] {1, 3}, elementosDe(lista));

        // Tras eliminar el último, agregar debe encadenar correctamente en ambos sentidos.
        lista.agregar(5);
        assertArrayEquals(new Integer[] {1, 3, 5}, elementosDe(lista));
    }

    @Test
    void removerPorIndiceCeroActualizaElPrimero() {
        ListaCircularDoble<Integer> lista = listaDe(1, 2, 3);

        assertEquals(1, lista.remover(0));
        assertArrayEquals(new Integer[] {2, 3}, elementosDe(lista));

        // El nuevo primero también debe quedar bien enlazado hacia atrás (circularidad doble).
        lista.agregar(0, 0);
        assertArrayEquals(new Integer[] {0, 2, 3}, elementosDe(lista));
    }

    @Test
    void removerPorElementoEliminaSoloLaPrimeraOcurrencia() {
        ListaCircularDoble<String> lista = listaDe("A", "B", "A");

        assertTrue(lista.remover("A"));
        assertFalse(lista.remover("Z"));
        assertArrayEquals(new String[] {"B", "A"}, elementosDe(lista));
    }

    @Test
    void removerPorElementoQueEsElUltimoActualizaLaReferencia() {
        ListaCircularDoble<Integer> lista = listaDe(1, 2, 3);

        assertTrue(lista.remover((Integer) 3));
        assertArrayEquals(new Integer[] {1, 2}, elementosDe(lista));

        lista.agregar(4);
        assertArrayEquals(new Integer[] {1, 2, 4}, elementosDe(lista));
    }

    @Test
    void contieneEIndiceDeSoportanValoresNulos() {
        ListaCircularDoble<String> lista = listaDe("uno", null, "dos", null);

        assertTrue(lista.contiene(null));
        assertEquals(1, lista.indiceDe(null));
        assertTrue(lista.remover((String) null));
        assertEquals(2, lista.indiceDe(null));
    }

    @Test
    void buscarRetornaLaPrimeraCoincidenciaOnullYExigeCriterioNoNulo() {
        ListaCircularDoble<Integer> lista = listaDe(3, 4, 6, 8);

        assertEquals(4, lista.buscar(numero -> numero % 2 == 0));
        assertNull(lista.buscar(numero -> numero > 10));
        assertThrows(IllegalArgumentException.class, () -> lista.buscar(null));
    }

    @Test
    void ordenarDevuelveUnaNuevaListaOrdenadaSinModificarLaOriginalYExigeComparadorNoNulo() {
        ListaCircularDoble<Integer> original = listaDe(3, 1, 2);

        TDALista<Integer> ordenada = original.ordenar(Comparator.naturalOrder());

        assertArrayEquals(new Integer[] {1, 2, 3}, elementosDe(ordenada));
        assertArrayEquals(new Integer[] {3, 1, 2}, elementosDe(original));
        assertThrows(IllegalArgumentException.class, () -> original.ordenar(null));
    }

    @Test
    void vaciarEliminaTodosLosElementosYPermiteReutilizarLaLista() {
        ListaCircularDoble<Integer> lista = listaDe(1, 2);

        lista.vaciar();

        assertTrue(lista.esVacio());
        assertEquals(0, lista.tamaño());
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(0));

        lista.agregar(3);
        assertEquals(3, lista.obtener(0));
        assertEquals(1, lista.tamaño());
    }

    @SafeVarargs
    private static <T> ListaCircularDoble<T> listaDe(T... elementos) {
        ListaCircularDoble<T> lista = new ListaCircularDoble<>();
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
