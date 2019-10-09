package de.tk.processmining.webservice.services;

import de.tk.processmining.webservice.database.EventLogAnnotationRepository;
import de.tk.processmining.webservice.database.entities.EventLogAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogAnnotationService {

    private EventLogAnnotationRepository eventLogAnnotationRepository;

    @Autowired
    public LogAnnotationService(EventLogAnnotationRepository eventLogAnnotationRepository) {
        this.eventLogAnnotationRepository = eventLogAnnotationRepository;
    }

    public Iterable<EventLogAnnotation> findByLogName(String logName) {
        return this.eventLogAnnotationRepository.findByLogName(logName);
    }

    public Iterable<EventLogAnnotation> saveAll(List<EventLogAnnotation> annotations) {
        this.eventLogAnnotationRepository.deleteAllByLogName(annotations.get(0).getLogName());
        return this.eventLogAnnotationRepository.saveAll(annotations);
    }

    public EventLogAnnotation save(EventLogAnnotation annotation) {
        return this.eventLogAnnotationRepository.save(annotation);
    }

    public void deleteById(Long id) {
        this.eventLogAnnotationRepository.deleteById(id);
    }

}
