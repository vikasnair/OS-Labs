// Vikas was here!
// 1 Mar 2018

import java.util.Collections;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public class RoundRobin {
	static void schedule(boolean verbose, int quantum, int totalRun, int totalIO, int cycle, int processesRan, int numOfProcesses,
		Process currentProcess, List<Process> ready, List<Process> unstarted, List<Process> blocked, List<Process> finished) {
		
		Main.resetBurstIndex();

		while (processesRan < numOfProcesses) {

			for (int i = 0; i < unstarted.size(); i++)
				if (unstarted.get(i).arrival == cycle)
					ready.add(unstarted.remove(i--));

			// Collections.sort(ready);
			// Main.sortByPriority(ready);

			if (currentProcess == null && !ready.isEmpty()) {

				// if (cycle == 68 || cycle == 69 || cycle == 70) {
				// 	System.out.println("ready:" + ready);
				// 	System.out.println("process: " + ready.get(0));
				// }

				currentProcess = ready.remove(0);

				// Collections.sort(ready);



				if (currentProcess.burst == 0)
					// currentProcess.burst = 2;
					currentProcess.randomBurstTime();
			}

			// TODO: log (if verbose flag enabled)

			// Main.log(cycle, ready, unstarted, blocked, finished, currentProcess);
			if (verbose)
				Main.verboseLog(cycle + 1, ready, unstarted, blocked, finished, currentProcess);

			if (currentProcess != null) {
				currentProcess.run();
				totalRun++;
			}

			quantum--;

			// wait on ready processes

			for (Process p : ready)
				p.rest();

			// go through blocked

			if (!blocked.isEmpty()) {
				totalIO++;

				for (int i = 0; i < blocked.size(); i++) {
					blocked.get(i).block();

					if (blocked.get(i).block == 0)
						ready.add(blocked.remove(i--));
				}
			}

			if (cycle == 68 || cycle == 69 || cycle == 70)
				System.out.println("Q:" + quantum);

			// if (currentProcess != null) {
			// 	if (currentProcess.remaining == 0) {
			// 		currentProcess.finished = cycle + 1;
			// 		finished.add(currentProcess);
			// 		processesRan++;
			// 		currentProcess = null;
			// 	} else if (currentProcess.burst <= 0) { // if the burst runs out but hasn't finished run, block process for IO
			// 		currentProcess.randomBlockTime();
			// 		blocked.add(currentProcess);
			// 		currentProcess = null;
			// 	}
			// }


			// if (quantum == 0) {
			// 	if (currentProcess != null) {
			// 		ready.add(currentProcess);
			// 		currentProcess = null;
			// 	}

			// 	quantum = 2;
			// }

			// tihs one  works better idk why

			if (quantum == 0) {
				if (currentProcess != null) {
					if (currentProcess.remaining == 0) {
						currentProcess.finished = cycle + 1;
						finished.add(currentProcess);
						processesRan++;
					} else if (currentProcess.burst <= 0) { // if the burst runs out but hasn't finished run, block process for IO
						currentProcess.randomBlockTime();
						blocked.add(currentProcess);
					} else {
						ready.add(currentProcess);
					}

					currentProcess = null;
				}

				quantum = 2;
			} else {
				if (currentProcess != null) {
					if (currentProcess.remaining == 0) {
						currentProcess.finished = cycle + 1;
						finished.add(currentProcess);
						processesRan++;
						currentProcess = null;
						quantum = 2;
					} else if (currentProcess.burst <= 0) {
						currentProcess.randomBlockTime();
						blocked.add(currentProcess);
						currentProcess = null;
						quantum = 2;
					}
				}

				// else {
				// 	quantum = 2;
				// }
			}

			cycle++;
		}

		System.out.println("The scheduling algorithm used was RR.\n");

		Main.report(cycle, totalRun, totalIO, numOfProcesses, finished);
	}
}