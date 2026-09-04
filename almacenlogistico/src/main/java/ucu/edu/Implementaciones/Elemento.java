package ucu.edu.aed.implementaciones;

import java.util.function.Consumer;
import ucu.edu.aed.tda.TDAElemento;

/** Nodo de un árbol binario de búsqueda. */
public class Elemento<T> implements TDAElemento<T> {
    private T dato;
    private Elemento<T> hijoIzquierdo;
    private Elemento<T> hijoDerecho;

    public Elemento(T dato) {
        this.dato = dato;
    }

    @Override
    public void setHijoIzquierdo(TDAElemento<T> hijoIzquierdo) {
        this.hijoIzquierdo = (Elemento<T>) hijoIzquierdo;
    }

    @Override
    public void setHijoDerecho(TDAElemento<T> hijoDerecho) {
        this.hijoDerecho = (Elemento<T>) hijoDerecho;
    }

    @Override public Elemento<T> getHijoIzquierdo() { return hijoIzquierdo; }
    @Override public Elemento<T> getHijoDerecho() { return hijoDerecho; }
    @Override public void setDato(T dato) { this.dato = dato; }
    @Override public T getDato() { return dato; }

    @Override
    public Elemento<T> buscar(Comparable<T> criterioBusqueda) {
        int comparacion = criterioBusqueda.compareTo(dato);
        if (comparacion == 0) return this;
        Elemento<T> siguiente = comparacion < 0 ? hijoIzquierdo : hijoDerecho;
        return siguiente == null ? null : siguiente.buscar(criterioBusqueda);
    }

    @Override
    public Elemento<T> eliminar(Comparable<T> criterioBusqueda) {
        int comparacion = criterioBusqueda.compareTo(dato);
        if (comparacion < 0) return eliminarHijo(true, criterioBusqueda);
        if (comparacion > 0) return eliminarHijo(false, criterioBusqueda);

        Elemento<T> eliminado = new Elemento<>(dato);
        if (esHoja()) return eliminado;
        if (hijoIzquierdo == null || hijoDerecho == null) {
            Elemento<T> hijo = hijoIzquierdo != null ? hijoIzquierdo : hijoDerecho;
            dato = hijo.dato;
            hijoIzquierdo = hijo.hijoIzquierdo;
            hijoDerecho = hijo.hijoDerecho;
            return eliminado;
        }
        Elemento<T> sucesor = hijoDerecho;
        while (sucesor.hijoIzquierdo != null) sucesor = sucesor.hijoIzquierdo;
        dato = sucesor.dato;
        eliminarHijo(false, sucesor.datoComparable());
        return eliminado;
    }

    private Elemento<T> eliminarHijo(boolean izquierdo, Comparable<T> criterio) {
        Elemento<T> hijo = izquierdo ? hijoIzquierdo : hijoDerecho;
        if (hijo == null) return null;
        int comparacion = criterio.compareTo(hijo.dato);
        if (comparacion != 0) return hijo.eliminarHijo(comparacion < 0, criterio);

        Elemento<T> eliminado = new Elemento<>(hijo.dato);
        if (hijo.hijoIzquierdo == null) {
            asignarHijo(izquierdo, hijo.hijoDerecho);
        } else if (hijo.hijoDerecho == null) {
            asignarHijo(izquierdo, hijo.hijoIzquierdo);
        } else {
            Elemento<T> sucesor = hijo.hijoDerecho;
            while (sucesor.hijoIzquierdo != null) sucesor = sucesor.hijoIzquierdo;
            hijo.dato = sucesor.dato;
            hijo.eliminarHijo(false, sucesor.datoComparable());
        }
        return eliminado;
    }

    private void asignarHijo(boolean izquierdo, Elemento<T> hijo) {
        if (izquierdo) hijoIzquierdo = hijo;
        else hijoDerecho = hijo;
    }

    @SuppressWarnings("unchecked")
    private Comparable<T> datoComparable() { return (Comparable<T>) dato; }

    @Override
    @SuppressWarnings("unchecked")
    public boolean insertar(Comparable<T> nuevoDato) {
        int comparacion = nuevoDato.compareTo(dato);
        if (comparacion == 0) return false;
        if (comparacion < 0) {
            if (hijoIzquierdo == null) {
                hijoIzquierdo = new Elemento<>((T) nuevoDato);
                return true;
            }
            return hijoIzquierdo.insertar(nuevoDato);
        }
        if (hijoDerecho == null) {
            hijoDerecho = new Elemento<>((T) nuevoDato);
            return true;
        }
        return hijoDerecho.insertar(nuevoDato);
    }

    @Override
    public void inOrder(Consumer<TDAElemento<T>> consumidor) {
        if (hijoIzquierdo != null) hijoIzquierdo.inOrder(consumidor);
        consumidor.accept(this);
        if (hijoDerecho != null) hijoDerecho.inOrder(consumidor);
    }

    @Override
    public void preOrder(Consumer<TDAElemento<T>> consumidor) {
        consumidor.accept(this);
        if (hijoIzquierdo != null) hijoIzquierdo.preOrder(consumidor);
        if (hijoDerecho != null) hijoDerecho.preOrder(consumidor);
    }

    @Override
    public void postOrder(Consumer<TDAElemento<T>> consumidor) {
        if (hijoIzquierdo != null) hijoIzquierdo.postOrder(consumidor);
        if (hijoDerecho != null) hijoDerecho.postOrder(consumidor);
        consumidor.accept(this);
    }

    @Override public boolean esHoja() { return hijoIzquierdo == null && hijoDerecho == null; }

    @Override
    public int cantidadHojas() {
        if (esHoja()) return 1;
        return (hijoIzquierdo == null ? 0 : hijoIzquierdo.cantidadHojas())
             + (hijoDerecho == null ? 0 : hijoDerecho.cantidadHojas());
    }

    @Override
    public int cantidadNodosInternos() {
        if (esHoja()) return 0;
        return 1 + (hijoIzquierdo == null ? 0 : hijoIzquierdo.cantidadNodosInternos())
                 + (hijoDerecho == null ? 0 : hijoDerecho.cantidadNodosInternos());
    }

    @Override
    public int cantidadNodos() {
        return 1 + (hijoIzquierdo == null ? 0 : hijoIzquierdo.cantidadNodos())
                 + (hijoDerecho == null ? 0 : hijoDerecho.cantidadNodos());
    }

    @Override
    public int altura() {
        int izquierda = hijoIzquierdo == null ? 0 : hijoIzquierdo.altura();
        int derecha = hijoDerecho == null ? 0 : hijoDerecho.altura();
        return 1 + Math.max(izquierda, derecha);
    }

    @Override
    public int obtenerNivel(Comparable<T> criterioBusqueda) {
        int comparacion = criterioBusqueda.compareTo(dato);
        if (comparacion == 0) return 0;
        Elemento<T> siguiente = comparacion < 0 ? hijoIzquierdo : hijoDerecho;
        if (siguiente == null) return -1;
        int nivel = siguiente.obtenerNivel(criterioBusqueda);
        return nivel == -1 ? -1 : nivel + 1;
    }
}
