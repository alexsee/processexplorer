package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.XLog2Database;
import de.tk.processmining.data.XLogUtils;
import de.tk.processmining.data.analysis.DirectlyFollowsGraphMiner;
import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController()
public class LogController {

    private final StorageService storageService;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LogController(StorageService storageService, JdbcTemplate jdbcTemplate) {
        this.storageService = storageService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("logName") String logName, RedirectAttributes redirectAttributes) {
        storageService.store(file);

        // read log
        var log = XLogUtils.readLog(storageService.load(file.getOriginalFilename()).toFile().getAbsolutePath());

        // import log to database
        var log2db = new XLog2Database(jdbcTemplate, logName);
        log2db.importLog(log);

        // generate result
        redirectAttributes.addAttribute("logName", logName);
        return "redirect:/statistics";
    }

    @RequestMapping("/mine/dfg")
    public String mingDFG(@RequestParam("logName") String logName) {
        var dfgMiner = new DirectlyFollowsGraphMiner(jdbcTemplate);
        dfgMiner.mine(logName);

        return "";
    }

}