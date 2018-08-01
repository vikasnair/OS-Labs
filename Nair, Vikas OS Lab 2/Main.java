// Vikas was here!
// 27 Feb 2018

import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
	static List<Integer> burstTimes;
	static List<Process> allProcesses, sortedProcesses, unstartedProcesses;
	static List<Process> readyProcesses;
	static int burstIndex = 0;

	public static void main(String[] args) { // TODO: accept verbose flag

		// init burstTimes, processes (unsorted + sorted) w/ readFile (pass in args)

		burstTimes = readIntegers(readFile(args[0]));
		allProcesses = readProcesses(readFile(args[1]));

		for (int i = 0; i < allProcesses.size(); i++)
			allProcesses.get(i).priority = i;

		sortedProcesses = new ArrayList<>(allProcesses);

		Collections.sort(sortedProcesses);

		// init process tables

		initializeProcessTables();

		// init starting scheduler vars

		int quantum = 2;
		int totalRun = 0;
		int totalIO = 0;
		int cycle = 0;
		int processesRan = 0;
		int numOfProcesses = allProcesses.size();
		Process currentProcess = null;

		boolean verbose = args.length > 2 && "--verbose".equals(args[2]);

		System.out.print("Pick an algo: 1) Uniprogram 2) FCFS 3) Round Robin 4) SRTN: ");

		switch (new Scanner(System.in).nextInt()) {
			case 1:
				Uniprogram.schedule(verbose, totalRun, totalIO, cycle, processesRan, numOfProcesses,
					currentProcess, new ArrayList<>(readyProcesses), new ArrayList<>(unstartedProcesses), new ArrayList<Process>(), new ArrayList<Process>());
				break;

			case 2:
				FCFS.schedule(verbose, totalRun, totalIO, cycle, processesRan, numOfProcesses,
					currentProcess, new ArrayList<>(readyProcesses), new ArrayList<>(unstartedProcesses), new ArrayList<Process>(), new ArrayList<Process>());
				break;

			case 3:
				RoundRobin.schedule(verbose, quantum, totalRun, totalIO, cycle, processesRan, numOfProcesses,
					currentProcess, new ArrayList<>(readyProcesses), new ArrayList<>(unstartedProcesses), new ArrayList<Process>(), new ArrayList<Process>());
				break;
			case 4:
				SRTN.schedule(verbose, totalRun, totalIO, cycle, processesRan, numOfProcesses,
					currentProcess, new ArrayList<>(readyProcesses), new ArrayList<>(unstartedProcesses), new ArrayList<Process>(), new ArrayList<Process>());
				break;

			default: break;
		}		
	}

	static int randomOS(int u) {
		return 1 + ((burstTimes.get(burstIndex++) % u));
	}

	static void resetBurstIndex() {
		burstIndex = 0;
	}

	static Scanner readFile(String fileName) {
		try {
			return new Scanner(new File(fileName));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	static List<Integer> readIntegers(Scanner fileReader) {
		List<Integer> integers = new ArrayList<>();
		while (fileReader.hasNextInt())
				integers.add(fileReader.nextInt());
		return integers;
	}

	static List<Process> readProcesses(Scanner fileReader) {
		List<Process> processes = new ArrayList<>();
		int numOfProcesses = fileReader.nextInt();
		int id = 0;

		while (fileReader.hasNextInt())
			processes.add(new Process(id++, fileReader.nextInt(), fileReader.nextInt(), fileReader.nextInt(), fileReader.nextInt()));

		return processes;
	}

	static void initializeProcessTables() {
		// readyProcesses = new PriorityQueue<>(new Comparator<Process>() {
		// 	public int compare(Process first, Process next) {
		// 		return first.arrival - next.arrival;
		// 	}
		// });

		readyProcesses = new ArrayList<>();
		unstartedProcesses = new ArrayList<>();

		for (Process p : sortedProcesses) {
			if (p.arrival == 0)
				readyProcesses.add(p);
			else
				unstartedProcesses.add(p);
		}
	}

	// static void sortByPriority(List<Process> processes) {
	// 	for (int i = 0; i < processes.size(); i++) {
	// 		for (int j = i + 1; j < processes.size(); j++) {
	// 			if (processes.get(i).arrival > processes.get(j).arrival
	// 				&& processes.get(i).priority > processes.get(j).priority) {
	// 				Process temp = processes.get(i);
	// 				processes.set(i, processes.get(j));
	// 				processes.set(j, temp);
	// 			}
	// 		}
	// 	}
	// }

	public static void log(int cycle, List<Process> ready, List<Process> unstarted, List<Process> blocked, List<Process> finished, Process running) {
		System.out.println("Cycle: " + cycle);
		System.out.println("\nReady");
		
		for (Process p : ready)
			System.out.println(p + "\n");

		System.out.println("\nBlocked");

		for (Process p : blocked)
			System.out.println(p + "\n");

		System.out.println("\nUnstarted");

		for (Process p : unstarted)
			System.out.println(p + "\n");

		System.out.println("\nRunning");

		System.out.println(running + "\n");

		System.out.println("\nFinished");

		for (Process p : finished)
			System.out.println(p + "\n");

		System.out.println("\n");
	}

	static void verboseLog(int cycle, List<Process> ready, List<Process> unstarted, List<Process> blocked, List<Process> finished, Process running) {
		System.out.print("Before cycle\t" + cycle + ":");

		for (Process p : sortedProcesses) {
			if (ready.contains(p))
				System.out.print("\tready: 0");
			else if (unstarted.contains(p))
				System.out.print("\tunstarted: 0");
			else if (blocked.contains(p))
				System.out.print("\tblocked: " + p.block);
			else if (finished.contains(p))
				System.out.print("\tterminated: 0");
			else if (p == running)
				System.out.print("\trunning: " + p.burst);
		}

		System.out.println();
	}

	static void report(int cycle, int totalRun, int totalIO, int numOfProcesses, List<Process> finished) {
		int totalTurnaround = 0;
		int totalWaiting = 0;

		for (int i = 0; i < finished.size(); i++) {
			System.out.println("Process " + i + "\n" + finished.get(i));

			totalTurnaround += (finished.get(i).finished - finished.get(i).arrival);
			totalWaiting += finished.get(i).totalWait;
		}

		System.out.println();

		summarize(cycle, totalRun, totalIO, totalTurnaround, totalWaiting, numOfProcesses);
	}

	static void summarize(int cycle, int totalRun, int totalIO, int totalTurnaround, int totalWaiting, int numOfProcesses) {
		System.out.println("Summary Data:");
		System.out.println("\tFinishing time: " + cycle);
		System.out.println("\tCPU Utilization : " + (double) totalRun / cycle);
		System.out.println("\tI/O Utilization : " + (double) totalIO / cycle);
		System.out.println("\tThroughput : " + (double) (100 * numOfProcesses) / cycle);
		System.out.println("\tAverage turnaround time : " + (double) totalTurnaround / numOfProcesses);
		System.out.println("\tAverage waiting time : " + (double) totalWaiting / numOfProcesses);
	}
}