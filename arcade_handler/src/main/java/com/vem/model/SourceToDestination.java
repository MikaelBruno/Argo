package com.vem.model;

import java.time.LocalDateTime;

public class SourceToDestination {
    private String trandip;
    private int dstport;
    private String srcnic;
    private int policyid;
    private int protocol;
    private String source;
    private String destination;
    private int srcport;
    private String device_id;
    private int sentbyte;
    private String dstnic;
    private String conn_matches;
    private LocalDateTime last_seen;

    // Getters and setters
    public String getTrandip() {
        return trandip;
    }

    public void setTrandip(String trandip) {
        this.trandip = trandip;
    }

    public int getDstport() {
        return dstport;
    }

    public void setDstport(int dstport) {
        this.dstport = dstport;
    }

    public String getSrcnic() {
        return srcnic;
    }

    public void setSrcnic(String srcintf) {
        this.srcnic = srcintf;
    }

    public int getPolicyid() {
        return policyid;
    }

    public void setPolicyid(int policyid) {
        this.policyid = policyid;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getSrcport() {
        return srcport;
    }

    public void setSrcport(int srcport) {
        this.srcport = srcport;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public int getSentbyte() {
        return sentbyte;
    }

    public void setSentbyte(int sentbyte) {
        this.sentbyte = sentbyte;
    }

    public String getDstnic() {
        return dstnic;
    }

    public void setDstnic(String dstintf) {
        this.dstnic = dstintf;
    }

    public String getConn_matches() {
        return conn_matches;
    }

    public void setConn_matches(String conn_matches) {
        this.conn_matches = conn_matches;
    }

    public LocalDateTime getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(LocalDateTime last_seen) {
        this.last_seen = last_seen;
    }
}
