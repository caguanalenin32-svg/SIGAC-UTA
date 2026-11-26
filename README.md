# SIGAC-UTA — Sistema Inteligente de Gestión Académica y Conectividad

Examen práctico integrador de **Estructura de Datos** · Ingeniería en Software · Facultad de Ingeniería en
Sistemas, Electrónica e Industrial (FISEI) · Universidad Técnica de Ambato.

Aplicación de consola en **Java** que integra vectores, listas enlazadas, pilas, colas, árbol general, árbol
binario de búsqueda (ABB) y grafos, **todo implementado manualmente** (sin `TreeMap`, `TreeSet` ni bibliotecas
de grafos). Los módulos comparten datos y funcionan como una sola aplicación.

## Requisitos

- **JDK 11 o superior** (`javac -version`). No se necesitan bibliotecas externas ni Maven/Gradle.
- Visual Studio Code con la extensión *Extension Pack for Java* (opcional).

## Cómo compilar y ejecutar

**Desde VS Code:** abra la carpeta del proyecto → *Run and Debug* → elija **SIGAC-UTA (menú)** o
**SIGAC-UTA (pruebas)** (configuración en `.vscode/launch.json`).

**Desde terminal (Linux / macOS):**

```bash
./compilar-y-ejecutar.sh            # compila y abre el menú
./compilar-y-ejecutar.sh pruebas    # compila y ejecuta las 53 pruebas
```

**Desde terminal (Windows):**

```bat
compilar-y-ejecutar.bat
compilar-y-ejecutar.bat pruebas
```

**Manualmente:**

```bash
mkdir bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")     # Windows: use el .bat
java -cp bin app.Main                 # menú
java -cp bin pruebas.PruebasSigac     # pruebas
```

> Si en Windows se ven mal las tildes, ejecute `chcp 65001` antes de `java` (el `.bat` ya lo hace).

## Uso rápido

Al abrir el menú, la opción **6** ejecuta el **caso integrador** completo y deja el sistema cargado con su
estado final para seguir explorando; la opción **7** ejecuta las pruebas automáticas.

```
1. M1 Gestión de estudiantes            5. M5 Red social y recomendaciones
2. M2 Atención e historial (FIFO/LIFO)  6. Ejecutar CASO INTEGRADOR
3. M3 Organización académica y ABB      7. Ejecutar pruebas automáticas
4. M4 Red de laboratorios (grafo)       8. Reiniciar sistema vacío
```

## Estructura del proyecto

```
src/
├── modelo/          Estudiante, SolicitudLaboratorio, Operacion, ComparacionBusqueda, Recomendacion
├── estructuras/     VectorDinamico, ListaEnlazada, Pila, Cola, ABB, ArbolGeneral, Grafo
├── servicios/       GestionEstudiantes, AtencionLaboratorio, OrganizacionAcademica,
│                    RedLaboratorio, RedSocial, SistemaSigac (integra todo), DatosCasoIntegrador
├── pruebas/         PruebasSigac (53 pruebas sin JUnit)
└── app/             Main (menú) y CasoIntegrador
docs/
├── matriz-pruebas.md        matriz de pruebas (normales, extremas y erróneas)
├── complejidad.md           complejidad temporal de las operaciones
├── contribuciones.md        plantilla de contribuciones por integrante (completar)
├── preguntas-individuales.md  guía para la comprobación individual
└── evidencias/              salidas reales de las pruebas y del caso integrador
```

## Cómo se integran los módulos

`SistemaSigac` es la fachada que mantiene la coherencia entre módulos:

- Un estudiante se guarda a la vez en **vector**, **lista enlazada**, **ABB** (índice por matrícula) y como hoja
  del **árbol general** bajo su carrera. Eliminarlo lo quita de las cuatro estructuras.
- Solo se registra un estudiante si su **carrera existe** en el árbol académico.
- Una **solicitud de laboratorio** (cola FIFO) solo se crea si existen el estudiante y el equipo (vértice del grafo).
- No se puede eliminar un estudiante ni un equipo con **solicitudes pendientes**.
- La **pila de historial** (LIFO) guarda las operaciones reversibles: registrar, eliminar y actualizar
  estudiantes, y eliminar una conexión del laboratorio. `deshacer` las revierte en orden inverso.

## Decisiones que hacen reproducibles las pruebas

