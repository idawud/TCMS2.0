package io.turntabl.dataaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.turntabl.dataentry.DataEntry;
import io.turntabl.menu.AnsiConsole;
import io.turntabl.menu.Printer;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataAccess {

    public void showAllClientsRecords() {
        Optional<List<Client>> clients = RestAPIConsume.getClients("https://mysterious-peak-14776.herokuapp.com/customer");
        if ( clients.isPresent()){
            printRecords(clients.get());
        }else {
            System.out.println(AnsiConsole.RED + "NO Records yet!" + AnsiConsole.RESET);
        }
    }

    private static void printRecords(List<Client> records) {
        if (records.size() == 0) {
            Printer.recordNotFound();
        } else {
            records.forEach(Printer::printClientCard);
        }
    }

    public void showSearchedClientsRecords() throws SQLException {
        String name = DataEntry.getStringInput("Enter Client's name: ");
        Optional<List<Client>> clients = RestAPIConsume.getClients("https://mysterious-peak-14776.herokuapp.com/customer/search?name=" + name);
        if ( clients.isPresent()){
            printRecords(clients.get());
        }else {
            System.out.println(AnsiConsole.RED + "NO Records yet name " + name + " !!"  + AnsiConsole.RESET);
        }
    }

    public void deleteClientRecord() throws Exception {
        String name = DataEntry.getStringInput("Enter Client's Name: ");

        Optional<List<Client>> clients = RestAPIConsume.getClients("https://mysterious-peak-14776.herokuapp.com/customer/search?name=" + name);
        if (clients.isPresent() && clients.get().size() != 0){
            List<Client> records = clients.get();
            records.forEach(Printer::printClientCardWithId);
            List<Integer> validIds = records.stream().map(Client::getId).collect(Collectors.toList());

            int id = getId("\nEnter the ID to be deleted: ");
            if (validIds.contains(id)) {
                Optional<Client> deletedClient = RestAPIConsume.delete("https://mysterious-peak-14776.herokuapp.com/customer/" + id);
                if ( deletedClient.isPresent()){
                    System.out.println(AnsiConsole.GREEN + "\nClient Record with id=" + id + " deleted Successfully!" + AnsiConsole.RESET);
                }
                else  { oops(id); }
            }
            else { oops(id); }

        }
        else {
            Printer.recordNotFound();
        }

    }


    public void recoverDeleteClientRecord() throws Exception {
        String name = DataEntry.getStringInput("Enter Client's Name: ");

        Optional<List<Client>> clients = RestAPIConsume.getClients("https://mysterious-peak-14776.herokuapp.com/customer/search/r/?name=" + name);
        if (clients.isPresent() && clients.get().size() != 0){
            List<Client> records = clients.get();
            records.forEach(Printer::printClientCardWithId);
            List<Integer> validIds = records.stream().map(Client::getId).collect(Collectors.toList());

            int id = getId("\nEnter the ID to be recovered: ");
            if (validIds.contains(id)) {
                Optional<Client> deletedClient = RestAPIConsume.recover("https://mysterious-peak-14776.herokuapp.com/customer/retrieve/" + id);
                if ( deletedClient.isPresent()){
                    System.out.println(AnsiConsole.GREEN + "\nClient Record with id=" + id + " recovered Successfully!" + AnsiConsole.RESET);
                }
                else  { oops(id); }
            }
            else { oops(id); }

        }
        else {
            Printer.recordNotFound();
        }
    }

    private static int getId(String s){
        Scanner input = new Scanner(System.in);
        System.out.print(s);
        int id = -99999;
        try {
            id = input.nextInt();
        }catch (Exception ignored){
        }
        return id;
    }

    public void entry() throws Exception {
        Client client = DataEntry.getClientInformation();
        String toJsonString = RestAPIConsume.clientObjectToJsonString(client);
        boolean post = RestAPIConsume.post("https://mysterious-peak-14776.herokuapp.com/customer/", toJsonString);
        if (post){
            System.out.println(AnsiConsole.GREEN + "New Client '" + client.getName() + "', added Successfully!" + AnsiConsole.RESET);
        }else {
            System.out.println(AnsiConsole.RED + "Oops! something went wrong adding new client '" + client.getName() + "', try again later!" + AnsiConsole.RESET);
        }
    }

    private void oops(int id) {
        System.out.println(AnsiConsole.RED + "Oops! Client with id " + id +
                " does not exist " + AnsiConsole.RESET);
    }
}
