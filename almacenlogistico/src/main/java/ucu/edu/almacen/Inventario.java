package ucu.edu.almacen;

import ucu.edu.implementaciones.ArbolAVL;
import ucu.edu.implementaciones.ListaArray;

/** Gestiona el inventario ubicado mediante un AVL de registros. */
public class Inventario {

    private ArbolAVL<RegistroInventario> registros;

    public Inventario() {
        this.registros = new ArbolAVL<>();
    }

    public ArbolAVL<RegistroInventario> getRegistros() {
        return registros;
    }

    public void registrarProducto(Producto producto, StockUbicado ubicacion) {
        validarProducto(producto);
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicación no puede ser null");
        }
        if (buscarProducto(producto.getCodigo()) != null) {
            return;
        }

        RegistroInventario registro = new RegistroInventario(producto);
        registro.getUbicaciones().agregar(ubicacion);
        registros.insertar(registro);
    }

    public RegistroInventario buscarProducto(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        return registros.buscar(new RegistroInventario(new Producto(codigo, null)));
    }

    /**
     * Devuelve los registros ordenados ascendentemente por código de producto.
     */
    public ListaArray<RegistroInventario> listarInventarioOrdenado() {
        ListaArray<RegistroInventario> inventarioOrdenado = new ListaArray<>();
        registros.inOrder(inventarioOrdenado::agregar);
        return inventarioOrdenado;
    }

    public void aumentarStock(String codigo, int cantidad, Sector posicion) {
        if (cantidad <= 0 || posicion == null) {
            throw new IllegalArgumentException("La cantidad y la posición deben ser válidas");
        }
        RegistroInventario registro = buscarProducto(codigo);
        if (registro == null) {
            throw new IllegalArgumentException("No existe un producto con ese código");
        }
        StockUbicado stock = registro.getStockUbicado(posicion);
        if (stock == null) {
            registro.getUbicaciones().agregar(new StockUbicado(posicion, cantidad));
        } else {
            stock.setCantidad(stock.getCantidad() + cantidad);
        }
    }

    public boolean disminuirStock(String codigo, int cantidad, Sector posicion) {
        if (cantidad <= 0 || posicion == null) {
            return false;
        }
        RegistroInventario registro = buscarProducto(codigo);
        StockUbicado stock = registro == null ? null : registro.getStockUbicado(posicion);
        if (stock == null || stock.getCantidad() < cantidad) {
            return false;
        }
        stock.setCantidad(stock.getCantidad() - cantidad);
        return true;
    }

    public boolean reubicarMercaderia(String codigo, Sector origen, Sector destino, int cantidad) {
        if (!disminuirStock(codigo, cantidad, origen)) {
            return false;
        }
        aumentarStock(codigo, cantidad, destino);
        return true;
    }

    public int obtenerStockTotal(String codigo) {
        RegistroInventario registro = buscarProducto(codigo);
        if (registro == null) {
            return 0;
        }
        int total = 0;
        ListaArray<StockUbicado> ubicaciones = registro.getUbicaciones();
        for (int i = 0; i < ubicaciones.tamaño(); i++) {
            total += ubicaciones.obtener(i).getCantidad();
        }
        return total;
    }

    /**
     * Inhabilita un sector y mueve la mercadería de todo su subárbol a
     * posiciones habilitadas con capacidad disponible fuera del subárbol.
     */
    public boolean inhabilitarSector(Deposito deposito, String codigoSector) {
        if (deposito == null) {
            return false;
        }
        Sector sector = deposito.buscarSector(codigoSector);
        if (sector == null || !sector.isHabilitado()) {
            return false;
        }

        ListaArray<Sector> origenes = deposito.obtenerSectoresDelSubarbol(codigoSector);
        ListaArray<Sector> destinos = posicionesDisponibles(deposito, origenes);
        if (cantidadEnSectores(origenes) > capacidadLibreTotal(destinos)) {
            return false;
        }

        registros.inOrder(registro -> moverRegistro(registro, origenes, destinos));
        sector.setHabilitado(false);
        return true;
    }

    /** Registra en el AVL la mercadería recibida y sus posiciones de guardado. */
    public void registrarMercaderiaRecibida(ListaArray<RegistroInventario> recibida) {
        if (recibida == null) throw new IllegalArgumentException("La entrega debe indicar posiciones");
        for (int i = 0; i < recibida.tamaño(); i++) {
            RegistroInventario registro = recibida.obtener(i);
            Producto producto = registro.getProducto();
            validarProducto(producto);
            if (registro.getUbicaciones() == null || registro.getUbicaciones().esVacio()) {
                throw new IllegalArgumentException("Cada producto recibido debe tener ubicaciones");
            }
            for (int j = 0; j < registro.getUbicaciones().tamaño(); j++) {
                StockUbicado stock = registro.getUbicaciones().obtener(j);
                if (stock.getPosicion() == null || stock.getCantidad() <= 0) {
                    throw new IllegalArgumentException("La ubicación recibida no es válida");
                }
                if (buscarProducto(producto.getCodigo()) == null) {
                    registrarProducto(producto, new StockUbicado(stock.getPosicion(), stock.getCantidad()));
                } else {
                    aumentarStock(producto.getCodigo(), stock.getCantidad(), stock.getPosicion());
                }
            }
        }
    }

    /**
     * Genera los pasos en preorden de posiciones habilitadas. Se completa una
     * posición antes de pasar a la siguiente, evitando volver a un sector.
     */
    public ListaArray<PasoRecoleccion> prepararPedidoUbicado(Deposito deposito,
            PedidoSucursal pedido) {
        if (deposito == null || pedido == null || pedido.getProductos() == null) {
            throw new IllegalArgumentException("El depósito y el pedido son obligatorios");
        }
        ListaArray<Sector> posiciones = posicionesHabilitadas(deposito);
        ListaArray<PasoRecoleccion> pasos = new ListaArray<>();
        int[] pendientes = pendientes(pedido.getProductos());

        for (int i = 0; i < posiciones.tamaño(); i++) {
            Sector posicion = posiciones.obtener(i);
            for (int j = 0; j < pedido.getProductos().tamaño(); j++) {
                if (pendientes[j] == 0) continue;
                DetalleProducto detalle = pedido.getProductos().obtener(j);
                RegistroInventario registro = buscarProducto(detalle.getProducto().getCodigo());
                StockUbicado stock = registro == null ? null : registro.getStockUbicado(posicion);
                if (stock == null || stock.getCantidad() == 0) continue;
                int cantidad = Math.min(pendientes[j], stock.getCantidad());
                pasos.agregar(new PasoRecoleccion(pedido, detalle.getProducto(), stock, cantidad));
                pendientes[j] -= cantidad;
            }
        }
        for (int i = 0; i < pendientes.length; i++) {
            if (pendientes[i] > 0) throw new IllegalStateException("No hay stock suficiente");
        }
        pedido.setPasosRecoleccion(pasos);
        return pasos;
    }

    /** Prepara el recorrido y descuenta el stock de cada posición indicada. */
    public boolean despacharPedidoUbicado(Deposito deposito, PedidoSucursal pedido) {
        ListaArray<PasoRecoleccion> pasos = prepararPedidoUbicado(deposito, pedido);
        for (int i = 0; i < pasos.tamaño(); i++) {
            PasoRecoleccion paso = pasos.obtener(i);
            if (paso.getStockOrigen().getCantidad() < paso.getCantidad()) return false;
        }
        for (int i = 0; i < pasos.tamaño(); i++) {
            PasoRecoleccion paso = pasos.obtener(i);
            paso.getStockOrigen().setCantidad(paso.getStockOrigen().getCantidad() - paso.getCantidad());
        }
        return true;
    }

    private ListaArray<Sector> posicionesHabilitadas(Deposito deposito) {
        ListaArray<Sector> posiciones = new ListaArray<>();
        deposito.getSectores().preOrder(sector -> {
            if (sector.getTipo() == TipoSector.POSICION && sector.isHabilitado()) posiciones.agregar(sector);
        });
        return posiciones;
    }

    private int[] pendientes(ListaArray<DetalleProducto> detalles) {
        int[] resultado = new int[detalles.tamaño()];
        for (int i = 0; i < detalles.tamaño(); i++) {
            DetalleProducto actual = detalles.obtener(i);
            if (actual.getProducto() == null || actual.getProducto().getCodigo() == null
                    || actual.getCantidad() <= 0) throw new IllegalArgumentException("Detalle inválido");
            boolean repetido = false;
            for (int j = 0; j < i; j++) {
                if (actual.getProducto().getCodigo().equals(detalles.obtener(j).getProducto().getCodigo())) repetido = true;
            }
            if (!repetido) {
                for (int j = i; j < detalles.tamaño(); j++) {
                    if (actual.getProducto().getCodigo().equals(detalles.obtener(j).getProducto().getCodigo())) {
                        resultado[i] += detalles.obtener(j).getCantidad();
                    }
                }
            }
        }
        return resultado;
    }

    private ListaArray<Sector> posicionesDisponibles(Deposito deposito, ListaArray<Sector> excluidos) {
        ListaArray<Sector> posiciones = new ListaArray<>();
        deposito.getSectores().preOrder(sector -> {
            if (sector.getTipo() == TipoSector.POSICION && sector.isHabilitado()
                    && !contiene(excluidos, sector) && capacidadLibre(sector) > 0) {
                posiciones.agregar(sector);
            }
        });
        return posiciones;
    }

    private int cantidadEnSectores(ListaArray<Sector> sectores) {
        final int[] total = {0};
        registros.inOrder(registro -> {
            for (int i = 0; i < registro.getUbicaciones().tamaño(); i++) {
                StockUbicado stock = registro.getUbicaciones().obtener(i);
                if (contiene(sectores, stock.getPosicion())) total[0] += stock.getCantidad();
            }
        });
        return total[0];
    }

    private int capacidadLibreTotal(ListaArray<Sector> posiciones) {
        int total = 0;
        for (int i = 0; i < posiciones.tamaño(); i++) total += capacidadLibre(posiciones.obtener(i));
        return total;
    }

    private int capacidadLibre(Sector posicion) {
        return Math.max(0, posicion.getCapacidad() - cantidadOcupada(posicion));
    }

    private int cantidadOcupada(Sector posicion) {
        final int[] total = {0};
        registros.inOrder(registro -> {
            StockUbicado stock = registro.getStockUbicado(posicion);
            if (stock != null) total[0] += stock.getCantidad();
        });
        return total[0];
    }

    private void moverRegistro(RegistroInventario registro, ListaArray<Sector> origenes,
            ListaArray<Sector> destinos) {
        ListaArray<StockUbicado> ubicaciones = registro.getUbicaciones();
        int originales = ubicaciones.tamaño();
        for (int i = 0; i < originales; i++) {
            StockUbicado origen = ubicaciones.obtener(i);
            int pendiente = contiene(origenes, origen.getPosicion()) ? origen.getCantidad() : 0;
            for (int j = 0; j < destinos.tamaño() && pendiente > 0; j++) {
                Sector destino = destinos.obtener(j);
                int mover = Math.min(pendiente, capacidadLibre(destino));
                if (mover > 0) {
                    StockUbicado destinoStock = registro.getStockUbicado(destino);
                    if (destinoStock == null) ubicaciones.agregar(new StockUbicado(destino, mover));
                    else destinoStock.setCantidad(destinoStock.getCantidad() + mover);
                    origen.setCantidad(origen.getCantidad() - mover);
                    pendiente -= mover;
                }
            }
        }
        for (int i = originales - 1; i >= 0; i--) {
            StockUbicado stock = ubicaciones.obtener(i);
            if (contiene(origenes, stock.getPosicion()) && stock.getCantidad() == 0) ubicaciones.remover(i);
        }
    }

    private boolean contiene(ListaArray<Sector> sectores, Sector sector) {
        if (sector == null || sector.getCodigo() == null) return false;
        for (int i = 0; i < sectores.tamaño(); i++) {
            if (sector.getCodigo().equals(sectores.obtener(i).getCodigo())) return true;
        }
        return false;
    }

    private void validarProducto(Producto producto) {
        if (producto == null || producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El producto debe tener un código");
        }
    }
}
