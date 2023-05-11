package org.sang.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class GetFastaController {
    @PostMapping("/submit_fatsa")

    public String processData(@RequestBody String inputData) throws IOException {
        // 将输入数据写入文件
        File dataFile = new File("data.fasta");
        FileUtils.writeStringToFile(dataFile, inputData, Charset.defaultCharset());

        // 在Python 2.7中运行脚本
        ProcessBuilder pb27 = new ProcessBuilder("python2.7", "get_ss8.py", "data.fasta");
        pb27.redirectErrorStream(true);
        Process p27 = pb27.start();

        // 等待Python 2.7进程完成
        try {
            p27.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 在Python 3.8中运行脚本
        ProcessBuilder pb38 = new ProcessBuilder("python3.8", "get_feature.py", "results/", "data.fasta");
        pb38.redirectErrorStream(true);
        Process p38 = pb38.start();

        // 等待Python 3.8进程完成
        try {
            p38.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // 从Python 3.8脚本读取输出
        BufferedReader reader38 = new BufferedReader(new InputStreamReader(p38.getInputStream()));
        String line38;
        StringBuilder output38 = new StringBuilder();
        while ((line38 = reader38.readLine()) != null) {
            output38.append(line38 + "\n");
        }

        // 将Python输出返回给前端
        return output38.toString();
    }
}

