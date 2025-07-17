import java.io.*;
import java.util.*;

public class HotelBookingSystem {
    // Room class
    static class Room {
        private int roomNumber;
        private String category;
        private boolean isAvailable;

        public Room(int roomNumber, String category, boolean isAvailable) {
            this.roomNumber = roomNumber;
            this.category = category;
            this.isAvailable = isAvailable;
        }

        public int getRoomNumber() { return roomNumber; }
        public String getCategory() { return category; }
        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { isAvailable = available; }

        @Override
        public String toString() {
            return roomNumber + "," + category + "," + isAvailable;
        }

        public static Room fromString(String line) {
            String[] parts = line.split(",");
            return new Room(Integer.parseInt(parts[0]), parts[1], Boolean.parseBoolean(parts[2]));
        }
    }

    // Booking class
    static class Booking {
        private String userName;
        private String email;
        private int roomNumber;
        private String category;

        public Booking(String userName, String email, int roomNumber, String category) {
            this.userName = userName;
            this.email = email;
            this.roomNumber = roomNumber;
            this.category = category;
        }

        public int getRoomNumber() { return roomNumber; }
        public String getEmail() { return email; }
        public String getCategory() { return category; }

        @Override
        public String toString() {
            return userName + "," + email + "," + roomNumber + "," + category;
        }

        public static Booking fromString(String line) {
            String[] parts = line.split(",");
            return new Booking(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]);
        }
    }

    // Main Hotel System Logic
    static class HotelSystem {
        private static final String ROOMS_FILE = "rooms.txt";
        private static final String BOOKINGS_FILE = "bookings.txt";

        private List<Room> rooms = new ArrayList<>();
        private List<Booking> bookings = new ArrayList<>();

        public HotelSystem() {
            loadRooms();
            loadBookings();
        }

        private void loadRooms() {
            try (BufferedReader br = new BufferedReader(new FileReader(ROOMS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    rooms.add(Room.fromString(line));
                }
            } catch (IOException e) {
                System.out.println("No rooms file found. Creating default rooms...");
                createDefaultRooms();
                saveRooms();
            }
        }

        private void createDefaultRooms() {
            for (int i = 101; i <= 105; i++) rooms.add(new Room(i, "Standard", true));
            for (int i = 201; i <= 203; i++) rooms.add(new Room(i, "Deluxe", true));
            for (int i = 301; i <= 302; i++) rooms.add(new Room(i, "Suite", true));
        }

        private void saveRooms() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
                for (Room room : rooms) {
                    bw.write(room.toString());
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving rooms.");
            }
        }

        private void loadBookings() {
            try (BufferedReader br = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    bookings.add(Booking.fromString(line));
                }
            } catch (IOException e) {
                System.out.println("No bookings file found. Starting fresh.");
            }
        }

        private void saveBookings() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
                for (Booking booking : bookings) {
                    bw.write(booking.toString());
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving bookings.");
            }
        }

        public void showAvailableRooms(String category) {
            boolean found = false;
            for (Room room : rooms) {
                if (room.getCategory().equalsIgnoreCase(category) && room.isAvailable()) {
                    System.out.println("Room " + room.getRoomNumber() + " is available.");
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No available rooms in category: " + category);
            }
        }

        public void bookRoom(String name, String email, String category) {
            for (Room room : rooms) {
                if (room.getCategory().equalsIgnoreCase(category) && room.isAvailable()) {
                    room.setAvailable(false);
                    Booking booking = new Booking(name, email, room.getRoomNumber(), category);
                    bookings.add(booking);
                    saveRooms();
                    saveBookings();
                    simulatePayment(name);
                    System.out.println("Room " + room.getRoomNumber() + " booked successfully!");
                    return;
                }
            }
            System.out.println("No available rooms in category: " + category);
        }

        public void cancelBooking(int roomNumber, String email) {
            Booking toCancel = null;
            for (Booking b : bookings) {
                if (b.getRoomNumber() == roomNumber && b.getEmail().equalsIgnoreCase(email)) {
                    toCancel = b;
                    break;
                }
            }

            if (toCancel != null) {
                bookings.remove(toCancel);
                for (Room room : rooms) {
                    if (room.getRoomNumber() == roomNumber) {
                        room.setAvailable(true);
                        break;
                    }
                }
                saveRooms();
                saveBookings();
                System.out.println("Booking canceled successfully.");
            } else {
                System.out.println("Booking not found for room " + roomNumber + " and email " + email);
            }
        }

        public void viewBookings(String email) {
            boolean found = false;
            for (Booking b : bookings) {
                if (b.getEmail().equalsIgnoreCase(email)) {
                    System.out.println("Booking -> Room: " + b.getRoomNumber() + ", Category: " + b.getCategory());
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No bookings found for email: " + email);
            }
        }

        private void simulatePayment(String userName) {
            System.out.println("Processing payment for " + userName + "...");
            System.out.println("Payment successful.");
        }
    }

    // Main menu
    public static void main(String[] args) {
        HotelSystem system = new HotelSystem();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Hotel Booking System ===");
            System.out.println("1. Show Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View My Bookings");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter room category (Standard/Deluxe/Suite): ");
                    String category = scanner.nextLine();
                    system.showAvailableRooms(category);
                }
                case 2 -> {
                    System.out.print("Enter your name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter your email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter room category (Standard/Deluxe/Suite): ");
                    String category = scanner.nextLine();
                    system.bookRoom(name, email, category);
                }
                case 3 -> {
                    System.out.print("Enter room number to cancel: ");
                    int roomNumber = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter your email: ");
                    String email = scanner.nextLine();
                    system.cancelBooking(roomNumber, email);
                }
                case 4 -> {
                    System.out.print("Enter your email: ");
                    String email = scanner.nextLine();
                    system.viewBookings(email);
                }
                case 0 -> System.out.println("Exiting the system. Goodbye!");
                default -> System.out.println("Invalid option. Please try again.");
            }
        } while (choice != 0);
    }
}
