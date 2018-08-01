import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

public class Linker {
	public static void main(String[] args) {
		if (args.length != 1) { 
			System.out.println("One arg allowed, for input file loc.");
			return;
		}

		try {
			HashMap<String, Integer> symbolTable = firstPass(new Scanner(new File(args[0])));
			ArrayList<Integer> memoryMap = secondPass(new Scanner(new File(args[0])), symbolTable, 200 * 4);

			System.out.println("\nSymbol table.");

			for (String key : symbolTable.keySet())
				System.out.println(key + ": " + symbolTable.get(key));

			System.out.println("\nMemory map.");

			for (int i = 0; i < memoryMap.size(); i++)
				System.out.println(i + ": " + memoryMap.get(i));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}

	public static ArrayList<Integer> secondPass(Scanner inputFile, HashMap<String, Integer> symbolTable, int machineSize) {
		ArrayList<Integer> memoryMap = new ArrayList<>();
		ArrayList<String> useList = new ArrayList<>();
		int modCount = inputFile.nextInt();
		int base = 0;

		for (int i = 0; i < modCount; i++) {

			// throw away def list

			int defCount = inputFile.nextInt();

			for (int j = 0; j < defCount; j++) {
				inputFile.next();
				inputFile.nextInt();
			}

			// get the symbols used here

			int useCount = inputFile.nextInt();

			String[] currentUseList = new String[useCount];
			boolean[] symbolUsed = new boolean[useCount];

			for (int j = 0; j < useCount; j++) {
				String symbol = inputFile.next();

				if (symbolTable.get(symbol) == null) {
					System.err.println("ERROR: Symbol " + symbol + " not defined; zero used.");
					symbolTable.put(symbol, 0);
				}

				currentUseList[j] = symbol;
			}

			// add to the total program use list

			for (String use : currentUseList)
				useList.add(use);

			// validate program content and generate memory map

			int modSize = inputFile.nextInt();

			for (int j = 0; j < modSize; j++) {
				String type = inputFile.next();
				int relativeAddress = inputFile.nextInt();

				switch (type) {
					case "I":
						memoryMap.add(relativeAddress);
						break;
					case "A":
						if (relativeAddress % 1000 >= (machineSize)) {
							System.err.println("ERROR: Absolute address " + relativeAddress + " exceeds machine size; zero used.");
							memoryMap.add(relativeAddress - (relativeAddress % 1000));
						} else
							memoryMap.add(relativeAddress);

						break;
					case "R":
						if (relativeAddress % 1000 >= modSize) {
							System.err.println("ERROR: Relative address " + relativeAddress + " exceeds module size; zero used.");
							memoryMap.add(relativeAddress - (relativeAddress % 1000));
						} else
							memoryMap.add((relativeAddress + base));

						break;
					case "E":
						int index = relativeAddress % 1000;

						if (index >= currentUseList.length) {
							System.err.println("ERROR: External address " + relativeAddress + " exceeds length of the use list; treated as immediate.");
							memoryMap.add(relativeAddress);
						} else {
							memoryMap.add((relativeAddress - index) + symbolTable.get(currentUseList[index]));
							symbolUsed[index] = true;
						}

						break;
				}
			}

			for (int j = 0; j < useCount; j++)
				if (!symbolUsed[j])
					System.err.println("WARN: Symbol " + currentUseList[j] + " declared in use list but not actually used.");

			base += modSize;
		}

		for (String symbol : symbolTable.keySet())
			if (!useList.contains(symbol))
				System.err.println("WARN: Symbol " + symbol + " defined but not used.");

		return memoryMap;
	}

	public static HashMap<String, Integer> firstPass(Scanner inputFile) {
		HashMap<String, Integer> symbolTable = new HashMap<>();
		int modCount = inputFile.nextInt();
		int base = 0;

		// loop through each module

		for (int i = 0; i < modCount; i++) {

			HashMap<String, Integer> currentSymbols = new HashMap<>();

			// iterate over definitions and add to hash map

			int defCount = inputFile.nextInt();

			for (int j = 0; j < defCount; j++) {
				String symbol = inputFile.next();

				if (symbolTable.get(symbol) == null)
					currentSymbols.put(symbol, inputFile.nextInt());
				else
					System.err.println("ERROR: Symbol " + symbol + " multiply defined as " + inputFile.nextInt() + "; using initial value.");
			}

			// we skip uses on the first pass

			int useCount = inputFile.nextInt();

			for (int j = 0; j < useCount; j++) inputFile.next();

			// get the module size to recalculate base

			int modSize = inputFile.nextInt();

			// loop through the symbols defined for the module to validate relative addresses

			for (String symbol : currentSymbols.keySet()) {
				if (currentSymbols.get(symbol) >= modSize) {
					System.err.println("ERROR: Address for symbol " + symbol + " exceeds module size; zero (relative) used.");
					symbolTable.put(symbol, base);
				} else
					symbolTable.put(symbol, currentSymbols.get(symbol) + base);
			}

			// update base
			
			base += modSize;

			// skip the module content

			for (int j = 0; j < modSize; j++) {
				inputFile.next();
				inputFile.nextInt();
			}
		}

		return symbolTable;
	}
}