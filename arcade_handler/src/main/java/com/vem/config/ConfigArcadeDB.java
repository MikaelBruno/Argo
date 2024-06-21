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
            CREATE EDGE TYPE SourceToDestination IF NOT EXIST;

            CREATE PROPERTY SourceToDestination.trandip STRING;
            CREATE PROPERTY SourceToDestination.dst_port INTEGER;
            CREATE PROPERTY SourceToDestination.src_nic STRING;
            CREATE PROPERTY SourceToDestination.policy_id INTEGER;
            CREATE PROPERTY SourceToDestination.protocol INTEGER;
            CREATE PROPERTY SourceToDestination.source_ip STRING;
            CREATE PROPERTY SourceToDestination.destination_ip STRING;
            CREATE PROPERTY SourceToDestination.src_port INTEGER;
            CREATE PROPERTY SourceToDestination.device_id STRING;
            CREATE PROPERTY SourceToDestination.sent_byte INTEGER;
            CREATE PROPERTY SourceToDestination.dst_nic STRING;
            CREATE PROPERTY SourceToDestination.conn_matches STRING;
            CREATE PROPERTY SourceToDestination.last_seen DATETIME;

            CREATE VERTEX TYPE Host IF NOT EXIST;

            CREATE PROPERTY Host.ip STRING;
            CREATE PROPERTY Host.interface STRING;
            """;
            
        database.command("sql", schema);
        
        System.out.println("Database configurato.");
        database.close();
    }
}