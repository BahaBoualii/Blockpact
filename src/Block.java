import helpers.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private long timeStamp;
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = generateHash();
    }

    //Calculate new hash based on blocks contents
    public String generateHash() {
        return StringUtil.applySHA256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
    }

    //Increases nonce value until hash target is reached.
    public void mineBlock(int difficulty) {
        merkleRoot = Helpers.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce ++;
            hash = generateHash();
        }
        System.out.println("Block Mined!!! :" + hash);
    }

    //Add transactions to this block
    public void addTransaction(Transaction transaction) {
        if(transaction == null) return;
        if(!"0".equals(previousHash)) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to block!!");
    }
}
