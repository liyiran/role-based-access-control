/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author lenovo
 */
public class md5 {
    public static void main(String... args) throws NoSuchAlgorithmException, IOException {
        String plaintext = "plaintext";
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(plaintext.getBytes());
        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        System.out.println(hashtext);
        Reader in = new FileReader("D:\\Data-Security\\ServerRMI\\src\\src\\password.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("userName", "password", "rand").parse(in);
        for (CSVRecord record : records) {
            String userName = record.get("userName");
            String password = record.get("password");
            String rand = record.get("rand");
            System.out.println(userName + "," + password + "," + rand);
        }
    }
}
