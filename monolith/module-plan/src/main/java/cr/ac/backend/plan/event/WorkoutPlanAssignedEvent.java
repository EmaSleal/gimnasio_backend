package cr.ac.backend.plan.event;

public record WorkoutPlanAssignedEvent(Long planId, Long userId, Long trainerId) {}
