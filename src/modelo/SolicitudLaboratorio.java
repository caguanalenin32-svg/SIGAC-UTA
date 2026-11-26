package modelo;

/** Solicitud de uso de un equipo de laboratorio (se atiende en orden FIFO). */
public class SolicitudLaboratorio {

    private final int id;
    private final int matricula;
    private final String equipo;
    private final String descripcion;

    public SolicitudLaboratorio(int id, int matricula, String equipo, String descripcion) {
        if (equipo == null || equipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El equipo no puede ser nulo ni vacío.");
        }
        this.id = id;
        this.matricula = matricula;
        this.equipo = equipo.trim();
        this.descripcion = (descripcion == null || descripcion.trim().isEmpty())
                ? "Uso de laboratorio" : descripcion.trim();
    }

    public int getId() {
        return id;
    }

    public int getMatricula() {
        return matricula;
    }

    public String getEquipo() {
        return equipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return "Solicitud #" + id + " | matrícula " + matricula + " | equipo " + equipo + " | " + descripcion;
    }
}
