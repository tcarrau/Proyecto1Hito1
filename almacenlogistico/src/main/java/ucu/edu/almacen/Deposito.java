package ucu.edu.almacen;

import ucu.edu.implementaciones.ArbolNario;

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
}
