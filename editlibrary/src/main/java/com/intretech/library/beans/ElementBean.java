package com.intretech.library.beans;


public abstract class ElementBean implements Comparable {
    protected int type;
    private int index;

    public ElementBean(){
        setType();
    }

    public int getType() {
        return type;
    }

    public abstract ElementBean setType();

    public int getIndex() {
        return index;
    }

    public ElementBean setIndex(int index) {
        this.index = index;
        return this;
    }

    @Override
    public int compareTo(Object another){
        return index - ((ElementBean) another).getIndex();
    }
}
