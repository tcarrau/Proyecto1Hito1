package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import ucu.edu.implementaciones.ArbolAVL;

class ArbolAVLTest {

    @Test
    void arbolVacioNoTieneNodos() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();

        assertTrue(arbol.esVacio());
        assertEquals(0, arbol.cantidadNodos());
        assertNull(arbol.buscar(1));
    }

    @Test
    void insertarYBuscarFuncionaComoUnBstNormal() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();

        arbol.insertar(5);
        arbol.insertar(3);
        arbol.insertar(8);

        assertEquals(3, arbol.cantidadNodos());
        assertEquals(5, arbol.buscar(5));
        assertEquals(3, arbol.buscar(3));
        assertEquals(8, arbol.buscar(8));
        assertNull(arbol.buscar(99));
    }

    @Test
    void noInsertaDuplicados() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        arbol.insertar(5);

        boolean insertado = arbol.insertar(5);

        assertFalse(insertado);
        assertEquals(1, arbol.cantidadNodos());
    }

    @Test
    void porNivelesVisitaLaRaizAntesQueSusHijosYNietos() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        // Con estos 3 valores, tras balancear, la raíz queda en 20 con hijos 10 y 30.
        arbol.insertar(30);
        arbol.insertar(10);
        arbol.insertar(20);

        List<Integer> porNiveles = new ArrayList<>();
        arbol.porNiveles(porNiveles::add);

        assertEquals(List.of(20, 10, 30), porNiveles);
    }

    @Test
    void inOrderSiempreDaLaSecuenciaOrdenada() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        int[] valores = {50, 20, 70, 10, 30, 60, 80, 5, 15};
        for (int v : valores) arbol.insertar(v);

        List<Integer> enOrden = new ArrayList<>();
        arbol.inOrder(enOrden::add);

        List<Integer> esperado = new ArrayList<>(enOrden);
        esperado.sort(Integer::compareTo);
        assertEquals(esperado, enOrden);
        assertEquals(valores.length, enOrden.size());
    }

    @Test
    void insertarEnOrdenCrecienteNoDegeneraAListaYQuedaBalanceado() {
        // Este es el caso que la letra pide explícitamente: un BST común, insertando
        // 1,2,3...N en orden, termina siendo una lista (altura N). Un AVL no.
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        int n = 1000;

        for (int i = 1; i <= n; i++) {
            arbol.insertar(i);
        }

        assertEquals(n, arbol.cantidadNodos());

        // Cota teórica de un AVL: altura <= 1.44 * log2(n+2). Le damos margen (2x log2).
        double cotaMaxima = 2.0 * (Math.log(n + 2) / Math.log(2));
        assertTrue(arbol.altura() <= cotaMaxima,
                "altura fue " + arbol.altura() + ", esperado <= " + cotaMaxima);
    }

    @Test
    void rotacionSimpleIzquierdaIzquierda() {
        // Insertar en orden descendente fuerza el caso "izquierda-izquierda".
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        arbol.insertar(30);
        arbol.insertar(20);
        arbol.insertar(10); // sin rotar, 30 quedaría con factor de balance 2

        assertEquals(2, arbol.altura()); // balanceado: 20 es raíz, 10 y 30 sus hijos
        assertEquals(20, arbol.obtenerRaiz().getDato());
    }

    @Test
    void rotacionSimpleDerechaDerecha() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        arbol.insertar(10);
        arbol.insertar(20);
        arbol.insertar(30);

        assertEquals(2, arbol.altura());
        assertEquals(20, arbol.obtenerRaiz().getDato());
    }

    @Test
    void rotacionDobleIzquierdaDerecha() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        arbol.insertar(30);
        arbol.insertar(10);
        arbol.insertar(20); // el hijo izquierdo (10) está cargado a la derecha

        assertEquals(2, arbol.altura());
        assertEquals(20, arbol.obtenerRaiz().getDato());
    }

    @Test
    void rotacionDobleDerechaIzquierda() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        arbol.insertar(10);
        arbol.insertar(30);
        arbol.insertar(20);

        assertEquals(2, arbol.altura());
        assertEquals(20, arbol.obtenerRaiz().getDato());
    }

    @Test
    void eliminarUnaHojaLaSacaDelArbol() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        arbol.insertar(5);
        arbol.insertar(3);
        arbol.insertar(8);

        boolean eliminado = arbol.eliminar(3);

        assertTrue(eliminado);
        assertEquals(2, arbol.cantidadNodos());
        assertNull(arbol.buscar(3));
    }

    @Test
    void eliminarLaRaizUnicaDejaElArbolVacio() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        arbol.insertar(5);

        boolean eliminado = arbol.eliminar(5);

        assertTrue(eliminado);
        assertTrue(arbol.esVacio());
    }

    @Test
    void eliminarUnValorInexistenteDevuelveFalse() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        arbol.insertar(5);

        assertFalse(arbol.eliminar(99));
        assertEquals(1, arbol.cantidadNodos());
    }
}
