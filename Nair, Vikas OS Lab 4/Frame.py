class Frame:
	def __init__(self, fid):
		self.fid = fid
		self.pid = self.page_id = self.recently_used = -1
		self.time_inserted = 0
		self.used = False

	def __repr__(self):
		return 'FID: ' + str(self.fid) + ', PID: ' + str(self.pid) + ', PAGE ID: ' + str(self.page_id) + ', USED: ' + str(self.used) + ', RECENTLY USED: ' + str(self.recently_used)