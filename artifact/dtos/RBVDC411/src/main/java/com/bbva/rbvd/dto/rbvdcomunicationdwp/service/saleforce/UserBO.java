package com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce;

import java.time.LocalDate;

public class UserBO {
    private String user;
    private String date;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "auditUser{" +
                "user='" + user + '\'' +
                ", date=" + date +
                '}';
    }
}
