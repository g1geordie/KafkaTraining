package com.geordie.jaas;

import java.security.Principal;

public class MyPrinciple implements Principal, java.io.Serializable {

    private String name;

    public MyPrinciple(String name) {
        if (name == null)
            throw new NullPointerException("illegal null input");

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return ("SamplePrincipal:  " + name);
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof MyPrinciple))
            return false;
        MyPrinciple that = (MyPrinciple) o;

        if (this.getName().equals(that.getName()))
            return true;
        return false;
    }

    public int hashCode() {
        return name.hashCode();
    }
}