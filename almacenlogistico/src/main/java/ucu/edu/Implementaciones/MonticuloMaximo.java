package ucu.edu.implementaciones;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

/** Montículo máximo respaldado por la {@link ListaArray} del proyecto. */
public class MonticuloMaximo<T extends Comparable<? super T>> {

    private ListaArray<T> elementos;

    public MonticuloMaximo() {
        this.elementos = new ListaArray<>();
    }

    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento no puede ser null");
        }
        elementos.agregar(elemento);
        subir(elementos.tamaño() - 1);
    }

    public T obtenerMaximo() {
        verificarNoVacio();
        return elementos.obtener(0);
    }

    public T quitarMaximo() {
        verificarNoVacio();
        T maximo = elementos.remover(0);
        if (elementos.esVacio()) {
            return maximo;
        }

        T ultimo = elementos.remover(elementos.tamaño() - 1);
        elementos.agregar(0, ultimo);
        bajar(0);
        return maximo;
    }

    /** Reordena un elemento cuya prioridad fue modificada externamente. */
    public boolean actualizar(T elemento) {
        int indice = indiceDeReferencia(elemento);
        if (indice < 0) {
            return false;
        }
        bajar(subir(indice));
        return true;
    }

    /** Busca un elemento sin modificar la estructura del montículo. */
    public T buscar(Predicate<T> criterio) {
        if (criterio == null) {
            throw new IllegalArgumentException("El criterio no puede ser null");
        }
        for (int i = 0; i < elementos.tamaño(); i++) {
            T elemento = elementos.obtener(i);
            if (criterio.test(elemento)) {
                return elemento;
            }
        }
        return null;
    }

    public boolean esVacio() {
        return elementos.esVacio();
    }

    public int tamaño() {
        return elementos.tamaño();
    }

    private int subir(int indice) {
        while (indice > 0) {
            int padre = (indice - 1) / 2;
            if (elementos.obtener(indice).compareTo(elementos.obtener(padre)) <= 0) {
                return indice;
            }
            intercambiar(indice, padre);
            indice = padre;
        }
        return indice;
    }

    private void bajar(int indice) {
        while (true) {
            int izquierdo = indice * 2 + 1;
            int derecho = indice * 2 + 2;
            int mayor = indice;

            if (izquierdo < elementos.tamaño()
                    && elementos.obtener(izquierdo).compareTo(elementos.obtener(mayor)) > 0) {
                mayor = izquierdo;
            }
            if (derecho < elementos.tamaño()
                    && elementos.obtener(derecho).compareTo(elementos.obtener(mayor)) > 0) {
                mayor = derecho;
            }
            if (mayor == indice) {
                return;
            }
            intercambiar(indice, mayor);
            indice = mayor;
        }
    }

    private int indiceDeReferencia(T elemento) {
        for (int i = 0; i < elementos.tamaño(); i++) {
            if (elementos.obtener(i) == elemento) {
                return i;
            }
        }
        return -1;
    }

    private void intercambiar(int primero, int segundo) {
        if (primero < segundo) {
            T datoPrimero = elementos.remover(primero);
            T datoSegundo = elementos.remover(segundo - 1);
            elementos.agregar(primero, datoSegundo);
            elementos.agregar(segundo, datoPrimero);
        } else {
            T datoSegundo = elementos.remover(segundo);
            T datoPrimero = elementos.remover(primero - 1);
            elementos.agregar(segundo, datoPrimero);
            elementos.agregar(primero, datoSegundo);
        }
    }

    private void verificarNoVacio() {
        if (esVacio()) {
            throw new NoSuchElementException("El montículo está vacío");
        }
    }
}
