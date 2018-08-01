import sys
from Process import Process
from Pager import Pager

class Driver:
	def __init__(self, args, file_name):
		self.machine_size = int(args[0])
		self.page_size = int(args[1])
		self.process_size = int(args[2])
		self.job_mix = int(args[3])
		self.reference_count = int(args[4])
		self.selected_algorithm = args[5].lower()
		self.verbose = int(args[6])
		self.quantum = 3
		self.random_numbers = open(file_name, 'r').read().split('\n')
		self.processes = {
			1 : [ Process(1, self.process_size, self.reference_count, 1.0, 0.0, 0.0) ],
			2 : [ Process(i + 1, self.process_size, self.reference_count, 1.0, 0.0, 0.0) for i in range(4) ],
			3 : [ Process(i + 1, self.process_size, self.reference_count, 0.0, 0.0, 0.0) for i in range(4) ],
			4 : [
					Process(1, self.process_size, self.reference_count, 0.75, 0.25, 0.0),
					Process(2, self.process_size, self.reference_count, 0.75, 0.0, 0.25),
					Process(3, self.process_size, self.reference_count, 0.75, 0.125, 0.125),
					Process(4, self.process_size, self.reference_count, 0.5, 0.125, 0.125)
				]
		}[self.job_mix]

		# print('\nINIT\n', self.processes, '\n')
		print()

		self.ready = self.processes[:]

	# calculate and set the reference for the next word

	def set_next_reference(self, process):
		current_reference = process.next_reference
		random_number = self.get_random_number()
		probability =  random_number / (sys.maxsize + 1.0)

		if probability < process.a:
			process.next_reference = (current_reference + 1) % process.size
		elif probability < process.a + process.b:
			process.next_reference = (current_reference - 5 + process.size) % process.size
		elif probability < process.a + process.b + process.c:
			process.next_reference = (current_reference + 4) % process.size
		else:
			random_number = self.get_random_number()
			process.next_reference = (random_number + process_size) % process.size

		if self.verbose:
			print(process.pid, 'uses random number:', random_number)

	def get_random_number(self):
		return int(self.random_numbers.pop(0))

	# main run

	def run(self):

		# init pager, clock

		p = Pager(self.page_size, (self.machine_size // self.page_size))
		current_time = 0

		# run all remaining ready processes

		while self.ready:
			next_process = self.ready.pop(0)

			# round-robin style

			for i in range(self.quantum):

				# check page fault

				page_id = p.get_page_from(next_process)
				frame = p.check_page_fault(page_id, next_process.pid)

				if self.verbose:
					print(next_process.pid, 'references word', next_process.next_reference, '(page', str(page_id) + ') at time', (current_time + 1), end = ': ');

				# if hit, cycle frames

				if frame:
					if self.verbose:
						print('Hit in frame', frame.fid)

					if self.selected_algorithm == 'lru' or self.selected_algorithm == 'fifo': # and fifo?
						p.frames.remove(frame)
						p.frames.append(frame)

				# if miss (fault), then look for a free frame

				else:
					frame = p.get_free_frame()

					# if there is a free frame, insert and cycle

					if frame:
						if self.verbose:
							print('Fault, using free frame', frame.fid)
						p.frames.remove(frame)
						p.insert(frame, page_id, next_process.pid, current_time, self.processes)

					# if there is no free frame, evict a current one, insert, and cycle

					else:
						if self.selected_algorithm == 'random':
							p.random = self.get_random_number()
						p.evict_and_insert(page_id, next_process.pid, current_time, self.processes, self.selected_algorithm, self.verbose)

				# update word references, clock

				next_process.reference_count -= 1
				current_time += 1
				self.set_next_reference(next_process)

				if next_process.reference_count == 0:
					break
			
			# if references remain, add back to ready pool

			if next_process.reference_count > 0:
				self.ready.append(next_process)

		self.report()

	# end game

	def report(self):
		print('\nThe machine size is', str(self.machine_size) + '.')
		print('The page size is', str(self.page_size) + '.')
		print('The process size is', str(self.process_size) + '.')
		print('The job mix number is', str(self.job_mix) + '.')
		print('The number of references per process is', str(self.reference_count) + '.')
		print('The replacement algorithm is', str(self.selected_algorithm) + '.\n')

		total_residency = 0
		total_eviction = 0
		total_fault = 0

		for process in self.processes:
			total_residency += process.residency
			total_eviction += process.eviction_count
			total_fault += process.fault_count
			average_residency = (process.residency / process.eviction_count) if process.eviction_count > 0 else -1

			if average_residency != -1:
				print('Process', process.pid, 'had', process.fault_count, 'faults and', average_residency, 'average residency.')
			else:
				print('Process', process.pid, 'had', process.fault_count, 'faults.')
				print('ERROR: undefined average residency.')

		if total_eviction > 0:
			total_average_residency = total_residency / total_eviction
			print('\nThe total number of faults is', total_fault, 'and the overall average residency is', total_average_residency, '\n')
		else:
			print('\nThe total number of faults is', str(total_fault) + '.')
			print('ERROR: undefined total average residency.\n')