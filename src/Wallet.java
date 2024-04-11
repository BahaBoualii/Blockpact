import models.TransactionInput;
import models.TransactionOutput;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    // this will act as the wallet address to receive payment
    public PrivateKey privateKey;
    // this will be used to sign the transactions
    public PublicKey publicKey;

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyPairGenerator.initialize(ecSpec, random);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {
        float total = 0;
        for(Map.Entry<String, TransactionOutput> item: NewbieChain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if(UTXO.doesCoinBelong(publicKey)) {
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.amount;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey _recipient, float value) {
        if (getBalance() < value) {
            System.out.println("#Not enough funds to send transaction, transaction will be discarded");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for(Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.amount;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs) {
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }

}
