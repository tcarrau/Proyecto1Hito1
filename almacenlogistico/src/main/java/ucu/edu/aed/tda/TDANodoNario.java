package ucu.edu.aed.tda;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Modela un nodo de un árbol general n-ario.
 *
 * <p>A diferencia de {@link TDAElemento} (nodo de árbol binario, con hijo
 * izquierdo y derecho fijos), un nodo n-ario puede tener cualquier cantidad de
 * hijos, incluyendo cero. La implementación de esta estructura debe ser
 * recursiva.</p>
 *
 * @param <T> el tipo del dato almacenado en el nodo
 */
public interface TDANodoNario<T> {

    /**
     * Devuelve el dato almacenado en este nodo.
     */
    T getDato();

    /**
     * Actualiza el dato almacenado en este nodo.
     */
    void setDato(T dato);

    /**
     * Devuelve la cantidad de hijos directos de este nodo.
     */
    int cantidadHijosDirectos();

    /**
     * Devuelve el hijo directo ubicado en la posición indicada, en el orden en
     * que fueron agregados.
     *
     * @throws IndexOutOfBoundsException si el índice no corresponde a un hijo existente
     */
    TDANodoNario<T> obtenerHijo(int index);

    /**
     * Determina si este nodo no tiene hijos.
     */
    boolean esHoja();

    /**
     * Busca, en este nodo y en todo su subárbol, el primer nodo (según un
     * recorrido en preorden) cuyo dato cumpla el criterio dado.
     *
     * @return el nodo encontrado, o {@code null} si ninguno cumple el criterio
     */
    TDANodoNario<T> buscar(Predicate<T> criterio);

    /**
     * Recorre este nodo y todo su subárbol en preorden: primero este nodo,
     * después cada uno de sus hijos (y sus respectivos subárboles) en el orden
     * en que fueron agregados.
     */
    void preOrder(Consumer<TDANodoNario<T>> consumidor);

    /**
     * Recorre este nodo y todo su subárbol en postorden: primero cada uno de
     * sus hijos (y sus respectivos subárboles), después este nodo.
     */
    void postOrder(Consumer<TDANodoNario<T>> consumidor);

    /**
     * Devuelve la cantidad de nodos de este subárbol, incluyendo este nodo.
     */
    int cantidadNodos();

    /**
     * Devuelve la cantidad de nodos hoja de este subárbol.
     */
    int cantidadHojas();

    /**
     * Devuelve la cantidad de nodos internos (no hoja) de este subárbol,
     * incluyendo este nodo si no es hoja.
     */
    int cantidadNodosInternos();

    /**
     * Devuelve la altura de este subárbol: 1 si este nodo es hoja, o
     * 1 + la altura máxima entre sus hijos en caso contrario.
     */
    int altura();
}
