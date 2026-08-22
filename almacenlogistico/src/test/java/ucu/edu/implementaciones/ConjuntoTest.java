package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import org.junit.jupiter.api.Test;

import ucu.edu.aed.tda.TDAConjunto;
import ucu.edu.aed.tda.TDALista;

class ConjuntoTest {

    @Test
    void conjuntoNuevoEstaVacio() {
        Conjunto<String> conjunto = new Conjunto<>();

        assertTrue(conjunto.esVacio());
        assertEquals(0, conjunto.tamaño());
    }

    @Test
    void agregarIgnoraLosElementosRepetidos() {
        Conjunto<String> conjunto = new Conjunto<>();

        conjunto.agregar("A");
        conjunto.agregar("B");
        conjunto.agregar("A");
        conjunto.agregar("B");

        assertEquals(2, conjunto.tamaño());
        assertArrayEquals(new String[] {"A", "B"}, elementosDe(conjunto));
    }

    @Test
    void agregarEnUnaPosicionTampocoAdmiteRepetidos() {
        Conjunto<String> conjunto = conjuntoDe("A", "B");

        conjunto.agregar(0, "C");
        conjunto.agregar(0, "A");

        assertEquals(3, conjunto.tamaño());
        assertArrayEquals(new String[] {"C", "A", "B"}, elementosDe(conjunto));
    }

    @Test
    void unionReuneLosElementosDeAmbosSinRepetir() {
        Conjunto<String> unos = conjuntoDe("A", "B");
        Conjunto<String> otros = conjuntoDe("B", "C");

        TDAConjunto<String> union = unos.union(otros);

        assertEquals(3, union.tamaño());
        assertTrue(union.contiene("A"));
        assertTrue(union.contiene("B"));
        assertTrue(union.contiene("C"));
    }

    @Test
    void interseccionDevuelveSoloLosElementosComunes() {
        Conjunto<String> unos = conjuntoDe("A", "B", "C");
        Conjunto<String> otros = conjuntoDe("B", "C", "D");

        TDAConjunto<String> interseccion = unos.interseccion(otros);

        assertEquals(2, interseccion.tamaño());
        assertTrue(interseccion.contiene("B"));
        assertTrue(interseccion.contiene("C"));
        assertFalse(interseccion.contiene("A"));
    }

    @Test
    void diferenciaDevuelveLoQueEstaEnEsteConjuntoYNoEnElOtro() {
        Conjunto<String> unos = conjuntoDe("A", "B", "C");
        Conjunto<String> otros = conjuntoDe("B", "D");

        TDAConjunto<String> diferencia = unos.diferencia(otros);

        assertEquals(2, diferencia.tamaño());
        assertTrue(diferencia.contiene("A"));
        assertTrue(diferencia.contiene("C"));
        assertFalse(diferencia.contiene("B"));
    }

    @Test
    void laDiferenciaNoEsSimetrica() {
        Conjunto<String> unos = conjuntoDe("A", "B");
        Conjunto<String> otros = conjuntoDe("B", "C");

        assertArrayEquals(new String[] {"A"}, elementosDe(unos.diferencia(otros)));
        assertArrayEquals(new String[] {"C"}, elementosDe(otros.diferencia(unos)));
    }

    @Test
    void lasOperacionesNoModificanLosConjuntosOriginales() {
        Conjunto<String> unos = conjuntoDe("A", "B");
        Conjunto<String> otros = conjuntoDe("B", "C");

        unos.union(otros);
        unos.interseccion(otros);
        unos.diferencia(otros);

        assertArrayEquals(new String[] {"A", "B"}, elementosDe(unos));
        assertArrayEquals(new String[] {"B", "C"}, elementosDe(otros));
    }

    @Test
    void operacionesConElConjuntoVacio() {
        Conjunto<String> conElementos = conjuntoDe("A", "B");
        Conjunto<String> vacio = new Conjunto<>();

        assertEquals(2, conElementos.union(vacio).tamaño());
        assertEquals(0, conElementos.interseccion(vacio).tamaño());
        assertEquals(2, conElementos.diferencia(vacio).tamaño());
        assertEquals(0, vacio.diferencia(conElementos).tamaño());
    }

