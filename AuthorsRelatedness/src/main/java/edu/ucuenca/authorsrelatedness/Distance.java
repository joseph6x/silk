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
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
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
public class Distance {

    // JDBC driver name and database URL
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://";

    //  Database credentials
    String USER = "usr";
    String PASS = "amd";

    Connection conn = null;
    Statement stmt = null;

    JsonObject config = null;

    public Distance() throws IOException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/config.cnf");
        //String readFile = readFile("./config.cnf", Charset.defaultCharset());
        String theString = IOUtils.toString(resourceAsStream, Charset.defaultCharset()); 
        config = JSON.parse(theString).getAsObject();
        
        
        DB_URL = DB_URL + config.get("dbServer").getAsString().value() + "/" + config.get("dbSchema").getAsString().value();
        USER = config.get("dbUser").getAsString().value();
        PASS = config.get("dbPassword").getAsString().value();

    }

    String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    /**
     * @param args the command line arguments
     */
    public synchronized double NWD(String uri1, String end1, String uri2, String end2, String quy) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        conn = DriverManager.getConnection(DB_URL, USER, PASS);

        Map<String, List<String>> map = new HashMap<>();

        if (true) {

            //  System.out.println(NGD("cuenca","semantic web"));
            // System.out.println(NGD("cuenca","ecuador"));
            // return;
        }

        List<String> Authors = new ArrayList();
        Authors.add(uri1);
        Authors.add(uri2);
        /*
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
         */
        List<String> Endpoints = new ArrayList();
        Endpoints.add(end1);
        Endpoints.add(end2);
        /*      Endpoints.add("http://190.15.141.102:8891/myservice/query");
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
         */
        Map<String, Double> Result = new HashMap<>();

        double avg = 0;
        double har = 0;

        double thres = 0.65;

        for (int i = 0; i < Authors.size(); i++) {
            for (int j = i + 1; j < Authors.size(); j++) {

                String a1 = Authors.get(i);
                String a2 = Authors.get(j);
                List<String> ka1 = null;
                List<String> ka2 = null;
                if (map.containsKey(a1)) {
                    ka1 = map.get(a1);
                } else {
                    ka1 = consultado2(a1, Endpoints.get(i));
                    String t1_ = traductor(Joiner.on(" | ").join(ka1)).toLowerCase();
                    ka1 = new LinkedList<String>(java.util.Arrays.asList(t1_.split("\\s\\|\\s")));
                    ka1 = clean(ka1);
                    ka1 = TopT(ka1, (int) (2.0 * Math.log(ka1.size())));
                    map.put(a1, ka1);
                }

                if (map.containsKey(a2)) {
                    ka2 = map.get(a2);
                } else {
                    ka2 = consultado2(a2, Endpoints.get(j));
                    String t2_ = traductor(Joiner.on(" | ").join(ka2)).toLowerCase();
                    ka2 = new LinkedList<String>(java.util.Arrays.asList(t2_.split("\\s\\|\\s")));
                    ka2 = clean(ka2);
                    ka2 = TopT(ka2, (int) (2.0 * Math.log(ka2.size())));
                    map.put(a2, ka2);
                }
                //System.out.println(ka1.size() + "," + ka2.size());

                double sum = 0;
                double num = 0;

                double sum2 = 0;

                for (String t1 : ka1) {
                    for (String t2 : ka2) {
                        num++;
                        String tt1 = t1;
                        String tt2 = t2;
                        double v = NGD(tt1, tt2);
                        sum += v;

                        //System.out.println(tt1 + "," + tt2 + "=" + v);
                    }
                }
                double prom = sum / num;
                //System.out.println(i + "," + j + "=" + prom);
                Result.put(i + "," + j, prom);

                if (avg == 0) {
                    avg = prom;
                } else {
                    avg = (avg + prom) / 2;
                }
                if (har == 0) {
                    har = prom;
                } else {
                    har = 2 / (1 / har + 1 / prom);
                }

            }
        }

        double r = 0;
        for (Map.Entry<String, Double> cc : Result.entrySet()) {
            r = cc.getValue();
        }

        return r;
    }

    public List<String> TopT(List<String> m, int n) throws IOException, SQLException {
        Map<String, Double> Mapa = new HashMap();
        for (int i = 0; i < m.size(); i++) {
            for (int j = i + 1; j < m.size(); j++) {
                double v = NGD(m.get(i), m.get(j));
                //System.out.print(i+"/"+m.size()+"\t");

                if (Mapa.containsKey(m.get(i))) {
                    Mapa.put(m.get(i), Mapa.get(m.get(i)) + v);
                } else {
                    Mapa.put(m.get(i), v);
                }

                if (Mapa.containsKey(m.get(j))) {
                    Mapa.put(m.get(j), Mapa.get(m.get(j)) + v);
                } else {
                    Mapa.put(m.get(j), v);
                }
            }
        }
        Map<String, Double> sortByValue = sortByValue(Mapa);
        List<String> ls = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList(sortByValue.keySet());
        ArrayList<Double> arrayList2 = new ArrayList(sortByValue.values());
        for (int i = 0; i < n; i++) {
            if (i < sortByValue.size()) {
                ls.add(arrayList.get(i));
                // System.out.println(arrayList.get(i)+"__"+arrayList2.get(i));
            }
        }

        return ls;
    }

    public List<String> consultado2(String ent, String end) {
        List<String> lista = new ArrayList();
        /*
         String consulta2 = " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                          + "PREFIX esdbpr: <http://es.dbpedia.org/resource/> "+ 
                           " PREFIX owl: <http://dbpedia.org/ontology/> "+
        "SELECT ?person WHERE{ "+
        "?person  rdf:type              owl:Scientist . "+
        "?person  owl:country  esdbpr:Ecuador.  }"; */

        String entidad = ent;
        String endpoint = end;

        String consulta = config.get("contextQuery").getAsString().value().replaceAll("\\|\\?\\|", entidad);

        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);

        // QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/fct/service", query);
        //System.out.println("Ejecutando consulta");
        ResultSet resultado = qexec.execSelect();
        //System.out.println("Fin consulta");

        try {
            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                //System.out.println(soln.getLiteral("d").getString());
                lista.add(soln.getLiteral("d").getString());
                // System.out.println ( "Val "+soln.getResource("d").getLocalName());

            }

            return lista;
