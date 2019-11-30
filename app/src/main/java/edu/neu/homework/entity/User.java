package edu.neu.homework.entity;

import org.litepal.crud.DataSupport;

import lombok.Data;

@Data
public class User extends DataSupport {

    private Integer id;

    private String name;

    private String email;

    private String password;

}
