package cr.ac.backend.exercise.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

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
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercise")

public class Exercise implements Serializable{


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercise", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "muscular_group", nullable = false)
    private String muscularGroup;

    @Column(name = "muscular_load", nullable = false)
    @Enumerated(EnumType.STRING)
    private MuscularLoad muscularLoad;

    @JsonIgnoreProperties("exercise")
    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
    private List<ExerciseSpecified> exerciseSpecified;

    /*enum to muscularLoad*/
    public enum MuscularLoad {
        LOW,
        MEDIUM,
        HIGH
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", muscularGroup='" + muscularGroup + '\'' +
                ", muscularLoad=" + muscularLoad.toString() +
                ", exerciseSpecified=" + exerciseSpecified +
                '}';
    }
}



