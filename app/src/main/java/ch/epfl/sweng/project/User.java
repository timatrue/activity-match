package ch.epfl.sweng.project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olga on 04.11.16.
 */

public class User {
    private String id;
    private String username;
    private String email;
    private List<String> organizedEvents;
    private List<String>  participatedEvents;
    private List<String>  interestedEvents;
    private String rating; //Double?
    private String photoLink;

    //private Boolean isActive;

    //private DataProvider mDB;

    /**
     * A class about user profile information that provides it to other classes.
     *
     * @param   username    the name of the user
     * @param   email   the e-mail of the user
     * @param   organizedEvents    a list of events organized by this user
     * @param   participatedEvents    a list of events this user participated in
     * @param   interestedEvents    a list of events this user is interested in
     * @param   rating    the rating of the user
     * @param   photoLink    the link to get the photo of the user
     */
    public User(String id, String username, String email, List<String>  organizedEvents, List<String>  participatedEvents, List<String>  interestedEvents,
                String rating, String photoLink) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.organizedEvents = organizedEvents;
        this.participatedEvents = participatedEvents;
        this.interestedEvents = interestedEvents;
        this.rating = rating;
        this.photoLink = photoLink;
        //this.isActive = new Boolean(isActive);
    }

    public String getId() {
        return this.id;
    }

    /**
     * @return username of the user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @return email of the user
     */
    public String getEmail() {
        return this.email;
    }

    public List<String> getOrganizedEvents() {
        List<String> events = null;
        for (String event : organizedEvents) {
            events.add(new String(event));
        }
        return events;
    }

    public List<String> getParticipatedEvents() {
        List<String> events = null;
        for (String event : participatedEvents) {
            events.add(new String(event));
        }
        return events;
    }

    public List<String> getInterestedEvents() {
        List<String> events = new ArrayList<>(interestedEvents);
        return events;
    }
    /**
     * @return rating of the user
     */
    public String getRating() {
        return new String(rating);
    }

    public String getPhotoLink() {
        return new String(photoLink);
    }

    public User copy() {
        return new User(this.id, this.username, this.email, this.organizedEvents, this.participatedEvents, this.interestedEvents, this.rating, this.photoLink);
    }
}
