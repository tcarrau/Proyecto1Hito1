package ucu.edu.implementaciones;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDAConjunto;
import ucu.edu.aed.tda.TDALista;

/**
 * Implementación de un conjunto: colección sin elementos repetidos.
 *
 * <p>Se apoya en una {@link ListaArray}. El invariante que mantiene es que no hay
 * dos elementos iguales según {@code equals}, y para sostenerlo cada alta consulta
 * primero si el elemento ya está presente.</p>
 *
 * <p>Se eligió la lista sobre arreglo porque las operaciones de conjuntos recorren
 * los elementos por índice: con acceso O(1) la unión, intersección y diferencia
 * quedan en O(n·m); sobre una lista enlazada cada acceso costaría O(n) y treparían
 * un orden más.</p>
 *
 * <p>El conjunto conserva el orden de inserción de sus elementos. Eso no forma parte
 * del TDA (en un conjunto el orden no importa) pero permite recorrerlo de manera
 * predecible al implementar las operaciones heredadas de {@link TDALista}.</p>
 *
 * @param <T> tipo de elementos almacenados
 */
public class Conjunto<T> implements TDAConjunto<T> {

    /**
     * Elementos del conjunto, sin repetidos.
     */
    private final ListaArray<T> elementos = new ListaArray<>();

    //Métodos de TDAConjunto

    @Override
    public TDAConjunto<T> union(TDAConjunto<T> otro) {
        Objects.requireNonNull(otro, "El conjunto no puede ser null");

        Conjunto<T> resultado = new Conjunto<>();

        for (int i = 0; i < elementos.tamaño(); i++) {
            resultado.agregar(elementos.obtener(i));
        }
        for (int i = 0; i < otro.tamaño(); i++) {
            resultado.agregar(otro.obtener(i));
        }

        return resultado;
    }

    @Override
    public TDAConjunto<T> interseccion(TDAConjunto<T> otro) {
        Objects.requireNonNull(otro, "El conjunto no puede ser null");

        Conjunto<T> resultado = new Conjunto<>();

        for (int i = 0; i < elementos.tamaño(); i++) {
            T elemento = elementos.obtener(i);

            if (otro.contiene(elemento)) {
                resultado.agregar(elemento);
            }
        }

        return resultado;
    }

    @Override
    public TDAConjunto<T> diferencia(TDAConjunto<T> otro) {
        Objects.requireNonNull(otro, "El conjunto no puede ser null");

        Conjunto<T> resultado = new Conjunto<>();

        for (int i = 0; i < elementos.tamaño(); i++) {
            T elemento = elementos.obtener(i);

            if (!otro.contiene(elemento)) {
                resultado.agregar(elemento);
            }
        }

        return resultado;
    }

    @Override
    public boolean esSubconjuntoDe(TDAConjunto<T> otro) {
        Objects.requireNonNull(otro, "El conjunto no puede ser null");

        for (int i = 0; i < elementos.tamaño(); i++) {
            if (!otro.contiene(elementos.obtener(i))) {
                return false;
            }
        }

        // El conjunto vacío es subconjunto de cualquier otro.
        return true;
    }

    //Métodos de TDALista

    /**
     * Agrega el elemento sólo si todavía no pertenece al conjunto. Si ya está,
     * la operación no tiene efecto: es la forma de sostener el invariante de
     * no repetidos sin romper la firma heredada, que no devuelve nada.
     */
    @Override
    public void agregar(T elem) {
        if (!contiene(elem)) {
            elementos.agregar(elem);
        }
    }

    @Override
    public void agregar(int index, T elem) {
        if (!contiene(elem)) {
            elementos.agregar(index, elem);
        }
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
