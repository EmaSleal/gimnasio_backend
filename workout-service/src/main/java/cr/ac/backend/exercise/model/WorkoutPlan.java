package cr.ac.backend.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "workout_plan")
public class WorkoutPlan implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workout_plan", nullable = false)
    private Long id;



    //user
    @Column(name = "id_user", nullable = false)
    private Long idUser;

    //trainer
    @Column(name = "id_trainer", nullable = false)
    private Long idTrainer;

    //description
    @Column(name = "description", nullable = true)
    private String description;

    //status
    @Column(name = "status", nullable = false)
    private String status;

    //start_date
    @Column(name = "start_date", nullable = false)
    private String startDate;

    //end_date
    @Column(name = "end_date", nullable = false)
    private String endDate;

    //created_at trigger that is set when the workout is created
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    //updated_at trigger that is set when the workout is updated
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    //is Template
    @Column(name = "is_template", nullable = false)
    private boolean isTemplate;

    @JsonIgnoreProperties("workoutPlan")
    @ManyToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL)
    private Set<DailyRoutine> dailyRoutine = new HashSet<>();

}
