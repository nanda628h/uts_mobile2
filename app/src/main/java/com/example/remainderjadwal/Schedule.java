package com.example.remainderjadwal;

import java.io.Serializable;
import java.util.UUID;

public class Schedule implements Serializable {
    private String id;
    private String dosen;
    private String ruangan;
    private String jam; // format: "HH:mm" (24h)
    private String catatan;
    private int reminderMinutes; // 0 = none, 10, or 5

    public Schedule(String dosen, String ruangan, String jam, String catatan, int reminderMinutes) {
        this.id = UUID.randomUUID().toString();
        this.dosen = dosen;
        this.ruangan = ruangan;
        this.jam = jam;
        this.catatan = catatan;
        this.reminderMinutes = reminderMinutes;
    }

    public String getId() { return id; }
    public String getDosen() { return dosen; }
    public String getRuangan() { return ruangan; }
    public String getJam() { return jam; }
    public String getCatatan() { return catatan; }
    public int getReminderMinutes() { return reminderMinutes; }

    public void setDosen(String dosen) { this.dosen = dosen; }
    public void setRuangan(String ruangan) { this.ruangan = ruangan; }
    public void setJam(String jam) { this.jam = jam; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
    public void setReminderMinutes(int reminderMinutes) { this.reminderMinutes = reminderMinutes; }
}
