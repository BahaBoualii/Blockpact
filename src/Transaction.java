import helpers.StringUtil;
import models.TransactionInput;
import models.TransactionOutput;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    public String transactionID;
    public PublicKey sender;
    public PublicKey recipient;
    public float amount;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs;
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.amount = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++;
        return StringUtil.applySHA256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(amount) +
                        sequence
        );
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(amount);
        signature = StringUtil.applyECDSASignature(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(amount);
        return StringUtil.verifyECDSASignature(sender, data, signature);
    }

    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("#Transaction Signature verification failed");
            return false;
        }

        for(TransactionInput input : inputs) {
            input.UTXO = NewbieChain.UTXOs.get(input.transactionOutputId);
        }

        if(getInputsValue() < NewbieChain.minimumTransaction) {
            System.out.println("#Transaction Input value too small: " + getInputsValue());
            System.out.println("Please enter at least " + NewbieChain.minimumTransaction);
            return false;
        }

        float leftOver = getInputsValue() - amount;
        transactionID = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, amount, transactionID));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionID));

        for(TransactionOutput output : outputs) {
            NewbieChain.UTXOs.put(output.id, output);
        }

        for(TransactionInput input : inputs) {
            if (input.UTXO == null) continue;
            NewbieChain.UTXOs.remove(input.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;

        for(TransactionInput input : inputs) {
            if (input.UTXO == null) continue;
            total += input.UTXO.amount;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput output : outputs) {
            total += output.amount;
        }
        return total;
    }
}
