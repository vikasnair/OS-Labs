import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class FIFO {

	// the main run of the fifo algorithm
	// run until no more tasks remaining (aborted, terminated)
	// get the current activity for each process, and handle according to 'state'
	// grant / deny requests, releases according to delay

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
				Activity currentActivity = current.get(i).activities.get(current.get(i).activityIndex);

				if (verbose)
					System.out.println(currentActivity + "BANK RESOURCES: " + java.util.Arrays.toString(b.resources));

				// filter out initialize or terminate activities, handle requests, release, terminate

				if (currentActivity.s == State.INIT || currentActivity.t.aborted) {
					if (verbose)
						System.out.println("SKIPPING");

					currentActivity.t.activityIndex++;
					continue;
				} else if (currentActivity.s == State.REQ) {
					if (currentActivity.request <= b.resources[currentActivity.resource]) {
						if (currentActivity.delay == 0) {
							if (verbose)
								System.out.println("GRANTED REQ");

							if (blocked.contains(currentActivity.t))
								blocked.remove(currentActivity.t);

							b.resources[currentActivity.resource] -= currentActivity.request;
							currentActivity.t.allocated[currentActivity.resource] -= currentActivity.request;
							currentActivity.t.activityIndex++;
						} else {
							if (verbose)
								System.out.println("DELAYED DURING REQ");

							currentActivity.delay--;
						}
					} else {
						if (verbose)
							System.out.println("DENIED REQ, BLOCKED");

						currentActivity.t.wait++;

						if (!blocked.contains(currentActivity.t))
							blocked.add(current.get(i));
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

			// deadlocked state, abort tasks

			if (blocked.size() == remainingTasks) {
				Collections.sort(blocked);

				for (int i = 0; i < blocked.size(); i++) {
					Activity currentActivity = blocked.get(i).activities.get(blocked.get(i).activityIndex);

					if (currentActivity.resource >= 0 && currentActivity.request > b.resources[currentActivity.resource]) {
						if (verbose)
							System.out.println("ABORTING " + currentActivity.t.id); 
						
						currentActivity.t.aborted = true;
						remainingTasks--;
						currentActivity.s = State.END;

						for (int j = 0; j < currentActivity.t.allocated.length; j++) {
							b.resources[j] += (currentActivity.t.claims[j] - currentActivity.t.allocated[j]);
							currentActivity.t.allocated[j] = currentActivity.t.claims[j];
						}
					}
				}
			}

			current.clear();
			cycle++;
		}

		return b;
	}
}