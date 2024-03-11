package cr.ac.backend.exercise;

import cr.ac.backend.exercise.model.*;
import cr.ac.backend.exercise.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;

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
            var exercise = new Exercise(null, "curl de biceps", muscularGroup, ExerciseEnums.MuscularLoad.LOW, new HashSet<>(), new HashSet<>());
            var exercise2 = new Exercise(null, "press de pecho", muscularGroup2, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>(), new HashSet<>());
            var exercise3 = new Exercise(null, "sentadillas", muscularGroup3, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>(), new HashSet<>());
            var exercise4 = new Exercise(null, "prensa de hombros", muscularGroup4, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>(), new HashSet<>());
            var exercise5 = new Exercise(null, "pull-ups", muscularGroup5, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>(), new HashSet<>());
            var exercise6 = new Exercise(null, "plank", muscularGroup6, ExerciseEnums.MuscularLoad.LOW, new HashSet<>(), new HashSet<>());
            var exercise7 = new Exercise(null, "curl de tríceps", muscularGroup7, ExerciseEnums.MuscularLoad.LOW, new HashSet<>(), new HashSet<>());
            var exercise8 = new Exercise(null, "zancadas", muscularGroup3, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>(), new HashSet<>());
            var exercise9 = new Exercise(null, "remo con barra", muscularGroup5, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>(), new HashSet<>());
            var exercise10 = new Exercise(null, "crunches", muscularGroup6, ExerciseEnums.MuscularLoad.LOW, new HashSet<>(), new HashSet<>());
            var exercise11 = new Exercise(null, "press militar", muscularGroup4, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>(), new HashSet<>());
            var exercise12 = new Exercise(null, "dominadas", muscularGroup5, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>(), new HashSet<>());
            var exercise13 = new Exercise(null, "extensiones de tríceps", muscularGroup7, ExerciseEnums.MuscularLoad.LOW, new HashSet<>(), new HashSet<>());
            var exercise14 = new Exercise(null, "burpees", muscularGroup8, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>(), new HashSet<>());
            var exercise15 = new Exercise(null, "sentadillas goblet", muscularGroup3, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>(), new HashSet<>());
            var exercise16 = new Exercise(null, "plancha lateral", muscularGroup6, ExerciseEnums.MuscularLoad.LOW, new HashSet<>(), new HashSet<>());
            var exercise17 = new Exercise(null, "elevaciones laterales", muscularGroup4, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>(), new HashSet<>());
            var exercise18 = new Exercise(null, "hip thrust", muscularGroup3, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>(), new HashSet<>());
            var exercise19 = new Exercise(null, "pull-ups pronas", muscularGroup5, ExerciseEnums.MuscularLoad.HIGH, new HashSet<>(), new HashSet<>());
            var exercise20 = new Exercise(null, "flexiones", muscularGroup2, ExerciseEnums.MuscularLoad.MEDIUM, new HashSet<>(), new HashSet<>());


            //exercises Specified

            var exerciseSpecified = new ExerciseSpecified(null, 12, 4, 25, 8, exercise);
            var exerciseSpecified2 = new ExerciseSpecified(null, 5, 4, 100, 7, exercise2);
            var exerciseSpecified3 = new ExerciseSpecified(null, 3, 4, 25, 8, exercise3);
            var exerciseSpecified4 = new ExerciseSpecified(null, 12, 3, 5, 9, exercise4);
            var exerciseSpecified5 = new ExerciseSpecified(null, 10, 3, 32, 10, exercise5);
            var exerciseSpecified6 = new ExerciseSpecified(null, 6, 3, 22, 10, exercise6);
            var exerciseSpecified7 = new ExerciseSpecified(null, 12, 4, 25, 8, exercise7);
            var exerciseSpecified8 = new ExerciseSpecified(null, 3, 4, 25, 8, exercise8);
            var exerciseSpecified9 = new ExerciseSpecified(null, 10, 3, 80, 10, exercise9);
            var exerciseSpecified10 = new ExerciseSpecified(null, 6, 3, 15, 10, exercise10);
            var exerciseSpecified11 = new ExerciseSpecified(null, 12, 3, 5, 9, exercise11);
            var exerciseSpecified12 = new ExerciseSpecified(null, 10, 3, 32, 10, exercise12);
            var exerciseSpecified13 = new ExerciseSpecified(null, 6, 3, 22, 10, exercise13);
            var exerciseSpecified14 = new ExerciseSpecified(null, 12, 4, 25, 8, exercise14);
            var exerciseSpecified15 = new ExerciseSpecified(null, 3, 4, 25, 8, exercise15);
            var exerciseSpecified16 = new ExerciseSpecified(null, 10, 3, 80, 10, exercise16);
            var exerciseSpecified17 = new ExerciseSpecified(null, 6, 3, 15, 10, exercise17);
            var exerciseSpecified18 = new ExerciseSpecified(null, 12, 3, 5, 9, exercise18);
            var exerciseSpecified19 = new ExerciseSpecified(null, 10, 3, 32, 10, exercise19);
            var exerciseSpecified20 = new ExerciseSpecified(null, 6, 3, 22, 10, exercise20);

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

            exercise.getRoutineDay().add(routineDay);
            exercise2.getRoutineDay().add(routineDay);
            exercise3.getRoutineDay().add(routineDay);
            exercise4.getRoutineDay().add(routineDay);
            exercise5 = service.save(exercise5).get();
            exercise6 = service.save(exercise6).get();
            exercise7 = service.save(exercise7).get();
            exercise8 = service.save(exercise8).get();
            exercise9 = service.save(exercise9).get();
            exercise10 = service.save(exercise10).get();
            exercise11 = service.save(exercise11).get();
            exercise12 = service.save(exercise12).get();
            exercise13 = service.save(exercise13).get();
            exercise14 = service.save(exercise14).get();
            exercise15 = service.save(exercise15).get();
            exercise16 = service.save(exercise16).get();
            exercise17 = service.save(exercise17).get();
            exercise18 = service.save(exercise18).get();
            exercise19 = service.save(exercise19).get();
            exercise20 = service.save(exercise20).get();

            // rutienDay 1



            routineDay = routineDayService.save(routineDay).get();

            routineDay.getExercise().add(exercise);
            routineDay.getExercise().add(exercise2);
            routineDay.getExercise().add(exercise3);
            routineDay.getExercise().add(exercise4);


            exercise = service.save(exercise).get();
            exercise2 = service.save(exercise2).get();
            exercise3 = service.save(exercise3).get();
            exercise4 = service.save(exercise4).get();

            exercise.getExerciseSpecified().add(exerciseSpecified);
            exercise2.getExerciseSpecified().add(exerciseSpecified2);
            exercise3.getExerciseSpecified().add(exerciseSpecified3);
            exercise4.getExerciseSpecified().add(exerciseSpecified4);

            serviceSpecified.update(exerciseSpecified);
            serviceSpecified.update(exerciseSpecified2);
            serviceSpecified.update(exerciseSpecified3);
            serviceSpecified.update(exerciseSpecified4);

            // routineDay 2

            exercise5.getRoutineDay().add(routineDay2);
            exercise6.getRoutineDay().add(routineDay2);
            exercise7.getRoutineDay().add(routineDay2);
            exercise8.getRoutineDay().add(routineDay2);

            routineDay2 = routineDayService.save(routineDay2).get();

            routineDay2.getExercise().add(exercise5);
            routineDay2.getExercise().add(exercise6);
            routineDay2.getExercise().add(exercise7);
            routineDay2.getExercise().add(exercise8);

            exercise5 = service.save(exercise5).get();
            exercise6 = service.save(exercise6).get();
            exercise7 = service.save(exercise7).get();
            exercise8 = service.save(exercise8).get();

            exercise5.getExerciseSpecified().add(exerciseSpecified5);
            exercise6.getExerciseSpecified().add(exerciseSpecified6);
            exercise7.getExerciseSpecified().add(exerciseSpecified7);
            exercise8.getExerciseSpecified().add(exerciseSpecified8);

            serviceSpecified.update(exerciseSpecified5);
            serviceSpecified.update(exerciseSpecified6);
            serviceSpecified.update(exerciseSpecified7);
            serviceSpecified.update(exerciseSpecified8);

            // routineDay 3

            exercise9.getRoutineDay().add(routineDay3);
            exercise10.getRoutineDay().add(routineDay3);
            exercise11.getRoutineDay().add(routineDay3);
            exercise12.getRoutineDay().add(routineDay3);
            exercise13.getRoutineDay().add(routineDay3);
            exercise14.getRoutineDay().add(routineDay3);

            routineDay3 = routineDayService.save(routineDay3).get();

            routineDay3.getExercise().add(exercise9);
            routineDay3.getExercise().add(exercise10);
            routineDay3.getExercise().add(exercise11);
            routineDay3.getExercise().add(exercise12);
            routineDay3.getExercise().add(exercise13);
            routineDay3.getExercise().add(exercise14);

            exercise9 = service.save(exercise9).get();
            exercise10 = service.save(exercise10).get();
            exercise11 = service.save(exercise11).get();
            exercise12 = service.save(exercise12).get();
            exercise13 = service.save(exercise13).get();
            exercise14 = service.save(exercise14).get();

            exercise9.getExerciseSpecified().add(exerciseSpecified9);
            exercise10.getExerciseSpecified().add(exerciseSpecified10);
            exercise11.getExerciseSpecified().add(exerciseSpecified11);
            exercise12.getExerciseSpecified().add(exerciseSpecified12);
            exercise13.getExerciseSpecified().add(exerciseSpecified13);
            exercise14.getExerciseSpecified().add(exerciseSpecified14);

            serviceSpecified.update(exerciseSpecified9);
            serviceSpecified.update(exerciseSpecified10);
            serviceSpecified.update(exerciseSpecified11);
            serviceSpecified.update(exerciseSpecified12);
            serviceSpecified.update(exerciseSpecified13);
            serviceSpecified.update(exerciseSpecified14);

            // routineDay 4

            exercise15.getRoutineDay().add(routineDay4);
            exercise16.getRoutineDay().add(routineDay4);
            exercise17.getRoutineDay().add(routineDay4);
            exercise18.getRoutineDay().add(routineDay4);
            exercise19.getRoutineDay().add(routineDay4);
            exercise20.getRoutineDay().add(routineDay4);

            routineDay4 = routineDayService.save(routineDay4).get();

            routineDay4.getExercise().add(exercise15);
            routineDay4.getExercise().add(exercise16);
            routineDay4.getExercise().add(exercise17);
            routineDay4.getExercise().add(exercise18);
            routineDay4.getExercise().add(exercise19);
            routineDay4.getExercise().add(exercise20);

            exercise15 = service.save(exercise15).get();
            exercise16 = service.save(exercise16).get();
            exercise17 = service.save(exercise17).get();
            exercise18 = service.save(exercise18).get();
            exercise19 = service.save(exercise19).get();
            exercise20 = service.save(exercise20).get();

            exercise15.getExerciseSpecified().add(exerciseSpecified15);
            exercise16.getExerciseSpecified().add(exerciseSpecified16);
            exercise17.getExerciseSpecified().add(exerciseSpecified17);
            exercise18.getExerciseSpecified().add(exerciseSpecified18);
            exercise19.getExerciseSpecified().add(exerciseSpecified19);
            exercise20.getExerciseSpecified().add(exerciseSpecified20);

            serviceSpecified.update(exerciseSpecified15);
            serviceSpecified.update(exerciseSpecified16);
            serviceSpecified.update(exerciseSpecified17);
            serviceSpecified.update(exerciseSpecified18);
            serviceSpecified.update(exerciseSpecified19);
            serviceSpecified.update(exerciseSpecified20);


        };
    }
}