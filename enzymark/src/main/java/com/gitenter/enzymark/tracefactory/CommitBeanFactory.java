package com.gitenter.enzymark.tracefactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.enzymark.traceanalyzer.TraceAnalyzerException;
import com.gitenter.enzymark.traceanalyzer.TraceableDocument;
import com.gitenter.enzymark.traceanalyzer.TraceableItem;
import com.gitenter.enzymark.traceanalyzer.TraceableRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.InvalidCommitBean;
import com.gitenter.protease.domain.git.ValidCommitBean;
import com.gitenter.protease.domain.traceability.TraceableDocumentBean;
import com.gitenter.protease.domain.traceability.TraceableItemBean;

public class CommitBeanFactory {

	public CommitBean getCommit(GitCommit gitCommit) 
			throws FileNotFoundException, CheckoutConflictException, GitAPIException, IOException {
		
		CommitBean commit;
		
		try {
			TraceableRepositoryFactory factory = new TraceableRepositoryFactory();
			TraceableRepository traceableRepository = factory.getTraceableRepository(gitCommit.getRoot());
			
			ValidCommitBean validCommit = new ValidCommitBean();
			validCommit.setFromGitCommit(gitCommit);
			
			Map<TraceableItem,TraceableItemBean> traceabilityIterateMap = new HashMap<TraceableItem,TraceableItemBean>();
			for (TraceableDocument traceableDocument : traceableRepository.getTraceableDocuments()) {
				
				DocumentBean document = new DocumentBean();
				document.setRelativePath(traceableDocument.getRelativePath());
				document.setCommit(validCommit);
				validCommit.addIncludeFile(document);
				
				TraceableDocumentBean traceableDocumentBean = new TraceableDocumentBean();
				traceableDocumentBean.setDocument(document);
				document.setTraceableDocument(traceableDocumentBean);
				
				for (TraceableItem traceableItem : traceableDocument.getTraceableItems()) {
					
					TraceableItemBean itemBean = new TraceableItemBean();
					itemBean.setTraceableDocument(traceableDocumentBean);
					traceableDocumentBean.addTraceableItem(itemBean);
					
					itemBean.setItemTag(traceableItem.getTag());
					itemBean.setContent(traceableItem.getContent());
					
					traceabilityIterateMap.put(traceableItem, itemBean);
				}
			}
				
			for (TraceableDocument traceableDocument : traceableRepository.getTraceableDocuments()) {
				for (TraceableItem traceableItem : traceableDocument.getTraceableItems()) {
					
					/*
					 * TODO:
					 * May try to build some "backed by" collection (through 
					 * "Collection.retainAll()") so the collections of 
					 * "TraceableItem" and "TraceableItemBean" can be handled
					 * together.
					 */
					for (TraceableItem downstreamItem : traceableItem.getDownstreamItems()) {
						traceabilityIterateMap.get(traceableItem).addDownstreamItem(traceabilityIterateMap.get(downstreamItem));
					}
					
					for (TraceableItem upstreamItem : traceableItem.getUpstreamItems()) {
						traceabilityIterateMap.get(traceableItem).addUpstreamItem(traceabilityIterateMap.get(upstreamItem));
					}
				}
			}
			
			commit = validCommit;
		}
		catch (TraceAnalyzerException e) {
			
			/*
			 * TODO:
			 * Can it show all the parsing exceptions at the same time (the current
			 * approach can only show the first exception which errors out)?
			 * Or a better way is to have a client-side hook to handle that?
			 * 
			 * Probably need to recover from the "TraceAnalyzerException"
			 * and continue append the error messages. 
			 */
			InvalidCommitBean invalidCommit = new InvalidCommitBean();
			invalidCommit.setErrorMessage(e.getMessage());
			invalidCommit.setFromGitCommit(gitCommit);
			
			commit = invalidCommit;
		}

		return commit;
	}
}
