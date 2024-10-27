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
}