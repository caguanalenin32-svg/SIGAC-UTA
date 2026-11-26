package pruebas;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import app.CasoIntegrador;
import estructuras.ABB;
import estructuras.ArbolGeneral;
import estructuras.Grafo;
import estructuras.VectorDinamico;
import modelo.ComparacionBusqueda;
import modelo.Estudiante;
import modelo.Operacion;
import modelo.Recomendacion;
import modelo.SolicitudLaboratorio;
import servicios.DatosCasoIntegrador;
import servicios.GestionEstudiantes;
import servicios.OrganizacionAcademica;
import servicios.RedLaboratorio;
import servicios.RedSocial;
import servicios.SistemaSigac;

/**
 * Pruebas automáticas SIN dependencias externas (no requiere JUnit).
 * Ejecutar:  java -cp bin pruebas.PruebasSigac          (resumen)
 *            java -cp bin pruebas.PruebasSigac --md     (tabla en Markdown para docs/matriz-pruebas.md)
 */
public class PruebasSigac {

    private interface Cuerpo {
        void ejecutar() throws Exception;
    }

    private static class Caso {
        final String id;
        final String tipo;
        final String descripcion;
        final String esperado;
        final Cuerpo cuerpo;
        boolean aprobado;
        String detalleError = "";

        Caso(String id, String tipo, String descripcion, String esperado, Cuerpo cuerpo) {
            this.id = id;
            this.tipo = tipo;
            this.descripcion = descripcion;
            this.esperado = esperado;
            this.cuerpo = cuerpo;
        }
    }

    private static final List<Caso> CASOS = new ArrayList<>();

    private static final String SW = OrganizacionAcademica.CARRERA_SOFTWARE;

    // ================================================================== infraestructura

    private static void caso(String id, String tipo, String descripcion, String esperado, Cuerpo cuerpo) {
        CASOS.add(new Caso(id, tipo, descripcion, esperado, cuerpo));
    }

    private static void afirmar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static void igual(Object esperado, Object real, String mensaje) {
        if (!String.valueOf(esperado).equals(String.valueOf(real))) {
            throw new AssertionError(mensaje + " -> esperado: <" + esperado + "> pero fue: <" + real + ">");
        }
    }

