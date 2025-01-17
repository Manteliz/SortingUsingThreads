import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {

	public static void main(String[] args) {	
		String threadCntString = "";
		String file = "";
		String outputFile = "";
		try {
			threadCntString = args[0];
			file = args[1];
			outputFile = args[2];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Please provide 3 parameters in the command line - number of threads, source file name, output file name.");
			System.exit(0);
		}
		
		int threadCount = 0;
		try {
			threadCount = Integer.parseInt(threadCntString);
		} catch (NumberFormatException e) {
			System.out.println("First parameter must be provided in the number format.");
			System.exit(0);
		}
		SourceFile sourceFile = readFile(file);
		int wordCount = sourceFile.getWordCount();
		String[] list = sourceFile.getWordList();
		
		/*Implementing multi-threading. Each thread will have to sort n = wordCount/threadCount(+-1) words.
		 * the number of threads that will sort n+1 words x = wordCount%threadCount
		 * the number of threads that will sort n words y = threadCount - x
		 */
		int x = wordCount%threadCount;
		int y = threadCount - x;
		int n = wordCount/threadCount;
		if(n <= 1 && wordCount > 1) {
			//In this case there is no point to have so many threads when the list is so small. Each thread should handle minimum 2 words
			threadCount = wordCount/2;
			x = wordCount%threadCount;
			y = threadCount - x;
			n = wordCount/threadCount;
		}else if(wordCount < 2){
			System.out.println("There is nothing to sort.");
			System.exit(0);
		}
		Monitor monitor = new Monitor();	
		//creating a pool of threads and launching them
		long start = System.currentTimeMillis();
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		for (int i = 0; i < y; i++) {
			String[] array = new String[n];
			System.arraycopy(list, i*n, array, 0, n);
			executor.submit(new Runner(array, monitor));
		}		
		for (int i = 0; i < x; i++) {
			String[] array = new String[n+1];
			System.arraycopy(list, (y*n + (i*(n+1))), array, 0, (n+1));
			executor.submit(new Runner(array, monitor));
		}
		
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String[] finalSortedArray = monitor.getArray();
		long end = System.currentTimeMillis();
		
		//Creating manual tests to see if the program actually works
		new ManualTests(finalSortedArray, wordCount);
		
		//Writing the final sorted list into the file provided by the user
		writeToFile(finalSortedArray, outputFile);
		System.out.println();
		System.out.println("It took " + (end-start) + " millisecond to sort the list with " + threadCount + " thread(s).");
	}
	
	private static void writeToFile(String[] array, String filename) {
		try {
			FileWriter fw = new FileWriter(new File(filename));
			for (int i = 0; i < array.length; i++) {
				fw.write(array[i] + "\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static SourceFile readFile(String fileName) {
		Scanner in = null;
		try {
			in = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		int wordCount = Integer.parseInt(in.nextLine());
		String[] list = new String[wordCount];
		int i = 0;

		while(in.hasNext() == true && i < wordCount) {
			list[i] = in.nextLine();
			i++;
		}
		if(in.hasNext() == true) {
			System.out.println("There are actually more words than stated in the first line.");
			System.exit(0);
		}else if(i < wordCount){
			System.out.println("There are actually less words than stated in the first line. The number of words given is " + i);
			System.exit(0);
		}
		return new SourceFile(wordCount, list);
	}
	
	public static void QuickSort(int low, int high, String[] wordList) {
		// implementing Quick Sort algorithm
		String pivot = wordList[high];
		int i = low - 1;
		String temp;
		if((high - low) > 0) {
			//there is more than one element in the partitioning array
			for(int j = low; j < high; j++) {
				if(wordList[j].compareTo(pivot) < 0) {
					//wordList[j] is lexicographically preceding the pivot
					i++;
					temp = wordList[i];
					wordList[i] = wordList[j];
					wordList[j] = temp;
				}			
			}
			//found the pivot position = i+1. Every element after the index i must be equal or bigger than the pivot
			temp = wordList[i+1];
			wordList[i+1] = pivot;
			wordList[high] = temp;
			if((i+1-low) >= 2) {
				//there are more than one element to the left of the pivot
				QuickSort(low, i, wordList);
			}
			if((high - (i+1)) >= 2) {
				//there are more than one element to the right of the pivot
				QuickSort(i+2, high, wordList);
			}
		}				
	}	
}
