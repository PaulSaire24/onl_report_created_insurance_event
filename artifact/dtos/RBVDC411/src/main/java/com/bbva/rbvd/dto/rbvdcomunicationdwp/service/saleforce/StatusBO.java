package com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce;

public class StatusBO {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StatusBO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
