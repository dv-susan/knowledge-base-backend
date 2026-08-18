package com.knowledge.base.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 文件服务启动类
 *
 * @author 苏三
 * @since 2026-04-24
 */
@SpringBootApplication(scanBasePackages = "com.knowledge.base")
@MapperScan("com.knowledge.base.file.mapper")
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
        System.out.println("""

            ========================================
            /\\\\
              \\\\  /  \\\\
               \\\\/
              /  \\\\
             /    \\\\
            /      \\\\
           /        \\\\
          /          \\\\
         /            \\\\
        /              \\\\
       /                \\\\
      /                  \\\\
     /                    \\\\
    /                      \\\\
   /                        \\\\
  /                          \\\\
 /                            \\\\
=======================================
  文件服务启动成功！
  端口：8084
  API文档：http://localhost:8084/api/file/doc.html
=======================================
""");
    }
}
