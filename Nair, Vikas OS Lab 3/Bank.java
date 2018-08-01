// keep all tasks and resources here

public class Bank {
	Task[] tasks;
	int[] resources;

	Bank(Task[] tasks, int[] resources) {
		this.tasks = tasks;
		this.resources = resources;
	}

	@Override
	public String toString() {
		return "Tasks:\n" + java.util.Arrays.toString(tasks) + "\nResources:\n" + java.util.Arrays.toString(resources);
	}
}