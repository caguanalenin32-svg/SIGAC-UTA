package modelo;

import estructuras.VectorDinamico;

/** Contacto recomendado y los amigos en común que lo justifican. */
public class Recomendacion {

    private final String nombre;
    private final VectorDinamico<String> amigosEnComun = new VectorDinamico<>();

    public Recomendacion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadEnComun() {
        return amigosEnComun.tamanio();
    }

    public VectorDinamico<String> getAmigosEnComun() {
        return amigosEnComun;
    }

    public void agregarAmigoEnComun(String amigo) {
        amigosEnComun.agregar(amigo);
    }

    @Override
    public String toString() {
        return nombre + " — " + getCantidadEnComun() + " amigo(s) en común (" + amigosEnComun.unir(", ") + ")";
    }
}
