package cr.ac.backend.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet; // Added import for HashSet
import java.util.Set; // Added import for Set

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "daily_routine")
public class DailyRoutine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_daily_routine", nullable = false)
    private Long id;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "days", nullable = false)
    private Set<ExerciseEnums.DayOfWeek> days = new HashSet<>(); // Changed List to Set

    @JsonIgnoreProperties("dailyRoutine")
    @ManyToMany(mappedBy = "dailyRoutine", cascade = CascadeType.ALL)
    private Set<WorkoutSpecification> workoutSpecification = new HashSet<>(); // Changed List to Set



    @JsonIgnoreProperties("dailyRoutine")
    @ManyToMany
    @JoinTable(name = "daily_routine_workout_plan",
            joinColumns = @JoinColumn(name = "id_daily_routine"),
            inverseJoinColumns = @JoinColumn(name = "id_workout_plan"))
    private Set<WorkoutPlan> workoutPlan = new HashSet<>(); // Changed List to Set


    @Override
    public String toString() {
        return "DailyRoutine{" +
                "id=" + id +
                ", days=" + days +
                ", exerciseSpecified=" + workoutSpecification +
                '}';
    }
}
