package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {
    private final TimeEntryRepository timeEntryRepository;
    private MeterRegistry meterRegistry;

    public TimeEntryController(@Qualifier("JdbcTimeEntryRepository") TimeEntryRepository timeEntryRepository, MeterRegistry meterRegistry) {

        this.timeEntryRepository = timeEntryRepository;
        this.meterRegistry = meterRegistry;
    }

    private void incrementCount() {
        meterRegistry.counter("timeEntry.actionCounter").increment();
    }

    @PostMapping(value = "/time-entries")
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry timeEntry = timeEntryRepository.create(timeEntryToCreate);
        incrementCount();
        recordSummary(1.0);
        return ResponseEntity.status(201).body(timeEntry);
    }

    private void recordSummary(double value) {
        meterRegistry.summary("timeEntry.summary").record(value);
    }

    @GetMapping(value = "/time-entries/{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable Long timeEntryId) {
        TimeEntry timeEntry = timeEntryRepository.find(timeEntryId);
        if (timeEntry == null) {
            return ResponseEntity.notFound().build();
        }
        incrementCount();
        return ResponseEntity.status(200).body(timeEntry);
    }

    @GetMapping(value = "/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        incrementCount();
        List<TimeEntry> timeEntries = timeEntryRepository.list();
        return ResponseEntity.status(200).body(timeEntries);
    }

    @PutMapping(value = "/time-entries/{timeEntryId}")
    public ResponseEntity update(@PathVariable Long timeEntryId, @RequestBody TimeEntry timeEntry) {
        TimeEntry update = timeEntryRepository.update(timeEntryId, timeEntry);
        if (update == null) {
            return ResponseEntity.notFound().build();
        }
        incrementCount();
        return ResponseEntity.status(200).body(update);
    }

    @DeleteMapping(value = "/time-entries/{timeEntryId}")
    public ResponseEntity delete(@PathVariable Long timeEntryId) {
        incrementCount();
        recordSummary(0);
        timeEntryRepository.delete(timeEntryId);
        return ResponseEntity.noContent().build();
    }
}