    private static void lanza(Class<? extends Throwable> tipo, Cuerpo cuerpo, String mensaje) {
        try {
            cuerpo.ejecutar();
        } catch (Throwable t) {
            if (tipo.isInstance(t)) {
                return;
            }
            throw new AssertionError(mensaje + " -> se esperaba " + tipo.getSimpleName()
                    + " pero se lanzó " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        throw new AssertionError(mensaje + " -> se esperaba " + tipo.getSimpleName() + " y no se lanzó nada");
    }

    private static SistemaSigac sistemaCaso() {
        SistemaSigac s = new SistemaSigac();
        DatosCasoIntegrador.cargar(s);
        return s;
    }

    private static Estudiante est(int m) {
        return new Estudiante(m, "Estudiante " + m, SW);
    }

    private static Grafo grafoDelCaso() {
        return sistemaCaso().getLaboratorio().getGrafo();
    }

    // ================================================================== M1

    private static void m1() {
        caso("M1-N1", "Normal", "Registrar los 7 estudiantes del caso y buscar la matrícula 40 en vector, lista y ABB",
                "Las tres búsquedas devuelven el mismo objeto y el total es 7", () -> {
            GestionEstudiantes g = sistemaCaso().getGestion();
            Estudiante a = g.buscarEnVector(40);
            Estudiante b = g.buscarEnLista(40);
            Estudiante c = g.buscarPorMatricula(40);
            afirmar(a != null && a == b && b == c, "las búsquedas deben coincidir");
            igual(7, g.total(), "total");
        });
        caso("M1-N2", "Normal", "Actualizar nombre y carrera de un estudiante",
                "Los datos cambian y el estudiante se reubica en la nueva carrera del árbol", () -> {
            SistemaSigac s = sistemaCaso();
            s.actualizarEstudiante(40, "Mateo E. Espín", OrganizacionAcademica.CARRERA_TI);
            igual("Mateo E. Espín", s.getGestion().buscarPorMatricula(40).getNombre(), "nombre");
            igual(OrganizacionAcademica.CARRERA_TI, s.getOrganizacion().carreraDe(40), "carrera en el árbol");
            igual(6, s.getOrganizacion().estudiantesDe(SW).tamanio(), "estudiantes en Software");
        });
        caso("M1-N3", "Normal", "Eliminar un estudiante existente (matrícula 20)",
                "Desaparece del vector, la lista, el ABB y el árbol académico", () -> {
            SistemaSigac s = sistemaCaso();
            s.eliminarEstudiante(20);
            GestionEstudiantes g = s.getGestion();
            afirmar(g.buscarEnVector(20) == null && g.buscarEnLista(20) == null
                    && g.buscarPorMatricula(20) == null, "no debe existir en ninguna estructura");
            afirmar(s.getOrganizacion().carreraDe(20) == null, "no debe estar en el árbol académico");
            igual(6, g.total(), "total");
        });
        caso("M1-N4", "Normal", "Comparar la búsqueda secuencial con la del ABB para la matrícula 80",
                "Vector y lista hacen 7 comparaciones; el ABB solo 3", () -> {
            ComparacionBusqueda c = sistemaCaso().getGestion().comparar(80);
            afirmar(c.isEncontrado(), "debe encontrarse");
            igual(7, c.getComparacionesVector(), "vector");
            igual(7, c.getComparacionesLista(), "lista");
            igual(3, c.getComparacionesABB(), "ABB");
        });
        caso("M1-E1", "Extremo", "Buscar y listar con el sistema vacío",
                "Devuelve null / lista vacía sin lanzar excepciones", () -> {
            GestionEstudiantes g = new SistemaSigac().getGestion();
            afirmar(g.buscarPorMatricula(10) == null && g.buscarEnVector(10) == null
                    && g.buscarEnLista(10) == null, "búsquedas sobre vacío");
            igual(0, g.total(), "total");
            afirmar(g.listar().estaVacio(), "listar vacío");
            afirmar(!g.comparar(10).isEncontrado(), "comparar sobre vacío");
        });
        caso("M1-E2", "Extremo", "Registrar un único estudiante y eliminarlo",
                "La estructura vuelve a quedar vacía", () -> {
            SistemaSigac s = new SistemaSigac();
            s.registrarEstudiante(est(5));
            s.eliminarEstudiante(5);
            igual(0, s.getGestion().total(), "total");
            afirmar(s.getGestion().getIndice().estaVacio(), "ABB vacío");
        });
        caso("M1-E3", "Extremo", "Matrículas en los límites (1 y Integer.MAX_VALUE)",
                "Ambas se aceptan y se encuentran", () -> {
            SistemaSigac s = new SistemaSigac();
            s.registrarEstudiante(est(1));
            s.registrarEstudiante(est(Integer.MAX_VALUE));
            afirmar(s.getGestion().buscarPorMatricula(1) != null, "matrícula 1");
            afirmar(s.getGestion().buscarPorMatricula(Integer.MAX_VALUE) != null, "matrícula máxima");
        });
        caso("M1-X1", "Erróneo", "Registrar una matrícula duplicada",
                "IllegalArgumentException y los tamaños no cambian", () -> {
            SistemaSigac s = sistemaCaso();
            lanza(IllegalArgumentException.class, () -> s.registrarEstudiante(est(50)), "duplicado");
            igual(7, s.getGestion().total(), "total");
            igual(7, s.getGestion().getIndice().tamanio(), "tamaño ABB");
            igual(7, s.getOrganizacion().estudiantesDe(SW).tamanio(), "árbol académico");
        });
        caso("M1-X2", "Erróneo", "Registrar un estudiante nulo",
                "IllegalArgumentException", () -> {
            SistemaSigac s = new SistemaSigac();
            lanza(IllegalArgumentException.class, () -> s.registrarEstudiante(null), "nulo");
            lanza(IllegalArgumentException.class, () -> s.getGestion().registrar(null), "nulo en gestión");
        });
        caso("M1-X3", "Erróneo", "Crear estudiantes con matrícula 0, negativa, nombre vacío o carrera nula",
                "IllegalArgumentException en cada caso", () -> {
            lanza(IllegalArgumentException.class, () -> new Estudiante(0, "A", SW), "matrícula 0");
            lanza(IllegalArgumentException.class, () -> new Estudiante(-5, "A", SW), "matrícula negativa");
            lanza(IllegalArgumentException.class, () -> new Estudiante(1, "   ", SW), "nombre vacío");
            lanza(IllegalArgumentException.class, () -> new Estudiante(1, "A", null), "carrera nula");
        });
        caso("M1-X4", "Erróneo", "Eliminar y actualizar una matrícula inexistente",
                "IllegalArgumentException y nada cambia", () -> {
            SistemaSigac s = sistemaCaso();
            lanza(IllegalArgumentException.class, () -> s.eliminarEstudiante(999), "eliminar inexistente");
            lanza(IllegalArgumentException.class, () -> s.actualizarEstudiante(999, "X", SW), "actualizar inexistente");
            igual(7, s.getGestion().total(), "total");
            igual(7, s.getAtencion().totalHistorial(), "el historial no debe registrar operaciones fallidas");
        });
        caso("M1-X5", "Erróneo", "Registrar un estudiante con una carrera que no existe en el árbol académico",
                "IllegalArgumentException y no queda registrado en ninguna estructura", () -> {
            SistemaSigac s = new SistemaSigac();
            lanza(IllegalArgumentException.class,
                    () -> s.registrarEstudiante(new Estudiante(9, "Ana", "Carrera Inventada")), "carrera inexistente");
            igual(0, s.getGestion().total(), "total");
            lanza(IllegalArgumentException.class,
                    () -> s.actualizarEstudiante(9, "Ana", "Carrera Inventada"), "actualizar a carrera inexistente");
        });
    }

    // ================================================================== M2

    private static void m2() {
        caso("M2-N1", "Normal", "Procesar tres solicitudes en orden FIFO",
                "Se atienden en el orden #1, #2, #3 y la cola queda vacía", () -> {
            SistemaSigac s = sistemaCaso();
            s.registrarSolicitud(50, "PC1", "a");
            s.registrarSolicitud(70, "PC2", "b");
            s.registrarSolicitud(20, "PC3", "c");
            igual(3, s.getAtencion().totalPendientes(), "pendientes");
            igual(1, s.atenderSolicitud().getId(), "primera");
            igual(2, s.atenderSolicitud().getId(), "segunda");
            igual(3, s.atenderSolicitud().getId(), "tercera");
            igual(0, s.getAtencion().totalPendientes(), "pendientes al final");
            igual(3, s.getAtencion().getAtendidas().tamanio(), "atendidas");
        });
        caso("M2-N2", "Normal", "Recuperar dos operaciones en orden LIFO",
                "Se deshacen primero el registro de 80 y luego el de 60", () -> {
            SistemaSigac s = sistemaCaso();
            igual(80, s.deshacer().getEstudiante().getMatricula(), "primera deshecha");
            igual(60, s.deshacer().getEstudiante().getMatricula(), "segunda deshecha");
            igual("20, 30, 40, 50, 70", s.getGestion().matriculasInorden().unir(", "), "inorden");
            igual(5, s.getAtencion().totalHistorial(), "historial restante");
        });
        caso("M2-N3", "Normal", "Deshacer una eliminación y un registro",
                "30 reaparece con el ABB válido; el estudiante registrado se elimina al deshacer", () -> {
            SistemaSigac s = sistemaCaso();
            s.eliminarEstudiante(30);
            s.deshacer();
            afirmar(s.getGestion().buscarPorMatricula(30) != null, "30 debe volver");
            afirmar(s.getGestion().getIndice().esValido(), "ABB válido");
            igual("20, 30, 40, 50, 60, 70, 80", s.getGestion().matriculasInorden().unir(", "), "inorden");
            s.registrarEstudiante(est(90));
            s.deshacer();
            afirmar(s.getGestion().buscarPorMatricula(90) == null, "90 debe desaparecer");
            afirmar(s.getOrganizacion().carreraDe(90) == null, "90 fuera del árbol académico");
        });
        caso("M2-N4", "Normal", "Deshacer una actualización de estudiante",
                "Nombre y carrera vuelven a su valor anterior en todas las estructuras", () -> {
            SistemaSigac s = sistemaCaso();
            s.actualizarEstudiante(40, "Otro Nombre", OrganizacionAcademica.CARRERA_TELECOM);
            s.deshacer();
            igual("Mateo Espín", s.getGestion().buscarPorMatricula(40).getNombre(), "nombre");
            igual(SW, s.getGestion().buscarPorMatricula(40).getCarrera(), "carrera");
            igual(SW, s.getOrganizacion().carreraDe(40), "carrera en el árbol");
        });
        caso("M2-N5", "Normal", "Deshacer la eliminación de una conexión del laboratorio",
                "La arista vuelve con su peso original", () -> {
            SistemaSigac s = sistemaCaso();
            igual(1, s.eliminarConexionLaboratorio("PC1", "SW1"), "peso devuelto");
            afirmar(!s.getLaboratorio().existeConexion("PC1", "SW1"), "arista eliminada");
            s.deshacer();
            afirmar(s.getLaboratorio().existeConexion("PC1", "SW1"), "arista restaurada");
            igual(1, s.getLaboratorio().getGrafo().pesoDe("PC1", "SW1"), "peso restaurado");
        });
        caso("M2-E1", "Extremo", "Atender con la cola vacía",
                "IllegalStateException", () -> {
            SistemaSigac s = new SistemaSigac();
            lanza(IllegalStateException.class, s::atenderSolicitud, "cola vacía");
            lanza(IllegalStateException.class, () -> s.getAtencion().verSiguiente(), "ver siguiente en cola vacía");
        });
        caso("M2-E2", "Extremo", "Deshacer con el historial vacío",
                "IllegalStateException", () -> {
            lanza(IllegalStateException.class, () -> new SistemaSigac().deshacer(), "pila vacía");
        });
        caso("M2-E3", "Extremo", "Encolar y atender 1000 solicitudes",
                "Se respeta el orden FIFO de principio a fin", () -> {
            SistemaSigac s = sistemaCaso();
            for (int i = 0; i < 1000; i++) {
                s.registrarSolicitud(50, "PC1", "n" + i);
            }
            for (int i = 1; i <= 1000; i++) {
                igual(i, s.atenderSolicitud().getId(), "orden FIFO en " + i);
            }
            igual(0, s.getAtencion().totalPendientes(), "pendientes");
        });
        caso("M2-X1", "Erróneo", "Solicitud de un estudiante inexistente",
                "IllegalArgumentException y la cola no cambia", () -> {
            SistemaSigac s = sistemaCaso();
            lanza(IllegalArgumentException.class, () -> s.registrarSolicitud(999, "PC1", "x"), "estudiante inexistente");
            igual(0, s.getAtencion().totalPendientes(), "pendientes");
        });
        caso("M2-X2", "Erróneo", "Solicitud sobre un equipo inexistente, nulo o vacío",
                "IllegalArgumentException", () -> {
            SistemaSigac s = sistemaCaso();
            lanza(IllegalArgumentException.class, () -> s.registrarSolicitud(50, "PC99", "x"), "equipo inexistente");
            lanza(IllegalArgumentException.class, () -> s.registrarSolicitud(50, null, "x"), "equipo nulo");
            lanza(IllegalArgumentException.class, () -> s.registrarSolicitud(50, "  ", "x"), "equipo vacío");
        });
        caso("M2-X3", "Erróneo", "Eliminar o deshacer el registro de un estudiante con solicitudes pendientes",
                "IllegalStateException; tras atender la solicitud la operación sí se puede hacer", () -> {
            SistemaSigac s = sistemaCaso();
            s.registrarSolicitud(80, "PC1", "x");
            lanza(IllegalStateException.class, () -> s.eliminarEstudiante(80), "eliminar con pendientes");
            lanza(IllegalStateException.class, s::deshacer, "deshacer registro con pendientes");
            igual(7, s.getAtencion().totalHistorial(), "la operación se conserva en el historial");
            s.atenderSolicitud();
            s.eliminarEstudiante(80);
            afirmar(s.getGestion().buscarPorMatricula(80) == null, "ahora sí se elimina");
        });
    }

    // ================================================================== M3

    private static void m3() {
        caso("M3-N1", "Normal", "Jerarquía Universidad -> Facultad -> Carrera -> Estudiantes con el caso cargado",
                "Los 7 estudiantes cuelgan de Ingeniería en Software; el árbol tiene 4 niveles", () -> {
            OrganizacionAcademica o = sistemaCaso().getOrganizacion();
            igual(SW, o.carreraDe(50), "carrera de 50");
            igual(7, o.estudiantesDe(SW).tamanio(), "estudiantes de Software");
            igual(4, o.getArbol().altura(), "altura");
            igual("UTA, FISEI", o.getArbol().preorden().obtener(0) + ", " + o.getArbol().preorden().obtener(1), "preorden");
            igual(13, o.getArbol().tamanio(), "tamaño: raíz + facultad + 4 carreras + 7 estudiantes");
        });
        caso("M3-N2", "Normal", "Recorridos del ABB del caso (50, 30, 70, 20, 40, 60, 80)",
                "Inorden 20..80; preorden 50,30,20,40,70,60,80; posorden 20,40,30,60,80,70,50", () -> {
            GestionEstudiantes g = sistemaCaso().getGestion();
            igual("20, 30, 40, 50, 60, 70, 80", g.matriculasInorden().unir(", "), "inorden");
            igual("50, 30, 20, 40, 70, 60, 80", g.matriculasPreorden().unir(", "), "preorden");
            igual("20, 40, 30, 60, 80, 70, 50", g.matriculasPosorden().unir(", "), "posorden");
        });
        caso("M3-N3", "Normal", "Eliminar del ABB un nodo con dos hijos (30), con un hijo y una hoja",
                "Se conserva la propiedad del ABB; 30 es reemplazado por su sucesor inorden (40)", () -> {
            SistemaSigac s = sistemaCaso();
            s.eliminarEstudiante(30);
            GestionEstudiantes g = s.getGestion();
            igual("20, 40, 50, 60, 70, 80", g.matriculasInorden().unir(", "), "inorden tras eliminar 30");
            igual("50, 40, 20, 70, 60, 80", g.matriculasPreorden().unir(", "), "preorden tras eliminar 30");
            afirmar(g.getIndice().esValido(), "ABB válido");
            s.eliminarEstudiante(20); // hoja
            s.eliminarEstudiante(40); // ahora hoja
            igual("50, 60, 70, 80", g.matriculasInorden().unir(", "), "inorden final");
            afirmar(g.getIndice().esValido(), "ABB válido al final");
        });
        caso("M3-N4", "Normal", "Eliminar la raíz (50) del ABB del caso",
                "La nueva raíz es el sucesor inorden (60)", () -> {
            SistemaSigac s = sistemaCaso();
            s.eliminarEstudiante(50);
            igual("60, 30, 20, 40, 70, 80", s.getGestion().matriculasPreorden().unir(", "), "preorden");
            igual("20, 30, 40, 60, 70, 80", s.getGestion().matriculasInorden().unir(", "), "inorden");
            afirmar(s.getGestion().getIndice().esValido(), "ABB válido");
        });
        caso("M3-N5", "Normal", "Árbol general: recorridos y eliminación de un subárbol completo",
                "Se eliminan el nodo y todos sus descendientes", () -> {
            ArbolGeneral t = new ArbolGeneral();
            t.crearRaiz("R", "RAIZ", "");
            t.agregarHijo("R", "A", "X", "");
            t.agregarHijo("R", "D", "X", "");
            t.agregarHijo("A", "B", "X", "");
            t.agregarHijo("A", "C", "X", "");
            igual("R, A, B, C, D", t.preorden().unir(", "), "preorden");
            igual("B, C, A, D, R", t.posorden().unir(", "), "posorden");
            igual("R, A, D, B, C", t.porNiveles().unir(", "), "por niveles");
            igual(3, t.eliminar("A"), "nodos eliminados");
            igual(2, t.tamanio(), "tamaño final");
            afirmar(t.buscar("B") == null, "B ya no existe");
        });
        caso("M3-E1", "Extremo", "ABB vacío: recorridos, búsqueda y eliminación",
                "Resultados vacíos / null sin excepciones", () -> {
            ABB<Integer, String> abb = new ABB<>();
            afirmar(abb.preorden().estaVacio() && abb.inorden().estaVacio() && abb.posorden().estaVacio(), "recorridos");
            afirmar(abb.buscar(1) == null, "buscar");
            afirmar(abb.eliminar(1) == null, "eliminar");
            igual(0, abb.altura(), "altura");
            afirmar(abb.esValido(), "vacío es válido");
        });
        caso("M3-E2", "Extremo", "ABB degenerado (500 claves ascendentes) y eliminaciones masivas",
                "Altura 500, búsqueda del último = 500 comparaciones, el ABB sigue siendo válido", () -> {
            ABB<Integer, Integer> abb = new ABB<>();
            for (int i = 1; i <= 500; i++) {
                abb.insertar(i, i);
            }
            igual(500, abb.altura(), "altura");
            abb.buscar(500);
            igual(500, abb.getUltimasComparaciones(), "comparaciones");
            for (int i = 1; i <= 250; i++) {
                afirmar(abb.eliminar(i) != null, "eliminar " + i);
            }
            igual(250, abb.tamanio(), "tamaño");
            afirmar(abb.esValido(), "válido");
        });
        caso("M3-X1", "Erróneo", "ABB: clave duplicada y claves/valores nulos",
                "insertar devuelve false para duplicados; los nulos lanzan IllegalArgumentException", () -> {
            ABB<Integer, String> abb = new ABB<>();
            afirmar(abb.insertar(1, "a"), "primera inserción");
            afirmar(!abb.insertar(1, "b"), "duplicado debe devolver false");
            igual("a", abb.buscar(1), "el valor original se conserva");
            igual(1, abb.tamanio(), "tamaño");
            lanza(IllegalArgumentException.class, () -> abb.insertar(null, "x"), "clave nula");
            lanza(IllegalArgumentException.class, () -> abb.insertar(2, null), "valor nulo");
            lanza(IllegalArgumentException.class, () -> abb.buscar(null), "buscar nulo");
        });
        caso("M3-X2", "Erróneo", "Árbol general: padre inexistente, id duplicado, eliminar raíz, árbol vacío",
                "IllegalArgumentException / IllegalStateException según el caso", () -> {
            ArbolGeneral t = new ArbolGeneral();
            lanza(IllegalStateException.class, () -> t.agregarHijo("R", "A", "X", ""), "árbol sin raíz");
            lanza(IllegalStateException.class, () -> t.eliminar("A"), "eliminar en árbol vacío");
            t.crearRaiz("R", "RAIZ", "");
            lanza(IllegalStateException.class, () -> t.crearRaiz("R2", "RAIZ", ""), "segunda raíz");
            lanza(IllegalArgumentException.class, () -> t.agregarHijo("NO", "A", "X", ""), "padre inexistente");
            t.agregarHijo("R", "A", "X", "");
            lanza(IllegalArgumentException.class, () -> t.agregarHijo("R", "A", "X", ""), "id duplicado");
            lanza(IllegalArgumentException.class, () -> t.agregarHijo("R", " ", "X", ""), "id vacío");
            lanza(IllegalArgumentException.class, () -> t.eliminar("R"), "eliminar raíz");
            lanza(IllegalArgumentException.class, () -> t.eliminar("ZZ"), "eliminar inexistente");
        });
        caso("M3-X3", "Erróneo", "Organización académica: facultad inexistente y carrera duplicada",
                "IllegalArgumentException", () -> {
            OrganizacionAcademica o = new OrganizacionAcademica();
            lanza(IllegalArgumentException.class, () -> o.agregarCarrera("NoExiste", "Nueva"), "facultad inexistente");
            lanza(IllegalArgumentException.class, () -> o.agregarCarrera("FISEI", SW), "carrera duplicada");
            lanza(IllegalArgumentException.class, () -> o.estudiantesDe("NoExiste"), "carrera inexistente");
            o.agregarFacultad("FCHE");
            o.agregarCarrera("FCHE", "Nueva Carrera");
            afirmar(o.existeCarrera("Nueva Carrera"), "carrera nueva");
        });
    }

    // ================================================================== M4

    private static void m4() {
        caso("M4-N1", "Normal", "BFS y DFS desde PC1 en la red del caso",
                "BFS: PC1, SW1, PC3, SRV1, PC2 — DFS: PC1, SW1, SRV1, PC2, PC3", () -> {
            RedLaboratorio lab = sistemaCaso().getLaboratorio();
            igual("PC1, SW1, PC3, SRV1, PC2", lab.bfs("PC1").unir(", "), "BFS");
            igual("PC1, SW1, SRV1, PC2, PC3", lab.dfs("PC1").unir(", "), "DFS");
            igual(6, lab.totalEquipos(), "equipos");
            igual(4, lab.totalConexiones(), "conexiones");
        });
        caso("M4-N2", "Normal", "Detectar equipos no alcanzables desde PC1",
                "Solo PC4", () -> {
            igual("PC4", sistemaCaso().getLaboratorio().noAlcanzables("PC1").unir(", "), "no alcanzables");
        });
        caso("M4-N3", "Normal", "Eliminar la arista SW1 -> SRV1 y recalcular la conectividad",
                "BFS: PC1, SW1, PC3; no alcanzables: SRV1, PC2, PC4", () -> {
            RedLaboratorio lab = sistemaCaso().getLaboratorio();
            igual(2, lab.eliminarConexion("SW1", "SRV1"), "peso de la arista eliminada");
            igual("PC1, SW1, PC3", lab.bfs("PC1").unir(", "), "BFS");
            igual("SRV1, PC2, PC4", lab.noAlcanzables("PC1").unir(", "), "no alcanzables");
            igual(3, lab.totalConexiones(), "conexiones");
        });
        caso("M4-N4", "Normal", "Eliminar el vértice SRV1 (aristas entrantes y salientes)",
                "Desaparecen SW1->SRV1 y SRV1->PC2; quedan 2 aristas", () -> {
            RedLaboratorio lab = sistemaCaso().getLaboratorio();
            igual(2, lab.eliminarEquipo("SRV1"), "aristas perdidas");
            afirmar(!lab.existeEquipo("SRV1"), "SRV1 eliminado");
            igual(2, lab.totalConexiones(), "conexiones restantes");
            afirmar(lab.getGrafo().vecinos("SW1").estaVacio(), "SW1 ya no apunta a SRV1");
            igual("PC1, SW1, PC3", lab.bfs("PC1").unir(", "), "BFS tras eliminar");
            igual("PC2, PC4", lab.noAlcanzables("PC1").unir(", "), "no alcanzables tras eliminar");
        });
        caso("M4-N5", "Normal", "Grafo no dirigido: la arista existe en ambos sentidos y se elimina en ambos",
                "A-B visible desde A y desde B; eliminarla la quita de los dos lados", () -> {
            Grafo g = new Grafo(false);
            g.agregarVertice("A");
            g.agregarVertice("B");
            g.agregarArista("A", "B", 5);
            afirmar(g.existeArista("A", "B") && g.existeArista("B", "A"), "ambos sentidos");
            igual(1, g.numAristas(), "una arista lógica");
            g.eliminarArista("B", "A");
            afirmar(!g.existeArista("A", "B") && !g.existeArista("B", "A"), "eliminada en ambos sentidos");
            igual(0, g.numAristas(), "sin aristas");
        });
        caso("M4-E1", "Extremo", "Vértice aislado y grafo de un solo vértice",
                "BFS/DFS devuelven solo el vértice; sin no alcanzables", () -> {
            Grafo g = new Grafo(true);
            g.agregarVertice("SOLO");
            igual("SOLO", g.bfs("SOLO").unir(", "), "BFS");
            igual("SOLO", g.dfs("SOLO").unir(", "), "DFS");
            afirmar(g.noAlcanzables("SOLO").estaVacio(), "no alcanzables");
            igual("PC4", grafoDelCaso().bfs("PC4").unir(", "), "BFS desde el aislado PC4");
        });
        caso("M4-E2", "Extremo", "Grafo con ciclo (A->B->C->A) y cadena de 5000 vértices",
                "Cada vértice se visita una sola vez y no hay desbordamiento de pila", () -> {
            Grafo c = new Grafo(true);
            c.agregarVertice("A");
            c.agregarVertice("B");
            c.agregarVertice("C");
            c.agregarArista("A", "B", 1);
            c.agregarArista("B", "C", 1);
            c.agregarArista("C", "A", 1);
            igual("A, B, C", c.bfs("A").unir(", "), "BFS con ciclo");
            igual("A, B, C", c.dfs("A").unir(", "), "DFS con ciclo");
            Grafo largo = new Grafo(true);
            for (int i = 0; i < 5000; i++) {
                largo.agregarVertice("N" + i);
            }
            for (int i = 0; i < 4999; i++) {
                largo.agregarArista("N" + i, "N" + (i + 1), 1);
            }
            igual(5000, largo.bfs("N0").tamanio(), "BFS cadena larga");
            igual(5000, largo.dfs("N0").tamanio(), "DFS cadena larga");
        });
        caso("M4-X1", "Erróneo", "Operaciones con vértices inexistentes, aristas duplicadas, lazos y pesos inválidos",
                "IllegalArgumentException (o false para duplicados) sin alterar el grafo", () -> {
            Grafo g = grafoDelCaso();
            lanza(IllegalArgumentException.class, () -> g.bfs("NOEXISTE"), "BFS inexistente");
            lanza(IllegalArgumentException.class, () -> g.dfs(null), "DFS nulo");
            lanza(IllegalArgumentException.class, () -> g.agregarArista("PC1", "NOEXISTE", 1), "destino inexistente");
            lanza(IllegalArgumentException.class, () -> g.agregarArista("PC1", "PC1", 1), "lazo");
            lanza(IllegalArgumentException.class, () -> g.agregarArista("PC1", "PC4", 0), "peso cero");
            lanza(IllegalArgumentException.class, () -> g.agregarArista("PC1", "PC4", -3), "peso negativo");
            lanza(IllegalArgumentException.class, () -> g.eliminarArista("PC2", "PC1"), "arista inexistente");
            lanza(IllegalArgumentException.class, () -> g.eliminarVertice("NOEXISTE"), "vértice inexistente");
            lanza(IllegalArgumentException.class, () -> g.agregarVertice("  "), "nombre vacío");
            afirmar(!g.agregarArista("PC1", "SW1", 9), "arista duplicada devuelve false");
            afirmar(!g.agregarVertice("PC1"), "vértice duplicado devuelve false");
            igual(4, g.numAristas(), "aristas intactas");
            igual(6, g.numVertices(), "vértices intactos");
        });
        caso("M4-X2", "Erróneo", "Eliminar un equipo con solicitudes pendientes",
                "IllegalStateException; tras atenderlas sí se puede eliminar", () -> {
            SistemaSigac s = sistemaCaso();
            s.registrarSolicitud(50, "PC2", "x");
            lanza(IllegalStateException.class, () -> s.eliminarEquipoLaboratorio("PC2"), "equipo con pendientes");
            afirmar(s.getLaboratorio().existeEquipo("PC2"), "PC2 sigue existiendo");
            s.atenderSolicitud();
            s.eliminarEquipoLaboratorio("PC2");
            afirmar(!s.getLaboratorio().existeEquipo("PC2"), "PC2 eliminado");
        });
    }

    // ================================================================== M5

    private static RedSocial socialDelCaso() {
        return sistemaCaso().getSocial();
    }

    private static void m5() {
        caso("M5-N1", "Normal", "Recomendar contactos a Ana en la red del caso",
                "Diego (2 en común: Beto y Carla) y Elena (1 en común: Carla), en ese orden", () -> {
            VectorDinamico<Recomendacion> r = socialDelCaso().recomendar("Ana");
            igual(2, r.tamanio(), "cantidad");
            igual("Diego", r.obtener(0).getNombre(), "primero");
            igual(2, r.obtener(0).getCantidadEnComun(), "comunes de Diego");
            igual("Beto, Carla", r.obtener(0).getAmigosEnComun().unir(", "), "intermediarios de Diego");
            igual("Elena", r.obtener(1).getNombre(), "segundo");
            igual(1, r.obtener(1).getCantidadEnComun(), "comunes de Elena");
        });
        caso("M5-N2", "Normal", "Excluir al usuario y a sus amigos directos (triángulo Ana-Beto-Carla)",
                "Ana no recibe recomendaciones", () -> {
            RedSocial red = new RedSocial();
            red.agregarUsuario("Ana");
            red.agregarUsuario("Beto");
            red.agregarUsuario("Carla");
            red.agregarAmistad("Ana", "Beto");
            red.agregarAmistad("Ana", "Carla");
            red.agregarAmistad("Beto", "Carla");
            afirmar(red.recomendar("Ana").estaVacio(), "no debe recomendar a amigos directos ni a sí misma");
        });
        caso("M5-N3", "Normal", "Empate de amigos en común",
                "Se ordena alfabéticamente (Diego antes que Zoe)", () -> {
            RedSocial red = new RedSocial();
            for (String u : new String[] {"Ana", "Beto", "Carla", "Zoe", "Diego"}) {
                red.agregarUsuario(u);
            }
            red.agregarAmistad("Ana", "Beto");
            red.agregarAmistad("Ana", "Carla");
            red.agregarAmistad("Beto", "Zoe");
            red.agregarAmistad("Carla", "Diego");
            VectorDinamico<Recomendacion> r = red.recomendar("Ana");
            igual("Diego, Zoe", r.obtener(0).getNombre() + ", " + r.obtener(1).getNombre(), "orden del empate");
        });
        caso("M5-N4", "Normal", "Eliminar la amistad Carla-Diego y recalcular",
                "Diego baja a 1 en común y queda empatado con Elena (orden alfabético)", () -> {
            RedSocial red = socialDelCaso();
            red.eliminarAmistad("Carla", "Diego");
            VectorDinamico<Recomendacion> r = red.recomendar("Ana");
            igual("Diego, Elena", r.obtener(0).getNombre() + ", " + r.obtener(1).getNombre(), "orden");
            igual(1, r.obtener(0).getCantidadEnComun(), "comunes de Diego");
        });
        caso("M5-E1", "Extremo", "Usuario sin amigos y usuario cuyo único amigo no tiene más contactos",
                "Lista de recomendaciones vacía", () -> {
            RedSocial red = new RedSocial();
            red.agregarUsuario("Solo");
            red.agregarUsuario("A");
            red.agregarUsuario("B");
            red.agregarAmistad("A", "B");
            afirmar(red.recomendar("Solo").estaVacio(), "sin amigos");
            afirmar(red.recomendar("A").estaVacio(), "único amigo sin otros contactos");
        });
        caso("M5-E2", "Extremo", "Red grande (200 usuarios en estrella)",
                "Cada hoja recibe 198 recomendaciones con 1 amigo en común (el centro)", () -> {
            RedSocial red = new RedSocial();
            red.agregarUsuario("Centro");
            for (int i = 0; i < 199; i++) {
                red.agregarUsuario("U" + i);
                red.agregarAmistad("Centro", "U" + i);
            }
            VectorDinamico<Recomendacion> r = red.recomendar("U0");
            igual(198, r.tamanio(), "cantidad");
            igual(1, r.obtener(0).getCantidadEnComun(), "comunes");
        });
        caso("M5-X1", "Erróneo", "Usuario inexistente, amistad consigo mismo, nulos y duplicados",
                "IllegalArgumentException (false para duplicados)", () -> {
            RedSocial red = socialDelCaso();
            lanza(IllegalArgumentException.class, () -> red.recomendar("Zed"), "usuario inexistente");
            lanza(IllegalArgumentException.class, () -> red.recomendar(null), "usuario nulo");
            lanza(IllegalArgumentException.class, () -> red.agregarAmistad("Ana", "Ana"), "autoamistad");
            lanza(IllegalArgumentException.class, () -> red.agregarAmistad("Ana", "Zed"), "amigo inexistente");
            lanza(IllegalArgumentException.class, () -> red.eliminarAmistad("Ana", "Diego"), "amistad inexistente");
            lanza(IllegalArgumentException.class, () -> red.agregarUsuario(" "), "usuario vacío");
            afirmar(!red.agregarAmistad("Ana", "Beto"), "amistad duplicada");
            afirmar(!red.agregarUsuario("Ana"), "usuario duplicado");
        });
    }

    // ================================================================== M6

    private static void m6() {
        caso("M6-N1", "Normal", "Ejecutar el caso integrador completo de principio a fin",
                "La salida contiene todos los resultados exigidos y el estado final es coherente", () -> {
            SistemaSigac s = new SistemaSigac();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(buffer, true, "UTF-8");
            CasoIntegrador.ejecutar(s, out);
            String salida = buffer.toString("UTF-8");
            afirmar(salida.contains("Inorden : 20, 30, 40, 50, 60, 70, 80"), "inorden inicial");
            afirmar(salida.contains("Inorden : 20, 40, 50, 60, 70, 80"), "inorden tras eliminar 30");
            afirmar(salida.contains("BFS desde PC1 : PC1 -> SW1 -> PC3 -> SRV1 -> PC2"), "BFS");
            afirmar(salida.contains("No alcanzables: PC4"), "PC4 no alcanzable");
            afirmar(salida.contains("Diego — 2 amigo(s) en común"), "Diego con 2 en común");
            afirmar(salida.contains("Elena — 1 amigo(s) en común"), "Elena con 1 en común");
            afirmar(salida.contains("No alcanzables: SRV1, PC2, PC4"), "conectividad tras eliminar arista");
            // Estado final: se deshicieron "eliminar 30" y "registrar 80"
            igual("20, 30, 40, 50, 60, 70", s.getGestion().matriculasInorden().unir(", "), "inorden final");
            afirmar(!s.getLaboratorio().existeConexion("SW1", "SRV1"), "arista eliminada");
            igual(3, s.getAtencion().getAtendidas().tamanio(), "solicitudes atendidas");
        });
        caso("M6-E1", "Extremo", "Usar todas las consultas del sistema recién creado (todo vacío)",
                "Ninguna consulta lanza excepciones; las operaciones sobre vacío fallan con mensajes claros", () -> {
            SistemaSigac s = new SistemaSigac();
            afirmar(s.getGestion().listar().estaVacio(), "listar");
            afirmar(s.getGestion().matriculasInorden().estaVacio(), "inorden");
            afirmar(s.getLaboratorio().descripcion().contains("vacío"), "red de laboratorio vacía");
            afirmar(s.getSocial().descripcion().contains("vacío"), "red social vacía");
            afirmar(s.getOrganizacion().imprimir().contains("FISEI"), "jerarquía inicial");
            afirmar(s.getGestion().getIndice().dibujar().contains("vacío"), "dibujo de ABB vacío");
            lanza(IllegalStateException.class, s::atenderSolicitud, "atender vacío");
            lanza(IllegalStateException.class, s::deshacer, "deshacer vacío");
        });
        caso("M6-X1", "Erróneo", "Entradas nulas o en blanco en los servicios",
                "IllegalArgumentException en todos los casos", () -> {
            SistemaSigac s = new SistemaSigac();
            lanza(IllegalArgumentException.class, () -> s.getLaboratorio().agregarEquipo(null), "equipo nulo");
            lanza(IllegalArgumentException.class, () -> s.getLaboratorio().agregarConexion(null, "A", 1), "origen nulo");
            lanza(IllegalArgumentException.class, () -> s.getSocial().agregarUsuario(""), "usuario vacío");
            lanza(IllegalArgumentException.class, () -> s.getOrganizacion().agregarEstudiante(null), "estudiante nulo en árbol");
            lanza(IllegalArgumentException.class, () -> s.getAtencion().registrarOperacion(null), "operación nula");
            lanza(IllegalArgumentException.class, () -> new SolicitudLaboratorio(1, 1, "", "x"), "solicitud sin equipo");
            lanza(IllegalArgumentException.class, () -> s.eliminarConexionLaboratorio("A", "B"), "conexión entre equipos inexistentes");
            igual(0, s.getAtencion().totalHistorial(), "ninguna operación fallida se apila");
        });
        caso("M6-X2", "Erróneo", "Deshacer una conexión cuando uno de sus equipos ya fue eliminado",
                "IllegalArgumentException y la operación conflictiva se descarta del historial", () -> {
            SistemaSigac s = sistemaCaso();
            s.eliminarConexionLaboratorio("PC1", "SW1");
            s.eliminarEquipoLaboratorio("SW1");
            int antes = s.getAtencion().totalHistorial();
            lanza(IllegalArgumentException.class, s::deshacer, "reversión en conflicto");
            igual(antes - 1, s.getAtencion().totalHistorial(), "la operación se descarta");
            igual(80, s.deshacer().getEstudiante().getMatricula(), "el historial sigue funcionando");
        });
    }

    // ================================================================== ejecución

    private static void registrarCasos() {
        CASOS.clear();
        m1();
        m2();
        m3();
        m4();
        m5();
        m6();
    }

    /** Ejecuta todas las pruebas. Devuelve la cantidad de fallos. */
    public static int ejecutarTodas(PrintStream out, boolean markdown) {
        registrarCasos();
        int fallos = 0;
        for (Caso c : CASOS) {
            try {
                c.cuerpo.ejecutar();
                c.aprobado = true;
            } catch (Throwable t) {
                c.aprobado = false;
                c.detalleError = t.getClass().getSimpleName() + ": " + t.getMessage();
                fallos++;
            }
        }
        if (markdown) {
            out.println("| ID | Tipo | Prueba | Resultado esperado | Estado |");
            out.println("| --- | --- | --- | --- | --- |");
            for (Caso c : CASOS) {
                out.println("| " + c.id + " | " + c.tipo + " | " + c.descripcion + " | " + c.esperado + " | "
                        + (c.aprobado ? "✅ Aprobada" : "❌ Falló") + " |");
            }
        } else {
            out.println();
            out.println("================ PRUEBAS AUTOMÁTICAS SIGAC-UTA ================");
            for (Caso c : CASOS) {
                out.println((c.aprobado ? "[ OK ] " : "[FALLA] ") + c.id + " (" + c.tipo + ") " + c.descripcion);
                if (!c.aprobado) {
                    out.println("         -> " + c.detalleError);
                }
            }
            out.println("----------------------------------------------------------------");
            out.println("Total: " + CASOS.size() + " | Aprobadas: " + (CASOS.size() - fallos) + " | Fallidas: " + fallos);
        }
        return fallos;
    }

    public static void main(String[] args) throws Exception {
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        boolean markdown = args.length > 0 && "--md".equals(args[0]);
        int fallos = ejecutarTodas(out, markdown);
        System.exit(fallos == 0 ? 0 : 1);
    }
}
