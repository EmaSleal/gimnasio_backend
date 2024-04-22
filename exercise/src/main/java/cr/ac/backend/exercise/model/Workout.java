/*
ID de Workout:

Tipo: Número entero.
Descripción: Identificador único para cada ejercicio.
Nombre del Workout:

Tipo: Cadena de texto.
Descripción: Nombre descriptivo del ejercicio.
Grupo Muscular:

Tipo: Cadena de texto.
Descripción: Grupo muscular al que pertenece el ejercicio.
Carga Muscular:

Tipo: Enumeración (baja, media, alta).
Descripción: Nivel de carga muscular asociado al ejercicio.
Número de Repeticiones:

Tipo: Número entero.
Descripción: Cantidad de repeticiones recomendadas para el ejercicio.
Número de Series:

Tipo: Número entero.
Descripción: Cantidad de series recomendadas para el ejercicio.
Peso Recomendado:

Tipo: Número decimal.
Descripción: Peso recomendado para el ejercicio.
Calificación del Entrenador:

Tipo: Número decimal.
Descripción: Calificación asignada por el entrenador al ejercicio.
*/
package cr.ac.backend.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet; // Added import for HashSet
import java.util.Set; // Added import for Set

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workout")
public class Workout implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workout", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JsonIgnoreProperties("workouts")
    @JoinColumn(name = "muscular_group", nullable = false)
    private MuscularGroup muscularGroup;

    @Column(name = "muscular_load", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExerciseEnums.MuscularLoad muscularLoad;

    @JsonIgnoreProperties("workout")
    @OneToMany(mappedBy = "workout", cascade = CascadeType.PERSIST)
    private Set<WorkoutSpecification> WorkoutSpecification = new HashSet<>();




    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", muscularGroup='" + muscularGroup + '\'' +
                ", muscularLoad=" + (muscularLoad != null ? muscularLoad.toString() : "null") +
                ", workoutSpecified=" + WorkoutSpecification +
                '}';
    }
}

