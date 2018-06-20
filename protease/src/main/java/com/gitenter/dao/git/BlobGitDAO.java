package com.gitenter.dao.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitenter.domain.git.BlobBean;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitFile;
import com.gitenter.gitar.GitRepository;
import com.gitenter.protease.source.GitSource;

@Component
public class BlobGitDAO {

	@Autowired private GitSource gitSource;
	
	public BlobBean find(String organizationName, String repositoryName, String commitSha, String relativeFilepath) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getCommit(commitSha);
		GitFile gitFile = gitCommit.getFile(relativeFilepath);
		
		return new BlobBean(gitFile.getBlobContent(), gitFile.getMimeType());
	}

	public BlobBean find(String organizationName, String repositoryName, BranchBean branch, String relativeFilepath) throws IOException, GitAPIException {

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getBranch(branch.getName()).getHead();
		GitFile gitFile = gitCommit.getFile(relativeFilepath);
		
		return new BlobBean(gitFile.getBlobContent(), gitFile.getMimeType());
	}
}