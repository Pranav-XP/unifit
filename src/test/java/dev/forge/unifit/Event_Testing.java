package dev.forge.unifit;

import dev.forge.unifit.event.Event;
import dev.forge.unifit.event.EventRepository;
import dev.forge.unifit.event.EventService;
import dev.forge.unifit.facility.Facility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Event_Testing {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event event;

    @BeforeEach
    public void setUp() {
        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("Description of the test event");
        // Set other event properties if necessary
    }

    @Test
    public void testGetAllEvents() {
        // Arrange
        List<Event> events = new ArrayList<>();
        events.add(event);
        when(eventRepository.findAll()).thenReturn(events);

        // Act
        List<Event> result = eventService.getAllEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(event.getTitle(), result.get(0).getTitle());
    }

    //@Test
/*    public void testSaveEvent_withImage() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn(new byte[0]);
        when(mockFile.isEmpty()).thenReturn(false);

        // Act
        eventService.saveEvent(event, mockFile);

        // Assert
        verify(eventRepository).save(event);
        assertNotNull(event.getImageUrl()); // Ensure image URL is set
    }*/

/*    @Test
    public void testSaveEvent_withoutImage() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);

        // Act
        eventService.saveEvent(event, mockFile);

        // Assert
        verify(eventRepository).save(event);
        assertNull(event.getImageUrl()); // Ensure image URL is not set
    }*/

/*    @Test
    public void testGetEventById() {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // Act
        Event result = eventService.getEventById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(event.getTitle(), result.getTitle());
    }*/

/*    @Test
    public void testDeleteEvent() {
        // Act
        eventService.deleteEvent(1L);

        // Assert
        verify(eventRepository).deleteById(1L);
    }*/

/*    @Test
    public void testUpdateEvent() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("newImage.jpg");
        when(mockFile.getBytes()).thenReturn(new byte[0]);
        when(mockFile.isEmpty()).thenReturn(false);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated Title");
        updatedEvent.setDescription("Updated Description");
        updatedEvent.setFacility(new Facility());

        // Act
        eventService.updateEvent(1L, updatedEvent, mockFile);

        // Assert
        verify(eventRepository).save(any(Event.class));
        assertEquals(updatedEvent.getTitle(), event.getTitle());
    }*/
}
