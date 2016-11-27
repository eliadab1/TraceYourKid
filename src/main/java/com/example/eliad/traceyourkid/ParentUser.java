package com.example.eliad.traceyourkid;

/**
 * Created by eliad on 05/04/2016.
 */
public class ParentUser {

    String fullName,password,email,userName,age;


    public ParentUser(String fullName, String password, String email, String userName, String age) {
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.userName = userName;
        this.age = age;

    }


    @Override
    public String toString() {
        return "ParentUser{" +
                "fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}
