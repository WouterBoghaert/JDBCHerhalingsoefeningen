package be.vdab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
// herschrijven met gebruik Set! => add returnt false als waarde al in Set zit
public class OmzetLeegmakenMain {
    private static final String URL = "jdbc:mysql://localhost/bieren?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String OMZET_LEEGMAKEN =
        "update brouwers set omzet = null where id = ?";
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)){
            List<Integer> brouwernummers = new ArrayList<>();
            System.out.println("Geef een brouwernummer op, 0 om te stoppen.");
            int id = scanner.nextInt();
            while(id != 0){
                if (id <=0){
                    System.out.println("Brouwernummer moet groter dan nul zijn!");
                }
                else {
                    final int brouwernummer = id;
                    if(brouwernummers.stream().anyMatch(nummer -> nummer ==  brouwernummer)){
                        System.out.println("Dit brouwernummer is al ingegeven!");
                    }
                    else {
                        brouwernummers.add(id);
                    }
                }
                System.out.println("Geef het volgende brouwernummer op, 0 om te stoppen.");
                id = scanner.nextInt();
            }
            System.out.println(brouwernummers);
            try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(OMZET_LEEGMAKEN)){
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                brouwernummers.stream().forEach(nummer -> {
                    try {
                        statement.setInt(1, nummer);
                        statement.addBatch();
                    }
                    catch(SQLException ex){
                        ex.printStackTrace();
                    }
                });
                int[] updates = statement.executeBatch();
                connection.commit();
                System.out.println("Aantal brouwers geüpdate: " +
                    Arrays.stream(updates).sum());
                Integer [] brouwernrs = brouwernummers.stream().toArray(aantal -> new Integer [aantal]);
                for (int i=0; i<updates.length;i++){
                    if(updates[i]==0){
                        System.out.println("Brouwernummer " + brouwernrs[i] + " bestaat niet en is niet geüpdate");
                    }
                }
            }
            catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        catch(InputMismatchException ex){
            System.out.println("Verkeerde input!");
        }
    }
    
}
