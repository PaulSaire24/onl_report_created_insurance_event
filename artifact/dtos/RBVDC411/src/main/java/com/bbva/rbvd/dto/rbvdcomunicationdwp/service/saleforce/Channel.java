package com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce;

public class Channel {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                '}';
    }
}
