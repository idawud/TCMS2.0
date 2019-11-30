package io.turntabl;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.turntabl.dataaccess.DataAccess;
import io.turntabl.menu.AnsiConsole;
import java.sql.SQLException;

public class ThreadProcessing {
    private DataAccess dataAccess = new DataAccess();

    public void operation(int option) throws InterruptedException {
        switch (option){
            case 1:
                Thread entry = new Thread(()-> {
                    try {
                        dataAccess.entry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                entry.start();
                entry.join();
                break;
            case 2:
                Thread clientsDisplay = new Thread(()-> {
                    dataAccess.showAllClientsRecords();
                });
                clientsDisplay.start();
                clientsDisplay.join();
                break;
            case 3:
                Thread records = new Thread(()-> {
                    try {
                        dataAccess.showSearchedClientsRecords();
                    }catch (SQLException ignored) { }
                });
                records.start();
                records.join();
                break;
            case 4:
                Thread delete = new Thread(()-> {
                    try {
                        dataAccess.deleteClientRecord();
                    } catch (SQLException ignored) { } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                delete.start();
                delete.join();
                break;
            case 5:
                Thread recover = new Thread(()-> {
                    dataAccess.recoverDeleteClientRecord();
                });
                recover.start();
                recover.join();
                break;
            case 6:
                System.exit(0);
            default:
                System.out.println( AnsiConsole.RED + "\nYou Entered an InCorrect Option!" + AnsiConsole.RESET);
        }
    }

}
