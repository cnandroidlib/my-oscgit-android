package com.bill.mytest;

import java.io.Serializable;

/**
 * Created by liaobb on 2015/8/2.
 */
public class Project implements Serializable{
    private String name;
    private String age;

    public Project(String age, String name) {
        this.age = age;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {

        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
