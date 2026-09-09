package ucu.edu.uy;

import java.time.LocalDateTime;
import java.util.Scanner;

import ucu.edu.aed.tda.TDANodoNario;
import ucu.edu.almacen.AlmacenLogistico;
import ucu.edu.almacen.Deposito;
import ucu.edu.almacen.DetalleProducto;
import ucu.edu.almacen.EntregaProveedor;
import ucu.edu.almacen.Inventario;
import ucu.edu.almacen.OperacionCarga;
import ucu.edu.almacen.PasoRecoleccion;
import ucu.edu.almacen.PedidoSucursal;
import ucu.edu.almacen.Producto;
import ucu.edu.almacen.Proveedor;
import ucu.edu.almacen.RegistroInventario;
import ucu.edu.almacen.Sector;
import ucu.edu.almacen.StockUbicado;
import ucu.edu.almacen.Sucursal;
import ucu.edu.almacen.TerminalCarga;
import ucu.edu.almacen.TipoSector;
import ucu.edu.implementaciones.ListaArray;

/**
 * Consola interactiva del sistema de almacén logístico.
 *
 * <p>El menú se organiza en secciones (productos, proveedores, sucursales,
 * depósito y terminales) en vez de una única lista con todas las opciones,
 * para que cada pantalla muestre solo las acciones relacionadas entre sí.</p>
 */
public class Main {

    private static final String SEPARADOR = "============================================";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AlmacenLogistico almacen = new AlmacenLogistico(true);
        int opcion;

