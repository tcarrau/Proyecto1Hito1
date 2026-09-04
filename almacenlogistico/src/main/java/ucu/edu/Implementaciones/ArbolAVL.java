package ucu.edu.implementaciones;

import java.util.function.Consumer;
import ucu.edu.aed.tda.TDAArbolBinarioBusqueda;
import ucu.edu.aed.tda.TDAElemento;

/**
 * Árbol binario de búsqueda balanceado (AVL).
 *
 * <p>Envoltorio liviano que solo sostiene la raíz; toda la lógica de inserción,
 * búsqueda, borrado y balanceo vive en {@link NodoAVL}, igual que {@code ArbolBinario}
 * delega en {@code Elemento}.</p>
 */
public class ArbolAVL<T> implements TDAArbolBinarioBusqueda<T> {
    private NodoAVL<T> raiz;

    @Override
    public T buscar(Comparable<T> predicate) {
        if (raiz == null) return null;
        NodoAVL<T> encontrado = raiz.buscar(predicate);
        return encontrado == null ? null : encontrado.getDato();
    }

    @Override
    public TDAElemento<T> obtenerRaiz() {
        return raiz;
    }

    @Override
    public boolean eliminar(Comparable<T> criterioBusqueda) {
        if (raiz == null) {
            return false;
        }

        // Caso especial: si lo que hay que borrar es la raíz y no tiene hijos,
        // NodoAVL no puede "vaciarse a sí mismo" -- lo maneja el árbol.
        if (criterioBusqueda.compareTo(raiz.getDato()) == 0 && raiz.esHoja()) {
            raiz = null;
            return true;
        }

        return raiz.eliminar(criterioBusqueda) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean insertar(Comparable<T> dato) {
        if (raiz == null) {
            raiz = new NodoAVL<>((T) dato);
            return true;
        }
        return raiz.insertar(dato);
    }

    @Override
    public void inOrder(Consumer<T> consumidor) {
        if (raiz != null) raiz.inOrder(nodo -> consumidor.accept(nodo.getDato()));
    }

    @Override
    public void preOrder(Consumer<T> consumidor) {
        if (raiz != null) raiz.preOrder(nodo -> consumidor.accept(nodo.getDato()));
    }

    @Override
    public void postOrder(Consumer<T> consumidor) {
        if (raiz != null) raiz.postOrder(nodo -> consumidor.accept(nodo.getDato()));
    }

    @Override
    public boolean esVacio() {
        return raiz == null;
    }

    @Override
    public int cantidadNodos() {
        return raiz == null ? 0 : raiz.cantidadNodos();
    }

    @Override
    public int cantidadHojas() {
        return raiz == null ? 0 : raiz.cantidadHojas();
    }

    @Override
    public int cantidadNodosInternos() {
        return raiz == null ? 0 : raiz.cantidadNodosInternos();
    }

    /** Altura del árbol completo (0 si está vacío). Conveniencia, no es de la interfaz. */
    public int altura() {
        return raiz == null ? 0 : raiz.altura();
    }

    /**
     * Recorrido por niveles (BFS): visita la raíz, después todos los nodos de
     * profundidad 1, después los de profundidad 2, etc.
     *
     * <p>No es parte de {@code TDAArbolBinario}; se apoya en {@link Cola}, la
     * estructura del Hito 1, tal como pide la integración.</p>
     */
    public void porNiveles(Consumer<T> consumidor) {
        if (raiz == null) return;

        Cola<NodoAVL<T>> pendientes = new Cola<>(cantidadNodos());
        pendientes.poneEnCola(raiz);

        while (!pendientes.esVacia()) {
            NodoAVL<T> actual = pendientes.quitaDeCola();
            consumidor.accept(actual.getDato());

            if (actual.getHijoIzquierdo() != null) pendientes.poneEnCola(actual.getHijoIzquierdo());
            if (actual.getHijoDerecho() != null) pendientes.poneEnCola(actual.getHijoDerecho());
        }
    }
}
