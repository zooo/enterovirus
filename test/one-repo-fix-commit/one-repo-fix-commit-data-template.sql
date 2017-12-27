INSERT INTO config.member VALUES
	(1, 'one-repo-fix-commit-username', 'one-repo-fix-commit-password', 'ONE-REPO-FIX-COMMIT-USERNAME', 'email@email.com');
ALTER SEQUENCE config.member_id_seq RESTART WITH 2;

INSERT INTO config.organization VALUES
	(1, 'one-repo-fix-commit-org', 'ONE-REPO-FIX-COMMIT-ORG'),
ALTER SEQUENCE config.organization_id_seq RESTART WITH 2;

INSERT INTO config.organization_manager_map VALUES
	(1, 1);

INSERT INTO config.repository VALUES
	(1, 1, 'one-repo-fix-commit-repo', 'ONE-REPO-FIX-COMMIT-REPO', '/home/beta/Workspace/enterovirus-test/one-repo-fix-commit.git');
ALTER SEQUENCE config.repository_id_seq RESTART WITH 2;

INSERT INTO git.git_commit VALUES
	(1, 1, TO-BE-1ST-COMMIT-SHA),
	(2, 1, TO-BE-2ND-COMMIT-SHA);
ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 3;