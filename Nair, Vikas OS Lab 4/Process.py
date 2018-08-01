class Process:
	def __init__(self, pid, size, reference_count, a, b, c):
		self.pid = pid
		self.size = size
		self.residency = self.fault_count = self.eviction_count = 0
		self.reference_count = reference_count

		# fractions a, b, and c of references

		self.a = a
		self.b = b
		self.c = c

		# set reference

		self.next_reference = (111 * pid) % size

	def __repr__(self):
		return 'PID: ' + str(self.pid) + ', SIZE: ' + str(self.size)\
		+ ', RESIDENCY: ' + str(self.residency) + ', FAULTS: ' + str(self.fault_count) + ', EVICTIONS: ' + str(self.eviction_count)\
		+ ', REFERENCES: ' + str(self.reference_count) + ', NEXT: ' + str(self.next_reference)\
		+ ', A: ' + str(self.a) + ', B: ' + str(self.b) + ', C: ' + str(self.c)