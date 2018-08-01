// Vikas was here!
// 27 Feb 2018

import java.util.Queue;
import java.util.List;

public class Uniprogram {
	static void schedule(boolean verbose, int totalRun, int totalIO, int cycle, int processesRan, int numOfProcesses,
		Process currentProcess, List<Process> ready, List<Process> unstarted, List<Process> blocked, List<Process> finished) {
		
		Main.resetBurstIndex();

		while (processesRan < numOfProcesses) {

			for (int i = 0; i < unstarted.size(); i++)
				if (unstarted.get(i).arrival == cycle)
					ready.add(unstarted.remove(i--));

			// create a process if no current + none blocked

			if (currentProcess == null && blocked.isEmpty() && !ready.isEmpty()) {
				currentProcess = ready.remove(0);
				currentProcess.randomBurstTime();
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

					if (blocked.get(i).block == 0) { // if it's done being blocked, set to current process
						currentProcess = blocked.remove(i--);
						currentProcess.randomBurstTime();
					}
				}
			}

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

		System.out.println("The scheduling algorithm used was Uniprocessor.\n");

		Main.report(cycle, totalRun, totalIO, numOfProcesses, finished);
	}
	
}