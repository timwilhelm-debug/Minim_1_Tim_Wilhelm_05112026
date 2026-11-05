# Minim 1 DSA - Flota de drons

Aplicacio per gestionar drons, pilots i plans de vol.
Els drons es poden portar a un magatzem per arreglar-los.

## Part 2

- Tot es guarda a `DroneManagerImpl` 
- Drons i pilots es guarden en un `HashMap` per trobar-los rapid per id.
- El magatzem es una **pila**: el dron que entra l'ultim es el primer que s'arregla.
- Un pla de vol dona error si el dron es al magatzem o si el dron o el pilot ja volen a aquella hora.
- Tests a `DroneManagerTest`: **passen tots (4)**.

## Part 3

1. Executar `Main.java`.
2. Obrir http://localhost:8080/swagger/
3. Hi ha 4 operacions:
   - Veure els drons ordenats per hores de vol.
   - Guardar un dron al magatzem.
   - Arreglar un dron del magatzem.
   - Crear un pla de vol (data amb format `2026-11-05 10:00`).



## Estat

- Els test funcionen i les operacions també
