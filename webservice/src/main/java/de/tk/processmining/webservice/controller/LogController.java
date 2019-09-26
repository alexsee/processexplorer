package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.storage.StorageService;
import de.tk.processmining.webservice.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController()
public class LogController {

    private final StorageService storageService;

    private final LogService logService;

    @Autowired
    public LogController(StorageService storageService, LogService logService) {
        this.storageService = storageService;
        this.logService = logService;
    }

    @RequestMapping("/getalllogs")
    public List<Log> getAll() {
        return logService.getAllLogs();
    }

    @RequestMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("logName") String logName, RedirectAttributes redirectAttributes) {
        // store and import log
        storageService.store(file);
        logService.importLog(storageService.load(file.getOriginalFilename()).toFile().getAbsolutePath(), logName);

        // generate result
        redirectAttributes.addAttribute("logName", logName);
        return "redirect:/statistics";
    }

    @RequestMapping("/mine/dfg")
    public String mingDFG(@RequestParam("logName") String logName) {
        // generate directly follows graph
        logService.mineDFG(logName);
        return "";
    }
}