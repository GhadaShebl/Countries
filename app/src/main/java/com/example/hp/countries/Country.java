package com.example.hp.countries;

public class Country
{
    String Name;
    String Capital;
    String Flag;

    public Country(String name, String capital, String flag) {
        Name = name;
        Capital = capital;
        Flag = flag;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCapital() {
        return Capital;
    }

    public void setCapital(String capital) {
        Capital = capital;
    }

    public String getFlag() {
        return Flag;
    }

    public void setFlag(String flag) {
        Flag = flag;
    }
}
