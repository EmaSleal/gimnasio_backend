

/*
ID de Exercise:

Tipo: Número entero.
Descripción: Identificador único para cada ejercicio.
Nombre del Exercise:

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
@Table(name = "exercise")
public class Exercise implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercise", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JsonIgnoreProperties("exercises")
    @JoinColumn(name = "muscular_group", nullable = false)
    private MuscularGroup muscularGroup;

    @Column(name = "muscular_load", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExerciseEnums.MuscularLoad muscularLoad;

    @JsonIgnoreProperties("exercise")
    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
    private Set<ExerciseSpecified> exerciseSpecified = new HashSet<>(); // Changed List to Set




    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", muscularGroup='" + muscularGroup + '\'' +
                ", muscularLoad=" + (muscularLoad != null ? muscularLoad.toString() : "null") + // Handle potential null value
                ", exerciseSpecified=" + exerciseSpecified +
                '}';
    }
}

