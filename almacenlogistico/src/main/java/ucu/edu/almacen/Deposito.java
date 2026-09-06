package ucu.edu.almacen;

import ucu.edu.implementaciones.ArbolNario;
import ucu.edu.implementaciones.ListaArray;
import ucu.edu.implementaciones.NodoNario;
import ucu.edu.aed.tda.TDANodoNario;

/** Contenedor de la estructura jerárquica de sectores del almacén. */
public class Deposito {

    private ArbolNario<Sector> sectores;

    public Deposito() {
        this.sectores = new ArbolNario<>();
    }

    public Deposito(ArbolNario<Sector> sectores) {
        this.sectores = sectores;
    }

    public ArbolNario<Sector> getSectores() {
        return sectores;
    }

    public void setSectores(ArbolNario<Sector> sectores) {
        this.sectores = sectores;
    }

    /** Busca un sector por su código dentro de la jerarquía del depósito. */
    public Sector buscarSector(String codigo) {
        NodoNario<Sector> nodo = buscarNodo(codigo);
        return nodo == null ? null : nodo.getDato();
    }

    /**
     * Mueve un sector y todo su subárbol a un nuevo padre.
     * No permite mover la raíz ni crear ciclos en la jerarquía.
     */
    public boolean moverSector(String codigoSector, String codigoNuevoPadre) {
        NodoNario<Sector> sectorAMover = buscarNodo(codigoSector);
        NodoNario<Sector> nuevoPadre = buscarNodo(codigoNuevoPadre);

        if (sectorAMover == null || nuevoPadre == null || sectorAMover.getPadre() == null) {
            return false;
        }

        for (NodoNario<Sector> actual = nuevoPadre; actual != null; actual = actual.getPadre()) {
            if (actual == sectorAMover) {
                return false;
            }
        }

        NodoNario<Sector> padreActual = sectorAMover.getPadre();
        TDANodoNario<Sector> subarbol = sectores.desprenderSubarbol(padreActual, sectorAMover);

        if (subarbol == null) {
            return false;
        }

        sectores.engancharSubarbol(nuevoPadre, subarbol);
        return true;
    }

    /** Devuelve todos los sectores del subárbol cuya raíz tiene el código indicado. */
    public ListaArray<Sector> obtenerSectoresDelSubarbol(String codigoSector) {
        ListaArray<Sector> resultado = new ListaArray<>();
        NodoNario<Sector> raizSubarbol = buscarNodo(codigoSector);

        if (raizSubarbol != null) {
            raizSubarbol.preOrder(nodo -> resultado.agregar(nodo.getDato()));
        }
        return resultado;
    }

    @SuppressWarnings("unchecked")
    private NodoNario<Sector> buscarNodo(String codigo) {
        if (codigo == null || codigo.isBlank() || sectores == null) {
            return null;
        }

        TDANodoNario<Sector> nodo = sectores.buscar(sector -> sector != null
                && codigo.equals(sector.getCodigo()));
        return nodo == null ? null : (NodoNario<Sector>) nodo;
    }
}
