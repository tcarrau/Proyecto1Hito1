package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import ucu.edu.implementaciones.ArbolBinario;

class ArbolBinarioTest {

    @Test
    void arbolVacioNoTieneNodos() {
        ArbolBinario<Integer> arbol = new ArbolBinario<>();

        assertTrue(arbol.esVacio());
        assertEquals(0, arbol.cantidadNodos());
        assertEquals(0, arbol.cantidadHojas());
        assertEquals(0, arbol.cantidadNodosInternos());
        assertEquals(0, arbol.altura());
        assertNull(arbol.obtenerRaiz());
        assertNull(arbol.buscar(5));
        assertFalse(arbol.eliminar(5));
    }

    @Test
    void unUnicoNodoEsRaizYHoja() {
        ArbolBinario<Integer> arbol = new ArbolBinario<>();

        arbol.insertar(50);

        assertFalse(arbol.esVacio());
        assertEquals(1, arbol.cantidadNodos());
        assertEquals(1, arbol.cantidadHojas());
        assertEquals(0, arbol.cantidadNodosInternos());
        assertEquals(1, arbol.altura());
        assertEquals(50, arbol.obtenerRaiz().getDato());
        assertEquals(50, arbol.buscar(50));
    }

    @Test
    void eliminarElUnicoNodoDejaElArbolVacioYReutilizable() {
        // Caso especial: la raíz es hoja. El nodo no puede "vaciarse a sí mismo";
        // lo tiene que resolver el árbol poniendo raiz = null.
        ArbolBinario<Integer> arbol = new ArbolBinario<>();
        arbol.insertar(50);

        assertTrue(arbol.eliminar(50));

        assertTrue(arbol.esVacio());
        assertEquals(0, arbol.cantidadNodos());
        assertNull(arbol.obtenerRaiz());

        arbol.insertar(7);
        assertEquals(1, arbol.cantidadNodos());
        assertEquals(7, arbol.obtenerRaiz().getDato());
    }

    @Test
    void insertarYBuscarFuncionaComoUnBstNormal() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        assertEquals(65, arbol.buscar(65));
        assertNull(arbol.buscar(999));
    }

    @Test
    void noInsertaDuplicados() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        assertFalse(arbol.insertar(50));
        assertEquals(8, arbol.cantidadNodos());
    }

    @Test
    void cuentaHojasYNodosInternosCorrectamente() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        // Hojas: 20, 40, 65, 80. Internos: 50, 30, 70, 60.
        assertEquals(4, arbol.cantidadHojas());
        assertEquals(4, arbol.cantidadNodosInternos());
    }

    @Test
    void inOrderSiempreDaLaSecuenciaOrdenada() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        assertEquals(List.of(20, 30, 40, 50, 60, 65, 70, 80), recorrer(arbol::inOrder));
    }

    @Test
    void preOrderVisitaLaRaizAntesQueLosHijos() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        assertEquals(List.of(50, 30, 20, 40, 70, 60, 65, 80), recorrer(arbol::preOrder));
    }

    @Test
    void postOrderVisitaLaRaizDespuesQueLosHijos() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        assertEquals(List.of(20, 40, 30, 65, 60, 80, 70, 50), recorrer(arbol::postOrder));
    }

    @Test
    void eliminarUnaHojaLaSacaDelArbolSinAfectarAlResto() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        assertTrue(arbol.eliminar(20));

        assertNull(arbol.buscar(20));
        assertEquals(7, arbol.cantidadNodos());
        assertEquals(List.of(30, 40, 50, 60, 65, 70, 80), recorrer(arbol::inOrder));
    }

    @Test
    void eliminarUnNodoConUnHijoLoReemplazaPorEseHijo() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        // 60 tiene un único hijo (65, a la derecha).
        assertTrue(arbol.eliminar(60));

        assertNull(arbol.buscar(60));
        assertEquals(65, arbol.buscar(65));
        assertEquals(7, arbol.cantidadNodos());
        assertEquals(List.of(20, 30, 40, 50, 65, 70, 80), recorrer(arbol::inOrder));
    }

    @Test
    void eliminarUnNodoConDosHijosLoReemplazaPorElSucesorInorden() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        // 70 tiene dos hijos (60 y 80). Su sucesor inorden es 80.
        assertTrue(arbol.eliminar(70));

        assertNull(arbol.buscar(70));
        assertEquals(7, arbol.cantidadNodos());
        assertEquals(List.of(20, 30, 40, 50, 60, 65, 80), recorrer(arbol::inOrder));
    }

    @Test
    void eliminarLaRaizConDosHijosLaReemplazaPorElSucesorYSigueSiendoUnBstValido() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        assertTrue(arbol.eliminar(50));

        assertNull(arbol.buscar(50));
        assertEquals(60, arbol.obtenerRaiz().getDato());
        assertEquals(List.of(20, 30, 40, 60, 65, 70, 80), recorrer(arbol::inOrder));
    }

    @Test
    void eliminarLaRaizConUnSoloHijoLaReemplazaPorEseHijo() {
        ArbolBinario<Integer> arbol = new ArbolBinario<>();
        arbol.insertar(10);
        arbol.insertar(20);

        assertTrue(arbol.eliminar(10));

        assertEquals(1, arbol.cantidadNodos());
        assertEquals(20, arbol.obtenerRaiz().getDato());
        assertNull(arbol.buscar(10));
    }

    @Test
    void eliminarUnValorInexistenteDevuelveFalseYNoAlteraElArbol() {
        ArbolBinario<Integer> arbol = arbolDeReferencia();

        assertFalse(arbol.eliminar(999));

        assertEquals(8, arbol.cantidadNodos());
    }

    @Test
    void insertarEnOrdenCrecienteDegeneraAUnaCadenaLineal() {
        // Este es el caso que la letra pide explícitamente: a diferencia de un AVL,
        // este árbol no reequilibra, así que insertar 1,2,3...N en orden ascendente
        // termina siendo una lista (altura N, una sola hoja).
        ArbolBinario<Integer> arbol = new ArbolBinario<>();
        int n = 1000;

        for (int i = 1; i <= n; i++) {
            arbol.insertar(i);
        }

        assertEquals(n, arbol.cantidadNodos());
        assertEquals(n, arbol.altura());
        assertEquals(1, arbol.cantidadHojas());
    }

    /**
     * Construye:
     * <pre>
     *              50
     *            /    \
     *          30      70
     *         /  \    /  \
     *       20   40  60   80
     *                   \
     *                    65
     * </pre>
     */
    private static ArbolBinario<Integer> arbolDeReferencia() {
        ArbolBinario<Integer> arbol = new ArbolBinario<>();
        for (int v : new int[] {50, 30, 70, 20, 40, 60, 80, 65}) {
            arbol.insertar(v);
        }
        return arbol;
    }

    private static List<Integer> recorrer(java.util.function.Consumer<java.util.function.Consumer<Integer>> recorrido) {
        List<Integer> visitados = new ArrayList<>();
        recorrido.accept(visitados::add);
        return visitados;
    }
}
