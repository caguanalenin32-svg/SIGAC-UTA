package estructuras;

/**
 * Árbol binario de búsqueda (clave -> valor) implementado manualmente.
 *
 * Política de eliminación (documentada para reproducibilidad):
 *  - Hoja: se elimina.
 *  - Un hijo: el hijo ocupa su lugar.
 *  - Dos hijos: se reemplaza por el SUCESOR INORDEN (mínimo del subárbol derecho),
 *    y luego se elimina ese nodo sucesor.
 * No se permiten claves duplicadas.
 */
public class ABB<K extends Comparable<K>, V> {

    private class Nodo {
        K clave;
        V valor;
        Nodo izquierdo;
        Nodo derecho;

        Nodo(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }

    private Nodo raiz;
    private int tamanio;
    private int ultimasComparaciones;
    private V valorRemovido;

    /** Inserta. Promedio O(log n), peor caso O(n). Devuelve false si la clave ya existe. */
    public boolean insertar(K clave, V valor) {
        validarClave(clave);
        if (valor == null) {
            throw new IllegalArgumentException("El valor no puede ser nulo.");
        }
        Nodo nuevo = new Nodo(clave, valor);
        if (raiz == null) {
            raiz = nuevo;
            tamanio++;
            return true;
        }
        Nodo actual = raiz;
        while (true) {
            int cmp = clave.compareTo(actual.clave);
            if (cmp == 0) {
                return false;
            }
            if (cmp < 0) {
                if (actual.izquierdo == null) {
                    actual.izquierdo = nuevo;
                    break;
                }
                actual = actual.izquierdo;
            } else {
                if (actual.derecho == null) {
                    actual.derecho = nuevo;
                    break;
                }
                actual = actual.derecho;
            }
        }
        tamanio++;
        return true;
    }

    /** Busca por clave. Promedio O(log n), peor caso O(n). Devuelve null si no existe. */
    public V buscar(K clave) {
        validarClave(clave);
        ultimasComparaciones = 0;
        Nodo actual = raiz;
        while (actual != null) {
            ultimasComparaciones++;
            int cmp = clave.compareTo(actual.clave);
            if (cmp == 0) {
                return actual.valor;
            }
            actual = (cmp < 0) ? actual.izquierdo : actual.derecho;
        }
        return null;
    }

    /** Elimina por clave. Devuelve el valor eliminado o null si no existía. */
    public V eliminar(K clave) {
        validarClave(clave);
        valorRemovido = null;
        raiz = eliminar(raiz, clave);
        V resultado = valorRemovido;
        valorRemovido = null;
        if (resultado != null) {
            tamanio--;
        }
        return resultado;
    }

    private Nodo eliminar(Nodo nodo, K clave) {
        if (nodo == null) {
            return null;
        }
        int cmp = clave.compareTo(nodo.clave);
        if (cmp < 0) {
            nodo.izquierdo = eliminar(nodo.izquierdo, clave);
        } else if (cmp > 0) {
            nodo.derecho = eliminar(nodo.derecho, clave);
        } else {
            valorRemovido = nodo.valor;
            if (nodo.izquierdo == null) {
                return nodo.derecho;
            }
            if (nodo.derecho == null) {
                return nodo.izquierdo;
            }
            Nodo sucesor = minimo(nodo.derecho);
            nodo.clave = sucesor.clave;
            nodo.valor = sucesor.valor;
            nodo.derecho = eliminarMinimo(nodo.derecho);
        }
        return nodo;
    }

    private Nodo minimo(Nodo nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo;
    }

    private Nodo eliminarMinimo(Nodo nodo) {
        if (nodo.izquierdo == null) {
            return nodo.derecho;
        }
        nodo.izquierdo = eliminarMinimo(nodo.izquierdo);
        return nodo;
    }

    // ---------------------------------------------------------------- recorridos (O(n))

    public VectorDinamico<K> preorden() {
        VectorDinamico<K> res = new VectorDinamico<>();
        preorden(raiz, res);
        return res;
    }

    public VectorDinamico<K> inorden() {
        VectorDinamico<K> res = new VectorDinamico<>();
        inorden(raiz, res);
        return res;
    }

    public VectorDinamico<K> posorden() {
        VectorDinamico<K> res = new VectorDinamico<>();
        posorden(raiz, res);
        return res;
    }

    private void preorden(Nodo n, VectorDinamico<K> res) {
        if (n == null) {
            return;
        }
        res.agregar(n.clave);
        preorden(n.izquierdo, res);
        preorden(n.derecho, res);
    }

    private void inorden(Nodo n, VectorDinamico<K> res) {
        if (n == null) {
            return;
        }
        inorden(n.izquierdo, res);
        res.agregar(n.clave);
        inorden(n.derecho, res);
    }

    private void posorden(Nodo n, VectorDinamico<K> res) {
        if (n == null) {
            return;
        }
        posorden(n.izquierdo, res);
        posorden(n.derecho, res);
        res.agregar(n.clave);
    }

    // ---------------------------------------------------------------- utilidades

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    /** Número de niveles (vacío = 0, un solo nodo = 1). */
    public int altura() {
        return altura(raiz);
    }

    private int altura(Nodo n) {
        if (n == null) {
            return 0;
        }
        return 1 + Math.max(altura(n.izquierdo), altura(n.derecho));
    }

    /** Comparaciones realizadas por la última llamada a buscar (para comparar con búsqueda secuencial). */
    public int getUltimasComparaciones() {
        return ultimasComparaciones;
    }

    /** Verifica la propiedad del ABB (izquierda < nodo < derecha) en todo el árbol. */
    public boolean esValido() {
        return validar(raiz, null, null);
    }

    private boolean validar(Nodo n, K min, K max) {
        if (n == null) {
            return true;
        }
        if (min != null && n.clave.compareTo(min) <= 0) {
            return false;
        }
        if (max != null && n.clave.compareTo(max) >= 0) {
            return false;
        }
        return validar(n.izquierdo, min, n.clave) && validar(n.derecho, n.clave, max);
    }

    /** Dibujo del árbol girado 90°: la raíz queda a la izquierda, el hijo derecho arriba. */
    public String dibujar() {
        if (raiz == null) {
            return "(ABB vacío)";
        }
        StringBuilder sb = new StringBuilder();
        dibujar(raiz, 0, sb);
        return sb.toString();
    }

    private void dibujar(Nodo n, int nivel, StringBuilder sb) {
        if (n == null) {
            return;
        }
        dibujar(n.derecho, nivel + 1, sb);
        for (int i = 0; i < nivel; i++) {
            sb.append("      ");
        }
        sb.append(n.clave).append('\n');
        dibujar(n.izquierdo, nivel + 1, sb);
    }

    private void validarClave(K clave) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula.");
        }
    }
}
