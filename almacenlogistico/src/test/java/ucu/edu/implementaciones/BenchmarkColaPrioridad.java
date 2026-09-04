package ucu.edu.implementaciones;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Compara el costo de vaciar una cola de prioridad respaldada por arreglo y
 * otra respaldada por lista doblemente enlazada.
 *
 * <p>No es una prueba unitaria: no afirma tiempos exactos, porque estos dependen
 * de la máquina. Su objetivo es mostrar la diferencia al medir exclusivamente
 * {@code quitaDeCola()} con las colas ya cargadas.</p>
 */
public final class BenchmarkColaPrioridad {

    private static final int CANTIDAD_POR_DEFECTO = 5_000;
    private static final int REPETICIONES = 5;

    private BenchmarkColaPrioridad() {
    }

    public static void main(String[] args) {
        int cantidad = args.length == 0 ? CANTIDAD_POR_DEFECTO : Integer.parseInt(args[0]);

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        long[] tiemposArray = new long[REPETICIONES];
        long[] tiemposDoble = new long[REPETICIONES];

        for (int i = 0; i < REPETICIONES; i++) {
            ColaPrioridad<Integer> colaArray = crearColaArray(cantidad);
            ColaPrioridadDobleEnlazada<Integer> colaDoble = crearColaDoble(cantidad);

            tiemposArray[i] = medirVaciado(colaArray);
            tiemposDoble[i] = medirVaciado(colaDoble);
        }

        long medianaArray = mediana(tiemposArray);
        long medianaDoble = mediana(tiemposDoble);

        System.out.println("Elementos: " + cantidad);
        System.out.println("Mediana al vaciar ColaPrioridad (array): "
                + medianaArray / 1_000_000.0 + " ms");
        System.out.println("Mediana al vaciar ColaPrioridadDobleEnlazada: "
                + medianaDoble / 1_000_000.0 + " ms");
        System.out.println("Relación array/doble: "
                + String.format("%.2f", (double) medianaArray / medianaDoble) + "x");
    }

    private static ColaPrioridad<Integer> crearColaArray(int cantidad) {
        ColaPrioridad<Integer> cola = new ColaPrioridad<>(Comparator.naturalOrder());
        cargarEnOrdenInverso(cola, cantidad);
        return cola;
    }

    private static ColaPrioridadDobleEnlazada<Integer> crearColaDoble(int cantidad) {
        ColaPrioridadDobleEnlazada<Integer> cola = new ColaPrioridadDobleEnlazada<>(
                Comparator.naturalOrder());
        cargarEnOrdenInverso(cola, cantidad);
        return cola;
    }

    private static void cargarEnOrdenInverso(ucu.edu.aed.tda.TDACola<Integer> cola, int cantidad) {
        // Cada nuevo valor queda al frente; al terminar ambas colas contienen 1..cantidad.
        for (int i = cantidad; i >= 1; i--) {
            cola.poneEnCola(i);
        }
    }

    private static long medirVaciado(ucu.edu.aed.tda.TDACola<Integer> cola) {
        long inicio = System.nanoTime();

        while (!cola.esVacio()) {
            cola.quitaDeCola();
        }

        return System.nanoTime() - inicio;
    }

    private static long mediana(long[] tiempos) {
        long[] copia = tiempos.clone();
        Arrays.sort(copia);
        return copia[copia.length / 2];
    }
}
