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
@Table(name = "routine_day")
public class RoutineDay implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_routine_day", nullable = false)
    private Long id;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "days", nullable = false)
    private Set<ExerciseEnums.DayOfWeek> days = new HashSet<>(); // Changed List to Set

    @JsonIgnoreProperties("routineDay")
    @ManyToMany(mappedBy = "routineDay", cascade = CascadeType.ALL)
    private Set<ExerciseSpecified> exerciseSpecifieds = new HashSet<>(); // Changed List to Set



    @JsonIgnoreProperties("routineDay")
    @ManyToMany
    @JoinTable(name = "routine_day_routine",
            joinColumns = @JoinColumn(name = "id_routine_day"),
            inverseJoinColumns = @JoinColumn(name = "id_routine"))
    private Set<Routine> routine = new HashSet<>(); // Changed List to Set


    @Override
    public String toString() {
        return "RoutineDay{" +
                "id=" + id +
                ", days=" + days +
                ", exerciseSpecified=" + exerciseSpecifieds +
                '}';
    }
}
