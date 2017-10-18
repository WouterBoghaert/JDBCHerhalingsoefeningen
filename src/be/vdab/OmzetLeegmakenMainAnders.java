package be.vdab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;
// herschrijven met gebruik Set! => add returnt false als waarde al in Set zit
public class OmzetLeegmakenMainAnders {
    private static final String URL = "jdbc:mysql://localhost/bieren?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String SELECT_BROUWERID =
        "select id from brouwers where id = ?";
    private static final String OMZET_LEEGMAKEN =
        "update brouwers set omzet = null where id = ?";
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)){
            Set<Integer> brouwernummers = new HashSet<>();
            System.out.println("Geef een brouwernummer op, 0 om te stoppen.");
            int id = scanner.nextInt();
            while(id != 0){
                if (id <=0){
                    System.out.println("Brouwernummer moet groter dan nul zijn!");
                }
                else {
                    try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        PreparedStatement statementSelect = connection.prepareStatement(SELECT_BROUWERID)){
                        connection.setAutoCommit(false);
                        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                        statementSelect.setInt(1, id);
                        try(ResultSet resultSet = statementSelect.executeQuery()){
                            if (resultSet.next()){
                                if(!brouwernummers.add(id)){
                                    System.out.println("Dit brouwernummer is al ingegeven!");
                                }
                            }
                            else {
                                System.out.println("Brouwernummer " + id + " bestaat niet.");
                            }
                        }
                        connection.commit();
                    }
                    catch(SQLException ex){
                        ex.printStackTrace();
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
