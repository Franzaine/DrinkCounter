package se.franzaine.nfc.drinkcounter;

public class PartyPerson {
    String name;
    int drinksLeft;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrinksLeft() {
        return drinksLeft;
    }

    public void setDrinksLeft(int drinksLeft) {
        this.drinksLeft = drinksLeft;
    }

    public PartyPerson(String name, int drinksLeft) {

        this.name = name;
        this.drinksLeft = drinksLeft;
    }
}
