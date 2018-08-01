import java.util.Arrays;

// representing a single task, which has a list of activities

public class Task implements Comparable<Task> {
	int id, total, wait, activityIndex;
	double waitRatio;
	boolean aborted;
	int[] allocated, claims;
	java.util.List<Activity> activities;

	Task(int id, int numOfResources) {
		this.id = id;
		this.allocated = new int[numOfResources];
		this.claims = new int[numOfResources];
		this.activities = new java.util.ArrayList<>();
	}

	Task(int id, int total, int wait, int activityIndex, double waitRatio, boolean aborted, int[] allocated, int[] claims) {
		this.id = id;
		this.total = total;
		this.wait = wait;
		this.activityIndex = activityIndex;
		this.waitRatio = waitRatio;
		this.aborted = aborted;
		this.allocated = allocated;
		this.claims = claims;
	}

	Task(Task t) {
		this(t.id, t.total, t.wait, t.activityIndex, t.waitRatio, t.aborted, Arrays.copyOf(t.allocated, t.allocated.length), Arrays.copyOf(t.claims, t.claims.length));
	}

	@Override
	public int compareTo(Task t) {
		return this.id - t.id;
	}

	@Override
	public String toString() {
		return "id: " + id + ", total: " + total + ", wait: " + wait + ", waitRatio: " + waitRatio + ", aborted: " + aborted
		+ ", allocated: " + java.util.Arrays.toString(allocated) + ", claims: " + java.util.Arrays.toString(claims); // activities?
	}
}