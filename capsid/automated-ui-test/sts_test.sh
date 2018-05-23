echo "\n"
echo "Reset/Initialize the database"
echo "============================="
cd $HOME/Workspace/enterovirus/database
sh setup.sh

echo "\n"
echo "Reset/Initialize the git storage"
echo "================================"
mkdir $HOME/Workspace/enterovirus-test/fake_server
mkdir $HOME/Workspace/enterovirus-test/fake_server/.ssh
cd $HOME/Workspace/enterovirus-test/fake_server
rm -rf *
cd $HOME/Workspace/enterovirus-test/fake_server/.ssh
rm -rf *
touch authorized_keys

echo "\n"
echo "Reset/Initialize fake client"
echo "============================"
mkdir $HOME/Workspace/enterovirus-test/fake_client
cd $HOME/Workspace/enterovirus-test/fake_client
rm -rf *

echo "\n"
echo "Run automatic UI test"
echo "====================="
cd $HOME/Workspace/enterovirus/capsid/automated-ui-test

# UI automatic test.
# URL without the "/" at the end of it
#
# This will write the .ssh/autorized_keys to a fake position.
# But it doesn't matter, since "git clone" is by local protocol.
python3 ui_init.py http://localhost:8888

# "git clone" by local protocol
sh git_init_repo1.sh $HOME/Workspace/enterovirus-test/fake_server/org1/repo1.git
sh git_init_repo2.sh $HOME/Workspace/enterovirus-test/fake_server/org1/repo2.git
