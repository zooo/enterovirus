package enterovirus.gitar;

import java.io.File;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class GitSourceTest {

	@Test
	public void test1() throws GitAPIException {
		
		System.out.println(GitSource.getOrganizationName(new File("/path/user1/repo1.git")));
		System.out.println(GitSource.getRepositoryName(new File("/path/user1/repo1.git")));
	}

}