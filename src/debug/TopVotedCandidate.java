package debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In an election, the i-th vote was cast for persons[i] at time times[i].
 *
 * Now, we would like to implement the following query function:
 * TopVotedCandidate.q(int t) will return the number of the person that was
 * leading the election at time t.
 *
 * Votes cast at time t will count towards our query. In the case of a tie, the
 * most recent vote (among tied candidates) wins.
 *
 *
 *
 * Example 1:
 *
 * Input: ["TopVotedCandidate","q","q","q","q","q","q"],
 * [[[0,1,1,0,0,1,0],[0,5,10,15,20,25,30]],[3],[12],[25],[15],[24],[8]]
 *  Output:
 * [null,0,1,1,0,0,1]
 *
 * Explanation:
 * At time 3, the votes are [0], and 0 is leading.
 * At time 12, the votes are [0,1,1], and 1 is leading.
 * At time 25, the votes are [0,1,1,0,0,1], and 1 is leading (as ties go to the most recent
 * vote.)
 * This continues for 3 more queries at time 15, 24, and 8.
 *
 *
 * Note:
 *
 * 1 <= persons.length = times.length <= 5000
 * 0 <= persons[i] <= persons.length
 * times is a strictly increasing array with all elements in [0, 10^9].
 * TopVotedCandidate.q is called at most 10000 times per test case.
 * TopVotedCandidate.q(int t) is always called with t >= times[0].
 *
 */
class TopVotedCandidate {
	List<List<Vote>> A;

	public TopVotedCandidate(int[] persons, int[] times) {
		A = new ArrayList<>();
		Map<Integer, Integer> count = new HashMap<>();
		for (int i = 0; i < persons.length; ++i) {
			int p = persons[i], t = times[i];
			int c = count.getOrDefault(p, 0) + 1;

			count.put(p, c);
			while (A.size() < c)
				A.add(new ArrayList<>());
			A.get(c - 1).add(new Vote(p, t));
		}
	}

	public int q(int t) {
		int lo = 0, hi = A.size();
		while (lo < hi) {
			int mi = lo + (hi - lo) / 2;
			var lmi = A.get(mi);
			if (lmi.get(0).time <= t && t <= lmi.get(lmi.size() - 1).time) {
				lo = mi;
				break;
			}
			else if(lmi.get(0).time > t)
				hi = mi - 1;
			else if(lmi.get(lmi.size() - 1).time < t)
				lo = mi + 1;
		}
		int i = lo;
		
		lo = 0;
		hi = A.get(i).size();
		while (lo < hi) {
			int mi = lo + (hi - lo) / 2;
			var vmi = A.get(i).get(mi);
			if (vmi.time < t)
				lo = mi + 1;
			else if(vmi.time == t) {
				lo = mi;
				break;
			}
			else hi = mi - 1;
		}
		int j = Math.max(lo, 0);
		return A.get(i).get(j).person;
	}
}

class Vote {
	int person, time;

	Vote(int p, int t) {
		person = p;
		time = t;
	}
}