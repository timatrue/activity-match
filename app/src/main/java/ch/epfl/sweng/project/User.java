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
    private Integer ratingNb;
    private Integer ratingSum;
    private String photoLink;


    /**
     * A class about user profile information that provides it to other classes.
     * @param   id    the id of the user
     * @param   username    the name of the user
     * @param   email   the e-mail of the user
     * @param   organizedEvents    a list of events organized by this user
     * @param   interestedEvents    a list of events this user is interested in
     * @param   ratingNb    number of users that rated this user
     * @param   ratingSum   sum of rating points that was given to the user
     * @param   photoLink    the link to get the photo of the user
     */
    public User(String id, String username, String email, List<String>  organizedEvents, List<String>  interestedEvents,
                Integer ratingNb, Integer ratingSum, String photoLink) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.organizedEventIds = organizedEvents;
        this.interestedEventIds = interestedEvents;
        this.ratingNb = ratingNb;
        this.ratingSum = ratingSum;
//        this.rating = rating;
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
        return new ArrayList<>(organizedEventIds);
    }

    public List<String> getInterestedEventIds() {
        return new ArrayList<>(interestedEventIds);
    }
    /**
     * @return rating of the user
     */
    public String getRating() {
        if (this.ratingNb != 0) {
            return Integer.toString(Math.round(ratingSum/ratingNb));
        }
        return Integer.toString(this.ratingNb);
    }

    public String getPhotoLink() {
        return new String(photoLink);
    }

    public User copy() {
        return new User(this.id, this.username, this.email, this.organizedEventIds, this.interestedEventIds, this.ratingNb, this.ratingSum, this.photoLink);
    }
}
