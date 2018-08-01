# OS Pager in Python
## Built by Vikas Nair, for Operating Systems (Lab 4)
## 18 Apr 2018

A Python implementation of a demand pager, using round-robin scheduling and FIFO, LRU, or Random paging algorithms.

### Run
```
python3 Run.py M P S J N R D, where:

M --> the machine size in words.
P --> the page size in words.
S --> the size of each process, i.e., the references are to virtual addresses 0..S-1. J, the ‘‘job mix’’, which determines A, B, and C, as described below.
N --> the number of references for each process.
R --> the replacement algorithm, FIFO, RANDOM, or LRU.
D --> the log verbose option (0/1)
```