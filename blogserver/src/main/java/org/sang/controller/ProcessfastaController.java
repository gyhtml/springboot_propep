package org.sang.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.io.InputStreamReader;


@RestController
@RequestMapping("/api")
public class ProcessfastaController {
//    这段代码实现了一个POST请求处理器，可以接收一个名为"fasta"的参数，并将其传递给Python脚本执行。在这里，我使用了Process类来执行Python脚本并获取其输出结果，并将处理结果返回给前端。
    @PostMapping("/submit_fasta")
    public String processFasta(@RequestBody Map<String, Object> params) {
        //获取前端传来的数据
        String fasta = (String) params.get("fasta");

        //指定python脚本及参数
        String python27Path = "/usr/bin/python3";
        String scriptPath = "/path/to/python/script.py";

        try {
            //执行python脚本
            String[] cmd = {python27Path, scriptPath, fasta};
            Process process = Runtime.getRuntime().exec(cmd);

            //获取Python脚本的输出结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            //返回处理结果
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }
}