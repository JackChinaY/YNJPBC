//package TEST;
//
//import java.util.ArrayList;
//import java.util.Collections;
//
//public class Test12 {
//}
//
//class City {
//    int x;
//    int y;
//
//    // Constructs a randomly placed city
//    public City() {
//        this.x = (int) (Math.random() * 200);
//        this.y = (int) (Math.random() * 200);
//    }
//
//    // Constructs a city at chosen x, y location
//    public City(int x, int y) {
//        this.x = x;
//        this.y = y;
//    }
//
//    // Gets city's x coordinate
//    public int getX() {
//        return this.x;
//    }
//
//    // Gets city's y coordinate
//    public int getY() {
//        return this.y;
//    }
//
//    // Gets the distance to given city
//    public double distanceTo(City city) {
//        int xDistance = Math.abs(getX() - city.getX());
//        int yDistance = Math.abs(getY() - city.getY());
//        double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
//        return distance;
//    }
//
//    @Override
//    public String toString() {
//        return getX() + ", " + getY();
//    }
//}
//
//class Tour {
//    /**
//     * Holds our citiesList of cities
//     */
//    private ArrayList<City> citiesList; // Cache
//    private int distance = 0;
//
//    public Tour() {
//        // TODO Auto-generated constructor stub
//        citiesList = new ArrayList<City>();
//    }
//
//
//    /**
//     * Constructs a citiesList from another citiesList
//     */
//    public Tour(ArrayList<City> tour) {
//        citiesList = new ArrayList<City>();
//        for (City city : tour) {
//            this.citiesList.add(city);
//        }
//
//    }
//
//    /**
//     * Returns citiesList information
//     */
//    public ArrayList<City> getCitiesList() {
//        return citiesList;
//    }
//
//    /**
//     * Creates a random individual
//     */
//    public Tour generateIndividual() {
//        // Loop through all our destination cities and add them to our citiesList
//        for (int cityIndex = 0; cityIndex < citiesList.size(); cityIndex++) {
//            setCity(cityIndex, this.getCity(cityIndex));
//        }
//        // Randomly reorder the citiesList
//        Collections.shuffle(citiesList);
//        return this;
//    }
//
//    /**
//     * Create new neighbour tour
//     */
//    public Tour generateNeighourTour() {
//        Tour newSolution = new Tour(this.citiesList);
//        // Get a random positions in the tour
//        int tourPos1 = (int) (newSolution.numberOfCities() * Math
//                .random());
//        int tourPos2 = (int) (newSolution.numberOfCities() * Math
//                .random());
//        // Get the cities at selected positions in the tour
//        City citySwap1 = newSolution.getCity(tourPos1);
//        City citySwap2 = newSolution.getCity(tourPos2);
//
//        // Swap them
//        newSolution.setCity(tourPos2, citySwap1);
//        newSolution.setCity(tourPos1, citySwap2);
//        return newSolution;
//    }
//
//    /**
//     * Gets a city from the citiesList
//     */
//    public City getCity(int tourPosition) {
//        return (City) citiesList.get(tourPosition);
//    }
//
//    /**
//     * Sets a city in a certain position within a citiesList
//     */
//    public void setCity(int tourPosition, City city) {
//        citiesList.set(tourPosition, city);
//        // If the tours been altered we need to reset the fitness and distance
//        distance = 0;
//    }
//
//    public Tour addCity(City city) {
//        citiesList.add(city);
//        return this;
//    }
//
//    public ArrayList<City> getAllCities() {
//        return citiesList;
//    }
//
//    /**
//     * Gets the total distance of the citiesList
//     */
//    public int getDistance() {
//        if (distance == 0) {
//            int tourDistance = 0;
//            // Loop through our citiesList's cities
//            for (int cityIndex = 0; cityIndex < numberOfCities(); cityIndex++) {
//                // Get city we're traveling from
//                City fromCity = getCity(cityIndex);
//                // City we're traveling to
//                City destinationCity;
//                // Check we're not on our citiesList's last city, if we are set our
//                // citiesList's final destination city to our starting city
//                if (cityIndex + 1 < numberOfCities()) {
//                    destinationCity = getCity(cityIndex + 1);
//                } else {
//                    destinationCity = getCity(0);
//                }
//                // Get the distance between the two cities
//                tourDistance += fromCity.distanceTo(destinationCity);
//            }
//            distance = tourDistance;
//        }
//        return distance;
//    }
//
//    /**
//     * Get number of cities on our citiesList
//     */
//    public int numberOfCities() {
//        return citiesList.size();
//    }
//
//    @Override
//    public String toString() {
//        String geneString = "|";
//        for (int i = 0; i < numberOfCities(); i++) {
//            geneString += getCity(i) + "|";
//        }
//        return geneString;
//    }
//}
//
//class SimulatedAnnealing {
//
//    /**
//     * Set initial temp
//     */
//    private double currentTemperature = 5000;
//    /**
//     * minimal temperature to cool
//     */
//    private double minTemperature = 0.0001;
//    private double internalLoop = 1000;
//    /**
//     * Cooling rate
//     */
//    private double coolingRate = 0.001;
//    /**
//     * Initialize intial solution
//     */
//    private Tour currentSolution;
//
//    /**
//     * set a random initial tour
//     */
//    public void initTour() {
//        Tour tour = new Tour();
//        tour.addCity(new City(60, 200))
//                .addCity(new City(180, 200))
//                .addCity(new City(80, 180))
//                .addCity(new City(140, 180))
//                .addCity(new City(20, 160))
//                .addCity(new City(100, 160))
//                .addCity(new City(200, 160))
//                .addCity(new City(140, 140))
//                .addCity(new City(40, 120))
//                .addCity(new City(100, 120))
//                .addCity(new City(180, 100))
//                .addCity(new City(60, 80))
//                .addCity(new City(120, 80))
//                .addCity(new City(180, 60))
//                .addCity(new City(20, 40))
//                .addCity(new City(100, 40))
//                .addCity(new City(200, 40))
//                .addCity(new City(20, 20))
//                .addCity(new City(60, 20))
//                .addCity(new City(160, 20));
//        currentSolution = tour.generateIndividual();
//        System.out.println("Initial solution distance: " + currentSolution.getDistance());
//    }
//
//    /**
//     * iterate for getting the best Tour
//     *
//     * @return best tour
//     */
//    public Tour anneal() {
//        DynamicDataWindow ddWindow = new DynamicDataWindow("模拟退火算法收敛过程");
//        ddWindow.setY_Coordinate_Name("所有路径和 ");
//        ddWindow.setVisible(true);
//        long tp = 0;
//
//        Tour bestSolution = new Tour(currentSolution.getCitiesList());
//        Tour newSolution = null;
//        // Loop until system has cooled
//        while (currentTemperature > minTemperature) {
//            for (int i = 0; i < internalLoop; i++) {
//                //get a solution from neighbour
//                newSolution = currentSolution.generateNeighourTour();
//                // Get energy of solutions
//                int currentEnergy = currentSolution.getDistance();
//                int neighbourEnergy = newSolution.getDistance();
//
//                // Decide if we should accept the neighbour
//                if (acceptanceProbability(currentEnergy, neighbourEnergy,
//                        currentTemperature) > Math.random()) {
//                    currentSolution = new Tour(newSolution.getCitiesList());
//                }
//
//                // Keep track of the best solution found
//                if (currentSolution.getDistance() < bestSolution.getDistance()) {
//                    bestSolution = new Tour(currentSolution.getCitiesList());
//                }
//            }
//            // Cool system
//            currentTemperature *= 1 - coolingRate;
//
//            long millis = System.currentTimeMillis();
//            if (millis - tp > 300) {
//                tp = millis;
//                ddWindow.addData(millis, bestSolution.getDistance());
//            }
//            try {
//                Thread.sleep(10L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return bestSolution;
//    }
//
//    /**
//     * Calculate the acceptance probability
//     **/
//    private double acceptanceProbability(int energy, int newEnergy, double temperature) {
//        // If the new solution is better, accept it
//        if (newEnergy < energy) {
//            return 1.0;
//        }
//        // If the new solution is worse, calculate an acceptance probability
//        return Math.exp((energy - newEnergy) / temperature);
//    }
//}