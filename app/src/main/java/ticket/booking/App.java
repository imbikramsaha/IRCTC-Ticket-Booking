package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.service.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {
        System.out.println("Running IRCTC Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        UserBookingService userBookingService = null;
        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            System.out.println("There is something wrong.");
        }

        while (option != 7) {
            System.out.println("Choose option:");
            System.out.println("1 -> Sign Up");
            System.out.println("2 -> Login");
            System.out.println("3 -> Fetch Booking");
            System.out.println("4 -> Search Trains");
            System.out.println("5 -> Book a Seat");
            System.out.println("6 -> Cancel Booking");
            System.out.println("7 -> Exit");
            option = scanner.nextInt();

            Train trainSelectedForBooking = new Train();
            switch (option) {
                case 1:
                    System.out.println("Enter the username to sign up");
                    String nameToSignUp = scanner.next();
                    System.out.println("Enter the password to sign up");
                    String passwordToSignUp = scanner.next();
                    User userToSignUp = new User(nameToSignUp, passwordToSignUp, UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(), UUID.randomUUID().toString());
                    userBookingService.signUp(userToSignUp);
                    break;

                case 2:
                    System.out.println("Enter the username to login");
                    String nameToLogin = scanner.next();
                    System.out.println("Enter the password to login");
                    String passwordToLogin = scanner.next();
                    User userToLogin = new User(nameToLogin, passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin), new ArrayList<>(), UUID.randomUUID().toString());
                    try {
                        userBookingService = new UserBookingService(userToLogin);
                    } catch (IOException ex) {
                        return;
                    }
                    break;

                case 3:
                    System.out.println("Fetching your booking");
                    userBookingService.fetchBooking();
                    break;

                case 4:
                    System.out.println("Type your source station");
                    String source = scanner.next();
                    System.out.println("Type your destination station");
                    String destination = scanner.next();
                    List<Train> trains = userBookingService.getTrains(source, destination);
                    int index = 1;
                    for (Train t : trains) {
                        System.out.println(index + " Train Id: " + t.getTrainId());
                        for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                            System.out.println("Station " + entry.getKey() + " time: " + entry.getValue());
                        }
                        index++;
                    }
                    System.out.println("Select the train by typing 1, 2, 3...");
                    trainSelectedForBooking = trains.get(scanner.nextInt() - 1);
                    break;

                case 5:
                    System.out.println("Select a seat out of these seats");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                    int row = 0;
                    for (List<Integer> seat : seats) {
                        for (int s = 0; s < seat.size(); s++) {
                            if (seat.get(s) == 0) {
                                System.out.print("Available -> Row " + row + " Seat " + s);
                            }
                        }
                        row++;
                    }
                    System.out.println("Enter the row you want to book");
                    int rowToBook = scanner.nextInt();
                    System.out.println("Enter the seat you want to book");
                    int seatToBook = scanner.nextInt();
                    userBookingService.bookTrainSeat(trainSelectedForBooking, rowToBook, seatToBook);
                    break;

                case 6:
                    System.out.println("Enter the ticket ID");
                    String ticketId = scanner.next();
                    userBookingService.cancelBooking(ticketId);
                    break;

                case 7:
                    System.out.println("Exit");
                    break;

                default:
                    System.out.println("Please type a valid option.");
            }
        }
    }
}

