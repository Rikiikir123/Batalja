import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

class Planet {
	String name;
	String color;
	int army;
	int x, y;
	float size;
	Planet closestEnemyPlanet;
	Planet closestNeutral;
	Planet closestFriend;

	Planet(String name, int army, int x, int y, String color, float size) {
		this.name = name;
		this.color = color;
		this.x = x;
		this.y = y;
		this.army = army;
		this.size = size;
	}
}

public class Player {
	static BufferedWriter fileOut = null;
	// GAME DATA
	public static int universeWidth;
	public static int universeHeight;
	public static String myColor;
	public static String teammateColor;
	public static String[] bluePlanets;
	public static String[] cyanPlanets;
	public static String[] greenPlanets;
	public static String[] yellowPlanets;
	public static String[] blueFleets;
	public static String[] cyanFleets;
	public static String[] greenFleets;
	public static String[] yellowFleets;

	//
	public static Planet[] allPlanets;
	public static Planet[] myPlanets;
	public static Planet[] enemyPlanets;
	public static Planet[] neutralPlanets;
	public static Planet[] teamPlanets; //includes my and my teammates planets
	public static HashMap<Integer, Integer> myFleets = new HashMap<>(); //<Name, Size>
	// attributes from dataset
	public static int turnsPlayed = 0;
	public static int fleetGenerated = 0;
	public static int numFleetGenerated = 0;
	public static int numFleetReinforced = 0;
	public static int largestAttack = 0;
	public static int fleetReinforced = 0;
	public static int largestReinforcement = 0;// not correct value

	public static void main(String[] args) throws Exception {

		try {
			Random rand = new Random();
			while (true) {
				turnsPlayed++;
				getGameState();

				//find and set a closest on each of my planets
				findClosestEnemy();
				findClosestNeutral();
				findClosestFriend();

				//first 30 turns attack closest neutrals
				if (turnsPlayed <= 50){
					attackClosestNeutral(rand);
				}
				//decision tree
				if (turnsPlayed > 50){
					if (fleetReinforced <= 3496){
						if (largestAttack <= 59){
							if (numFleetReinforced <= 94) {
								if (turnsPlayed % 2 == 0){
									defendNearest(rand);
								}
								else {
									attackClosestEnemy(rand);
								}
							} else {
								if (turnsPlayed <= 278) {
									if (fleetReinforced <= 988) {
										if (turnsPlayed % 2 == 0){
											defendNearest(rand);
										}
										else {
											attackClosestEnemy(rand);
										}
									} else {
										if (turnsPlayed % 3 == 0){
											defendNearest(rand);
										}
										else {
											attackClosestEnemy(rand);
										}
									}
								} else {
									if (turnsPlayed % 2 == 0){
										defendNearest(rand);
									}
									else {
										attackClosestEnemy(rand);
									}
								}
							}
						} else {
							if (turnsPlayed <= 321) {
								if (turnsPlayed % 3 == 0){
									defendNearest(rand);
								}
								else {
									attackClosestEnemy(rand);
								}
							} else {
								if (turnsPlayed % 2 == 0){
									defendNearest(rand);
								}
								else {
									attackClosestEnemy(rand);
								}
							}
						}
					} else {
						if (turnsPlayed <= 522) {
							if (turnsPlayed % 3 == 0){
								defendNearest(rand);
							}
							else {
								attackClosestEnemy(rand);
							}
						} else {
							if (fleetReinforced <= 28612) {
								if (turnsPlayed % 2 == 0){
									defendNearest(rand);
								}
								else {
									attackClosestEnemy(rand);
								}
							} else {
								if (turnsPlayed % 3 == 0){
									defendNearest(rand);
								}
								else {
									attackClosestEnemy(rand);
								}
							}
						}
					}
				}
				System.out.println("E");
			}
		} catch (Exception e) {
			logToFile("ERROR: ");
			logToFile(e.getMessage());
			e.printStackTrace();
		}
		fileOut.close();

	}

	/**
	 * This function should be used instead of System.out.print for debugging, since
	 * the System.out.println is used to send commands to the game
	 *
	 * @param line String you want to log into the log file.
	 * @throws IOException
	 */
	public static void logToFile(String line) throws IOException {
		if (fileOut == null) {
			FileWriter fstream = new FileWriter("Igralec.log");
			fileOut = new BufferedWriter(fstream);
		}
		if (line.charAt(line.length() - 1) != '\n') {
			line += "\n";
		}
		fileOut.write(line);
		fileOut.flush();
	}

