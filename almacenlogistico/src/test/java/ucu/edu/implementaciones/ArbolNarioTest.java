package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import ucu.edu.aed.tda.TDANodoNario;
import ucu.edu.implementaciones.ArbolNario;

class ArbolNarioTest {

    @Test
    void arbolVacioNoTieneNodos() {
        ArbolNario<String> arbol = new ArbolNario<>();

        assertTrue(arbol.esVacio());
        assertEquals(0, arbol.cantidadNodos());
        assertEquals(0, arbol.cantidadHojas());
        assertEquals(0, arbol.cantidadNodosInternos());
        assertEquals(0, arbol.altura());
        assertNull(arbol.obtenerRaiz());
        assertNull(arbol.buscar(s -> true));
        assertFalse(arbol.eliminar(s -> true));
    }

    @Test
    void agregarHijoConPadreNuloEstableceLaRaiz() {
        ArbolNario<String> arbol = new ArbolNario<>();

        TDANodoNario<String> raiz = arbol.agregarHijo(null, "Deposito");

        assertFalse(arbol.esVacio());
        assertEquals("Deposito", raiz.getDato());
        assertEquals(1, arbol.cantidadNodos());
        assertEquals(1, arbol.cantidadHojas());
        assertEquals(1, arbol.altura());
        assertTrue(raiz.esHoja());
    }

    @Test
    void agregarHijoConPadreNuloEnUnArbolNoVacioLanzaExcepcion() {
        ArbolNario<String> arbol = new ArbolNario<>();
        arbol.agregarHijo(null, "Deposito");

        assertThrows(IllegalStateException.class, () -> arbol.agregarHijo(null, "otro"));
    }

    @Test
    void ramasDeProfundidadDistintaSeReflejanEnLaAlturaYElConteo() {
        // Modela el escenario de la letra: la playa de recepción tiene posiciones
        // directas (profundidad 2), mientras que la cámara de frío se subdivide
        // varias veces más (profundidad 6). El árbol no obliga a que todas las
        // ramas tengan la misma cantidad de niveles.
        ArbolNario<String> deposito = new ArbolNario<>();
        TDANodoNario<String> raiz = deposito.agregarHijo(null, "Deposito");

        TDANodoNario<String> playa = deposito.agregarHijo(raiz, "PlayaRecepcion");
        deposito.agregarHijo(playa, "PosicionA");
        deposito.agregarHijo(playa, "PosicionB");

        TDANodoNario<String> camara = deposito.agregarHijo(raiz, "CamaraFrio");
        TDANodoNario<String> pasillo = deposito.agregarHijo(camara, "PasilloF1");
        TDANodoNario<String> estanteria = deposito.agregarHijo(pasillo, "EstanteriaF1");
        TDANodoNario<String> bandeja = deposito.agregarHijo(estanteria, "BandejaF1");
        deposito.agregarHijo(bandeja, "PosicionF1");

        assertEquals(9, deposito.cantidadNodos());
        assertEquals(3, deposito.cantidadHojas()); // PosicionA, PosicionB, PosicionF1
        assertEquals(6, deposito.cantidadNodosInternos());
        assertEquals(6, deposito.altura()); // la rama de la cámara de frío domina
        assertEquals(2, raiz.cantidadHijosDirectos());
        assertEquals(2, playa.cantidadHijosDirectos());
        assertEquals(1, camara.cantidadHijosDirectos());
    }

    @Test
    void buscarEncuentraElPrimerNodoEnOrdenPreorden() {
        ArbolNario<String> arbol = depositoDeReferencia();

        assertEquals("EstanteriaF1", arbol.buscar(s -> s.equals("EstanteriaF1")).getDato());
        assertNull(arbol.buscar(s -> s.equals("NoExiste")));
    }

    @Test
    void preOrderVisitaLaRaizAntesQueCadaHijoYSuSubarbol() {
        ArbolNario<String> arbol = depositoDeReferencia();

        assertEquals(
                List.of("Deposito", "PlayaRecepcion", "PosicionA", "PosicionB",
                        "CamaraFrio", "PasilloF1", "EstanteriaF1", "BandejaF1", "PosicionF1"),
                recorrer(arbol::preOrder));
    }

    @Test
    void postOrderVisitaLosHijosAntesQueLaRaiz() {
        ArbolNario<String> arbol = depositoDeReferencia();

        assertEquals(
                List.of("PosicionA", "PosicionB", "PlayaRecepcion",
                        "PosicionF1", "BandejaF1", "EstanteriaF1", "PasilloF1", "CamaraFrio", "Deposito"),
                recorrer(arbol::postOrder));
    }

    @Test
    void porNivelesVisitaNivelANivel() {
        // El recorrido por niveles se apoya en Cola (estructura lineal del primer hito).
        ArbolNario<String> arbol = depositoDeReferencia();

        assertEquals(
                List.of("Deposito", "PlayaRecepcion", "CamaraFrio",
                        "PosicionA", "PosicionB", "PasilloF1", "EstanteriaF1", "BandejaF1", "PosicionF1"),
                recorrer(arbol::porNiveles));
    }

