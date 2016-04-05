/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsrelatedness;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bibliodigital
 */
public class NewMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, Exception {
//Distance dd = new Distance();

        //      System.out.println(dd.traductorBing("hola mundo cruel"));
        //    if (true)
        //   return ;
        List<String> Authors = new ArrayList();
        List<String> Endpoints = new ArrayList();

        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ACURIO_DEL_PINO__SANTIAGO");
        Authors.add("http://190.15.141.66:8899/puce/contribuyente/ACURIO_DEL_PINO__SANTIAGO");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ACURIO_PAEZ__FAUSTO_DAVID");

        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/CHUCHUCA__VICTOR");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/SAQUICELA__VICTOR");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/SAQUICELA_GALARZA__VICTOR_HUGO");
        Authors.add("http://190.15.141.66:8899/cedia/contribuyente/SAQUICELA__VICTOR");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/SAQUICELA__V");
        Authors.add("http://190.15.141.66:8899/puce/contribuyente/CEVALLOS__VICTOR");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/AREVALO__VICTOR");
        Authors.add("http://190.15.141.66:8899/puce/contribuyente/DEL_PINO__EMILIA");
        Authors.add("http://190.15.141.66:8899/uce/contribuyente/ACURIO_ACURIO__JAIME_NEPTALI");
        Authors.add("http://190.15.141.66:8899/uide/contribuyente/ACURIO_DEL_PINO__SANTIAGO");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ESPINOZA__MAURICIO");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ESPINOZA_ESPINOZA__JHONNY_MAURICIO");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ASTUDILLO_ESPINOZA__CHRISTIAN_MAURICIO");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ESPINOZA_MEJIA__JORGE_MAURICIO");
        Authors.add("http://190.15.141.66:8899/cedia/contribuyente/ESPINOZA__MAURICIO");

        Authors.add("http://190.15.141.66:8899/puce/contribuyente/ESPINOZA__OSWALDO");
        Authors.add("http://190.15.141.66:8899/puce/contribuyente/ESPINOZA_VITERI__OSWALDO");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ESPINOZA_VEINTIMILLA__ANGEL_OSWALDO");
        Authors.add("http://190.15.141.66:8899/ucuenca/contribuyente/ENCALADA_ESPINOZA__OSWALDO_JAVIER");

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
                System.out.println(i + "," + j + "=" + NWD);
            }
        }

    }

}
