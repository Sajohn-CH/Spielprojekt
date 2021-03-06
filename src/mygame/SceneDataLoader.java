package mygame;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.math.Vector3f;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author florianwenk
 */
public class SceneDataLoader implements AssetLoader{
    
    @Override
    public Object load(AssetInfo assetInfo) {
        Document doc = createDocFromStream(assetInfo.openStream());
        HashMap<String, Object> data = new HashMap<>();
//      multipliers laden
        Element multiplier = (Element) doc.getElementsByTagName("Multipliers").item(0);
        data.put("bombMultiplier", Float.valueOf(multiplier.getAttribute("Bomb")));
        data.put("towerMultiplier", Float.valueOf(multiplier.getAttribute("Tower")));
//      Corners laden
        ArrayList<Vector3f> corners = new ArrayList<>();
        NodeList list = doc.getElementsByTagName("corner");
        for(int i = 0; i < list.getLength(); i++) {
            Element element = getElement(i, list);

            Vector3f vector = new Vector3f();
            vector.setX(Float.valueOf(element.getAttribute("x")));
            vector.setY(Float.valueOf(element.getAttribute("y")));
            vector.setZ(Float.valueOf(element.getAttribute("z")));
            corners.add(vector);
        }
        data.put("corners", corners);
        //Weitere zu ladende Aspekte aus der sceneData-Datei
        return data;
    }

    private Document createDocFromStream(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            return doc;
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Gibt das Element in der XML-NodeList (nicht zu verwechseln mit dem Node der JMonkeyEngine) mit der übergebenen ID zurück. Die ID ist immer ein Attribut jedes Elements in der NodeList.
     * @param id  ID des gewünschten Elements
     * @param list  NodeList mit allen Elementen
     * @return gewünschtes Element
     */
    private Element getElement(int id, NodeList list) {
        for(int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            if(element.getAttribute("id").equals(String.valueOf(id))) {
                return element;
            }
        }
        return null;
    }
}
