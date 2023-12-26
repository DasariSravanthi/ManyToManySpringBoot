/*
 * You can use the following import statements
 *
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.http.HttpStatus;
 * import org.springframework.stereotype.Service;
 * import org.springframework.web.server.ResponseStatusException;
 * 
 * import java.util.*;
 *
 */

// Write your code here

package com.example.eventmanagementsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.eventmanagementsystem.repository.EventRepository;
import com.example.eventmanagementsystem.repository.EventJpaRepository;
import com.example.eventmanagementsystem.repository.SponsorJpaRepository;
import com.example.eventmanagementsystem.model.Event;
import com.example.eventmanagementsystem.model.Sponsor;

import java.util.List;
import java.util.ArrayList;

@Service
public class EventJpaService implements EventRepository {

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @Autowired
    private SponsorJpaRepository sponsorJpaRepository;

    @Override
    public ArrayList<Event> getEvents() {
        List<Event> eventsList = eventJpaRepository.findAll();
        ArrayList<Event> events = new ArrayList<>(eventsList);
        return events;
    }

    @Override
    public Event getEventById(int eventId) {
        try {
            Event event = eventJpaRepository.findById(eventId).get();
            return event;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Event addEvent(Event event) {
        List<Integer> sponsorIds = new ArrayList<>();

        for (Sponsor sponsor : event.getSponsors()) {
            sponsorIds.add(sponsor.getSponsorId());
        }

        List<Sponsor> sponsors = sponsorJpaRepository.findAllById(sponsorIds);

        event.setSponsors(sponsors);

        for (Sponsor sponsor : sponsors) {
            event.getSponsors().add(sponsor);
        }

        eventJpaRepository.save(event);

        sponsorJpaRepository.saveAll(sponsors);

        return event;
    }

    @Override
    public Event updateEvent(int eventId, Event event) {
        try {
            Event existingEvent = eventJpaRepository.findById(eventId).get();

            if (event.getEventName() != null) {
                existingEvent.setEventName(event.getEventName());
            }
            if (event.getDate() != null) {
                existingEvent.setDate(event.getDate());
            }
            if (event.getSponsors() != null) {
                List<Sponsor> existingSponsors = existingEvent.getSponsors();

                for (Sponsor sponsor : existingSponsors) {
                    sponsor.getEvents().remove(existingEvent);
                }

                sponsorJpaRepository.saveAll(existingSponsors);

                List<Integer> sponsorIds = new ArrayList<>();

                for (Sponsor sponsor : event.getSponsors()) {
                    sponsorIds.add(sponsor.getSponsorId());
                }

                List<Sponsor> sponsors = sponsorJpaRepository.findAllById(sponsorIds);

                existingEvent.setSponsors(sponsors);

                for (Sponsor sponsor : sponsors) {
                    existingEvent.getSponsors().add(sponsor);
                }

                sponsorJpaRepository.saveAll(sponsors);
            }

            eventJpaRepository.save(existingEvent);
            return existingEvent;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteEvent(int eventId) {
        try {
            Event event = eventJpaRepository.findById(eventId).get();

            List<Sponsor> sponsors = event.getSponsors();

            for (Sponsor sponsor : sponsors) {
                sponsor.getEvents().remove(event);
            }

            sponsorJpaRepository.saveAll(sponsors);

            eventJpaRepository.deleteById(eventId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @Override
    public List<Sponsor> getSponsorsByEventId(int eventId) {
        try {
            Event event = eventJpaRepository.findById(eventId).get();
            List<Sponsor> sponsors = event.getSponsors();
            return sponsors;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}