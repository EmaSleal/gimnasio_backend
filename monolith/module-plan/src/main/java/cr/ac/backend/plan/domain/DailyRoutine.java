package cr.ac.backend.plan.domain;

import cr.ac.backend.workout.domain.WorkoutSpecification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pln_daily_routines")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyRoutine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pln_routine_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToMany
    @JoinTable(
        name = "pln_routine_specifications",
        joinColumns = @JoinColumn(name = "routine_id"),
        inverseJoinColumns = @JoinColumn(name = "spec_id")
    )
    @Builder.Default
    private Set<WorkoutSpecification> workoutSpecifications = new HashSet<>();
}
