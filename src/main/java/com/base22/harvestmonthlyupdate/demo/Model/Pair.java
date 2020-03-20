package com.base22.harvestmonthlyupdate.demo.Model;



public class Pair<K, V> {

    K item_1;
    V item_2;

    public K getItem_1() {
        return item_1;
    }

    public void setItem_1(K item_1) {
        this.item_1 = item_1;
    }

    public V getItem_2() {
        return item_2;
    }

    public void setItem_2(V item_2) {
        this.item_2 = item_2;
    }



    public Pair(K item_1, V item_2) {
        this.item_1 = item_1;
        this.item_2 = item_2;
    }






}