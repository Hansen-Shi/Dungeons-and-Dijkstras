import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.util.*;

public class Solution {
    
    //Function that takes in a file name as a String
    //Reads the contents of a file into an ArrayList and returns the ArrayList
    public static List<String> readFile(String dungeonFile){
        List <String> dungeonLayout = new ArrayList <String> ();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dungeonFile));
            String rooms;
            while ((rooms = reader.readLine()) != null){
                dungeonLayout.add(rooms);
            }
            reader.close();
        }
        catch (Exception e){
            System.out.println("oops");
        }
        return dungeonLayout;
    }
    
    //Function that takes in a graphs number of edges and vertices
    //Calculates the density of the graph then returns the value formatted to 2 decimal places
    public static double densityCalculator(double edges, double vertices) {
    	double density = (2 * edges)/(vertices * (vertices - 1));
    	int temp = (int) (density * 100);
    	return (double) temp/100;
    	
    }
    
    //Function that takes in a sorted List
    //Calculates and returns the median challenge value
    public static double medianCalculator(List<Integer> maximumChallenge) {
    	
    	int temp = maximumChallenge.size();
    	double median;
    	int mid1;
    	int mid2;
    	
    	//if there's an even number of values average the two values in the middle
    	if (temp % 2 == 0) {
    		mid1 = maximumChallenge.get((temp/2)- 1);
    		mid2 = maximumChallenge.get(temp/2);
    		median = (double)(mid1 + mid2)/2;
    	}
    	//if there's an odd number of values take the one in the middle
    	else { 
    		mid1 = maximumChallenge.get((temp-1)/2);
    		median = (double)mid1;
    	}
    	return (double)median;
    }
    
    //Function that performs a BFS on the graph and evaluates if the graph is valid or not
    //It takes in 2 hashmaps, one with all the connections, and one with all the rooms
    //returns a boolean
    public static boolean validityTester(HashMap<String, ArrayList<String>> h1, HashMap<String, Integer> h2) {
    	
    	//create a hashset for adding visited rooms to
    	LinkedHashSet <String> visited = new LinkedHashSet<String>();
    	
    	//create a stack for the dfs
    	Stack<String> roomStack = new Stack<String>();
    	
    	//added this so that the start room wasn't hard coded as "Entrance"
    	//this caused it to crash on some dungeons
    	Object[] rooms = h1.keySet().toArray();
    	String s = (String) rooms[0];
    	
    	roomStack.push(s);
    	
    	//while there's still stuff in the stack
    	while (!roomStack.empty()) {
    		
    		//pop an element off and check to see if it's been visited before
    		s = roomStack.pop();
    		
    		//if it hasn't been visited add it to the set and push the rooms connected to it onto the stack
    		if (!visited.contains(s)) {
    			visited.add(s);
    			for (String str : h1.get(s)) {
    				roomStack.push(str);
    				}
    			}
    		}
    	
    	//Checking if all the rooms have been visited
		return visited.size() == h2.size();
    }
    
    //Function to test the balance of the dungeon (a modified BFS)
    //Takes in the median challenge of the graph, a hashmap of the connections, and a hashmap of the rooms with their challenge values
    //Returns a boolean
    public static boolean balanceTester(double median, HashMap<String, ArrayList<String>> h1, HashMap<String, Integer> h2){
    	
    	//create a hashset for adding visited rooms to
    	LinkedHashSet <String> visited = new LinkedHashSet<String>();
    	
    	//create a stack for the dfs
    	Stack<String> roomStack = new Stack<String>();
    	
    	//added this so that the start room wasn't hard coded as "Entrance"
    	//this caused it to crash on some dungeons
    	Object[] rooms = h1.keySet().toArray();
    	String s = (String) rooms[0];
    	
    	roomStack.push(s);
    	
    	//keeps track of the balance of the dungeon
    	boolean balanced = true;
    	
    	//keeps track of the challenge of the current room
    	String currentChallenge;
    	
    	//while there's still stuff in the stack
    	while (!roomStack.empty()) {
    		
    		//pop an element off and check to see if it's been visited before
    		s = roomStack.pop();
    		
    		//checks the challenge value of the room that was popped
    		//updates the currentChallenge to either be "hard" or "easy"
    		if (h2.get(s) >= median) {
    			currentChallenge = "hard";
    		}
    		else {
    			currentChallenge = "easy";
    		}
    		//if it hasn't been visited add it to the set and push the rooms connected to it onto the stack
    		if (!visited.contains(s)) {
    			visited.add(s);
    			for (String str : h1.get(s)) {
    				
    				//checks to see if any of the connected rooms is of equal challenge
    				//if one exists, then the dungeon is not balanced
    				if (h2.get(str) > median && currentChallenge.equals("hard")) {
    					balanced = false;
    				}
    				else if (h2.get(str) < median && currentChallenge.equals("easy")) {
    					balanced = false;
    				}
    				roomStack.push(str);
    				}
    			}
    		}
    	return balanced;
    }
    
    //Function to sort the objectives into an achievable order
    //Takes in a hashmap of the objectives and their prereqs
    //returns an arraylist of the objectives in order
    public static ArrayList<String> topSortObjectives(HashMap<String, ArrayList<String>> h1){
    	
    	//what will be returned
    	ArrayList<String> order = new ArrayList<String>();
    	
    	//hashset that stores the visited objectives
    	LinkedHashSet<String> visited = new LinkedHashSet<String>();
    	
    	//looping through all the objectives in the hashmap
    	h1.forEach((k,v) -> {
    		
    		//if the objective hasn't been visited and it has no prereqs
    		//then add it to the visited hashset and add it to the ordered ArrayList
    		if (!visited.contains(k) && v.get(0).equals("none")) {
    			visited.add(k);
    			order.add(k);
    		}
    		
    		//if the objective hasn't been visited and it has prereqs
    		//then call the helper function
    		else if (!visited.contains(k)) {
    			topSortHelp(k, order, visited, h1);
    		}
    	});
    	return order;
    }
    
    //helper function for sorting the objectives
    //takes in the objective's name, the ArrayList we'll be returning, the hashset of visited objectives and the hashmap of objectives
    public static void topSortHelp(String k, ArrayList<String> order, HashSet<String> visited, 
    		HashMap<String, ArrayList<String>> h1) {
    	
    	//mark the current objective as visited
    	visited.add(k);
    	
    	//loop through the ArrayList of prereqs of the current objective
    	for (String str : h1.get(k)) {
    		
    		//if the prereq has been visited go to the next prereq in the list
    		if (visited.contains(str)) {
    			continue;
    		}
    		
    		//if the prereq has no other prereqs we're done, there's no other objective to check
    		else if (h1.get(k).get(0).equals("none")) {
    			continue;
    		}
    		
    		//if the current prereq has another prereq recursively call this function with the current prereq
    		else {
    			topSortHelp(str, order, visited, h1);
    		}
    	}
    	
    	//add the current objective to the ArrayList
    	order.add(k);
    }
    
    //Dijkstra's algorithm function
    //takes in a hashmap of all the rooms and their respective challenge values, a hashmap of all the connections, and a hashmap of all the objectives
    //returns a hashmap of rooms and previous rooms (if we had a table with: rooms, distance, and previous room)
    public static LinkedHashMap<String, String> namesakeFunction(String startIndex, HashMap<String, Integer> roomChallenge, HashMap<String, ArrayList<String>> connections,
    		HashMap<String, ArrayList<String>> objectives) {
    	
    	//holds the current room
    	String currentRoom = null;
    	
    	//a string that holds the starting room of each dungeon
    	String start = startIndex;

    	//a lot of setup here
    	//parents is a hashmap where (k, v) are (room, previous room)
    	LinkedHashMap<String, String> parents = new LinkedHashMap<String, String>();
    	
    	//unvisited is a hashmap where (k, v) are (room, challenge value)
    	LinkedHashMap<String, Integer> unvisited = new LinkedHashMap<String, Integer>();
    	
    	//rooms is a hashset of all the rooms
    	LinkedHashSet<String> rooms = new LinkedHashSet<String>();
    	
    	//visited is an arraylist that holds all the visited rooms
    	ArrayList<String> visited = new ArrayList<String>();
    	
    	//loops through the old hashmap of rooms and their challenge values
    	roomChallenge.forEach((k, v) -> {
    		
    		//if it's the first room of the dungeon 
    		//add it to unvisited with a value/distance of zero so it's picked first in the algorithm
    		//add it to rooms as well
    		if (k.equals(start)) {
    			unvisited.put(k, 0);
    			rooms.add(k);
    		}
    		
    		//if it's not the start room
    		//add it to unvisited with a value/distance of a very large number
    		//add it to rooms as well
    		else {
    			unvisited.put(k, Integer.MAX_VALUE);
    			rooms.add(k);
    			}
    	});
    	
    	//add the start room to the parents hashmap
    	parents.put(start, "none");
    	
    	//continues to loop until all the rooms have been visited
    	while (unvisited.size() > 0) {
    		
    		//current room is equal to the room closest to the last room we were in
    		currentRoom = dijkstrasHelper(unvisited, rooms);
    		
    		//remove the room we're in from unvisited and add it to visited
    		unvisited.remove(currentRoom);
    		visited.add(currentRoom);
    		
    		//loops through all the rooms connected to the current room we're in
    		for (String str : connections.get(currentRoom)) {
    			
    			//if the room hasn't been visited, update its distance value in unvisited
    			//add the room to parents with currentRoom as its parent
    			if (!visited.contains(str)) {
    				unvisited.put(str, roomChallenge.get(str));
    				parents.put(str, currentRoom);
    			}
    		}
    	}
    	//return hashmap of rooms and their parents
    	for (String room : rooms) {
    		parents.get(room);
    	}
    	return parents;
    }
    	
    //This function just helps to determine the closest room 
    //It takes in the unvisited hashmap and the hashset of rooms
    public static String dijkstrasHelper(HashMap<String, Integer> unvisited, HashSet<String> rooms) {
    	
    	String closest = "";
    	int smallestDistance = Integer.MAX_VALUE;
    	
    	//loops through all the rooms
    	for (String str : rooms) {
    		
    		//if they're unvisited and their 'distance' is less than the current smallest
    		//set the new smallest distance and save the room's name
    		if(unvisited.containsKey(str)) {	
    			if (unvisited.get(str) < smallestDistance) {
    				smallestDistance = unvisited.get(str);
    				closest = str;
    				}
    			}
    		}
    	//return the closest room
    	return closest;
    }
    
    //Function that does some setup for routeObj
    //I originally tried to do everything in here but that didn't work so well
    public static void pathfinder(int currentObj, String startingRoom, HashMap<String, String> parents, ArrayList<String> orderedObjs, HashMap<String, String> objectiveLocations) {
    	
    	//the current objective's room
    	String start = orderedObjs.get(currentObj);
    	
    	//the room we want to end on
    	String firstRoom = startingRoom;
    	
    	Stack<String> order = new Stack<String>();
    	
    	routeObj(start, firstRoom, parents, objectiveLocations, order, currentObj, orderedObjs);
    	}
    
    //function puts together a route for an objective
    public static void routeObj(String start, String end, HashMap<String, String> parents, HashMap<String, String> objectiveLocations,
    		Stack<String> stack, int currentObj, ArrayList<String> orderedObjs) {
    	
    	//string that will be printed
    	String objRoute = "";
    	
    	//arraylist of all the rooms in route order
    	ArrayList<String> route = new ArrayList<String>();
    	
    	
    	stack.clear();
    	
    	
    	String startingRoom = objectiveLocations.get(start);
    	stack.push(startingRoom);
    	
    	//while we haven't reached the room we want to end on
    	while (!stack.contains(end)) {
    		
    		startingRoom = parents.get(startingRoom);
    		stack.push(startingRoom);
    	}
    	
    	//popping the stack into an arraylist
    	while (!stack.isEmpty()) {
    		route.add(stack.pop());
    	}
    	
    	//formatting for output
    	objRoute = objRoute.concat(" " + orderedObjs.get(currentObj) + ": ");
    	for (String room : route) {
    		objRoute = objRoute.concat(room).concat("->");
    	}
    	objRoute = objRoute.replaceAll("->$", "");
    	System.out.println("Route for" + objRoute);
    	}
    
    
    
    public static void main(String args[]) throws IOException {
        
    	String order = "";
    	ArrayList<String> orderedObjs = new ArrayList<String>();
    	String firstRoom = "";
    	LinkedHashMap<String, String> parents = new LinkedHashMap<String, String>();
    	List<String> x;
    	List<String> y;
    	List<Integer> maxChallenge = new ArrayList<Integer>();
        LinkedHashMap<String, Integer> uniqueRooms = new LinkedHashMap<String, Integer>();
    	LinkedHashMap<String, ArrayList<String>> connections = new LinkedHashMap<String, ArrayList<String>>();
    	LinkedHashMap<String, ArrayList<String>> objectives = new LinkedHashMap <String, ArrayList<String>>();
    	LinkedHashMap<String, String> objectiveLocations = new LinkedHashMap <String, String>();
    	LinkedHashMap<String, Integer> roomChallenge = new LinkedHashMap <String, Integer>();
        
        Scanner scanner = new Scanner(System.in);
        String filename = scanner.next();
        scanner.close();
        String dungeonFile= String.format("dungeons/%s_in.txt", filename);
        
        double density;
        double median;
        int connectionsCount = 0;
        int deadEndsCount = 0;
        int hubsCount = 0;
    	
        
        //reads the file as Strings into an ArrayList
        List <String> myDungeon = readFile(dungeonFile);
        
        //Iterates through the list
        for (String str : myDungeon) {
        	
        	//splits the string where there's a comma
        	//converts it back into a readable list
        	//I wish I figured this out during project 2, whoops :)
        	x = Arrays.asList(str.split(","));
        	
        	//if the first string in the list is "room"
        	if (x.get(0).equals("room")) {
        		
        		//add the rooms name to the hashtable of rooms with an initial value of zero
        		//the value will be how many connections that room has
        		uniqueRooms.put(x.get(1), 0);
        		
        		//add the room's name and challenge value to a hashmap
        		roomChallenge.put(x.get(1), Integer.parseInt(x.get(2)));
        		
        		//if a room has an objective, add the objective and room to a hashmap
        		if (!x.get(3).equals("none")) {
        		objectiveLocations.put(x.get(3), x.get(1));
        		}
        	}
        	
        	//if the first string in the list is "connection"
        	if (x.get(0).equals("connection")) {
        		
        		//Increment the value associated with the start room by 1
        		//Increment the number of connections by 1
        		uniqueRooms.put(x.get(1), uniqueRooms.get(x.get(1)) + 1);
        		connectionsCount += 1;
        		
        		//Increment the value associated with the destination room by 1
        		uniqueRooms.put(x.get(2), uniqueRooms.get(x.get(2)) + 1);
        		
        		//Adds both sides of a connection to a hashmap, having both makes some stuff easier
        		//If the Key already exists then append the new destination to the value
        		connections.putIfAbsent(x.get(1), new ArrayList<String>());
        		connections.get(x.get(1)).add(x.get(2));
        		connections.putIfAbsent(x.get(2), new ArrayList<String>());
        		connections.get(x.get(2)).add(x.get(1));
        		}
        	
        	//if the first string in the list is "objective"
        	if (x.get(0).equals("objective")) {
        		
        		//parse the prereqs
        		y = Arrays.asList(x.get(2).split("\\+"));
        		
        		//add the objective with an empty arraylist as its value
        		objectives.putIfAbsent(x.get(1), new ArrayList<String>());
        		
        		//add all the prereqs to the value
        		for (String obj : y) {
        			objectives.get(x.get(1)).add(obj);
        		}
        	}
        }
        
        //adds the challenge values to a list for easy median and highest challenge
    	for (int i : roomChallenge.values()) {
    		maxChallenge.add(i);
    	}
    	
    	//sorting the list of challenge values for max and median
    	Collections.sort(maxChallenge);
        
        density = densityCalculator(connectionsCount, uniqueRooms.size());
        median = medianCalculator(maxChallenge);
        
        //iterates through all the values in the hashmap of rooms
        //since the values represent how many connections a room has:
        //if a room only had a value of 1 then it is a dead end
        //if a room had at least a value of 3 then it is a hub
        for (int rooms : uniqueRooms.values()) {
        	if (rooms == 1) {
        		deadEndsCount += 1;
        	}
        	if (rooms >= 3) {
        		hubsCount += 1;
        	}
        }
        
        System.out.println("Rooms: " + uniqueRooms.size());
        System.out.println("Connections: " + connectionsCount);
        System.out.println("Objectives: " + objectives.size());
        System.out.println("Density: " + density);
        System.out.println("Dead-ends: " + deadEndsCount);
        System.out.println("Hubs: " + hubsCount);
        System.out.println("Max Challenge: " + maxChallenge.get(maxChallenge.size()-1));
        System.out.println("Median Challenge: " + median);
       
        //if the dungeon is valid, do the rest of the tests
        if (validityTester(connections, uniqueRooms)) {
        	System.out.println("Valid: True");
        	
        	if (balanceTester(median, connections, roomChallenge)) {
        		System.out.println("Balanced: True");
        	}
        	else {
        		System.out.println("Balanced: False");
        	}
        	
        	orderedObjs = topSortObjectives(objectives);
        	
        	//This is just so the output fits what the autograder wants
        	for (String str : orderedObjs) {
        		
        		order = order.concat(str).concat(",");
        	}
        	order = order.replaceAll(",$", "");
        	System.out.println("Order: " + order);
        	
        	firstRoom = (String) Arrays.asList(uniqueRooms.keySet().toArray()).get(0);
            parents = namesakeFunction(firstRoom, roomChallenge, connections, objectives);
            pathfinder(0, firstRoom, parents, orderedObjs, objectiveLocations);
            for (int i = 0; i<orderedObjs.size()-1; i++) {
             	parents = namesakeFunction(objectiveLocations.get(orderedObjs.get(i)), roomChallenge, connections, objectives);
             	pathfinder(i+1, objectiveLocations.get(orderedObjs.get(i)), parents, orderedObjs, objectiveLocations);
             	}
            }
        else {
        	System.out.println("Valid: False");
        	}
        }
    }
