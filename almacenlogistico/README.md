# Almacén logístico

Proyecto de Algoritmos y Estructuras de Datos para administrar productos, entregas
de proveedores, pedidos de sucursales y terminales de carga.

## Estructuras implementadas

El proyecto incluye listas sobre arreglo, listas simplemente y doblemente enlazadas,
listas circulares, pila, cola, conjunto y cola de prioridad.

Además de `ColaPrioridad`, respaldada por `ListaArray`, se incorporó
`ColaPrioridadDobleEnlazada`, respaldada por `ListaDoblementeEnlazada`.

## Comparación de las colas de prioridad

Ambas mantienen los elementos ordenados según el comparador recibido. El elemento
menor según ese comparador es el primero en salir.

| Operación | `ColaPrioridad` (array) | `ColaPrioridadDobleEnlazada` |
| --- | --- | --- |
| Insertar ordenado | O(n) | O(n) |
| Consultar frente | O(1) | O(1) |
| Quitar frente | O(n) | O(1) |
| Obtener por índice | O(1) | O(n) |
| Memoria por elemento | Menor | Mayor, por enlaces anterior y siguiente |

La versión doblemente enlazada evita desplazar todos los elementos al quitar el
pedido prioritario. Por eso es conveniente cuando predominan las operaciones de
atender o despachar pedidos.

## Pruebas

Para ejecutar las pruebas funcionales:

```bash
mvn test
```

## Benchmark de extracción

El benchmark prepara ambas colas y mide solamente el tiempo de ejecutar
`quitaDeCola()` hasta vaciarlas. Realiza cinco repeticiones y reporta la mediana.

Primero compilá las clases de prueba:

```bash
mvn test
```

Luego ejecutá el benchmark con la cantidad de elementos deseada:

```bash
java -cp target/test-classes:target/classes \
  ucu.edu.implementaciones.BenchmarkColaPrioridad 5000
```

El número `5000` puede cambiarse. Los tiempos exactos dependen de la computadora,
pero al crecer la cantidad de elementos debería observarse que la extracción sobre
la lista doblemente enlazada escala mejor.