        do {
            mostrarMenuPrincipal();
            opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    menuProductosEInventario(scanner, almacen);
                    break;
                case 2:
                    menuProveedoresYEntregas(scanner, almacen);
                    break;
                case 3:
                    menuSucursalesYReabastecimiento(scanner, almacen);
                    break;
                case 4:
                    menuDepositoYUbicaciones(scanner, almacen);
                    break;
                case 5:
                    menuTerminalesDeCarga(scanner, almacen);
                    break;
                case 0:
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);

        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println(SEPARADOR);
        System.out.println("   SISTEMA DE ALMACÉN LOGÍSTICO");
        System.out.println(SEPARADOR);
        System.out.println("1. Productos e inventario");
        System.out.println("2. Proveedores y entregas");
        System.out.println("3. Sucursales y reabastecimiento");
        System.out.println("4. Depósito y ubicaciones");
        System.out.println("5. Terminales de carga");
        System.out.println("0. Salir");
        System.out.println(SEPARADOR);
        System.out.print("Seleccione una opción: ");
    }

    /*
            ==================================
            ====== Productos e inventario =====
            ==================================
    */

    private static void menuProductosEInventario(Scanner scanner, AlmacenLogistico almacen) {
        int opcion;

        do {
            System.out.println();
            System.out.println("---- Productos e inventario ----");
            System.out.println("1. Registrar producto nuevo");
            System.out.println("2. Registrar producto con ubicación inicial");
            System.out.println("3. Aumentar stock (sin ubicar)");
            System.out.println("4. Aumentar stock en una posición");
            System.out.println("5. Disminuir stock en una posición");
            System.out.println("6. Reubicar mercadería entre posiciones");
            System.out.println("7. Buscar producto por código");
            System.out.println("8. Consultar stock total de un producto");
            System.out.println("9. Listar inventario ordenado por código");
            System.out.println("10. Listar productos con stock bajo");
            System.out.println("11. Listar productos sin stock");
            System.out.println("12. Ver cantidad total del inventario");
            System.out.println("13. Buscar producto por código a gran escala");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    registrarProducto(scanner, almacen);
                    break;
                case 2:
                    registrarProductoConUbicacion(scanner, almacen);
                    break;
                case 3:
                    aumentarStock(scanner, almacen);
                    break;
                case 4:
                    aumentarStockEnPosicion(scanner, almacen);
                    break;
                case 5:
                    disminuirStockEnPosicion(scanner, almacen);
                    break;
                case 6:
                    reubicarMercaderia(scanner, almacen);
                    break;
                case 7:
                    buscarProductoPorCodigo(scanner, almacen);
                    break;
                case 8:
                    consultarStock(scanner, almacen);
                    break;
                case 9:
                    listarProductos(almacen);
                    break;
                case 10:
                    listarProductosStockBajo(almacen);
                    break;
                case 11:
                    listarProductosSinStock(almacen);
                    break;
                case 12:
                    verInventarioTotal(almacen);
                    break;
                case 13:
                    compararBusquedaProductoConHito1(scanner);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);
    }

    private static void registrarProducto(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del producto: ");

        if (almacen.buscarProducto(codigo) != null) {
            System.out.println("Ya existe un producto con ese código.");
            return;
        }

        String nombre = leerTexto(scanner, "Nombre del producto: ");

        try {
            almacen.registrarProducto(new Producto(codigo, nombre));
            System.out.println("Producto registrado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarProductoConUbicacion(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del producto: ");

        if (almacen.buscarProducto(codigo) != null) {
            System.out.println("Ya existe un producto con ese código.");
            return;
        }

        String nombre = leerTexto(scanner, "Nombre del producto: ");

        mostrarEstructuraDeposito(almacen);
        Sector posicion = pedirPosicion(scanner, almacen, "Código de la posición donde se guarda: ");

        if (posicion == null) {
            return;
        }

        Integer cantidad = leerEntero(scanner, "Cantidad inicial: ");

        if (cantidad == null || cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a cero.");
            return;
        }

        try {
            almacen.getInventario().registrarProducto(
                    new Producto(codigo, nombre), new StockUbicado(posicion, cantidad));
            System.out.println("Producto registrado y ubicado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void aumentarStock(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del producto: ");

        if (almacen.buscarProducto(codigo) == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        Integer cantidad = leerEntero(scanner, "Ingrese la cantidad a sumar: ");

        if (cantidad == null) {
            return;
        }

        try {
            almacen.aumentarStock(codigo, cantidad);
            System.out.println("Stock actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void aumentarStockEnPosicion(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del producto: ");

        if (almacen.buscarProducto(codigo) == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        Sector posicion = pedirPosicion(scanner, almacen, "Código de la posición: ");

        if (posicion == null) {
            return;
        }

        Integer cantidad = leerEntero(scanner, "Cantidad a sumar: ");

        if (cantidad == null || cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a cero.");
            return;
        }

        try {
            almacen.getInventario().aumentarStock(codigo, cantidad, posicion);
            System.out.println("Stock actualizado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void disminuirStockEnPosicion(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del producto: ");

        if (almacen.buscarProducto(codigo) == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        Sector posicion = pedirPosicion(scanner, almacen, "Código de la posición: ");

        if (posicion == null) {
            return;
        }

        Integer cantidad = leerEntero(scanner, "Cantidad a restar: ");

        if (cantidad == null || cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a cero.");
            return;
        }

        boolean ok = almacen.getInventario().disminuirStock(codigo, cantidad, posicion);
        System.out.println(ok
                ? "Stock actualizado correctamente."
                : "No hay stock suficiente de ese producto en esa posición.");
    }

    private static void reubicarMercaderia(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del producto: ");

        if (almacen.buscarProducto(codigo) == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        mostrarEstructuraDeposito(almacen);
        Sector origen = pedirPosicion(scanner, almacen, "Código de la posición de origen: ");

        if (origen == null) {
            return;
        }

        Sector destino = pedirPosicion(scanner, almacen, "Código de la posición de destino: ");

        if (destino == null) {
            return;
        }

        Integer cantidad = leerEntero(scanner, "Cantidad a reubicar: ");

        if (cantidad == null || cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a cero.");
            return;
        }

        boolean ok = almacen.getInventario().reubicarMercaderia(codigo, origen, destino, cantidad);
        System.out.println(ok
                ? "Mercadería reubicada correctamente."
                : "No se pudo reubicar (verifique el stock en origen y la capacidad del destino).");
    }

    private static void buscarProductoPorCodigo(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del producto a buscar: ");
        DetalleProducto detalle = almacen.buscarProducto(codigo);

        if (detalle == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        System.out.println(detalle);
    }

    private static void consultarStock(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del producto: ");

        if (almacen.buscarProducto(codigo) == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        System.out.println("Cantidad de stock disponible: " + almacen.obtenerStockProducto(codigo));
    }

    private static void listarProductos(AlmacenLogistico almacen) {
        almacen.listarProductosYStock();
    }

    private static void listarProductosStockBajo(AlmacenLogistico almacen) {
        ListaArray<Producto> productos = almacen.productosConStockBajo();

        if (productos.esVacio()) {
            System.out.println("No hay productos con stock bajo.");
            return;
        }

        for (int i = 0; i < productos.tamaño(); i++) {
            System.out.println(productos.obtener(i));
        }
    }

    private static void listarProductosSinStock(AlmacenLogistico almacen) {
        ListaArray<Producto> productos = almacen.productosSinStock();

        if (productos.esVacio()) {
            System.out.println("No hay productos sin stock.");
            return;
        }

        for (int i = 0; i < productos.tamaño(); i++) {
            System.out.println(productos.obtener(i));
        }
    }

    private static void verInventarioTotal(AlmacenLogistico almacen) {
        System.out.println("Cantidad de inventario total: " + almacen.cantidadInventarioTotal());
    }

    /**
     * Compara buscarProducto tal como funcionaba en el Hito 1 (recorrido lineal
     * sobre ListaArray) contra la implementación actual de Inventario, que por
     * dentro usa el árbol AVL — la misma función que corre la opción 7 de este
     * menú, solo que acá se prueba con un volumen grande para que la diferencia
     * se note.
     *
     * <p>Arma su propio inventario de prueba: no toca el inventario real que ya
     * hayan cargado en esta sesión.</p>
     */
    private static void compararBusquedaProductoConHito1(Scanner scanner) {
        Integer cantidad = leerEntero(scanner, "¿Con cuántos productos simular la búsqueda? (ej: 10000): ");

        if (cantidad == null || cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a cero.");
            return;
        }

        System.out.println("Cargando " + cantidad
                + " productos en un inventario de prueba (no afecta el inventario actual)...");

        // Inventario real del proyecto: buscarProducto busca por dentro con ArbolAVL.
        Inventario inventarioActual = new Inventario();
        // Reconstrucción de cómo resolvía la misma búsqueda el Hito 1: recorrido lineal.
        ListaArray<RegistroInventario> inventarioHito1 = new ListaArray<>();

        for (int i = 1; i <= cantidad; i++) {
            Producto producto = new Producto(String.format("P%07d", i), "Producto " + i);
            inventarioActual.registrarProducto(producto);
            inventarioHito1.agregar(new RegistroInventario(producto));
        }

        // Código que no existe: peor caso real para las dos formas de buscar.
        String codigoBuscado = "P9999999";

        final int repeticiones = 5;
        long[] tiemposHito1 = new long[repeticiones];
        long[] tiemposActual = new long[repeticiones];

        for (int i = 0; i < repeticiones; i++) {
            long inicioHito1 = System.nanoTime();
            inventarioHito1.buscar(r -> r.getProducto().getCodigo().equals(codigoBuscado));
            tiemposHito1[i] = System.nanoTime() - inicioHito1;

            long inicioActual = System.nanoTime();
            inventarioActual.buscarProducto(codigoBuscado);
            tiemposActual[i] = System.nanoTime() - inicioActual;
        }

        double medianaHito1 = medianaEnMs(tiemposHito1);
        double medianaActual = medianaEnMs(tiemposActual);

        System.out.println();
        System.out.println("Buscando el código " + codigoBuscado + " (no existe: peor caso para las dos formas de buscar)");
        System.out.printf("buscarProducto en el Hito 1 (recorrido lineal, ListaArray): %.3f ms%n", medianaHito1);
        System.out.printf("buscarProducto ahora (Inventario, árbol AVL):               %.3f ms%n", medianaActual);
        System.out.printf("La implementación actual fue %.1f veces más rápida.%n", medianaHito1 / medianaActual);
    }

    private static double medianaEnMs(long[] tiemposNanos) {
        long[] copia = tiemposNanos.clone();
        java.util.Arrays.sort(copia);
        long medianaNanos = copia[copia.length / 2];
        return medianaNanos / 1_000_000.0;
    }

    /*
            ==============================================
            ====== Proveedores y entregas ======
            ==============================================
    */

    private static void menuProveedoresYEntregas(Scanner scanner, AlmacenLogistico almacen) {
        int opcion;

        do {
            System.out.println();
            System.out.println("---- Proveedores y entregas ----");
            System.out.println("1. Registrar entrega de proveedor (sin ubicar)");
            System.out.println("2. Registrar entrega de proveedor con posiciones de guardado");
            System.out.println("3. Ver próxima entrega pendiente");
            System.out.println("4. Descargar próxima entrega (sin ubicar)");
            System.out.println("5. Descargar próxima entrega ubicada");
            System.out.println("6. Ver proveedores con entregas pendientes");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    registrarEntregaProveedor(scanner, almacen);
                    break;
                case 2:
                    registrarEntregaProveedorUbicada(scanner, almacen);
                    break;
                case 3:
                    verSiguienteEntregaProveedor(almacen);
                    break;
                case 4:
                    descargarSiguienteEntregaProveedor(almacen);
                    break;
                case 5:
                    descargarSiguienteEntregaProveedorUbicada(almacen);
                    break;
                case 6:
                    verProveedoresPendientes(almacen);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);
    }

    private static void registrarEntregaProveedor(Scanner scanner, AlmacenLogistico almacen) {
        String codigoProveedor = leerTexto(scanner, "Código del proveedor: ");
        String nombreProveedor = leerTexto(scanner, "Nombre del proveedor: ");

        Integer cantidadProductos = leerEntero(scanner, "Cantidad de tipos de productos entregados: ");

        if (cantidadProductos == null || cantidadProductos <= 0) {
            System.out.println("La entrega debe incluir al menos un producto.");
            return;
        }

        ListaArray<DetalleProducto> productosEntrega = new ListaArray<>();

        for (int i = 0; i < cantidadProductos; i++) {
            System.out.println("Producto " + (i + 1) + ":");
            String codigoProducto = leerTexto(scanner, "  Código: ");
            DetalleProducto detalleInventario = almacen.buscarProducto(codigoProducto);

            if (detalleInventario == null) {
                System.out.println("El producto no está registrado en el almacén.");
                return;
            }

            Integer cantidad = leerEntero(scanner, "  Cantidad entregada: ");

            if (cantidad == null || cantidad <= 0) {
                System.out.println("La cantidad debe ser mayor a cero.");
                return;
            }

            DetalleProducto detalleEntrega = new DetalleProducto();
            detalleEntrega.setProducto(detalleInventario.getProducto());
            detalleEntrega.setCantidad(cantidad);
            productosEntrega.agregar(detalleEntrega);
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setCodigo(codigoProveedor);
        proveedor.setNombre(nombreProveedor);

        EntregaProveedor entrega = new EntregaProveedor();
        entrega.setProveedor(proveedor);
        entrega.setProductos(productosEntrega);
        entrega.setFecha(LocalDateTime.now());

        try {
            almacen.registrarEntregaProveedor(entrega);
            System.out.println("Entrega registrada correctamente.");
        } catch (UnsupportedOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarEntregaProveedorUbicada(Scanner scanner, AlmacenLogistico almacen) {
        String codigoProveedor = leerTexto(scanner, "Código del proveedor: ");
        String nombreProveedor = leerTexto(scanner, "Nombre del proveedor: ");

        Integer cantidadProductos = leerEntero(scanner, "Cantidad de tipos de productos entregados: ");

        if (cantidadProductos == null || cantidadProductos <= 0) {
            System.out.println("La entrega debe incluir al menos un producto.");
            return;
        }

        mostrarEstructuraDeposito(almacen);

        ListaArray<RegistroInventario> mercaderiaUbicada = new ListaArray<>();
        ListaArray<DetalleProducto> resumen = new ListaArray<>();

        for (int i = 0; i < cantidadProductos; i++) {
            System.out.println("Producto " + (i + 1) + ":");
            String codigoProducto = leerTexto(scanner, "  Código: ");
            DetalleProducto detalleInventario = almacen.buscarProducto(codigoProducto);

            if (detalleInventario == null) {
                System.out.println("El producto no está registrado en el almacén.");
                return;
            }

            Integer cantidadPosiciones = leerEntero(scanner,
                    "  ¿En cuántas posiciones distintas se guarda?: ");

            if (cantidadPosiciones == null || cantidadPosiciones <= 0) {
                System.out.println("Debe indicar al menos una posición.");
                return;
            }

            RegistroInventario registro = new RegistroInventario(detalleInventario.getProducto());
            int totalProducto = 0;

            for (int j = 0; j < cantidadPosiciones; j++) {
                Sector posicion = pedirPosicion(scanner, almacen,
                        "  Código de la posición " + (j + 1) + ": ");

                if (posicion == null) {
                    return;
                }

                Integer cantidad = leerEntero(scanner, "  Cantidad en esa posición: ");

                if (cantidad == null || cantidad <= 0) {
                    System.out.println("La cantidad debe ser mayor a cero.");
                    return;
                }

                registro.getUbicaciones().agregar(new StockUbicado(posicion, cantidad));
                totalProducto += cantidad;
            }

            mercaderiaUbicada.agregar(registro);

            DetalleProducto detalleResumen = new DetalleProducto();
            detalleResumen.setProducto(detalleInventario.getProducto());
            detalleResumen.setCantidad(totalProducto);
            resumen.agregar(detalleResumen);
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setCodigo(codigoProveedor);
        proveedor.setNombre(nombreProveedor);

        EntregaProveedor entrega = new EntregaProveedor();
        entrega.setProveedor(proveedor);
        entrega.setProductos(resumen);
        entrega.setMercaderiaUbicada(mercaderiaUbicada);
        entrega.setFecha(LocalDateTime.now());

        try {
            almacen.registrarEntregaProveedor(entrega);
            System.out.println("Entrega ubicada registrada correctamente.");
        } catch (UnsupportedOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void verSiguienteEntregaProveedor(AlmacenLogistico almacen) {
        if (!almacen.hayEntregasPendientes()) {
            System.out.println("No hay entregas de proveedores pendientes.");
            return;
        }

        System.out.println(almacen.obtenerSiguienteEntregaProveedor());
    }

    private static void descargarSiguienteEntregaProveedor(AlmacenLogistico almacen) {
        if (!almacen.hayEntregasPendientes()) {
            System.out.println("No hay entregas de proveedores pendientes.");
            return;
        }

        try {
            EntregaProveedor entrega = almacen.descargarSiguienteEntregaProveedor();
            System.out.println("Entrega descargada correctamente:");
            System.out.println(entrega);
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo descargar la entrega: " + e.getMessage());
        }
    }

    private static void descargarSiguienteEntregaProveedorUbicada(AlmacenLogistico almacen) {
        if (!almacen.hayEntregasPendientes()) {
            System.out.println("No hay entregas de proveedores pendientes.");
            return;
        }

        try {
            EntregaProveedor entrega = almacen.descargarSiguienteEntregaProveedorUbicada();
            System.out.println("Entrega ubicada descargada correctamente:");
            System.out.println(entrega);
        } catch (IllegalStateException e) {
            System.out.println("No se pudo descargar: " + e.getMessage());
        }
    }

    private static void verProveedoresPendientes(AlmacenLogistico almacen) {
        ListaArray<Proveedor> proveedores = almacen.proveedoresConEntregasPendientes();

        if (proveedores.esVacio()) {
            System.out.println("No hay proveedores con entregas pendientes.");
            return;
        }

        for (int i = 0; i < proveedores.tamaño(); i++) {
            System.out.println(proveedores.obtener(i));
        }
    }

    /*
            ============================================================
            ====== Sucursales y reabastecimiento ======
            ============================================================
    */

    private static void menuSucursalesYReabastecimiento(Scanner scanner, AlmacenLogistico almacen) {
        int opcion;

        do {
            System.out.println();
            System.out.println("---- Sucursales y reabastecimiento ----");
            System.out.println("1. Registrar pedido de reabastecimiento");
            System.out.println("2. Ver siguiente pedido prioritario");
            System.out.println("3. Despachar siguiente pedido (sin ubicar)");
            System.out.println("4. Preparar y despachar un pedido ubicado (con ruta de recolección)");
            System.out.println("5. Modificar la prioridad de un pedido pendiente");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    registrarPedidoReabastecimiento(scanner, almacen);
                    break;
                case 2:
                    verSiguientePedido(almacen);
                    break;
                case 3:
                    despacharSiguientePedido(almacen);
                    break;
                case 4:
                    prepararYDespacharPedidoUbicado(scanner, almacen);
                    break;
                case 5:
                    modificarPrioridadPedido(scanner, almacen);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);
    }

    private static void registrarPedidoReabastecimiento(Scanner scanner, AlmacenLogistico almacen) {
        String codigoSucursal = leerTexto(scanner, "Código de la sucursal: ");
        String nombreSucursal = leerTexto(scanner, "Nombre de la sucursal: ");
        Integer clientesPromedio = leerEntero(scanner, "Clientes promedio: ");
        Integer cantidadProductos = leerEntero(scanner, "Cantidad de tipos de productos solicitados: ");

        if (clientesPromedio == null || clientesPromedio < 0
                || cantidadProductos == null || cantidadProductos <= 0) {
            System.out.println("Los datos ingresados no son válidos.");
            return;
        }

        ListaArray<DetalleProducto> productosPedido = new ListaArray<>();

        for (int i = 0; i < cantidadProductos; i++) {
            System.out.println("Producto " + (i + 1) + ":");
            String codigoProducto = leerTexto(scanner, "  Código: ");
            DetalleProducto detalleInventario = almacen.buscarProducto(codigoProducto);

            if (detalleInventario == null) {
                System.out.println("El producto no está registrado en el almacén.");
                return;
            }

            Integer cantidad = leerEntero(scanner, "  Cantidad solicitada: ");

            if (cantidad == null || cantidad <= 0) {
                System.out.println("La cantidad debe ser mayor a cero.");
                return;
            }

            DetalleProducto detallePedido = new DetalleProducto();
            detallePedido.setProducto(detalleInventario.getProducto());
            detallePedido.setCantidad(cantidad);
            productosPedido.agregar(detallePedido);
        }

        Sucursal sucursal = new Sucursal();
        sucursal.setCodigo(codigoSucursal);
        sucursal.setNombre(nombreSucursal);
        sucursal.setClientesPromedio(clientesPromedio);

        PedidoSucursal pedido = new PedidoSucursal();
        pedido.setSucursal(sucursal);
        pedido.setProductos(productosPedido);
        pedido.setFecha(LocalDateTime.now());

        try {
            almacen.registrarPedidoReabastecimiento(pedido);
            System.out.println("Pedido registrado correctamente. Prioridad: " + pedido.getPrioridad());
        } catch (UnsupportedOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void verSiguientePedido(AlmacenLogistico almacen) {
        PedidoSucursal pedido = almacen.obtenerSiguientePedidoReabastecimiento();

        if (pedido == null) {
            System.out.println("No hay pedidos de reabastecimiento pendientes.");
            return;
        }

        System.out.println(pedido);
    }

    private static void despacharSiguientePedido(AlmacenLogistico almacen) {
        if (!almacen.hayPedidosReabastecimientoPendientes()) {
            System.out.println("No hay pedidos de reabastecimiento pendientes.");
            return;
        }

        PedidoSucursal pedido = almacen.despacharSiguientePedidoReabastecimiento();

        if (pedido == null) {
            System.out.println("No hay stock suficiente para despachar el pedido prioritario.");
            return;
        }

        System.out.println("Pedido despachado correctamente:");
        System.out.println(pedido);
    }

    private static void prepararYDespacharPedidoUbicado(Scanner scanner, AlmacenLogistico almacen) {
        String codigoSucursal = leerTexto(scanner, "Código de la sucursal: ");
        String nombreSucursal = leerTexto(scanner, "Nombre de la sucursal: ");
        Integer clientesPromedio = leerEntero(scanner, "Clientes promedio: ");
        Integer cantidadProductos = leerEntero(scanner, "Cantidad de tipos de productos solicitados: ");

        if (clientesPromedio == null || clientesPromedio < 0
                || cantidadProductos == null || cantidadProductos <= 0) {
            System.out.println("Los datos ingresados no son válidos.");
            return;
        }

        ListaArray<DetalleProducto> productosPedido = new ListaArray<>();

        for (int i = 0; i < cantidadProductos; i++) {
            System.out.println("Producto " + (i + 1) + ":");
            String codigoProducto = leerTexto(scanner, "  Código: ");
            DetalleProducto detalleInventario = almacen.buscarProducto(codigoProducto);

            if (detalleInventario == null) {
                System.out.println("El producto no está registrado en el almacén.");
                return;
            }

            Integer cantidad = leerEntero(scanner, "  Cantidad solicitada: ");

            if (cantidad == null || cantidad <= 0) {
                System.out.println("La cantidad debe ser mayor a cero.");
                return;
            }

            DetalleProducto detallePedido = new DetalleProducto();
            detallePedido.setProducto(detalleInventario.getProducto());
            detallePedido.setCantidad(cantidad);
            productosPedido.agregar(detallePedido);
        }

        Sucursal sucursal = new Sucursal();
        sucursal.setCodigo(codigoSucursal);
        sucursal.setNombre(nombreSucursal);
        sucursal.setClientesPromedio(clientesPromedio);

        PedidoSucursal pedido = new PedidoSucursal();
        pedido.setSucursal(sucursal);
        pedido.setProductos(productosPedido);
        pedido.setFecha(LocalDateTime.now());

        ListaArray<PasoRecoleccion> pasos;

        try {
            pasos = almacen.getInventario().prepararPedidoUbicado(almacen.getDeposito(), pedido);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo preparar el pedido: " + e.getMessage());
            return;
        }

        System.out.println("Ruta de recolección (posiciones en el orden en que hay que recorrerlas):");

        for (int i = 0; i < pasos.tamaño(); i++) {
            PasoRecoleccion paso = pasos.obtener(i);
            Sector posicion = paso.getStockOrigen().getPosicion();
            System.out.println("  " + (i + 1) + ". Retirar " + paso.getCantidad() + " de "
                    + paso.getProducto().getNombre() + " desde " + posicion.getNombre()
                    + " [" + posicion.getCodigo() + "]");
        }

        String confirmacion = leerTexto(scanner, "¿Confirmar despacho? (s/n): ");

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Despacho cancelado.");
            return;
        }

        boolean ok = almacen.getInventario().despacharPedidoUbicado(almacen.getDeposito(), pedido);
        System.out.println(ok
                ? "Pedido despachado correctamente."
                : "No se pudo completar el despacho (el stock cambió entre la preparación y el despacho).");
    }

    private static void modificarPrioridadPedido(Scanner scanner, AlmacenLogistico almacen) {
        String codigoSucursal = leerTexto(scanner, "Código de la sucursal: ");
        Integer nuevaPrioridad = leerEntero(scanner, "Nueva prioridad: ");

        if (nuevaPrioridad == null || nuevaPrioridad < 0) {
            System.out.println("La prioridad debe ser un número entero no negativo.");
            return;
        }

        boolean ok = almacen.modificarPrioridadPedido(codigoSucursal, nuevaPrioridad);
        System.out.println(ok
                ? "Prioridad actualizada correctamente."
                : "No se encontró un pedido pendiente de esa sucursal.");
    }

    /*
            ==================================
            ====== Depósito y ubicaciones =====
            ==================================
    */

    private static void menuDepositoYUbicaciones(Scanner scanner, AlmacenLogistico almacen) {
        int opcion;

        do {
            System.out.println();
            System.out.println("---- Depósito y ubicaciones ----");
            System.out.println("1. Crear el depósito (sector raíz)");
            System.out.println("2. Agregar un sector");
            System.out.println("3. Ver estructura del depósito");
            System.out.println("4. Buscar un sector por código");
            System.out.println("5. Mover un sector a otro punto del depósito");
            System.out.println("6. Inhabilitar un sector");
            System.out.println("7. Consultar capacidad y ocupación de un sector");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    crearDeposito(scanner, almacen);
                    break;
                case 2:
                    agregarSector(scanner, almacen);
                    break;
                case 3:
                    mostrarEstructuraDeposito(almacen);
                    break;
                case 4:
                    buscarSector(scanner, almacen);
                    break;
                case 5:
                    moverSector(scanner, almacen);
                    break;
                case 6:
                    inhabilitarSector(scanner, almacen);
                    break;
                case 7:
                    consultarCapacidadYOcupacion(scanner, almacen);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);
    }

    private static void crearDeposito(Scanner scanner, AlmacenLogistico almacen) {
        Deposito deposito = almacen.getDeposito();

        if (!deposito.getSectores().esVacio()) {
            System.out.println("El depósito ya tiene un sector raíz.");
            return;
        }

        String codigo = leerTexto(scanner, "Código del depósito: ");
        String nombre = leerTexto(scanner, "Nombre del depósito: ");

        deposito.getSectores().agregarHijo(null, new Sector(codigo, nombre, TipoSector.DEPOSITO, 0));
        System.out.println("Depósito creado correctamente.");
    }

    private static void agregarSector(Scanner scanner, AlmacenLogistico almacen) {
        Deposito deposito = almacen.getDeposito();

        if (deposito.getSectores().esVacio()) {
            System.out.println("Primero hay que crear el sector raíz del depósito (opción 1).");
            return;
        }

        mostrarEstructuraDeposito(almacen);
        String codigoPadre = leerTexto(scanner, "Código del sector padre: ");
        TDANodoNario<Sector> nodoPadre = deposito.getSectores()
                .buscar(sector -> codigoPadre.equals(sector.getCodigo()));

        if (nodoPadre == null) {
            System.out.println("No existe un sector con ese código.");
            return;
        }

        String codigo = leerTexto(scanner, "Código del nuevo sector: ");

        if (deposito.buscarSector(codigo) != null) {
            System.out.println("Ya existe un sector con ese código.");
            return;
        }

        String nombre = leerTexto(scanner, "Nombre del nuevo sector: ");
        TipoSector tipo = elegirTipoSector(scanner);

        if (tipo == null) {
            return;
        }

        int capacidad = 0;

        if (tipo == TipoSector.POSICION) {
            Integer capacidadIngresada = leerEntero(scanner,
                    "Capacidad de la posición (unidades de producto): ");

            if (capacidadIngresada == null || capacidadIngresada < 0) {
                System.out.println("La capacidad debe ser un número entero no negativo.");
                return;
            }

            capacidad = capacidadIngresada;
        }

        deposito.getSectores().agregarHijo(nodoPadre, new Sector(codigo, nombre, tipo, capacidad));
        System.out.println("Sector agregado correctamente.");
    }

    private static void mostrarEstructuraDeposito(AlmacenLogistico almacen) {
        TDANodoNario<Sector> raiz = almacen.getDeposito().getSectores().obtenerRaiz();

        if (raiz == null) {
            System.out.println("El depósito todavía no tiene sectores. Empiece creando el sector raíz.");
            return;
        }

        System.out.println("--- Estructura del depósito ---");
        imprimirSector(raiz, 0);
    }

    private static void imprimirSector(TDANodoNario<Sector> nodo, int profundidad) {
        Sector sector = nodo.getDato();
        String sangria = "  ".repeat(profundidad);
        String capacidad = sector.getTipo() == TipoSector.POSICION
                ? " (capacidad " + sector.getCapacidad() + ")"
                : "";
        String estado = sector.isHabilitado() ? "" : " [INHABILITADO]";

        System.out.println(sangria + "- " + sector.getNombre() + " [" + sector.getCodigo() + "] "
                + sector.getTipo() + capacidad + estado);

        for (int i = 0; i < nodo.cantidadHijosDirectos(); i++) {
            imprimirSector(nodo.obtenerHijo(i), profundidad + 1);
        }
    }

    private static void buscarSector(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del sector: ");
        Sector sector = almacen.getDeposito().buscarSector(codigo);

        if (sector == null) {
            System.out.println("No existe un sector con ese código.");
            return;
        }

        System.out.println("Nombre: " + sector.getNombre());
        System.out.println("Tipo: " + sector.getTipo());
        System.out.println("Capacidad: " + sector.getCapacidad());
        System.out.println("Habilitado: " + (sector.isHabilitado() ? "sí" : "no"));
    }

    private static void moverSector(Scanner scanner, AlmacenLogistico almacen) {
        mostrarEstructuraDeposito(almacen);
        String codigoSector = leerTexto(scanner, "Código del sector a mover: ");
        String codigoNuevoPadre = leerTexto(scanner, "Código del nuevo sector padre: ");

        boolean ok = almacen.getDeposito().moverSector(codigoSector, codigoNuevoPadre);
        System.out.println(ok
                ? "Sector movido correctamente."
                : "No se pudo mover el sector (verifique los códigos, que no sea la raíz, "
                        + "o que el destino no esté dentro de su propio subárbol).");
    }

    private static void inhabilitarSector(Scanner scanner, AlmacenLogistico almacen) {
        mostrarEstructuraDeposito(almacen);
        String codigo = leerTexto(scanner, "Código del sector a inhabilitar: ");

        boolean ok = almacen.getInventario().inhabilitarSector(almacen.getDeposito(), codigo);
        System.out.println(ok
                ? "Sector inhabilitado y mercadería reubicada correctamente."
                : "No se pudo inhabilitar (no existe, ya está inhabilitado, o no hay capacidad "
                        + "disponible fuera de él para reubicar su mercadería).");
    }

    private static void consultarCapacidadYOcupacion(Scanner scanner, AlmacenLogistico almacen) {
        String codigo = leerTexto(scanner, "Código del sector a consultar: ");

        if (almacen.getDeposito().buscarSector(codigo) == null) {
            System.out.println("No existe un sector con ese código.");
            return;
        }

        Inventario inventario = almacen.getInventario();
        Deposito deposito = almacen.getDeposito();

        System.out.println("Capacidad total (posiciones del subárbol): "
                + inventario.obtenerCapacidadTotal(deposito, codigo));
        System.out.println("Ocupación actual: " + inventario.obtenerOcupacion(deposito, codigo));
        System.out.println("Capacidad disponible: "
                + inventario.obtenerCapacidadDisponible(deposito, codigo));
    }

    /** Busca una posición por código, o informa el error e indica {@code null}. */
    private static Sector pedirPosicion(Scanner scanner, AlmacenLogistico almacen, String mensaje) {
        String codigo = leerTexto(scanner, mensaje);
        Sector posicion = almacen.getDeposito().buscarSector(codigo);

        if (posicion == null) {
            System.out.println("No existe un sector con ese código.");
            return null;
        }

        return posicion;
    }

    private static TipoSector elegirTipoSector(Scanner scanner) {
        System.out.println("Tipo de sector:");
        System.out.println("  1. Zona");
        System.out.println("  2. Pasillo");
        System.out.println("  3. Estantería");
        System.out.println("  4. Bandeja");
        System.out.println("  5. Posición");
        Integer opcion = leerEntero(scanner, "Opción: ");

        if (opcion == null) {
            return null;
        }

        switch (opcion) {
            case 1:
                return TipoSector.ZONA;
            case 2:
                return TipoSector.PASILLO;
            case 3:
                return TipoSector.ESTANTERIA;
            case 4:
                return TipoSector.BANDEJA;
            case 5:
                return TipoSector.POSICION;
            default:
                System.out.println("Opción inválida.");
                return null;
        }
    }

    /*
            ==================================
            ====== Terminales de carga =====
            ==================================
    */

    private static void menuTerminalesDeCarga(Scanner scanner, AlmacenLogistico almacen) {
        int opcion;

        do {
            System.out.println();
            System.out.println("---- Terminales de carga ----");
            System.out.println("1. Registrar terminal");
            System.out.println("2. Listar terminales");
            System.out.println("3. Habilitar o deshabilitar terminal");
            System.out.println("4. Iniciar operación en terminal");
            System.out.println("5. Liberar terminal");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    registrarTerminal(scanner, almacen);
                    break;
                case 2:
                    listarTerminales(almacen);
                    break;
                case 3:
                    cambiarHabilitacionTerminal(scanner, almacen);
                    break;
                case 4:
                    iniciarOperacionTerminal(scanner, almacen);
                    break;
                case 5:
                    liberarTerminal(scanner, almacen);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);
    }

    private static void registrarTerminal(Scanner scanner, AlmacenLogistico almacen) {
        Integer id = leerEntero(scanner, "Id de la terminal: ");

        if (id == null) {
            return;
        }

        try {
            almacen.registrarTerminal(new TerminalCarga(id));
            System.out.println("Terminal registrada correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listarTerminales(AlmacenLogistico almacen) {
        ListaArray<TerminalCarga> terminales = almacen.obtenerTerminales();

        if (terminales.esVacio()) {
            System.out.println("No hay terminales registradas.");
            return;
        }

        for (int i = 0; i < terminales.tamaño(); i++) {
            System.out.println(terminales.obtener(i));
        }
    }

    private static void cambiarHabilitacionTerminal(Scanner scanner, AlmacenLogistico almacen) {
        Integer id = leerEntero(scanner, "Id de la terminal: ");
        Integer opcion = leerEntero(scanner, "1. Habilitar\n2. Deshabilitar\nOpción: ");

        if (id == null || opcion == null || (opcion != 1 && opcion != 2)) {
            System.out.println("Opción inválida.");
            return;
        }

        try {
            almacen.cambiarHabilitacionTerminal(id, opcion == 1);
            System.out.println("Estado de la terminal actualizado.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void iniciarOperacionTerminal(Scanner scanner, AlmacenLogistico almacen) {
        Integer id = leerEntero(scanner, "Id de la terminal: ");
        Integer opcion = leerEntero(scanner, "1. Descargar proveedor\n2. Cargar sucursal\nOpción: ");

        if (id == null || opcion == null || (opcion != 1 && opcion != 2)) {
            System.out.println("Opción inválida.");
            return;
        }

        OperacionCarga operacion = opcion == 1
                ? OperacionCarga.DESCARGANDO_PROVEEDOR
                : OperacionCarga.CARGANDO_SUCURSAL;

        try {
            almacen.iniciarOperacionEnTerminal(id, operacion);
            System.out.println("Operación iniciada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void liberarTerminal(Scanner scanner, AlmacenLogistico almacen) {
        Integer id = leerEntero(scanner, "Id de la terminal: ");

        if (id == null) {
            return;
        }

        try {
            almacen.liberarTerminal(id);
            System.out.println("Terminal liberada correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /*
            ==================================
            ====== Helpers de lectura =====
            ==================================
    */

    /** Lee la opción elegida en un menú. Devuelve -1 (opción inválida) si no es un número. */
    private static int leerOpcion(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Integer leerEntero(Scanner scanner, String mensaje) {
        System.out.print(mensaje);

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un número entero.");
            return null;
        }
    }

    private static String leerTexto(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
}
