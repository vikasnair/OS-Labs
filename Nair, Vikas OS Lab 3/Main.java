import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		boolean verbose = args.length > 1 && args[1].equals("--verbose");
		System.out.println("\n//// FIFO");
		report(FIFO.run(makeBank(readFile(args[0])), verbose));
		System.out.println("\n//// Dijkstra");
		report(Dijkstra.run(makeBank(readFile(args[0])), verbose));
	}

	static Scanner readFile(String fileName) {
		try {
			return new Scanner(new File(fileName));
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// parse from the file, init data structures and return a stocked bank

	static Bank makeBank(Scanner fileReader) {
		int numOfTasks = fileReader.nextInt();
		int numOfResources = fileReader.nextInt();
		Task[] tasks = new Task[numOfTasks];
		int[] resources = new int[numOfResources];

		for (int i = 0; i < numOfTasks; i++)
			tasks[i] = new Task(i, numOfResources);

		for (int i = 0; i < numOfResources; i++)
			resources[i] = fileReader.nextInt();

		fileReader.nextLine();

		while (fileReader.hasNextLine()) {
			String activityState = fileReader.next().trim();
			int taskID = fileReader.nextInt() - 1;
			int delay = fileReader.nextInt();
			int resource = fileReader.nextInt() - 1;
			int amount = fileReader.nextInt();
			Activity a = new Activity(tasks[taskID], delay, resource);

			// TODO: add marker variable for state

			switch (activityState) {
				case "initiate":
					a.s = State.INIT;
					a.claim = amount;
					tasks[taskID].allocated[resource] = amount;
					tasks[taskID].claims[resource] = amount;
					break;
				case "request":
					a.s = State.REQ;
					a.request = amount;
					break;
				case "release":
					a.s = State.REL;
					a.release = amount;
					break;
				case "terminate":
					a.s = State.TERM;
					break;
			}

			tasks[taskID].activities.add(a);

			fileReader.nextLine();
		}

		return new Bank(tasks, resources);
	}

	// end method, report on the bank after algo run

	static void report(Bank b) {
		System.out.println("\nREPORT\n======\n");

		int total = 0;
		int wait = 0;

		for (int i = 0; i < b.tasks.length; i++) {
			b.tasks[i].waitRatio = ((double) b.tasks[i].wait / b.tasks[i].total) * 100;

			if (!b.tasks[i].aborted) {
				total += b.tasks[i].total;
				wait += b.tasks[i].wait;
			}

			System.out.println(b.tasks[i]);
		}

		System.out.println("\ntotal: " + total + ", wait: " + wait + ", waitRatio: " + ((double) wait / total) * 100);
	}
}