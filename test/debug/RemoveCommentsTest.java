package debug;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class RemoveCommentsTest {
	
	@Test
	public void removeComments() {
		RemoveComments r = new RemoveComments();
		String[] input = new String[]{
				"/*Test program */",
				"int main()", "{ ",
				"  // variable declaration ",
				"int a, b, c;", "/* This is a test", "   multiline  ", "   comment for ", "   testing */",
				"a = b + c;", "}"
		};
		var ans = new String[]{"int main()","{ ","  ","int a, b, c;","a = b + c;","}"};
		assertArrayEquals(ans, r.removeComments(input).toArray());
		input = new String[]{"a/*comment", "line", "more_comment*/b"};
		assertArrayEquals(new String[]{"ab"}, r.removeComments(input).toArray());
		
	}
}