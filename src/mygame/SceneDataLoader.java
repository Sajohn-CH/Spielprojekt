package mygame;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author florianwenk
 */
public class SceneDataLoader implements AssetLoader{
    
    @Override
    public Object load(AssetInfo assetInfo) {
        Document doc = createDocFromStream(assetInfo.openStream());
        HashMap<String, Object> data = new HashMap<>();
        Element multiplier = (Element) doc.getElementsByTagName("Multipliers").item(0);
        data.put("bombMultiplier", Float.valueOf(multiplier.getAttribute("Bomb")));
        data.put("towerMultiplier", Float.valueOf(multiplier.getAttribute("Tower")));
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
}
