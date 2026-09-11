package model;

public class Exercise extends BaseModel {

    private String name;
    private String muscle;

    public Exercise() {
    }

    public Exercise(int id, String name, String muscle) {
        this.id = id;
        this.name = name;
        this.muscle = muscle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    @Override
    public String toString() {
        return name;
    }
}