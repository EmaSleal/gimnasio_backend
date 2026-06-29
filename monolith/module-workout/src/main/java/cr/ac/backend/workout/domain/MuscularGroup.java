package cr.ac.backend.workout.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wkt_muscular_groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MuscularGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wkt_mg_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
