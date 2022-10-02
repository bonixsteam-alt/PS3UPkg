import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static final String ver = "1.0";
    static final String help = System.lineSeparator() + "PS3UPkg - " + ver + " by bonix"+ System.lineSeparator() + "    Tool for download updates for PS3 games. Made for RPCS3" + System.lineSeparator() + "Usage: ps3upkg GAME_ID [\"DOWNLOAD_DIR\"]" + System.lineSeparator();
    static final String psn = "https://a0.ww.np.dl.playstation.net/tpl/np/";
    static ArrayList<URL> dLinks = new ArrayList<>();
    static ArrayList<Long> bytes = new ArrayList<>();

    static final String os = System.getProperty("os.name").toLowerCase();
    public static final int sys = os.contains("win") ? 1 : os.contains("mac") ? 2 : os.contains("nix") || os.contains("nux") || os.contains("aix") ? 3 : 0;

    public static String gameId;


    public static void main(String[] args) {
        try {
            print(help);
            if(args.length > 2) { print("Too many arguments. Exiting..."); System.exit(0);}
            String downloadDir;
            try {
                downloadDir = args[1];
                downloadDir = downloadDir.replace("'", "");
                downloadDir = downloadDir.replace("\"", "");
                print(downloadDir);
            } catch (ArrayIndexOutOfBoundsException | NullPointerException ignored) { downloadDir = "";}


            tryParse(args[0]);
            gameId = args[0];
            Downloader down = new Downloader();
            URL[] u = new URL[dLinks.size()];
            Long[] l = new Long[bytes.size()];
            down.init(dLinks.toArray(u), new File(downloadDir), bytes.toArray(l));
        } catch (ArrayIndexOutOfBoundsException ignored) { print("No Game ID provided."); System.exit(0);}

    }

    static void tryParse(String id) {
        print("Attempting to parse ID " + id + "...");

        // Network check
        try{
            URL url = new URL("https://www.google.com");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.connect();
        }catch (IOException ex) {
            print("No internet connection. Exiting...");
            System.exit(1);
        }

        // Manager for SSL bypass
        TrustManager[] dummyTrustManager = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        // Bypass SSL
        try{
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, dummyTrustManager, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        long bytesLong = 0;

        // Parse attempt
        try {
            URL url = new URL(psn + id + "/" + id + "-ver.xml");
            URLConnection con = url.openConnection();

            Scanner s = new Scanner(con.getInputStream()).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            if(result.equalsIgnoreCase("")) {
                print("Game has no updates available. Exiting...");
                System.exit(0);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder b = factory.newDocumentBuilder();

            ByteArrayInputStream input = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            Document doc = b.parse(input);
            doc.getDocumentElement().normalize();

            String name = doc.getElementsByTagName("TITLE").item(0).getTextContent();
            print("Found " + name + " with ID " + id + System.lineSeparator());

            NodeList nl = doc.getElementsByTagName("package");
            for(int i=0; i<nl.getLength(); i++) {
                Node n = nl.item(i);
                if(n.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) n;

                    dLinks.add(new URL(e.getAttribute("url")));
                    bytes.add(Long.parseLong(e.getAttribute("size")));
                    bytesLong += Long.parseLong(e.getAttribute("size"));
                    print("Found version " + e.getAttribute("version"));
                }
            }

            print(System.lineSeparator() + "Total size of updates: ~" + formatSize(bytesLong));


        } catch (IOException ex) {
            print("Couldn't find XML. Exiting...");
            print(ex);
            System.exit(1);
        } catch (ParserConfigurationException | SAXException e) {
            print("Error while getting info from XML.");
            throw new RuntimeException(e);
        }
    }

    static void print(Object s) { System.out.println(s); }

    // this was made without any help from stackoverflow, trust me
    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }
}
