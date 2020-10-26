package com.sunfintech.encrypt.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import com.sunfintech.common.util.FileUtil;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 加密、解密工具
 * @author Created by baidu&wuhuan 2018-10-29
 */
public class SecurityUtil {

    public static final String SIGN_ALGORITHM = "SHA256withRSA";
    /**
     * 计算sha256摘要算法.
     */
    public static final String SHA256 = "SHA-256";
    public static final String CHARSET = "UTF-8";
    public static final String AES_ALGORITHM_CFB_PKCS5 = "AES/CFB/PKCS5Padding";
    /**
     * 加密模式
     */
    public static final String RSA_ALGORITHM_ECB_PKCS1 = "RSA/ECB/PKCS1Padding";
    public static final String AES_ALGORITHM_ECB_PKCS5 = "AES/ECB/PKCS5Padding";
    public static final String RSA = "RSA";
    public static final String AES = "AES";
    /**
     * 1024
     */
    protected static final int READSIZE = 0x400;

    /**
     * 获取RSA算法的钥匙工厂类
     * 用来获取 操作公钥 或 操作私钥 的实体类
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyFactory getRSAKeyFactory() throws NoSuchAlgorithmException {
        // 3. 获取公钥私钥
        try {
            return KeyFactory.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }
    }

    /**
     * 获取AES算法的钥匙工厂类
     * 用来获取 加密需要的 key
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyGenerator getAESKeyGenerator() throws NoSuchAlgorithmException {
        try {
            return KeyGenerator.getInstance(AES);
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }
    }

    /**
     * 获取操作公钥的类,可用于加密,加签等.
     * @param keyFactory
     * @param key
     * @return
     * @throws InvalidKeySpecException
     */
    public static PublicKey getX509EncodedKeySpec(KeyFactory keyFactory, String key) throws InvalidKeySpecException {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(
                Base64.decodeBase64(key));
        PublicKey publicKey = null;
        try {
            publicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            throw e ;
        }
        return publicKey;
    }

