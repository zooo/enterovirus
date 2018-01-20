package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import enterovirus.gitar.GitBranch;
import enterovirus.gitar.GitLog;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.domain.CommitBean;
import enterovirus.protease.domain.RepositoryBean;

/*
 * Functions of this class is not merged into RepositoryRepository,
 * is because some usage of RepositoryBean doesn't need the git data.
 * This makes some kind of lazy evaluation.
 * 
 * TODO:
 * Can it be done in the way that it loads the Git data when calling 
 * "repository.getCommitLog()" and "repository.getBranchNames()" ?
 */
@Component
public class RepositoryGitDAO {

	@Autowired private GitSource gitSource;
	@Autowired private CommitRepository commitRepository;
	
	public RepositoryBean loadCommitLog(RepositoryBean repository, BranchName branchName, Integer maxCount, Integer skip) throws IOException, GitAPIException {
		
		/*
		 * TODO:
		 * Paging which only load part of the log.
		 */
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(repository.getOrganization().getName(), repository.getName());
		GitLog gitLog = new GitLog(repositoryDirectory, branchName, maxCount, skip);
		List<CommitInfo> commitInfos = gitLog.getCommitInfos();
		
		List<CommitSha> commitShas = new ArrayList<CommitSha>();
		for (CommitInfo commitInfo : commitInfos) {
			commitShas.add(commitInfo.getCommitSha());
		}
		
		/*
		 * Do it in one single SQL query by performance concerns.
		 */
		List<CommitBean> commits = commitRepository.findByCommitShaIn(commitShas);
		Map<String,CommitBean> commitMap = new HashMap<String,CommitBean>();
		for (CommitBean commit : commits) {
			commitMap.put(commit.getShaChecksumHash(), commit);
		}
		
		/*
		 * LinkedHashMap to maintain key's order.
		 */
		Map<CommitInfo,CommitBean> commitLogMap = new LinkedHashMap<CommitInfo,CommitBean>();
		for (CommitInfo commitInfo : commitInfos) {
			commitLogMap.put(commitInfo, commitMap.get(commitInfo.getCommitSha().getShaChecksumHash()));
		}
		
		repository.setCommitLogMap(commitLogMap);
		return repository;
	}
	
	public RepositoryBean loadBranchNames (RepositoryBean repository) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(repository.getOrganization().getName(), repository.getName());
		GitBranch gitBranch = new GitBranch(repositoryDirectory);
		repository.setBranchNames(gitBranch.getBranchNames());
		
		return repository;
	}

}
