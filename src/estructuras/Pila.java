package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Pila LIFO enlazada (implementación manual). Todas las operaciones son O(1). */
public class Pila<T> implements Iterable<T> {

    private static class Nodo<T> {
        final T dato;
        final Nodo<T> debajo;

        Nodo(T dato, Nodo<T> debajo) {
            this.dato = dato;
            this.debajo = debajo;
        }
    }

    private Nodo<T> cima;
    private int tamanio;

    public void push(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se permiten elementos nulos.");
        }
        cima = new Nodo<>(dato, cima);
        tamanio++;
    }

    public T pop() {
        if (cima == null) {
            throw new IllegalStateException("La pila está vacía.");
        }
        T dato = cima.dato;
        cima = cima.debajo;
        tamanio--;
        return dato;
    }

    public T peek() {
        if (cima == null) {
            throw new IllegalStateException("La pila está vacía.");
        }
        return cima.dato;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int tamanio() {
        return tamanio;
    }

    /** Recorre desde la cima hacia el fondo (del más reciente al más antiguo). */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cima;

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
                actual = actual.debajo;
                return dato;
            }
        };
    }
}
