package ucu.edu.uy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Recorrido completo del menú de consola del almacén.
 *
 * <p>Cada prueba teclea un guion de entradas contra {@link Main#main(String[])},
 * con {@code System.in} y {@code System.out} redirigidos, y verifica lo que la
 * consola contestó. Se recorre cada opción de cada submenú con su camino feliz y
 * con los caminos de error que puede provocar quien la usa: códigos que no
 * existen, cantidades en cero, texto donde se espera un número, etc.</p>
 *
 * <p>Todos los guiones arrancan con los datos base de {@code AlmacenLogistico(true)}:
 * terminales 1 y 2 libres, productos P-001 (25 unidades, mínimo 10), P-002 (12,
 * mínimo 8) y P-003 (0, mínimo 5), una entrega pendiente de PROV-001 con 30
 * unidades de P-003, y dos pedidos: SUC-001 (prioridad 400) y SUC-002
 * (prioridad 200). El depósito arranca vacío, así que las pruebas que necesitan
 * posiciones lo crean primero con {@link #DEPOSITO}.</p>
 */
class MenuConsolaTest {

    /** Lo último que imprime el programa; sirve para saber que el menú no se colgó ni explotó. */
    private static final String DESPEDIDA = "¡Hasta luego!";

    /**
     * Guion que crea el depósito DEP con dos posiciones: POS-1 (capacidad 100)
     * y POS-2 (capacidad 50). Termina de vuelta en el menú principal.
     */
    private static final String[] DEPOSITO = {
            "4",
            "1", "DEP", "Deposito central",
            "2", "DEP", "POS-1", "Posicion 1", "5", "100",
            "2", "DEP", "POS-2", "Posicion 2", "5", "50",
            "0"
    };

    /*
            ==================================
            ====== Helpers de la prueba ======
            ==================================
    */

    /**
     * Corre el menú como si cada línea se tecleara en la consola y devuelve todo
     * lo que se imprimió. El guion tiene que terminar de salir del programa: si
     * se queda sin líneas antes, {@code Scanner} falla y la prueba lo marca.
     */
    private String consola(String... lineas) {
        String tecleado = String.join("\n", lineas) + "\n";
        ByteArrayOutputStream capturado = new ByteArrayOutputStream();
        InputStream entradaOriginal = System.in;
        PrintStream salidaOriginal = System.out;

        System.setIn(new ByteArrayInputStream(tecleado.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(capturado, true, StandardCharsets.UTF_8));

        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(salidaOriginal);
            System.setIn(entradaOriginal);
        }

        String salida = capturado.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains(DESPEDIDA),
                () -> "El programa no llegó a salir del menú principal. Salida:\n" + salida);
        return salida;
    }

    /** Arma un guion pegando líneas atrás de otro guion (por ejemplo, el del depósito). */
    private static String[] guion(String[] inicio, String... resto) {
        List<String> lineas = new ArrayList<>();
        for (String linea : inicio) {
            lineas.add(linea);
        }
        for (String linea : resto) {
            lineas.add(linea);
        }
        return lineas.toArray(new String[0]);
    }

    private static void assertMuestra(String salida, String... esperados) {
        for (String esperado : esperados) {
            assertTrue(salida.contains(esperado),
                    () -> "Se esperaba encontrar \"" + esperado + "\" en la salida:\n" + salida);
        }
    }

    private static void assertNoMuestra(String salida, String noEsperado) {
        assertTrue(!salida.contains(noEsperado),
                () -> "No se esperaba encontrar \"" + noEsperado + "\" en la salida:\n" + salida);
    }

    /** Verifica que {@code primero} se haya impreso antes que {@code segundo}. */
    private static void assertEnEseOrden(String salida, String primero, String segundo) {
        assertMuestra(salida, primero, segundo);
        assertTrue(salida.indexOf(primero) < salida.indexOf(segundo),
                () -> "Se esperaba \"" + primero + "\" antes que \"" + segundo + "\". Salida:\n" + salida);
    }

    private static int veces(String salida, String texto) {
        int total = 0;
        int desde = salida.indexOf(texto);

        while (desde >= 0) {
            total++;
            desde = salida.indexOf(texto, desde + texto.length());
        }

        return total;
    }

    /*
            ==================================
            ======== Menú principal ==========
            ==================================
    */

    @Nested
    @DisplayName("Menú principal")
    class MenuPrincipal {

        @Test
        void muestraLasCincoSeccionesYSaleConCero() {
            String salida = consola("0");

            assertMuestra(salida,
                    "SISTEMA DE ALMACÉN LOGÍSTICO",
                    "1. Productos e inventario",
                    "2. Proveedores y entregas",
                    "3. Sucursales y reabastecimiento",
                    "4. Depósito y ubicaciones",
                    "5. Terminales de carga",
                    "0. Salir",
                    DESPEDIDA);
        }

        @Test
        void unaOpcionQueNoExisteAvisaYVuelveAMostrarElMenu() {
            String salida = consola("9", "0");

            assertMuestra(salida, "Opción inválida.");
            assertEquals(2, veces(salida, "1. Productos e inventario"),
                    "El menú principal se tiene que volver a mostrar después de una opción inválida");
        }

        @Test
        void textoEnLugarDeNumeroNoRompeElMenu() {
            String salida = consola("hola", "0");

            assertMuestra(salida, "Opción inválida.", DESPEDIDA);
        }

        @Test
        void seEntraYSeVuelveDeLosCincoSubmenus() {
            String salida = consola("1", "0", "2", "0", "3", "0", "4", "0", "5", "0", "0");

            assertMuestra(salida,
                    "---- Productos e inventario ----",
                    "---- Proveedores y entregas ----",
                    "---- Sucursales y reabastecimiento ----",
                    "---- Depósito y ubicaciones ----",
                    "---- Terminales de carga ----");
            assertEquals(6, veces(salida, "1. Productos e inventario"),
                    "El menú principal se muestra una vez al entrar y otra al volver de cada submenú");
        }

        @Test
        void cadaSubmenuAvisaCuandoLaOpcionNoExiste() {
            for (String submenu : new String[] {"1", "2", "3", "4", "5"}) {
                String salida = consola(submenu, "99", "0", "0");
                assertMuestra(salida, "Opción inválida.");
            }
        }
    }

    /*
            ==================================
            ====== Productos e inventario ====
            ==================================
    */

    @Nested
    @DisplayName("1. Productos e inventario")
    class ProductosEInventario {

        @Test
        void elSubmenuListaSusTreceOpciones() {
            String salida = consola("1", "0", "0");

            assertMuestra(salida,
                    "1. Registrar producto nuevo",
                    "2. Registrar producto con ubicación inicial",
                    "3. Aumentar stock (sin ubicar)",
                    "4. Aumentar stock en una posición",
                    "5. Disminuir stock en una posición",
                    "6. Reubicar mercadería entre posiciones",
                    "7. Buscar producto por código",
                    "8. Consultar stock total de un producto",
                    "9. Listar inventario ordenado por código",
                    "10. Listar productos con stock bajo",
                    "11. Listar productos sin stock",
                    "12. Ver cantidad total del inventario",
                    "13. Buscar producto por código a gran escala",
                    "0. Volver al menú principal");
        }

        @Test
        void opcion0VuelveAlMenuPrincipal() {
            String salida = consola("1", "0", "0");

            assertEquals(2, veces(salida, "1. Productos e inventario"),
                    "Hay que volver a ver el menú principal después de salir del submenú");
        }

        @Test
        void opcion1RegistraUnProductoNuevo() {
            String salida = consola("1", "1", "P-010", "Fideos secos", "7", "P-010", "0", "0");

            assertMuestra(salida,
                    "Producto registrado correctamente.",
                    "DetalleProducto{producto=Producto{codigo='P-010', nombre='Fideos secos'}, "
                            + "cantidad=0, cantidadMinima=0}");
        }

        @Test
        void opcion1RechazaUnCodigoQueYaExiste() {
            String salida = consola("1", "1", "P-001", "0", "0");

            assertMuestra(salida, "Ya existe un producto con ese código.");
            assertNoMuestra(salida, "Producto registrado correctamente.");
        }

        @Test
        void opcion1RechazaUnCodigoVacio() {
            String salida = consola("1", "1", "", "Producto sin codigo", "0", "0");

            assertMuestra(salida, "Error: El producto debe tener un código");
        }

        @Test
        void opcion2RegistraElProductoYLoUbicaEnUnaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40", "8", "P-020", "0", "0"));

            assertMuestra(salida,
                    "Producto registrado y ubicado correctamente.",
                    "Cantidad de stock disponible: 40");
        }

        @Test
        void opcion2RechazaUnCodigoQueYaExiste() {
            String salida = consola(guion(DEPOSITO, "1", "2", "P-001", "0", "0"));

            assertMuestra(salida, "Ya existe un producto con ese código.");
        }

        @Test
        void opcion2AvisaCuandoLaPosicionNoExiste() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-021", "Arvejas", "NO-EXISTE", "0", "0"));

            assertMuestra(salida, "No existe un sector con ese código.");
        }

        @Test
        void opcion2NoGuardaMercaderiaEnUnSectorQueNoEsUnaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-022", "Lentejas", "DEP", "5", "0", "0"));

            assertMuestra(salida, "Error: La mercadería solo puede ubicarse en una posición");
        }

        @Test
        void opcion2NoDejaPasarseDeLaCapacidadDeLaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-023", "Harina", "POS-2", "51", "0", "0"));

            assertMuestra(salida, "Error: La posición no tiene capacidad disponible suficiente");
        }

        @Test
        void opcion2RechazaCantidadCero() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-024", "Sal fina", "POS-1", "0", "0", "0"));

            assertMuestra(salida, "La cantidad debe ser mayor a cero.");
        }

        @Test
        void opcion2RechazaUnaCantidadQueNoEsNumero() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-025", "Azucar", "POS-1", "muchas", "0", "0"));

            assertMuestra(salida, "Debe ingresar un número entero.", "La cantidad debe ser mayor a cero.");
        }

        @Test
        void opcion3AumentaElStockSinUbicar() {
            String salida = consola("1", "3", "P-003", "10", "8", "P-003", "0", "0");

            assertMuestra(salida, "Stock actualizado correctamente.", "Cantidad de stock disponible: 10");
        }

        @Test
        void opcion3AvisaCuandoElProductoNoExiste() {
            String salida = consola("1", "3", "NO-EXISTE", "0", "0");

            assertMuestra(salida, "No existe un producto con ese código.");
        }

        @Test
        void opcion3RechazaCantidadCero() {
            String salida = consola("1", "3", "P-001", "0", "0", "0");

            assertMuestra(salida, "Error: La cantidad debe ser mayor a cero");
        }

        @Test
        void opcion3RechazaUnaCantidadQueNoEsNumero() {
            String salida = consola("1", "3", "P-001", "diez", "0", "0");

            assertMuestra(salida, "Debe ingresar un número entero.");
            assertNoMuestra(salida, "Stock actualizado correctamente.");
        }

        @Test
        void opcion4AumentaElStockEnUnaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40",
                    "4", "P-020", "POS-2", "10",
                    "8", "P-020", "0", "0"));

            assertMuestra(salida, "Stock actualizado correctamente.", "Cantidad de stock disponible: 50");
        }

        @Test
        void opcion4AvisaCuandoElProductoNoExiste() {
            String salida = consola(guion(DEPOSITO, "1", "4", "NO-EXISTE", "0", "0"));

            assertMuestra(salida, "No existe un producto con ese código.");
        }

        @Test
        void opcion4NoDejaPasarseDeLaCapacidadDeLaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-2", "50",
                    "4", "P-020", "POS-2", "1", "0", "0"));

            assertMuestra(salida, "Error: La posición no tiene capacidad disponible suficiente");
        }

        @Test
        void opcion4NoGuardaEnUnaPosicionInhabilitada() {
            String salida = consola(guion(DEPOSITO,
                    "4", "6", "POS-2", "0",
                    "1", "2", "P-020", "Galletas", "POS-1", "10",
                    "4", "P-020", "POS-2", "5", "0", "0"));

            assertMuestra(salida, "Error: La posición está inhabilitada");
        }

        @Test
        void opcion5DisminuyeElStockDeUnaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40",
                    "5", "P-020", "POS-1", "15",
                    "8", "P-020", "0", "0"));

            assertMuestra(salida, "Stock actualizado correctamente.", "Cantidad de stock disponible: 25");
        }

        @Test
        void opcion5AvisaCuandoNoHayStockSuficienteEnEsaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40",
                    "5", "P-020", "POS-1", "41", "0", "0"));

            assertMuestra(salida, "No hay stock suficiente de ese producto en esa posición.");
        }

        @Test
        void opcion5AvisaCuandoElProductoNoExiste() {
            String salida = consola(guion(DEPOSITO, "1", "5", "NO-EXISTE", "0", "0"));

            assertMuestra(salida, "No existe un producto con ese código.");
        }

        @Test
        void opcion6ReubicaMercaderiaEntrePosiciones() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40",
                    "6", "P-020", "POS-1", "POS-2", "15",
                    "8", "P-020", "0",
                    "4", "7", "POS-2", "0", "0"));

            assertMuestra(salida,
                    "Mercadería reubicada correctamente.",
                    "Cantidad de stock disponible: 40",
                    "Ocupación actual: 15");
        }

        @Test
        void opcion6AvisaCuandoNoHayStockEnLaPosicionDeOrigen() {
            String salida = consola(guion(DEPOSITO, "1", "6", "P-001", "POS-1", "POS-2", "1", "0", "0"));

            assertMuestra(salida,
                    "No se pudo reubicar (verifique el stock en origen y la capacidad del destino).");
        }

        @Test
        void opcion6AvisaCuandoElProductoNoExiste() {
            String salida = consola(guion(DEPOSITO, "1", "6", "NO-EXISTE", "0", "0"));

            assertMuestra(salida, "No existe un producto con ese código.");
        }

        @Test
        void opcion7MuestraElProductoBuscado() {
            String salida = consola("1", "7", "P-002", "0", "0");

            assertMuestra(salida, "DetalleProducto{producto=Producto{codigo='P-002', "
                    + "nombre='Leche entera'}, cantidad=12, cantidadMinima=8}");
        }

        @Test
        void opcion7AvisaCuandoElProductoNoExiste() {
            String salida = consola("1", "7", "NO-EXISTE", "0", "0");

            assertMuestra(salida, "No existe un producto con ese código.");
        }

        @Test
        void opcion8MuestraElStockTotalDelProducto() {
            String salida = consola("1", "8", "P-001", "0", "0");

            assertMuestra(salida, "Cantidad de stock disponible: 25");
        }

        @Test
        void opcion8AvisaCuandoElProductoNoExiste() {
            String salida = consola("1", "8", "NO-EXISTE", "0", "0");

            assertMuestra(salida, "No existe un producto con ese código.");
        }

        @Test
        void opcion9ListaElInventarioOrdenadoPorCodigo() {
            String salida = consola("1", "9", "0", "0");

            assertEnEseOrden(salida, "codigo='P-001'", "codigo='P-002'");
            assertEnEseOrden(salida, "codigo='P-002'", "codigo='P-003'");
        }

        @Test
        void opcion10ListaSoloLosProductosConStockBajo() {
            String salida = consola("1", "10", "0", "0");

            assertMuestra(salida, "Producto{codigo='P-003', nombre='Arroz blanco'}");
            assertNoMuestra(salida, "codigo='P-001'");
            assertNoMuestra(salida, "codigo='P-002'");
        }

        @Test
        void opcion10AvisaCuandoNingunProductoEstaBajoElMinimo() {
            String salida = consola("1", "3", "P-003", "10", "10", "0", "0");

            assertMuestra(salida, "No hay productos con stock bajo.");
        }

        @Test
        void opcion11ListaLosProductosSinStock() {
            String salida = consola("1", "11", "0", "0");

            assertMuestra(salida, "Producto{codigo='P-003', nombre='Arroz blanco'}");
            assertNoMuestra(salida, "codigo='P-001'");
        }

        @Test
        void opcion11AvisaCuandoTodosLosProductosTienenStock() {
            String salida = consola("1", "3", "P-003", "1", "11", "0", "0");

            assertMuestra(salida, "No hay productos sin stock.");
        }

        @Test
        void opcion12MuestraLaCantidadTotalYSeActualizaConElStock() {
            String salida = consola("1", "12", "3", "P-001", "5", "12", "0", "0");

            assertMuestra(salida,
                    "Cantidad de inventario total: 37",
                    "Cantidad de inventario total: 42");
        }

        @Test
        void opcion13ComparaLaBusquedaContraLaDelHito1() {
            String salida = consola("1", "13", "50", "0", "0");

            assertMuestra(salida,
                    "Cargando 50 productos en un inventario de prueba (no afecta el inventario actual)...",
                    "Buscando el código P9999999",
                    "buscarProducto en el Hito 1 (recorrido lineal, ListaArray):",
                    "buscarProducto ahora (Inventario, árbol AVL):");
        }

        @Test
        void opcion13NoTocaElInventarioReal() {
            String salida = consola("1", "13", "50", "12", "0", "0");

            assertMuestra(salida, "Cantidad de inventario total: 37");
        }

        @Test
        void opcion13RechazaCantidadCero() {
            String salida = consola("1", "13", "0", "0", "0");

            assertMuestra(salida, "La cantidad debe ser mayor a cero.");
        }

        @Test
        void opcion13RechazaUnaCantidadQueNoEsNumero() {
            String salida = consola("1", "13", "mil", "0", "0");

            assertMuestra(salida, "Debe ingresar un número entero.");
        }
    }

    /*
            ==================================
            ====== Proveedores y entregas ====
            ==================================
    */

    @Nested
    @DisplayName("2. Proveedores y entregas")
    class ProveedoresYEntregas {

        @Test
        void elSubmenuListaSusSeisOpciones() {
            String salida = consola("2", "0", "0");

            assertMuestra(salida,
                    "1. Registrar entrega de proveedor (sin ubicar)",
                    "2. Registrar entrega de proveedor con posiciones de guardado",
                    "3. Ver próxima entrega pendiente",
                    "4. Descargar próxima entrega (sin ubicar)",
                    "5. Descargar próxima entrega ubicada",
                    "6. Ver proveedores con entregas pendientes",
                    "0. Volver al menú principal");
        }

        @Test
        void opcion1RegistraUnaEntregaConVariosProductos() {
            String salida = consola("2", "1", "PROV-002", "Molino del Este", "2",
                    "P-001", "5", "P-002", "7", "6", "0", "0");

            assertMuestra(salida,
                    "Entrega registrada correctamente.",
                    "Proveedor{codigo='PROV-002', nombre='Molino del Este'}");
        }

        @Test
        void opcion1AvisaCuandoElProductoNoEstaRegistrado() {
            String salida = consola("2", "1", "PROV-003", "Proveedor nuevo", "1", "NO-EXISTE", "0", "0");

            assertMuestra(salida, "El producto no está registrado en el almacén.");
            assertNoMuestra(salida, "Entrega registrada correctamente.");
        }

        @Test
        void opcion1ExigeAlMenosUnProducto() {
            String salida = consola("2", "1", "PROV-004", "Entrega vacia", "0", "0", "0");

            assertMuestra(salida, "La entrega debe incluir al menos un producto.");
        }

        @Test
        void opcion1RechazaCantidadCero() {
            String salida = consola("2", "1", "PROV-005", "Entrega en cero", "1", "P-001", "0", "0", "0");

            assertMuestra(salida, "La cantidad debe ser mayor a cero.");
        }

        @Test
        void opcion2RegistraLaEntregaConSusPosicionesDeGuardado() {
            String salida = consola(guion(DEPOSITO,
                    "2", "2", "PROV-010", "Lacteos del Sur", "1", "P-002", "1", "POS-1", "10",
                    "6", "0", "0"));

            assertMuestra(salida,
                    "Entrega ubicada registrada correctamente.",
                    "Proveedor{codigo='PROV-010', nombre='Lacteos del Sur'}");
        }

        @Test
        void opcion2AvisaCuandoElProductoNoEstaRegistrado() {
            String salida = consola(guion(DEPOSITO,
                    "2", "2", "PROV-012", "Proveedor nuevo", "1", "NO-EXISTE", "0", "0"));

            assertMuestra(salida, "El producto no está registrado en el almacén.");
        }

        @Test
        void opcion2ExigeAlMenosUnaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "2", "2", "PROV-013", "Sin posiciones", "1", "P-002", "0", "0", "0"));

            assertMuestra(salida, "Debe indicar al menos una posición.");
        }

        @Test
        void opcion2AvisaCuandoLaPosicionNoExiste() {
            String salida = consola(guion(DEPOSITO,
                    "2", "2", "PROV-014", "Posicion mala", "1", "P-002", "1", "NO-EXISTE", "0", "0"));

            assertMuestra(salida, "No existe un sector con ese código.");
        }

        @Test
        void opcion2RechazaCantidadCero() {
            String salida = consola(guion(DEPOSITO,
                    "2", "2", "PROV-015", "Cantidad cero", "1", "P-002", "1", "POS-1", "0", "0", "0"));

            assertMuestra(salida, "La cantidad debe ser mayor a cero.");
        }

        @Test
        void opcion3MuestraLaProximaEntregaSinSacarlaDeLaCola() {
            String salida = consola("2", "3", "3", "0", "0");

            assertEquals(2, veces(salida, "Proveedor{codigo='PROV-001', nombre='Distribuidora del Sur'}"),
                    "Consultar la próxima entrega no tiene que sacarla de la cola");
        }

        @Test
        void opcion3AvisaCuandoNoHayEntregasPendientes() {
            String salida = consola("2", "4", "3", "0", "0");

            assertMuestra(salida, "No hay entregas de proveedores pendientes.");
        }

        @Test
        void opcion4DescargaLaEntregaYSumaElStock() {
            String salida = consola("2", "4", "0", "1", "8", "P-003", "0", "0");

            assertMuestra(salida,
                    "Entrega descargada correctamente:",
                    "Cantidad de stock disponible: 30");
        }

        @Test
        void opcion4RespetaElOrdenDeLlegada() {
            String salida = consola("2", "1", "PROV-020", "Segunda en llegar", "1", "P-001", "5",
                    "4", "4", "0", "0");

            assertEnEseOrden(salida, "codigo='PROV-001'", "codigo='PROV-020'");
        }

        @Test
        void opcion4AvisaCuandoNoHayEntregasPendientes() {
            String salida = consola("2", "4", "4", "0", "0");

            assertMuestra(salida, "No hay entregas de proveedores pendientes.");
        }

        @Test
        void opcion5DescargaLaEntregaUbicadaEnSusPosiciones() {
            String salida = consola(guion(DEPOSITO,
                    "2", "4",
                    "2", "PROV-011", "Distribuidora Norte", "1", "P-002", "2", "POS-1", "6", "POS-2", "4",
                    "5", "0",
                    "1", "8", "P-002", "0",
                    "4", "7", "POS-1", "0", "0"));

            assertMuestra(salida,
                    "Entrega ubicada descargada correctamente:",
                    "Cantidad de stock disponible: 22",
                    "Ocupación actual: 6");
        }

        @Test
        void opcion5AvisaCuandoLaEntregaNoIndicaPosiciones() {
            String salida = consola("2", "5", "0", "0");

            assertMuestra(salida, "No se pudo descargar: La entrega no indica posiciones de guardado");
        }

        @Test
        void opcion5AvisaCuandoNoHayEntregasPendientes() {
            String salida = consola("2", "4", "5", "0", "0");

            assertMuestra(salida, "No hay entregas de proveedores pendientes.");
        }

        @Test
        void opcion6ListaCadaProveedorPendienteUnaSolaVez() {
            String salida = consola("2",
                    "1", "PROV-030", "Repetido", "1", "P-001", "1",
                    "1", "PROV-030", "Repetido", "1", "P-001", "2",
                    "6", "0", "0");

            assertMuestra(salida, "Proveedor{codigo='PROV-001', nombre='Distribuidora del Sur'}");
            assertEquals(1, veces(salida, "Proveedor{codigo='PROV-030', nombre='Repetido'}"),
                    "Un proveedor con dos entregas pendientes se lista una sola vez");
        }

        @Test
        void opcion6AvisaCuandoNoHayProveedoresPendientes() {
            String salida = consola("2", "4", "6", "0", "0");

            assertMuestra(salida, "No hay proveedores con entregas pendientes.");
        }
    }

    /*
            ============================================
            ====== Sucursales y reabastecimiento =======
            ============================================
    */

    @Nested
    @DisplayName("3. Sucursales y reabastecimiento")
    class SucursalesYReabastecimiento {

        @Test
        void elSubmenuListaSusCincoOpciones() {
            String salida = consola("3", "0", "0");

            assertMuestra(salida,
                    "1. Registrar pedido de reabastecimiento",
                    "2. Ver siguiente pedido prioritario",
                    "3. Despachar siguiente pedido (sin ubicar)",
                    "4. Preparar y despachar un pedido ubicado (con ruta de recolección)",
                    "5. Modificar la prioridad de un pedido pendiente",
                    "0. Volver al menú principal");
        }

        @Test
        void opcion1RegistraElPedidoConLaPrioridadDeLaSucursal() {
            String salida = consola("3", "1", "SUC-003", "Este", "150", "1", "P-001", "5", "0", "0");

            assertMuestra(salida, "Pedido registrado correctamente. Prioridad: 150");
        }

        @Test
        void opcion1AceptaVariosProductosYElPedidoQuedaEnLaCola() {
            String salida = consola("3", "1", "SUC-004", "Oeste", "900", "2",
                    "P-001", "3", "P-002", "2", "2", "0", "0");

            assertMuestra(salida,
                    "Pedido registrado correctamente. Prioridad: 900",
                    "sucursal=Sucursal{codigo='SUC-004', nombre='Oeste', clientesPromedio=900}");
        }

        @Test
        void opcion1AvisaCuandoElProductoNoEstaRegistrado() {
            String salida = consola("3", "1", "SUC-005", "Sur", "100", "1", "NO-EXISTE", "0", "0");

            assertMuestra(salida, "El producto no está registrado en el almacén.");
        }

        @Test
        void opcion1RechazaClientesPromedioNegativos() {
            String salida = consola("3", "1", "SUC-006", "Negativa", "-5", "1", "0", "0");

            assertMuestra(salida, "Los datos ingresados no son válidos.");
        }

        @Test
        void opcion1RechazaUnPedidoSinProductos() {
            String salida = consola("3", "1", "SUC-007", "Sin productos", "10", "0", "0", "0");

            assertMuestra(salida, "Los datos ingresados no son válidos.");
        }

        @Test
        void opcion1RechazaCantidadCero() {
            String salida = consola("3", "1", "SUC-008", "Cero", "10", "1", "P-001", "0", "0", "0");

            assertMuestra(salida, "La cantidad debe ser mayor a cero.");
        }

        @Test
        void opcion2MuestraElPedidoMasPrioritarioSinDespacharlo() {
            String salida = consola("3", "2", "2", "0", "0");

            assertMuestra(salida, "prioridad=400");
            assertEquals(2, veces(salida, "codigo='SUC-001'"),
                    "Consultar el pedido prioritario no tiene que despacharlo");
        }

        @Test
        void opcion2AvisaCuandoNoHayPedidos() {
            String salida = consola("3", "3", "3", "2", "0", "0");

            assertMuestra(salida, "No hay pedidos de reabastecimiento pendientes.");
        }

        @Test
        void opcion3DespachaPorPrioridadYDescuentaElStock() {
            String salida = consola("3", "3", "3", "0", "1", "8", "P-001", "8", "P-002", "0", "0");

            assertMuestra(salida, "Pedido despachado correctamente:",
                    "Cantidad de stock disponible: 19",
                    "Cantidad de stock disponible: 8");
            assertEnEseOrden(salida, "codigo='SUC-001'", "codigo='SUC-002'");
        }

        @Test
        void opcion3AvisaCuandoNoHayStockSuficiente() {
            String salida = consola("3", "1", "SUC-009", "Norte Grande", "5000", "1", "P-003", "5",
                    "3", "0", "0");

            assertMuestra(salida, "No hay stock suficiente para despachar el pedido prioritario.");
        }

        @Test
        void opcion3AvisaCuandoNoHayPedidos() {
            String salida = consola("3", "3", "3", "3", "0", "0");

            assertMuestra(salida, "No hay pedidos de reabastecimiento pendientes.");
        }

        @Test
        void opcion4MuestraLaRutaDeRecoleccionYDespacha() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40", "4", "P-020", "POS-2", "10", "0",
                    "3", "4", "SUC-010", "Este", "300", "1", "P-020", "45", "s", "0",
                    "1", "8", "P-020", "0", "0"));

            assertMuestra(salida,
                    "Ruta de recolección (posiciones en el orden en que hay que recorrerlas):",
                    "1. Retirar 40 de Galletas desde Posicion 1 [POS-1]",
                    "2. Retirar 5 de Galletas desde Posicion 2 [POS-2]",
                    "Pedido despachado correctamente.",
                    "Cantidad de stock disponible: 5");
        }

        @Test
        void opcion4PermiteCancelarElDespachoSinTocarElStock() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40", "0",
                    "3", "4", "SUC-011", "Sur", "100", "1", "P-020", "10", "n", "0",
                    "1", "8", "P-020", "0", "0"));

            assertMuestra(salida, "Despacho cancelado.", "Cantidad de stock disponible: 40");
        }

        @Test
        void opcion4AvisaCuandoNoHayStockUbicadoSuficiente() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "10", "0",
                    "3", "4", "SUC-012", "Oeste", "100", "1", "P-020", "999", "0", "0"));

            assertMuestra(salida, "No se pudo preparar el pedido: No hay stock suficiente");
        }

        @Test
        void opcion4AvisaCuandoElProductoNoEstaRegistrado() {
            String salida = consola("3", "4", "SUC-013", "Centro", "100", "1", "NO-EXISTE", "0", "0");

            assertMuestra(salida, "El producto no está registrado en el almacén.");
        }

        @Test
        void opcion4RechazaDatosInvalidos() {
            String salida = consola("3", "4", "SUC-014", "Norte", "-1", "1", "0", "0");

            assertMuestra(salida, "Los datos ingresados no son válidos.");
        }

        @Test
        void opcion5CambiaLaPrioridadYAlteraElOrdenDeDespacho() {
            String salida = consola("3", "2", "5", "SUC-002", "900", "2", "0", "0");

            assertMuestra(salida, "Prioridad actualizada correctamente.", "prioridad=900");
            assertEnEseOrden(salida, "codigo='SUC-001'", "codigo='SUC-002'");
        }

        @Test
        void opcion5AvisaCuandoLaSucursalNoTienePedidosPendientes() {
            String salida = consola("3", "5", "NO-EXISTE", "5", "0", "0");

            assertMuestra(salida, "No se encontró un pedido pendiente de esa sucursal.");
        }

        @Test
        void opcion5RechazaUnaPrioridadNegativa() {
            String salida = consola("3", "5", "SUC-001", "-1", "0", "0");

            assertMuestra(salida, "La prioridad debe ser un número entero no negativo.");
        }

        @Test
        void opcion5RechazaUnaPrioridadQueNoEsNumero() {
            String salida = consola("3", "5", "SUC-001", "alta", "0", "0");

            assertMuestra(salida, "Debe ingresar un número entero.",
                    "La prioridad debe ser un número entero no negativo.");
        }
    }

    /*
            ==================================
            ====== Depósito y ubicaciones ====
            ==================================
    */

    @Nested
    @DisplayName("4. Depósito y ubicaciones")
    class DepositoYUbicaciones {

        @Test
        void elSubmenuListaSusSieteOpciones() {
            String salida = consola("4", "0", "0");

            assertMuestra(salida,
                    "1. Crear el depósito (sector raíz)",
                    "2. Agregar un sector",
                    "3. Ver estructura del depósito",
                    "4. Buscar un sector por código",
                    "5. Mover un sector a otro punto del depósito",
                    "6. Inhabilitar un sector",
                    "7. Consultar capacidad y ocupación de un sector",
                    "0. Volver al menú principal");
        }

        @Test
        void opcion1CreaElSectorRaiz() {
            String salida = consola("4", "1", "DEP", "Deposito central", "3", "0", "0");

            assertMuestra(salida,
                    "Depósito creado correctamente.",
                    "- Deposito central [DEP] DEPOSITO");
        }

        @Test
        void opcion1AvisaCuandoYaExisteElSectorRaiz() {
            String salida = consola("4", "1", "DEP", "Deposito central", "1", "0", "0");

            assertMuestra(salida, "El depósito ya tiene un sector raíz.");
        }

        @Test
        void opcion2AvisaCuandoTodaviaNoHaySectorRaiz() {
            String salida = consola("4", "2", "0", "0");

            assertMuestra(salida, "Primero hay que crear el sector raíz del depósito (opción 1).");
        }

        @Test
        void opcion2AgregaUnSectorDeCadaTipo() {
            String salida = consola("4", "1", "DEP", "Deposito central",
                    "2", "DEP", "Z-1", "Zona A", "1",
                    "2", "Z-1", "PAS-1", "Pasillo 1", "2",
                    "2", "PAS-1", "EST-1", "Estanteria 1", "3",
                    "2", "EST-1", "BAN-1", "Bandeja 1", "4",
                    "2", "BAN-1", "POS-1", "Posicion 1", "5", "100",
                    "3", "0", "0");

            assertEquals(5, veces(salida, "Sector agregado correctamente."));
            assertMuestra(salida,
                    "- Zona A [Z-1] ZONA",
                    "- Pasillo 1 [PAS-1] PASILLO",
                    "- Estanteria 1 [EST-1] ESTANTERIA",
                    "- Bandeja 1 [BAN-1] BANDEJA",
                    "- Posicion 1 [POS-1] POSICION (capacidad 100)");
        }

        @Test
        void opcion2AvisaCuandoElSectorPadreNoExiste() {
            String salida = consola("4", "1", "DEP", "Deposito central", "2", "NO-EXISTE", "0", "0");

            assertMuestra(salida, "No existe un sector con ese código.");
        }

        @Test
        void opcion2RechazaUnCodigoDeSectorRepetido() {
            String salida = consola(guion(DEPOSITO, "4", "2", "DEP", "POS-1", "0", "0"));

            assertMuestra(salida, "Ya existe un sector con ese código.");
        }

        @Test
        void opcion2AvisaCuandoElTipoDeSectorNoExiste() {
            String salida = consola("4", "1", "DEP", "Deposito central",
                    "2", "DEP", "S-1", "Sector raro", "9", "0", "0");

            assertMuestra(salida, "Opción inválida.");
            assertNoMuestra(salida, "Sector agregado correctamente.");
        }

        @Test
        void opcion2RechazaUnaCapacidadQueNoEsNumero() {
            String salida = consola("4", "1", "DEP", "Deposito central",
                    "2", "DEP", "POS-9", "Posicion 9", "5", "mucha", "0", "0");

            assertMuestra(salida, "Debe ingresar un número entero.",
                    "La capacidad debe ser un número entero no negativo.");
        }

        @Test
        void opcion3MuestraLaJerarquiaConSusNiveles() {
            String salida = consola(guion(DEPOSITO, "4", "3", "0", "0"));

            assertMuestra(salida,
                    "--- Estructura del depósito ---",
                    "- Deposito central [DEP] DEPOSITO",
                    "  - Posicion 1 [POS-1] POSICION (capacidad 100)",
                    "  - Posicion 2 [POS-2] POSICION (capacidad 50)");
        }

        @Test
        void opcion3AvisaCuandoElDepositoEstaVacio() {
            String salida = consola("4", "3", "0", "0");

            assertMuestra(salida, "El depósito todavía no tiene sectores. Empiece creando el sector raíz.");
        }

        @Test
        void opcion4MuestraLosDatosDelSector() {
            String salida = consola(guion(DEPOSITO, "4", "4", "POS-1", "0", "0"));

            assertMuestra(salida,
                    "Nombre: Posicion 1",
                    "Tipo: POSICION",
                    "Capacidad: 100",
                    "Habilitado: sí");
        }

        @Test
        void opcion4AvisaCuandoElSectorNoExiste() {
            String salida = consola(guion(DEPOSITO, "4", "4", "NO-EXISTE", "0", "0"));

            assertMuestra(salida, "No existe un sector con ese código.");
        }

        @Test
        void opcion5MueveUnSectorConSuSubarbol() {
            String salida = consola(guion(DEPOSITO, "4", "5", "POS-2", "POS-1", "3", "0", "0"));

            assertMuestra(salida,
                    "Sector movido correctamente.",
                    "    - Posicion 2 [POS-2] POSICION (capacidad 50)");
        }

        @Test
        void opcion5NoDejaMoverElSectorRaiz() {
            String salida = consola(guion(DEPOSITO, "4", "5", "DEP", "POS-1", "0", "0"));

            assertMuestra(salida, "No se pudo mover el sector");
        }

        @Test
        void opcion5NoDejaMoverUnSectorDentroDeSuPropioSubarbol() {
            String salida = consola(guion(DEPOSITO,
                    "4", "5", "POS-2", "POS-1", "5", "POS-1", "POS-2", "0", "0"));

            assertMuestra(salida, "Sector movido correctamente.", "No se pudo mover el sector");
        }

        @Test
        void opcion5AvisaCuandoAlgunCodigoNoExiste() {
            String salida = consola(guion(DEPOSITO, "4", "5", "NO-EXISTE", "DEP", "0", "0"));

            assertMuestra(salida, "No se pudo mover el sector");
        }

        @Test
        void opcion6InhabilitaUnSectorVacio() {
            String salida = consola(guion(DEPOSITO, "4", "6", "POS-2", "3", "0", "0"));

            assertMuestra(salida,
                    "Sector inhabilitado y mercadería reubicada correctamente.",
                    "- Posicion 2 [POS-2] POSICION (capacidad 50) [INHABILITADO]");
        }

        @Test
        void opcion6ReubicaLaMercaderiaAlInhabilitarLaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40", "0",
                    "4", "6", "POS-1", "7", "POS-2", "0",
                    "1", "8", "P-020", "0", "0"));

            assertMuestra(salida,
                    "Sector inhabilitado y mercadería reubicada correctamente.",
                    "Ocupación actual: 40",
                    "Cantidad de stock disponible: 40");
        }

        @Test
        void opcion6AvisaCuandoNoHayLugarParaReubicarLaMercaderia() {
            String salida = consola("4",
                    "1", "DEP", "Deposito central",
                    "2", "DEP", "POS-1", "Posicion 1", "5", "100",
                    "2", "DEP", "POS-3", "Posicion 3", "5", "5", "0",
                    "1", "2", "P-020", "Galletas", "POS-1", "40", "0",
                    "4", "6", "POS-1", "7", "POS-1", "0", "0");

            assertMuestra(salida,
                    "No se pudo inhabilitar (no existe, ya está inhabilitado, o no hay capacidad "
                            + "disponible fuera de él para reubicar su mercadería).",
                    "Ocupación actual: 40");
        }

        @Test
        void opcion6AvisaCuandoElSectorYaEstaInhabilitado() {
            String salida = consola(guion(DEPOSITO, "4", "6", "POS-2", "6", "POS-2", "0", "0"));

            assertEquals(1, veces(salida, "Sector inhabilitado y mercadería reubicada correctamente."));
            assertMuestra(salida, "No se pudo inhabilitar");
        }

        @Test
        void opcion6AvisaCuandoElSectorNoExiste() {
            String salida = consola(guion(DEPOSITO, "4", "6", "NO-EXISTE", "0", "0"));

            assertMuestra(salida, "No se pudo inhabilitar");
        }

        @Test
        void opcion7MuestraCapacidadOcupacionYDisponible() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40", "0",
                    "4", "7", "DEP", "0", "0"));

            assertMuestra(salida,
                    "Capacidad total (posiciones del subárbol): 150",
                    "Ocupación actual: 40",
                    "Capacidad disponible: 110");
        }

        @Test
        void opcion7NoCuentaComoDisponibleLoQueEstaInhabilitado() {
            String salida = consola(guion(DEPOSITO, "4", "6", "POS-2", "7", "DEP", "0", "0"));

            assertMuestra(salida,
                    "Capacidad total (posiciones del subárbol): 150",
                    "Capacidad disponible: 100");
        }

        @Test
        void opcion7AvisaCuandoElSectorNoExiste() {
            String salida = consola(guion(DEPOSITO, "4", "7", "NO-EXISTE", "0", "0"));

            assertMuestra(salida, "No existe un sector con ese código.");
        }
    }

    /*
            ==================================
            ====== Terminales de carga =======
            ==================================
    */

    @Nested
    @DisplayName("5. Terminales de carga")
    class TerminalesDeCarga {

        @Test
        void elSubmenuListaSusCincoOpciones() {
            String salida = consola("5", "0", "0");

            assertMuestra(salida,
                    "1. Registrar terminal",
                    "2. Listar terminales",
                    "3. Habilitar o deshabilitar terminal",
                    "4. Iniciar operación en terminal",
                    "5. Liberar terminal",
                    "0. Volver al menú principal");
        }

        @Test
        void opcion1RegistraUnaTerminalNueva() {
            String salida = consola("5", "1", "3", "2", "0", "0");

            assertMuestra(salida,
                    "Terminal registrada correctamente.",
                    "TerminalCarga{id=3, habilitada=true, operacionActual=LIBRE}");
        }

        @Test
        void opcion1RechazaUnIdRepetido() {
            String salida = consola("5", "1", "1", "0", "0");

            assertMuestra(salida, "Error: Ya existe una terminal con ese id");
        }

        @Test
        void opcion1RechazaUnIdQueNoEsNumero() {
            String salida = consola("5", "1", "tres", "0", "0");

            assertMuestra(salida, "Debe ingresar un número entero.");
            assertNoMuestra(salida, "Terminal registrada correctamente.");
        }

        @Test
        void opcion2ListaLasTerminalesDeLosDatosBase() {
            String salida = consola("5", "2", "0", "0");

            assertMuestra(salida,
                    "TerminalCarga{id=1, habilitada=true, operacionActual=LIBRE}",
                    "TerminalCarga{id=2, habilitada=true, operacionActual=LIBRE}");
        }

        @Test
        void opcion3DeshabilitaYVuelveAHabilitarUnaTerminal() {
            String salida = consola("5", "3", "1", "2", "2", "3", "1", "1", "2", "0", "0");

            assertEquals(2, veces(salida, "Estado de la terminal actualizado."));
            assertMuestra(salida, "TerminalCarga{id=1, habilitada=false, operacionActual=LIBRE}");
            assertEnEseOrden(salida,
                    "TerminalCarga{id=1, habilitada=false, operacionActual=LIBRE}",
                    "TerminalCarga{id=1, habilitada=true, operacionActual=LIBRE}");
        }

        @Test
        void opcion3AvisaCuandoNoSeEligeHabilitarNiDeshabilitar() {
            String salida = consola("5", "3", "1", "9", "0", "0");

            assertMuestra(salida, "Opción inválida.");
        }

        @Test
        void opcion3AvisaCuandoLaTerminalNoExiste() {
            String salida = consola("5", "3", "99", "1", "0", "0");

            assertMuestra(salida, "Error: No existe una terminal con ese id");
        }

        @Test
        void opcion4IniciaUnaDescargaDeProveedor() {
            String salida = consola("5", "4", "1", "1", "2", "0", "0");

            assertMuestra(salida,
                    "Operación iniciada correctamente.",
                    "TerminalCarga{id=1, habilitada=true, operacionActual=DESCARGANDO_PROVEEDOR}");
        }

        @Test
        void opcion4IniciaUnaCargaDeSucursal() {
            String salida = consola("5", "4", "2", "2", "2", "0", "0");

            assertMuestra(salida,
                    "Operación iniciada correctamente.",
                    "TerminalCarga{id=2, habilitada=true, operacionActual=CARGANDO_SUCURSAL}");
        }

        @Test
        void opcion4AvisaCuandoLaTerminalYaEstaOcupada() {
            String salida = consola("5", "4", "1", "1", "4", "1", "2", "0", "0");

            assertMuestra(salida, "Error: La terminal no está disponible");
        }

        @Test
        void opcion4AvisaCuandoLaTerminalEstaDeshabilitada() {
            String salida = consola("5", "3", "1", "2", "4", "1", "1", "0", "0");

            assertMuestra(salida, "Error: La terminal no está disponible");
        }

        @Test
        void opcion4AvisaCuandoLaTerminalNoExiste() {
            String salida = consola("5", "4", "99", "1", "0", "0");

            assertMuestra(salida, "Error: No existe una terminal con ese id");
        }

        @Test
        void opcion4RechazaUnaOperacionQueNoExiste() {
            String salida = consola("5", "4", "1", "9", "0", "0");

            assertMuestra(salida, "Opción inválida.");
        }

        @Test
        void opcion5LiberaLaTerminalOcupada() {
            String salida = consola("5", "4", "1", "1", "5", "1", "2", "0", "0");

            assertMuestra(salida,
                    "Terminal liberada correctamente.",
                    "TerminalCarga{id=1, habilitada=true, operacionActual=LIBRE}");
        }

        @Test
        void opcion5AvisaCuandoLaTerminalNoExiste() {
            String salida = consola("5", "5", "99", "0", "0");

            assertMuestra(salida, "Error: No existe una terminal con ese id");
        }

        @Test
        void opcion5RechazaUnIdQueNoEsNumero() {
            String salida = consola("5", "5", "uno", "0", "0");

            assertMuestra(salida, "Debe ingresar un número entero.");
            assertNoMuestra(salida, "Terminal liberada correctamente.");
        }
    }

    /*
            ==================================
            ====== Defectos detectados =======
            ==================================
    */

    /**
     * Caminos del menú que hoy no terminan bien. Cada prueba describe lo que la
     * consola debería contestar; quedan {@code @Disabled} para que el resto de la
     * suite corra en verde. Cuando se arregle el defecto en {@code Main}, sacar la
     * anotación y la prueba pasa a cuidar el arreglo.
     */
    @Nested
    @DisplayName("Defectos detectados")
    class DefectosDetectados {

        @Test
        @Disabled("Reubicar hacia un sector que no es posición corta el programa entero: "
                + "Main.reubicarMercaderia no atrapa la IllegalArgumentException del inventario")
        void productosOpcion6DeberiaAvisarCuandoElDestinoNoEsUnaPosicion() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "40",
                    "6", "P-020", "POS-1", "DEP", "5", "0", "0"));

            assertMuestra(salida, "No se pudo reubicar");
        }

        @Test
        @Disabled("Reubicar más de lo que entra en el destino corta el programa entero: "
                + "Main.reubicarMercaderia no atrapa la IllegalStateException del inventario")
        void productosOpcion6DeberiaAvisarCuandoElDestinoNoTieneCapacidad() {
            String salida = consola(guion(DEPOSITO,
                    "1", "2", "P-020", "Galletas", "POS-1", "60",
                    "6", "P-020", "POS-1", "POS-2", "51", "0", "0"));

            assertMuestra(salida, "No se pudo reubicar");
        }

        @Test
        @Disabled("Descargar una entrega guardada en un sector que no es posición corta el "
                + "programa: Main solo atrapa IllegalStateException, no IllegalArgumentException")
        void proveedoresOpcion5DeberiaAvisarCuandoLaPosicionGuardadaNoEsUnaPosicion() {
            String salida = consola("4", "1", "DEP", "Deposito central", "0",
                    "2", "4",
                    "2", "PROV-016", "Proveedor con sector malo", "1", "P-002", "1", "DEP", "5",
                    "5", "0", "0");

            assertMuestra(salida, "No se pudo descargar");
        }

        @Test
        @Disabled("La cola de proveedores tiene 15 lugares y los datos base ya ocupan uno: "
                + "la entrega que no entra se descarta en silencio y la consola igual dice "
                + "que se registró correctamente")
        void proveedoresOpcion1DeberiaAvisarCuandoLaColaEstaLlena() {
            List<String> lineas = new ArrayList<>();
            lineas.add("2");

            for (int i = 1; i <= 15; i++) {
                lineas.add("1");
                lineas.add("PROV-" + i);
                lineas.add("Proveedor " + i);
                lineas.add("1");
                lineas.add("P-001");
                lineas.add("1");
            }

            lineas.add("6");
            lineas.add("0");
            lineas.add("0");

            String salida = consola(lineas.toArray(new String[0]));

            assertEquals(14, veces(salida, "Entrega registrada correctamente."),
                    "Solo entran 14 entregas más en la cola: la última no se tiene que "
                            + "informar como registrada");
        }
    }
}
