package estructuras;

/**
 * Grafo dinámico con listas de adyacencia, ponderado y configurable como dirigido o no dirigido.
 *
 * Reglas (documentadas para que las pruebas sean reproducibles):
 *  - Los vecinos de un vértice se guardan y recorren en ORDEN DE INSERCIÓN de las aristas.
 *  - En un grafo dirigido, agregarArista(o, d) crea solo o -> d.
 *  - En un grafo no dirigido se crean ambas direcciones con el mismo peso.
 *  - No se permiten lazos (o == d), pesos <= 0 ni aristas duplicadas.
 *  - eliminarVertice elimina también TODAS las aristas entrantes y salientes.
 *  - BFS usa una Cola propia; DFS usa una Pila propia (iterativo, sin recursión).
 *
 * V = vértices, E = aristas.
 */
public class Grafo {

    private static class Arista {
        final Vertice destino;
        final int peso;

        Arista(Vertice destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    private static class Vertice {
        final String nombre;
        int indice;
        final ListaEnlazada<Arista> adyacentes = new ListaEnlazada<>();

        Vertice(String nombre, int indice) {
            this.nombre = nombre;
            this.indice = indice;
        }
    }

    private final boolean dirigido;
    private final String etiqueta; // cómo se llama un vértice en los mensajes ("equipo", "usuario", ...)
    private final VectorDinamico<Vertice> vertices = new VectorDinamico<>();
    private int aristas;

    public Grafo(boolean dirigido) {
        this(dirigido, "vértice");
    }

    public Grafo(boolean dirigido, String etiquetaVertice) {
        this.dirigido = dirigido;
        this.etiqueta = etiquetaVertice;
    }

    public boolean esDirigido() {
        return dirigido;
    }

    // ------------------------------------------------------------ vértices

    /** O(V) por la comprobación de duplicados. Devuelve false si ya existe. */
    public boolean agregarVertice(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del " + etiqueta + " no puede ser nulo ni vacío.");
        }
        if (buscar(nombre) != null) {
            return false;
        }
        vertices.agregar(new Vertice(nombre.trim(), vertices.tamanio()));
        return true;
    }

    /**
     * Elimina el vértice, todas sus aristas salientes y todas las ENTRANTES de los demás vértices.
     * O(V + E). Devuelve cuántas aristas desaparecieron.
     */
    public int eliminarVertice(String nombre) {
        final Vertice objetivo = exigir(nombre);
        int antes = aristas;
        for (Vertice v : vertices) {
            if (v != objetivo) {
                v.adyacentes.eliminarPrimero(a -> a.destino == objetivo);
            }
        }
        vertices.eliminar(objetivo.indice);
        for (int i = 0; i < vertices.tamanio(); i++) {
            vertices.obtener(i).indice = i;
        }
        aristas = contarAristas();
        return antes - aristas;
    }

    public boolean existeVertice(String nombre) {
        return buscar(nombre) != null;
    }

    // ------------------------------------------------------------ aristas

    /** O(grado). Devuelve false si la arista ya existía. */
    public boolean agregarArista(String origen, String destino, int peso) {
        Vertice vo = exigir(origen);
        Vertice vd = exigir(destino);
        if (vo == vd) {
            throw new IllegalArgumentException("No se permiten lazos (origen = destino): " + origen);
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser un entero positivo.");
        }
        if (buscarArista(vo, vd) != null) {
            return false;
        }
        vo.adyacentes.agregarAlFinal(new Arista(vd, peso));
        if (!dirigido) {
            vd.adyacentes.agregarAlFinal(new Arista(vo, peso));
        }
        aristas++;
        return true;
    }

    /** O(grado). Devuelve el peso de la arista eliminada; lanza excepción si no existe. */
    public int eliminarArista(String origen, String destino) {
        final Vertice vo = exigir(origen);
        final Vertice vd = exigir(destino);
        Arista eliminada = vo.adyacentes.eliminarPrimero(a -> a.destino == vd);
        if (eliminada == null) {
            throw new IllegalArgumentException("No existe la conexión " + origen + " -> " + destino);
        }
        if (!dirigido) {
            vd.adyacentes.eliminarPrimero(a -> a.destino == vo);
        }
        aristas--;
        return eliminada.peso;
    }

    public boolean existeArista(String origen, String destino) {
        Vertice vo = buscar(origen);
        Vertice vd = buscar(destino);
        return vo != null && vd != null && buscarArista(vo, vd) != null;
    }

    /** Peso de la arista o -> d; lanza excepción si no existe. */
    public int pesoDe(String origen, String destino) {
        Vertice vo = exigir(origen);
        Vertice vd = exigir(destino);
        Arista a = buscarArista(vo, vd);
        if (a == null) {
            throw new IllegalArgumentException("No existe la conexión " + origen + " -> " + destino);
        }
        return a.peso;
    }

    // ------------------------------------------------------------ recorridos

