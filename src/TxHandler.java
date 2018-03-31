import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class TxHandler {

	/**
	 * Creates a public ledger whose current UTXOPool (collection of unspent
	 * transaction outputs) is {@code utxoPool}. This should make a copy of utxoPool
	 * by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public UTXOPool pool;

	public TxHandler(UTXOPool utxoPool) {
		this.pool = new UTXOPool(utxoPool);
	}

	/**
	 * @return true if: (1) all outputs claimed by {@code tx} are in the current
	 *         UTXO pool, (2) the signatures on each input of {@code tx} are valid,
	 *         (3) no UTXO is claimed multiple times by {@code tx}, (4) all of
	 *         {@code tx}s output values are non-negative, and (5) the sum of
	 *         {@code tx}s input values is greater than or equal to the sum of its
	 *         output values; and false otherwise.
	 */
	public boolean isValidTx(Transaction tx) {
		double inputSum = 0;
		double outputSum = 0;

		UTXOPool checkPool = new UTXOPool();
		for( int i = 0; i< tx.getInputs().size() ;i ++ ) {
			Transaction.Input input = tx.getInput(i);
			UTXO utxo = new UTXO( input.prevTxHash , input.outputIndex);
			Transaction.Output output = this.pool.getTxOutput( utxo );
			//step 1 check
			if( !this.pool.contains( utxo ) || output == null) {
				return false;
			}
			//step 2 check
			if( !Crypto.verifySignature(output.address, tx.getRawDataToSign(i), input.signature) ) {
				return false;
			}
			
			
			//step 3 check
			if( checkPool.contains( utxo ) ) {
				return false;
			}
			else {
				checkPool.addUTXO( utxo , output);
			}
			
			inputSum += output.value;
		}
		
		for( int i = 0 ;i< tx.getOutputs().size() ;i++) {
			Transaction.Output output = tx.getOutput( i );
			//step 4 check
			if( output.value < 0 ) {
				return false;
			}
			else {
				outputSum += output.value;
			}
		}
		//step 5 check
		if( inputSum >= outputSum ) {
			return true;
		}
		else {
			return false;
		}

	}

	/**
	 * Handles each epoch by receiving an unordered array of proposed transactions,
	 * checking each transaction for correctness, returning a mutually valid array
	 * of accepted transactions, and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		// IMPLEMENT THIS
		Set<Transaction> set = new HashSet<>();
		for( Transaction transaction : possibleTxs ) {
			if(isValidTx( transaction )) {
				set.add( transaction );
				for(  Transaction.Input input : transaction.getInputs() ) {
					UTXO deleteUTXO = new UTXO( input.prevTxHash , input.outputIndex);
					this.pool.removeUTXO( deleteUTXO );
				}
				for( Transaction.Output output : transaction.getOutputs() ) {
					UTXO addUTXO = new UTXO( transaction.getHash(), transaction.getOutputs().indexOf( output ) );
					this.pool.addUTXO( addUTXO , output);
				}
			}
		}
		Transaction[] result = new Transaction[set.size()];
		return set.toArray(result);
	}

}
