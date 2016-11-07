package ch.epfl.sweng.project;

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
    private Boolean isActive;

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
     * @param   isActive    status of the user - either active (1) or not (0)
     */
    public User(String id, String username, String email, List<String>  organizedEvents, List<String>  participatedEvents, List<String>  interestedEvents,
                         String rating, String photoLink, Boolean isActive) {
        this.id = new String(id);
        this.username = new String(username);
        this.email = new String(email);
        this.organizedEvents = organizedEvents; //why not new String []? what does it change?
        this.participatedEvents = participatedEvents;
        this.interestedEvents = interestedEvents;
        this.rating = new String(rating);
        this.photoLink = new String(photoLink);
        this.isActive = new Boolean(isActive);
    }

    /**
     * @return username of the user
     */
    public String getUsername() {
        return new String(username);
    }

    /**
     * @return email of the user
     */
    public String getEmail() {
        return new String(email);
    }

    /**
     * @return rating of the user
     */
    public String getRating() {
        return new String(rating);
    }

}
