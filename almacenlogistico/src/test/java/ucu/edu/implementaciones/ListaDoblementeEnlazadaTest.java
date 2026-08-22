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

class ListaDoblementeEnlazadaTest {

    @Test
    void listaNuevaEstaVacia() {
        ListaDoblementeEnlazada<String> lista = new ListaDoblementeEnlazada<>();

        assertTrue(lista.esVacio());
        assertEquals(0, lista.tamaño());
    }

    @Test
    void listaVaciaLanzaExcepcionesEnOperacionesDeAcceso() {
        ListaDoblementeEnlazada<Integer> lista = new ListaDoblementeEnlazada<>();

        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(0));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.remover(0));
        assertFalse(lista.contiene(1));
        assertEquals(-1, lista.indiceDe(1));
        assertNull(lista.buscar(numero -> true));
        assertFalse(lista.remover(Integer.valueOf(1)));
    }

    @Test
    void agregarAlFinalConservaElOrden() {
        ListaDoblementeEnlazada<Integer> lista = new ListaDoblementeEnlazada<>();

        lista.agregar(10);
        lista.agregar(20);
        lista.agregar(30);

        assertEquals(3, lista.tamaño());
        assertArrayEquals(new Integer[] {10, 20, 30}, elementosDe(lista));
    }

    @Test
    void agregarEnIndiceCeroInsertaAlPrincipioYActualizaElPrimero() {
        ListaDoblementeEnlazada<String> lista = listaDe("B", "C");

        lista.agregar(0, "A");

        assertArrayEquals(new String[] {"A", "B", "C"}, elementosDe(lista));
        assertEquals("A", lista.obtener(0));
    }

    @Test
    void agregarEnIndiceIntermedioDesplazaLosElementosPosteriores() {
        ListaDoblementeEnlazada<String> lista = listaDe("A", "C");

        lista.agregar(1, "B");
        lista.agregar(3, "D");

        assertArrayEquals(new String[] {"A", "B", "C", "D"}, elementosDe(lista));
    }

    @Test
    void agregarEnIndiceIgualAlTamañoDejaLaReferenciaAlUltimoUsable() {
        ListaDoblementeEnlazada<String> lista = listaDe("A", "B");

        lista.agregar(2, "C");
        lista.agregar("D");

        assertArrayEquals(new String[] {"A", "B", "C", "D"}, elementosDe(lista));
    }

    @Test
    void agregarEnIndiceInvalidoLanzaExcepcion() {
        ListaDoblementeEnlazada<Integer> lista = listaDe(1);

        assertThrows(IndexOutOfBoundsException.class, () -> lista.agregar(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.agregar(2, 2));
    }

    @Test
    void obtenerEnIndiceInvalidoLanzaExcepcion() {
        ListaDoblementeEnlazada<Integer> lista = listaDe(1);

        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(1));
    }

    @Test
    void obtenerRecorreDesdeElExtremoMasCercanoAlIndice() {
        ListaDoblementeEnlazada<Integer> lista = new ListaDoblementeEnlazada<>();
        for (int i = 0; i < 100; i++) {
            lista.agregar(i);
        }

        // Indices de la primera mitad, la segunda mitad y los bordes exactos.
        assertEquals(0, lista.obtener(0));
        assertEquals(10, lista.obtener(10));
        assertEquals(49, lista.obtener(49));
        assertEquals(50, lista.obtener(50));
        assertEquals(90, lista.obtener(90));
        assertEquals(99, lista.obtener(99));
    }

    @Test
    void removerPorIndiceDevuelveElementoYMantieneElOrden() {
        ListaDoblementeEnlazada<Integer> lista = listaDe(1, 2, 3, 4);

        assertEquals(2, lista.remover(1));
        assertEquals(4, lista.remover(2));
        assertArrayEquals(new Integer[] {1, 3}, elementosDe(lista));
    }

    @Test
    void removerElUltimoDejaLaReferenciaAlUltimoSincronizada() {
        ListaDoblementeEnlazada<String> lista = listaDe("A", "B", "C");

        assertEquals("C", lista.remover(2));
        lista.agregar("D");

        assertArrayEquals(new String[] {"A", "B", "D"}, elementosDe(lista));
    }

    @Test
    void removerElPrimeroDejaLaReferenciaAlPrimeroSincronizada() {
        ListaDoblementeEnlazada<String> lista = listaDe("A", "B", "C");

        assertEquals("A", lista.remover(0));
        lista.agregar(0, "Z");

        assertArrayEquals(new String[] {"Z", "B", "C"}, elementosDe(lista));
    }

    @Test
    void removerElUnicoElementoDejaLaListaVaciaYReutilizable() {
        ListaDoblementeEnlazada<String> lista = listaDe("unico");

        assertEquals("unico", lista.remover(0));
        assertTrue(lista.esVacio());

        lista.agregar("nuevo");
        lista.agregar("otro");

        assertArrayEquals(new String[] {"nuevo", "otro"}, elementosDe(lista));
    }

    @Test
    void removerPorElementoEliminaSoloLaPrimeraOcurrencia() {
        ListaDoblementeEnlazada<String> lista = listaDe("A", "B", "A");

        assertTrue(lista.remover("A"));
        assertFalse(lista.remover("Z"));
        assertArrayEquals(new String[] {"B", "A"}, elementosDe(lista));
    }

    @Test
    void contieneEIndiceDeSoportanValoresNulos() {
        ListaDoblementeEnlazada<String> lista = listaDe("uno", null, "dos", null);

        assertTrue(lista.contiene(null));
        assertEquals(1, lista.indiceDe(null));
        assertTrue(lista.remover(null));
        assertEquals(2, lista.indiceDe(null));
    }

    @Test
    void buscarRetornaLaPrimeraCoincidenciaOnullYExigeCriterioNoNulo() {
        ListaDoblementeEnlazada<Integer> lista = listaDe(3, 4, 6, 8);

        assertEquals(4, lista.buscar(numero -> numero % 2 == 0));
        assertNull(lista.buscar(numero -> numero > 10));
        assertThrows(NullPointerException.class, () -> lista.buscar(null));
    }

    @Test
    void ordenarDevuelveUnaNuevaListaOrdenadaSinModificarLaOriginalYExigeComparadorNoNulo() {
        ListaDoblementeEnlazada<Integer> original = listaDe(5, 3, 9, 1, 7);

        TDALista<Integer> ordenada = original.ordenar(Comparator.naturalOrder());

        assertArrayEquals(new Integer[] {1, 3, 5, 7, 9}, elementosDe(ordenada));
        assertArrayEquals(new Integer[] {5, 3, 9, 1, 7}, elementosDe(original));
        assertThrows(NullPointerException.class, () -> original.ordenar(null));
    }

    @Test
    void ordenarFuncionaEnLosCasosBorde() {
        ListaDoblementeEnlazada<Integer> vacia = new ListaDoblementeEnlazada<>();

        assertEquals(0, vacia.ordenar(Comparator.naturalOrder()).tamaño());
        assertArrayEquals(new Integer[] {7},
                elementosDe(listaDe(7).ordenar(Comparator.naturalOrder())));
        assertArrayEquals(new Integer[] {1, 2},
                elementosDe(listaDe(2, 1).ordenar(Comparator.naturalOrder())));
        assertArrayEquals(new Integer[] {4, 4, 4},
                elementosDe(listaDe(4, 4, 4).ordenar(Comparator.naturalOrder())));
    }

    @Test
    void ordenarEsEstableConElementosEquivalentes() {
        ListaDoblementeEnlazada<String> lista = listaDe("bb", "a", "cc", "d", "aa");

        TDALista<String> ordenada = lista.ordenar(Comparator.comparingInt(String::length));

        assertArrayEquals(new String[] {"a", "d", "bb", "cc", "aa"}, elementosDe(ordenada));
    }

    @Test
    void ordenarDejaLosEnlacesHaciaAtrasReconstruidos() {
        ListaDoblementeEnlazada<Integer> original = listaDe(5, 3, 9, 1, 7);

        TDALista<Integer> ordenada = original.ordenar(Comparator.naturalOrder());

        // Leer de atras hacia adelante recorre la lista usando los enlaces anteriores.
        for (int i = ordenada.tamaño() - 1; i >= 0; i--) {
            assertEquals(new Integer[] {1, 3, 5, 7, 9}[i], ordenada.obtener(i));
        }

        // Y removerlos desde el final tambien depende de esos enlaces.
        assertEquals(9, ordenada.remover(ordenada.tamaño() - 1));
        assertEquals(7, ordenada.remover(ordenada.tamaño() - 1));
        assertArrayEquals(new Integer[] {1, 3, 5}, elementosDe(ordenada));
    }

    @Test
    void vaciarEliminaTodosLosElementosYPermiteReutilizarLaLista() {
        ListaDoblementeEnlazada<Integer> lista = listaDe(1, 2);

        lista.vaciar();

        assertTrue(lista.esVacio());
        assertEquals(0, lista.tamaño());
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(0));

        lista.agregar(3);
        lista.agregar(4);
        assertArrayEquals(new Integer[] {3, 4}, elementosDe(lista));
    }

    @Test
    void removerRepetidamenteDesdeLosDosExtremosMantieneLosEnlacesSanos() {
        ListaDoblementeEnlazada<Integer> lista = new ListaDoblementeEnlazada<>();
        for (int i = 0; i < 50; i++) {
            lista.agregar(i);
        }

        for (int i = 49; i >= 25; i--) {
            assertEquals(i, lista.remover(lista.tamaño() - 1));
        }
        for (int i = 0; i < 25; i++) {
            assertEquals(i, lista.remover(0));
        }

        assertTrue(lista.esVacio());
        lista.agregar(99);
        assertArrayEquals(new Integer[] {99}, elementosDe(lista));
    }

    @SafeVarargs
    private static <T> ListaDoblementeEnlazada<T> listaDe(T... elementos) {
        ListaDoblementeEnlazada<T> lista = new ListaDoblementeEnlazada<>();
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
