package ucu.edu.implementaciones;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDACola;
import ucu.edu.aed.tda.TDALista;

/**
 * Cola con prioridad mantenida ordenada sobre una lista doblemente enlazada.
 *
 * <p>El elemento menor según el comparador queda al frente. Insertar requiere
 * recorrer la lista para hallar su posición, por lo que cuesta O(n), pero quitar
 * el frente cuesta O(1): se desenlaza el primer nodo sin desplazar elementos.</p>
 *
 * @param <T> tipo de elementos almacenados
 */
public class ColaPrioridadDobleEnlazada<T> implements TDACola<T> {

    private final ListaDoblementeEnlazada<T> elementos = new ListaDoblementeEnlazada<>();
    private final Comparator<? super T> comparador;

    public ColaPrioridadDobleEnlazada(Comparator<? super T> comparador) {
        this.comparador = Objects.requireNonNull(comparador, "El comparador no puede ser null");
    }

    @Override
    public T frente() {
        if (esVacio()) {
            throw new NoSuchElementException("La cola con prioridad está vacía");
        }
        return elementos.obtener(0);
    }

    @Override
    public boolean poneEnCola(T dato) {
        elementos.agregarOrdenado(dato, comparador);
        return true;
    }

    @Override
    public T quitaDeCola() {
        if (esVacio()) {
            throw new NoSuchElementException("La cola con prioridad está vacía");
        }
        return elementos.remover(0);
    }

    @Override
    public void agregar(T elem) {
        poneEnCola(elem);
    }

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
    public TDALista<T> ordenar(Comparator<T> comparador) {
        return elementos.ordenar(comparador);
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
