package com.leyou.test;

import com.leyou.LeyouSearchApplication;
import com.leyou.client.SpecClient;
import com.leyou.domain.SpecParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouSearchApplication.class)
public class SpecTest {
    @Autowired
    private SpecClient specClient;

    @Test
    public void testSpec(){
        List<SpecParam> params = specClient.findParams(null, 76L, null, true);
        params.forEach(param -> {
            System.out.println(param.getName());
        });

    }
}
