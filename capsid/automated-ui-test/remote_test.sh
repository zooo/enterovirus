# TODO:
# After the tomcat user changes, the /webapps folder can
# nolonger auto deploy the new site. Quick solution below,
# need to find a better solution later.
#
#sh ssh.sh
#cd /var/lib/tomcat8/webapps
#sudo rm -rf ROOT
#sudo rm ROOT.war

# Shall first setup the server
#cd $HOME/Workspace/enterovirus/deployment/ec2
#sh scp.sh
#sh ssh.sh
#cd /tmp/
#sh reset.sh

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
python3 ui_init.py http://gitenter.com

# "git clone" by SSH protocol
sh git_init_repo1.sh git@gitenter.com:org1/repo1.git
sh git_init_repo2.sh git@gitenter.com:org1/repo2.git
