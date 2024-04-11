package models;

public class TransactionInput {
    public String transactionOutputId; // ref to TransactionsOutputs
    public TransactionOutput UTXO; //contains the unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
