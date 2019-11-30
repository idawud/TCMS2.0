package io.turntabl.dataentry;

import io.turntabl.dataaccess.Client;

import java.util.Scanner;

public class DataEntry {
    private static final Scanner INPUT = new Scanner(System.in);

    public static Client getClientInformation() {
        String name = getStringInput("Full Name: ");
        String address = getStringInput("Address: ");
        String telephoneNumber = getStringInput("Telephone number: ");
        String email = getStringInput("Email Address: ");

        Client client = new Client();
        client.setName(name);
        client.setAddress(address);
        client.setTelephoneNumber(telephoneNumber);
        client.setEmail(email);

        return client;
    }

    public static String getStringInput(String s) {
        System.out.print(s);
        return ( INPUT.nextLine() );
    }
}
