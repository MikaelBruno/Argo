package com.vem.handler;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


import com.arcadedb.database.MutableDocument;
import com.arcadedb.database.RID;
import com.arcadedb.graph.MutableEdge;
import com.arcadedb.graph.MutableVertex;
import com.arcadedb.graph.Vertex;
import com.arcadedb.query.sql.executor.Result;
import com.arcadedb.query.sql.executor.ResultSet;
import com.arcadedb.remote.RemoteDatabase;
import com.arcadedb.remote.RemoteServer;
import com.arcadedb.serializer.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vem.model.LogJson;


public class ArcadeDBHandler {
    
    public void main(String message) {
        
        // flag per indicare se i vertici vengono creati
        boolean isInsert = false;
        RID ridSource;
        RID ridDestination;
        Vertex sourceHostV;
        Vertex destinationHostV;

        // trasformo il fakeJson in un oggetto 
        Gson gson = new Gson();
        LogJson log = gson.fromJson(message, LogJson.class);
        String timestamp = log.getTimeStamp().toString();
        System.out.println("il mio time stamp è = " + timestamp);

        // connessione al DB
        RemoteDatabase database = new RemoteDatabase("192.168.241.35", 2480, "Vem", "root", "playwithdata");
        


        // Composizione query e parametri 
        String querySelectHost = "select * from Host where ip = :ip AND interface = :interface";


        Map<String,Object> sourceHostParameters = new HashMap<>();
            sourceHostParameters.put("ip", log.getSourceIp());
            sourceHostParameters.put("interface", log.getSourceInterface());


        Map<String,Object> destinationHostParameters = new HashMap<>();
            destinationHostParameters.put("ip", log.getDestinationIp());
            destinationHostParameters.put("interface", log.getDestinationInterface());


        //query al dispositivo
        ResultSet resultsetSourceHost = database.query("sql", querySelectHost, sourceHostParameters);
        ResultSet resultsetDestinationeHost = database.query("sql", querySelectHost, destinationHostParameters);

        

        //prende il primo record della lista (qui dovrebbe sempre essercene uno o zero)
        if ( resultsetSourceHost.hasNext() ) {
            Result record = resultsetSourceHost.next();
            sourceHostV = record.getVertex().get();


        } else {
            // allora non esiste il record e deve essere creato
            sourceHostV = database.newVertex("Host").set("ip", log.getSourceIp()).set("interface", log.getSourceInterface()).save();
            isInsert = true;
        }
        


        //prende il primo record della lista (qui dovrebbe sempre essercene uno o zero)
        if ( resultsetDestinationeHost.hasNext() ) {
            Result record = resultsetDestinationeHost.next();
            destinationHostV = record.getVertex().get();


        } else {
            // allora non esiste il record e deve essere creato
            destinationHostV = database.newVertex("Host").set("ip", log.getDestinationIp()).set("interface", log.getDestinationInterface()).save();
            isInsert = true;
        }
        


        // in ogni caso (creati o recuperati) a noi interessano i RID di sourceHost e destinationHost per recuperare gli edge collegati ad essi
        // ovviamente se creiamo uno dei due vertici o entrambi allora non esiste un edge tra di loro

        if (isInsert) {
            // qui creiamo l'edge
            // String insertEdgeQuery = "CREATE EDGE SourceToDestination FROM (SELECT FROM Host WHERE @rid = :ridSource) TO (SELECT FROM Host WHERE @rid = :ridDestination) SET " +
            // "trandisp = :trandisp, dstport = :dstport, srcintf = :srcintf, policyid = :policyid, protocol = :protocol, source = :source, destination = :destination, " +
            // "srcport = :srcport, device_id = :device_id, sentbyte = :sentbyte, dstintf = :dstintf, conn_matches = :conn_matches, last_seen = :last_seen";

            // MutableEdge edge = sourceHostV.newEdge("Know", destinationHostV, true).save();

        } else {
            // qui cerchiamo tra gli edge se c'è uno con le stesse proprietà, se c'è ci salviamo il rid e aggiorniamo i byte (se ci sono) e last seen (datetime)
            // altrimenti ne creiamo uno nuovo


        }
        database.close();
    }
}
