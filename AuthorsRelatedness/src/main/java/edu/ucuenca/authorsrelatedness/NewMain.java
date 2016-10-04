/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsrelatedness;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author bibliodigital
 */
public class NewMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, Exception {
        
        Distance dss = new Distance();
        
        List<String> consultado2 = dss.consultado2("http://190.15.141.66:8899/espe/contribuyente/ANDRADE_FUENTES__EDWIN_PAUL", "http://190.15.141.66:8892/myservice/sparql");
        
        for (String c : consultado2){
            System.out.println(""+dss.traductorYandex(c)+"   "+c);
        }
        
        
        
        if (true)
            return ;
        
        
//Distance dd = new Distance();

        //      System.out.println(dd.traductorBing("hola mundo cruel"));
        //    if (true)
        //   return ;
        List<String> Authors = new ArrayList();
        List<String> Endpoints = new ArrayList();

        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ACURIO_DEL_PINO__SANTIAGO");//0
        Authors.add("http://190.15.141.66:8899/puce/contribuyente/ACURIO_DEL_PINO__SANTIAGO");//1
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ACURIO_PAEZ__FAUSTO_DAVID");//2
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/CHUCHUCA__VICTOR");//3
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/SAQUICELA__VICTOR");//4
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/SAQUICELA_GALARZA__VICTOR_HUGO");//5
        Authors.add("http://190.15.141.66:8899/cedia/contribuyente/SAQUICELA__VICTOR");//6
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/SAQUICELA__V");//7
        Authors.add("http://190.15.141.66:8899/puce/contribuyente/CEVALLOS__VICTOR");//8
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/AREVALO__VICTOR");//9
        Authors.add("http://190.15.141.66:8899/puce/contribuyente/DEL_PINO__EMILIA");//10
        Authors.add("http://190.15.141.66:8899/uce/contribuyente/ACURIO_ACURIO__JAIME_NEPTALI");//11
        Authors.add("http://190.15.141.66:8899/uide/contribuyente/ACURIO_DEL_PINO__SANTIAGO");//12
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ESPINOZA__MAURICIO");//13
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ESPINOZA_ESPINOZA__JHONNY_MAURICIO");//14
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ASTUDILLO_ESPINOZA__CHRISTIAN_MAURICIO");//15
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ESPINOZA_MEJIA__JORGE_MAURICIO");//16
        Authors.add("http://190.15.141.66:8899/cedia/contribuyente/ESPINOZA__MAURICIO");//17
        Authors.add("http://190.15.141.66:8899/puce/contribuyente/ESPINOZA__OSWALDO");//18
        Authors.add("http://190.15.141.66:8899/puce/contribuyente/ESPINOZA_VITERI__OSWALDO");//19
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ESPINOZA_VEINTIMILLA__ANGEL_OSWALDO");//20
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ENCALADA_ESPINOZA__OSWALDO_JAVIER");//21

        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.66:8893/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");

        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.66:8890/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.66:8893/myservice/query");

        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.66:8893/myservice/query");
        Endpoints.add("http://190.15.141.66:8891/myservice/query");
        Endpoints.add("http://190.15.141.66:8895/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.66:8890/myservice/query");

        Endpoints.add("http://190.15.141.66:8893/myservice/query");
        Endpoints.add("http://190.15.141.66:8893/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");
        Endpoints.add("http://190.15.141.102:8891/myservice/query");

        for (int i = 0; i < Authors.size(); i++) {
            for (int j = i + 1; j < Authors.size(); j++) {
                Distance d = new Distance();
                double NWD = d.NWD(Authors.get(i), Endpoints.get(i), Authors.get(j), Endpoints.get(j), "");
                if (NWD < 0.85) {
                    System.out.println(i + "," + j + "=" + NWD);
                }
            }
        }

    }

}
