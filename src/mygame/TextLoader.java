/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author florianwenk
 */
public class TextLoader implements AssetLoader{
    @Override
    public Object load(AssetInfo assetInfo) {
        InputStream inputStream = assetInfo.openStream();
        ArrayList<String> text = getStringFromInputStream(inputStream);        
        return text;
    }
    
    private static ArrayList<String> getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        ArrayList<StringBuilder> sb = new ArrayList<>();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                    sb.add(new StringBuilder());
                    sb.get(sb.size()-1).append(line);
            }
        } catch (IOException e) {
                e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ArrayList<String> text = new ArrayList<>();
        for(int i = 0; i < sb.size(); i++){
            text.add(sb.get(i).toString());
        }
        return text;
    }

}
