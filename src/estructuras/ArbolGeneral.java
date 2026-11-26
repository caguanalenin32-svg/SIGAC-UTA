package estructuras;

/**
 * Árbol general (n-ario): cada nodo tiene un número arbitrario de hijos guardados en una
 * lista enlazada. Se usa para Universidad -> Facultad -> Carrera -> Estudiantes.
 * Los identificadores de nodo son únicos en todo el árbol.
 */
public class ArbolGeneral {

    public static class Nodo {
        private final String id;
        private final String tipo;
        private final String descripcion;
        private Nodo padre;
        private final ListaEnlazada<Nodo> hijos = new ListaEnlazada<>();

        private Nodo(String id, String tipo, String descripcion) {
            this.id = id;
            this.tipo = tipo;
            this.descripcion = descripcion;
        }

        public String getId() {
            return id;
        }

        public String getTipo() {
            return tipo;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public Nodo getPadre() {
            return padre;
        }

        public ListaEnlazada<Nodo> getHijos() {
            return hijos;
        }
    }

    private Nodo raiz;
    private int tamanio;

    public void crearRaiz(String id, String tipo, String descripcion) {
        if (raiz != null) {
            throw new IllegalStateException("El árbol ya tiene raíz.");
        }
        validarTexto(id, "El id");
        validarTexto(tipo, "El tipo");
        raiz = new Nodo(id.trim(), tipo.trim(), descripcion == null ? "" : descripcion.trim());
        tamanio = 1;
    }

    /** Agrega un hijo al final de la lista de hijos del padre. O(n) por la búsqueda del padre y del id. */
    public void agregarHijo(String idPadre, String id, String tipo, String descripcion) {
        if (raiz == null) {
            throw new IllegalStateException("El árbol está vacío: cree primero la raíz.");
        }
        validarTexto(idPadre, "El id del padre");
        validarTexto(id, "El id");
        validarTexto(tipo, "El tipo");
        Nodo padre = buscar(idPadre);
        if (padre == null) {
            throw new IllegalArgumentException("No existe el nodo padre: " + idPadre);
        }
        if (buscar(id) != null) {
            throw new IllegalArgumentException("Ya existe un nodo con id: " + id);
        }
        Nodo nuevo = new Nodo(id.trim(), tipo.trim(), descripcion == null ? "" : descripcion.trim());
        nuevo.padre = padre;
        padre.hijos.agregarAlFinal(nuevo);
        tamanio++;
    }

    /** Búsqueda por niveles usando una cola. O(n). Devuelve null si no existe. */
    public Nodo buscar(String id) {
        if (raiz == null || id == null) {
            return null;
        }
        String buscado = id.trim();
        Cola<Nodo> cola = new Cola<>();
        cola.encolar(raiz);
        while (!cola.estaVacia()) {
            Nodo actual = cola.desencolar();
            if (actual.id.equals(buscado)) {
                return actual;
            }
            for (Nodo hijo : actual.hijos) {
                cola.encolar(hijo);
            }
        }
        return null;
    }

    /** Elimina el nodo y todo su subárbol. Devuelve cuántos nodos se eliminaron. */
    public int eliminar(String id) {
        if (raiz == null) {
            throw new IllegalStateException("El árbol está vacío.");
        }
        validarTexto(id, "El id");
        final Nodo nodo = buscar(id);
        if (nodo == null) {
            throw new IllegalArgumentException("No existe el nodo: " + id);
        }
        if (nodo == raiz) {
            throw new IllegalArgumentException("No se puede eliminar la raíz.");
        }
        nodo.padre.hijos.eliminarPrimero(h -> h == nodo);
        nodo.padre = null;
        int eliminados = contar(nodo);
        tamanio -= eliminados;
        return eliminados;
    }

    private int contar(Nodo nodo) {
        int total = 1;
        for (Nodo hijo : nodo.hijos) {
            total += contar(hijo);
        }
        return total;
    }

    public VectorDinamico<String> preorden() {
        VectorDinamico<String> res = new VectorDinamico<>();
        preorden(raiz, res);
        return res;
    }

    private void preorden(Nodo n, VectorDinamico<String> res) {
        if (n == null) {
            return;
        }
        res.agregar(n.id);
        for (Nodo h : n.hijos) {
            preorden(h, res);
        }
    }

    public VectorDinamico<String> posorden() {
        VectorDinamico<String> res = new VectorDinamico<>();
        posorden(raiz, res);
        return res;
    }

    private void posorden(Nodo n, VectorDinamico<String> res) {
        if (n == null) {
            return;
        }
        for (Nodo h : n.hijos) {
            posorden(h, res);
        }
        res.agregar(n.id);
    }

    public VectorDinamico<String> porNiveles() {
        VectorDinamico<String> res = new VectorDinamico<>();
        if (raiz == null) {
            return res;
        }
        Cola<Nodo> cola = new Cola<>();
        cola.encolar(raiz);
        while (!cola.estaVacia()) {
            Nodo actual = cola.desencolar();
            res.agregar(actual.id);
            for (Nodo h : actual.hijos) {
                cola.encolar(h);
            }
        }
        return res;
    }

    /** Número de niveles (vacío = 0). */
    public int altura() {
        return altura(raiz);
    }

    private int altura(Nodo n) {
        if (n == null) {
            return 0;
        }
        int max = 0;
        for (Nodo h : n.hijos) {
            max = Math.max(max, altura(h));
        }
        return 1 + max;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public Nodo getRaiz() {
        return raiz;
    }

    /** Representación indentada del árbol completo. */
    public String imprimir() {
        if (raiz == null) {
            return "(árbol vacío)";
        }
        StringBuilder sb = new StringBuilder();
        imprimir(raiz, 0, sb);
        return sb.toString();
    }

    private void imprimir(Nodo n, int nivel, StringBuilder sb) {
        for (int i = 0; i < nivel; i++) {
            sb.append("    ");
        }
        sb.append(nivel == 0 ? "" : "└─ ").append('[').append(n.tipo).append("] ").append(n.id);
        if (!n.descripcion.isEmpty() && !n.descripcion.equals(n.id)) {
            sb.append(" — ").append(n.descripcion);
        }
        sb.append('\n');
        for (Nodo h : n.hijos) {
            imprimir(h, nivel + 1, sb);
        }
    }

    private void validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede ser nulo ni vacío.");
        }
    }
}
