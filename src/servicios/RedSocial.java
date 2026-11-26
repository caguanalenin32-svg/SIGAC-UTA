package servicios;

import estructuras.Grafo;
import estructuras.VectorDinamico;
import modelo.Recomendacion;

/** M5: grafo social NO dirigido (la amistad es simétrica) y recomendación de contactos de segundo grado. */
public class RedSocial {

    private final Grafo grafo = new Grafo(false, "usuario");

    public boolean agregarUsuario(String nombre) {
        return grafo.agregarVertice(nombre);
    }

    public boolean agregarAmistad(String a, String b) {
        return grafo.agregarArista(a, b, 1);
    }

    public void eliminarAmistad(String a, String b) {
        grafo.eliminarArista(a, b);
    }

    public VectorDinamico<String> amigosDe(String usuario) {
        return grafo.vecinos(usuario);
    }

    public boolean existeUsuario(String nombre) {
        return grafo.existeVertice(nombre);
    }

    /**
     * Recomienda contactos de segundo grado: amigos de mis amigos que NO son el usuario ni sus
     * amigos directos. Orden: más amigos en común primero; empate -> nombre alfabético.
     * Complejidad: O(sum(grado(amigo)) * C), con C = número de candidatos (búsqueda lineal en candidatos).
     */
    public VectorDinamico<Recomendacion> recomendar(String usuario) {
        VectorDinamico<String> amigos = grafo.vecinos(usuario); // lanza excepción si no existe
        VectorDinamico<Recomendacion> candidatos = new VectorDinamico<>();
        for (String amigo : amigos) {
            for (String candidato : grafo.vecinos(amigo)) {
                if (candidato.equals(usuario.trim()) || grafo.existeArista(usuario, candidato)) {
                    continue; // se excluye al propio usuario y a sus amigos directos
                }
                Recomendacion r = buscarCandidato(candidatos, candidato);
                if (r == null) {
                    r = new Recomendacion(candidato);
                    candidatos.agregar(r);
                }
                r.agregarAmigoEnComun(amigo);
            }
        }
        ordenar(candidatos);
        return candidatos;
    }

    private Recomendacion buscarCandidato(VectorDinamico<Recomendacion> candidatos, String nombre) {
        for (Recomendacion r : candidatos) {
            if (r.getNombre().equals(nombre)) {
                return r;
            }
        }
        return null;
    }

    /** Ordenamiento por inserción (estable): comunes descendente, luego nombre ascendente. */
    private void ordenar(VectorDinamico<Recomendacion> v) {
        for (int i = 1; i < v.tamanio(); i++) {
            Recomendacion clave = v.obtener(i);
            int j = i - 1;
            while (j >= 0 && iraAntes(clave, v.obtener(j))) {
                v.establecer(j + 1, v.obtener(j));
                j--;
            }
            v.establecer(j + 1, clave);
        }
    }

    private boolean iraAntes(Recomendacion a, Recomendacion b) {
        if (a.getCantidadEnComun() != b.getCantidadEnComun()) {
            return a.getCantidadEnComun() > b.getCantidadEnComun();
        }
        return a.getNombre().compareTo(b.getNombre()) < 0;
    }

    public String descripcion() {
        return grafo.descripcion();
    }

    public Grafo getGrafo() {
        return grafo;
    }
}
