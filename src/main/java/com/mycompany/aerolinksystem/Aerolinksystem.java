package com.mycompany.aerolinksystem;

import java.util.*;

// ==========================================
// 1. ENUMS
// ==========================================

enum FlightCategory {
    DOMESTIC,
    INTERNATIONAL,
    CARGO
}

enum FlightStatus {
    SCHEDULED,
    BOARDING,
    DELAYED,
    DEPARTED,
    CANCELLED
}

// ==========================================
// 2. DOMAIN CLASSES
// ==========================================

class Flight {
    private String flightId;
    private String airlineName;
    private String destination;
    private String departureTime;
    private int passengerCapacity;
    private int bookedPassengers;
    private FlightCategory flightCategory;
    private FlightStatus flightStatus;

    public Flight(String flightId, String airlineName, String destination, String departureTime,
                  int passengerCapacity, int bookedPassengers, FlightCategory flightCategory,
                  FlightStatus flightStatus) {
        if (passengerCapacity < 0 || bookedPassengers < 0) {
            throw new IllegalArgumentException("Capacity and booked passengers must be non-negative.");
        }
        if (bookedPassengers > passengerCapacity) {
            throw new IllegalArgumentException("Booked passengers cannot exceed passenger capacity.");
        }
        this.flightId = flightId;
        this.airlineName = airlineName;
        this.destination = destination;
        this.departureTime = departureTime;
        this.passengerCapacity = passengerCapacity;
        this.bookedPassengers = bookedPassengers;
        this.flightCategory = flightCategory;
        this.flightStatus = flightStatus;
    }

    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }

    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public int getPassengerCapacity() { return passengerCapacity; }
    public void setPassengerCapacity(int passengerCapacity) {
        if (passengerCapacity < 0) {
            throw new IllegalArgumentException("Passenger capacity cannot be negative.");
        }
        if (this.bookedPassengers > passengerCapacity) {
            throw new IllegalArgumentException("Passenger capacity cannot be less than current booked passengers.");
        }
        this.passengerCapacity = passengerCapacity;
    }

    public int getBookedPassengers() { return bookedPassengers; }
    public void setBookedPassengers(int bookedPassengers) {
        if (bookedPassengers < 0) {
            throw new IllegalArgumentException("Booked passengers cannot be negative.");
        }
        if (bookedPassengers > this.passengerCapacity) {
            throw new IllegalArgumentException("Booked passengers cannot exceed capacity.");
        }
        this.bookedPassengers = bookedPassengers;
    }

    public FlightCategory getFlightCategory() { return flightCategory; }
    public void setFlightCategory(FlightCategory flightCategory) { this.flightCategory = flightCategory; }

    public FlightStatus getFlightStatus() { return flightStatus; }
    public void setFlightStatus(FlightStatus flightStatus) { this.flightStatus = flightStatus; }

    public double getPassengerLoadPercentage() {
        if (passengerCapacity <= 0) return 0.0;
        return ((double) bookedPassengers / passengerCapacity) * 100.0;
    }

    public void displayDetails() {
        System.out.println("Flight ID:          " + flightId);
        System.out.println("Airline Name:       " + airlineName);
        System.out.println("Destination:        " + destination);
        System.out.println("Departure Time:     " + departureTime);
        System.out.println("Capacity:           " + passengerCapacity);
        System.out.println("Booked Passengers:  " + bookedPassengers);
        System.out.println("Passenger Load:     " + String.format("%.2f%%", getPassengerLoadPercentage()));
        System.out.println("Category:           " + flightCategory);
        System.out.println("Status:             " + flightStatus);
    }
}

class InternationalFlight extends Flight {
    private String departureTerminal;
    private String boardingGate;
    private boolean customsClearanceRequired;

    public InternationalFlight(String flightId, String airlineName, String destination, String departureTime,
                               int passengerCapacity, int bookedPassengers, FlightStatus flightStatus,
                               String departureTerminal, String boardingGate, boolean customsClearanceRequired) {
        super(flightId, airlineName, destination, departureTime, passengerCapacity, bookedPassengers,
              FlightCategory.INTERNATIONAL, flightStatus);
        this.departureTerminal = departureTerminal;
        this.boardingGate = boardingGate;
        this.customsClearanceRequired = customsClearanceRequired;
    }

    public String getDepartureTerminal() { return departureTerminal; }
    public void setDepartureTerminal(String departureTerminal) { this.departureTerminal = departureTerminal; }

    public String getBoardingGate() { return boardingGate; }
    public void setBoardingGate(String boardingGate) { this.boardingGate = boardingGate; }

