package com.vem.model;

import com.google.gson.annotations.SerializedName;

public class LogJson {
    @SerializedName("policyid")
    private Integer policyId;

    @SerializedName("srcport")
    private Integer sourcePort;

    @SerializedName("sentbyte")
    private Integer sentByte;

    @SerializedName("dstintf")
    private String destinationInterface;

    @SerializedName("srcip")
    private String sourceIp;

    @SerializedName("srcintfrole")
    private String sourceInterfaceRole;

    @SerializedName("srcname")
    private String sourceName;

    @SerializedName("srcintf")
    private String sourceInterface;

    @SerializedName("trandisp")
    private String transactionDisposition;

    @SerializedName("devname")
    private String deviceName;

    @SerializedName("dstport")
    private Integer destinationPort;

    @SerializedName("proto")
    private Integer protocol;

    @SerializedName("dstintfrole")
    private String destinationInterfaceRole;

    @SerializedName("dstip")
    private String destinationIp;

    // Getter methods
    public Integer getPolicyId() {
        return policyId;
    }

    public Integer getSourcePort() {
        return sourcePort;
    }

    public Integer getSentByte() {
        return sentByte;
    }

    public String getDestinationInterface() {
        return destinationInterface;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getSourceInterfaceRole() {
        return sourceInterfaceRole;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getSourceInterface() {
        return sourceInterface;
    }

    public String getTransactionDisposition() {
        return transactionDisposition;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Integer getDestinationPort() {
        return destinationPort;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public String getDestinationInterfaceRole() {
        return destinationInterfaceRole;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    @Override
    public String toString() {
        return "LogJson{" +
                "policyId=" + policyId +
                ", sourcePort=" + sourcePort +
                ", sentByte=" + sentByte +
                ", destinationInterface='" + destinationInterface + '\'' +
                ", sourceIp='" + sourceIp + '\'' +
                ", sourceInterfaceRole='" + sourceInterfaceRole + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", sourceInterface='" + sourceInterface + '\'' +
                ", transactionDisposition='" + transactionDisposition + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", destinationPort=" + destinationPort +
                ", protocol=" + protocol +
                ", destinationInterfaceRole='" + destinationInterfaceRole + '\'' +
                ", destinationIp='" + destinationIp + '\'' +
                '}';
    }
}
