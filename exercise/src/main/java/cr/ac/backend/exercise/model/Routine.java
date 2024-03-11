package cr.ac.backend.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "routine")
public class Routine implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_routine", nullable = false)
    private Long id;

    @JsonIgnoreProperties("routine")
    @ManyToMany(mappedBy = "routine", cascade = CascadeType.ALL)
    private Set<RoutineDay> routineDay = new HashSet<>();


}
