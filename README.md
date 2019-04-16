# Search algorithm: n-puzzle problem:


## The project

The N puzzle is a game invented and popularized by Noyes Palmer Chapman in the 1870s. It is played
on an N-by-N grid with $N^2 - 1$ tiles labeled 1 through N-1 and a blank square. The goal is to
rearrange the tiles so that they are in order. You are permitted to slide one of the available
tiles horizontally or vertically (but not diagonally) into the blank square.

The N puzzle is represented as an array of integers. The 15-puzzle shown above, for
instance, can be represented as an array of integers [15,2,1,12,8,5,6,11,4,9,10,7,3,14,13,0] with
'0' denoting the blank.

This program solves the puzzle automatically (for N=3, but also for N=4, N=5, and N=6).
There are two search strategies: blind search and informed search.

