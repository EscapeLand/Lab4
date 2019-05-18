package debug;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TopVotedCandidateTest {
	
	@Test
	public void q() {
		TopVotedCandidate t = new TopVotedCandidate(new int[]{0,1,1,0,0,1,0}, new int[]{0,5,10,15,20,25,30});
		var query = new int[]{3,12,25,15,24,8};
		var ans = new int[]{0,1,1,0,0,1};
		assertEquals(ans[0], t.q(query[0]));
		assertEquals(ans[1], t.q(query[1]));
		assertEquals(ans[2], t.q(query[2]));
		assertEquals(ans[3], t.q(query[3]));
		assertEquals(ans[4], t.q(query[4]));
		assertEquals(ans[5], t.q(query[5]));
	}
}