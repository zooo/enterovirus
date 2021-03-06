package com.gitenter.enzymark.traceanalyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.gitenter.protease.domain.git.FileType;

public class TraceableFileTest {
	
	@Test
	public void testIfNoTraceableItemIncluded() throws Exception {
		
		String textContent = 
				  "normal text\n"
				+ "\n"
				+ "- normal list item 1\n"
				+ "- normal list item 2\n"
				+ "\n"
				+ "- **bold** list item\n"
				+ "- *italic* list item\n"
				+ "\n"
				+ "more text\n"
				+ "\n";
		
		TraceableFile document = mock(TraceableFile.class);
		document.parse(textContent);
		
		verify(document, never()).addTraceableItem(any(TraceableItem.class));
	}
	
	@Test
	public void testParseATraceableItem() throws Exception {
		
		String textContent = "- [tag]{refer} Traceable item content with **bold** and *italic*.\n";
		
		TraceableFile document = new TraceableFile("/fake/relative/file/path.md", FileType.MARKDOWN);
		TraceableFile spyDocument = spy(document);
		spyDocument.parse(textContent);
		
		verify(spyDocument, times(1)).addTraceableItem(any(TraceableItem.class));
		assertEquals(spyDocument.getTraceableItems().size(), 1);
		
		TraceableItem traceableItem = spyDocument.getTraceableItems().get(0);
		assertEquals(traceableItem.getTag(), "tag");
		assertEquals(traceableItem.upstreamItemTags.length, 1);
		assertEquals(traceableItem.upstreamItemTags[0], "refer");
		assertEquals(traceableItem.getContent(), "Traceable item content with <strong>bold</strong> and <em>italic</em>.");
	}
	
	@Test
	public void testParseTraceableItems() throws Exception {
		
		/*
		 * TODO:
		 * 
		 * For the case of mixed normal list and traceable item,
		 * think about a way how to handle that. (Should that be
		 * legal? Do we support it? How to support it?)
		 */
		String textContent = 
				  "- [tag]{refer} Dummy traceable item.\n"
				+ "- [another-tag] Another dummy traceable item.\n"
				+ "- [the-third-tag]{} A third traceable item";
		
		TraceableFile document = new TraceableFile("/fake/relative/file/path.md", FileType.MARKDOWN);
		TraceableFile spyDocument = spy(document);
		spyDocument.parse(textContent);
		
		verify(spyDocument, times(3)).addTraceableItem(any(TraceableItem.class));
		assertEquals(spyDocument.getTraceableItems().size(), 3);
	}
}