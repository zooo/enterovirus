package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitFolderStructureTest {
	
	File repositoryDirectory;
	
	@Before
	public void initialize() {
		repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one-repo-fix-commit/org/repo.git");
	}
	
	@Test
	public void test1() throws IOException {
		
		GitFolderStructure gitCommit;
		
		File commitRecordFile = new File("/home/beta/Workspace/enterovirus-test/one-repo-fix-commit/commit-sha-list.txt");
		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
		
		gitCommit = new GitFolderStructure(repositoryDirectory, commitSha);
		
		System.out.println(gitCommit.getCommitSha().getShaChecksumHash());
		showFolderStructure(gitCommit);
	}

	@Test
	public void test2() throws IOException {
		
		GitFolderStructure gitCommit;
		
		BranchName branchName = new BranchName("master"); 
		gitCommit = new GitFolderStructure(repositoryDirectory, branchName);
		
		System.out.println(gitCommit.getCommitSha().getShaChecksumHash());
		showFolderStructure(gitCommit);
	}

	@Test
	public void test3() throws IOException {
		
		GitFolderStructure gitCommit;
			
		gitCommit = new GitFolderStructure(repositoryDirectory);
		
		System.out.println(gitCommit.getCommitSha().getShaChecksumHash());
		showFolderStructure(gitCommit);
	}
	
	private void showFolderStructure (GitFolderStructure gitCommit) {
		showHierarchy(gitCommit.getFolderStructure(), 0);
	}
	
	private void showHierarchy (GitFolderStructure.ListableTreeNode parentNode, int level) {
		
		for (int i = 0; i < level; ++i) {
			System.out.print("\t");
		}
		System.out.println(parentNode);
		
//		Enumeration<TreeNode> e = parentNode.children();
////		while(e.hasMoreElements()) {
////			TreeNode node = e.nextElement();
////			showHierarchy(node);
////		}		
//		for(TreeNode node : Collections.list(e)) {
//			showHierarchy(node, level+1);
//		}
		
		for(GitFolderStructure.ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
}