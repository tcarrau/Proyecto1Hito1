package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ABBTest {

    @Test
    void arbolVacioNoTieneNodosHojasNiNodosInternos() {
        ABB<Integer> arbol = new ABB<>();

        assertEquals(0, arbol.cantidadNodos());
        assertEquals(0, arbol.cantidadHojas());
        assertEquals(0, arbol.cantidadNodosInternos());
    }

    @Test
    void calculaNodosHojasYNodosInternos() {
        ABB<Integer> arbol = new ABB<>();
        insertar(arbol, 8, 3, 10, 1, 6, 14, 4, 7, 13);

        assertEquals(9, arbol.cantidadNodos());
        assertEquals(4, arbol.cantidadHojas());
        assertEquals(5, arbol.cantidadNodosInternos());
    }

    @Test
    void unArbolConUnNodoTieneUnaHojaYCeroNodosInternos() {
        ABB<Integer> arbol = new ABB<>();
        arbol.insertar(5);

        assertEquals(1, arbol.cantidadNodos());
        assertEquals(1, arbol.cantidadHojas());
        assertEquals(0, arbol.cantidadNodosInternos());
    }

    private void insertar(ABB<Integer> arbol, int... valores) {
        for (int valor : valores) {
            arbol.insertar(valor);
        }
    }
}
