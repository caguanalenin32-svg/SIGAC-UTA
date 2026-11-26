package servicios;

import estructuras.Grafo;
import estructuras.VectorDinamico;

/**
 * M4: red de equipos de laboratorio sobre un grafo DIRIGIDO y ponderado.
 * Las aristas siguen el sentido de las flechas del enunciado (PC1 -> SW1 -> SRV1 -> PC2, PC1 -> PC3).
 */
public class RedLaboratorio {

    private final Grafo grafo = new Grafo(true, "equipo");

    public boolean agregarEquipo(String nombre) {
        return grafo.agregarVertice(nombre);
    }

    public boolean agregarConexion(String origen, String destino, int peso) {
        return grafo.agregarArista(origen, destino, peso);
    }

    /** Devuelve el peso de la conexión eliminada. */
    public int eliminarConexion(String origen, String destino) {
        return grafo.eliminarArista(origen, destino);
    }

    /** Elimina el equipo y todas las conexiones entrantes y salientes. Devuelve cuántas conexiones se perdieron. */
    public int eliminarEquipo(String nombre) {
        return grafo.eliminarVertice(nombre);
    }

    public boolean existeEquipo(String nombre) {
        return grafo.existeVertice(nombre);
    }

    public boolean existeConexion(String origen, String destino) {
        return grafo.existeArista(origen, destino);
    }

    public VectorDinamico<String> bfs(String inicio) {
        return grafo.bfs(inicio);
    }

    public VectorDinamico<String> dfs(String inicio) {
        return grafo.dfs(inicio);
    }

    public VectorDinamico<String> noAlcanzables(String inicio) {
        return grafo.noAlcanzables(inicio);
    }

    public int totalEquipos() {
        return grafo.numVertices();
    }

    public int totalConexiones() {
        return grafo.numAristas();
    }

    public String descripcion() {
        return grafo.descripcion();
    }

    public Grafo getGrafo() {
        return grafo;
    }
}
