package ucu.edu.aed.tda;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Define un Tipo de Dato Abstracto (TDA) Árbol General N-ario.
 *
 * <p>Un árbol n-ario es una estructura de datos jerárquica en la que cada nodo
 * puede tener cualquier cantidad de hijos (cero, uno o varios), y donde ramas
 * distintas pueden tener profundidades distintas. A diferencia de
 * {@link TDAArbolBinario}, no hay un límite fijo de dos hijos por nodo.</p>
 *
 * <p>No se ofrece recorrido inorden: ese recorrido (izquierda, raíz, derecha)
 * depende de que cada nodo tenga exactamente dos hijos para intercalarse entre
 * ellos, algo que no generaliza a un nodo con una cantidad arbitraria de hijos.
 * En su lugar, además de preorden y postorden, se ofrece recorrido por niveles,
 * que sí tiene sentido para cualquier árbol.</p>
 *
 * @param <T> el tipo de los elementos almacenados en el árbol
 */
public interface TDAArbolNario<T> {

    /**
     * Retorna el nodo raíz del árbol.
     *
     * @return el nodo raíz, o {@code null} si el árbol está vacío
     */
    TDANodoNario<T> obtenerRaiz();

    /**
     * Determina si el árbol no contiene elementos.
     */
    boolean esVacio();

    /**
     * Agrega un nuevo nodo hoja con el dato indicado como hijo de {@code padre}.
     *
     * <p>Si el árbol está vacío, {@code padre} debe ser {@code null} y el nuevo
     * nodo se convierte en la raíz.</p>
     *
     * @param padre el nodo al que se le agrega el hijo, o {@code null} para
     *              establecer la raíz de un árbol vacío
     * @param dato  el dato del nuevo nodo
     * @return el nodo recién creado
     * @throws IllegalStateException si el árbol ya tiene raíz y {@code padre} es {@code null}
     */
    TDANodoNario<T> agregarHijo(TDANodoNario<T> padre, T dato);

    /**
     * Desprende el subárbol que cuelga de {@code hijo} de la lista de hijos de
     * {@code padre}, sin alterar la descendencia de {@code hijo}.
     *
     * <p>El nodo devuelto queda listo para engancharse en otro punto del árbol
     * (o de otro árbol) mediante {@link #engancharSubarbol}. Es la primitiva que
     * sostiene mover un sector completo, con todo su contenido, a otro lugar de
     * la organización.</p>
     *
     * @param padre nodo del que se desprende el hijo; no puede ser {@code null}
     * @param hijo  el hijo directo de {@code padre} a desprender
     * @return el subárbol desprendido, o {@code null} si {@code hijo} no era un
     *         hijo directo de {@code padre}
     */
    TDANodoNario<T> desprenderSubarbol(TDANodoNario<T> padre, TDANodoNario<T> hijo);

    /**
     * Engancha un subárbol ya existente (con toda su descendencia intacta) como
     * hijo de {@code padre}.
     *
     * @param padre    nodo al que se engancha el subárbol; no puede ser {@code null}
     * @param subarbol el subárbol a enganchar, típicamente obtenido de una
     *                 llamada previa a {@link #desprenderSubarbol}
     */
    void engancharSubarbol(TDANodoNario<T> padre, TDANodoNario<T> subarbol);

    /**
     * Busca el primer nodo, según un recorrido en preorden, cuyo dato cumpla el
     * criterio dado.
     *
     * @return el nodo encontrado, o {@code null} si el árbol está vacío o
     *         ningún nodo cumple el criterio
     */
    TDANodoNario<T> buscar(Predicate<T> criterio);

    /**
     * Elimina el primer nodo, según un recorrido en preorden, cuyo dato cumpla
     * el criterio dado, junto con todo su subárbol.
     *
     * <p>Si el nodo eliminado es la raíz, el árbol queda vacío.</p>
     *
     * @return {@code true} si se encontró y eliminó un nodo; {@code false} en
     *         caso contrario
     */
    boolean eliminar(Predicate<T> criterio);

    /**
     * Recorre el árbol en preorden: la raíz, y después cada uno de sus hijos
     * (y sus respectivos subárboles) en el orden en que fueron agregados.
     */
    void preOrder(Consumer<T> consumidor);

    /**
     * Recorre el árbol en postorden: primero los hijos de la raíz (y sus
     * respectivos subárboles), y por último la raíz.
     */
    void postOrder(Consumer<T> consumidor);

    /**
     * Recorre el árbol por niveles (de la raíz hacia las hojas, y dentro de un
     * mismo nivel de izquierda a derecha).
     */
    void porNiveles(Consumer<T> consumidor);

    /**
     * Devuelve la cantidad de nodos del árbol.
     */
    int cantidadNodos();

    /**
     * Devuelve la cantidad de nodos hoja del árbol.
     */
    int cantidadHojas();

    /**
     * Devuelve la cantidad de nodos internos (no hoja) del árbol.
     */
    int cantidadNodosInternos();

    /**
     * Devuelve la altura del árbol (0 si está vacío).
     */
    int altura();
}
