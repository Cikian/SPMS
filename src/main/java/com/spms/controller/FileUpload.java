package com.spms.controller;


import org.springframework.boot.system.ApplicationHome;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/upload")
public class FileUpload {
    @PostMapping
    public String singleUploadFile(MultipartFile file, Model model){
        String fileName=file.getOriginalFilename(); //获取文件名以及后缀名
        //fileName= UUID.randomUUID()+"_"+fileName;//重新生成文件名（根据具体情况生成对应文件名）

        //获取jar包所在目录
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        //在jar包所在目录下生成一个upload文件夹用来存储上传的图片
        String dirPath = jarF.getParentFile().toString()+"/upload/";
        System.out.println(dirPath);

        File filePath=new File(dirPath);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        try{
            //将文件写入磁盘
            file.transferTo(new File(dirPath+fileName));
            //上传成功返回状态信息
            model.addAttribute("uploadStatus","上传成功");
        }catch (Exception e){
            e.printStackTrace();
            //上传失败，返回失败信息
            model.addAttribute("uploadStatus","上传失败:"+e.getMessage());
        }
        //携带上传状态信息回调到文件上传页面
        return "singleUpload";
    }
}
