package com.vem;
import com.vem.handler.ArcadeDBHandler;

public class Main {
    static String fakeJson = """
                {
                       "policyid" : "23",
                        "srcport" : "64994",
                       "sentbyte" : "67",
                        "dstintf" : "eth1",
                          "srcip" : "192.168.1.1",
                    "srcintfrole" : "undefined",
                        "srcname" : "AD",
                        "srcintf" : "eth0",
                       "trandisp" : "snat",
                        "devname" : "FGT_LabSecurity",
                        "dstport" : "53",
                          "proto" : "17",
                    "dstintfrole" : "undefined",
                          "dstip" : "10.0.0.1"
                }
            """;
            
    public static void main(String[] args) {
        ArcadeDBHandler handler = new ArcadeDBHandler();
        handler.main(fakeJson);
    }
}
