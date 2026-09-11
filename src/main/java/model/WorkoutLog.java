package model;

public class WorkoutLog extends BaseModel {

    private int routineId;
    private int exerciseId;
    private String exerciseName;
    private String routineName;
    private String date;
    private double weight;
    private int reps;

    public WorkoutLog() {
    }

    public WorkoutLog(int id, int routineId, int exerciseId, String date, double weight, int reps) {
        this.id = id;
        this.routineId = routineId;
        this.exerciseId = exerciseId;
        this.date = date;
        this.weight = weight;
        this.reps = reps;
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

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }
}