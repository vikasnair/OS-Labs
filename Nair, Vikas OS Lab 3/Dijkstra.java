import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Dijkstra {

	// the main run of the banking algorithm
	// run until no more tasks remaining (aborted, terminated)
	// get the current activity for each process, and handle according to 'state'
	// grant / deny requests, releases according to delay and SAFETY

	static Bank run(Bank b, boolean verbose) {
		List<Task> blocked, current;
		int cycle, remainingTasks;

		blocked = new ArrayList<>();
		current = new ArrayList<>();
		cycle = 0;
		remainingTasks = b.tasks.length;

		while (remainingTasks > 0) {
			
			// TODO: add blocked tasks to current tasks first

			if (verbose)
				System.out.println("\nCYCLE " + cycle + "\n=======");

			int[] released = new int[b.resources.length];

			// add blocked items first, then the rest

			for (int i = 0; i < blocked.size(); i++)
				current.add(blocked.get(i));

			for (int i = 0; i < b.tasks.length; i++)
				if (!current.contains(b.tasks[i]))
					current.add(b.tasks[i]);

			// loop through each activity

			for (int i = 0; i < current.size(); i++) {
				// System.out.println(current.get(i));
				Activity currentActivity = current.get(i).activities.get(current.get(i).activityIndex);

				if (verbose)
					System.out.println(currentActivity + "BANK RESOURCES: " + java.util.Arrays.toString(b.resources));

				// filter out initialize or terminate activities, handle requests, release, terminate

				if (currentActivity.s == State.INIT) {
					if (current.get(i).claims[currentActivity.resource] > b.resources[currentActivity.resource]) {
						if (verbose)
							System.out.println("CLAIMS TOO MUCH, ABORTING");

						current.get(i).aborted = true;
						remainingTasks--;
					}

					currentActivity.t.activityIndex++;
					continue;
				} else if (currentActivity.t.aborted) {
					if (verbose)
						System.out.println("SKIPPING");

					continue;
				} else if (currentActivity.s == State.REQ) {
					if (currentActivity.delay != 0) {
						if (verbose)
							System.out.println("DELAYED DURING REQ");

						currentActivity.delay--;
					} else {
						if (!isSafe(b, currentActivity)) {
							if (verbose)
								System.out.println("NOT SAFE, BLOCKED");

							currentActivity.t.wait++;

							if (!blocked.contains(currentActivity.t))
								blocked.add(current.get(i));
						} else if (currentActivity.request > currentActivity.t.allocated[currentActivity.resource]) {
							if (verbose)
								System.out.println("DENIED REQ, ABORTED");
							
							currentActivity.t.aborted = true;
							remainingTasks--;
							currentActivity.s = State.END;

							for (int j = 0; j < currentActivity.t.allocated.length; j++) {
								b.resources[j] += (currentActivity.t.claims[j] - currentActivity.t.allocated[j]);
								currentActivity.t.allocated[j] = currentActivity.t.claims[j];
							}
						} else {
							if (verbose)
								System.out.println("GRANTED REQ");

							if (blocked.contains(currentActivity.t))
								blocked.remove(currentActivity.t);

							b.resources[currentActivity.resource] -= currentActivity.request;
							currentActivity.t.allocated[currentActivity.resource] -= currentActivity.request;
							currentActivity.t.activityIndex++;	
						}
					}
				} else if (currentActivity.s == State.REL) {
					if (currentActivity.delay == 0) {
						if (verbose)
							System.out.println("RELEASING");

						released[currentActivity.resource] += currentActivity.release;
						currentActivity.t.allocated[currentActivity.resource] += currentActivity.release;
						currentActivity.t.activityIndex++;
					} else {
						if (verbose)
							System.out.println("DELAYED DURING REL");

						currentActivity.delay--;
					}
				} else if (currentActivity.s == State.TERM) {
					if (currentActivity.delay == 0) {
						if (verbose)
							System.out.println("TERMINATED");

						for (int j = 0; j < currentActivity.t.allocated.length; j++) {
							b.resources[j] += (currentActivity.t.claims[j] - currentActivity.t.allocated[j]);
							currentActivity.t.allocated[j] = currentActivity.t.claims[j];
						}

						currentActivity.t.total = cycle;
						remainingTasks--;
						currentActivity.s = State.END;
					} else {
						if (verbose)
							System.out.println("DELAYED DURING TERM");

						currentActivity.delay--;
					}
				} else {
					if (verbose)
						System.out.println(currentActivity.s + " SKIPPING");

					continue;
				}
			}

			// add the released resources for the next cycle

			for (int i = 0; i < released.length; i++)
				b.resources[i] += released[i];
			
			current.clear();
			cycle++;
		}

		return b;
	}

	// recursive method to check if a state is safe
	// base case: no tasks available (must be safe)
	// else, go through and check if any process can be completed
	// if one can be terminated, then complete the task, remove it, restore resources, and recurse
	// otherwise, if none can be completed then it is not safe

	static boolean isSafe(List<Task> tasks, int[] resources, Activity currentActivity) {
		if (tasks.isEmpty())
			return true;

		for (int i = 0; i < tasks.size(); i++) {
			Task t = tasks.get(i);

			boolean isSafe = true;

			for (int j = 0; j < resources.length; j++) {
				int available = resources[j];
				int needed = t.allocated[j];

				// System.out.println("AVAILABLE R: " + available);
				// System.out.println("TASK " + tasks.get(i).id + " NEEDED R: " + needed);

				if (needed > available)
					isSafe = false;

					// System.out.println("TASK " + tasks.get(i).id + " COMPLETES.");
					// System.out.println("BANK: " + Arrays.toString(resources));
			}

			if (isSafe) {
				for (int j = 0; j < t.allocated.length; j++)
					resources[j] += (t.claims[j] - t.allocated[j]);

				tasks.remove(i);
				return isSafe(tasks, resources, currentActivity);
			}
			

			
		}

		return false;
	}

	// convenience method, init data structures and decrement current request from resources

	static boolean isSafe(Bank b, Activity currentActivity) {
		Task[] tasks = new Task[b.tasks.length];

		for (int i = 0; i < b.tasks.length; i++)
			tasks[i] = new Task(b.tasks[i]);

		List<Task> tasksAsList = new ArrayList<>(Arrays.asList(tasks));

		for (int i = 0; i < tasksAsList.size(); i++)
			if (tasksAsList.get(i).aborted)
				tasksAsList.remove(i--);

		int[] resources = Arrays.copyOf(b.resources, b.resources.length);

		// System.out.println("\n\n CHECKING SAFE STATE \n\n");
		// System.out.println(Arrays.toString(tasks));
		// System.out.println(Arrays.toString(resources));
		// System.out.println(currentActivity);

		resources[currentActivity.resource] -= currentActivity.request;
		tasks[currentActivity.t.id].allocated[currentActivity.resource] -= currentActivity.request;

		return isSafe(tasksAsList, resources, currentActivity);
	}
}