import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW );
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void addSubTasksId(int id) {
        if (!subTasksIds.contains(id)) {
            this.subTasksIds.add(id);
        }


    }

    public void removeSubTasksId(int id) {
        if (subTasksIds.contains(id)) {
            for (int i = 0; i < subTasksIds.size(); i++) {
                if (subTasksIds.get(i) == id) {
                    subTasksIds.remove(i);
                }
            }
        }
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "{name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subTasksIds=" + subTasksIds +
                '}';
    }
}
