package com.example.platform.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "<!doctype html>"
                + "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>线上教学平台后端</title>"
                + "<style>body{font-family:Microsoft YaHei,Arial,sans-serif;background:#f5f7fb;margin:0;padding:40px;color:#111827}"
                + ".box{max-width:760px;background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:28px}"
                + "a{color:#2563eb;text-decoration:none} li{line-height:2}</style></head>"
                + "<body><div class=\"box\"><h2>线上教学平台后端已启动</h2>"
                + "<p>8080 是后端接口服务端口，页面请访问学员端或管理端。</p>"
                + "<ul>"
                + "<li>接口探活：<a href=\"/api/news/page?page=1&limit=1\">/api/news/page?page=1&limit=1</a></li>"
                + "<li>学员端：<a href=\"http://localhost:5173\">http://localhost:5173</a></li>"
                + "<li>管理端：<a href=\"http://localhost:5174/admin/login\">http://localhost:5174/admin/login</a></li>"
                + "</ul></div></body></html>";
    }
}
