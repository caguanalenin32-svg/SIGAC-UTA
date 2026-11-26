package servicios;

import estructuras.ArbolGeneral;
import estructuras.VectorDinamico;
import modelo.Estudiante;

/** M3: jerarquía Universidad -> Facultad -> Carrera -> Estudiantes sobre un árbol general. */
public class OrganizacionAcademica {

    public static final String TIPO_UNIVERSIDAD = "UNIVERSIDAD";
    public static final String TIPO_FACULTAD = "FACULTAD";
    public static final String TIPO_CARRERA = "CARRERA";
    public static final String TIPO_ESTUDIANTE = "ESTUDIANTE";

    public static final String UNIVERSIDAD = "UTA";
    public static final String FACULTAD_FISEI = "FISEI";
    public static final String CARRERA_SOFTWARE = "Ingeniería en Software";
    public static final String CARRERA_TI = "Tecnologías de la Información";
    public static final String CARRERA_TELECOM = "Telecomunicaciones";
    public static final String CARRERA_INDUSTRIAL = "Ingeniería Industrial";

    private final ArbolGeneral arbol = new ArbolGeneral();

    /** Crea la jerarquía inicial de la FISEI. */
    public OrganizacionAcademica() {
        arbol.crearRaiz(UNIVERSIDAD, TIPO_UNIVERSIDAD, "Universidad Técnica de Ambato");
        arbol.agregarHijo(UNIVERSIDAD, FACULTAD_FISEI, TIPO_FACULTAD,
                "Facultad de Ingeniería en Sistemas, Electrónica e Industrial");
        arbol.agregarHijo(FACULTAD_FISEI, CARRERA_SOFTWARE, TIPO_CARRERA, "");
        arbol.agregarHijo(FACULTAD_FISEI, CARRERA_TI, TIPO_CARRERA, "");
        arbol.agregarHijo(FACULTAD_FISEI, CARRERA_TELECOM, TIPO_CARRERA, "");
        arbol.agregarHijo(FACULTAD_FISEI, CARRERA_INDUSTRIAL, TIPO_CARRERA, "");
    }

    public void agregarFacultad(String nombre) {
        arbol.agregarHijo(UNIVERSIDAD, nombre, TIPO_FACULTAD, "");
    }

    public void agregarCarrera(String facultad, String carrera) {
        ArbolGeneral.Nodo f = arbol.buscar(facultad);
        if (f == null || !TIPO_FACULTAD.equals(f.getTipo())) {
            throw new IllegalArgumentException("No existe la facultad: " + facultad);
        }
        arbol.agregarHijo(facultad, carrera, TIPO_CARRERA, "");
    }

    public boolean existeCarrera(String carrera) {
        ArbolGeneral.Nodo n = arbol.buscar(carrera);
        return n != null && TIPO_CARRERA.equals(n.getTipo());
    }

    /** Cuelga al estudiante de su carrera. La carrera debe existir. */
    public void agregarEstudiante(Estudiante e) {
        if (e == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        if (!existeCarrera(e.getCarrera())) {
            throw new IllegalArgumentException("No existe la carrera: " + e.getCarrera());
        }
        arbol.agregarHijo(e.getCarrera(), idEstudiante(e.getMatricula()), TIPO_ESTUDIANTE, e.getNombre());
    }

    public void quitarEstudiante(int matricula) {
        arbol.eliminar(idEstudiante(matricula));
    }

    /** Carrera de la que cuelga el estudiante, o null si no está en el árbol. */
    public String carreraDe(int matricula) {
        ArbolGeneral.Nodo n = arbol.buscar(idEstudiante(matricula));
        return n == null ? null : n.getPadre().getId();
    }

    /** Ids de nodo ("EST-50", ...) de los estudiantes de una carrera. */
    public VectorDinamico<String> estudiantesDe(String carrera) {
        if (!existeCarrera(carrera)) {
            throw new IllegalArgumentException("No existe la carrera: " + carrera);
        }
        VectorDinamico<String> res = new VectorDinamico<>();
        for (ArbolGeneral.Nodo hijo : arbol.buscar(carrera).getHijos()) {
            res.agregar(hijo.getId());
        }
        return res;
    }

    public String imprimir() {
        return arbol.imprimir();
    }

    public ArbolGeneral getArbol() {
        return arbol;
    }

    private static String idEstudiante(int matricula) {
        return "EST-" + matricula;
    }
}
