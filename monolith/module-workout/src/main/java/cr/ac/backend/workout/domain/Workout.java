package cr.ac.backend.workout.domain;

import cr.ac.backend.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "wkt_workouts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workout extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wkt_workout_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muscular_group_id", nullable = false)
    private MuscularGroup muscularGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "muscular_load", nullable = false, length = 20)
    private MuscularLoad muscularLoad;
}
