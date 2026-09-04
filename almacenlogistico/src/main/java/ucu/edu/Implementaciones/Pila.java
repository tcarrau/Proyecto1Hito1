package ucu.edu.implementaciones;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDALista;
import ucu.edu.aed.tda.TDAPila;

/**
 * Implementación de una pila (LIFO) construida sobre una lista doblemente enlazada.
 *
 * <p>El tope de la pila es el último elemento de la lista interna. Se eligió la
 * lista doblemente enlazada porque la pila inserta y quita siempre por el mismo
 * extremo: allí agregar al final es O(1) y quitar el último también es O(1) gracias
 * al enlace hacia atrás. Sobre una lista simplemente enlazada quitar el último
 * costaría O(n), porque habría que recorrerla entera para llegar al penúltimo.</p>
 *
 * <p>Al ubicar el tope al final, la operación heredada agregar coincide exactamente
 * con mete, de modo que las dos vistas de la estructura no se contradicen.</p>
 *
 * @param <T> tipo de elementos almacenados
 */
public class Pila<T> implements TDAPila<T> {

    /**
     * Elementos de la pila. El último es el tope.
     */
    private final ListaDoblementeEnlazada<T> elementos = new ListaDoblementeEnlazada<>();

    //Métodos de TDAPila

    @Override
    public T tope() {
        if (esVacio()) {
            throw new NoSuchElementException("La pila está vacía");
        }

        return elementos.obtener(elementos.tamaño() - 1);
    }

    @Override
    public T saca() {
        if (esVacio()) {
            throw new NoSuchElementException("La pila está vacía");
        }

        return elementos.remover(elementos.tamaño() - 1);
    }

    @Override
    public void mete(T dato) {
        elementos.agregar(dato);
    }

    //Métodos de TDALista

    @Override
    public void agregar(T elem) {
        mete(elem);
    }

    @Override
    public void agregar(int index, T elem) {
        elementos.agregar(index, elem);
    }

    @Override
    public T obtener(int index) {
        return elementos.obtener(index);
    }

    @Override
    public T remover(int index) {
        return elementos.remover(index);
    }

    @Override
    public boolean remover(T elem) {
        return elementos.remover(elem);
    }

    @Override
    public boolean contiene(T elem) {
        return elementos.contiene(elem);
    }

    @Override
    public int indiceDe(T elem) {
        return elementos.indiceDe(elem);
    }

    @Override
    public T buscar(Predicate<T> criterio) {
        return elementos.buscar(criterio);
    }

    @Override
    public TDALista<T> ordenar(Comparator<T> comparator) {
        return elementos.ordenar(comparator);
    }

    @Override
    public int tamaño() {
        return elementos.tamaño();
    }

    @Override
    public boolean esVacio() {
        return elementos.esVacio();
    }

    @Override
    public void vaciar() {
        elementos.vaciar();
    }
}
