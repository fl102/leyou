package com.leyou.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.service.UpLoadImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadImageServiceImpl implements UpLoadImageService {
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif","image/png");
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadImageServiceImpl.class);

    @Autowired
    private FastFileStorageClient storageClient;

    @Override
    public String uploadImage(MultipartFile file) {
        //校验文件类型
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)){
            LOGGER.info("文件类不合法 {}" , originalFilename);
            return null;
        }
        try {
            //校验文件内容
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null){
                LOGGER.info("文件内容不合法 {}" , originalFilename);
                return null;
            }
            //保存到服务器
            //file.transferTo(new File("E:\\upload\\image\\" + originalFilename));
            //生成url地址
            // 上传并保存图片，参数：1-上传的文件流 2-文件的大小 3-文件的后缀 4-可以不管他
            String ext = org.apache.commons.lang.StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.storageClient.uploadFile(
                    file.getInputStream(), file.getSize(), ext, null);
            //return "http://image.leyou.com/" + originalFilename;
            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (Exception e){
            LOGGER.info("服务器内部错误{}" , originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
