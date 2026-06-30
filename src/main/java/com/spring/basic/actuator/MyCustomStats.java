package com.spring.basic.actuator;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id="my-custom-stats")
public class MyCustomStats {

    @ReadOperation
    public String hello(){
        return "Hello World! ";
    }

     @ReadOperation
    public String read(@Selector String name,@Selector String message){
        return "Hello "+name+" ,message: "+message;
    }

    @WriteOperation
    public String write(){
        return "written";
    }

    @DeleteOperation
    public String delete(){
        return "Deleted";
    }

}
