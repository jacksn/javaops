package ru.javaops.config.exception;

public class NoPartnerException extends RuntimeException {
    private String partnerKey;

    public NoPartnerException(String partnerKey) {
        this.partnerKey = partnerKey;
    }

    public String getPartnerKey() {
        return partnerKey;
    }
}
