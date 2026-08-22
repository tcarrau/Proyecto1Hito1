package ucu.edu.implementaciones;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDACola;
import ucu.edu.aed.tda.TDALista;

/**
 * Implementación de una cola con prioridad.
 *
 * <p>Respeta el mismo TDA que la cola común: lo único que cambia es la política de
 * salida. En vez de salir el que llegó primero (FIFO), sale el de mayor prioridad
 * según el comparador recibido en el constructor. Al compartir interfaz con
 * {@link Cola}, las dos son intercambiables donde se necesite un TDACola.</p>
 *
 * <p><b>Convención:</b> sale primero el elemento <i>menor</i> según el comparador.
 * Para priorizar por mayor valor (por ejemplo, la sucursal con más ventas) hay que
 * pasar el comparador invertido con {@code Comparator.reversed()}.</p>
 *
 * <p>Los elementos se mantienen ordenados en el momento de insertar, apoyándose en
 * una {@link ListaArray}. Se eligió la lista sobre arreglo porque buscar la posición
 * de inserción necesita acceso por índice: sobre una lista enlazada cada obtener
 * sería O(n) y el recorrido degeneraría a O(n²) por inserción. Con acceso O(1) el
 * costo de poneEnCola queda en O(n), mientras que frente es O(1).</p>
 *
 * <p>Entre elementos de igual prioridad se respeta el orden de llegada, porque la
 * inserción los ubica detrás de sus equivalentes.</p>
 *
 * @param <T> tipo de elementos almacenados
 */
public class ColaPrioridad<T> implements TDACola<T> {

    /**
     * Elementos de la cola, siempre ordenados por prioridad.
     */
    private final ListaArray<T> elementos = new ListaArray<>();

    /**
     * Criterio de prioridad. El menor según este comparador sale primero.
     */
    private final Comparator<? super T> comparador;

    public ColaPrioridad(Comparator<? super T> comparador) {
        this.comparador = Objects.requireNonNull(comparador, "El comparador no puede ser null");
    }

    //Métodos de TDACola

    @Override
    public T frente() {
        if (esVacio()) {
            throw new NoSuchElementException("La cola con prioridad está vacía");
        }

        return elementos.obtener(0);
    }

    @Override
    public boolean poneEnCola(T dato) {
        int posicion = 0;

        // Avanza mientras los elementos tengan prioridad mayor o igual que la del nuevo.
        // El <= 0 hace que el nuevo quede detrás de sus equivalentes y se respete el
        // orden de llegada entre elementos de la misma prioridad.
        while (posicion < elementos.tamaño()
                && comparador.compare(elementos.obtener(posicion), dato) <= 0) {
            posicion++;
        }

        elementos.agregar(posicion, dato);
        return true;
    }

    @Override
    public T quitaDeCola() {
        if (esVacio()) {
            throw new NoSuchElementException("La cola con prioridad está vacía");
        }

        return elementos.remover(0);
    }

    //Métodos de TDALista

    @Override
    public void agregar(T elem) {
        poneEnCola(elem);
    }

    /**
     * No se admite insertar en una posición elegida por quien llama: rompería el
     * invariante de que los elementos están siempre ordenados por prioridad.
     * Para agregar hay que usar {@code poneEnCola}, que ubica el elemento donde
     * le corresponde.
     */
    @Override
    public void agregar(int index, T elem) {
        throw new UnsupportedOperationException(
                "La posición la determina la prioridad: usar poneEnCola");
    }

    @Override
    public T obtener(int index) {
        return elementos.obtener(index);
    }

    @Override
    public T remover(int index) {
        return elementos.remover(index);
    }

    @Override
    public boolean remover(T elem) {
        return elementos.remover(elem);
    }

    @Override
    public boolean contiene(T elem) {
        return elementos.contiene(elem);
    }

    @Override
    public int indiceDe(T elem) {
        return elementos.indiceDe(elem);
    }

    @Override
    public T buscar(Predicate<T> criterio) {
        return elementos.buscar(criterio);
    }

    @Override
    public TDALista<T> ordenar(Comparator<T> comparator) {
        return elementos.ordenar(comparator);
    }

    @Override
    public int tamaño() {
        return elementos.tamaño();
    }

    @Override
    public boolean esVacio() {
        return elementos.esVacio();
    }

    @Override
    public void vaciar() {
        elementos.vaciar();
    }
}
