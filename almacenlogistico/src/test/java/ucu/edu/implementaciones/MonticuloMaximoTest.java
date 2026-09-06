package ucu.edu.implementaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MonticuloMaximoTest {

    @Test
    void conservaElMayorElementoEnLaRaiz() {
        MonticuloMaximo<Integer> monticulo = new MonticuloMaximo<>();
        monticulo.agregar(3);
        monticulo.agregar(1);
        monticulo.agregar(4);
        monticulo.agregar(2);

        assertEquals(4, monticulo.obtenerMaximo());
        assertEquals(4, monticulo.quitarMaximo());
        assertEquals(3, monticulo.obtenerMaximo());
    }
}
