package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import enterovirus.gitar.GitFolderStructure;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.domain.*;

@Component
public class CommitGitDAO {

	@Autowired private GitSource gitSource;
	
	public CommitValidBean loadFolderStructure (CommitValidBean commit) throws IOException {
		
		String organizationName = commit.getRepository().getOrganization().getName();
		String repositoryName = commit.getRepository().getName();
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		CommitSha commitSha = new CommitSha(commit.getShaChecksumHash());
		
		GitFolderStructure gitFolderStructure = new GitFolderStructure(repositoryDirectory, commitSha);
		commit.setFolderStructure(gitFolderStructure.getFolderStructure());
		
		return commit;
	}

}
