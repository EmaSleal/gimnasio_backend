package cr.ac.backend.exercise;

import cr.ac.backend.exercise.model.Exercise;
import cr.ac.backend.exercise.model.ExerciseSpecified;
import cr.ac.backend.exercise.model.RutineDay;
import cr.ac.backend.exercise.service.ExerciseService;
import cr.ac.backend.exercise.service.ExerciseSpecifiedService;
import cr.ac.backend.exercise.service.RutineDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication(
        scanBasePackages = {
                "cr.ac.backend.exercise",
                //"com.m4n0.amq"
        }
)
/*@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:application-${spring.profiles.active}.properties")
})*/
@EnableDiscoveryClient
@Slf4j
public class ExerciseServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExerciseServerApplication.class,args);
    }

    @Bean
    CommandLineRunner run(ExerciseService service, ExerciseSpecifiedService serviceSpecified, RutineDayService rutineDayService) {
        return args -> {
            var exercise = new Exercise(null,"curl de biceps", "biceps", Exercise.MuscularLoad.LOW, new ArrayList<>());
            var rutineDay = new RutineDay(null,new ArrayList<>(), new ArrayList<>());
            rutineDay.getDays().add(RutineDay.DayOfWeek.MONDAY);
            rutineDay.getDays().add(RutineDay.DayOfWeek.WEDNESDAY);
            var exerciseSpecified = new ExerciseSpecified(null, 12, 4, 25, 8, exercise, new ArrayList<>());
            exercise = service.save(exercise).get();
            rutineDay = rutineDayService.save(rutineDay).get();
            exerciseSpecified = serviceSpecified.save(exerciseSpecified).get();

            exerciseSpecified.getRutineDay().add(rutineDay);

            exercise.getExerciseSpecified().add(exerciseSpecified);
                 rutineDay.getExerciseSpecified().add(exerciseSpecified);
            rutineDayService.save(rutineDay);
            serviceSpecified.save(exerciseSpecified);
            service.save(exercise);



        };
    }
}