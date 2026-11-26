package app;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import estructuras.VectorDinamico;
import modelo.Estudiante;
import modelo.Operacion;
import modelo.Recomendacion;
import modelo.SolicitudLaboratorio;
import pruebas.PruebasSigac;
import servicios.OrganizacionAcademica;
import servicios.SistemaSigac;

/** M6: menú de consola de SIGAC-UTA. Todas las operaciones validan la entrada y muestran errores claros. */
public class Main {

    private static Scanner entrada;
    private static SistemaSigac sistema = new SistemaSigac();

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // se usa la codificación por defecto
        }
        entrada = new Scanner(System.in, "UTF-8");

        boolean salir = false;
        while (!salir) {
            System.out.println();
            System.out.println("=========== SIGAC-UTA ===========");
            System.out.println(" 1. M1 Gestión de estudiantes");
            System.out.println(" 2. M2 Atención e historial (cola FIFO / pila LIFO)");
            System.out.println(" 3. M3 Organización académica y ABB");
            System.out.println(" 4. M4 Red de laboratorios (grafo)");
            System.out.println(" 5. M5 Red social y recomendaciones");
            System.out.println(" 6. Ejecutar CASO INTEGRADOR (reinicia el sistema)");
            System.out.println(" 7. Ejecutar pruebas automáticas");
            System.out.println(" 8. Reiniciar sistema vacío");
            System.out.println(" 0. Salir");
            int opcion = leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1: menuEstudiantes(); break;
                    case 2: menuAtencion(); break;
                    case 3: menuOrganizacion(); break;
                    case 4: menuLaboratorio(); break;
                    case 5: menuSocial(); break;
                    case 6:
                        sistema = new SistemaSigac();
                        CasoIntegrador.ejecutar(sistema, System.out);
                        System.out.println("(El sistema queda cargado con el estado final del caso.)");
                        break;
                    case 7: PruebasSigac.ejecutarTodas(System.out, false); break;
                    case 8:
                        sistema = new SistemaSigac();
                        System.out.println("Sistema reiniciado.");
                        break;
                    case 0: salir = true; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
        System.out.println("Hasta luego.");
    }

    // ------------------------------------------------------------------ M1

    private static void menuEstudiantes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- M1 Gestión de estudiantes ---");
            System.out.println("1. Registrar   2. Buscar por matrícula (ABB)   3. Comparar búsquedas");
            System.out.println("4. Actualizar  5. Eliminar                     6. Listar   0. Volver");
            int op = leerEntero("Opción: ");
            try {
                switch (op) {
                    case 1: {
                        int m = leerEntero("Matrícula: ");
                        String nombre = leerTexto("Nombre: ");
                        System.out.println("Carreras disponibles: " + OrganizacionAcademica.CARRERA_SOFTWARE + " | "
                                + OrganizacionAcademica.CARRERA_TI + " | " + OrganizacionAcademica.CARRERA_TELECOM
                                + " | " + OrganizacionAcademica.CARRERA_INDUSTRIAL);
                        String carrera = leerTexto("Carrera: ");
                        sistema.registrarEstudiante(new Estudiante(m, nombre, carrera));
                        System.out.println("Estudiante registrado.");
                        break;
                    }
                    case 2: {
                        Estudiante e = sistema.getGestion().buscarPorMatricula(leerEntero("Matrícula: "));
                        System.out.println(e == null ? "No encontrado." : e.toString());
                        break;
                    }
                    case 3:
                        System.out.println(sistema.getGestion().comparar(leerEntero("Matrícula: ")));
                        break;
                    case 4: {
                        int m = leerEntero("Matrícula: ");
                        String nombre = leerTexto("Nuevo nombre: ");
                        String carrera = leerTexto("Nueva carrera: ");
                        sistema.actualizarEstudiante(m, nombre, carrera);
                        System.out.println("Estudiante actualizado.");
                        break;
                    }
                    case 5: {
                        Estudiante e = sistema.eliminarEstudiante(leerEntero("Matrícula: "));
                        System.out.println("Eliminado: " + e);
                        break;
                    }
                    case 6: {
                        VectorDinamico<Estudiante> todos = sistema.getGestion().listar();
                        if (todos.estaVacio()) {
                            System.out.println("No hay estudiantes registrados.");
                        }
                        for (Estudiante e : todos) {
                            System.out.println("  " + e);
                        }
                        break;
                    }
                    case 0: volver = true; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ M2

    private static void menuAtencion() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- M2 Atención e historial ---");
            System.out.println("1. Registrar solicitud   2. Atender siguiente (FIFO)   3. Ver cola");
            System.out.println("4. Deshacer última operación (LIFO)   5. Ver historial   0. Volver");
            int op = leerEntero("Opción: ");
            try {
                switch (op) {
                    case 1: {
                        int m = leerEntero("Matrícula del estudiante: ");
                        String equipo = leerTexto("Equipo (p. ej. PC1): ");
                        String desc = leerTexto("Descripción: ");
                        System.out.println("Registrada: " + sistema.registrarSolicitud(m, equipo, desc));
                        break;
                    }
                    case 2:
                        System.out.println("Atendida: " + sistema.atenderSolicitud());
                        break;
                    case 3: {
                        if (sistema.getAtencion().totalPendientes() == 0) {
                            System.out.println("No hay solicitudes pendientes.");
                        }
                        for (SolicitudLaboratorio s : sistema.getAtencion().getPendientes()) {
                            System.out.println("  " + s);
                        }
                        break;
                    }
                    case 4:
                        System.out.println("Deshecha: " + sistema.deshacer());
                        break;
                    case 5: {
                        if (sistema.getAtencion().totalHistorial() == 0) {
                            System.out.println("El historial está vacío.");
                        }
                        for (Operacion o : sistema.getAtencion().getHistorial()) {
                            System.out.println("  " + o);
                        }
                        break;
                    }
                    case 0: volver = true; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ M3

    private static void menuOrganizacion() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- M3 Organización académica y ABB ---");
            System.out.println("1. Ver jerarquía   2. Agregar facultad   3. Agregar carrera");
            System.out.println("4. Recorridos del ABB (pre/in/pos)   5. Dibujar ABB   0. Volver");
            int op = leerEntero("Opción: ");
            try {
                switch (op) {
                    case 1:
                        System.out.print(sistema.getOrganizacion().imprimir());
                        break;
                    case 2:
                        sistema.getOrganizacion().agregarFacultad(leerTexto("Nombre de la facultad: "));
                        System.out.println("Facultad agregada.");
                        break;
                    case 3: {
                        String fac = leerTexto("Facultad: ");
                        String car = leerTexto("Nombre de la carrera: ");
                        sistema.getOrganizacion().agregarCarrera(fac, car);
                        System.out.println("Carrera agregada.");
                        break;
                    }
                    case 4:
                        if (sistema.getGestion().total() == 0) {
                            System.out.println("El ABB está vacío.");
                        } else {
                            System.out.println("Preorden: " + sistema.getGestion().matriculasPreorden().unir(", "));
                            System.out.println("Inorden : " + sistema.getGestion().matriculasInorden().unir(", "));
                            System.out.println("Posorden: " + sistema.getGestion().matriculasPosorden().unir(", "));
                        }
                        break;
                    case 5:
                        System.out.print(sistema.getGestion().getIndice().dibujar() + "\n");
                        break;
                    case 0: volver = true; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ M4

    private static void menuLaboratorio() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- M4 Red de laboratorios (grafo dirigido) ---");
            System.out.println("1. Agregar equipo   2. Agregar conexión   3. Eliminar conexión   4. Eliminar equipo");
            System.out.println("5. BFS   6. DFS   7. No alcanzables   8. Mostrar red   0. Volver");
            int op = leerEntero("Opción: ");
            try {
                switch (op) {
                    case 1:
                        System.out.println(sistema.getLaboratorio().agregarEquipo(leerTexto("Nombre del equipo: "))
                                ? "Equipo agregado." : "El equipo ya existía.");
                        break;
                    case 2: {
                        String o = leerTexto("Origen: ");
                        String d = leerTexto("Destino: ");
                        int peso = leerEntero("Peso (entero > 0): ");
                        System.out.println(sistema.getLaboratorio().agregarConexion(o, d, peso)
                                ? "Conexión agregada." : "La conexión ya existía.");
                        break;
                    }
                    case 3: {
                        String o = leerTexto("Origen: ");
                        String d = leerTexto("Destino: ");
                        int peso = sistema.eliminarConexionLaboratorio(o, d);
                        System.out.println("Conexión eliminada (peso " + peso + "). Se puede deshacer desde M2.");
                        break;
                    }
                    case 4: {
                        int perdidas = sistema.eliminarEquipoLaboratorio(leerTexto("Equipo a eliminar: "));
                        System.out.println("Equipo eliminado junto con " + perdidas + " conexión(es).");
                        break;
                    }
                    case 5:
                        System.out.println("BFS: " + sistema.getLaboratorio().bfs(leerTexto("Inicio: ")).unir(" -> "));
                        break;
                    case 6:
                        System.out.println("DFS: " + sistema.getLaboratorio().dfs(leerTexto("Inicio: ")).unir(" -> "));
                        break;
                    case 7: {
                        VectorDinamico<String> no = sistema.getLaboratorio().noAlcanzables(leerTexto("Inicio: "));
                        System.out.println(no.estaVacio() ? "Todos los equipos son alcanzables." : "No alcanzables: " + no.unir(", "));
                        break;
                    }
                    case 8:
                        System.out.print(sistema.getLaboratorio().descripcion());
                        break;
                    case 0: volver = true; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ M5

    private static void menuSocial() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- M5 Red social ---");
            System.out.println("1. Agregar usuario   2. Agregar amistad   3. Eliminar amistad");
            System.out.println("4. Ver amigos   5. Recomendar contactos   6. Mostrar red   0. Volver");
            int op = leerEntero("Opción: ");
            try {
                switch (op) {
                    case 1:
                        System.out.println(sistema.getSocial().agregarUsuario(leerTexto("Nombre: "))
                                ? "Usuario agregado." : "El usuario ya existía.");
                        break;
                    case 2: {
                        String a = leerTexto("Usuario 1: ");
                        String b = leerTexto("Usuario 2: ");
                        System.out.println(sistema.getSocial().agregarAmistad(a, b)
                                ? "Amistad registrada." : "Ya eran amigos.");
                        break;
                    }
                    case 3: {
                        String a = leerTexto("Usuario 1: ");
                        String b = leerTexto("Usuario 2: ");
                        sistema.getSocial().eliminarAmistad(a, b);
                        System.out.println("Amistad eliminada.");
                        break;
                    }
                    case 4: {
                        VectorDinamico<String> amigos = sistema.getSocial().amigosDe(leerTexto("Usuario: "));
                        System.out.println(amigos.estaVacio() ? "Sin amigos." : "Amigos: " + amigos.unir(", "));
                        break;
                    }
                    case 5: {
                        VectorDinamico<Recomendacion> rec = sistema.getSocial().recomendar(leerTexto("Usuario: "));
                        if (rec.estaVacio()) {
                            System.out.println("No hay recomendaciones.");
                        }
                        for (Recomendacion r : rec) {
                            System.out.println("  " + r);
                        }
                        break;
                    }
                    case 6:
                        System.out.print(sistema.getSocial().descripcion());
                        break;
                    case 0: volver = true; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ entrada

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!entrada.hasNextLine()) {
            System.out.println("\nFin de la entrada. Hasta luego.");
            System.exit(0);
        }
        return entrada.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida: escriba un número entero.");
            }
        }
    }
}
