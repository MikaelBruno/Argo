package com.vem.config;
import java.util.List;
import com.arcadedb.remote.*;

public class ConfigArcadeDB {
    public static void main(String[] args) {
        RemoteServer server = new RemoteServer("localhost", 2480, "root", "playwithdata");
        RemoteDatabase database = new RemoteDatabase("localhost", 2480, "Vem", "root", "playwithdata");

        if (!server.exists("Vem")){
            server.create("Vem");
        }
        
        List<String> databases = server.databases();
        assert(databases.contains("Vem"));

        String schema = """
            CREATE EDGE TYPE SourceToDestination;

            CREATE PROPERTY SourceToDestination.trandip STRING;
            CREATE PROPERTY SourceToDestination.dstport INTEGER;
            CREATE PROPERTY SourceToDestination.srcintf STRING;
            CREATE PROPERTY SourceToDestination.policyid INTEGER;
            CREATE PROPERTY SourceToDestination.protocol INTEGER;
            CREATE PROPERTY SourceToDestination.source STRING;
            CREATE PROPERTY SourceToDestination.destination STRING;
            CREATE PROPERTY SourceToDestination.srcport INTEGER;
            CREATE PROPERTY SourceToDestination.device_id STRING;
            CREATE PROPERTY SourceToDestination.sentbyte INTEGER;
            CREATE PROPERTY SourceToDestination.dstintf STRING;
            CREATE PROPERTY SourceToDestination.conn_matches STRING;
            CREATE PROPERTY SourceToDestination.last_seen DATETIME;

            CREATE VERTEX TYPE Host;

            CREATE PROPERTY Host.ip STRING;
            CREATE PROPERTY Host.interface STRING;
            """;
            
        database.command("sql", schema);
        
        System.out.println("Database configurato.");
        database.close();
    }
}