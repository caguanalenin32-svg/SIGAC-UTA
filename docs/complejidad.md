# Complejidad temporal de las operaciones

Notación: **n** = elementos de la estructura, **V** = vértices, **E** = aristas, **h** = altura del ABB,
**d** = grado de un vértice. Todas las estructuras están implementadas manualmente en `src/estructuras/`
(el sistema no usa `TreeMap`, `TreeSet`, `LinkedList`, `ArrayList` ni bibliotecas de grafos; solo `java.util.Iterator`, `Predicate` y `Scanner`).

## Operaciones principales (≥ 4 documentadas)

| # | Operación | Clase / método | Mejor | Promedio | Peor | Justificación |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | Búsqueda por matrícula en el **ABB** | `ABB.buscar` | O(1) | **O(log n)** | O(n) | Se desciende un camino de la raíz a una hoja: h comparaciones. Con inserción ordenada el árbol degenera en lista (h = n); prueba `M3-E2`. |
| 2 | Búsqueda secuencial en **vector** / **lista** | `GestionEstudiantes.buscarEnVector` / `buscarEnLista` | O(1) | O(n) | **O(n)** | Se recorre elemento a elemento hasta encontrarlo. Con las 7 matrículas del caso: 7 comparaciones contra 3 del ABB para la matrícula 80 (`M1-N4`). |
| 3 | Inserción en el ABB | `ABB.insertar` | O(1) | **O(log n)** | O(n) | Descenso iterativo hasta la posición libre. |
| 4 | Eliminación en el ABB (dos hijos → sucesor inorden) | `ABB.eliminar` | O(1) | **O(log n)** | O(n) | Se localiza el nodo (h), se busca el mínimo del subárbol derecho (≤ h) y se elimina. |
| 5 | **BFS** | `Grafo.bfs` | — | **O(V + E)** | O(V + E) | Cada vértice entra una vez a la `Cola` y cada arista se examina una vez (listas de adyacencia). |
| 6 | **DFS** (con `Pila` explícita) | `Grafo.dfs` | — | **O(V + E)** | O(V + E) | Cada vértice se visita una vez y cada arista se examina una vez. |
| 7 | Equipos no alcanzables | `Grafo.noAlcanzables` | — | **O(V + E)** | O(V + E) | Un BFS más un barrido de la marca de visitados. |
| 8 | Eliminar una arista | `Grafo.eliminarArista` | O(1) | O(d) | O(d) | Se recorre la lista de adyacencia del origen (y la del destino si es no dirigido). |
| 9 | Eliminar un vértice (aristas entrantes y salientes) | `Grafo.eliminarVertice` | — | **O(V + E)** | O(V + E) | Hay que revisar la lista de cada vértice para quitar las aristas entrantes y reindexar. |
| 10 | Encolar / desencolar (FIFO) | `Cola.encolar` / `desencolar` | **O(1)** | O(1) | O(1) | Referencias a frente y fin; no hay recorridos. |
| 11 | Push / pop (LIFO) | `Pila.push` / `pop` | **O(1)** | O(1) | O(1) | Se opera solo sobre la cima. |
| 12 | Agregar al final del vector | `VectorDinamico.agregar` | O(1) | **O(1) amortizado** | O(n) | La capacidad se duplica al llenarse; el copiado ocurre pocas veces. |
| 13 | Agregar al final de la lista enlazada | `ListaEnlazada.agregarAlFinal` | **O(1)** | O(1) | O(1) | Se mantiene referencia a la cola. |
| 14 | Recorridos preorden / inorden / posorden | `ABB.preorden` … | — | **O(n)** | O(n) | Cada nodo se visita una vez. |
| 15 | Recomendación de contactos de 2.º grado | `RedSocial.recomendar` | — | **O(Σ grado(amigos) · C)** | — | Se recorren los amigos de cada amigo; buscar al candidato en la lista de candidatos cuesta O(C), con C = candidatos distintos. Luego un ordenamiento por inserción O(C²). |

## Notas de diseño que afectan la complejidad

- Los vértices se buscan por nombre con una búsqueda lineal O(V) (`Grafo.buscar`), de modo que agregar un
  vértice o una arista cuesta O(V) por la validación. Una vez creados, los recorridos usan referencias directas
  y un índice por vértice, por eso BFS/DFS son realmente O(V + E) y no O(V · E).
- El DFS es **iterativo** (con `Pila`) para soportar grafos profundos sin desbordar la pila de llamadas
  (la prueba `M4-E2` recorre una cadena de 5000 vértices). Los recorridos del ABB y del árbol general son
  recursivos: su profundidad máxima es la altura del árbol.
