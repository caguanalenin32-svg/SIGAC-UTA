package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/** Lista simplemente enlazada con referencias a cabeza y cola (implementación manual). */
public class ListaEnlazada<T> implements Iterable<T> {

    private static class Nodo<T> {
        final T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    /** O(1) gracias a la referencia a la cola. */
    public void agregarAlFinal(T dato) {
        validar(dato);
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            cola.siguiente = nuevo;
        }
        cola = nuevo;
        tamanio++;
    }

    /** O(1). */
    public void agregarAlInicio(T dato) {
        validar(dato);
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
        if (cola == null) {
            cola = nuevo;
        }
        tamanio++;
    }

    /** Búsqueda secuencial. O(n). Devuelve null si ningún elemento cumple. */
    public T buscarPrimero(Predicate<T> criterio) {
        for (Nodo<T> actual = cabeza; actual != null; actual = actual.siguiente) {
            if (criterio.test(actual.dato)) {
                return actual.dato;
            }
        }
        return null;
    }

    /** Elimina el primer elemento que cumple el criterio y lo devuelve (null si no hubo). O(n). */
    public T eliminarPrimero(Predicate<T> criterio) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (criterio.test(actual.dato)) {
                if (anterior == null) {
                    cabeza = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                if (actual == cola) {
                    cola = anterior;
                }
                tamanio--;
                return actual.dato;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return null;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    private void validar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se permiten elementos nulos.");
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }
}
