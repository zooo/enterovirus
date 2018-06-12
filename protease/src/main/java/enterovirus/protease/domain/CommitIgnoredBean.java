package enterovirus.protease.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit_ignored")
public class CommitIgnoredBean extends CommitBean {

	/*
	 * This default constructor is needed for Hibernate.
	 */
	public CommitIgnoredBean () {
		super();
	}
	
	public CommitIgnoredBean (RepositoryBean repository, String commitSha) {
		super(repository, commitSha);
	}
}
