package com.hosengamers.beluga.loginpage.datacontrol;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class InformationProcess {
	
    private static final String  successAccount= "belugaAccount";
    
    private static final String  successPassword = "belugaPassword";
    
    private static final String successUid = "belugaUserUid";
    
    private static final String thirdPartyInfo = "Third Party Info";
    
    public static boolean ok;
    

    public static void saveAccountPassword(String account,String password,Activity act)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        Editor editor = preferences.edit();

        //key,value
        try {
            String encacc = encrypt(account, "9@a8i7Az");
            String enpwd = encrypt(password, "9@a8i7Az");
            editor.putString(successAccount,URLEncoder.encode(encacc, "utf-8"));
            editor.putString(successPassword,URLEncoder.encode(enpwd, "utf-8"));
            Settings.System.putString(act.getContentResolver(), successAccount, URLEncoder.encode(encacc, "utf-8"));
            Settings.System.putString(act.getContentResolver(), successPassword, URLEncoder.encode(enpwd, "utf-8"));

        } catch (Exception e) {
        }
        editor.commit();

    }
    
    public static void saveThirdPartyInfo(String info, Activity act){
    	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        Editor editor = preferences.edit();
        
        try {
            String data = encrypt(info, "9@a8i7Az");
            editor.putString(thirdPartyInfo, URLEncoder.encode(data, "utf-8"));
            Settings.System.putString(act.getContentResolver(), thirdPartyInfo, URLEncoder.encode(data, "utf-8"));
        } catch (Exception e) {
        }
        editor.commit();
    }
	public static void saveGoogleThirdPartyInfo(String info, Activity act){
	    	
	    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
	        Editor editor = preferences.edit();
	        
	        try {
	            //String data = encrypt(info, "9@a8i7Az");
	        	String data = encrypt(info, "BelugaGoogleIdKey");
	            editor.putString(thirdPartyInfo, URLEncoder.encode(data, "utf-8"));
	            Settings.System.putString(act.getContentResolver(), thirdPartyInfo, URLEncoder.encode(data, "utf-8"));
	        } catch (Exception e) {
	        }
	        editor.commit();
	 }
    
 
    public static void saveUserUid(String uid, Activity act) {
        if (uid.equalsIgnoreCase("") || uid.equalsIgnoreCase("0") || uid == null ) {
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        Editor editor = preferences.edit();
        System.setProperty(successUid, uid);

        try {
            String enUid = encrypt(uid, "9@a8i7Az");
            editor.putString(successUid, URLEncoder.encode(enUid, "utf-8"));
            Settings.System.putString(act.getContentResolver(), successUid, URLEncoder.encode(enUid, "utf-8"));
            editor.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getAccountString(Activity act)
    {
        String getaccountString ;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        getaccountString = preferences.getString(successAccount, "");

        try {
            if(getaccountString.compareTo("")==0){
                getaccountString = Settings.System.getString(act.getContentResolver(), successAccount);
            }
            if(getaccountString == null){
                getaccountString = "";
            }
            getaccountString = URLDecoder.decode(getaccountString, "utf-8");
            String decacc = decrypt(getaccountString, "9@a8i7Az");
            return decacc;
        } catch (Exception e) {

        }
        return "";
    }
    
    public static String getThirdPartyInfo(Activity act)
    {
        String getThirdPartyInfo ;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        getThirdPartyInfo = preferences.getString(thirdPartyInfo, "");

        try {
            if(getThirdPartyInfo.compareTo("")==0){
            	getThirdPartyInfo = Settings.System.getString(act.getContentResolver(), thirdPartyInfo);
            }
            if(getThirdPartyInfo == null){
            	getThirdPartyInfo = "";
            }
            getThirdPartyInfo = URLDecoder.decode(getThirdPartyInfo, "utf-8");
            String decThirdPartyInfo = decrypt(getThirdPartyInfo, "9@a8i7Az");
            return decThirdPartyInfo;
        } catch (Exception e) {

        }
        return "";
    }
    
    public static String getGoogleThirdPartyInfo(Activity act)
    {
        String getThirdPartyInfo ;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        getThirdPartyInfo = preferences.getString(thirdPartyInfo, "");

        try {
            if(getThirdPartyInfo.compareTo("")==0){
            	getThirdPartyInfo = Settings.System.getString(act.getContentResolver(), thirdPartyInfo);
            }
            if(getThirdPartyInfo == null){
            	getThirdPartyInfo = "";
            }
            getThirdPartyInfo = URLDecoder.decode(getThirdPartyInfo, "utf-8");
            String decThirdPartyInfo = decrypt(getThirdPartyInfo, "BelugaGoogleIdKey");
            return decThirdPartyInfo;
        } catch (Exception e) {

        }
        return "";
    }

    public static String getPasswordString(Activity act)
    {
        String getpasswordString ;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        getpasswordString = preferences.getString(successPassword, "");
        try {
            if(getpasswordString.compareTo("")==0){
                getpasswordString = Settings.System.getString(act.getContentResolver(), successPassword);
            }
            if(getpasswordString == null){
                getpasswordString = "";
            }
            getpasswordString = URLDecoder.decode(getpasswordString, "utf-8");
            String decpwd = decrypt(getpasswordString, "9@a8i7Az");
            return decpwd;
        } catch (Exception e) {
        }
        return "";
    }

    public static String getUserUid(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userUid = preferences.getString(successUid, "");

        try {
            if(userUid.compareTo("")==0){
                userUid = Settings.System.getString(context.getContentResolver(), successUid);
            }
            if(userUid == null){
                userUid = "";
            }
            userUid = URLDecoder.decode(userUid, "utf-8");
            String realUid = decrypt(userUid, "9@a8i7Az");
            return realUid;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private final static String characterEncoding = "UTF-8";
    private final static String cipherTransformation = "AES/CBC/PKCS5Padding";
    private final static String aesEncryptionAlgorithm = "AES";

    public static  byte[] decrypt(byte[] cipherText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    public static byte[] encrypt(byte[] plainText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        plainText = cipher.doFinal(plainText);
        return plainText;
    }

    private static byte[] getKeyBytes(String key) throws UnsupportedEncodingException{
        byte[] keyBytes= new byte[16];
        byte[] parameterKeyBytes= key.getBytes(characterEncoding);
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }

    /// <summary>
    /// Encrypts plaintext using AES 128bit key and a Chain Block Cipher and returns a base64 encoded string
    /// </summary>
    /// <param name="plainText">Plain text to encrypt</param>
    /// <param name="key">Secret key</param>
    /// <returns>Base64 encoded string</returns>
    private static String encrypt(String plainText, String key) throws Exception{
        byte[] plainTextbytes = plainText.getBytes(characterEncoding);
        byte[] keyBytes = getKeyBytes(key);
        return Base64.encodeToString(encrypt(plainTextbytes,keyBytes, keyBytes), Base64.DEFAULT);
    }

    /// <summary>
    /// Decrypts a base64 encoded string using the given key (AES 128bit key and a Chain Block Cipher)
    /// </summary>
    /// <param name="encryptedText">Base64 Encoded String</param>
    /// <param name="key">Secret Key</param>
    /// <returns>Decrypted String</returns>
    private static String decrypt(String encryptedText, String key) throws Exception{
        byte[] cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] keyBytes = getKeyBytes(key);
        return new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);
    }
}
