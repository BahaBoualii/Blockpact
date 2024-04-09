import com.google.gson.GsonBuilder;
import models.Block;

import java.util.ArrayList;

public class Main {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 6;

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            if(!currentBlock.hash.equals(currentBlock.generateHash())){
                System.out.println("current block hash is incorrect");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("previous block hash is incorrect");
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        blockchain.add(new Block("Hi i am the first block", "0"));
        System.out.println("Trying to mine block 1...");
        blockchain.get(0).mineBlock(difficulty);
        blockchain.add(new Block("Yo i am the first block", blockchain.get(blockchain.size()-1).hash));
        System.out.println("Trying to mine block 2...");
        blockchain.get(1).mineBlock(difficulty);
        blockchain.add(new Block("Hey i am the first block", blockchain.get(blockchain.size()-1).hash));
        System.out.println("Trying to mine block 3...");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is valid: " + isChainValid());

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }
}