    /**
     * 获取操作私钥的类,可用于解密,验签等.
     * @param keyFactory
     * @param key
     * @return
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPKCS8EncodedKeySpec(KeyFactory keyFactory, String key) throws InvalidKeySpecException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                Base64.decodeBase64(key));
        PrivateKey privateKey = null;
        try {
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            throw e ;
        }
        return privateKey;
    }

    /**
     * 根据公钥和原始内容产生加密内容.
     * RSA/ECB算法加密
     * @param key		百度的公钥
     * @param content	加密的内容
     * @return
     * @throws Exception
     */
    public static String encryptRSA(PublicKey key, String content) throws Exception {

        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM_ECB_PKCS1);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeBase64String(cipher.doFinal(content.getBytes(CHARSET)));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据公钥和原始内容产生加密内容.
     * RSA算法加密
     * @param key		百度的公钥
     * @param content	加密的内容
     * @return	加密结果
     * @throws Exception
     */
    public static String encryptRSA(PublicKey key, byte[] content) throws Exception {

        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM_ECB_PKCS1);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeBase64String(cipher.doFinal(content));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 计算一个文件的sha256摘要.
     */
    public static String sha256file(String fileName) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(SHA256);
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }
        DigestInputStream dis = null;
        try {
            dis = new DigestInputStream(new BufferedInputStream(new FileInputStream(fileName)), md);
        } catch (FileNotFoundException e) {
            throw e;
        }
        try {
            while (dis.read() != -1) {
                ;
            }
            dis.close();
        } catch (IOException e) {
            throw e;
        }
        return Base64.encodeBase64String(md.digest());
    }

    /**
     * 根据AES的key和IV加密文件.
     */
    public static String[] encryptFiles(SecretKeySpec aesKey, IvParameterSpec iv, String decryptDir,
                                        String encryptDir, String targetFileSuffix) throws Exception {

        // 筛选.csv文件并按照升序方式排序
        String[] sortedFiles = getSortedFiles(decryptDir, targetFileSuffix);

        // 遍历该文件夹下的所有文件
        for (String fileName : sortedFiles) {
            File inFile = new File(decryptDir + File.separator + fileName);
            File outFile = new File(encryptDir + File.separator + fileName);
            // 加密文件
            encryptFileAES(aesKey, inFile, outFile, iv);
        }
        return sortedFiles;
    }

    /**
     * 根据AES的key加密文件.
     */
    public static String[] encryptFiles4ABC(SecretKeySpec aesKey, String decryptDir, String encryptDir,
                                            String targetFileSuffix) throws Exception {

        // 筛选.csv文件并按照升序方式排序
        String[] sortedFiles = getSortedFiles(decryptDir, targetFileSuffix);

        // 遍历该文件夹下的所有文件
        for (String fileName : sortedFiles) {
            File inFile = new File(decryptDir + File.separator + fileName);
            File outFile = new File(encryptDir + File.separator + fileName);
            // 加密文件
            encryptFileAES4ABC(aesKey, inFile, outFile);
        }
        return sortedFiles;
    }

    /**
     * 获取加密/解密文件
     * 将指定目录下以指定后缀命名的文件按升序排序.
     * @param decryptDir		目录
     * @param targetFileSuffix	文件后缀
     * @return
     */
    public static String[] getSortedFiles(String decryptDir, String targetFileSuffix) {
        File file = new File(decryptDir);
        String[] files = file.list();

        // 在noah上一任务起始时间一致, 分表数>机器数的情况下,不会出现文件为空的情况,
        // 如果出现没有文件的情况,需要具体核实下
        if (files == null) {
            return new String[0];
        }
        return filterAndSortFiles(files, targetFileSuffix);
    }

    /**
     * 用对方公钥publicKey进行验签.
     * @param key		百度的公钥
     * @param content	验签的内容
     * @param sign		签名
     * @return	验签结果
     * @throws Exception
     */
    public static boolean verify(PublicKey key, String content, String sign) throws Exception {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(key);
            signature.update(content.getBytes(CHARSET));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据私钥和加密内容产生原始内容.
     * RSA/ECB算法解密
     * @param key		前置的私钥
     * @param content	加密的内容
     * @return	解密结果
     */
    public static String decryptRSA(PrivateKey key, String content)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, DecoderException,
            BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM_ECB_PKCS1);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(content)), CHARSET);
    }

    /**
     * 打包加密的csv文件和key文件.
     */
    public static void doTarGZip(String encryptDir, String keyFileName, String[] fileNames, String tarFileName) throws IOException {

        try {
            TarArchiveOutputStream tar = new TarArchiveOutputStream(
                    new GzipCompressorOutputStream(
                            new BufferedOutputStream(
                                    new FileOutputStream(new File(encryptDir + File.separator + tarFileName)))));
            // 加密文件打包
            for (String name : fileNames) {
                File file = new File(encryptDir + File.separator + name);
                TarArchiveEntry tarEntry = new TarArchiveEntry(name);
                tarEntry.setSize(file.length());
                tar.putArchiveEntry(tarEntry);
                InputStream in = new FileInputStream(file);
                FileUtil.copy(in, tar);
                in.close();
                tar.closeArchiveEntry();
            }
            // key打包
            File file = new File(encryptDir + File.separator + keyFileName);
            TarArchiveEntry tarEntry = new TarArchiveEntry(keyFileName);
            tarEntry.setSize(file.length());
            tar.putArchiveEntry(tarEntry);
            InputStream in = new FileInputStream(file);
            FileUtil.copy(in, tar);
            in.close();
            tar.closeArchiveEntry();
            tar.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 根据AES的key, 加密文件.
     * AES/ECB模式(不能使用IV)
     */
    public static void encryptFileAES4ABC(SecretKey key, File in, File out) throws Exception {

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(AES_ALGORITHM_ECB_PKCS5);
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (Exception e) {
            throw e;
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(in);
        } catch (FileNotFoundException e) {
            throw e;
        }
        CipherOutputStream os = null;
        try {
            os = new CipherOutputStream(new FileOutputStream(out), cipher);
        } catch (FileNotFoundException e) {
        	is.close();
            throw e;
        }
        FileUtil.copy(is, os);
        try {
            is.close();
            os.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 根据AES的key, 解密文件.
     * AES/ECB模式(不能使用IV)
     */
    public static void decryptFileAES4ABC(SecretKey key, File in, File out) throws Exception {

        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_ECB_PKCS5);
            cipher.init(Cipher.DECRYPT_MODE, key);
            CipherInputStream is = new CipherInputStream(new FileInputStream(in), cipher);
            FileOutputStream os = new FileOutputStream(out);
            FileUtil.copy(is, os);
            is.close();
            os.close();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据私钥和数据内容产生签名, base64编码.
     */
    public static String sign(PrivateKey key, String content) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initSign(key);
            signature.update(content.getBytes(CHARSET));
            return Base64.encodeBase64String(signature.sign());
        } catch (Exception e) {
            throw  e;
        }
    }

    /**
     * 筛选.xxx文件并按照升序方式排序
     */
    public static String[] filterAndSortFiles(String[] files, String targetFileSuffix) {
        return Arrays.asList(files).stream()
                .filter(fileName -> fileName.endsWith(targetFileSuffix))
                .sorted(Comparator.comparing(a -> a.substring(0, a.length() - targetFileSuffix.length())))
                .toArray(String[]::new);
    }

    /**
     * 解密文件(批量)
     * AES/CFB模式(使用IV)
     */
    public static void decryptfile(SecretKeySpec aesKey2, IvParameterSpec iv,
    		String encryptDir, String decryptDir, String[] fileNames) throws Exception {
    	decryptfile(aesKey2, iv, encryptDir, decryptDir, fileNames, false);
    }

    /**
     * 解密文件(批量)
     * AES/CFB模式(使用IV)
     */
    public static void decryptfile(SecretKeySpec aesKey2, IvParameterSpec iv,
                                   String encryptDir, String decryptDir, String[] fileNames, boolean isDel) throws Exception {

        for (String name : fileNames) {
            File inFile = new File(encryptDir + File.separator + name);
            File outFile = new File(decryptDir + File.separator + name);
            decryptFileAES(aesKey2, inFile, outFile, iv);
            // 解密文件后删除文件.
            if (isDel) {
            	inFile.delete();
			}
        }
    }

    /**
     * 根据AES的key和IV, 加密文件.
     * AES/CFB模式(使用IV)
     */
    public static void encryptFileAES(SecretKey key, File in, File out, IvParameterSpec iv) throws Exception {

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(AES_ALGORITHM_CFB_PKCS5);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        } catch (Exception e) {
            throw e;
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(in);
        } catch (FileNotFoundException e) {
            throw e;
        }
        CipherOutputStream os = null;
        try {
            os = new CipherOutputStream(new FileOutputStream(out), cipher);
        } catch (FileNotFoundException e) {
        	is.close();
            throw e;
        }
        FileUtil.copy(is, os);
        try {
            is.close();
            os.close();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据AES的key和IV, 解密文件.
     * AES/CFB模式(使用IV)
     */
    public static void decryptFileAES(SecretKey key, File in, File out, IvParameterSpec iv) throws Exception {

        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_CFB_PKCS5);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            CipherInputStream is = new CipherInputStream(new FileInputStream(in), cipher);
            FileOutputStream os = new FileOutputStream(out);
            FileUtil.copy(is, os);
            is.close();
            os.close();
        } catch (Exception e) {
            throw e;
        }
    }

}
