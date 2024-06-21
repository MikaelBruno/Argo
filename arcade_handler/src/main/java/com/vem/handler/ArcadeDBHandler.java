package com.vem.handler;

import java.util.HashMap;
import java.util.Map;

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
        String ridSource = "";
        String ridDestination = "";

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
            // questo estraggo il RID
            // TODO: cercare un metodo migliore per estrarre il RID
            JSONObject json =  record.toJSON();
            ridSource = json.getString("@rid"); // questo dovrebbe ridare il RID del record #<bucket-identifier>:<record-position>.
        } else {
            // allora non esiste il record e deve essere creato
            isInsert = true;
        }

        //prende il primo record della lista (qui dovrebbe sempre essercene uno o zero)
        if ( resultsetDestinationeHost.hasNext() ) {
            Result record = resultsetDestinationeHost.next();
            // questo estraggo il RID
            // TODO: cercare un metodo migliore per estrarre il RID
            JSONObject json =  record.toJSON();
            ridDestination = json.getString("@rid"); // questo dovrebbe ridare il RID del record #<bucket-identifier>:<record-position>.
        } else {
            // allora non esiste il record e deve essere creato
            isInsert = true;
        }

        // in ogni caso (creati o recuperati) a noi interessano i RID di sourceHost e destinationHost per recuperare gli edge collegati ad essi
        // ovviamente se creiamo uno dei due vertici o entrambi allora non esiste un edge tra di loro

        if (isInsert) {
            // qui creiamo l'edge
        } else {
            // qui cerchiamo tra gli edge se c'è uno con le stesse proprietà, se c'è ci salviamo il rid e aggiorniamo i byte (se ci sono) e last seen (datetime)
            // altrimenti ne creiamo uno nuovo
            String queryEdgeSourceToDestination = "MATCH {type: SourceToDestination, as: link, where: (@in = :ridDestination and @out = :ridSource )} RETURN link";
            Map<String,Object> edgeSourceToDestinationParameters = new HashMap<>();
                edgeSourceToDestinationParameters.put("ridSource", ridSource);
                edgeSourceToDestinationParameters.put("ridDestination", ridDestination);
            
            ResultSet resultEdgeSourceToDestination = database.query("sql", queryEdgeSourceToDestination, edgeSourceToDestinationParameters);
            
            while (resultEdgeSourceToDestination.hasNext()) {
                Result record = resultEdgeSourceToDestination.next();
                JSONObject json =  record.toJSON();
                System.out.println(json.toString());
            }
        }
         
        

        database.close();
    }
}
