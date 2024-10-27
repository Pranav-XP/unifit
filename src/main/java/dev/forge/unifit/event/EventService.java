package dev.forge.unifit.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public void saveEvent(Event event) {

        eventRepository.save(event);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }


    public void updateEvent(Long eventId, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(eventId).orElse(null);

        if (existingEvent != null) {
            // Update event details
            existingEvent.setTitle(updatedEvent.getTitle());
            existingEvent.setDescription(updatedEvent.getDescription());
            existingEvent.setEventDateTime(updatedEvent.getEventDateTime()); // Ensure this is set
            existingEvent.setFacility(updatedEvent.getFacility());


            eventRepository.save(existingEvent);
        }
    }
}
