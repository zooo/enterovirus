package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitCommit {	

	/*
	 * JGit's "TreeWalk" class provides some simply functions
	 * to iterate some multi-child tree structure. However, that
	 * interface is really bad:
	 * 
	 * (1) It seems can only iterate once. There's no way to 
	 * re-navigate the tree structure or navigate it in a user
	 * defined way.
	 * 
	 * (2) Its "next()" walk to the next relevant entry but also
	 * return whether there's a next entry. There's no "hasNext()".
	 * 
	 * Therefore, we copy the tree structure out to another class
	 * (we use Swing's TreeModel and TreeNode -- although initially
	 * proposed to Swing's GUI to display, there's no reason to not
	 * just use the structure) and generate the folder structure
	 * navigating functions based on it. 
	 */
	private MutableTreeNode folderStructure;
	
	public GitCommit (File repositoryDirectory, CommitSha commitSha) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(ObjectId.fromString(commitSha.getShaChecksumHash()));
		RevTree revTree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(false);
		
		folderStructure = generateFolderStructureFromTreeWalk(treeWalk);
	}

	public GitCommit (File repositoryDirectory, BranchName branchName) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		Ref branch = repository.exactRef("refs/heads/"+branchName.getName());
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(branch.getObjectId());
		RevTree revTree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(false);
		
		folderStructure = generateFolderStructureFromTreeWalk(treeWalk);
	}
	
	public GitCommit (File repositoryDirectory) throws IOException {
		this(repositoryDirectory, new BranchName("master"));
	}
	
	private MutableTreeNode generateFolderStructureFromTreeWalk (TreeWalk treeWalk) throws IOException {
		
		MutableTreeNode folderStructure = new DefaultMutableTreeNode(".");
		
//		while (treeWalk.next()) {
//			if (treeWalk.isSubtree()) {
//				System.out.println("dir: " + treeWalk.getPathString()+"----"+"Depth:"+treeWalk.getDepth());
//				treeWalk.enterSubtree();
//			} else {
//				System.out.println("file: " + treeWalk.getPathString()+"----"+"Depth:"+treeWalk.getDepth());
//			}
//		}
		
		/*
		 * Currently "treeWalk == null" unless we do one time
		 * "treeWalk.next()". There's an "if" for the case if
		 * the repository has no file at all.
		 */
		if (treeWalk.next()) {
			
			int position = 0;
			while (true) {
				GenerateTreeReturnValue returnValue = generateTree(treeWalk);
				folderStructure.insert(returnValue.node, position++);
				if (returnValue.hasNext == false) {
					break;
				}
			}
		}
		
		return folderStructure;
	}

	private GenerateTreeReturnValue generateTree (TreeWalk treeWalk) throws IOException {
		
		MutableTreeNode parentNode = new DefaultMutableTreeNode(treeWalk.getPathString());
		boolean hasNext;
		
		if (treeWalk.isSubtree()) {
			
			treeWalk.enterSubtree();
			
			int depth = treeWalk.getDepth();
			int position = 0;
			while (hasNext = treeWalk.next()) {
				if (treeWalk.getDepth() <= depth) {
					break;
				}
				GenerateTreeReturnValue childReturnValue = generateTree(treeWalk);
				parentNode.insert(childReturnValue.node, position++);
				hasNext = childReturnValue.hasNext;
			}
		}
		else {
			hasNext = treeWalk.next();
		}
		
		return new GenerateTreeReturnValue(parentNode, hasNext);
	}
	
	/*
	 * This method "generateTree" need to have two return values,
	 * one for whether there's a next element (due to the fact that
	 * JGit's "TreeWalk" only has a "next()" walk to the next 
	 * relevant entry but also return whether there's a next entry 
	 * but no "hasNext()".
	 */
	private class GenerateTreeReturnValue {

		private MutableTreeNode node;
		private boolean hasNext;
		
		public GenerateTreeReturnValue(MutableTreeNode node, boolean hasNext) {
			this.node = node;
			this.hasNext = hasNext;
		}
	}
	
	public TreeNode getFolderStructure () {
		return folderStructure;
	}
	
//	public List<String> getFolders () throws IOException {
//		
//		List<String> paths = new ArrayList<String>();
//		
//		Enumeration e = folderStructure.children();
//		while(e.hasMoreElements()) {
//			MutableTreeNode node = (MutableTreeNode)e.nextElement();
//			showHierarchy(node);
//		}
//		
////		treeWalk.reset();
//		while (treeWalk.next()) {
//			if (treeWalk.isSubtree()) {
//				paths.add(treeWalk.getPathString());
//			}
//		}
//		
//		return paths;	
//	}
//	
//	public List<String> getFiles () throws IOException {
//		
//		List<String> paths = new ArrayList<String>();
//		
////		treeWalk.reset();
//		while (treeWalk.next()) {
//			if (!treeWalk.isSubtree()) {
//				paths.add(treeWalk.getPathString());
//			}
//		}
//		
//		return paths;	
//	}
}