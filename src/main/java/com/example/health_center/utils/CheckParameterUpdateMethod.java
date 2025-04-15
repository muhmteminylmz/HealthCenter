package com.example.health_center.utils;


import com.example.health_center.entity.abstracts.User;
import com.example.health_center.payload.request.abstracts.BaseUserRequest;

public class CheckParameterUpdateMethod{

    public static boolean checkParameter(User user, BaseUserRequest baseUserRequest){

        return user.getTc().equalsIgnoreCase(baseUserRequest.getTc())
                && user.getPhoneNumber().equalsIgnoreCase(baseUserRequest.getPhoneNumber())
                && user.getUsername().equalsIgnoreCase(baseUserRequest.getUsername());
    }
}
