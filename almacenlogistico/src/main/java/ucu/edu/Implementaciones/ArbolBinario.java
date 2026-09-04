package ucu.edu.implementaciones;

import java.util.function.Consumer;

import ucu.edu.aed.tda.TDAArbolBinario;
import ucu.edu.aed.tda.TDAElemento;

/**
 * Árbol binario de búsqueda sin balanceo automático.
 *
 * <p>Envoltorio liviano que solo sostiene la raíz; toda la lógica de inserción,
 * búsqueda, borrado y recorridos vive en {@link Elemento}, igual que
 * {@code ArbolAVL} delega en {@code NodoAVL}.</p>
 *
 * <p>A diferencia de {@code ArbolAVL}, este árbol no reequilibra su forma tras
 * insertar o eliminar. Insertar una secuencia ya ordenada lo degenera a una
 * cadena lineal (altura O(n)), mientras que la versión balanceada mantiene
 * altura O(log n) para la misma secuencia. Esa comparación es justamente el
 * caso de prueba que exige la letra del hito.</p>
 *
 * @param <T> tipo de elementos almacenados
 */
public class ArbolBinario<T> implements TDAArbolBinario<T> {

    private Elemento<T> raiz;

    @Override
    public T buscar(Comparable<T> predicate) {
        if (raiz == null) {
            return null;
        }

        Elemento<T> encontrado = raiz.buscar(predicate);
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
        // Elemento no puede "vaciarse a sí mismo" -- lo maneja el árbol.
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
            raiz = new Elemento<>((T) dato);
            return true;
        }

        return raiz.insertar(dato);
    }

    @Override
    public void inOrder(Consumer<T> consumidor) {
        if (raiz != null) {
            raiz.inOrder(nodo -> consumidor.accept(nodo.getDato()));
        }
    }

    @Override
    public void preOrder(Consumer<T> consumidor) {
        if (raiz != null) {
            raiz.preOrder(nodo -> consumidor.accept(nodo.getDato()));
        }
    }

    @Override
    public void postOrder(Consumer<T> consumidor) {
        if (raiz != null) {
            raiz.postOrder(nodo -> consumidor.accept(nodo.getDato()));
        }
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

    /**
     * Altura del árbol (0 si está vacío). No forma parte de {@link TDAArbolBinario},
     * pero se expone igual que en {@code ArbolAVL} para poder comparar directamente
     * la altura de las dos variantes ante la misma secuencia de inserción.
     */
    public int altura() {
        return raiz == null ? 0 : raiz.altura();
    }
}
