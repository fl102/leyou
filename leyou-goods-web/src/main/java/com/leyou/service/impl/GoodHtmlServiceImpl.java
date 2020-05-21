package com.leyou.service.impl;

import com.leyou.common.ThreadUtils;
import com.leyou.service.GoodHtmlService;
import com.leyou.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class GoodHtmlServiceImpl implements GoodHtmlService {
    @Autowired
    private GoodService goodService;
    @Autowired
    private TemplateEngine templateEngine;
    @Override
    public void createHtml(Long spuId) {
        //初始化thymeleaf上下文
        Context context = new Context();
        //数据放入上下文对象
        context.setVariables(this.goodService.loadData(spuId));
        PrintWriter printWriter = null;
        //创建输出流
        try {
            File file = new File("D:\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null){
                printWriter.close();
            }
        }

    }

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }

    public void deleteById(Long id) {
        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File("D:\\nginx-1.14.0\\html\\item\\" + id + ".html");
                file.deleteOnExit();
            }
        });
    }
}
