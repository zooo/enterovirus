package enterovirus.capsid.domain;

import java.util.*;
import lombok.*;

@Getter
@Setter
public class TextFileBean extends BlobBean {
	
	private List<LineContentBean> lineContents = new ArrayList<LineContentBean>();
	
//	private String content;
	
	public void addLineContent(LineContentBean lineContent) {
		lineContents.add(lineContent);
	}
}
