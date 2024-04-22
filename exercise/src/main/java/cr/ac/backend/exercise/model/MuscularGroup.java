package cr.ac.backend.exercise.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "muscular_groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MuscularGroup implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   // @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private String name;

    @JsonIgnoreProperties("muscularGroup")
    @OneToMany(mappedBy = "muscularGroup", cascade = CascadeType.ALL)
    private Set<Workout> workouts = new HashSet<>();


}