// ResultSet results = qexec.execSelect();
//QuerySolution solucion = results.nextSolution();
//ResultSetFormatter.out(System.out, results);
        } catch (Exception e) {
            System.out.println("Verificar consulta, no existen datos para mostrar");
        } finally {
            qexec.close();

        }
        return lista;
    }

    private double NGD(String a, String b) throws IOException, SQLException {

        a = a.trim();
        b = b.trim();

        //double n0 = getResultsCount(""+a+"");
        //double n1 = getResultsCount(""+b+"");
        //String c = ""+a+" "+b+"";
        double n0 = getResultsCount("\"" + a + "\"~10");
        double n1 = getResultsCount("\"" + b + "\"~10");
        String c = "\"" + a + " " + b + "\"~50";

        double n2 = getResultsCount(c);
        //double m = 5026040.0 * 590;

        double m = 5029469;

        double distance = 0;

        int Measure = 0;

        double l1 = Math.max(Math.log10(n0), Math.log10(n1)) - Math.log10(n2);
        double l2 = Math.log10(m) - Math.min(Math.log10(n0), Math.log10(n1));

        if (Measure == 0) {
            distance = l1 / l2;
        }

        if (Measure == 1) {
            distance = 1 - (Math.log10(n2) / Math.log10(n0 + n1 - n2));
        }

        if (n0 == 0 || n1 == 0 || n2 == 0) {
            distance = 1;
        }

        //System.out.println("n0="+n0);
        //System.out.println("n1="+n1);
        //System.out.println("n2="+n2);
        //System.out.println(a + "," + b + "=" + distance2);
        return distance;
    }

    public String traductorBing(String palabras) {

        Translate.setClientId("fedquest");
        Translate.setClientSecret("ohCuvdnTlx8Sac4r7gfqyHy0xOJJpKK9duFC4tn9Sho=");
        String translatedText;
        try {
            translatedText = Translate.execute(palabras, Language.ENGLISH);

            return translatedText;
        } catch (Exception ex) {

            try {
                String[] ls = palabras.split("\\s\\|\\s");
                int chunk = ls.length / 2; // chunk size to divide
                String pal = "";
                for (int i = 0; i < ls.length; i += chunk) {
                    String[] pr = java.util.Arrays.copyOfRange(ls, i, i + chunk);
                    pr = clean2(pr);
                    String u = Joiner.on(" | ").join(pr);
                    u = traductorBing(u);
                    pal += u + " ";

                }

                return pal;

            } catch (Exception exx) {
                exx.printStackTrace(new PrintStream(System.out));
            }

        }

        return palabras;
    }

    private double getResultsCount(final String query) throws IOException, SQLException {

        double c = 0;
        c = getResultsCount1(query);
        return c;
    }

    private double getResultsCount1(final String query) throws IOException, SQLException {

        String url = "https://en.wikipedia.org/w/api.php?action=query&list=search&format=json&srsearch=" + URLEncoder.encode(query, "UTF-8");
        String s = Http(url);
        JsonObject parse = JSON.parse(s);
        double v = 0;

        try {
            v = parse.get("query").getAsObject().get("searchinfo").getAsObject().get("totalhits").getAsNumber().value().doubleValue();
        } catch (Exception e) {
            System.out.println(query + s);
        }
        return v;
    }

    public List<String> clean(List<String> ls) {
        List<String> al = ls;
        Set<String> hs = new HashSet<>();
        hs.addAll(al);
        al.clear();
        al.addAll(hs);

        
        JsonArray asArray = config.get("stopwords").getAsArray();
        
        for (JsonValue s:asArray){
            al.remove(s.getAsString().value());
        }
        
        

        return al;
    }

    public <K, V extends Comparable<? super V>> Map<K, V>
            sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list
                = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public String[] clean2(final String[] v) {
        List<String> list = new ArrayList<String>(java.util.Arrays.asList(v));
        list.removeAll(Collections.singleton(null));
        return list.toArray(new String[list.size()]);
    }

    public String Http(String s) throws SQLException, IOException {

        stmt = conn.createStatement();
        String sql;
        sql = "SELECT * FROM cache where cache.key='" + getMD5(s) + "'";
        java.sql.ResultSet rs = stmt.executeQuery(sql);
        String resp = "";
        if (rs.next()) {
            resp = rs.getString("value");
            //System.out.println("ok c..");
        } else {
            final URL url = new URL(s);
            final URLConnection connection = url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:44.0) Gecko/20100101 Firefox/44.0");
            connection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8");
            while (reader.hasNextLine()) {
                final String line = reader.nextLine();
                resp += line + "\n";
            }
            reader.close();

            try {
                PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO cache (cache.key, value) values (?, ?)");
                stmt2.setString(1, getMD5(s));
                stmt2.setString(2, resp);
                stmt2.executeUpdate();
                stmt2.close();
            } catch (Exception e) {

            }

        }
        rs.close();
        stmt.close();
        return resp;
    }

    public String Http2(String s, Map<String, String> mp) throws SQLException, IOException {
        String md = s + mp.toString();
        stmt = conn.createStatement();
        String sql;
        sql = "SELECT * FROM cache where cache.key='" + getMD5(md) + "'";
        java.sql.ResultSet rs = stmt.executeQuery(sql);
        String resp = "";
        if (rs.next()) {
            resp = rs.getString("value");
            //System.out.println("ok c..");
        } else {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(s);

            //Add any parameter if u want to send it with Post req.
            for (Entry<String, String> mcc : mp.entrySet()) {
                method.addParameter(mcc.getKey(), mcc.getValue());
            }

            int statusCode = client.executeMethod(method);

            if (statusCode != -1) {
                InputStream in = method.getResponseBodyAsStream();
                final Scanner reader = new Scanner(in, "UTF-8");
                while (reader.hasNextLine()) {
                    final String line = reader.nextLine();
                    resp += line + "\n";
                }
                reader.close();
                try {
                    PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO cache (cache.key, value) values (?, ?)");
                    stmt2.setString(1, getMD5(md));
                    stmt2.setString(2, resp);
                    stmt2.executeUpdate();
                    stmt2.close();
                } catch (Exception e) {

                }

            }

        }
        rs.close();
        stmt.close();
        return resp;
    }

    public String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String traductorYandex(String palabras) throws UnsupportedEncodingException, SQLException, IOException {
        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate";

        //String url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20160321T160516Z.43cfb95e23a69315.6c0a2ae19f56388c134615f4740fbb1d400f15d3&lang=en&text=" + URLEncoder.encode(palabras, "UTF-8");
        Map<String, String> mp = new HashMap<>();
        mp.put("key", "trnsl.1.1.20160321T160516Z.43cfb95e23a69315.6c0a2ae19f56388c134615f4740fbb1d400f15d3");
        mp.put("lang", "en");
        mp.put("text", palabras);
        
        try {
            String Http = Http2(url, mp);
            String res = Http;
            JsonObject parse = JSON.parse(res).getAsObject();
            JsonArray asArray = parse.get("text").getAsArray();
            res = asArray.get(0).getAsString().value();
            palabras = res;
        } catch (Exception e) {
            try {
                String[] ls = palabras.split("\\s\\|\\s");
                int chunk = ls.length / 2; // chunk size to divide
                String pal = "";
                for (int i = 0; i < ls.length; i += chunk) {
                    String[] pr = java.util.Arrays.copyOfRange(ls, i, i + chunk);
                    pr = clean2(pr);
                    String u = Joiner.on(" | ").join(pr);
                    u = traductorYandex(u);
                    pal += u + " ";

                }

                return pal;

            } catch (Exception exx) {
                exx.printStackTrace(new PrintStream(System.out));
            }
        }

        return palabras;

    }

    private String traductor(String join) throws SQLException, IOException {
        String v = traductorYandex(join);
        //System.out.println(join+"     "+v);
        return v;
    }

}
