package servicios;

import modelo.Estudiante;
import modelo.Operacion;
import modelo.SolicitudLaboratorio;

/**
 * M6: fachada que integra todos los módulos para que compartan datos y funcionen como una sola aplicación.
 *
 * Reglas de integridad:
 *  - Un estudiante solo se registra si su carrera existe en el árbol académico.
 *  - Registrar/eliminar/actualizar mantiene sincronizados vector, lista, ABB y árbol general.
 *  - Una solicitud solo se crea si el estudiante y el equipo existen.
 *  - No se puede eliminar un estudiante ni un equipo que tengan solicitudes pendientes.
 *  - Las operaciones reversibles se apilan en el historial y se pueden deshacer en orden LIFO.
 */
public class SistemaSigac {

    private final GestionEstudiantes gestion = new GestionEstudiantes();
    private final OrganizacionAcademica organizacion = new OrganizacionAcademica();
    private final RedLaboratorio laboratorio = new RedLaboratorio();
    private final RedSocial social = new RedSocial();
    private final AtencionLaboratorio atencion = new AtencionLaboratorio();

    // ------------------------------------------------------------ estudiantes (reversibles)

    public void registrarEstudiante(Estudiante e) {
        aplicarRegistro(e);
        atencion.registrarOperacion(Operacion.registro(e));
    }

    public Estudiante eliminarEstudiante(int matricula) {
        if (atencion.hayPendientesDeEstudiante(matricula)) {
            throw new IllegalStateException("El estudiante " + matricula
                    + " tiene solicitudes pendientes; atiéndalas antes de eliminarlo.");
        }
        Estudiante eliminado = aplicarEliminacion(matricula);
        atencion.registrarOperacion(Operacion.eliminacion(eliminado));
        return eliminado;
    }

    public void actualizarEstudiante(int matricula, String nombre, String carrera) {
        if (carrera == null || !organizacion.existeCarrera(carrera.trim())) {
            throw new IllegalArgumentException("No existe la carrera: " + carrera);
        }
        Estudiante anterior = aplicarActualizacion(matricula, nombre, carrera.trim());
        atencion.registrarOperacion(Operacion.actualizacion(anterior));
    }

    // ------------------------------------------------------------ laboratorio

    public int eliminarConexionLaboratorio(String origen, String destino) {
        int peso = laboratorio.eliminarConexion(origen, destino);
        atencion.registrarOperacion(Operacion.conexionEliminada(origen.trim(), destino.trim(), peso));
        return peso;
    }

    /** Elimina el equipo y sus conexiones (no reversible). Falla si hay solicitudes pendientes para él. */
    public int eliminarEquipoLaboratorio(String equipo) {
        if (atencion.hayPendientesDeEquipo(equipo)) {
            throw new IllegalStateException("El equipo " + equipo
                    + " tiene solicitudes pendientes; atiéndalas antes de eliminarlo.");
        }
        return laboratorio.eliminarEquipo(equipo);
    }

    // ------------------------------------------------------------ solicitudes FIFO

    public SolicitudLaboratorio registrarSolicitud(int matricula, String equipo, String descripcion) {
        if (gestion.buscarPorMatricula(matricula) == null) {
            throw new IllegalArgumentException("No existe un estudiante con la matrícula " + matricula);
        }
        if (equipo == null || !laboratorio.existeEquipo(equipo)) {
            throw new IllegalArgumentException("No existe el equipo de laboratorio: " + equipo);
        }
        return atencion.encolar(matricula, equipo.trim(), descripcion);
    }

    public SolicitudLaboratorio atenderSolicitud() {
        return atencion.atenderSiguiente();
    }

    // ------------------------------------------------------------ deshacer (LIFO)

    /**
     * Deshace la última operación reversible y la devuelve.
     * Si la reversión no es posible por un conflicto (p. ej. la matrícula fue reutilizada), la operación
     * se descarta del historial y se propaga la excepción.
     * Registrar un estudiante con solicitudes pendientes NO se puede deshacer: la operación se conserva.
     */
    public Operacion deshacer() {
        Operacion op = atencion.ultimaOperacion();
        if (op.getTipo() == Operacion.Tipo.REGISTRAR_ESTUDIANTE
                && atencion.hayPendientesDeEstudiante(op.getEstudiante().getMatricula())) {
            throw new IllegalStateException("No se puede deshacer: el estudiante "
                    + op.getEstudiante().getMatricula() + " tiene solicitudes pendientes.");
        }
        atencion.retirarUltimaOperacion();
        switch (op.getTipo()) {
            case REGISTRAR_ESTUDIANTE:
                aplicarEliminacion(op.getEstudiante().getMatricula());
                break;
            case ELIMINAR_ESTUDIANTE:
                aplicarRegistro(op.getEstudiante().copiar());
                break;
            case ACTUALIZAR_ESTUDIANTE:
                aplicarActualizacion(op.getEstudiante().getMatricula(),
                        op.getEstudiante().getNombre(), op.getEstudiante().getCarrera());
                break;
            default:
                laboratorio.agregarConexion(op.getOrigen(), op.getDestino(), op.getPeso());
                break;
        }
        return op;
    }

    // ------------------------------------------------------------ operaciones internas (sin historial)

    private void aplicarRegistro(Estudiante e) {
        if (e == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        if (!organizacion.existeCarrera(e.getCarrera())) {
            throw new IllegalArgumentException("No existe la carrera: " + e.getCarrera());
        }
        gestion.registrar(e);
        organizacion.agregarEstudiante(e);
    }

    private Estudiante aplicarEliminacion(int matricula) {
        Estudiante eliminado = gestion.eliminar(matricula);
        organizacion.quitarEstudiante(matricula);
        return eliminado;
    }

    private Estudiante aplicarActualizacion(int matricula, String nombre, String carrera) {
        if (!organizacion.existeCarrera(carrera)) {
            throw new IllegalArgumentException("No existe la carrera: " + carrera);
        }
        Estudiante anterior = gestion.actualizar(matricula, nombre, carrera);
        organizacion.quitarEstudiante(matricula);
        organizacion.agregarEstudiante(gestion.buscarPorMatricula(matricula));
        return anterior;
    }

    // ------------------------------------------------------------ acceso a módulos

    public GestionEstudiantes getGestion() {
        return gestion;
    }

    public OrganizacionAcademica getOrganizacion() {
        return organizacion;
    }

    public RedLaboratorio getLaboratorio() {
        return laboratorio;
    }

    public RedSocial getSocial() {
        return social;
    }

    public AtencionLaboratorio getAtencion() {
        return atencion;
    }
}
