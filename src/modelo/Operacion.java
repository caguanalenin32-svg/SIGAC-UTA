package modelo;

/**
 * Operación reversible guardada en la pila de historial (LIFO).
 * Guarda lo necesario para poder deshacerla.
 */
public class Operacion {

    public enum Tipo {
        REGISTRAR_ESTUDIANTE,
        ELIMINAR_ESTUDIANTE,
        ACTUALIZAR_ESTUDIANTE,
        ELIMINAR_CONEXION
    }

    private final Tipo tipo;
    private final Estudiante estudiante; // registrado / eliminado / estado ANTERIOR (según el tipo)
    private final String origen;
    private final String destino;
    private final int peso;

    private Operacion(Tipo tipo, Estudiante estudiante, String origen, String destino, int peso) {
        this.tipo = tipo;
        this.estudiante = estudiante;
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public static Operacion registro(Estudiante registrado) {
        return new Operacion(Tipo.REGISTRAR_ESTUDIANTE, registrado.copiar(), null, null, 0);
    }

    public static Operacion eliminacion(Estudiante eliminado) {
        return new Operacion(Tipo.ELIMINAR_ESTUDIANTE, eliminado.copiar(), null, null, 0);
    }

    public static Operacion actualizacion(Estudiante estadoAnterior) {
        return new Operacion(Tipo.ACTUALIZAR_ESTUDIANTE, estadoAnterior.copiar(), null, null, 0);
    }

    public static Operacion conexionEliminada(String origen, String destino, int peso) {
        return new Operacion(Tipo.ELIMINAR_CONEXION, null, origen, destino, peso);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public int getPeso() {
        return peso;
    }

    @Override
    public String toString() {
        switch (tipo) {
            case REGISTRAR_ESTUDIANTE:
                return "REGISTRAR_ESTUDIANTE " + estudiante;
            case ELIMINAR_ESTUDIANTE:
                return "ELIMINAR_ESTUDIANTE " + estudiante;
            case ACTUALIZAR_ESTUDIANTE:
                return "ACTUALIZAR_ESTUDIANTE (estado anterior) " + estudiante;
            default:
                return "ELIMINAR_CONEXION " + origen + " -> " + destino + " (peso " + peso + ")";
        }
    }
}
