package servicios;

import modelo.Estudiante;

/** Carga los datos del caso integrador obligatorio (sección 6 del examen). */
public final class DatosCasoIntegrador {

    /** Matrículas en el orden de inserción exigido por el enunciado. */
    public static final int[] MATRICULAS = {50, 30, 70, 20, 40, 60, 80};

    private static final String[] NOMBRES = {
        "Marco Aguirre", "Lucía Benítez", "Pablo Cevallos", "Sofía Dávila",
        "Mateo Espín", "Valeria Fiallos", "Andrés Guerra"
    };

    private DatosCasoIntegrador() {
    }

    /** El sistema debe estar vacío (sin estudiantes, equipos ni usuarios). */
    public static void cargar(SistemaSigac s) {
        for (int i = 0; i < MATRICULAS.length; i++) {
            s.registrarEstudiante(new Estudiante(MATRICULAS[i], NOMBRES[i], OrganizacionAcademica.CARRERA_SOFTWARE));
        }

        // Red de laboratorio (dirigida). Orden de creación de vértices y de aristas = orden de recorrido.
        RedLaboratorio lab = s.getLaboratorio();
        String[] equipos = {"PC1", "SW1", "SRV1", "PC2", "PC3", "PC4"};
        for (String equipo : equipos) {
            lab.agregarEquipo(equipo);
        }
        lab.agregarConexion("PC1", "SW1", 1);
        lab.agregarConexion("SW1", "SRV1", 2);
        lab.agregarConexion("SRV1", "PC2", 1);
        lab.agregarConexion("PC1", "PC3", 3);
        // PC4 queda aislado

        // Red social (no dirigida)
        RedSocial social = s.getSocial();
        String[] usuarios = {"Ana", "Beto", "Carla", "Diego", "Elena"};
        for (String u : usuarios) {
            social.agregarUsuario(u);
        }
        social.agregarAmistad("Ana", "Beto");
        social.agregarAmistad("Ana", "Carla");
        social.agregarAmistad("Beto", "Diego");
        social.agregarAmistad("Carla", "Diego");
        social.agregarAmistad("Carla", "Elena");
    }
}
