package ucu.edu.implementaciones;

import java.util.function.Consumer;
import java.util.function.Predicate;

import ucu.edu.aed.tda.TDANodoNario;

/**
 * Nodo de un árbol general n-ario.
 *
 * <p>Los hijos se guardan en una {@link ListaArray}, reutilizando una de las
 * estructuras lineales del primer hito: se recorren enteros en cada operación
 * recursiva (preorden, postorden, conteos) y se agregan al final con frecuencia,
 * dos patrones de acceso donde el arreglo rinde bien.</p>
 *
 * @param <T> tipo del dato almacenado
 */
public class NodoNario<T> implements TDANodoNario<T> {

    private T dato;
    private NodoNario<T> padre;
    private final ListaArray<NodoNario<T>> hijos = new ListaArray<>();

    public NodoNario(T dato) {
        this.dato = dato;
    }

    @Override
    public T getDato() {
        return dato;
    }

    @Override
    public void setDato(T dato) {
        this.dato = dato;
    }

    /** Devuelve el padre del nodo, o {@code null} si es la raíz. */
    public NodoNario<T> getPadre() {
        return padre;
    }

    @Override
    public int cantidadHijosDirectos() {
        return hijos.tamaño();
    }

    @Override
    public NodoNario<T> obtenerHijo(int index) {
        return hijos.obtener(index);
    }

    @Override
    public boolean esHoja() {
        return hijos.esVacio();
    }

    /** Agrega {@code hijo} al final de la lista de hijos directos. */
    void agregarHijoDirecto(NodoNario<T> hijo) {
        hijo.padre = this;
        hijos.agregar(hijo);
    }

    /**
     * Quita {@code hijo} de la lista de hijos directos.
     *
     * @return {@code true} si {@code hijo} era un hijo directo y se quitó
     */
    boolean removerHijoDirecto(NodoNario<T> hijo) {
        for (int i = 0; i < hijos.tamaño(); i++) {
            if (hijos.obtener(i) == hijo) {
                hijos.remover(i);
                hijo.padre = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public NodoNario<T> buscar(Predicate<T> criterio) {
        if (criterio.test(dato)) {
            return this;
        }
        for (int i = 0; i < hijos.tamaño(); i++) {
            NodoNario<T> encontrado = hijos.obtener(i).buscar(criterio);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }

    @Override
    public void preOrder(Consumer<TDANodoNario<T>> consumidor) {
        consumidor.accept(this);
        for (int i = 0; i < hijos.tamaño(); i++) {
            hijos.obtener(i).preOrder(consumidor);
        }
    }

    @Override
    public void postOrder(Consumer<TDANodoNario<T>> consumidor) {
        for (int i = 0; i < hijos.tamaño(); i++) {
            hijos.obtener(i).postOrder(consumidor);
        }
        consumidor.accept(this);
    }

    @Override
    public int cantidadNodos() {
        int cantidad = 1;
        for (int i = 0; i < hijos.tamaño(); i++) {
            cantidad += hijos.obtener(i).cantidadNodos();
        }
        return cantidad;
    }

    @Override
    public int cantidadHojas() {
        if (esHoja()) {
            return 1;
        }
        int cantidad = 0;
        for (int i = 0; i < hijos.tamaño(); i++) {
            cantidad += hijos.obtener(i).cantidadHojas();
        }
        return cantidad;
    }

    @Override
    public int cantidadNodosInternos() {
        if (esHoja()) {
            return 0;
        }
        int cantidad = 1;
        for (int i = 0; i < hijos.tamaño(); i++) {
            cantidad += hijos.obtener(i).cantidadNodosInternos();
        }
        return cantidad;
    }

    @Override
    public int altura() {
        if (esHoja()) {
            return 1;
        }
        int maxima = 0;
        for (int i = 0; i < hijos.tamaño(); i++) {
            maxima = Math.max(maxima, hijos.obtener(i).altura());
        }
        return 1 + maxima;
    }
}