	/**
	 * This function should be called at the start of each turn to obtain
	 * information about the current state of the game. The data received includes
	 * details about planets and fleets, categorized by color and type.
	 *
	 * This version of the function uses dynamic lists to store data about planets
	 * and fleets for each color, accommodating for an unknown quantity of items. At
	 * the end of data collection, these lists are converted into fixed-size arrays
	 * for consistent integration with other parts of the program.
	 *
	 * Feel free to modify and extend this function to enhance the parsing of game
	 * data to your needs.
	 *
	 * @throws NumberFormatException if parsing numeric values from the input fails.
	 * @throws IOException           if an I/O error occurs while reading input.
	 */
	public static void getGameState() throws NumberFormatException, IOException {
		BufferedReader stdin = new BufferedReader(new java.io.InputStreamReader(System.in));
		LinkedList<String> bluePlanetsList = new LinkedList<>();
		LinkedList<String> cyanPlanetsList = new LinkedList<>();
		LinkedList<String> greenPlanetsList = new LinkedList<>();
		LinkedList<String> yellowPlanetsList = new LinkedList<>();

		LinkedList<Planet> allPlanetsList = new LinkedList<>();
		LinkedList<Planet> myPlanetsList = new LinkedList<>();
		LinkedList<Planet> enemyPlanetsList = new LinkedList<>();
		LinkedList<Planet> neutralPlanetsList = new LinkedList<>();
		LinkedList<Planet> teamPlanetsList = new LinkedList<>();


		String line = "";
		while (!(line = stdin.readLine()).equals("S")) {
			logToFile(line);
			String[] tokens = line.split(" ");
			char firstLetter = line.charAt(0);
			if (firstLetter == 'U') {
				universeWidth = Integer.parseInt(tokens[1]);
				universeHeight = Integer.parseInt(tokens[2]);
				myColor = tokens[3];
				teammateColor = teammate();
			}
			if (firstLetter == 'P') {
				String planetName = tokens[1];
				int x = Integer.parseInt(tokens[2]);
				int y = Integer.parseInt(tokens[3]);
				float size = Float.parseFloat(tokens[4]);
				int army = Integer.parseInt(tokens[5]);
				String color = tokens[6];
				if (tokens[6].equals("blue")) {
					bluePlanetsList.add(planetName);
				}
				if (tokens[6].equals("cyan")) {
					cyanPlanetsList.add(planetName);
				}
				if (tokens[6].equals("green")) {
					greenPlanetsList.add(planetName);
				}
				if (tokens[6].equals("yellow")) {
					yellowPlanetsList.add(planetName);
				}
				if (tokens[6].equals("null")) {
					Planet planet = new Planet(planetName, army, x, y, color, size);
					neutralPlanetsList.add(planet);
				}
				//only my planets
				if (myColor.equals(color)) {
					Planet planet = new Planet(planetName, army, x, y, color, size);
					myPlanetsList.add(planet);
				}
				//my and my teammates planets
				if (color.equals(teammateColor) || color.equals(myColor)){
					Planet planet = new Planet(planetName, army, x, y, color, size);
					teamPlanetsList.add(planet);
				}
				if (!color.equals(myColor) || !color.equals(teammateColor)) {
					Planet planet = new Planet(planetName, army, x, y, color, size);
					enemyPlanetsList.add(planet);
				}
				Planet planet = new Planet(planetName, army, x, y, color, size);
				allPlanetsList.add(planet);
			}
			if (firstLetter == 'F') {
				int fleetName = Integer.parseInt(tokens[1]);
				int fleetSize = Integer.parseInt(tokens[2]);
				String fleetSource = tokens[3];
				String fleetDestination = tokens[4];
				int travelTurns = Integer.parseInt(tokens[6]);
				String fleetPlayerColor = tokens[7];
				if (fleetPlayerColor.equals(myColor) && !myFleets.containsKey(fleetName)) {
					myFleets.put(fleetName, fleetSize);
					fleetGenerated += fleetSize;
					numFleetGenerated++;
					if (myPlanets != null) {
						for (Planet planet : myPlanets) {
							if (planet.name.equals(fleetDestination)) {
								fleetReinforced += fleetSize;
								numFleetReinforced++;
								if (fleetSize > largestReinforcement) {
									largestReinforcement = fleetSize;
								}
							}
						}
					}
				}
				if (fleetPlayerColor.equals(myColor)) {
					int atk = fleetSize;
					if (largestAttack < atk) {
						largestAttack = atk;
					}
				}

			}
		}


		bluePlanets = bluePlanetsList.toArray(new String[0]);
		cyanPlanets = cyanPlanetsList.toArray(new String[0]);
		greenPlanets = greenPlanetsList.toArray(new String[0]);
		yellowPlanets = yellowPlanetsList.toArray(new String[0]);

		myPlanets = myPlanetsList.toArray(new Planet[0]);
		enemyPlanets = enemyPlanetsList.toArray(new Planet[0]);
		allPlanets = allPlanetsList.toArray(new Planet[0]);
		neutralPlanets = neutralPlanetsList.toArray(new Planet[0]);
		teamPlanets = teamPlanetsList.toArray(new Planet[0]);
	}
	public static double calculateDistance(Planet planet1, Planet planet2) {
		int deltaX = planet1.x - planet2.x;
		int deltaY = planet1.y - planet2.y;
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}
	public static void findClosestNeutral(){
		if (myPlanets != null && neutralPlanets != null){
			double closesetDistance = Double.MAX_VALUE;
			double distance;
			for (Planet myPlanet : myPlanets){
				for (Planet enemyPlanet : neutralPlanets){
					distance = calculateDistance(myPlanet,enemyPlanet);
					if (distance < closesetDistance){
						myPlanet.closestNeutral = enemyPlanet;
						closesetDistance = distance;
					}
				}
			}
		}
	}
	public static void findClosestEnemy(){
		if (myPlanets != null && enemyPlanets != null){
			double closesetDistance = Double.MAX_VALUE;
			double distance;
			for (Planet myPlanet : myPlanets){
				for (Planet enemyPlanet : enemyPlanets){
					distance = calculateDistance(myPlanet,enemyPlanet);
					if (distance < closesetDistance){
						myPlanet.closestEnemyPlanet = enemyPlanet;
						closesetDistance = distance;
					}
				}
			}
		}
	}
	public static void findClosestFriend(){
		if (myPlanets != null && myPlanets.length >= 2){
			double closesetDistance = Double.MAX_VALUE;
			double distance;
			for (Planet myPlanet1 : myPlanets){
				for (Planet myPlanet2 : teamPlanets){
					if (!myPlanet1.name.equals(myPlanet2)){
						distance = calculateDistance(myPlanet1,myPlanet2);
						if (distance < closesetDistance){
							myPlanet1.closestFriend = myPlanet2;
							closesetDistance = distance;
						}
					}
				}
			}
		}
	}

