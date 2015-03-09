/** An analysis of tweets from the Oscars 2015.
 *  @author Dipsikha Halder, UC Berkeley
 */

import edu.princeton.cs.introcs.In;
import java.io.File;
import java.util.*; 

/* Tweet format reference:
* Time 0, ID 1, Text 2, Retweets 3, GeoTag 4, PlaceTag 5, Favorites 6, 
* User Name 7, User Location 8, User ID 9, Time Zone 10, User Followers 11, 
* User Statuses 12, User Friends 13, User Handle 14, HashTags in Tweet 15, UserMentions in Tweet 16
*/

public class OscarSocialData {
    /* Best Picture Reference List
    “American Sniper” Clint Eastwood, Robert Lorenz, Andrew Lazar, Bradley Cooper and Peter Morgan, Producers
    “Birdman or (The Unexpected Virtue of Ignorance)” Alejandro G. Iñárritu, John Lesher and James W. Skotchdopole, Producers
    “Boyhood” Richard Linklater and Cathleen Sutherland, Producers
    “The Grand Budapest Hotel” Wes Anderson, Scott Rudin, Steven Rales and Jeremy Dawson, Producers
    “The Imitation Game” Nora Grossman, Ido Ostrowsky and Teddy Schwarzman, Producers
    “Selma” Christian Colson, Oprah Winfrey, Dede Gardner and Jeremy Kleiner, Producers
    “The Theory of Everything” Tim Bevan, Eric Fellner, Lisa Bruce and Anthony McCarten, Producers
    “Whiplash” Jason Blum, Helen Estabrook and David Lancaster, Producers
    */

    /* HashMap key: Movie from Best Picture List value: Number of times found in text of tweets */
    private HashMap<String, Integer> bestPictureListtomentions = new HashMap<String, Integer>();

    /* HashMap key: time hh:mm value: Number of times "Birdman" found a that time as a hashtag 
    or in the text of a tweet */
    private TreeMap<String, Integer> timetoBirdmanmentions = new TreeMap<String, Integer>();

    /* HashMap key: Location value: Number of times #Oscars found at that location */
    private TreeMap<String, Integer> locationtoOscar2k15 = new TreeMap<String, Integer>();

    /* List containing 50 states for comparison */
    private List<String> states = new ArrayList<>(Arrays.asList("Alabama","Alaska","Arizona","Arkansas","California","Colorado",
        "Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana",
        "Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan",
        "Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire",
        "New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio",
        "Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota",
        "Tennessee","Texas","Utah","Vermont","Virginia","Washington",
        "West Virginia","Wisconsin","Wyoming"));


    public OscarSocialData(String oscarCsv) {
        /* Add data to bestPicturetomentions Map */
        bestPictureListtomentions.put("American Sniper", 0);
        bestPictureListtomentions.put("Birdman", 0);
        bestPictureListtomentions.put("Boyhood", 0);
        bestPictureListtomentions.put("The Grand Budapest Hotel", 0);
        bestPictureListtomentions.put("The Imitation Game", 0);
        bestPictureListtomentions.put("Selma", 0);
        bestPictureListtomentions.put("The Theory of Everything", 0);
        bestPictureListtomentions.put("Whiplash", 0);

        
        File oscarFile = new File(oscarCsv);
        In inOscar = new In(oscarFile);

        while (inOscar.hasNextLine()) {
            // Parse data
            String[] tokens = inOscar.readLine().split(",");
            if (tokens.length > 16) { 
                String id = tokens[1];
                String time = tokens[0];
                if (time.split(" ").length == 6) {
                    time = time.split(" ")[3].substring(0,5);
                }   
                String location = tokens[8];
                String text = tokens[2];
                String hashTags = tokens[15];
                String mentions = tokens[16];
            
                /* organize states and tweets containing the hashtag #Oscars2015 */
                TreeSet hashtagset = new TreeSet();
                String[] splittag = hashTags.split(",");
                for (int i = 0; i < splittag.length; i++) {
                    hashtagset.add(splittag[i]);
                }
                if (hashtagset.contains("Oscars2015") && (location.length()!=0)) {
                    if (locationtoOscar2k15.containsKey(location)) {
                        Integer value = locationtoOscar2k15.get(location);
                        locationtoOscar2k15.put(location, value+1);
                    } else {
                        locationtoOscar2k15.put(location, 1);
                    }  
                }

                /* organize times based on tweets containing "Birdman" as text or a hashtag */
                if (hashtagset.contains("Birdman") || text.contains("Birdman")) {
                    if (timetoBirdmanmentions.containsKey(time)) {
                        Integer value = timetoBirdmanmentions.get(time);
                        timetoBirdmanmentions.put(time, value+1);
                    } else {
                        timetoBirdmanmentions.put(time, 1);
                    }
                }

                /* organize Best Picture nominees by most appearances in text of tweet */
                for (String movie: bestPictureListtomentions.keySet()) {
                    if (text.contains(movie)) {
                        int value = bestPictureListtomentions.get(movie);
                        bestPictureListtomentions.put(movie, value+1);
                    }
                }
            } 
        } 
    } 

    // 
    public void activeState() {
        //Sort mao by values containing number of mentions and print the keys
        Map<String, Integer> x = sortByComparator(locationtoOscar2k15);
        int z = 1;
        for (String g: x.keySet()) {
            if (g.length()!=0 && states.contains(g)) {
                System.out.println(z+". "+g+": "+x.get(g));
                z++;
            }
        }
    }

    public void winnerPrediction() {
        //sort the map by values and print the first value which contains the highest value
        Map<String, Integer> b = sortByComparator(timetoBirdmanmentions);
        System.out.println(b.keySet().iterator().next());
    }

    
    public void popularity() {
        //sort the map by values and print the keys
        Map<String, Integer> x = sortByComparator(bestPictureListtomentions);
        int z = 1;
        for (String y: x.keySet()) {
            System.out.println(z+". "+y+" Mentions: "+x.get(y));
            z++;
        }
    }


    /* code obtained from http://www.mkyong.com/java/how-to-sort-a-map-in-java/
    * Sorts a map based on values in decreasing order */
    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {
 
        // Convert Map to List
        List<Map.Entry<String, Integer>> list = 
            new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
 
        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
 
        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static void main(String[] args) {
        OscarSocialData x = new OscarSocialData("oscar_tweets.csv");

        System.out.println("1. Popularity Rank:");
        System.out.println("A list of the most tweeted about best picture nominees:");
        x.popularity();
        System.out.println();
       
        System.out.println("2. Winner Announcement Prediction in UTC:");
        System.out.println("(based on Hour and minute when Birdman was mentioned on Twitter most frequently)");
        x.winnerPrediction();
        System.out.println();
        
        System.out.println("3. Location");
        System.out.println("A list of which states were the most active in tweeting about #Oscars2015, ordered from most active to least");
        System.out.println("(State: #Oscars2015 count)");
        x.activeState(); 
    }
} 