    public boolean isCustomsClearanceRequired() { return customsClearanceRequired; }
    public void setCustomsClearanceRequired(boolean customsClearanceRequired) { this.customsClearanceRequired = customsClearanceRequired; }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Departure Terminal: " + departureTerminal);
        System.out.println("Boarding Gate:      " + boardingGate);
        System.out.println("Customs Clearance:  " + (customsClearanceRequired ? "Yes" : "No"));
    }
}

// ==========================================
// 3. CORE SCHEDULER & REPORTING LOGIC
// ==========================================

class GateScheduler {
    public static final String[] TIME_SLOTS = {"06:00", "08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00"};
    public static final String[] GATES = {"G01", "G02", "G03", "G04", "G05"};

    private final Flight[][] schedule = new Flight[5][8];
    private final List<Flight> flightRegistry = new ArrayList<>();

    // --- Feature 1: Flight Management ---

    public boolean registerFlight(Flight flight) {
        if (findFlightById(flight.getFlightId()) != null) {
            return false;
        }
        flightRegistry.add(flight);
        return true;
    }

    public Flight findFlightById(String flightId) {
        for (Flight f : flightRegistry) {
            if (f.getFlightId().equalsIgnoreCase(flightId)) {
                return f;
            }
        }
        return null;
    }

    public boolean updateFlight(String flightId, String newAirline, String newDest, String newTime, int newCap, int newBooked, FlightStatus newStatus) {
        Flight flight = findFlightById(flightId);
        if (flight == null || newCap < 0 || newBooked < 0 || newBooked > newCap) return false;

        flight.setAirlineName(newAirline);
        flight.setDestination(newDest);
        flight.setDepartureTime(newTime);

        if (newCap < flight.getPassengerCapacity()) {
            flight.setBookedPassengers(newBooked);
            flight.setPassengerCapacity(newCap);
        } else {
            flight.setPassengerCapacity(newCap);
            flight.setBookedPassengers(newBooked);
        }

        flight.setFlightStatus(newStatus);

        if (newStatus == FlightStatus.CANCELLED) {
            releaseFlightAllocation(flightId);
        }
        return true;
    }

    public boolean deleteFlight(String flightId) {
        Flight flight = findFlightById(flightId);
        if (flight != null) {
            releaseFlightAllocation(flightId);
            flightRegistry.remove(flight);
            return true;
        }
        return false;
    }

    public List<Flight> getAllFlights() {
        return new ArrayList<>(flightRegistry);
    }

    // --- Feature 2: Gate Scheduling Management ---

    public boolean allocateGate(String flightId, int gateIndex, int timeSlotIndex) {
        Flight flight = findFlightById(flightId);
        if (flight == null) return false;

        if (gateIndex < 0 || gateIndex >= 5 || timeSlotIndex < 0 || timeSlotIndex >= 8) return false;
        if (schedule[gateIndex][timeSlotIndex] != null) return false;
        if (isFlightScheduled(flightId)) return false;

        schedule[gateIndex][timeSlotIndex] = flight;
        flight.setDepartureTime(TIME_SLOTS[timeSlotIndex]);
        return true;
    }

    public boolean releaseGate(int gateIndex, int timeSlotIndex) {
        if (gateIndex < 0 || gateIndex >= 5 || timeSlotIndex < 0 || timeSlotIndex >= 8) return false;
        if (schedule[gateIndex][timeSlotIndex] == null) return false;

        schedule[gateIndex][timeSlotIndex] = null;
        return true;
    }

