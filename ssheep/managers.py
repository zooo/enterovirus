from sqlalchemy import or_

from models import (
    Member,
    Organization,
    Repository,
    RepositoryMemberMap,
    SshKey
)


class OrganizationMemberMapManager():

    @staticmethod
    def __get_by_username_and_org_name(session, username, org_name):
        return session.query(OrganizationMemberMap).\
            join(Organization).join(Member).\
            filter(Member.username == username).\
            filter(Organization.name == org_name).\
            all()

    @staticmethod
    def is_member_in_org(session, username, org_name):
        maps = OrganizationMemberMapManager.__get_by_username_and_org_name(
            session, username, org_name)
        if len(maps) >= 1:
            return True
        else:
            return False


class RepositoryManager():

    @staticmethod
    def get_by_repo_name_and_org_name(session, repo_name, org_name):
        return session.query(Repository).join(Organization).\
            filter(Repository.name == repo_name).\
            filter(Organization.name == org_name).\
            one()


class RepositoryMemberMapManager():

    @staticmethod
    def __filter_user_and_repo_name(
            session, username, repo_name):
        return session.query(RepositoryMemberMap).\
            join(Repository).join(Member).\
            filter(Member.username == username).\
            filter(Repository.name == repo_name)

    @staticmethod
    def __filter_user_and_org_and_repo_name(
            session, username, org_name, repo_name):
        return session.query(RepositoryMemberMap).\
            join(Repository).join(Organization).join(Member).\
            filter(Member.username == username).\
            filter(Organization.name == org_name).\
            filter(Repository.name == repo_name)

    @staticmethod
    def __is_user_repo_organizer_or_editor(session, username, org_name, repo_name):
        maps = RepositoryMemberMapManager.__filter_user_and_org_and_repo_name(
            session, username, org_name, repo_name).\
            filter(or_(
                RepositoryMemberMap.role_shortname == 'O',
                RepositoryMemberMap.role_shortname == 'E')).all()

        if len(maps) >= 1:
            return True
        else:
            return False

    @staticmethod
    def __is_user_in_black_list(session, username, repo_name):
        maps = RepositoryMemberMapManager.__filter_user_and_repo_name(
            session, username, repo_name).\
            filter(RepositoryMemberMap.role_shortname == 'O').all()
        if len(maps) >= 1:
            return True
        else:
            return False

    # TODO:
    # Testing (but readable has no need to check from `ssheep`)
    @staticmethod
    def is_readable(session, username, org_name, repo_name):
        repo = RepositoryManager.get_by_repo_name_and_org_name(
            session, repo_name, org_name)

        if repo.is_public:
            return not RepositoryMemberMapManager.__is_user_in_black_list(session, username, repo_name)

        else:
            if OrganizationMemberMapManager.is_member_in_org(session, username, org_name):
                return not RepositoryMemberMapManager.__is_user_in_black_list(session, username, repo_name)
            else:
                return False

    @staticmethod
    def is_editable(session, username, org_name, repo_name):
        return RepositoryMemberMapManager.__is_user_repo_organizer_or_editor(session, username, org_name, repo_name)


class SshKeyManager():

    @classmethod
    def get_all_ssh_keys(cls, session):
        return session.query(SshKey).join(Member).all()

    # TODO:
    # Consider using `ssh-copy-id`, which may be relatively safer comparing to
    # manually change the `.ssh/authorized_keys` file.
    # https://linux.die.net/man/1/ssh-copy-id
    @classmethod
    def get_plain_authorized_keys_file_content(cls, session):
        output = ""
        for ssh_key in cls.get_all_ssh_keys(session):
            output += ssh_key.get_authorized_keys_line()
        return output

    @classmethod
    def get_force_command_authorized_keys_file_content(cls, session):
        output = ""
        for ssh_key in cls.get_all_ssh_keys(session):
            options = [
                "command=\"sh /ssheep/check_if_can_edit_repository.sh {}\"".format(ssh_key.member.username),
                "no-port-forwarding",
                "no-x11-forwarding",
                "no-agent-forwarding",
                "no-pty"
            ]
            output += ssh_key.get_authorized_keys_line(options)
        return output