    /** BFS (anchura) con cola. O(V + E). Visita los vecinos en orden de inserción. */
    public VectorDinamico<String> bfs(String inicio) {
        Vertice ini = exigir(inicio);
        return bfs(ini, new boolean[vertices.tamanio()]);
    }

    private VectorDinamico<String> bfs(Vertice ini, boolean[] visitado) {
        VectorDinamico<String> orden = new VectorDinamico<>();
        Cola<Vertice> cola = new Cola<>();
        visitado[ini.indice] = true;
        cola.encolar(ini);
        while (!cola.estaVacia()) {
            Vertice actual = cola.desencolar();
            orden.agregar(actual.nombre);
            for (Arista a : actual.adyacentes) {
                if (!visitado[a.destino.indice]) {
                    visitado[a.destino.indice] = true;
                    cola.encolar(a.destino);
                }
            }
        }
        return orden;
    }

    /**
     * DFS (profundidad) con pila explícita. O(V + E). Produce el mismo orden que la versión
     * recursiva: se apilan los vecinos en orden inverso para visitar primero el primero insertado.
     */
    public VectorDinamico<String> dfs(String inicio) {
        Vertice ini = exigir(inicio);
        boolean[] visitado = new boolean[vertices.tamanio()];
        VectorDinamico<String> orden = new VectorDinamico<>();
        Pila<Vertice> pila = new Pila<>();
        pila.push(ini);
        while (!pila.estaVacia()) {
            Vertice actual = pila.pop();
            if (visitado[actual.indice]) {
                continue;
            }
            visitado[actual.indice] = true;
            orden.agregar(actual.nombre);
            VectorDinamico<Vertice> vecinos = new VectorDinamico<>();
            for (Arista a : actual.adyacentes) {
                vecinos.agregar(a.destino);
            }
            for (int i = vecinos.tamanio() - 1; i >= 0; i--) {
                Vertice v = vecinos.obtener(i);
                if (!visitado[v.indice]) {
                    pila.push(v);
                }
            }
        }
        return orden;
    }

    /** Vértices que NO se pueden alcanzar desde 'inicio' (en orden de creación). O(V + E). */
    public VectorDinamico<String> noAlcanzables(String inicio) {
        Vertice ini = exigir(inicio);
        boolean[] visitado = new boolean[vertices.tamanio()];
        bfs(ini, visitado);
        VectorDinamico<String> res = new VectorDinamico<>();
        for (int i = 0; i < vertices.tamanio(); i++) {
            if (!visitado[i]) {
                res.agregar(vertices.obtener(i).nombre);
            }
        }
        return res;
    }

    // ------------------------------------------------------------ consultas

    /** Nombres de los vecinos salientes en orden de inserción. */
    public VectorDinamico<String> vecinos(String nombre) {
        Vertice v = exigir(nombre);
        VectorDinamico<String> res = new VectorDinamico<>();
        for (Arista a : v.adyacentes) {
            res.agregar(a.destino.nombre);
        }
        return res;
    }

    public VectorDinamico<String> vertices() {
        VectorDinamico<String> res = new VectorDinamico<>();
        for (Vertice v : vertices) {
            res.agregar(v.nombre);
        }
        return res;
    }

    public int numVertices() {
        return vertices.tamanio();
    }

    public int numAristas() {
        return aristas;
    }

    /** Lista de adyacencia legible: "PC1 -> SW1(1), PC3(3)". */
    public String descripcion() {
        if (vertices.estaVacio()) {
            return "(grafo vacío)";
        }
        StringBuilder sb = new StringBuilder();
        for (Vertice v : vertices) {
            sb.append(v.nombre).append(dirigido ? " -> " : " -- ");
            if (v.adyacentes.estaVacia()) {
                sb.append("(sin conexiones)");
            } else {
                boolean primero = true;
                for (Arista a : v.adyacentes) {
                    if (!primero) {
                        sb.append(", ");
                    }
                    sb.append(a.destino.nombre).append('(').append(a.peso).append(')');
                    primero = false;
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // ------------------------------------------------------------ internos

    private Vertice buscar(String nombre) {
        if (nombre == null) {
            return null;
        }
        String limpio = nombre.trim();
        for (Vertice v : vertices) {
            if (v.nombre.equals(limpio)) {
                return v;
            }
        }
        return null;
    }

    private Vertice exigir(String nombre) {
        Vertice v = buscar(nombre);
        if (v == null) {
            throw new IllegalArgumentException("No existe el " + etiqueta + ": " + nombre);
        }
        return v;
    }

    private Arista buscarArista(final Vertice origen, final Vertice destino) {
        return origen.adyacentes.buscarPrimero(a -> a.destino == destino);
    }

    private int contarAristas() {
        int suma = 0;
        for (Vertice v : vertices) {
            suma += v.adyacentes.tamanio();
        }
        return dirigido ? suma : suma / 2;
    }
}
