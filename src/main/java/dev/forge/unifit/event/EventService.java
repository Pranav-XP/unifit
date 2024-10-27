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

    // Directory where images will be stored
    private final String uploadDir = "C:/uploads/";

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

/*    // Method to handle image storage
    public String saveImage(MultipartFile file) {
        try {
            // Create a unique name for the image file
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // Ensure the upload directory exists
            Files.createDirectories(filePath.getParent());

            // Save the image to the file system
            Files.write(filePath, file.getBytes());

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public void updateEvent(Long eventId, Event updatedEvent/*, MultipartFile file*/) {
        Event existingEvent = eventRepository.findById(eventId).orElse(null);

        if (existingEvent != null) {
            // Update event details
            existingEvent.setTitle(updatedEvent.getTitle());
            existingEvent.setDescription(updatedEvent.getDescription());
            existingEvent.setEventDateTime(updatedEvent.getEventDateTime()); // Ensure this is set
            existingEvent.setFacility(updatedEvent.getFacility());

/*            // If a new image is provided, save it and update the image field
            if (file != null && !file.isEmpty()) {
                String imageName = saveImage(file);
                existingEvent.setImageUrl(imageName);
            }*/

            eventRepository.save(existingEvent);
        }
    }
}
