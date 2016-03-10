/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author florianwenk
 */
public class PropertiesLoader implements AssetLoader{
    @Override
    public Object load(AssetInfo assetInfo) {
        Properties properties = new Properties();
        InputStream inputStream = assetInfo.openStream();
        try {
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(PropertiesLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return properties;
    }
}
