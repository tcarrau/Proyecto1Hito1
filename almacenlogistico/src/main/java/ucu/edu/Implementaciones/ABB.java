package ucu.edu.implementaciones;

import java.util.function.Consumer;

import ucu.edu.aed.tda.TDAArbolBinario;
import ucu.edu.aed.tda.TDAElemento;

public class ABB<T> implements TDAArbolBinario<T> {

    private Elemento<T> raiz;

      /* Busca y retorna el primer elemento que cumple con el predicado dado.
     *
     * <p>El recorrido del árbol para la búsqueda queda sujeto a la implementación.</p>
     *
     * @param predicate el predicado que define el criterio de búsqueda
     * @return el primer elemento que cumple el criterio, o {@code null}
     * si no existe ninguno
     */
    public T buscar(Comparable<T> predicate){
        if (raiz == null) {  
            return null;
        }

        Elemento<T> encontrado = raiz.buscar(predicate);
        return encontrado == null ? null : encontrado.getDato();
    }

    /**
     * Retorna el elemento raíz del árbol.
     *
     * @return el elemento raíz del árbol, o {@code null} si el árbol está vacío
     */
    public TDAElemento<T> obtenerRaiz(){
        return this.obtenerRaiz();
    }

    /**
     * Elimina el o los nodos según el criterio de búsqueda.
     *
     * @param criterioBusqueda el predicado que define qué elementos deben ser eliminados
     * @return {@code true} si al menos un elemento fue eliminado;
     * {@code false} en caso contrario
     */
    public boolean eliminar(Comparable<T> criterioBusqueda){
        if (raiz == null || raiz.buscar(criterioBusqueda) == null) {
            return false;
        }

        raiz = eliminarNodo(raiz, criterioBusqueda);
        return true;
    }

    /** Elimina el nodo y devuelve la raíz del subárbol actualizado. */
    private Elemento<T> eliminarNodo(Elemento<T> nodo, Comparable<T> criterioBusqueda) {
        int comparacion = criterioBusqueda.compareTo(nodo.getDato());

        if (comparacion < 0) {
            nodo.setHijoIzquierdo(eliminarNodo(nodo.getHijoIzquierdo(), criterioBusqueda));
            return nodo;
        }
        if (comparacion > 0) {
            nodo.setHijoDerecho(eliminarNodo(nodo.getHijoDerecho(), criterioBusqueda));
            return nodo;
        }

        // Sin hijo izquierdo: sirve tanto para hoja como para nodo con un hijo derecho.
        if (nodo.getHijoIzquierdo() == null) {
            return nodo.getHijoDerecho();
        }
        // Nodo con un único hijo izquierdo.
        if (nodo.getHijoDerecho() == null) {
            return nodo.getHijoIzquierdo();
        }

        // Dos hijos: reemplaza el dato por el menor del subárbol derecho.
        Elemento<T> sucesor = nodo.getHijoDerecho();
        while (sucesor.getHijoIzquierdo() != null) {
            sucesor = sucesor.getHijoIzquierdo();
        }
        nodo.setDato(sucesor.getDato());
        nodo.setHijoDerecho(eliminarMinimo(nodo.getHijoDerecho()));
        return nodo;
    }

    /** Devuelve el subárbol luego de quitar su nodo mínimo. */
    private Elemento<T> eliminarMinimo(Elemento<T> nodo) {
        if (nodo.getHijoIzquierdo() == null) {
            return nodo.getHijoDerecho();
        }

        nodo.setHijoIzquierdo(eliminarMinimo(nodo.getHijoIzquierdo()));
        return nodo;
    }

    /**
     * Agrega un dato al árbol.
     *
     * <p>
     * Si el dato ya existe en el árbol, no se agrega nuevamente.
     * </p>
     *
     * @param dato el elemento a insertar
     * @return {@code true} si el elemento fue agregado correctamente;
     *         {@code false} si el elemento ya existía y no fue agregado
     */
    public boolean insertar(Comparable<T> dato) {
        if (raiz == null) {
            Elemento<T> nuevo = new Elemento<>((T) dato);
            this.raiz = nuevo;

        }
        return raiz.insertar(dato);
    }

    /**
     * Recorre el árbol en in-order
     * {@snippet :
     * // ejemplo de uso
     * elemento.inOrder(dato ->{
     * // procesar dato
     * // esta función se llama tantas veces como nodos halla en el árbol
     * });
     * }
     */
    public void inOrder(Consumer<T> consumidor) {

    }

    /**
     * Recorre el árbol en pre-order
     * {@snippet :
     * // ejemplo de uso
     * elemento.preOrder(dato ->{
     * // procesar dato
     * // esta función se llama tantas veces como nodos halla en el árbol
     * });
     * }
     */
    public void preOrder(Consumer<T> consumidor) {

    }

    /**
     * Recorre el árbol en post-order
     * {@snippet :
     * // ejemplo de uso
     * elemento.postOrder(dato ->{
     * // procesar dato
     * // esta función se llama tantas veces como nodos halla en el árbol
     * });
     * }
     */
    public void postOrder(Consumer<T> consumidor) {

    }

    /**
     * Devuelve true si el árbol es vacío
     */
    public boolean esVacio() {
        if(raiz == null){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Devuelve la cantidad de nodos del árbol
     **/
    public int cantidadNodos() {
        return raiz == null ? 0 : raiz.cantidadNodos();
    }

    /**
     * Devuelve la cantidad de nodos que son hojas
     */
    public int cantidadHojas() {
        return raiz == null ? 0 : raiz.cantidadHojas();
    }

    /**
     * Devuelve la cantidad de nodos que NO son hojas
     */
    public int cantidadNodosInternos() {
        return raiz == null ? 0 : raiz.cantidadNodosInternos();
    }
}
