package cr.ac.backend.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "exercise_specified")
public class ExerciseSpecified implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercise_specified", nullable = false)
    private Long id;

    @Column(name = "reps_number", nullable = false)
    private int repsNumber;

    @Column(name = "sets_number", nullable = false)
    private int setsNumber;

    @Column(name = "recommended_weight", nullable = false)
    private double recommendedWeight;

    @Column(name = "trainer_rating", nullable = false)
    private double trainerRating;

    @JsonIgnoreProperties("exerciseSpecified")
    @ManyToOne
    @JoinColumn(name = "id_exercise", nullable = false)
    private Exercise exercise;

    @JsonIgnoreProperties("exerciseSpecified")
    @ManyToMany
    @JoinTable(name = "exercise_rutine",
            joinColumns = @JoinColumn(name = "id_exercise_specified"),
            inverseJoinColumns = @JoinColumn(name = "id_rutine"))
    private List<RutineDay> rutineDay;

    @Override
    public String toString() {
        return "ExerciseSpecified{" +
                "id=" + id +
                ", repsNumber=" + repsNumber +
                ", setsNumber=" + setsNumber +
                ", recommendedWeight=" + recommendedWeight +
                ", trainerRating=" + trainerRating +
                ", exercise=" + exercise +
                ", rutineDay=" + rutineDay +
                '}';
    }
}
