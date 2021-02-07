package com.ttpai.framework.rose.boot.example.controllers;

import net.paoding.rose.web.annotation.Path;
import net.paoding.rose.web.annotation.rest.Get;

@Path("/rose")
public class RoseController {

    @Get("/hello")
    public String hello() {
        return "@hello rose";
    }

}
