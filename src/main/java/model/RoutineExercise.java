package model;

public class RoutineExercise extends BaseModel {

    private int routineId;
    private int exerciseId;
    private String exerciseName;

    public RoutineExercise() {
    }

    public RoutineExercise(int id, int routineId, int exerciseId, String exerciseName) {
        this.id = id;
        this.routineId = routineId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
    }

    public int getRoutineId() {
        return routineId;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
}