package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Long lastId = 1L;
    private Map<Long, TimeEntry> db = new HashMap<>();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        Long nextId = lastId;
        lastId++;
        timeEntry.setId(nextId);
        db.put(timeEntry.getId(), timeEntry);
        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return db.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(db.values());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry timeEntry1 = db.get(id);
        if(timeEntry1 == null) {
            return null;
        }
        timeEntry1.setProjectId(timeEntry.getProjectId());
        timeEntry1.setUserId(timeEntry.getUserId());
        timeEntry1.setDate(timeEntry.getDate());
        timeEntry1.setHours(timeEntry.getHours());
        db.replace(id, timeEntry1);
        return timeEntry1;
    }

    @Override
    public void delete(long id) {
        db.remove(id);
    }

}