	public static void attackClosestNeutral(Random rand){
		if(myPlanets.length > 0 && enemyPlanets.length > 0 && neutralPlanets.length > 0){
			for (Planet planet : myPlanets){
				try{
					Planet target = planet.closestNeutral;
					System.out.println("A " + planet.name + " " + target.name);
				}
				//if error then just attack something
				catch (Exception e){
					Planet enemyPlanet = enemyPlanets[rand.nextInt(enemyPlanets.length)];
					System.out.println("A " + planet.name + " " + enemyPlanet.name);
				}
			}
		}
	}
	public static void attackClosestEnemy(Random rand){
		if(myPlanets.length > 0 && enemyPlanets.length > 0){
			for (Planet planet : myPlanets){
				try{
					Planet target = planet.closestEnemyPlanet;
					System.out.println("A " + planet.name + " " + target.name);
				}
				//if error then just attack something
				catch (Exception e){
					Planet enemyPlanet = enemyPlanets[rand.nextInt(enemyPlanets.length)];
					System.out.println("A " + planet.name + " " + enemyPlanet.name);
				}
			}
		}
	}
	public static void defendNearest(Random rand){
		if(myPlanets.length > 0 && enemyPlanets.length > 0){
			for (Planet planet : myPlanets){
				try{
					Planet target = planet.closestFriend;
					System.out.println("A " + planet.name + " " + target.name);
				}
				//if error then just attack something
				catch (Exception e){
					Planet enemyPlanet = enemyPlanets[rand.nextInt(enemyPlanets.length)];
					System.out.println("A " + planet.name + " " + enemyPlanet.name);
				}
			}
		}
	}
	public static String teammate(){
		if (myColor.equals("blue")){
			return "cyan";
		}
		if(myColor.equals("cyan")){
			return "blue";
		}
		if ((myColor.equals("green"))){
			return "yellow";
		}
		if (myColor.equals("yellow")){
			return "green";
		}
		else {
			return null;
		}
	}
}
