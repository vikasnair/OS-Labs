from Frame import Frame
from Process import Process

class Pager:
	def __init__(self, page_size, frame_count):
		self.page_size = page_size
		self.frame_count = frame_count
		self.frames = [ Frame(i) for i in range(frame_count) ]

	def check_page_fault(self, page_id, pid):
		for frame in self.frames:
			if frame.page_id == page_id and frame.pid == pid - 1:
				return frame

	def get_page_from(self, process):
		return process.next_reference // self.page_size

	def get_free_frame(self):
		for frame in reversed(self.frames):
			if not frame.used:
				return frame

	def evict(self, index, current_time, processes, selected_algorithm):
		frame = self.frames.pop(index)
		process = processes[frame.pid]
		process.residency += (current_time - frame.time_inserted)
		process.eviction_count += 1

		return frame

	def insert(self, frame, page_id, pid, current_time, processes):
		process = processes[pid - 1]
		process.fault_count += 1
		frame.pid = pid - 1
		frame.page_id = page_id
		frame.used = True
		frame.time_inserted = current_time
		self.frames.append(frame)

	def RAND(self):
		return self.frame_count - 1 - (self.random % self.frame_count)

	def evict_and_insert(self, page_id, pid, current_time, processes, selected_algorithm, verbose):
		if selected_algorithm == 'random':
			frame = self.evict(self.RAND(), current_time, processes, selected_algorithm)
		else:
			frame = self.evict(0, current_time, processes, selected_algorithm)

		if verbose:
			print('Fault, evicting page', page_id, 'from frame', frame.fid)

			if (hasattr(self, 'random')):
				print(pid, 'uses random number:', self.random)
		self.insert(frame, page_id, pid, current_time, processes)