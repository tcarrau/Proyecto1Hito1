package ucu.edu.implementaciones;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDACola;
import ucu.edu.aed.tda.TDALista;

/**
 * Implementación de una cola (FIFO) sobre un arreglo de capacidad fija.
 *
 * <p>La capacidad se fija en el constructor y no crece: es una decisión deliberada
 * para modelar una zona de espera con lugar limitado. Cuando la cola está llena,
 * {@code poneEnCola} lo informa devolviendo {@code false}, mientras que
 * {@code agregar} lanza {@link IllegalStateException}, porque su firma no permite
 * devolver nada y descartar el elemento en silencio sería pérdida de datos.</p>
 *
 * <p>El frente es siempre la posición 0. Por eso {@code quitaDeCola} tiene que
 * desplazar el resto de los elementos y cuesta O(n), mientras que
 * {@code poneEnCola} es O(1).</p>
 *
 * @param <T> tipo de elementos almacenados
 */
public class Cola<T> implements TDACola<T>{
    private T[] elementos;
    private int cantidad;
    @SuppressWarnings("unchecked")
    public Cola(int capacidad){
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa");
        }
        elementos=(T[]) new Object[capacidad];
        cantidad=0;
    }
    
   //Métodos de TDACola
    @Override
    public T frente(){
        if(esVacia()){
            throw new NoSuchElementException("La cola esta vacía");
        }
        else{
            return elementos[0];
        }
    }
    @Override
    public T quitaDeCola(){
        if(esVacia()){
            throw new NoSuchElementException("La cola esta vacía");
        }
        T dato=elementos[0];
        for (int i=0;i<cantidad-1;i++){
            elementos[i]=elementos[i+1];
        }
        cantidad --;
        elementos[cantidad]=null;
        return dato;
    }
    @Override
    public boolean poneEnCola(T dato) {
        if (cantidad == elementos.length) {
            return false;
        }

        elementos[cantidad] = dato;
        cantidad++;

        return true;
    }
    // Se mantiene porque AlmacenLogistico ya la usa. Delega en esVacio() para que
    // no queden dos definiciones distintas de la misma condicion.
    public boolean esVacia() {
        return esVacio();
    }
    
    //Métodos de TDALista
    @Override
    public void agregar(T elem){
        // agregar no puede informar si el elemento entro o no, asi que una cola
        // llena se reporta con excepcion en vez de descartarlo en silencio.
        if(!poneEnCola(elem)){
            throw new IllegalStateException("La cola esta llena");
        }
    }
    @Override
    public void agregar(int index, T elem){
        if(index<0||index>cantidad){
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + index);
        }
        if(cantidad==elementos.length){
            throw new IllegalStateException("La cola esta llena");
        }
        for (int i = cantidad; i > index; i--) {
            elementos[i] = elementos[i - 1];
        }

        elementos[index] = elem;
        cantidad++;
    }
    @Override
    public T obtener(int index){
        // El indice valido llega hasta cantidad-1. Con > se permitia leer una
        // posicion inexistente, que devolvia null en lugar de fallar.
        if(index<0 || index>=cantidad){
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + index);
        }
        return elementos[index];
    }

    @Override
    public int indiceDe(T elem){
        for(int i=0;i<cantidad;i++){
            // Objects.equals tolera null de los dos lados; elementos[i].equals
            // reventaba con NullPointerException si la cola guardaba algun null.
            if(Objects.equals(elementos[i], elem)){
                return i;
            }
        }
        return -1;
    }
    @Override
    public boolean contiene(T elem){
        return indiceDe(elem) !=-1;
    }
    @Override
    public int tamaño(){
        return cantidad;
    }
    @Override
    public boolean esVacio(){
        return cantidad==0;
    }
    @Override
    public void vaciar(){
        for(int i =0;i<cantidad;i++){
            elementos[i]=null;
        }
        cantidad=0;
    }
    @Override
    public TDALista<T> ordenar(Comparator<T> comparator) {
        Objects.requireNonNull(comparator, "El comparador no puede ser null");

        Cola<T> resultado = new Cola<>(elementos.length);

        for (int i = 0; i < cantidad; i++) {
            resultado.elementos[i] = elementos[i];
        }

        resultado.cantidad = cantidad;

        for (int i = 0; i < resultado.cantidad - 1; i++) {
            for (int j = i + 1; j < resultado.cantidad; j++) {

                if (comparator.compare(
                        resultado.elementos[i],
                        resultado.elementos[j]) > 0) {

                    T aux = resultado.elementos[i];
                    resultado.elementos[i] = resultado.elementos[j];
                    resultado.elementos[j] = aux;
                }
            }
        }

        return resultado;
    }
     @Override
    public T buscar(Predicate<T> criterio) {
        Objects.requireNonNull(criterio, "El criterio no puede ser null");

        for(int i=0;i<cantidad;i++){
            if(criterio.test(elementos[i])){
                return elementos[i];
            }
        }
        return null;
    }
    @Override
    public T remover(int index) {
        if (index < 0 || index >= cantidad) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + index);
        }

        T eliminado = elementos[index];

        for (int i = index; i < cantidad - 1; i++) {
            elementos[i] = elementos[i + 1];
        }

        cantidad--;
        elementos[cantidad] = null;

        return eliminado;
    }
    @Override
    public boolean remover(T elem) {
        int index = indiceDe(elem);

        if (index == -1) {
            return false;
        }

        remover(index);
        return true;
    }
}
