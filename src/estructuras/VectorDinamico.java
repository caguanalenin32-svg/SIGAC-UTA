package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Vector (arreglo) dinámico implementado manualmente.
 * Duplica su capacidad cuando se llena (costo amortizado O(1) al agregar).
 */
public class VectorDinamico<T> implements Iterable<T> {

    private static final int CAPACIDAD_INICIAL = 4;
    private Object[] datos = new Object[CAPACIDAD_INICIAL];
    private int tamanio = 0;

    /** Agrega al final. Amortizado O(1). */
    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se permiten elementos nulos.");
        }
        if (tamanio == datos.length) {
            crecer();
        }
        datos[tamanio++] = elemento;
    }

    /** Acceso por índice. O(1). */
    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        validarIndice(indice);
        return (T) datos[indice];
    }

    /** Reemplaza el elemento de una posición. O(1). */
    public void establecer(int indice, T elemento) {
        validarIndice(indice);
        if (elemento == null) {
            throw new IllegalArgumentException("No se permiten elementos nulos.");
        }
        datos[indice] = elemento;
    }

    /** Elimina por índice desplazando los elementos siguientes. O(n). */
    public T eliminar(int indice) {
        T eliminado = obtener(indice);
        for (int i = indice; i < tamanio - 1; i++) {
            datos[i] = datos[i + 1];
        }
        datos[--tamanio] = null;
        return eliminado;
    }

    /** Búsqueda secuencial usando equals. O(n). Devuelve -1 si no existe. */
    public int indiceDe(T elemento) {
        if (elemento == null) {
            return -1;
        }
        for (int i = 0; i < tamanio; i++) {
            if (elemento.equals(datos[i])) {
                return i;
            }
        }
        return -1;
    }

    public boolean contiene(T elemento) {
        return indiceDe(elemento) >= 0;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return tamanio == 0;
    }

    /** Une los elementos con un separador, p. ej. "20, 30, 40". */
    public String unir(String separador) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tamanio; i++) {
            if (i > 0) {
                sb.append(separador);
            }
            sb.append(datos[i]);
        }
        return sb.toString();
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice + " (tamaño " + tamanio + ")");
        }
    }

    private void crecer() {
        Object[] nuevo = new Object[datos.length * 2];
        for (int i = 0; i < tamanio; i++) {
            nuevo[i] = datos[i];
        }
        datos = nuevo;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int pos = 0;

            @Override
            public boolean hasNext() {
                return pos < tamanio;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (T) datos[pos++];
            }
        };
    }

    @Override
    public String toString() {
        return "[" + unir(", ") + "]";
    }
}
