package ch.epfl.sweng.project;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DeboxActivity {

    private String id;
    private String organizer;
    private String title;
    private String description;
    private Calendar timeStart;
    private Calendar timeEnd;
    private double longitude;
    private double latitude;
    private String category;
    private List<String> imagesList;
    private int nbMaxOfParticipants;
    private int nbOfParticipants;

    /**
     * Allocates a <code>DeboxActivity</code> object and initializes it so that
     * it represents a Debox activity, containing all informations about it.
     *
     * @param   organizer    The name of the organizer of the activity
     * @param   title   the title of the activity
     * @param   description    a description of the activity
     * @param   timeStart    the <code>Calendar</code> representing the scheduled start of the Activity
     * @param   timeEnd    the <code>Calendar</code> representing the scheduled end of the Activity
     * @param   longitude    the longitude of the activity's meeting point location
     * @param   latitude    the latitude of the activity's meeting point location
     * @param   category    the category of the activity
     */
    public DeboxActivity(String id, String organizer, String title, String description, Calendar timeStart,
                         Calendar timeEnd, double latitude, double longitude, String category) {
        this.id = id;
        this.organizer = organizer;
        this.title = title;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.imagesList = new ArrayList<>();
        this.nbOfParticipants = 0;
        this.nbMaxOfParticipants = 0;

    }

    /**
     * Allocates a <code>DeboxActivity</code> object and initializes it so that
     * it represents a Debox activity, containing all informations about it.
     *
     * @param   organizer    The name of the organizer of the activity
     * @param   title   the title of the activity
     * @param   description    a description of the activity
     * @param   timeStart    the <code>Calendar</code> representing the scheduled start of the Activity
     * @param   timeEnd    the <code>Calendar</code> representing the scheduled end of the Activity
     * @param   longitude    the longitude of the activity's meeting point location
     * @param   latitude    the latitude of the activity's meeting point location
     * @param   category    the category of the activity
     */
    public DeboxActivity(String id, String organizer, String title, String description, Calendar timeStart,
                         Calendar timeEnd, double latitude, double longitude, String category, int nbOfParticipants, int nbMaxParticipants ) {
        this.id = id;
        this.organizer = organizer;
        this.title = title;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.imagesList = new ArrayList<>();
        this.nbOfParticipants = nbOfParticipants;
        this.nbMaxOfParticipants = nbMaxParticipants;

    }
    /**
     * @param   imagesList    the list of images name on Firebase storage
     */
    public DeboxActivity(String id, String organizer, String title, String description, Calendar timeStart,
                              Calendar timeEnd, double latitude, double longitude, String category,
                              List<String> imagesList) {
        this(id, organizer, title, description, timeStart, timeEnd, latitude, longitude, category);
        this.imagesList = imagesList;
    }

    /**
     * @param   imagesList    the list of images name on Firebase storage
     */
    public DeboxActivity(String id, String organizer, String title, String description, Calendar timeStart,
                         Calendar timeEnd, double latitude, double longitude, String category,
                         List<String> imagesList,int nbOfParticipants, int nbMaxParticipants ) {
        this(id, organizer, title, description, timeStart, timeEnd, latitude, longitude, category,
                nbOfParticipants, nbMaxParticipants);
        this.imagesList = imagesList;
    }

    /**,
     * @return  the organizer of the Debox Activity
     */
    public String getId() { return id; }

    /**
     * @return  the organizer of the Debox Activity
     */
    String getOrganizer() {
        return organizer;
    }

    /**
     * @return  the title of the Debox Activity
     */
    public String getTitle() {
        return title;
    }


    /**
     * @return the description of the Debox Activity
     */
    String getDescription(){
        return description;
    }

    /**
     * @return a short description of the Debox Activity. The short description corresponds to the
     * description, with a maximum length of 64 characters.
     */
    public String getShortDescription() {
        int DEBOX_ACTIVITY_SHORT_DESCRIPTION_MAX_LENGTH = 64;
        if (description.length() > DEBOX_ACTIVITY_SHORT_DESCRIPTION_MAX_LENGTH) {
            return description.substring(0, DEBOX_ACTIVITY_SHORT_DESCRIPTION_MAX_LENGTH - 3) + "...";
        }
        else {
            return description;
        }
    }

    /**
     * @param   maxLength    the maximum number of character of the short description to return
     * @return a short description of the Debox Activity. The short description corresponds to the
     * description, with a maximum length specified by the paramter maxLength
     */
    String getShortDescription(int maxLength) {
        if(maxLength <= 3) {
            return "...";
        }
        if (description.length() > maxLength) {
            return description.substring(0, maxLength - 3) + "...";
        }
        else {
            return description;
        }
    }


    /**
     * @return the start time of the Debox Activity
     */
    public Calendar getTimeStart() {
        return timeStart;
    }

    /**
     * @return the end time of the Debox Activity
     */
    public Calendar getTimeEnd() {
        return timeEnd;
    }

    /**
     * @return an array containing the latitude and longitude of the Debox Activity meeting point
     * location
     */
    public double[] getLocation() {
        return new double[]{latitude, longitude};
    }

    /**
     * @return the category of the Debox Activity
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return the NbOfParticipants of the Debox Activity
     */
    public int getNbOfParticipants() {
        return nbOfParticipants;
    }

    /**
     * @return the NbMaxOfParticipants of the Debox Activity
     */
    public int getNbMaxOfParticipants() {
        return nbMaxOfParticipants;
    }


    /**
     * @param imageName: the name of the image to insert
     */
    void addImage(String imageName) {
        imagesList.add(imageName);
    }

    /**
     * @return a deep copy of the images list
     */
    public List<String> getImageList() {
        if(imagesList == null) {
            return null;
        }

        List<String> list = new ArrayList<>();
        for(String image: imagesList) {
            list.add(image);
        }
        return list;
    }

}
