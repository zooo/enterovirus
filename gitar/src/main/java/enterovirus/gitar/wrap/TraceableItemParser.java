package enterovirus.gitar.wrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class TraceableItemParser {

	private String itemTag;
	private List<String> upstreamItemTags = new ArrayList<String>();
	private String content;
	private boolean isTraceableItem = true;
	
	/*
	 * Useful links:
	 * 
	 * Java regular expression special characters:
	 * https://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
	 * 
	 * Regular expression how to:
	 * http://www.vogella.com/tutorials/JavaRegularExpressions/article.html
	 * 
	 * Scanner how to:
	 * https://docs.oracle.com/javase/1.5.0/docs/api/java/util/Scanner.html
	 * 
	 * A brackets: \\[ and \\]
	 * A curly bracket: \\{ and \\}
	 * A non-whitespace character: \S
	 */
	public TraceableItemParser(String lineContent) {
		
		Scanner s = new Scanner(lineContent);

		try {
			/*
			 * The pattern this class currently supporting:
			 * (1) - [itemTag] content
			 */
			try {
				s.findInLine("- \\[(\\S+)\\] (\\S+)");
				MatchResult result = s.match();
				itemTag = result.group(1);
				content = result.group(2);
			}
			/*
			 * The pattern this class currently supporting:
			 * (2) - [itemTag]{0-or-more-upstreamItemTags} content
			 */
			catch (IllegalStateException e) {
				s.findInLine("- \\[(\\S+)\\]\\{(\\S+)\\} (\\S+)");
				MatchResult result = s.match();
				itemTag = result.group(1);
				for (String upstreamItemTag : result.group(2).split(",")) {
					upstreamItemTags.add(upstreamItemTag);
				}
				content = result.group(3);			
			}
		}
		/*
		 * If all not match, then this line is not a traceable item at all.
		 */
		catch (IllegalStateException e) {
			isTraceableItem = false;
		}
		
		s.close();
	}

	public String getItemTag() {
		return itemTag;
	}
	
	public List<String> getUpstreamItemTags() {
		return upstreamItemTags;
	}

	public String getContent() {
		return content;
	}
	
	public boolean isTraceableItem () {
		return isTraceableItem;
	}
}