| Tema | Decisión |
| --- | --- |
| **Orden de inserción de vecinos** | Los vecinos se guardan y recorren en el orden en que se insertaron las aristas. Vértices del laboratorio: `PC1, SW1, SRV1, PC2, PC3, PC4`. Aristas: `PC1→SW1`, `SW1→SRV1`, `SRV1→PC2`, `PC1→PC3`. |
| **Dirección de aristas** | La red de **laboratorio es dirigida** (sigue las flechas del enunciado) y ponderada (pesos 1, 2, 1, 3). La **red social es no dirigida** (la amistad es simétrica). `Grafo` soporta ambos modos. |
| **Política de eliminación del ABB** | Hoja: se borra. Un hijo: el hijo ocupa su lugar. **Dos hijos: se reemplaza por el sucesor inorden** (mínimo del subárbol derecho). Sin claves duplicadas. |
| **Desempate en recomendaciones** | Más amigos en común primero; si hay empate, orden alfabético. |
| **Aristas** | No se permiten lazos, pesos ≤ 0 ni aristas duplicadas. `eliminarVertice` elimina también las aristas entrantes. |
| **«Recuperar dos operaciones LIFO»** | Se interpreta como **deshacer** las dos últimas operaciones del historial. En el caso integrador son `ELIMINAR 30` (el 30 reaparece) y `REGISTRAR 80` (el 80 se quita); el inorden final es `20, 30, 40, 50, 60, 70`. |

## Resultados del caso integrador

| Paso | Resultado |
| --- | --- |
| Inorden del ABB | `20, 30, 40, 50, 60, 70, 80` |
| Eliminar matrícula 30 | Inorden `20, 40, 50, 60, 70, 80`; preorden `50, 40, 20, 70, 60, 80` (ABB válido) |
| 3 solicitudes FIFO | Se atienden en el orden #1, #2, #3 |
| 2 operaciones LIFO | Se recuperan `ELIMINAR 30` y luego `REGISTRAR 80` |
| BFS desde PC1 | `PC1 → SW1 → PC3 → SRV1 → PC2`; **PC4 no alcanzable** |
| Recomendaciones para Ana | **Diego** (2 en común: Beto, Carla) y **Elena** (1: Carla) |
| Eliminar `SW1 → SRV1` | BFS `PC1 → SW1 → PC3`; no alcanzables: `SRV1, PC2, PC4` |

Salida completa: [`docs/evidencias/salida-caso-integrador.txt`](docs/evidencias/salida-caso-integrador.txt).

## Complejidad temporal (resumen)

| Operación | Complejidad |
| --- | --- |
| Búsqueda en ABB (`ABB.buscar`) | O(log n) promedio, O(n) peor caso |
| Búsqueda secuencial en vector / lista | O(n) |
| Inserción / eliminación en ABB | O(log n) promedio, O(n) peor caso |
| BFS y DFS | O(V + E) |
| Encolar / desencolar, push / pop | O(1) |
| Eliminar un vértice (aristas entrantes) | O(V + E) |

Detalle y justificación de 15 operaciones en [`docs/complejidad.md`](docs/complejidad.md).

## Pruebas

53 pruebas automáticas (normales, extremas y erróneas) sin dependencias externas.
Matriz completa en [`docs/matriz-pruebas.md`](docs/matriz-pruebas.md).

## Rúbrica → dónde se cumple

| Criterio | Dónde |
| --- | --- |
| M1 Vectores y listas enlazadas | `VectorDinamico`, `ListaEnlazada`, `GestionEstudiantes` |
| M2 Pilas y colas | `Pila`, `Cola`, `AtencionLaboratorio`, `SistemaSigac.deshacer` |
| M3 Árboles general y ABB | `ArbolGeneral`, `ABB`, `OrganizacionAcademica` |
| M4 Grafos, BFS, DFS, conectividad | `Grafo`, `RedLaboratorio` |
| M5 Recomendaciones | `RedSocial.recomendar` |
| M6 Integración, pruebas, validación | `SistemaSigac`, `app/Main`, `pruebas/PruebasSigac` |
| GitHub | `docs/contribuciones.md` + historial de commits del equipo |
| README y documentación técnica | este archivo y `docs/` |

## Subir el proyecto a GitHub

Con un repositorio vacío ya creado en GitHub:

```bash
cd SIGAC-UTA
git init
git add .
git commit -m "Proyecto base SIGAC-UTA"
git branch -M main
git remote add origin https://github.com/USUARIO/REPOSITORIO.git
git push -u origin main
```

Si ya clonó su repositorio, copie dentro de él el contenido de esta carpeta (incluida `.gitignore`) y haga
`git add . && git commit && git push`. Después, cada integrante debe trabajar en ramas propias y hacer sus
commits desde su cuenta (ver [`docs/contribuciones.md`](docs/contribuciones.md)).
