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
    private List<String> organizedEventIds;
    private List<String>  interestedEventIds;
    private String rating;
    private String photoLink;


    /**
     * A class about user profile information that provides it to other classes.
     *
     * @param   username    the name of the user
     * @param   email   the e-mail of the user
     * @param   organizedEvents    a list of events organized by this user
     * @param   interestedEvents    a list of events this user is interested in
     * @param   rating    the rating of the user
     * @param   photoLink    the link to get the photo of the user
     */
    public User(String id, String username, String email, List<String>  organizedEvents, List<String>  interestedEvents,
                String rating, String photoLink) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.organizedEventIds = organizedEvents;
        //this.participatedEvents = participatedEvents;
        this.interestedEventIds = interestedEvents;
        this.rating = rating;
        this.photoLink = photoLink;
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

    public List<String> getOrganizedEventIds() {
        List<String> events = new ArrayList<>(organizedEventIds);
        return events;
    }

    public List<String> getInterestedEventIds() {
        List<String> events = new ArrayList<>(interestedEventIds);
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
        return new User(this.id, this.username, this.email, this.organizedEventIds, this.interestedEventIds, this.rating, this.photoLink);
    }
}
