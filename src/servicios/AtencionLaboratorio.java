package servicios;

import estructuras.Cola;
import estructuras.Pila;
import estructuras.VectorDinamico;
import modelo.Operacion;
import modelo.SolicitudLaboratorio;

/** M2: cola FIFO de solicitudes de laboratorio y pila LIFO con el historial de operaciones reversibles. */
public class AtencionLaboratorio {

    private final Cola<SolicitudLaboratorio> pendientes = new Cola<>();
    private final Pila<Operacion> historial = new Pila<>();
    private final VectorDinamico<SolicitudLaboratorio> atendidas = new VectorDinamico<>();
    private int siguienteId = 1;

    public SolicitudLaboratorio encolar(int matricula, String equipo, String descripcion) {
        SolicitudLaboratorio s = new SolicitudLaboratorio(siguienteId, matricula, equipo, descripcion);
        siguienteId++;
        pendientes.encolar(s);
        return s;
    }

    /** Atiende la solicitud más antigua (FIFO). */
    public SolicitudLaboratorio atenderSiguiente() {
        if (pendientes.estaVacia()) {
            throw new IllegalStateException("No hay solicitudes pendientes.");
        }
        SolicitudLaboratorio s = pendientes.desencolar();
        atendidas.agregar(s);
        return s;
    }

    public SolicitudLaboratorio verSiguiente() {
        if (pendientes.estaVacia()) {
            throw new IllegalStateException("No hay solicitudes pendientes.");
        }
        return pendientes.frente();
    }

    public boolean hayPendientesDeEstudiante(int matricula) {
        for (SolicitudLaboratorio s : pendientes) {
            if (s.getMatricula() == matricula) {
                return true;
            }
        }
        return false;
    }

    public boolean hayPendientesDeEquipo(String equipo) {
        if (equipo == null) {
            return false;
        }
        for (SolicitudLaboratorio s : pendientes) {
            if (s.getEquipo().equals(equipo.trim())) {
                return true;
            }
        }
        return false;
    }

    public void registrarOperacion(Operacion op) {
        if (op == null) {
            throw new IllegalArgumentException("La operación no puede ser nula.");
        }
        historial.push(op);
    }

    public Operacion ultimaOperacion() {
        if (historial.estaVacia()) {
            throw new IllegalStateException("No hay operaciones para deshacer.");
        }
        return historial.peek();
    }

    public Operacion retirarUltimaOperacion() {
        if (historial.estaVacia()) {
            throw new IllegalStateException("No hay operaciones para deshacer.");
        }
        return historial.pop();
    }

    public int totalPendientes() {
        return pendientes.tamanio();
    }

    public int totalHistorial() {
        return historial.tamanio();
    }

    public Iterable<SolicitudLaboratorio> getPendientes() {
        return pendientes;
    }

    /** Del más reciente al más antiguo. */
    public Iterable<Operacion> getHistorial() {
        return historial;
    }

    public VectorDinamico<SolicitudLaboratorio> getAtendidas() {
        return atendidas;
    }
}
