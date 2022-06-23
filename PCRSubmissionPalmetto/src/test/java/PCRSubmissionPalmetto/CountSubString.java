package PCRSubmissionPalmetto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountSubString {
	private boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public int countMatches(String text, String str) {
		if (isEmpty(text) || isEmpty(str)) {
			return 0;
		}
		int count = 0;
		Matcher matcher = Pattern.compile(str).matcher(text);

		while (matcher.find()) {
			count++;
		}
		return count;
	}
}

