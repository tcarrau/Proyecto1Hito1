package ucu.edu.Implementaciones;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDALista;

/**
 * Implementación de una lista doblemente enlazada.
 *
 * <p>Cada nodo conoce a su sucesor y a su predecesor. Tener el enlace hacia atrás
 * permite quitar un nodo en O(1) una vez ubicado, y recorrer la lista desde el
 * extremo más cercano al índice buscado.</p>
 *
 * @param <T> tipo de elementos almacenados
 */
public class ListaDoblementeEnlazada<T> implements TDALista<T> {

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
            primero = nuevo;
        } else {
            nuevo.anterior = ultimo;
            ultimo.siguiente = nuevo;
        }

        ultimo = nuevo;
        tamaño++;
    }

    @Override
    public void agregar(int index, T elem) {
        validarIndiceInsercion(index);

        if (index == tamaño) {
            agregar(elem);
            return;
        }

        // Como index < tamaño, siempre existe un nodo que quedará después del nuevo.
        Nodo<T> siguiente = nodoEn(index);
        Nodo<T> anterior = siguiente.anterior;
        Nodo<T> nuevo = new Nodo<>(elem);

        nuevo.anterior = anterior;
        nuevo.siguiente = siguiente;
        siguiente.anterior = nuevo;

        if (anterior == null) {
            primero = nuevo;
        } else {
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

        return desenlazar(nodoEn(index));
    }

    @Override
    public boolean remover(T elem) {
        for (Nodo<T> actual = primero; actual != null; actual = actual.siguiente) {
            if (Objects.equals(actual.dato, elem)) {
                desenlazar(actual);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contiene(T elem) {
        return indiceDe(elem) != -1;
    }

    @Override
    public int indiceDe(T elem) {
        int indice = 0;

        for (Nodo<T> actual = primero; actual != null; actual = actual.siguiente) {
            if (Objects.equals(actual.dato, elem)) {
                return indice;
            }

            indice++;
        }

        return -1;
    }

    @Override
    public T buscar(Predicate<T> criterio) {
        Objects.requireNonNull(criterio, "El criterio no puede ser null");

        for (Nodo<T> actual = primero; actual != null; actual = actual.siguiente) {
            if (criterio.test(actual.dato)) {
                return actual.dato;
            }
        }

        return null;
    }

    @Override
    public TDALista<T> ordenar(Comparator<T> comparator) {
        Objects.requireNonNull(comparator, "El comparador no puede ser null");

        ListaDoblementeEnlazada<T> resultado = new ListaDoblementeEnlazada<>();

        for (Nodo<T> actual = primero; actual != null; actual = actual.siguiente) {
            resultado.agregar(actual.dato);
        }

        // Ordenamiento por mezcla: O(n log n) siguiendo solo los enlaces hacia adelante.
        resultado.primero = ordenarPorMezcla(resultado.primero, comparator);

        // La mezcla dejó los enlaces hacia atrás y ultimo desactualizados: se rehacen en una pasada.
        resultado.reconstruirEnlacesHaciaAtras();

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
     * Quita el nodo dado de la cadena y retorna su dato.
     *
     * <p>Es O(1): al tener el enlace hacia atrás no hace falta buscar el predecesor.
     * Ésta es la ventaja concreta frente a la lista simplemente enlazada, donde
     * eliminar el último elemento obliga a recorrer toda la lista.</p>
     */
    private T desenlazar(Nodo<T> nodo) {
        Nodo<T> anterior = nodo.anterior;
        Nodo<T> siguiente = nodo.siguiente;

        if (anterior == null) {
            primero = siguiente;
        } else {
            anterior.siguiente = siguiente;
        }

        if (siguiente == null) {
            ultimo = anterior;
        } else {
            siguiente.anterior = anterior;
        }

        nodo.anterior = null;
        nodo.siguiente = null;
        tamaño--;

        return nodo.dato;
    }

    /**
     * Ordena por mezcla la cadena que arranca en el nodo dado y retorna la nueva cabeza.
     *
     * <p>Trabaja únicamente con el enlace siguiente; los enlaces hacia atrás se
     * reconstruyen después con {@code reconstruirEnlacesHaciaAtras}.</p>
     */
    private Nodo<T> ordenarPorMezcla(Nodo<T> cabeza, Comparator<T> comparator) {
        // Una cadena de 0 o 1 nodos ya está ordenada.
        if (cabeza == null || cabeza.siguiente == null) {
            return cabeza;
        }

        Nodo<T> finPrimeraMitad = buscarFinDePrimeraMitad(cabeza);
        Nodo<T> segundaMitad = finPrimeraMitad.siguiente;

        // Corta la cadena en dos listas independientes.
        finPrimeraMitad.siguiente = null;

        return mezclar(
                ordenarPorMezcla(cabeza, comparator),
                ordenarPorMezcla(segundaMitad, comparator),
                comparator);
    }

    /**
     * Retorna el último nodo de la primera mitad de la cadena.
     *
     * <p>Usa dos punteros: uno avanza de a un nodo y el otro de a dos, de modo que
     * cuando el rápido llega al final el lento quedó en la mitad. El rápido arranca
     * una posición adelantado para que una cadena de dos nodos se parta 1-1 y la
     * recursión siempre progrese.</p>
     */
    private Nodo<T> buscarFinDePrimeraMitad(Nodo<T> cabeza) {
        Nodo<T> lento = cabeza;
        Nodo<T> rapido = cabeza.siguiente;

        while (rapido != null && rapido.siguiente != null) {
            lento = lento.siguiente;
            rapido = rapido.siguiente.siguiente;
        }

        return lento;
    }

    /**
     * Intercala dos cadenas ya ordenadas en una sola, reenganchando los punteros.
     */
    private Nodo<T> mezclar(Nodo<T> izquierda, Nodo<T> derecha, Comparator<T> comparator) {
        // Nodo centinela: evita tratar aparte el primer enganche. Se descarta al final.
        Nodo<T> centinela = new Nodo<>(null);
        Nodo<T> cola = centinela;

        while (izquierda != null && derecha != null) {
            // El <= mantiene estable el orden de los elementos equivalentes.
            if (comparator.compare(izquierda.dato, derecha.dato) <= 0) {
                cola.siguiente = izquierda;
                izquierda = izquierda.siguiente;
            } else {
                cola.siguiente = derecha;
                derecha = derecha.siguiente;
            }

            cola = cola.siguiente;
        }

        // A lo sumo una de las dos cadenas quedó con elementos: se engancha entera.
        cola.siguiente = (izquierda != null) ? izquierda : derecha;

        return centinela.siguiente;
    }

    /**
     * Recorre la cadena hacia adelante rehaciendo cada enlace anterior y dejando
     * ultimo apuntando al final.
     */
    private void reconstruirEnlacesHaciaAtras() {
        Nodo<T> anterior = null;
        Nodo<T> actual = primero;

        while (actual != null) {
            actual.anterior = anterior;
            anterior = actual;
            actual = actual.siguiente;
        }

        ultimo = anterior;
    }

    /**
     * Retorna el nodo ubicado en el índice indicado.
     *
     * <p>Arranca desde el extremo más cercano al índice, así recorre a lo sumo la
     * mitad de la lista. Sigue siendo O(n), pero con la mitad de pasos.</p>
     */
    private Nodo<T> nodoEn(int index) {
        if (index < tamaño / 2) {
            Nodo<T> actual = primero;

            for (int i = 0; i < index; i++) {
                actual = actual.siguiente;
            }

            return actual;
        }

        Nodo<T> actual = ultimo;

        for (int i = tamaño - 1; i > index; i--) {
            actual = actual.anterior;
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

    /**
     * Valida un índice de inserción, donde tamaño también es un valor válido.
     */
    private void validarIndiceInsercion(int index) {
        if (index < 0 || index > tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
    }
}
