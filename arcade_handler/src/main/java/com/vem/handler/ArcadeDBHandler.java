package com.vem.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import com.google.gson.JsonObject;
import com.vem.model.LogJson;

public class ArcadeDBHandler {
    
    public void main(String message) {
        // flag per indicare se i vertici vengono creati
        boolean isInsert = false;
        Vertex sourceHostV;
        Vertex destinationHostV;

        // trasformo il fakeJson in un oggetto 
        Gson gson = new Gson();
        LogJson log = gson.fromJson(message, LogJson.class);

        // connessione al DB
        RemoteDatabase database = new RemoteDatabase("localhost", 2480, "Vem", "root", "playwithdata");

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
            Map<String, Object> sourceToDestinationParameters = new HashMap<>();
                sourceToDestinationParameters.put("trandip", log.getTransactionDisposition());
                sourceToDestinationParameters.put("dst_port", log.getDestinationPort());
                sourceToDestinationParameters.put("src_nic", log.getSourceInterface());
                sourceToDestinationParameters.put("policy_id", log.getPolicyId());
                sourceToDestinationParameters.put("protocol", log.getProtocol());
                sourceToDestinationParameters.put("source_ip", log.getSourceIp());
                sourceToDestinationParameters.put("destination_ip", log.getDestinationIp());
                sourceToDestinationParameters.put("src_port", log.getSourcePort());
                sourceToDestinationParameters.put("device_id", log.getDeviceName());
                sourceToDestinationParameters.put("sent_byte", Optional.ofNullable(log.getSentByte()).orElse(0));
                sourceToDestinationParameters.put("dst_nic", log.getDestinationInterface());
                sourceToDestinationParameters.put("conn_matches", 0); 
            // TODO gestire last_seen

            System.out.println(sourceToDestinationParameters);
            System.out.println("ciapo");

            MutableEdge edge = sourceHostV.newEdge("SourceToDestination", destinationHostV, true, sourceToDestinationParameters).save(); // edge in questo caso embedded direzionale da source a destination

            
        } else {
            // qui cerchiamo tra gli edge se c'è uno con le stesse proprietà, se c'è ci salviamo il rid e aggiorniamo i byte (se ci sono) e last seen (datetime)
            // altrimenti ne creiamo uno nuovo

        }
        database.close();
    }
}
