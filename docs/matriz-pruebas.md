# Matriz de pruebas — SIGAC-UTA

Las pruebas se ejecutan con:

```bash
java -cp bin pruebas.PruebasSigac          # resumen en consola
java -cp bin pruebas.PruebasSigac --md     # esta misma tabla en Markdown
```

No requieren JUnit ni ninguna biblioteca externa (código en `src/pruebas/PruebasSigac.java`).
La columna **Tipo** clasifica cada caso como **Normal** (uso esperado), **Extremo** (límites: vacío, un solo
elemento, tamaños grandes, estructuras degeneradas) o **Erróneo** (entradas inválidas que deben rechazarse
sin dejar el sistema en un estado inconsistente).

**Resultado de la última ejecución: 53 pruebas, 53 aprobadas, 0 fallidas**
(salida completa en [`evidencias/salida-pruebas.txt`](evidencias/salida-pruebas.txt)).

| ID | Tipo | Prueba | Resultado esperado | Estado |
| --- | --- | --- | --- | --- |
| M1-N1 | Normal | Registrar los 7 estudiantes del caso y buscar la matrícula 40 en vector, lista y ABB | Las tres búsquedas devuelven el mismo objeto y el total es 7 | ✅ Aprobada |
| M1-N2 | Normal | Actualizar nombre y carrera de un estudiante | Los datos cambian y el estudiante se reubica en la nueva carrera del árbol | ✅ Aprobada |
| M1-N3 | Normal | Eliminar un estudiante existente (matrícula 20) | Desaparece del vector, la lista, el ABB y el árbol académico | ✅ Aprobada |
| M1-N4 | Normal | Comparar la búsqueda secuencial con la del ABB para la matrícula 80 | Vector y lista hacen 7 comparaciones; el ABB solo 3 | ✅ Aprobada |
| M1-E1 | Extremo | Buscar y listar con el sistema vacío | Devuelve null / lista vacía sin lanzar excepciones | ✅ Aprobada |
| M1-E2 | Extremo | Registrar un único estudiante y eliminarlo | La estructura vuelve a quedar vacía | ✅ Aprobada |
| M1-E3 | Extremo | Matrículas en los límites (1 y Integer.MAX_VALUE) | Ambas se aceptan y se encuentran | ✅ Aprobada |
| M1-X1 | Erróneo | Registrar una matrícula duplicada | IllegalArgumentException y los tamaños no cambian | ✅ Aprobada |
| M1-X2 | Erróneo | Registrar un estudiante nulo | IllegalArgumentException | ✅ Aprobada |
| M1-X3 | Erróneo | Crear estudiantes con matrícula 0, negativa, nombre vacío o carrera nula | IllegalArgumentException en cada caso | ✅ Aprobada |
| M1-X4 | Erróneo | Eliminar y actualizar una matrícula inexistente | IllegalArgumentException y nada cambia | ✅ Aprobada |
| M1-X5 | Erróneo | Registrar un estudiante con una carrera que no existe en el árbol académico | IllegalArgumentException y no queda registrado en ninguna estructura | ✅ Aprobada |
| M2-N1 | Normal | Procesar tres solicitudes en orden FIFO | Se atienden en el orden #1, #2, #3 y la cola queda vacía | ✅ Aprobada |
| M2-N2 | Normal | Recuperar dos operaciones en orden LIFO | Se deshacen primero el registro de 80 y luego el de 60 | ✅ Aprobada |
| M2-N3 | Normal | Deshacer una eliminación y un registro | 30 reaparece con el ABB válido; el estudiante registrado se elimina al deshacer | ✅ Aprobada |
| M2-N4 | Normal | Deshacer una actualización de estudiante | Nombre y carrera vuelven a su valor anterior en todas las estructuras | ✅ Aprobada |
| M2-N5 | Normal | Deshacer la eliminación de una conexión del laboratorio | La arista vuelve con su peso original | ✅ Aprobada |
| M2-E1 | Extremo | Atender con la cola vacía | IllegalStateException | ✅ Aprobada |
| M2-E2 | Extremo | Deshacer con el historial vacío | IllegalStateException | ✅ Aprobada |
| M2-E3 | Extremo | Encolar y atender 1000 solicitudes | Se respeta el orden FIFO de principio a fin | ✅ Aprobada |
| M2-X1 | Erróneo | Solicitud de un estudiante inexistente | IllegalArgumentException y la cola no cambia | ✅ Aprobada |
| M2-X2 | Erróneo | Solicitud sobre un equipo inexistente, nulo o vacío | IllegalArgumentException | ✅ Aprobada |
| M2-X3 | Erróneo | Eliminar o deshacer el registro de un estudiante con solicitudes pendientes | IllegalStateException; tras atender la solicitud la operación sí se puede hacer | ✅ Aprobada |
| M3-N1 | Normal | Jerarquía Universidad -> Facultad -> Carrera -> Estudiantes con el caso cargado | Los 7 estudiantes cuelgan de Ingeniería en Software; el árbol tiene 4 niveles | ✅ Aprobada |
| M3-N2 | Normal | Recorridos del ABB del caso (50, 30, 70, 20, 40, 60, 80) | Inorden 20..80; preorden 50,30,20,40,70,60,80; posorden 20,40,30,60,80,70,50 | ✅ Aprobada |
| M3-N3 | Normal | Eliminar del ABB un nodo con dos hijos (30), con un hijo y una hoja | Se conserva la propiedad del ABB; 30 es reemplazado por su sucesor inorden (40) | ✅ Aprobada |
| M3-N4 | Normal | Eliminar la raíz (50) del ABB del caso | La nueva raíz es el sucesor inorden (60) | ✅ Aprobada |
| M3-N5 | Normal | Árbol general: recorridos y eliminación de un subárbol completo | Se eliminan el nodo y todos sus descendientes | ✅ Aprobada |
| M3-E1 | Extremo | ABB vacío: recorridos, búsqueda y eliminación | Resultados vacíos / null sin excepciones | ✅ Aprobada |
| M3-E2 | Extremo | ABB degenerado (500 claves ascendentes) y eliminaciones masivas | Altura 500, búsqueda del último = 500 comparaciones, el ABB sigue siendo válido | ✅ Aprobada |
| M3-X1 | Erróneo | ABB: clave duplicada y claves/valores nulos | insertar devuelve false para duplicados; los nulos lanzan IllegalArgumentException | ✅ Aprobada |
| M3-X2 | Erróneo | Árbol general: padre inexistente, id duplicado, eliminar raíz, árbol vacío | IllegalArgumentException / IllegalStateException según el caso | ✅ Aprobada |
| M3-X3 | Erróneo | Organización académica: facultad inexistente y carrera duplicada | IllegalArgumentException | ✅ Aprobada |
| M4-N1 | Normal | BFS y DFS desde PC1 en la red del caso | BFS: PC1, SW1, PC3, SRV1, PC2 — DFS: PC1, SW1, SRV1, PC2, PC3 | ✅ Aprobada |
| M4-N2 | Normal | Detectar equipos no alcanzables desde PC1 | Solo PC4 | ✅ Aprobada |
| M4-N3 | Normal | Eliminar la arista SW1 -> SRV1 y recalcular la conectividad | BFS: PC1, SW1, PC3; no alcanzables: SRV1, PC2, PC4 | ✅ Aprobada |
| M4-N4 | Normal | Eliminar el vértice SRV1 (aristas entrantes y salientes) | Desaparecen SW1->SRV1 y SRV1->PC2; quedan 2 aristas | ✅ Aprobada |
| M4-N5 | Normal | Grafo no dirigido: la arista existe en ambos sentidos y se elimina en ambos | A-B visible desde A y desde B; eliminarla la quita de los dos lados | ✅ Aprobada |
| M4-E1 | Extremo | Vértice aislado y grafo de un solo vértice | BFS/DFS devuelven solo el vértice; sin no alcanzables | ✅ Aprobada |
| M4-E2 | Extremo | Grafo con ciclo (A->B->C->A) y cadena de 5000 vértices | Cada vértice se visita una sola vez y no hay desbordamiento de pila | ✅ Aprobada |
| M4-X1 | Erróneo | Operaciones con vértices inexistentes, aristas duplicadas, lazos y pesos inválidos | IllegalArgumentException (o false para duplicados) sin alterar el grafo | ✅ Aprobada |
| M4-X2 | Erróneo | Eliminar un equipo con solicitudes pendientes | IllegalStateException; tras atenderlas sí se puede eliminar | ✅ Aprobada |
| M5-N1 | Normal | Recomendar contactos a Ana en la red del caso | Diego (2 en común: Beto y Carla) y Elena (1 en común: Carla), en ese orden | ✅ Aprobada |
| M5-N2 | Normal | Excluir al usuario y a sus amigos directos (triángulo Ana-Beto-Carla) | Ana no recibe recomendaciones | ✅ Aprobada |
| M5-N3 | Normal | Empate de amigos en común | Se ordena alfabéticamente (Diego antes que Zoe) | ✅ Aprobada |
| M5-N4 | Normal | Eliminar la amistad Carla-Diego y recalcular | Diego baja a 1 en común y queda empatado con Elena (orden alfabético) | ✅ Aprobada |
| M5-E1 | Extremo | Usuario sin amigos y usuario cuyo único amigo no tiene más contactos | Lista de recomendaciones vacía | ✅ Aprobada |
| M5-E2 | Extremo | Red grande (200 usuarios en estrella) | Cada hoja recibe 198 recomendaciones con 1 amigo en común (el centro) | ✅ Aprobada |
| M5-X1 | Erróneo | Usuario inexistente, amistad consigo mismo, nulos y duplicados | IllegalArgumentException (false para duplicados) | ✅ Aprobada |
| M6-N1 | Normal | Ejecutar el caso integrador completo de principio a fin | La salida contiene todos los resultados exigidos y el estado final es coherente | ✅ Aprobada |
| M6-E1 | Extremo | Usar todas las consultas del sistema recién creado (todo vacío) | Ninguna consulta lanza excepciones; las operaciones sobre vacío fallan con mensajes claros | ✅ Aprobada |
| M6-X1 | Erróneo | Entradas nulas o en blanco en los servicios | IllegalArgumentException en todos los casos | ✅ Aprobada |
| M6-X2 | Erróneo | Deshacer una conexión cuando uno de sus equipos ya fue eliminado | IllegalArgumentException y la operación conflictiva se descarta del historial | ✅ Aprobada |

## Cobertura por módulo

| Módulo | Normales | Extremas | Erróneas |
| --- | --- | --- | --- |
| M1 Estudiantes (vector / lista / búsqueda) | 4 | 3 | 5 |
| M2 Cola FIFO y pila LIFO | 5 | 3 | 3 |
| M3 Árbol general y ABB | 5 | 2 | 3 |
| M4 Grafo, BFS, DFS, conectividad | 5 | 2 | 2 |
| M5 Recomendaciones | 4 | 2 | 1 |
| M6 Integración | 1 | 1 | 2 |

## Evidencias

- [`evidencias/salida-pruebas.txt`](evidencias/salida-pruebas.txt): salida de las 53 pruebas.
- [`evidencias/salida-caso-integrador.txt`](evidencias/salida-caso-integrador.txt): ejecución del caso integrador desde el menú (opción 6).
- **Capturas de pantalla:** el equipo debe agregar en `docs/evidencias/` capturas de su propia ejecución
  (menú, caso integrador y pruebas) y enlazarlas aquí, por ejemplo: `![Caso integrador](evidencias/captura-caso.png)`.
