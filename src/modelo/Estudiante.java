package modelo;

/** Estudiante de la FISEI. La matrícula es la clave única. */
public class Estudiante {

    private final int matricula;
    private String nombre;
    private String carrera;

    public Estudiante(int matricula, String nombre, String carrera) {
        if (matricula <= 0) {
            throw new IllegalArgumentException("La matrícula debe ser un entero positivo.");
        }
        validar(nombre, "El nombre");
        validar(carrera, "La carrera");
        this.matricula = matricula;
        this.nombre = nombre.trim();
        this.carrera = carrera.trim();
    }

    public int getMatricula() {
        return matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setNombre(String nombre) {
        validar(nombre, "El nombre");
        this.nombre = nombre.trim();
    }

    public void setCarrera(String carrera) {
        validar(carrera, "La carrera");
        this.carrera = carrera.trim();
    }

    /** Copia independiente (se usa como instantánea para poder deshacer cambios). */
    public Estudiante copiar() {
        return new Estudiante(matricula, nombre, carrera);
    }

    private static void validar(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede ser nulo ni vacío.");
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Estudiante && ((Estudiante) o).matricula == matricula;
    }

    @Override
    public int hashCode() {
        return matricula;
    }

    @Override
    public String toString() {
        return "[" + matricula + "] " + nombre + " — " + carrera;
    }
}
