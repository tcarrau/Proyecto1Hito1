package ucu.edu.implementaciones;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Montículo binario mínimo representado mediante un arreglo.
 *
 * <p>Los nodos se guardan en un arreglo: para un nodo ubicado en {@code i}, sus
 * hijos están en {@code 2 * i + 1} y {@code 2 * i + 2}. El dato de cada padre
 * es menor o igual al de sus hijos, por lo que el mínimo siempre está en la
 * posición {@code 0}.</p>
 *
 * @param <T> tipo de dato contenido en cada nodo
 */
public class MonticuloBinario<T> {
    private Elemento<T>[] elementos;
    private int proximo;
    private final Comparator<? super T> comparador;

    public MonticuloBinario() {
        this(1, comparadorNatural());
    }

    public MonticuloBinario(int capacidad) {
        this(capacidad, comparadorNatural());
    }

    @SuppressWarnings("unchecked")
    public MonticuloBinario(int capacidad, Comparator<? super T> comparador) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa");
        }
        elementos = (Elemento<T>[]) new Elemento[Math.max(1, capacidad)];
        proximo = 0;
        this.comparador = Objects.requireNonNull(comparador, "El comparador no puede ser null");
    }

    /** Devuelve el nodo almacenado en la posición indicada. */
    public Elemento<T> getElemento(int indice) {
        validarIndice(indice);
        return elementos[indice];
    }

    /**
     * Reemplaza un nodo y reordena el montículo si es necesario.
     */
    public void setElemento(int indice, Elemento<T> elemento) {
        validarIndice(indice);
        elementos[indice] = Objects.requireNonNull(elemento, "El elemento no puede ser null");
        bajar(subir(indice));
    }

    /** Agrega un nodo y lo ubica en la posición que le corresponde. */
    public void agregar(Elemento<T> elemento) {
        asegurarCapacidad();
        elementos[proximo] = Objects.requireNonNull(elemento, "El elemento no puede ser null");
        subir(proximo++);
    }

    /** Devuelve el nodo mínimo sin removerlo. */
    public Elemento<T> getMinimo() {
        verificarNoVacio();
        return elementos[0];
    }

    /** Remueve y devuelve el nodo mínimo. */
    public Elemento<T> quitarMinimo() {
        verificarNoVacio();

        Elemento<T> minimo = elementos[0];
        elementos[0] = elementos[--proximo];
        elementos[proximo] = null;
        if (proximo > 0) {
            bajar(0);
        }
        return minimo;
    }

    /** Devuelve la cantidad de nodos almacenados. */
    public int getTamaño() {
        return proximo;
    }

    /** Indica si el montículo no contiene nodos. */
    public boolean esVacio() {
        return proximo == 0;
    }

    private int subir(int indice) {
        while (indice > 0) {
            int padre = (indice - 1) / 2;
            if (comparar(elementos[padre], elementos[indice]) <= 0) {
                break;
            }
            intercambiar(padre, indice);
            indice = padre;
        }
        return indice;
    }

    private void bajar(int indice) {
        while (true) {
            int izquierdo = 2 * indice + 1;
            int derecho = 2 * indice + 2;
            int menor = indice;

            if (izquierdo < proximo && comparar(elementos[izquierdo], elementos[menor]) < 0) {
                menor = izquierdo;
            }
            if (derecho < proximo && comparar(elementos[derecho], elementos[menor]) < 0) {
                menor = derecho;
            }
            if (menor == indice) {
                return;
            }
            intercambiar(indice, menor);
            indice = menor;
        }
    }

    private int comparar(Elemento<T> primero, Elemento<T> segundo) {
        return comparador.compare(primero.getDato(), segundo.getDato());
    }

    private void intercambiar(int primero, int segundo) {
        Elemento<T> auxiliar = elementos[primero];
        elementos[primero] = elementos[segundo];
        elementos[segundo] = auxiliar;
    }

    private void verificarNoVacio() {
        if (esVacio()) {
            throw new NoSuchElementException("El montículo está vacío");
        }
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= proximo) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + indice);
        }
    }

    @SuppressWarnings("unchecked")
    private void asegurarCapacidad() {
        if (proximo < elementos.length) {
            return;
        }

        Elemento<T>[] nuevosElementos = (Elemento<T>[]) new Elemento[elementos.length * 2];
        System.arraycopy(elementos, 0, nuevosElementos, 0, proximo);
        elementos = nuevosElementos;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> Comparator<T> comparadorNatural() {
        return (primero, segundo) -> ((Comparable) primero).compareTo(segundo);
    }
}
