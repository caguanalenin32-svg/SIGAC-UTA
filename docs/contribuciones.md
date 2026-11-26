# Contribuciones del equipo

> **Este archivo debe completarlo el equipo con datos reales.** La rúbrica evalúa la colaboración
> **individualmente** mediante commits sustantivos y verificables en GitHub; sin evidencia, el criterio
> vale 0. Ningún archivo puede reemplazar los commits reales de cada integrante.

## Integrantes

| Integrante | Usuario de GitHub | Módulo(s) a cargo | Archivos principales |
| --- | --- | --- | --- |
| _Nombre 1_ | _@usuario1_ | M1 – Estudiantes (vector, lista) | `estructuras/VectorDinamico`, `ListaEnlazada`, `servicios/GestionEstudiantes`, `modelo/Estudiante` |
| _Nombre 2_ | _@usuario2_ | M2 – Cola y pila | `estructuras/Cola`, `Pila`, `servicios/AtencionLaboratorio`, `modelo/Operacion` |
| _Nombre 3_ | _@usuario3_ | M3 – Árbol general y ABB | `estructuras/ABB`, `ArbolGeneral`, `servicios/OrganizacionAcademica` |
| _Nombre 4_ | _@usuario4_ | M4 y M5 – Grafos y recomendaciones | `estructuras/Grafo`, `servicios/RedLaboratorio`, `RedSocial`, `modelo/Recomendacion` |
| _Nombre 5_ | _@usuario5_ | M6 – Integración, pruebas y documentación | `servicios/SistemaSigac`, `app/*`, `pruebas/*`, `docs/*`, `README.md` |

(Ajuste la distribución al número real de integrantes.)

## Flujo de trabajo recomendado

1. Una rama por módulo o tarea: `git checkout -b feature/m3-abb`.
2. Commits pequeños y descriptivos hechos **desde la cuenta de cada integrante**
   (`git config user.name` / `user.email` con el correo asociado a su GitHub).
3. Pull Request hacia `main`, revisado por otro integrante.
4. Mensajes de commit con verbo y alcance, p. ej. `M3: elimina nodo con dos hijos usando sucesor inorden`.

## Evidencia verificable (completar al final)

Ejecute estos comandos en el repositorio y pegue o enlace el resultado:

```bash
git shortlog -sn --all                      # commits por autor
git log --author="usuario" --oneline        # commits de una persona
git log --graph --oneline --all             # ramas y fusiones
```

| Integrante | N.º de commits | Pull Requests | Enlace a commits representativos |
| --- | --- | --- | --- |
| _Nombre 1_ | | | |
| _Nombre 2_ | | | |
| _Nombre 3_ | | | |
| _Nombre 4_ | | | |
| _Nombre 5_ | | | |
