package ucu.edu.Implementaciones;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDALista;

/**
 * Implementacion de {@link TDALista} basada en un arreglo redimensionable.
 *
 * @param <T> tipo de los elementos almacenados
 */
public class ListaArray<T> implements TDALista<T> {
    private T[] lista;
    private int proximo;

    @SuppressWarnings("unchecked")
    public ListaArray(int capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa");
        }
        lista = (T[]) new Object[capacidad];
        proximo = 0;
    }

    @SuppressWarnings("unchecked")
    private void asegurarCapacidad() {
        if (proximo < lista.length) {
            return;
        }

        int nuevaCapacidad = lista.length == 0 ? 1 : lista.length * 2;
        T[] nuevaLista = (T[]) new Object[nuevaCapacidad];
        for (int i = 0; i < proximo; i++) {
            nuevaLista[i] = lista[i];
        }
        lista = nuevaLista;
    }

    private void validarIndiceElemento(int index) {
        if (index < 0 || index >= proximo) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + index);
        }
    }

    private void validarIndiceInsercion(int index) {
        if (index < 0 || index > proximo) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + index);
        }
    }

    @Override
    public void agregar(T elem) {
        asegurarCapacidad();
        lista[proximo] = elem;
        proximo++;
    }

    @Override
    public void agregar(int index, T elem) {
        validarIndiceInsercion(index);
        asegurarCapacidad();

        for (int i = proximo; i > index; i--) {
            lista[i] = lista[i - 1];
        }
        lista[index] = elem;
        proximo++;
    }

    @Override
    public T obtener(int index) {
        validarIndiceElemento(index);
        return lista[index];
    }

    @Override
    public T remover(int index) {
        validarIndiceElemento(index);
        T removido = lista[index];

        for (int i = index; i < proximo - 1; i++) {
            lista[i] = lista[i + 1];
        }
        lista[--proximo] = null;
        return removido;
    }

    @Override
    public boolean remover(T elem) {
        int index = indiceDe(elem);
        if (index == -1) {
            return false;
        }
        remover(index);
        return true;
    }

    @Override
    public boolean contiene(T elem) {
        return indiceDe(elem) != -1;
    }

    @Override
    public int indiceDe(T elem) {
        for (int i = 0; i < proximo; i++) {
            if (Objects.equals(lista[i], elem)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public T buscar(Predicate<T> criterio) {
        Objects.requireNonNull(criterio, "El criterio no puede ser null");
        for (int i = 0; i < proximo; i++) {
            if (criterio.test(lista[i])) {
                return lista[i];
            }
        }
        return null;
    }

    @Override
    public TDALista<T> ordenar(Comparator<T> comparator) {
        Objects.requireNonNull(comparator, "El comparador no puede ser null");
        ListaArray<T> ordenada = new ListaArray<>(proximo);
        for (int i = 0; i < proximo; i++) {
            ordenada.agregar(lista[i]);
        }

        // Ordenamiento por insercion sobre la copia.
        for (int i = 1; i < ordenada.proximo; i++) {
            T actual = ordenada.lista[i];
            int j = i - 1;
            while (j >= 0 && comparator.compare(ordenada.lista[j], actual) > 0) {
                ordenada.lista[j + 1] = ordenada.lista[j];
                j--;
            }
            ordenada.lista[j + 1] = actual;
        }
        return ordenada;
    }

    @Override
    public int tamaño() {
        return proximo;
    }

    @Override
    public boolean esVacio() {
        return proximo == 0;
    }

    @Override
    public void vaciar() {
        for (int i = 0; i < proximo; i++) {
            lista[i] = null;
        }
        proximo = 0;
    }
}
