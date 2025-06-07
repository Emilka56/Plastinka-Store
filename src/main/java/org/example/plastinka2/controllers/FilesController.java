package org.example.plastinka2.controllers;


import org.example.plastinka2.services.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;

@Controller
public class FilesController {
    @Autowired
    private FileInfoService fileInfoService;

    @GetMapping("/files/{file-name:.+}")
    public void getFile(@PathVariable("file-name") String fileName, HttpServletResponse response) {
        fileInfoService.writeImageToResponse(fileName, response);
    }
}
