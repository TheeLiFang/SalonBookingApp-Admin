package my.salonapp.salonbookingapp;

import android.util.Log;

import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * XML Parser Class (For CustomizedListView). References:
 * http://www.androidhive.info/2011/11/android-xml-parsing-tutorial/
 *
 * @author Ravi Tamada
 * @since Nov 12, 2011
 */
public class XMLParser {

    // XML nodes for insert status
    private static final String XML_NODE_INSERT = "Insert";
    private static final String XML_NODE_STATUS = "Status";

    public XMLParser() {
    }

    /**
     * Getting XML content by making HTTP Request.
     *
     * @param url          HTTP URL
     * @param isGet        Request method GET or POST
     * @param entity       Multipart/form coded HTTP entity
     * @param stringEntity String entity
     * @return XML document
     */
    public String getXmlFromUrl(String url, boolean isGet, MultipartEntity entity, StringEntity stringEntity) {
        String xml = "";

        try {
            // Instantiate the URL object with the target URL of the resource to request
            URL url1 = new URL(url);

            // Instantiate the HttpURLConnection with the URL object - A new
            // connection is opened every time by calling the openConnection
            // method of the protocol handler for this URL.
            // 1. This is the point where the connection is opened.
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setReadTimeout(Utils.timeout_submission);
            connection.setConnectTimeout(Utils.timeout_submission);
            connection.setRequestProperty("Connection", "Keep-Alive");

            if (!isGet) {
                // Instead of a GET, we're going to send using method = "POST"
                connection.setRequestMethod("POST");
                // Set connection input to true
                connection.setDoInput(true);

                if (entity != null) {
                    connection.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
                    OutputStream os = connection.getOutputStream();
                    entity.writeTo(connection.getOutputStream());
                    os.close();
                } else if (stringEntity != null) {
                    connection.addRequestProperty(stringEntity.getContentType().getName(), stringEntity.getContentType().getValue());
                    OutputStream os = connection.getOutputStream();
                    stringEntity.writeTo(connection.getOutputStream());
                    os.close();
                }
            } else {
                connection.setRequestMethod("GET");
            }

            connection.connect();

            // If there is a response code AND that response code is 200 OK, do stuff in the first if block
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder xmlResponse = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()), 8192);
                String strLine = null;

                while ((strLine = input.readLine()) != null) {
                    xmlResponse.append(strLine);
                }

                xml = xmlResponse.toString();
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }

        return xml;
    }

    /**
     * Submit HTTP Request with URL parameters.
     *
     * @param url HTTP URL
     * @return XML document
     */
    public Boolean httpParamSubmission(String url) {
        Boolean status = Boolean.FALSE;

        try {
            URL url1 = new URL(url);
            String xml = "";

            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(Utils.timeout_submission);
            connection.setConnectTimeout(Utils.timeout_submission);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder xmlResponse = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()), 8192);
                String strLine;

                while ((strLine = input.readLine()) != null) {
                    xmlResponse.append(strLine);
                }

                xml = xmlResponse.toString();
                input.close();
            }

            if (xml.length() > 0) {
                XMLParser parser = new XMLParser();
                Document doc = parser.getDomElement(xml);
                NodeList nl = doc.getElementsByTagName(XML_NODE_INSERT);
                Element e = (Element) nl.item(0);

                status = Boolean.valueOf(parser.getValue(e, XML_NODE_STATUS));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }

        return status;
    }

    /**
     * Getting XML content by making local request.
     *
     * @param xml XML element
     * @return XML document
     */
    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            doc = db.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
            return null;
        }

        // Return DOM.
        return doc;
    }

    /**
     * Get each XML child element value by passing element node name.
     *
     * @param item XML node element
     * @param str  XML node key
     * @return XML child element value
     */
    public String getValue(Element item, String str) {
        NodeList nl = item.getElementsByTagName(str);

        return this.getElementValue(nl.item(0));
    }

    /**
     * Get each XML child element value by passing element node name.
     *
     * @param elem element node name
     * @return XML child element
     */
    private String getElementValue(Node elem) {
        Node child;

        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }

        return "";
    }
}