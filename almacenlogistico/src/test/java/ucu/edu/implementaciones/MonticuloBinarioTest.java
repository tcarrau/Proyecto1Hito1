package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class MonticuloBinarioTest {

    @Test
    void elMinimoSiempreQuedaEnLaRaizYSeExtraeOrdenado() {
        MonticuloBinario<Integer> monticulo = new MonticuloBinario<>();
        agregar(monticulo, 8, 3, 10, 1, 6, 14, 4, 7, 13);

        assertEquals(1, monticulo.getMinimo().getDato());
        assertPropiedadDeMonticuloMinimo(monticulo);

        int[] esperados = {1, 3, 4, 6, 7, 8, 10, 13, 14};
        for (int esperado : esperados) {
            assertEquals(esperado, monticulo.quitarMinimo().getDato());
            assertPropiedadDeMonticuloMinimo(monticulo);
        }
        assertTrue(monticulo.esVacio());
    }

    @Test
    void reemplazarUnElementoReordenaElMonticulo() {
        MonticuloBinario<Integer> monticulo = new MonticuloBinario<>();
        agregar(monticulo, 5, 7, 9);

        monticulo.setElemento(2, new Elemento<>(1));

        assertEquals(1, monticulo.getMinimo().getDato());
        assertEquals(1, monticulo.quitarMinimo().getDato());
        assertEquals(5, monticulo.quitarMinimo().getDato());
        assertEquals(7, monticulo.quitarMinimo().getDato());
    }

    @Test
    void consultarOQuitarElMinimoDeUnMonticuloVacioLanzaExcepcion() {
        MonticuloBinario<Integer> monticulo = new MonticuloBinario<>();

        assertThrows(NoSuchElementException.class, monticulo::getMinimo);
        assertThrows(NoSuchElementException.class, monticulo::quitarMinimo);
    }

    private void agregar(MonticuloBinario<Integer> monticulo, int... valores) {
        for (int valor : valores) {
            monticulo.agregar(new Elemento<>(valor));
        }
    }

    private void assertPropiedadDeMonticuloMinimo(MonticuloBinario<Integer> monticulo) {
        for (int indice = 1; indice < monticulo.getTamaño(); indice++) {
            int padre = (indice - 1) / 2;
            assertTrue(monticulo.getElemento(padre).getDato()
                    <= monticulo.getElemento(indice).getDato());
        }
    }
}
