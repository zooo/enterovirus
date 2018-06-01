package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitBareRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	static GitBareRepository getOne(TemporaryFolder folder) throws IOException, GitAPIException {
		
		Random rand = new Random();
		String name = "repo-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		File directory = folder.newFolder(name+".git");
		return new GitBareRepository(directory);
	}

	@Test
	public void testInit() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo.git");
		new GitBareRepository(directory);
		
		assertTrue(new File(directory, "branches").isDirectory());
		assertTrue(new File(directory, "hooks").isDirectory());
		assertTrue(new File(directory, "logs").isDirectory());
		assertTrue(new File(directory, "objects").isDirectory());
		assertTrue(new File(directory, "refs").isDirectory());
		assertTrue(new File(directory, "config").isFile());
		assertTrue(new File(directory, "HEAD").isFile());
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderNotExist() throws IOException, GitAPIException {
		
		File directory = new File("/a/path/which/does/not/exist");
		new GitBareRepository(directory);
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderReadOnly() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo.git");
		directory.setReadOnly();
		
		new GitBareRepository(directory);
	}
	
	@Test(expected = IOException.class)
	public void testDirectoryInitToOtherType() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");

		new GitNormalRepository(directory);
		new GitBareRepository(directory);
	}
	
	@Test
	public void testAddAHook() throws IOException, GitAPIException {
		
		GitRepository repository = getOne(folder);
		
		File hook = folder.newFile("whatever-name-for-the-hook-file");
		repository.addAHook(hook, "pre-receive");
		
		File targetHook = new File(new File(repository.directory, "hooks"), "pre-receive");
		assertTrue(targetHook.isFile());
		assertTrue(targetHook.canExecute());
	}
	
	@Test
	public void testAddHooks() throws IOException, GitAPIException {
		
		GitRepository repository = getOne(folder);
		
		File hooks = folder.newFolder("hooks");
		new File(hooks, "pre-receive").createNewFile();
		repository.addHooks(hooks);
		
		File targetHook = new File(new File(repository.directory, "hooks"), "pre-receive");
		assertTrue(targetHook.isFile());
		assertTrue(targetHook.canExecute());
	}
}