    public boolean releaseFlightAllocation(String flightId) {
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 8; c++) {
                if (schedule[r][c] != null && schedule[r][c].getFlightId().equalsIgnoreCase(flightId)) {
                    schedule[r][c] = null;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean rescheduleFlight(String flightId, int newGateIndex, int newTimeSlotIndex) {
        releaseFlightAllocation(flightId);
        return allocateGate(flightId, newGateIndex, newTimeSlotIndex);
    }

    public boolean isFlightScheduled(String flightId) {
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 8; c++) {
                if (schedule[r][c] != null && schedule[r][c].getFlightId().equalsIgnoreCase(flightId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void displayGateSchedule() {
        System.out.println("\n=========================================================================================");
        System.out.println("                               AIRPORT GATE DEPARTURE SCHEDULE                             ");
        System.out.println("=========================================================================================");
        System.out.printf("%-10s", "Gate");
        for (String slot : TIME_SLOTS) {
            System.out.printf("%-10s", slot);
        }
        System.out.println("\n-----------------------------------------------------------------------------------------");

        for (int r = 0; r < 5; r++) {
            System.out.printf("%-10s", GATES[r]);
            for (int c = 0; c < 8; c++) {
                String val = (schedule[r][c] == null) ? "—" : schedule[r][c].getFlightId();
                System.out.printf("%-10s", val);
            }
            System.out.println();
        }
        System.out.println("=========================================================================================\n");
    }

    public void displayAvailableSlots() {
        System.out.println("\nAvailable Gate Slots:");
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 8; c++) {
                if (schedule[r][c] == null) {
                    System.out.println(GATES[r] + " at " + TIME_SLOTS[c]);
                }
            }
        }
    }

    public void displayOccupiedSlots() {
        System.out.println("\nOccupied Gate Slots:");
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 8; c++) {
                if (schedule[r][c] != null) {
                    System.out.println(GATES[r] + " at " + TIME_SLOTS[c] + " -> Flight: " + schedule[r][c].getFlightId());
                }
            }
        }
    }

    // --- Feature 3: Operational Reports & Sorting ---

    public int getScheduledCount() {
        int count = 0;
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 8; c++) {
                if (schedule[r][c] != null) count++;
            }
        }
        return count;
    }

    public int getDelayedCount() {
        int count = 0;
        for (Flight f : flightRegistry) {
            if (f.getFlightStatus() == FlightStatus.DELAYED) {
                count++;
            }
        }
        return count;
    }

    public String getBusiestGate() {
        int maxCount = -1;
        String busiest = "None";
        for (int r = 0; r < 5; r++) {
            int count = 0;
            for (int c = 0; c < 8; c++) {
                if (schedule[r][c] != null) count++;
            }
            if (count > maxCount) {
                maxCount = count;
                busiest = GATES[r] + " - " + count + " Scheduled Flights";
            }
        }
        return busiest;
    }

    public String getBusiestTimeSlot() {
        int maxCount = -1;
        String busiest = "None";
        for (int c = 0; c < 8; c++) {
            int count = 0;
            for (int r = 0; r < 5; r++) {
                if (schedule[r][c] != null) count++;
            }
            if (count > maxCount) {
                maxCount = count;
                busiest = TIME_SLOTS[c] + " - " + count + " Scheduled Departures";
            }
        }
        return busiest;
    }

    public Flight getHighestPassengerLoadFlight() {
        Flight highest = null;
        double maxLoad = -1.0;
        for (Flight f : flightRegistry) {
            if (f.getPassengerCapacity() > 0) {
                double load = f.getPassengerLoadPercentage();
                if (load > maxLoad) {
                    maxLoad = load;
                    highest = f;
                }
            }
        }
        return highest;
    }

    public double getAveragePassengerLoadPercentage() {
        double totalLoad = 0;
        int count = 0;
        for (Flight f : flightRegistry) {
            if (f.getPassengerCapacity() > 0) {
                totalLoad += f.getPassengerLoadPercentage();
                count++;
            }
        }
        return count == 0 ? 0.0 : totalLoad / count;
    }

    public void generateOperationsReport() {
        System.out.println("==================================================");
        System.out.println("          AEROLINK AIRPORT OPERATIONS REPORT       ");
        System.out.println("==================================================");
        System.out.println("Scheduled Flights: " + getScheduledCount());
        System.out.println("Delayed Flights:   " + getDelayedCount());
        System.out.println("\nBusiest Gate:\n" + getBusiestGate());
        System.out.println("\nBusiest Departure Time:\n" + getBusiestTimeSlot());

        Flight topFlight = getHighestPassengerLoadFlight();
        System.out.println("\nHighest Passenger Load:");
        if (topFlight != null) {
            System.out.println("Flight ID: " + topFlight.getFlightId());
            System.out.println("Booked Passengers: " + topFlight.getBookedPassengers());
            System.out.println("Passenger Capacity: " + topFlight.getPassengerCapacity());
            System.out.printf("Passenger Load: %.2f%%\n", topFlight.getPassengerLoadPercentage());
        } else {
            System.out.println("N/A");
        }

        System.out.printf("\nAverage Passenger Load: %.2f%%\n", getAveragePassengerLoadPercentage());
        System.out.println("==================================================");
    }

    public List<Flight> getFlightsSortedById() {
        List<Flight> sorted = new ArrayList<>(flightRegistry);
        sorted.sort(Comparator.comparing(Flight::getFlightId));
        return sorted;
    }

    public List<Flight> getFlightsSortedByDepartureTime() {
        List<Flight> sorted = new ArrayList<>(flightRegistry);
        sorted.sort(Comparator.comparing(Flight::getDepartureTime));
        return sorted;
    }
}

