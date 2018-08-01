import sys
from Driver import Driver

def main():
	d = Driver(sys.argv[1:], 'input/random-numbers.txt')
	d.run()

if __name__ == '__main__':
	main()