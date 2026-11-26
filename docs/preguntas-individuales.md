# Preparación para la comprobación individual (sección 8 del examen)

Cada integrante responde una pregunta sobre su código. Respuestas de referencia y dónde está el código:

## 1. Eliminación de un nodo del ABB con dos hijos
**Archivo:** `src/estructuras/ABB.java` → `eliminar(Nodo, K)`.
Se busca el **sucesor inorden** (el nodo más a la izquierda del subárbol derecho, `minimo`). Su clave y valor
se copian al nodo que se elimina y después se borra el sucesor original con `eliminarMinimo` (que tiene como
mucho un hijo derecho). Así se conserva la propiedad *izquierda < nodo < derecha*.
Ejemplo del caso: al eliminar 30 (hijos 20 y 40) lo reemplaza 40 → preorden `50, 40, 20, 70, 60, 80`.

## 2. Uso de la cola en BFS
**Archivo:** `src/estructuras/Grafo.java` → `bfs`.
La cola (FIFO) garantiza visitar los vértices por niveles: primero el inicio, luego todos sus vecinos, luego los
vecinos de estos. Se marca el vértice como visitado **al encolarlo** para no encolarlo dos veces. Desde PC1 el
orden es `PC1, SW1, PC3, SRV1, PC2`. Coste O(V + E).

## 3. Exclusión de amigos directos en las recomendaciones
**Archivo:** `src/servicios/RedSocial.java` → `recomendar`.
Para cada amigo se recorren sus amigos; se descarta al candidato si es el propio usuario
(`candidato.equals(usuario)`) o si ya hay arista entre el usuario y el candidato
(`grafo.existeArista(usuario, candidato)`). Los candidatos restantes acumulan los amigos en común que los
justifican y se ordenan por cantidad (descendente) y nombre (ascendente).

## 4. Eliminación de aristas entrantes a un vértice
**Archivo:** `src/estructuras/Grafo.java` → `eliminarVertice`.
En un grafo con listas de adyacencia, las aristas **salientes** desaparecen al descartar la lista del vértice,
pero las **entrantes** están en las listas de los demás vértices: hay que recorrer todos los vértices y quitar
de su lista cualquier arista cuyo destino sea el vértice eliminado; después se reindexan los vértices.
Coste O(V + E). Prueba: `M4-N4`.
