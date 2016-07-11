package com.example.rav.part4projectv1;

/**
 * Created by Rav on 30/04/2016.
 */
public class FirebaseDataAnalyser
{
    private String battery_voltage;
    private String load_current;
    private String security_switch;
    private String solar_voltage;
    private String time;

    public FirebaseDataAnalyser(String battery_voltage,String load_current,String security_switch, String solar_voltage, String time)
    {
        this.battery_voltage = battery_voltage;
        this.load_current = load_current;
        this.security_switch = security_switch;
        this.solar_voltage = solar_voltage;
        this.time = time;
    }

    public String getBattery_voltage()
    {
        return battery_voltage;
    }
    public String getLoad_current()
    {
        return load_current;
    }
    public String getSecurity_switch()
    {
        return security_switch;
    }
    public String getSolar_voltage()
    {
        return solar_voltage;
    }
    public String getTime()
    {
        return time;
    }
}
