package seung.bookhub;

class NetworkConfiguration {
    private static boolean useLocalHost = true;

    static String getURL() {
        return "http://10.0.2.2:8080/";
    }
}