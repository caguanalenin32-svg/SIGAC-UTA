package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Cola FIFO enlazada (implementación manual). Todas las operaciones son O(1). */
public class Cola<T> implements Iterable<T> {

    private static class Nodo<T> {
        final T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    public void encolar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se permiten elementos nulos.");
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fin == null) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        tamanio++;
    }

    public T desencolar() {
        if (frente == null) {
            throw new IllegalStateException("La cola está vacía.");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    public T frente() {
        if (frente == null) {
            throw new IllegalStateException("La cola está vacía.");
        }
        return frente.dato;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamanio() {
        return tamanio;
    }

    /** Recorre desde el frente hasta el final (orden de atención). */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = frente;

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
