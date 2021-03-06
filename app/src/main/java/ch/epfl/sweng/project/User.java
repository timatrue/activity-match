package ch.epfl.sweng.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class User {
    private String id;
    private String username;
    private String email;
    private List<String> organizedEventIds;
    private List<String> interestedEventIds;
    private List<String> rankedEventIds;
    private Integer ratingNb;
    private Integer ratingSum;
    private String photoLink;
    private List<Map<String, String>> commentField;


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
                List<String> rankedEventIds, Integer ratingNb, Integer ratingSum, String photoLink, List<Map<String, String>> commentField) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.organizedEventIds = organizedEvents;
        this.interestedEventIds = interestedEvents;
        this.rankedEventIds = rankedEventIds;
        this.ratingNb = ratingNb;
        this.ratingSum = ratingSum;
//        this.rating = rating;
        this.photoLink = photoLink;
        this.commentField = commentField;
    }

    public String getId() {
        return this.id;
    }

    /**
     * @return username of the user
     */
    String getUsername() {
        return this.username;
    }

    /**
     * @return email of the user
     */
    public String getEmail() {
        return this.email;
    }

    List<String> getOrganizedEventIds() {
        return new ArrayList<>(organizedEventIds);
    }

    List<String> getInterestedEventIds() {
        return new ArrayList<>(interestedEventIds);
    }

    List<String> getRankedEventIds() {
        return new ArrayList<>(rankedEventIds);
    }
    /**
     * @return rating of the user
     */
    double getRating() {
        if (this.ratingNb > 0) {
            return (((double)ratingSum)/ratingNb);
        }
        return -1;
    }

    int getRatingSum(){
        return ratingSum;
    }

    int getRatingNb(){
        return ratingNb;
    }

    public String getPhotoLink() {
        if(photoLink == null) {
            return null;
        }
        return photoLink;
    }

    public void setPhotoLink(String newPhotoLink) {
        photoLink = newPhotoLink;
    }

    List<Map<String, String>> getCommentField() {
        return this.commentField;
    }
    public User copy() {
        return new User(this.id, this.username, this.email, this.organizedEventIds, this.interestedEventIds, this.rankedEventIds,
                this.ratingNb, this.ratingSum, this.photoLink, this.commentField);
    }
}
