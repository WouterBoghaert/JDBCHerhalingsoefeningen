package be.vdab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BierenVanEenSoort {
    private static final String URL = "jdbc:mysql://localhost/bieren?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String SELECT_BIEREN_VAN_SOORT = 
//        "select bieren.naam " +
//        "from bieren inner join soorten on bieren.soortid = soorten.id " +
//        "where soorten.naam = ?";
        "select naam from bieren where soortid = ?";
    private static final String SELECT_SOORT = 
        "select id from soorten where naam = ?";
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)){
            System.out.println("Geef de biersoort op:");
            String soort = scanner.nextLine();
            try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement statementSoort = connection.prepareStatement(SELECT_SOORT)){
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                statementSoort.setString(1, soort);
                try(ResultSet resultSetSoort = statementSoort.executeQuery()){
                    if (resultSetSoort.next()){
                        try(PreparedStatement statementBier = connection.prepareStatement(SELECT_BIEREN_VAN_SOORT)){
                            statementBier.setInt(1,resultSetSoort.getInt("id"));
                            try(ResultSet resultSetBier = statementBier.executeQuery()){
                                while(resultSetBier.next()){
                                    System.out.println(resultSetBier.getString("naam"));
                                }
                            }
                        }
                    }
                    else {
                        System.out.println("Deze soort is niet gevonden.");
                    }
                }
                connection.commit();
            }
            catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
    
}
