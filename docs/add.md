# Add projects to MAYPAD
To add a project first add a projectgroup, then click on "Add project" and enter the Clone-Url of the project. Currently SVN and git projects are supported.

## Adding non-public projects
You can add a ServiceAccount or SSH-Key to add a non-public project to MAYPAD. For some of the most common repository hosting services, refer to the following instructions on how to create credentials for use with MAYPAD.

### Gitlab
A Maintainer or Owner can create a Deploy-Token for a project by going to Settings -> Repository -> Deploy Token. We recommend naming it "MAYPAD" and not setting a expiry date. You will need to add the "read\_repository" scope to the token. To use the token choose "ServiceAccount" as "Authentication Method" in MAYPAD, use the token name (starting with "gitlab+deploy-token-") as the username and the created token as the password.

### GitHub
Firstly, create a new SSH-Key without a password on your local machine. Add the public key as a Deploy Key in GitHub by going to the project you want to add -> Settings -> Deploy keys -> Add deploy key. We recommend naming it "MAYPAD". Write access is not needed. To use the key in MAYPAD choose "SSH-Key" as "Authentication Method" and paste the private (!) key of the newly created SSH-Key into MAYPAD.

### Bitbucket
Firstly, create a new SSH-Key without a password on your local machine. Add the public key as a Access Key in Bitbucket by going to the project you want to add -> Settings -> Access keys -> Add key. We recommend labeling it "MAYPAD". To use the key in MAYPAD choose "SSH-Key" as "Authentication Method" and paste the private (!) key of the newly created SSH-Key into MAYPAD.