    @Test
    void esSubconjuntoDeReconoceLaInclusion() {
        Conjunto<String> pequeño = conjuntoDe("A", "B");
        Conjunto<String> grande = conjuntoDe("A", "B", "C");

        assertTrue(pequeño.esSubconjuntoDe(grande));
        assertFalse(grande.esSubconjuntoDe(pequeño));
    }

    @Test
    void todoConjuntoEsSubconjuntoDeSiMismoYElVacioDeCualquiera() {
        Conjunto<String> conjunto = conjuntoDe("A", "B");
        Conjunto<String> vacio = new Conjunto<>();

        assertTrue(conjunto.esSubconjuntoDe(conjunto));
        assertTrue(vacio.esSubconjuntoDe(conjunto));
        assertTrue(vacio.esSubconjuntoDe(vacio));
        assertFalse(conjunto.esSubconjuntoDe(vacio));
    }

    @Test
    void lasOperacionesDeConjuntosRechazanUnArgumentoNulo() {
        Conjunto<String> conjunto = conjuntoDe("A");

        assertThrows(NullPointerException.class, () -> conjunto.union(null));
        assertThrows(NullPointerException.class, () -> conjunto.interseccion(null));
        assertThrows(NullPointerException.class, () -> conjunto.diferencia(null));
        assertThrows(NullPointerException.class, () -> conjunto.esSubconjuntoDe(null));
    }

    @Test
    void removerPorElementoYPorIndiceQuitanDelConjunto() {
        Conjunto<String> conjunto = conjuntoDe("A", "B", "C");

        assertTrue(conjunto.remover("B"));
        assertFalse(conjunto.remover("Z"));
        assertEquals("A", conjunto.remover(0));
        assertArrayEquals(new String[] {"C"}, elementosDe(conjunto));
    }

    @Test
    void unElementoRemovidoSePuedeVolverAAgregar() {
        Conjunto<String> conjunto = conjuntoDe("A", "B");

        assertTrue(conjunto.remover("A"));
        conjunto.agregar("A");

        assertEquals(2, conjunto.tamaño());
        assertTrue(conjunto.contiene("A"));
    }

    @Test
    void contieneEIndiceDeUbicanLosElementos() {
        Conjunto<String> conjunto = conjuntoDe("A", "B", "C");

        assertTrue(conjunto.contiene("B"));
        assertFalse(conjunto.contiene("Z"));
        assertEquals(1, conjunto.indiceDe("B"));
        assertEquals(-1, conjunto.indiceDe("Z"));
    }

    @Test
    void buscarRetornaLaPrimeraCoincidenciaOnull() {
        Conjunto<Integer> conjunto = conjuntoDe(3, 4, 6, 8);

        assertEquals(4, conjunto.buscar(numero -> numero % 2 == 0));
        assertNull(conjunto.buscar(numero -> numero > 10));
    }

    @Test
    void ordenarDevuelveUnaNuevaListaSinModificarElConjunto() {
        Conjunto<Integer> conjunto = conjuntoDe(3, 1, 2);

        TDALista<Integer> ordenada = conjunto.ordenar(Comparator.naturalOrder());

        assertArrayEquals(new Integer[] {1, 2, 3}, elementosDe(ordenada));
        assertArrayEquals(new Integer[] {3, 1, 2}, elementosDe(conjunto));
    }

    @Test
    void vaciarEliminaTodosLosElementosYPermiteReutilizarElConjunto() {
        Conjunto<String> conjunto = conjuntoDe("A", "B");

        conjunto.vaciar();

        assertTrue(conjunto.esVacio());
        assertFalse(conjunto.contiene("A"));

        conjunto.agregar("A");
        assertArrayEquals(new String[] {"A"}, elementosDe(conjunto));
    }

    @SafeVarargs
    private static <T> Conjunto<T> conjuntoDe(T... elementos) {
        Conjunto<T> conjunto = new Conjunto<>();
        for (T elemento : elementos) {
            conjunto.agregar(elemento);
        }
        return conjunto;
    }

    private static <T> Object[] elementosDe(TDALista<T> lista) {
        Object[] elementos = new Object[lista.tamaño()];
        for (int i = 0; i < lista.tamaño(); i++) {
            elementos[i] = lista.obtener(i);
        }
        return elementos;
    }
}
