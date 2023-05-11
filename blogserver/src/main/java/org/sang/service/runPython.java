package org.sang.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
//pythonPath 参数用于指定 Python 解释器的路径，scriptPath 参数用于指定 Python 脚本的路径，input 参数用于指定需要传递给 Python 进程的输入数据。
public class runPython {
    private String runPythonProcess(String pythonPath, String scriptPath, String input) {
        try {
            ProcessBuilder builder = new ProcessBuilder(pythonPath, scriptPath);
            builder.redirectInput(ProcessBuilder.Redirect.PIPE);
            builder.redirectOutput(ProcessBuilder.Redirect.PIPE);

            Process process = builder.start();

            // 在子线程中异步处理标准输出流
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "";
            });

            // 向子进程输入数据
            OutputStream stdin = process.getOutputStream();
            stdin.write(input.getBytes());
            stdin.flush();
            stdin.close();

            // 等待子进程结束
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                String output = future.get(5, TimeUnit.SECONDS);
                return output;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//    inputData 参数用于指定从前端页面接收到的输入数据。
//    通过分别调用 runPythonProcess() 方法启动 Python 2.7 和 Python 3.8 进程，并将 inputData 作为输入数据传递给它们。
    @GetMapping("/runPython")
    public String runPython(@RequestParam String inputData) {
        String output27 = runPythonProcess("python2.7", "your_python27_file.py", inputData);
        String output38 = runPythonProcess("python3.8", "your_python38_file.py", inputData);
        // TODO: 处理 Python 运行结果
        return "Python 运行完毕";
    }


}
