package ucu.edu.implementaciones;

/**
 * Esqueleto de un montículo máximo para elementos prioritarios.
 *
 * <p>La representación reutiliza {@link ListaArray}, desarrollada en el
 * proyecto. El reordenamiento interno queda pendiente.</p>
 *
 * @param <T> tipo de dato almacenado
 */
public class MonticuloMaximo<T> {

    private ListaArray<T> elementos;

    public MonticuloMaximo() {
        this.elementos = new ListaArray<>();
    }

    public void agregar(T elemento) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public T obtenerMaximo() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public T quitarMaximo() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public boolean esVacio() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public int tamaño() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
