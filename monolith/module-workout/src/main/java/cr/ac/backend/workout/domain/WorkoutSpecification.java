package cr.ac.backend.workout.domain;

import cr.ac.backend.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wkt_workout_specifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSpecification extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wkt_spec_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @Column(length = 500)
    private String description;

    @Column(name = "reps_number")
    private Integer repsNumber;

    @Column(name = "sets_number", nullable = false)
    private int setsNumber;

    @Column(name = "recommended_weight", nullable = false)
    private double recommendedWeight;

    @Column(name = "trainer_rating", nullable = false)
    private double trainerRating;

    @Column(name = "is_time_based", nullable = false)
    private boolean isTimeBased;

    @Column(name = "time_seconds")
    private Integer timeSeconds;
}
