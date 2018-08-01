// Vikas was here!
// 27 Feb 2018

public class Process implements Comparable<Process> {
	int id, arrival, maxBurst, total, remaining, maxBlock, burst, block, totalBlock, totalWait, finished, priority;

	Process(int id, int arrival, int maxBurst, int total, int maxBlock) {
		this.id = id;
		this.arrival = arrival;
		this.maxBurst = maxBurst;
		this.total = total;
		this.remaining = total;
		this.maxBlock = maxBlock;
		this.totalWait = 0;
	}

	void randomBurstTime() {
		this.burst = Math.min(Main.randomOS(maxBurst), remaining);
	}

	// void randomBurstTime(int quantum) {
	// 	this.burst = quantum;
	// }

	void randomBlockTime() {
		this.block = Main.randomOS(maxBlock);
	}

	void run() {
		remaining--;
		burst--;
	}

	void rest() {
		totalWait++;
	}

	void block() {
		block--;
		totalBlock++;
	}

	@Override
	public String toString() {
		return "\t(A, B, C, IO) = (" + arrival + "," + maxBurst + "," + total + "," + maxBlock + ")"
		+ "\n\tFinishing time: " + finished + "\n\tTurnaround time: " + (finished - arrival) + "\n\tI/O time: " + totalBlock + "\n\tWaiting time: " + totalWait
		+ "\nP:" + priority;
		// + "\n\tID: " + id + "\n\tRemaining CPU: " + remaining + "\n\tCurrent CPU burst: " + burst + "\n\tCurrent Block: " + block;
	}

	@Override
	public int compareTo(Process p) {
		return this.arrival - p.arrival;
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof Process) {
			return this.id == ((Process) object).id;
		}

		return false;
	}
}