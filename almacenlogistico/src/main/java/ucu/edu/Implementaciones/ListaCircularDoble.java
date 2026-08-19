package ucu.edu.Implementaciones;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDALista;

/**
 * Implementación de una lista circular doblemente enlazada.
 *
 * @param <T> tipo de elementos almacenados
 */
public class ListaCircularDoble<T> implements TDALista<T> {

    /**
     * Nodo de la lista.
     */
    private static class Nodo<T> {
        T dato;
        Nodo<T> anterior;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    /**
     * Primer nodo de la lista.
     */
    private Nodo<T> primero;

    /**
     * Último nodo de la lista.
     */
    private Nodo<T> ultimo;

    /**
     * Cantidad de elementos de la lista.
     */
    private int tamaño;

    @Override
    public void agregar(T elem) {
        Nodo<T> nuevo = new Nodo<>(elem);

        if (esVacio()) {
            nuevo.siguiente = nuevo;
            nuevo.anterior = nuevo;

            primero = nuevo;
            ultimo = nuevo;
        } else {
            nuevo.anterior = ultimo;
            nuevo.siguiente = primero;

            ultimo.siguiente = nuevo;
            primero.anterior = nuevo;

            ultimo = nuevo;
        }

        tamaño++;
    }

    @Override
    public void agregar(int index, T elem) {
        if (index < 0 || index > tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }

        if (index == tamaño) {
            agregar(elem);
            return;
        }

        Nodo<T> nuevo = new Nodo<>(elem);
        Nodo<T> actual = nodoEn(index);

        nuevo.anterior = actual.anterior;
        nuevo.siguiente = actual;

        actual.anterior.siguiente = nuevo;
        actual.anterior = nuevo;

        if (index == 0) {
            primero = nuevo;
        }

        tamaño++;
    }

    @Override
    public T obtener(int index) {
        validarIndice(index);

        return nodoEn(index).dato;
    }

    @Override
    public T remover(int index) {
        validarIndice(index);

        Nodo<T> eliminado = nodoEn(index);

        if (tamaño == 1) {
            primero = null;
            ultimo = null;
        } else {
            eliminado.anterior.siguiente = eliminado.siguiente;
            eliminado.siguiente.anterior = eliminado.anterior;

            if (eliminado == primero) {
                primero = eliminado.siguiente;
            }

            if (eliminado == ultimo) {
                ultimo = eliminado.anterior;
            }
        }

        tamaño--;

        return eliminado.dato;
    }

    @Override
    public boolean remover(T elem) {
        if (esVacio()) {
            return false;
        }

        Nodo<T> actual = primero;

        for (int i = 0; i < tamaño; i++) {
            if (Objects.equals(actual.dato, elem)) {

                if (tamaño == 1) {
                    primero = null;
                    ultimo = null;
                } else {
                    actual.anterior.siguiente = actual.siguiente;
                    actual.siguiente.anterior = actual.anterior;

                    if (actual == primero) {
                        primero = actual.siguiente;
                    }

                    if (actual == ultimo) {
                        ultimo = actual.anterior;
                    }
                }

                tamaño--;
                return true;
            }

            actual = actual.siguiente;
        }

        return false;
    }

    @Override
    public boolean contiene(T elem) {
        return indiceDe(elem) != -1;
    }

    @Override
    public int indiceDe(T elem) {
        Nodo<T> actual = primero;

        for (int i = 0; i < tamaño; i++) {
            if (Objects.equals(actual.dato, elem)) {
                return i;
            }

            actual = actual.siguiente;
        }

        return -1;
    }

    @Override
    public T buscar(Predicate<T> criterio) {
        if (criterio == null) {
            throw new IllegalArgumentException("El criterio no puede ser null");
        }

        Nodo<T> actual = primero;

        for (int i = 0; i < tamaño; i++) {
            if (criterio.test(actual.dato)) {
                return actual.dato;
            }

            actual = actual.siguiente;
        }

        return null;
    }

    @Override
    public TDALista<T> ordenar(Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("El comparator no puede ser null");
        }

        ListaCircularDoble<T> resultado = new ListaCircularDoble<>();

        Nodo<T> actual = primero;

        for (int i = 0; i < tamaño; i++) {
            resultado.agregar(actual.dato);
            actual = actual.siguiente;
        }

        // Ordenamiento por selección.
        for (int i = 0; i < resultado.tamaño; i++) {
            Nodo<T> nodoMinimo = resultado.nodoEn(i);
            Nodo<T> nodoActual = nodoMinimo.siguiente;

            for (int j = i + 1; j < resultado.tamaño; j++) {
                if (comparator.compare(nodoActual.dato, nodoMinimo.dato) < 0) {
                    nodoMinimo = nodoActual;
                }

                nodoActual = nodoActual.siguiente;
            }

            Nodo<T> nodoPosicion = resultado.nodoEn(i);

            if (nodoMinimo != nodoPosicion) {
                T temp = nodoPosicion.dato;
                nodoPosicion.dato = nodoMinimo.dato;
                nodoMinimo.dato = temp;
            }
        }

        return resultado;
    }

    @Override
    public int tamaño() {
        return tamaño;
    }

    @Override
    public boolean esVacio() {
        return tamaño == 0;
    }

    @Override
    public void vaciar() {
        primero = null;
        ultimo = null;
        tamaño = 0;
    }

    /**
     * Busca el nodo correspondiente al índice.
     *
     * <p>Al ser una lista doblemente enlazada, se decide si conviene
     * recorrer desde el principio o desde el final.</p>
     */
    private Nodo<T> nodoEn(int index) {
        if (index < tamaño / 2) {
            Nodo<T> actual = primero;

            for (int i = 0; i < index; i++) {
                actual = actual.siguiente;
            }

            return actual;
        } else {
            Nodo<T> actual = ultimo;

            for (int i = tamaño - 1; i > index; i--) {
                actual = actual.anterior;
            }

            return actual;
        }
    }

    /**
     * Valida un índice que debe corresponder a un elemento existente.
     */
    private void validarIndice(int index) {
        if (index < 0 || index >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
    }
}