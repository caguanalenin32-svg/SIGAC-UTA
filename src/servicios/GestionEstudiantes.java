package servicios;

import estructuras.ABB;
import estructuras.ListaEnlazada;
import estructuras.VectorDinamico;
import modelo.ComparacionBusqueda;
import modelo.Estudiante;

/**
 * M1 + índice de M3. Guarda cada estudiante en tres estructuras sincronizadas:
 * un vector dinámico, una lista enlazada y un ABB indexado por matrícula.
 * Los tres comparten la MISMA referencia al objeto Estudiante.
 */
public class GestionEstudiantes {

    private final VectorDinamico<Estudiante> vector = new VectorDinamico<>();
    private final ListaEnlazada<Estudiante> lista = new ListaEnlazada<>();
    private final ABB<Integer, Estudiante> indice = new ABB<>();
    private int comparacionesVector;
    private int comparacionesLista;

    /** Registra rechazando nulos y matrículas duplicadas. */
    public void registrar(Estudiante e) {
        if (e == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        if (!indice.insertar(e.getMatricula(), e)) {
            throw new IllegalArgumentException("Ya existe un estudiante con la matrícula " + e.getMatricula());
        }
        vector.agregar(e);
        lista.agregarAlFinal(e);
    }

    /** Búsqueda por matrícula usando el ABB: promedio O(log n). */
    public Estudiante buscarPorMatricula(int matricula) {
        return indice.buscar(matricula);
    }

    /** Búsqueda secuencial en el vector: O(n). */
    public Estudiante buscarEnVector(int matricula) {
        comparacionesVector = 0;
        for (int i = 0; i < vector.tamanio(); i++) {
            comparacionesVector++;
            if (vector.obtener(i).getMatricula() == matricula) {
                return vector.obtener(i);
            }
        }
        return null;
    }

    /** Búsqueda secuencial en la lista enlazada: O(n). */
    public Estudiante buscarEnLista(int matricula) {
        comparacionesLista = 0;
        for (Estudiante e : lista) {
            comparacionesLista++;
            if (e.getMatricula() == matricula) {
                return e;
            }
        }
        return null;
    }

    /** Ejecuta las tres búsquedas y devuelve el número de comparaciones de cada una. */
    public ComparacionBusqueda comparar(int matricula) {
        Estudiante porVector = buscarEnVector(matricula);
        buscarEnLista(matricula);
        buscarPorMatricula(matricula);
        return new ComparacionBusqueda(matricula, porVector != null, comparacionesVector,
                comparacionesLista, indice.getUltimasComparaciones());
    }

    /**
     * Actualiza nombre y carrera. Devuelve una COPIA del estado anterior (para poder deshacer).
     * Si los datos nuevos no son válidos no se modifica nada.
     */
    public Estudiante actualizar(int matricula, String nombre, String carrera) {
        Estudiante actual = indice.buscar(matricula);
        if (actual == null) {
            throw new IllegalArgumentException("No existe un estudiante con la matrícula " + matricula);
        }
        new Estudiante(matricula, nombre, carrera); // valida antes de modificar
        Estudiante anterior = actual.copiar();
        actual.setNombre(nombre);
        actual.setCarrera(carrera);
        return anterior;
    }

    /** Eliminación segura: lo quita de las tres estructuras o lanza excepción si no existe. */
    public Estudiante eliminar(int matricula) {
        final Estudiante eliminado = indice.eliminar(matricula);
        if (eliminado == null) {
            throw new IllegalArgumentException("No existe un estudiante con la matrícula " + matricula);
        }
        vector.eliminar(vector.indiceDe(eliminado));
        lista.eliminarPrimero(x -> x == eliminado);
        return eliminado;
    }

    public int total() {
        return vector.tamanio();
    }

    /** Copia de los estudiantes en orden de registro. */
    public VectorDinamico<Estudiante> listar() {
        VectorDinamico<Estudiante> copia = new VectorDinamico<>();
        for (Estudiante e : vector) {
            copia.agregar(e);
        }
        return copia;
    }

    public VectorDinamico<Integer> matriculasPreorden() {
        return indice.preorden();
    }

    public VectorDinamico<Integer> matriculasInorden() {
        return indice.inorden();
    }

    public VectorDinamico<Integer> matriculasPosorden() {
        return indice.posorden();
    }

    public ABB<Integer, Estudiante> getIndice() {
        return indice;
    }
}
