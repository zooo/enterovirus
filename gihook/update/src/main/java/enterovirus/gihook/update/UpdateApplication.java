package enterovirus.gihook.update;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import enterovirus.gitar.GitCommit;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed. 
 */
@ComponentScan(basePackages = {
		"enterovirus.protease.config",
		"enterovirus.protease.database",
		"enterovirus.protease.domain"})
public class UpdateApplication {
	
//	@Autowired Tmp tmp;
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired CommitRepository commitRepository;
	
	private void hook (String branchName, String oldCommitSha, String newCommitSha) throws IOException {
//		System.out.println("hello world");
//		System.out.println(tmp.find());

		
		System.out.println("Current directory: "+System.getProperty("user.dir"));
		
		File repositoryDirectory = new File(System.getProperty("user.dir"));
		CommitSha commitSha = new CommitSha(newCommitSha);
		
		String organizationName = GitSource.getBareRepositoryOrganizationName(repositoryDirectory);
		String repositoryName = GitSource.getBareRepositoryName(repositoryDirectory);
		System.out.println("organizationName="+organizationName);
		System.out.println("repositoryName="+repositoryName);

		GitCommit gitCommit = new GitCommit(repositoryDirectory, commitSha);
		showFolderStructure(gitCommit);
		
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName(organizationName, repositoryName);
		CommitBean commitBean = new CommitBean(repository, commitSha);
		commitRepository.saveAndFlush(commitBean);
		

	}
	
	public static void main (String[] args) throws IOException {

		String branchName = args[0];
		String oldCommitSha = args[1];
		String newCommitSha = args[2];
		
		System.out.println("branchName: "+branchName);
		System.out.println("oldCommitSha: "+oldCommitSha);
		System.out.println("newCommitSha: "+newCommitSha);
		
		ApplicationContext context = new AnnotationConfigApplicationContext(UpdateApplication.class);
		UpdateApplication p = context.getBean(UpdateApplication.class);
		p.hook(branchName, oldCommitSha, newCommitSha);
	}
	
	private static void showFolderStructure (GitCommit gitCommit) {
		showHierarchy(gitCommit.getFolderStructure(), 0);
	}
	
	private static void showHierarchy (GitCommit.ListableTreeNode parentNode, int level) {
		
		for (int i = 0; i < level; ++i) {
			System.out.print("\t");
		}
		System.out.println(parentNode);
		
		for(GitCommit.ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
}
