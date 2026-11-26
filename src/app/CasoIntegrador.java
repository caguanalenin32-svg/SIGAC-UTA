package app;

import java.io.PrintStream;

import estructuras.VectorDinamico;
import modelo.Operacion;
import modelo.Recomendacion;
import modelo.SolicitudLaboratorio;
import servicios.DatosCasoIntegrador;
import servicios.SistemaSigac;

/** Ejecuta paso a paso el caso integrador obligatorio (sección 6 del examen) mostrando cada resultado. */
public final class CasoIntegrador {

    private CasoIntegrador() {
    }

    /** El sistema recibido debe estar vacío: este método carga los datos del caso y los procesa. */
    public static void ejecutar(SistemaSigac s, PrintStream out) {
        DatosCasoIntegrador.cargar(s);

        titulo(out, "0. Datos cargados");
        out.println("Estudiantes (orden de inserción): 50, 30, 70, 20, 40, 60, 80");
        out.println("Red de laboratorio (grafo dirigido, aristas en orden de inserción):");
        out.print(sangrar(s.getLaboratorio().descripcion()));
        out.println("Red social (grafo no dirigido):");
        out.print(sangrar(s.getSocial().descripcion()));
        out.println("Jerarquía académica:");
        out.print(sangrar(s.getOrganizacion().imprimir()));

        titulo(out, "1. Recorrido inorden del ABB");
        out.println("Inorden : " + s.getGestion().matriculasInorden().unir(", "));
        out.println("Preorden: " + s.getGestion().matriculasPreorden().unir(", "));
        out.println("Posorden: " + s.getGestion().matriculasPosorden().unir(", "));
        out.println("ABB (raíz a la izquierda):");
        out.print(sangrar(s.getGestion().getIndice().dibujar()));
        out.println("Comparación de búsquedas:");
        out.println(sangrar(s.getGestion().comparar(80).toString()));

        titulo(out, "2. Eliminar la matrícula 30 (nodo con dos hijos: se usa el sucesor inorden)");
        s.eliminarEstudiante(30);
        out.println("Inorden : " + s.getGestion().matriculasInorden().unir(", "));
        out.println("Preorden: " + s.getGestion().matriculasPreorden().unir(", "));
        out.println("¿Propiedad del ABB conservada? " + s.getGestion().getIndice().esValido());
        out.println("ABB (raíz a la izquierda):");
        out.print(sangrar(s.getGestion().getIndice().dibujar()));

        titulo(out, "3. Tres solicitudes FIFO");
        s.registrarSolicitud(50, "PC1", "Práctica de redes");
        s.registrarSolicitud(70, "PC2", "Proyecto de estructuras de datos");
        s.registrarSolicitud(20, "PC3", "Pruebas de conectividad");
        out.println("Cola (frente -> final):");
        for (SolicitudLaboratorio sol : s.getAtencion().getPendientes()) {
            out.println("  " + sol);
        }
        out.println("Atención (orden FIFO):");
        for (int i = 0; i < 3; i++) {
            out.println("  Atendida -> " + s.atenderSolicitud());
        }
        out.println("Solicitudes pendientes: " + s.getAtencion().totalPendientes());

        titulo(out, "4. Recuperar dos operaciones LIFO (deshacer)");
        out.println("Historial antes (más reciente primero):");
        int n = 0;
        for (Operacion op : s.getAtencion().getHistorial()) {
            if (n++ < 4) {
                out.println("  " + op);
            }
        }
        out.println("  ... (" + s.getAtencion().totalHistorial() + " operaciones en total)");
        for (int i = 1; i <= 2; i++) {
            out.println("Deshacer #" + i + " -> " + s.deshacer());
        }
        out.println("Inorden tras deshacer: " + s.getGestion().matriculasInorden().unir(", "));

        titulo(out, "5. BFS desde PC1 y equipos no alcanzables");
        out.println("BFS desde PC1 : " + s.getLaboratorio().bfs("PC1").unir(" -> "));
        out.println("DFS desde PC1 : " + s.getLaboratorio().dfs("PC1").unir(" -> "));
        out.println("No alcanzables: " + s.getLaboratorio().noAlcanzables("PC1").unir(", "));

        titulo(out, "6. Recomendaciones de contactos para Ana");
        VectorDinamico<Recomendacion> recomendaciones = s.getSocial().recomendar("Ana");
        for (Recomendacion r : recomendaciones) {
            out.println("  " + r);
        }

        titulo(out, "7. Eliminar la arista SW1 -> SRV1 y actualizar la conectividad");
        int peso = s.eliminarConexionLaboratorio("SW1", "SRV1");
        out.println("Arista eliminada (peso " + peso + "). Red actual:");
        out.print(sangrar(s.getLaboratorio().descripcion()));
        out.println("BFS desde PC1 : " + s.getLaboratorio().bfs("PC1").unir(" -> "));
        out.println("No alcanzables: " + s.getLaboratorio().noAlcanzables("PC1").unir(", "));
        out.println();
        out.println("Caso integrador finalizado.");
    }

    private static void titulo(PrintStream out, String texto) {
        out.println();
        out.println("=== " + texto + " ===");
    }

    private static String sangrar(String texto) {
        StringBuilder sb = new StringBuilder();
        for (String linea : texto.split("\n", -1)) {
            if (!linea.isEmpty()) {
                sb.append("  ").append(linea).append('\n');
            }
        }
        return sb.toString();
    }
}