// ==========================================
// 4. MAIN CONSOLE INTERFACE & UNIT TEST RUNNER
// ==========================================

public class Aerolinksystem {
    private static final GateScheduler scheduler = new GateScheduler();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        runUnitTests();

        boolean exit = false;
        while (!exit) {
            displayMainMenu();
            int choice = getIntInput("Enter choice: ");
            switch (choice) {
                case 1 -> flightManagementMenu();
                case 2 -> gateSchedulingMenu();
                case 3 -> reportsMenu();
                case 4 -> {
                    exit = true;
                    System.out.println("Exiting System. Goodbye!");
                }
                default -> System.out.println("Invalid selection. Try again.");
            }
        }
    }

    private static void runUnitTests() {
        System.out.println("Running automated self-tests...");
        Flight f1 = new Flight("FL100", "AeroAir", "London", "08:00", 200, 150, FlightCategory.INTERNATIONAL, FlightStatus.SCHEDULED);
        boolean registered = scheduler.registerFlight(f1);
        System.out.println("Flight registration test: " + (registered ? "PASSED" : "FAILED"));
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    private static void displayMainMenu() {
        System.out.println("\n=================================");
        System.out.println(" AEROLINK GATE SCHEDULING SYSTEM ");
        System.out.println("=================================");
        System.out.println("1. Flight Management");
        System.out.println("2. Gate Scheduling Management");
        System.out.println("3. Airport Operational Reports");
        System.out.println("4. Exit");
    }

    private static void flightManagementMenu() {
        System.out.println("\n--- Flight Management ---");
        System.out.println("1. Register Flight");
        System.out.println("2. Search Flight");
        System.out.println("3. Update Flight");
        System.out.println("4. Delete Flight");
        System.out.println("5. Display All Flights");
        int choice = getIntInput("Choice: ");

        switch (choice) {
            case 1 -> registerFlightUI();
            case 2 -> searchFlightUI();
            case 3 -> updateFlightUI();
            case 4 -> deleteFlightUI();
            case 5 -> displayAllFlightsUI();
        }
    }

    private static void registerFlightUI() {
        System.out.print("Enter Flight ID: ");
        String id = scanner.nextLine();
        if (scheduler.findFlightById(id) != null) {
            System.out.println("Error: Flight ID already exists!");
            return;
        }
        System.out.print("Enter Airline Name: ");
        String airline = scanner.nextLine();
        System.out.print("Enter Destination: ");
        String dest = scanner.nextLine();
        System.out.print("Enter Departure Time (e.g. 08:00): ");
        String time = scanner.nextLine();
        int cap = getIntInput("Enter Capacity: ");
        int booked = getIntInput("Enter Booked Passengers: ");

        if (cap < 0 || booked < 0 || booked > cap) {
            System.out.println("Error: Invalid capacity or booked passenger values!");
            return;
        }

        System.out.println("Select Category: 1. DOMESTIC  2. INTERNATIONAL  3. CARGO");
        int catChoice = getIntInput("Choice: ");
        FlightCategory category = switch (catChoice) {
            case 2 -> FlightCategory.INTERNATIONAL;
            case 3 -> FlightCategory.CARGO;
            default -> FlightCategory.DOMESTIC;
        };

        Flight flight;
        if (category == FlightCategory.INTERNATIONAL) {
            System.out.print("Enter Terminal: ");
            String terminal = scanner.nextLine();
            System.out.print("Enter Boarding Gate: ");
            String gate = scanner.nextLine();
            System.out.print("Requires Customs Clearance (true/false): ");
            boolean customs = scanner.nextBoolean();
            scanner.nextLine();
            flight = new InternationalFlight(id, airline, dest, time, cap, booked, FlightStatus.SCHEDULED, terminal, gate, customs);
        } else {
            flight = new Flight(id, airline, dest, time, cap, booked, category, FlightStatus.SCHEDULED);
        }

        if (scheduler.registerFlight(flight)) {
            System.out.println("Flight registered successfully!");
        } else {
            System.out.println("Failed to register flight.");
        }
    }

    private static void searchFlightUI() {
        System.out.print("Enter Flight ID to search: ");
        String id = scanner.nextLine();
        Flight f = scheduler.findFlightById(id);
        if (f != null) {
            f.displayDetails();
        } else {
            System.out.println("Flight not found.");
        }
    }

    private static void updateFlightUI() {
        System.out.print("Enter Flight ID to update: ");
        String id = scanner.nextLine();
        Flight f = scheduler.findFlightById(id);
        if (f == null) {
            System.out.println("Flight not found.");
            return;
        }

        System.out.print("Enter New Airline Name: ");
        String airline = scanner.nextLine();
        System.out.print("Enter New Destination: ");
        String dest = scanner.nextLine();
        System.out.print("Enter New Departure Time: ");
        String time = scanner.nextLine();
        int cap = getIntInput("Enter New Capacity: ");
        int booked = getIntInput("Enter New Booked Passengers: ");

        System.out.println("Select Status: 1. SCHEDULED 2. BOARDING 3. DELAYED 4. DEPARTED 5. CANCELLED");
        int statusChoice = getIntInput("Choice: ");
        FlightStatus status = switch (statusChoice) {
            case 2 -> FlightStatus.BOARDING;
            case 3 -> FlightStatus.DELAYED;
            case 4 -> FlightStatus.DEPARTED;
            case 5 -> FlightStatus.CANCELLED;
            default -> FlightStatus.SCHEDULED;
        };

        if (scheduler.updateFlight(id, airline, dest, time, cap, booked, status)) {
            System.out.println("Flight updated successfully!");
        } else {
            System.out.println("Failed to update flight.");
        }
    }

    private static void deleteFlightUI() {
        System.out.print("Enter Flight ID to delete: ");
        String id = scanner.nextLine();
        if (scheduler.deleteFlight(id)) {
            System.out.println("Flight deleted successfully!");
        } else {
            System.out.println("Flight not found.");
        }
    }

    private static void displayAllFlightsUI() {
        List<Flight> flights = scheduler.getAllFlights();
        if (flights.isEmpty()) {
            System.out.println("No flights registered.");
            return;
        }
        for (Flight f : flights) {
            System.out.println("--------------------------------");
            f.displayDetails();
        }
    }

    private static void gateSchedulingMenu() {
        System.out.println("\n--- Gate Scheduling Management ---");
        System.out.println("1. Display Schedule Matrix");
        System.out.println("2. Allocate Gate Slot");
        System.out.println("3. Release Gate Slot");
        System.out.println("4. Reschedule Flight");
        System.out.println("5. Display Available Slots");
        System.out.println("6. Display Occupied Slots");
        int choice = getIntInput("Choice: ");

        switch (choice) {
            case 1 -> scheduler.displayGateSchedule();
            case 2 -> {
                System.out.print("Enter Flight ID: ");
                String id = scanner.nextLine();
                int gateIdx = getIntInput("Enter Gate Index (0-4): ");
                int slotIdx = getIntInput("Enter Time Slot Index (0-7): ");
                if (scheduler.allocateGate(id, gateIdx, slotIdx)) {
                    System.out.println("Gate allocated successfully.");
                } else {
                    System.out.println("Failed to allocate gate.");
                }
            }
            case 3 -> {
                int gateIdx = getIntInput("Enter Gate Index (0-4): ");
                int slotIdx = getIntInput("Enter Time Slot Index (0-7): ");
                if (scheduler.releaseGate(gateIdx, slotIdx)) {
                    System.out.println("Gate slot released.");
                } else {
                    System.out.println("Slot already empty or invalid index.");
                }
            }
            case 4 -> {
                System.out.print("Enter Flight ID: ");
                String id = scanner.nextLine();
                int gateIdx = getIntInput("Enter New Gate Index (0-4): ");
                int slotIdx = getIntInput("Enter New Time Slot Index (0-7): ");
                if (scheduler.rescheduleFlight(id, gateIdx, slotIdx)) {
                    System.out.println("Flight rescheduled successfully.");
                } else {
                    System.out.println("Failed to reschedule flight.");
                }
            }
            case 5 -> scheduler.displayAvailableSlots();
            case 6 -> scheduler.displayOccupiedSlots();
        }
    }

    private static void reportsMenu() {
        System.out.println("\n--- Operational Reports ---");
        System.out.println("1. Operations Summary Report");
        System.out.println("2. Display Flights Sorted by ID");
        System.out.println("3. Display Flights Sorted by Departure Time");
        int choice = getIntInput("Choice: ");

        switch (choice) {
            case 1 -> scheduler.generateOperationsReport();
            case 2 -> {
                List<Flight> list = scheduler.getFlightsSortedById();
                for (Flight f : list) {
                    System.out.println(f.getFlightId() + " - " + f.getAirlineName() + " to " + f.getDestination());
                }
            }
            case 3 -> {
                List<Flight> list = scheduler.getFlightsSortedByDepartureTime();
                for (Flight f : list) {
                    System.out.println(f.getDepartureTime() + " - " + f.getFlightId() + " to " + f.getDestination());
                }
            }
        }
    }
}