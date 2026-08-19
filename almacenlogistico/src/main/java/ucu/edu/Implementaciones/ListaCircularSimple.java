package ucu.edu.Implementaciones;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDALista;

/**
 * Implementación de una lista circular simplemente enlazada.
 *
 * @param <T> tipo de elementos almacenados
 */
public class ListaCircularSimple<T> implements TDALista<T> {

    /**
     * Nodo de la lista.
     */
    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    /**
     * Último nodo de la lista.
     *
     * <p>El siguiente nodo de ultimo es siempre el primero.</p>
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
            ultimo = nuevo;
        } else {
            nuevo.siguiente = ultimo.siguiente;
            ultimo.siguiente = nuevo;
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

        if (index == 0) {
            nuevo.siguiente = ultimo.siguiente;
            ultimo.siguiente = nuevo;
        } else {
            Nodo<T> anterior = nodoEn(index - 1);

            nuevo.siguiente = anterior.siguiente;
            anterior.siguiente = nuevo;
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

        Nodo<T> eliminado;

        if (tamaño == 1) {
            eliminado = ultimo;
            ultimo = null;
        } else if (index == 0) {
            eliminado = ultimo.siguiente;
            ultimo.siguiente = eliminado.siguiente;
        } else {
            Nodo<T> anterior = nodoEn(index - 1);
            eliminado = anterior.siguiente;
            anterior.siguiente = eliminado.siguiente;

            if (eliminado == ultimo) {
                ultimo = anterior;
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

        Nodo<T> anterior = ultimo;
        Nodo<T> actual = ultimo.siguiente;

        do {
            if (Objects.equals(actual.dato, elem)) {

                if (tamaño == 1) {
                    ultimo = null;
                } else {
                    anterior.siguiente = actual.siguiente;

                    if (actual == ultimo) {
                        ultimo = anterior;
                    }
                }

                tamaño--;
                return true;
            }

            anterior = actual;
            actual = actual.siguiente;

        } while (actual != ultimo.siguiente);

        return false;
    }

    @Override
    public boolean contiene(T elem) {
        return indiceDe(elem) != -1;
    }

    @Override
    public int indiceDe(T elem) {
        if (esVacio()) {
            return -1;
        }

        Nodo<T> actual = ultimo.siguiente;

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

        if (esVacio()) {
            return null;
        }

        Nodo<T> actual = ultimo.siguiente;

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

        ListaCircularSimple<T> resultado = new ListaCircularSimple<>();

        Nodo<T> actual = esVacio() ? null : ultimo.siguiente;

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

            if (nodoMinimo != resultado.nodoEn(i)) {
                T temp = resultado.nodoEn(i).dato;
                resultado.nodoEn(i).dato = nodoMinimo.dato;
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
        ultimo = null;
        tamaño = 0;
    }

    /**
     * Retorna el nodo ubicado en el índice indicado.
     */
    private Nodo<T> nodoEn(int index) {
        Nodo<T> actual = ultimo.siguiente;

        for (int i = 0; i < index; i++) {
            actual = actual.siguiente;
        }

        return actual;
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