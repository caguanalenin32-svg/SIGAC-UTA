package modelo;

/** Resultado de comparar la búsqueda secuencial (vector y lista) con la búsqueda en el ABB. */
public class ComparacionBusqueda {

    private final int matricula;
    private final boolean encontrado;
    private final int comparacionesVector;
    private final int comparacionesLista;
    private final int comparacionesABB;

    public ComparacionBusqueda(int matricula, boolean encontrado, int comparacionesVector,
                               int comparacionesLista, int comparacionesABB) {
        this.matricula = matricula;
        this.encontrado = encontrado;
        this.comparacionesVector = comparacionesVector;
        this.comparacionesLista = comparacionesLista;
        this.comparacionesABB = comparacionesABB;
    }

    public int getMatricula() {
        return matricula;
    }

    public boolean isEncontrado() {
        return encontrado;
    }

    public int getComparacionesVector() {
        return comparacionesVector;
    }

    public int getComparacionesLista() {
        return comparacionesLista;
    }

    public int getComparacionesABB() {
        return comparacionesABB;
    }

    @Override
    public String toString() {
        return "Búsqueda de la matrícula " + matricula + " (" + (encontrado ? "encontrada" : "no encontrada") + ")\n"
                + "  Vector (secuencial): " + comparacionesVector + " comparaciones\n"
                + "  Lista enlazada (secuencial): " + comparacionesLista + " comparaciones\n"
                + "  ABB: " + comparacionesABB + " comparaciones";
    }
}
