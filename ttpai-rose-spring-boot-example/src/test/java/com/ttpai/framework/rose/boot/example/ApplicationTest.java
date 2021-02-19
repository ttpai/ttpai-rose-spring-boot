package com.ttpai.framework.rose.boot.example;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ApplicationTest {


    @Test
    @Ignore
    public void resources() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;

//        resources = resolver.getResources("classpath*:applicationContext*.xml");
//        System.out.println(Arrays.stream(resources).map(Objects::toString).collect(Collectors.joining("\n")));

        resources = resolver.getResources("classpath*:applicationContext-rose.xml");
        System.out.println(Arrays.stream(resources).map(Objects::toString).collect(Collectors.joining("\n")));
    }

}