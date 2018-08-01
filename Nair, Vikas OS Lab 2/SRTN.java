// Vikas was here!
// 2 Mar 2018

import java.util.Queue;
import java.util.List;

public class SRTN {
	static void schedule(boolean verbose, int totalRun, int totalIO, int cycle, int processesRan, int numOfProcesses,
		Process currentProcess, List<Process> ready, List<Process> unstarted, List<Process> blocked, List<Process> finished) {
		
		Main.resetBurstIndex();

		while (processesRan < numOfProcesses) {

			for (int i = 0; i < unstarted.size(); i++)
				if (unstarted.get(i).arrival == cycle)
					ready.add(unstarted.remove(i--));

			// create a process if none current

			if (!ready.isEmpty()) {
				sortByRemaining(ready);

				if (currentProcess != null) {
					if (currentProcess.remaining > ready.get(0).remaining) {
						Process oldProcess = currentProcess;
						currentProcess = ready.remove(0);
						ready.add(oldProcess);
					}
				} else {
					currentProcess = ready.remove(0);
				}

				sortByRemaining(ready);

				// tie breaker

				if (!ready.isEmpty() && currentProcess != ready.get(0)) {
					if (currentProcess.remaining == ready.get(0).remaining && currentProcess.maxBurst > ready.get(0).maxBurst) {
						Process oldProcess = currentProcess;
						currentProcess = ready.remove(0);
						ready.add(oldProcess);
					}
				}

				if (currentProcess.burst == 0)
					currentProcess.randomBurstTime();

				sortByRemaining(ready);
			}

			// run current process (if any)

			if (currentProcess != null) {
				currentProcess.run();
				totalRun++;
			}

			// TODO: log (if verbose flag enabled)

			// Main.log(cycle, ready, unstarted, blocked, finished, currentProcess);
			if (verbose)
				Main.verboseLog(cycle + 1, ready, unstarted, blocked, finished, currentProcess);

			// wait on ready processes

			for (Process p : ready)
				p.rest();

			// go through blocked

			if (!blocked.isEmpty()) {
				totalIO++;

				// decrement remaining block time on each
				// if available then set to current process

				for (int i = 0; i < blocked.size(); i++) {
					blocked.get(i).block();

					if (blocked.get(i).block == 0)
						ready.add(blocked.remove(i--));
				}
			}

			// deal with current process (finish or block)

			if (currentProcess != null) {
				if (currentProcess.remaining == 0) { // process finished
					currentProcess.finished = cycle + 1;
					finished.add(currentProcess);
					currentProcess = null;
					processesRan++;
				} else if (currentProcess.burst == 0) { // if the burst runs out but hasn't finished run, block process for IO
					currentProcess.randomBlockTime();
					blocked.add(currentProcess);
					currentProcess = null;
				}
			}

			cycle++;
		}

		System.out.println("The scheduling algorithm used was SRTN.\n");

		Main.report(cycle, totalRun, totalIO, numOfProcesses, finished);
	}

	static void sortByRemaining(List<Process> processes) {
		for (int i = 0; i < processes.size(); i++) {
			for (int j = i + 1; j < processes.size(); j++) {
				if (processes.get(j).remaining < processes.get(i).remaining) {
					Process temp = processes.get(i);
					processes.set(i, processes.get(j));
					processes.set(j, temp);
				}
			}
		}
	}
}