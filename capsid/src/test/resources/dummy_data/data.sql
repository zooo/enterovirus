INSERT INTO config.member VALUES
	(1, 'user1', 'user1', 'USER1', 'ann@ann.com'),
	(2, 'user2', 'user2', 'USER2', 'bell@bell.com'),
	(3, 'user3', 'user3', NULL, 'cindy@cindy.com');
ALTER SEQUENCE config.member_id_seq RESTART WITH 4;

INSERT INTO config.organization VALUES
	(1, 'org1', 'ORG1'),
	(2, 'org2', 'ORG2'),
	(3, 'org3', NULL);
ALTER SEQUENCE config.organization_id_seq RESTART WITH 4;

INSERT INTO config.organization_manager_map VALUES
	(1, 1),
	(1, 2),
	(2, 2),
	(2, 3),
	(3, 3);

INSERT INTO config.repository VALUES
	(1, 1, 'repo1', 'REPO1', '/home/beta/Workspace/enterovirus_data/org1/repo1.git'),
	(2, 1, 'repo2', NULL, 'https://git.com/gov/bbb.git'),
	(3, 2, 'repo3', 'AAA', 'https://git.com/ngo/aaa.git');
ALTER SEQUENCE config.repository_id_seq RESTART WITH 4;

INSERT INTO git.git_commit VALUES
	(1, 1, 'c3474227d51ed985a4bf12c3099a68d6dbc11a77'),
	(2, 1, '834a67585dcd0a83e09e8fa34a6741bad1f0be73'),
	(3, 1, '86e9b06f5ebd285eaa2dcef1ba10451cbe8037e9'),
	(4, 1, 'a1ee78d350f2b5f92311bcf3d008b07b943f94ac'),
	(5, 1, 'ac211df0fbe5e2368ba82f1c26a1f3aab192fc35'),
	(6, 1, 'ff728f5674201025b9fc4ea76a0adde3323fb9fb'),
	(7, 1, '841d9d8cb6c560f1efc4ff677b8c71362d71203c');
ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 8;

INSERT INTO git.document VALUES
	(1, 6),
	(2, 6),
	(3, 7),
	(4, 7),
	(5, 7);

INSERT INTO git.document_modified VALUES
	(1, 'folder_1/same-name-file'),
	(2, 'test-add-a-file-from-client_1'),
	(3, 'folder_2/same-name-file'),
	(4, 'test-add-a-file-from-client_1');

INSERT INTO git.document_unmodified VALUES
	(5, 1);