    @Test
    void moverUnSectorCompletoConservaSuContenido() {
        // "Mover un sector completo, con todo su contenido, a otro punto de la
        // organización": se desprende PlayaRecepcion (con sus 2 posiciones) de la
        // raíz, y se reengancha colgando de CamaraFrio.
        ArbolNario<String> arbol = new ArbolNario<>();
        TDANodoNario<String> raiz = arbol.agregarHijo(null, "Deposito");
        TDANodoNario<String> playa = arbol.agregarHijo(raiz, "PlayaRecepcion");
        arbol.agregarHijo(playa, "PosicionA");
        arbol.agregarHijo(playa, "PosicionB");
        TDANodoNario<String> camara = arbol.agregarHijo(raiz, "CamaraFrio");

        TDANodoNario<String> desprendido = arbol.desprenderSubarbol(raiz, playa);

        assertEquals(playa, desprendido);
        assertEquals(1, raiz.cantidadHijosDirectos());
        assertEquals(2, playa.cantidadHijosDirectos(), "el subárbol desprendido conserva sus hijos");
        assertEquals(3, playa.cantidadNodos());

        arbol.engancharSubarbol(camara, desprendido);

        assertEquals(1, camara.cantidadHijosDirectos());
        assertEquals(5, arbol.cantidadNodos()); // Deposito, CamaraFrio, PlayaRecepcion, PosicionA, PosicionB
        assertEquals("PosicionA", arbol.buscar(s -> s.equals("PosicionA")).getDato());
    }

    @Test
    void desprenderUnNodoQueNoEsHijoDirectoDevuelveNull() {
        ArbolNario<String> arbol = depositoDeReferencia();
        TDANodoNario<String> raiz = arbol.obtenerRaiz();
        TDANodoNario<String> nieto = arbol.buscar(s -> s.equals("EstanteriaF1"));

        assertNull(arbol.desprenderSubarbol(raiz, nieto));
    }

    @Test
    void eliminarUnNodoIntermedioSeLlevaTodoSuSubarbol() {
        ArbolNario<String> arbol = depositoDeReferencia();

        assertTrue(arbol.eliminar(s -> s.equals("PasilloF1")));

        assertNull(arbol.buscar(s -> s.equals("PasilloF1")));
        assertNull(arbol.buscar(s -> s.equals("EstanteriaF1")));
        assertNull(arbol.buscar(s -> s.equals("BandejaF1")));
        assertNull(arbol.buscar(s -> s.equals("PosicionF1")));
        assertEquals(5, arbol.cantidadNodos()); // Deposito, PlayaRecepcion, PosicionA, PosicionB, CamaraFrio
    }

    @Test
    void eliminarUnValorInexistenteDevuelveFalse() {
        ArbolNario<String> arbol = depositoDeReferencia();

        assertFalse(arbol.eliminar(s -> s.equals("NoExiste")));
        assertEquals(9, arbol.cantidadNodos());
    }

    @Test
    void eliminarLaRaizVaciaElArbolPorCompletoYQuedaReutilizable() {
        ArbolNario<String> arbol = depositoDeReferencia();

        assertTrue(arbol.eliminar(s -> s.equals("Deposito")));

        assertTrue(arbol.esVacio());
        assertEquals(0, arbol.cantidadNodos());
        assertNull(arbol.obtenerRaiz());

        arbol.agregarHijo(null, "NuevoDeposito");
        assertEquals(1, arbol.cantidadNodos());
    }

    @Test
    void unArbolAnchoConMuchosHijosDirectosTieneAlturaDos() {
        ArbolNario<Integer> arbol = new ArbolNario<>();
        TDANodoNario<Integer> raiz = arbol.agregarHijo(null, 0);

        for (int i = 1; i <= 5000; i++) {
            arbol.agregarHijo(raiz, i);
        }

        assertEquals(5001, arbol.cantidadNodos());
        assertEquals(5000, arbol.cantidadHojas());
        assertEquals(2, arbol.altura());

        // El recorrido por niveles usa una Cola de capacidad fija dimensionada en
        // cantidadNodos(): con un árbol ancho, casi todos los nodos están en cola
        // a la vez. Este caso verifica que no se quede corta.
        int[] visitados = {0};
        arbol.porNiveles(v -> visitados[0]++);
        assertEquals(5001, visitados[0]);
    }

    @Test
    void unArbolDegeneradoEnCadenaLinealTieneAlturaIgualACantidadDeNodos() {
        ArbolNario<Integer> arbol = new ArbolNario<>();
        TDANodoNario<Integer> actual = arbol.agregarHijo(null, 0);

        for (int i = 1; i <= 2000; i++) {
            actual = arbol.agregarHijo(actual, i);
        }

        assertEquals(2001, arbol.cantidadNodos());
        assertEquals(2001, arbol.altura());
        assertEquals(1, arbol.cantidadHojas());
    }

    /**
     * Construye el mismo depósito que {@link #ramasDeProfundidadDistintaSeReflejanEnLaAlturaYElConteo}.
     */
    private static ArbolNario<String> depositoDeReferencia() {
        ArbolNario<String> arbol = new ArbolNario<>();
        TDANodoNario<String> raiz = arbol.agregarHijo(null, "Deposito");

        TDANodoNario<String> playa = arbol.agregarHijo(raiz, "PlayaRecepcion");
        arbol.agregarHijo(playa, "PosicionA");
        arbol.agregarHijo(playa, "PosicionB");

        TDANodoNario<String> camara = arbol.agregarHijo(raiz, "CamaraFrio");
        TDANodoNario<String> pasillo = arbol.agregarHijo(camara, "PasilloF1");
        TDANodoNario<String> estanteria = arbol.agregarHijo(pasillo, "EstanteriaF1");
        TDANodoNario<String> bandeja = arbol.agregarHijo(estanteria, "BandejaF1");
        arbol.agregarHijo(bandeja, "PosicionF1");

        return arbol;
    }

    private static List<String> recorrer(java.util.function.Consumer<java.util.function.Consumer<String>> recorrido) {
        List<String> visitados = new ArrayList<>();
        recorrido.accept(visitados::add);
        return visitados;
    }
}
