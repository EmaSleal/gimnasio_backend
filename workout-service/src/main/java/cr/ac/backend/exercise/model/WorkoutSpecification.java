package cr.ac.backend.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet; // Added import for HashSet
import java.util.Set; // Added import for Set

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "workout_specification")
public class WorkoutSpecification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workout_specification", nullable = false)
    private Long id;

    //description
    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "reps_number", nullable = true)
    private int repsNumber;

    @Column(name = "sets_number", nullable = false)
    private int setsNumber;

    @Column(name = "recommended_weight", nullable = false)
    private double recommendedWeight;

    @Column(name = "trainer_rating", nullable = false)
    private double trainerRating;

    //isTimeBased
    @Column(name = "is_time_based", nullable = false)
    private boolean isTimeBased;

    //time
    @Column(name = "time", nullable = true)
    private int time;

    @JsonIgnoreProperties("workoutSpecification")
    @ManyToOne
    @JoinColumn(name = "id_workout", nullable = false)
    private Workout workout;

    @JsonIgnoreProperties("workoutSpecification")
    @ManyToMany
    @JoinTable(name = "workout_specification_daily_routine",
            joinColumns = @JoinColumn(name = "id_workout_specification"),
            inverseJoinColumns = @JoinColumn(name = "id_daily_routine"))
    private Set<DailyRoutine> dailyRoutine = new HashSet<>(); // Changed List to Set

    @Override
    public String toString() {
        return "WorkoutSpecification{" +
                "id=" + id +
                ", repsNumber=" + repsNumber +
                ", setsNumber=" + setsNumber +
                ", recommendedWeight=" + recommendedWeight +
                ", trainerRating=" + trainerRating +
                ", exercise=" + (workout != null ? workout.getId() : "null") + // Handle potential null value
                '}';
    }
}
