package be.vdab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OmzetNietGekendMain {
    private static final String URL = "jdbc:mysql://localhost/bieren?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String SELECT_BROUWERS_OMZET_NIET_GEKEND =
        "select brouwers.naam as brouwersnaam, count(*) as aantalBieren " +
        "from bieren inner join brouwers on bieren.brouwerid = brouwers.id " +
        "group by brouwers.id, brouwers.naam, brouwers.omzet " +
        "having brouwers.omzet is null";
    public static void main(String[] args) {
        try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            try(ResultSet resultSet = statement.executeQuery(SELECT_BROUWERS_OMZET_NIET_GEKEND)){
                System.out.println("Brouwersnaam\tAantal bieren");
                while(resultSet.next()){
                    System.out.println(resultSet.getString("brouwersnaam") + "\t" +
                        resultSet.getInt("aantalBieren"));
                }
            }
            connection.commit();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }    
}
