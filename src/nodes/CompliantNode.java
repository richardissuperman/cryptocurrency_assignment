
package nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
	
	private HashSet<Transaction> allTrans = new HashSet<>();
	private boolean[]  followees;
	private double p_malicious;

	public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
		// IMPLEMENT THIS
		this.p_malicious = p_malicious;
	}

	public void setFollowees(boolean[] followees) {
		// IMPLEMENT THIS
		this.followees = followees;	
	}

	public void setPendingTransaction(Set<Transaction> pendingTransactions) {
		// IMPLEMENT THIS
		allTrans.addAll( pendingTransactions );
	}

	public Set<Transaction> sendToFollowers() {
		// IMPLEMENT THIS
		return allTrans;
	}

	public void receiveFromFollowees(Set<Candidate> candidates) {
		// IMPLEMENT THIS
		if(Math.random() < p_malicious) {
			return;
		}
		for(Candidate candidate : candidates) {
			if( !followees[candidate.sender] ) {
				continue;
			}
			allTrans.add( candidate.tx );
		}
	}
}
