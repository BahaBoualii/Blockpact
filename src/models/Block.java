package models;

import helpers.StringUtil;

import java.util.Date;

public class Block {
    public String hash;
    private String data;
    // the data we're working with is simple text/message
    public String previousHash;
    private long timeStamp;
    private int nonce;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = generateHash();
    }

    public String generateHash() {
        return StringUtil.applySHA256(
                previousHash +
                        data +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce)
        );
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce ++;
            hash = generateHash();
        }
        System.out.println("Block Mined!!! :" + hash);
    }
}
