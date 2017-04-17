package com.liangyou.util.des;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;

import org.apache.commons.io.IOUtils;

public class DesUtil {
	
	public static void main(String[] args) {
	//createDesKey("desdb.key");
		Key key = getKey("desdb.key");
		try {
			encrypt("db.properties", "desdb.properties", key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * 生成密钥
     * 
     * @param keyPath 密钥文件
     */
    public static void createDesKey(String keyPath) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            SecureRandom sr = new SecureRandom();
            KeyGenerator kg = KeyGenerator.getInstance("DES");
            kg.init(sr);
            fos = new FileOutputStream(keyPath);
            oos = new ObjectOutputStream(fos);
            // 生成密钥
            Key key = kg.generateKey();
            oos.writeObject(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(oos);
        }
    }

    /**
     * 获得密钥
     * 
     * @param keyPath
     * @return
     */
    public static Key getKey(String keyPath) {
        Key kp = null;
        InputStream is = null;
        ObjectInputStream ois = null;
        try {
            is = ClassLoader.getSystemClassLoader().getResourceAsStream(keyPath);
            return getKey(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(ois);
        }
        return kp;
    }
    
    /**
     * 获得密钥
     * @param is
     * @return
     */
    public static Key getKey(InputStream is) {
        Key key = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(is);
            key = (Key)ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(ois);
        }
        return key;
    }

    /**
     * 加密源文件并保存到目标文件
     * 
     * @param srcFile
     *            源文件
     * @param destFile
     *            目标文件
     * @param key
     *            加密用的Key
     * @throws Exception
     */
    public static void encrypt(String srcFile, String destFile, Key key) throws Exception {
        InputStream is = null;
        OutputStream out = null;
        CipherInputStream cis = null;
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            is = ClassLoader.getSystemClassLoader().getResourceAsStream(srcFile);
            out = new FileOutputStream(destFile);
            cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
        } finally {
            IOUtils.closeQuietly(cis);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
        }

    }

    /**
     * 解密文件
     * 
     * @param file
     * @param key
     * @return
     * @throws Exception
     */
    public static InputStream decrypt(InputStream is, Key key) throws Exception {
        OutputStream out = null;
        CipherOutputStream cos = null;
        ByteArrayOutputStream bout = null;
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = is.read(buf)) != -1) {
                bout.write(buf, 0, count);
                buf = new byte[1024];
            }
            byte[] orgData = bout.toByteArray();
            byte[] raw = cipher.doFinal(orgData);
            return new ByteArrayInputStream(raw);
        } finally {
            IOUtils.closeQuietly(cos);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(bout);
        }
    }
}

