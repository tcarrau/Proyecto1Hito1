package ucu.edu.implementaciones;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import ucu.edu.aed.tda.TDAArbolNario;
import ucu.edu.aed.tda.TDANodoNario;

/**
 * Árbol general n-ario: cada nodo puede tener cualquier cantidad de hijos, y
 * ramas distintas pueden tener profundidades distintas.
 *
 * <p>Envoltorio liviano que solo sostiene la raíz; toda la lógica recursiva
 * (recorridos, conteos, altura, búsqueda) vive en {@link NodoNario}, siguiendo
 * el mismo patrón que {@code ArbolBinario} delega en {@code Elemento}.</p>
 *
 * <p>El recorrido por niveles se apoya en {@link Cola}, la estructura lineal
 * FIFO del primer hito: es la representación natural del orden en que hay que
 * visitar los nodos nivel a nivel.</p>
 *
 * @param <T> tipo de elementos almacenados
 */
public class ArbolNario<T> implements TDAArbolNario<T> {

    private NodoNario<T> raiz;

    @Override
    public TDANodoNario<T> obtenerRaiz() {
        return raiz;
    }

    @Override
    public boolean esVacio() {
        return raiz == null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TDANodoNario<T> agregarHijo(TDANodoNario<T> padre, T dato) {
        if (padre == null) {
            if (raiz != null) {
                throw new IllegalStateException(
                        "El árbol ya tiene raíz; para agregar hijos hay que indicar el nodo padre.");
            }
            raiz = new NodoNario<>(dato);
            return raiz;
        }

        NodoNario<T> padreConcreto = (NodoNario<T>) padre;
        NodoNario<T> nuevo = new NodoNario<>(dato);
        padreConcreto.agregarHijoDirecto(nuevo);
        return nuevo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TDANodoNario<T> desprenderSubarbol(TDANodoNario<T> padre, TDANodoNario<T> hijo) {
        Objects.requireNonNull(padre, "El padre no puede ser null");
        Objects.requireNonNull(hijo, "El hijo a desprender no puede ser null");

        NodoNario<T> padreConcreto = (NodoNario<T>) padre;
        NodoNario<T> hijoConcreto = (NodoNario<T>) hijo;

        return padreConcreto.removerHijoDirecto(hijoConcreto) ? hijoConcreto : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void engancharSubarbol(TDANodoNario<T> padre, TDANodoNario<T> subarbol) {
        Objects.requireNonNull(padre, "El padre no puede ser null");
        Objects.requireNonNull(subarbol, "El subárbol a enganchar no puede ser null");

        NodoNario<T> padreConcreto = (NodoNario<T>) padre;
        NodoNario<T> subarbolConcreto = (NodoNario<T>) subarbol;

        padreConcreto.agregarHijoDirecto(subarbolConcreto);
    }

    @Override
    public TDANodoNario<T> buscar(Predicate<T> criterio) {
        Objects.requireNonNull(criterio, "El criterio no puede ser null");

        return raiz == null ? null : raiz.buscar(criterio);
    }

    @Override
    public boolean eliminar(Predicate<T> criterio) {
        Objects.requireNonNull(criterio, "El criterio no puede ser null");

        if (raiz == null) {
            return false;
        }
        if (criterio.test(raiz.getDato())) {
            raiz = null;
            return true;
        }
        return eliminarEntreHijos(raiz, criterio);
    }

    /**
     * Busca, en preorden, un hijo directo o descendiente de {@code nodo} que
     * cumpla el criterio, y lo desprende de su padre inmediato.
     */
    private boolean eliminarEntreHijos(NodoNario<T> nodo, Predicate<T> criterio) {
        for (int i = 0; i < nodo.cantidadHijosDirectos(); i++) {
            NodoNario<T> hijo = nodo.obtenerHijo(i);

            if (criterio.test(hijo.getDato())) {
                nodo.removerHijoDirecto(hijo);
                return true;
            }
            if (eliminarEntreHijos(hijo, criterio)) {
                return true;
            }
        }
        return false;
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
    public void porNiveles(Consumer<T> consumidor) {
        if (raiz == null) {
            return;
        }

        // La cola nunca retiene más elementos que nodos tiene el árbol, así que
        // esa cantidad alcanza como capacidad para toda la recorrida.
        Cola<NodoNario<T>> pendientes = new Cola<>(cantidadNodos());
        pendientes.poneEnCola(raiz);

        while (!pendientes.esVacio()) {
            NodoNario<T> actual = pendientes.quitaDeCola();
            consumidor.accept(actual.getDato());

            for (int i = 0; i < actual.cantidadHijosDirectos(); i++) {
                pendientes.poneEnCola(actual.obtenerHijo(i));
            }
        }
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

    @Override
    public int altura() {
        return raiz == null ? 0 : raiz.altura();
    }
}
