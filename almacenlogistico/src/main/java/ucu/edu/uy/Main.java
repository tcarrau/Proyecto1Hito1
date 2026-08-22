package ucu.edu.uy;

import java.time.LocalDateTime;
import java.util.Scanner;

import ucu.edu.almacen.AlmacenLogistico;
import ucu.edu.almacen.DetalleProducto;
import ucu.edu.almacen.EntregaProveedor;
import ucu.edu.almacen.OperacionCarga;
import ucu.edu.almacen.PedidoSucursal;
import ucu.edu.almacen.Producto;
import ucu.edu.almacen.Proveedor;
import ucu.edu.almacen.Sucursal;
import ucu.edu.almacen.TerminalCarga;
import ucu.edu.implementaciones.ListaArray;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AlmacenLogistico almacen = new AlmacenLogistico(true);
        int opcion = 0;
        do {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar el número de una opción.");
                continue;
            }
            switch (opcion) {
              case 1:
                registrarProducto(scanner, almacen);
                break;
              case 2:
                aumentarStock(scanner, almacen);
                break;
              case 3:
                consultarStock(scanner, almacen);
                break;
              case 4: 
                listarProductosStockBajo(scanner, almacen);
                break;
              case 5: 
                listarProductosSinStock(scanner, almacen);
                break;
              case 6:
                registrarEntregaProveedor(scanner, almacen);
                break;
              case 7:
                verProveedoresPendientes(scanner, almacen);
                break;
              case 8:
                descargarSiguienteEntregaProveedor(almacen);
                break;
              case 9:
                registrarPedidoReabastecimiento(scanner, almacen);
                break;
              case 10:
                verSiguientePedido(scanner, almacen);
                break;
              case 11:
                despacharSiguientePedido(almacen);
                break;
              case 12:
                verInventarioTotal(scanner, almacen);
                break;

              case 13:
                listarProductos(scanner, almacen);
                break;
              case 14:
                registrarTerminal(scanner, almacen);
                break;
              case 15:
                listarTerminales(almacen);
                break;
              case 16:
                cambiarHabilitacionTerminal(scanner, almacen);
                break;
              case 17:
                iniciarOperacionTerminal(scanner, almacen);
                break;
              case 18:
                liberarTerminal(scanner, almacen);
                break;
              default:
                  break;
            }
        } while (opcion != 0);
    }
    public static void mostrarMenu() {
        System.out.println("SISTEMA DE ALMACÉN LOGÍSTICO");
        System.out.println("1. Registrar producto");
        System.out.println("2. Aumentar stock manualmente");
        System.out.println("3. Consultar stock de un producto");
        System.out.println("4. Listar productos con stock bajo");
        System.out.println("5. Listar productos sin stock");
        System.out.println("6. Registrar entrega de proveedor");
        System.out.println("7. Ver proveedores con entregas pendientes");
        System.out.println("8. Descargar siguiente entrega de proveedor");
        System.out.println("9. Registrar pedido de reabastecimiento");
        System.out.println("10. Ver siguiente pedido prioritario");
        System.out.println("11. Despachar siguiente pedido");
        System.out.println("12. Ver cantidad total del inventario");
        System.out.println("13. listar productos");
        System.out.println("14. Registrar terminal de carga");
        System.out.println("15. Listar terminales de carga");
        System.out.println("16. Habilitar o deshabilitar terminal");
        System.out.println("17. Iniciar operación en terminal");
        System.out.println("18. Liberar terminal");       
        System.out.println("0. Salir");
        System.out.println("====================================");
        System.out.print("Seleccione una opción: ");
    }

    public int PedirCodigo(Scanner scanner){
        System.out.println("ingrese codigo del Producto");
        int codigo = scanner.nextInt();
        return codigo;
    }

    private static void registrarProducto(Scanner scanner, AlmacenLogistico almacen) {
        System.out.print("Código del producto: ");
        String codigo = scanner.nextLine();

        if (almacen.buscarProducto(codigo) != null) {
            System.out.println("Ya existe un producto con ese código.");
            return;
        }

        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine();

        try {
            almacen.registrarProducto(new Producto(codigo, nombre));
            System.out.println("Producto registrado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void aumentarStock(Scanner scanner, AlmacenLogistico almacen){
        System.out.print("Código del producto: ");
            String codigo = scanner.nextLine();

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

    private static void consultarStock(Scanner scanner, AlmacenLogistico almacen ){
        System.out.println("Codigo del producto");
        String codigo = scanner.nextLine();

        if (almacen.buscarProducto(codigo) == null) {
            System.out.println("No existe un producto con ese código.");
            return;
        }

        System.out.println("Cantidad de Stock dispoinble : " + almacen.obtenerStockProducto(codigo));
    }

    private static void listarProductosStockBajo(Scanner scanner, AlmacenLogistico almacen){
        ListaArray<Producto> productos = almacen.productosConStockBajo();
        
        for(int i = 0; i < productos.tamaño(); i++){
            System.out.println(productos.obtener(i).toString());
        }
    }

    private static void listarProductosSinStock(Scanner scanner, AlmacenLogistico almacen){
        ListaArray<Producto> productos = almacen.productosSinStock();
        
        for(int i = 0; i < productos.tamaño(); i++){
            System.out.println(productos.obtener(i).toString());
        }
    }

    private static void registrarEntregaProveedor(Scanner scanner, AlmacenLogistico almacen){
        System.out.print("Código del proveedor: ");
        String codigoProveedor = scanner.nextLine();
        System.out.print("Nombre del proveedor: ");
        String nombreProveedor = scanner.nextLine();

        System.out.print("Cantidad de tipos de productos entregados: ");
        int cantidadProductos;

        try {
            cantidadProductos = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("La cantidad debe ser un número entero.");
            return;
        }

        if (cantidadProductos <= 0) {
            System.out.println("La entrega debe incluir al menos un producto.");
            return;
        }

        ListaArray<DetalleProducto> productosEntrega = new ListaArray<>();

        for (int i = 0; i < cantidadProductos; i++) {
            System.out.println("Producto " + (i + 1) + ":");
            System.out.print("Código: ");
            String codigoProducto = scanner.nextLine();
            DetalleProducto detalleInventario = almacen.buscarProducto(codigoProducto);

            if (detalleInventario == null) {
                System.out.println("El producto no está registrado en el almacén.");
                return;
            }

            System.out.print("Cantidad entregada: ");
            int cantidad;

            try {
                cantidad = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("La cantidad debe ser un número entero.");
                return;
            }

            if (cantidad <= 0) {
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

    private static void listarProductos(Scanner scanner, AlmacenLogistico almacen){
        almacen.listarProductosYStock();
    }

    private static void verProveedoresPendientes(Scanner scanner, AlmacenLogistico almacen){
        ListaArray<Proveedor> proveedores = almacen.proveedoresConEntregasPendientes();

        for(int i = 0; i < proveedores.tamaño(); i++){
            System.out.println(proveedores.obtener(i).toString());
        }
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

    private static void registrarPedidoReabastecimiento(Scanner scanner,
            AlmacenLogistico almacen) {
        System.out.print("Código de la sucursal: ");
        String codigoSucursal = scanner.nextLine();
        System.out.print("Nombre de la sucursal: ");
        String nombreSucursal = scanner.nextLine();
        Integer clientesPromedio = leerEntero(scanner, "Clientes promedio: ");
        Integer cantidadProductos = leerEntero(scanner,
                "Cantidad de tipos de productos solicitados: ");

        if (clientesPromedio == null || clientesPromedio < 0
                || cantidadProductos == null || cantidadProductos <= 0) {
            System.out.println("Los datos ingresados no son válidos.");
            return;
        }

        ListaArray<DetalleProducto> productosPedido = new ListaArray<>();

        for (int i = 0; i < cantidadProductos; i++) {
            System.out.println("Producto " + (i + 1) + ":");
            System.out.print("Código: ");
            String codigoProducto = scanner.nextLine();
            DetalleProducto detalleInventario = almacen.buscarProducto(codigoProducto);

            if (detalleInventario == null) {
                System.out.println("El producto no está registrado en el almacén.");
                return;
            }

            Integer cantidad = leerEntero(scanner, "Cantidad solicitada: ");

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
            System.out.println("Pedido registrado correctamente. Prioridad: "
                    + pedido.getPrioridad());
        } catch (UnsupportedOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
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

    private static void cambiarHabilitacionTerminal(Scanner scanner,
            AlmacenLogistico almacen) {
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

    private static void iniciarOperacionTerminal(Scanner scanner,
            AlmacenLogistico almacen) {
        Integer id = leerEntero(scanner, "Id de la terminal: ");
        Integer opcion = leerEntero(scanner,
                "1. Descargar proveedor\n2. Cargar sucursal\nOpción: ");

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

    private static Integer leerEntero(Scanner scanner, String mensaje) {
    
        System.out.print(mensaje);

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un número entero.");
            return null;
        }
    }

    private static void verSiguientePedido(Scanner scanner, AlmacenLogistico almacen){
        PedidoSucursal pedido = almacen.obtenerSiguientePedidoReabastecimiento();

        if (pedido == null) {
            System.out.println("No hay pedidos de reabastecimiento pendientes.");
            return;
        }

        System.out.println(pedido);
    }

    private static void verInventarioTotal(Scanner scanner, AlmacenLogistico almacen){
        int total = almacen.cantidadInventarioTotal();
        System.out.println("Cantidad de inventario total : " + total);
    }
}
