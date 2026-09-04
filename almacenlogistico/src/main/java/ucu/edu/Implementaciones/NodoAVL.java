package ucu.edu.implementaciones;

import java.util.function.Consumer;
import ucu.edu.aed.tda.TDAElemento;

public class NodoAVL<T> implements TDAElemento<T> {
    private T dato;
    private NodoAVL<T> hijoIzquierdo;
    private NodoAVL<T> hijoDerecho;
    private int altura;

    public NodoAVL(T dato) {
        this.dato = dato;
        this.hijoIzquierdo = null;
        this.hijoDerecho = null;
        this.altura = 1; // altura inicial de un nodo hoja es 1
    }

    // ==== Helpers propios del AVL (no vienen de la interfaz) ====

    /** Altura de un nodo, tratando null (subárbol vacío) como altura 0. */
    private int alturaDe(NodoAVL<T> nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    /** Diferencia de altura entre el hijo izquierdo y el derecho. */
    private int factorBalance() {
        return alturaDe(hijoIzquierdo) - alturaDe(hijoDerecho);
    }

    /** Recalcula la altura de este nodo en O(1) a partir de la de sus hijos. */
    private void actualizarAltura() {
        altura = 1 + Math.max(alturaDe(hijoIzquierdo), alturaDe(hijoDerecho));
    }

    /**
     * Rotación simple a la derecha (caso izquierda-izquierda).
     *
     * <p>Como insertar()/eliminar() deben devolver boolean/TDAElemento por contrato
     * de la interfaz (no pueden devolver "el nuevo nodo raíz de este subárbol"),
     * la rotación se hace "in place": this sigue siendo el mismo objeto que tiene
     * el padre referenciado, pero pasa a representar los datos de su hijo izquierdo,
     * y ese hijo (el mismo objeto, reciclado) baja a ocupar el lugar de this.</p>
     */
    private void rotarDerecha() {
        NodoAVL<T> y = hijoIzquierdo;
        NodoAVL<T> t1 = y.hijoIzquierdo;
        NodoAVL<T> t2 = y.hijoDerecho;
        NodoAVL<T> t3 = this.hijoDerecho;

        T datoX = this.dato;
        T datoY = y.dato;

        // 'y' se recicla para representar lo que era 'this' (X), como nuevo hijo derecho.
        y.dato = datoX;
        y.hijoIzquierdo = t2;
        y.hijoDerecho = t3;
        y.actualizarAltura();

        // 'this' pasa a representar lo que era 'y' (Y), que "sube".
        this.dato = datoY;
        this.hijoIzquierdo = t1;
        this.hijoDerecho = y;
        this.actualizarAltura();
    }

    /** Espejo de rotarDerecha(): caso derecha-derecha. */
    private void rotarIzquierda() {
        NodoAVL<T> y = hijoDerecho;
        NodoAVL<T> t1 = this.hijoIzquierdo;
        NodoAVL<T> t2 = y.hijoIzquierdo;
        NodoAVL<T> t3 = y.hijoDerecho;

        T datoX = this.dato;
        T datoY = y.dato;

        // 'y' se recicla para representar lo que era 'this' (X), como nuevo hijo izquierdo.
        y.dato = datoX;
        y.hijoIzquierdo = t1;
        y.hijoDerecho = t2;
        y.actualizarAltura();

        this.dato = datoY;
        this.hijoIzquierdo = y;
        this.hijoDerecho = t3;
        this.actualizarAltura();
    }

    /**
     * Chequea el factor de balance de this y aplica la rotación (simple o doble)
     * que corresponda. Se llama después de actualizarAltura(), en cada nivel de
     * la recursión de insertar(), de abajo hacia arriba.
     */
    private void balancear() {
        int balance = factorBalance();

        if (balance > 1) {
            // Izquierda-derecha: el hijo izquierdo está cargado a la derecha, hay que
            // enderezarlo primero para caer en el caso simple izquierda-izquierda.
            if (hijoIzquierdo.factorBalance() < 0) {
                hijoIzquierdo.rotarIzquierda();
            }
            rotarDerecha();
        } else if (balance < -1) {
            if (hijoDerecho.factorBalance() > 0) {
                hijoDerecho.rotarDerecha();
            }
            rotarIzquierda();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean insertar(Comparable<T> nuevoDato) {
        int comparacion = nuevoDato.compareTo(dato);
        boolean insertado;

        if (comparacion == 0) {
            return false; // duplicado, no se inserta
        } else if (comparacion < 0) {
            if (hijoIzquierdo == null) {
                hijoIzquierdo = new NodoAVL<>((T) nuevoDato);
                insertado = true;
            } else {
                insertado = hijoIzquierdo.insertar(nuevoDato);
            }
        } else {
            if (hijoDerecho == null) {
                hijoDerecho = new NodoAVL<>((T) nuevoDato);
                insertado = true;
            } else {
                insertado = hijoDerecho.insertar(nuevoDato);
            }
        }

        if (insertado) {
            actualizarAltura();
            balancear();
        }
        return insertado;
    }

    // ponytail: eliminar() borra correctamente (mismo algoritmo BST de 3 casos que
    // Elemento) pero NO rebalancea el árbol después de borrar -- solo insertar()
    // rota. Un árbol armado solo con inserciones queda balanceado igual; si además
    // se necesita que quede balanceado tras muchos eliminar(), agregar balancear()
    // en los mismos puntos donde ya se llama actualizarAltura() acá abajo.

    @Override
    public NodoAVL<T> eliminar(Comparable<T> criterioBusqueda) {
        int comparacion = criterioBusqueda.compareTo(dato);
        if (comparacion < 0) return eliminarHijo(true, criterioBusqueda);
        if (comparacion > 0) return eliminarHijo(false, criterioBusqueda);

        NodoAVL<T> eliminado = new NodoAVL<>(dato);
        if (esHoja()) return eliminado;
        if (hijoIzquierdo == null || hijoDerecho == null) {
            NodoAVL<T> hijo = hijoIzquierdo != null ? hijoIzquierdo : hijoDerecho;
            dato = hijo.dato;
            hijoIzquierdo = hijo.hijoIzquierdo;
            hijoDerecho = hijo.hijoDerecho;
            actualizarAltura();
            return eliminado;
        }
        NodoAVL<T> sucesor = hijoDerecho;
        while (sucesor.hijoIzquierdo != null) sucesor = sucesor.hijoIzquierdo;
        dato = sucesor.dato;
        eliminarHijo(false, sucesor.datoComparable());
        actualizarAltura();
        return eliminado;
    }

    private NodoAVL<T> eliminarHijo(boolean izquierdo, Comparable<T> criterio) {
        NodoAVL<T> hijo = izquierdo ? hijoIzquierdo : hijoDerecho;
        if (hijo == null) return null;
        int comparacion = criterio.compareTo(hijo.dato);
        if (comparacion != 0) {
            NodoAVL<T> resultado = hijo.eliminarHijo(comparacion < 0, criterio);
            actualizarAltura();
            return resultado;
        }

        NodoAVL<T> eliminado = new NodoAVL<>(hijo.dato);
        if (hijo.hijoIzquierdo == null) {
            asignarHijo(izquierdo, hijo.hijoDerecho);
        } else if (hijo.hijoDerecho == null) {
            asignarHijo(izquierdo, hijo.hijoIzquierdo);
        } else {
            NodoAVL<T> sucesor = hijo.hijoDerecho;
            while (sucesor.hijoIzquierdo != null) sucesor = sucesor.hijoIzquierdo;
            hijo.dato = sucesor.dato;
            hijo.eliminarHijo(false, sucesor.datoComparable());
            hijo.actualizarAltura();
        }
        actualizarAltura();
        return eliminado;
    }

    private void asignarHijo(boolean izquierdo, NodoAVL<T> hijo) {
        if (izquierdo) hijoIzquierdo = hijo;
        else hijoDerecho = hijo;
    }

    @SuppressWarnings("unchecked")
    private Comparable<T> datoComparable() {
        return (Comparable<T>) dato;
    }

    // ==== El resto: recorrido de BST estándar, igual que Elemento ====

    @Override
    public void setHijoIzquierdo(TDAElemento<T> hijoIzquierdo) {
        this.hijoIzquierdo = (NodoAVL<T>) hijoIzquierdo;
    }

    @Override
    public void setHijoDerecho(TDAElemento<T> hijoDerecho) {
        this.hijoDerecho = (NodoAVL<T>) hijoDerecho;
    }

    @Override
    public NodoAVL<T> getHijoIzquierdo() {
        return hijoIzquierdo;
    }

    @Override
    public NodoAVL<T> getHijoDerecho() {
        return hijoDerecho;
    }

    @Override
    public void setDato(T dato) {
        this.dato = dato;
    }

    @Override
    public T getDato() {
        return dato;
    }

    @Override
    public NodoAVL<T> buscar(Comparable<T> criterioBusqueda) {
        int comparacion = criterioBusqueda.compareTo(dato);
        if (comparacion == 0) return this;
        NodoAVL<T> siguiente = comparacion < 0 ? hijoIzquierdo : hijoDerecho;
        return siguiente == null ? null : siguiente.buscar(criterioBusqueda);
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

    @Override
    public boolean esHoja() {
        return hijoIzquierdo == null && hijoDerecho == null;
    }

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

    /** O(1): a diferencia de Elemento.altura(), acá no recorremos nada, leemos el campo cacheado. */
    @Override
    public int altura() {
        return altura;
    }

    @Override
    public int obtenerNivel(Comparable<T> criterioBusqueda) {
        int comparacion = criterioBusqueda.compareTo(dato);
        if (comparacion == 0) return 0;
        NodoAVL<T> siguiente = comparacion < 0 ? hijoIzquierdo : hijoDerecho;
        if (siguiente == null) return -1;
        int nivel = siguiente.obtenerNivel(criterioBusqueda);
        return nivel == -1 ? -1 : nivel + 1;
    }
}
