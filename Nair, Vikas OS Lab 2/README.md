# OS Scheduler in Java
## Built by Vikas Nair, for Operating Systems (Lab 2)
## 3 Mar 2018

A Java implementation of four OS scheduling algorithms: uniprogramming, FCFS (first-come-fist-serve), RR2 (round-robin, with quantum 2), and SRTN (shortest-remaining-time-next).
Simple UI designed for choosing one of the four algorithms.

```
- Uniprogramming
	- runs each process to completion, OS busy waits during I/O block

- FCFS
	- runs other processes during I/O block, ordered by arrival time

- RR2
	- runs through each next available process, ordered by original order in file

- SRTN
	- runs other processes during I/O block, ordered by remaining CPU time left (and CPU burst amount)
```

### Compile
```
javac *.java
```

### Run
```
java Main input/random-numbers.txt input/input-?.txt --verbose
```
