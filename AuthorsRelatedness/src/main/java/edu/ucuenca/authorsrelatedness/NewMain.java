/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsrelatedness;

import com.google.common.base.Joiner;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

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
