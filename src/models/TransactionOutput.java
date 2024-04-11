package models;

import helpers.StringUtil;

import java.security.PublicKey;

public class TransactionOutput {

    public String id;
    public PublicKey recipient;
    public float amount;
    public String parentTransactionId;

    public TransactionOutput(PublicKey recipient, float amount, String parentTransactionId) {
        this.recipient = recipient;
        this.amount = amount;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySHA256(StringUtil.getStringFromKey(recipient)+Float.toString(amount)+parentTransactionId);
    }

    // check if coin belongs
    public boolean doesCoinBelong(PublicKey publicKey) {
        return (publicKey.equals(recipient));
    }

}
