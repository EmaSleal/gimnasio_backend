package cr.ac.backend.exercise;

import cr.ac.backend.exercise.model.*;
import cr.ac.backend.exercise.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.stream.Collectors;

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
    CommandLineRunner run(ExerciseService service, ExerciseSpecifiedService serviceSpecified, RoutineDayService routineDayService, MuscularGroupService muscularGroupService, RoutineService Ru){
        return args -> {
            //muscular groups
            var muscularGroup = new MuscularGroup(null, "BICEPS", new HashSet<>());
            var muscularGroup2 = new MuscularGroup(null, "PECHO", new HashSet<>());
            var muscularGroup3 = new MuscularGroup(null, "PIERNAS", new HashSet<>());
            var muscularGroup4 = new MuscularGroup(null, "HOMBROS", new HashSet<>());
            var muscularGroup5 = new MuscularGroup(null, "ESPALDA", new HashSet<>());
            var muscularGroup6 = new MuscularGroup(null, "CORE", new HashSet<>());
            var muscularGroup7 = new MuscularGroup(null, "TRICEPS", new HashSet<>());
            var muscularGroup8 = new MuscularGroup(null, "CUERPO_COMPLETO", new HashSet<>());

            muscularGroupService.save(muscularGroup);
            muscularGroupService.save(muscularGroup2);
            muscularGroupService.save(muscularGroup3);
            muscularGroupService.save(muscularGroup4);
            muscularGroupService.save(muscularGroup5);
            muscularGroupService.save(muscularGroup6);
            muscularGroupService.save(muscularGroup7);
            muscularGroupService.save(muscularGroup8);



            //exercises
            var exercises = new ArrayList<Exercise>();

            exercises.add(new Exercise(null, "curl de biceps", muscularGroup, ExerciseEnums.MuscularLoad.LOW,  new HashSet<>()));
            exercises.add(new Exercise(null, "press de pecho", muscularGroup2, ExerciseEnums.MuscularLoad.MEDIUM,  new HashSet<>()));
            exercises.add(new Exercise(null, "sentadillas", muscularGroup3, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>()));
            exercises.add(new Exercise(null, "prensa de hombros", muscularGroup4, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>()));
            exercises.add(new Exercise(null, "pull-ups", muscularGroup5, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>()));
            exercises.add(new Exercise(null, "plank", muscularGroup6, ExerciseEnums.MuscularLoad.LOW, new HashSet<>()));
            exercises.add(new Exercise(null, "curl de tríceps", muscularGroup7, ExerciseEnums.MuscularLoad.LOW, new HashSet<>()));
            exercises.add(new Exercise(null, "zancadas", muscularGroup3, ExerciseEnums.MuscularLoad.MEDIUM,  new HashSet<>()));
            exercises.add(new Exercise(null, "remo con barra", muscularGroup5, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>()));
            exercises.add(new Exercise(null, "crunches", muscularGroup6, ExerciseEnums.MuscularLoad.LOW, new HashSet<>()));
            exercises.add(new Exercise(null, "press militar", muscularGroup4, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>()));
            exercises.add(new Exercise(null, "dominadas", muscularGroup5, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>()));
            exercises.add(new Exercise(null, "extensiones de tríceps", muscularGroup7, ExerciseEnums.MuscularLoad.LOW, new HashSet<>()));
            exercises.add(new Exercise(null, "burpees", muscularGroup8, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>()));
            exercises.add(new Exercise(null, "sentadillas goblet", muscularGroup3, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>()));
            exercises.add(new Exercise(null, "plancha lateral", muscularGroup6, ExerciseEnums.MuscularLoad.LOW, new HashSet<>()));
            exercises.add(new Exercise(null, "elevaciones laterales", muscularGroup4, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>()));
            exercises.add(new Exercise(null, "hip thrust", muscularGroup3, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>()));
            exercises.add(new Exercise(null, "pull-ups pronas", muscularGroup5, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>()));
            exercises.add(new Exercise(null, "flexiones", muscularGroup2, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>()));



            //exercises Specified

            var exercisesSpecified = new ArrayList<ExerciseSpecified>();

            exercisesSpecified.add( new ExerciseSpecified(null, 12, 4, 25, 8, exercises.get(0), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 5, 4, 100, 7, exercises.get(1), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 3, 4, 25, 8, exercises.get(2), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 12, 3, 5, 9, exercises.get(3), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 10, 3, 32, 10, exercises.get(4), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 6, 3, 22, 10, exercises.get(5), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 12, 4, 25, 8, exercises.get(6), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 3, 4, 25, 8, exercises.get(7), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 10, 3, 80, 10, exercises.get(8), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 6, 3, 15, 10, exercises.get(9), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 12, 3, 5, 9, exercises.get(10), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 10, 3, 32, 10, exercises.get(11), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 6, 3, 22, 10, exercises.get(12), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 12, 4, 25, 8, exercises.get(13), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 3, 4, 25, 8, exercises.get(14), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 10, 3, 80, 10, exercises.get(15), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 6, 3, 15, 10, exercises.get(16), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 12, 3, 5, 9, exercises.get(17), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 10, 3, 32, 10, exercises.get(18), new HashSet<>()));
            exercisesSpecified.add( new ExerciseSpecified(null, 6, 3, 22, 10, exercises.get(19), new HashSet<>()));

            var routineDay = new RoutineDay(null,new HashSet<>(), new HashSet<>(), new HashSet<>());
            routineDay.getDays().add(ExerciseEnums.DayOfWeek.MONDAY);
            routineDay.getDays().add(ExerciseEnums.DayOfWeek.WEDNESDAY);

            var routineDay2 = new RoutineDay(null,new HashSet<>(), new HashSet<>(), new HashSet<>());
            routineDay2.getDays().add(ExerciseEnums.DayOfWeek.TUESDAY);

            var routineDay3 = new RoutineDay(null,new HashSet<>(), new HashSet<>(), new HashSet<>());
            routineDay3.getDays().add(ExerciseEnums.DayOfWeek.THURSDAY);

            var routineDay4 = new RoutineDay(null,new HashSet<>(), new HashSet<>(), new HashSet<>());
            routineDay4.getDays().add(ExerciseEnums.DayOfWeek.FRIDAY);

            //routine

            var routine = new Routine(null, new HashSet<>());
            routine = Ru.save(routine).get();
            routine.getRoutineDay().add(routineDay);
            routine.getRoutineDay().add(routineDay2);
            routine.getRoutineDay().add(routineDay3);
            routine.getRoutineDay().add(routineDay4);


            routineDay.getRoutine().add(routine);
            routineDay2.getRoutine().add(routine);
            routineDay3.getRoutine().add(routine);
            routineDay4.getRoutine().add(routine);

            routineDayService.update(routineDay);
            routineDayService.update(routineDay2);
            routineDayService.update(routineDay3);
            routineDayService.update(routineDay4);



            for (int i = 0; i < exercises.size(); i++) {
                var exercise = exercises.get(i);
                exercises.set(i, service.save(exercise).get());
            }

            // routineDay 1

            //guardo routineDay en los 4 primeros exerciseSpecified de la lista
            saveRutine(service, serviceSpecified, routineDayService, exercisesSpecified, routineDay, exercises, 0, 4);

            // routineDay 2

            //guardo routineDay2 en los exerciseSpecified 4 al 7 de la lista
            saveRutine(service, serviceSpecified, routineDayService, exercisesSpecified, routineDay2, exercises, 4, 8);

            // routineDay 3

            //guardo routineDay3 en los exerciseSpecified 8 al 13 de la lista
            saveRutine(service, serviceSpecified, routineDayService, exercisesSpecified, routineDay3, exercises, 8, 14);

            // routineDay 4

            //guardo routineDay4 en los exerciseSpecified 14 al 19 de la lista
            saveRutine(service, serviceSpecified, routineDayService, exercisesSpecified, routineDay4, exercises, 14, 20);


        };
    }

    private static void saveRutine(ExerciseService service, ExerciseSpecifiedService serviceSpecified, RoutineDayService routineDayService, ArrayList<ExerciseSpecified> exercisesSpecified, RoutineDay routineDay, ArrayList<Exercise> exercises, Integer number1, Integer number2) {
        //guardo routineDay en los 4 primeros exerciseSpecified de la lista

        for (int i = number1; i < number2; i++) {
            exercisesSpecified.get(i).getRoutineDay().add(routineDay);
        }

        routineDay = routineDayService.save(routineDay).get();

        //guardo los 4 primeros exerciseSpecified en routineDay

        for (int i = number1; i < number2; i++) {
            routineDay.getExerciseSpecifieds().add(exercisesSpecified.get(i));
        }

        //guardo los 4 primeros exercises en la base de datos

        for (int i = number1; i < number2; i++) {
            var exercise = exercises.get(i);
            exercises.remove(i);
            exercises.add(i, service.update(exercise).get());

        }

        //actualizo los 4 primeros exerciseSpecified con los 4 primeros exercises

        for (int i = number1; i < number2; i++) {
            var exerciseSpecified = exercisesSpecified.get(i);
            for (int j = number1; j < number2; j++) {
                if (i == j) {
                    exerciseSpecified.setExercise(exercises.get(i));
                    serviceSpecified.update(exerciseSpecified);
                }
            }

        }
    }
}