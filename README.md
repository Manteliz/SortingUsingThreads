# Multi-Threading
Program which sorts words using threads. The program has to receive three parameters in the command line: an integer that specifies the number of threads to be used for sorting, the file name of an input file and an output file name. Example of the command line:
"128 names.txt out.txt"
In the example above, the Java program uses 128 threads, reads the words from the file named "names.txt" and prints the result to the file "out.txt".

The first line of the input file is an integer specifying how many words there are in the file. The rest of the file contains all the words to be sorted separated by line breaks.

Quick Sort Algorith was chosen to be implemented for this project. The average complexity is n*log(n).

The manual testing is implemented to check if the list really gets sorted.


