/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsrelatedness;


import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author bibliodigital
 */
public class NewMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, Exception {

        
        //http://190.15.141.66:8899/uce/contribuyente/ABAD_CARDENAS__XAVIER_SEGUNDO|
        //http://190.15.141.66:8891/myservice/query|
        //http://190.15.141.66:8899/ucuenca/contribuyente/ABAD_CARDENAS__XAVIER_SEGUNDO|
        //http://190.15.141.66:8893/myservice/query|NaN

        
        Distance a = new Distance();
        double s =a.NWD(
                "http://190.15.141.66:8899/uce/contribuyente/ABAD_CARDENAS__XAVIER_SEGUNDO", 
                "http://190.15.141.66:8891/myservice/query", 
                "http://190.15.141.66:8899/ucuenca/contribuyente/ABAD_CARDENAS__XAVIER_SEGUNDO", 
                "http://190.15.141.102:8891/myservice/query", "");
        
        
        System.out.println(""+s);
        
    }

}
