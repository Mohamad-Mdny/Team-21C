package backend.models;

import java.util.ArrayList;

public class User {
    ArrayList<Item> Basket = new ArrayList<>();

    public User(){

    }

    public void addItem(Item item){
        Basket.add(item);
    }

    public void removeItem(Item item){
        Basket.remove(item);
    }

    public ArrayList<Item> getBasket(){
        return Basket;
    }
}
