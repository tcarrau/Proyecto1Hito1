package ucu.edu.implementaciones;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import ucu.edu.aed.tda.TDACola;
import ucu.edu.aed.tda.TDALista;
public class Cola<T> implements TDACola<T>{
    private T[] elementos;
    private int cantidad;
    @SuppressWarnings("unchecked")
    public Cola(int capacidad){
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
    public boolean esVacia() {
        return cantidad == 0;
    }
    
    //Métodos de TDALista
    @Override
    public void agregar(T elem){
        poneEnCola(elem);
    }
    @Override
    public void agregar(int index, T elem){
        if(index<0||index>cantidad){
            throw new IndexOutOfBoundsException("");
        }
        if(cantidad==elementos.length){
            throw new IllegalStateException("la cola esta llena");
        }
        for (int i = cantidad; i > index; i--) {
            elementos[i] = elementos[i - 1];
        }

        elementos[index] = elem;
        cantidad++;
    }
    @Override
    public T obtener(int index){
        if(index<0 || index>cantidad){
            throw new IndexOutOfBoundsException("La cola estta llena");
        }
        return elementos[index];
    }

    @Override
    public int indiceDe(T elem){
        for(int i=0;i<cantidad;i++){
            if(elementos[i].equals(elem)){
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
            throw new IndexOutOfBoundsException();
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
