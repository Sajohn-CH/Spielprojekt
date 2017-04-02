package mygame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author florianwenk
 */
public class Cryption {
//    private Cipher encryptionCipher;
//    private Cipher decryptionCipher;
//    
//    public Cryption(String key){
//        try {
//            // Create key and cipher
//            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
//            
//            this.encryptionCipher = Cipher.getInstance("AES");
//            this.encryptionCipher.init(Cipher.ENCRYPT_MODE, aesKey);
//            this.decryptionCipher = Cipher.getInstance("AES");
//            this.decryptionCipher.init(Cipher.DECRYPT_MODE, aesKey);
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
//            Logger.getLogger(Cryption.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    
//    public String encrypt(String text){
//        try {
//            // encrypt the text
//            byte[] encrypted = encryptionCipher.doFinal(text.getBytes());
//            return new String(encrypted).replaceAll("&#", "...---...");
//        } catch (IllegalBlockSizeException | BadPaddingException ex) {
//            Logger.getLogger(Cryption.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//
//    public String decrypt(String encryptedString){
//        try {
//            // decrypt the text
//            byte[] encrypted = encryptedString.replaceAll("...---...", "&#").getBytes();
//            return new String(decryptionCipher.doFinal(encrypted));
//        } catch (IllegalBlockSizeException | BadPaddingException ex) {
//            Logger.getLogger(Cryption.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

    private String key;
    private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES";
 
    public Cryption(String key){
        this.key = key;
    }
    
    public void encrypt(File inputFile, File outputFile){
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
 
    public void decrypt(File inputFile, File outputFile){
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
 
    private void doCrypto(int cipherMode, String key, File inputFile, File outputFile){
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException ex) {
            Logger.getLogger(Cryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
