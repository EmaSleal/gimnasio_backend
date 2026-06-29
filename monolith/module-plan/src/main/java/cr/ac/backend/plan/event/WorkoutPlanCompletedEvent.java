package cr.ac.backend.plan.event;

public record WorkoutPlanCompletedEvent(Long planId, Long userId) {}
