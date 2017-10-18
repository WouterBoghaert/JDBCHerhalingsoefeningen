package be.vdab;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;
// herschrijven met gebruik Set! => add returnt false als waarde al in Set zit
public class OmzetLeegmakenMainAndersBeter {
    private static final String URL = "jdbc:mysql://localhost/bieren?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String SELECT_BROUWERID =
        "select id from brouwers where id in (";
    private static final String OMZET_LEEGMAKEN =
        "update brouwers set omzet = null where id in (";
    
    // methods
    
    private static Set<Integer> vraagBrouwernummers(){
        Set<Integer> brouwernummers = new HashSet<>();
        try(Scanner scanner = new Scanner(System.in)){            
            System.out.println("Geef een brouwernummer op, 0 om te stoppen.");
            int id = scanner.nextInt();
            while(id != 0){
                if (id <=0){
                    System.out.println("Brouwernummer moet groter dan nul zijn!");
                }
                else {
                    if(!brouwernummers.add(id)){
                        System.out.println("Dit brouwernummer is al ingegeven!");
                    }
                }  
                System.out.println("Geef het volgende brouwernummer op, 0 om te stoppen.");
                id = scanner.nextInt();
            }
        }
        catch(InputMismatchException ex){
            System.out.println("Verkeerde input!");
        }
        return brouwernummers;
    }
    
    private static int updateBrouwers(Set<Integer> brouwernummers, Connection connection) throws SQLException {
        StringBuilder updateSQL = new StringBuilder(OMZET_LEEGMAKEN);
        for (int id: brouwernummers){
            updateSQL.append("? ,");
        }
        updateSQL.replace(updateSQL.length()-1, updateSQL.length(), ")");
        
        try(PreparedStatement statementUpdate = connection.prepareStatement(updateSQL.toString())){
            int index = 1;
            for (int id: brouwernummers){
                statementUpdate.setInt(index++, id);
            }
            return statementUpdate.executeUpdate();
        }        
    }
    
    private static void checkOnbestaand(Set<Integer> brouwernummers, Connection connection) throws SQLException {
        StringBuilder selectSQL = new StringBuilder(SELECT_BROUWERID);
        for (int id: brouwernummers){
            selectSQL.append("? ,");
        }
        selectSQL.replace(selectSQL.length()-1, selectSQL.length(), ")");
        
        try (PreparedStatement statementSelect = connection.prepareStatement(selectSQL.toString())){
            int index = 1;
            for (int id: brouwernummers){
                statementSelect.setInt(index++, id);
            }
            try(ResultSet resultSet = statementSelect.executeQuery()){
                while(resultSet.next()){
                    brouwernummers.remove(resultSet.getInt("id"));
                }
            }
        }
    }
    
    
    
    public static void main(String[] args) {
        Set<Integer> brouwernummers = vraagBrouwernummers();
        try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            if(updateBrouwers(brouwernummers, connection) != brouwernummers.size()){
                checkOnbestaand(brouwernummers, connection);
            }
            connection.commit();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        for(int id: brouwernummers){
            System.out.println("Brouwernummer " + id + " bestaat niet en is niet geüpdate");
        }
    }
    
}
