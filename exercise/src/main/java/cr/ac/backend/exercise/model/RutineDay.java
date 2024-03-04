package cr.ac.backend.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "rutine_day")
public class RutineDay implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutine", nullable = false)
    private Long id;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "days", nullable = false)
    private List<DayOfWeek> days;

    @JsonIgnoreProperties("rutineDay")
    @ManyToMany(mappedBy = "rutineDay", cascade = CascadeType.ALL)
    @Column(name = "exercise_rutine", nullable = false)
    private List<ExerciseSpecified> exerciseSpecified;

    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    @Override
    public String toString() {
        return "RutineDay{" +
                "id=" + id +
                ", days=" + days +
                ", exerciseSpecified=" + exerciseSpecified +
                '}';
    }
}



