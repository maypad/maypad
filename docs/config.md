# Configuration of projects
Projects in MAYPAD are configured using the `maypad.yaml` configuration file in the project's repository's root directory.

## `maypad.yaml`
Full reference:
```
projectName: cool_name                          # STRING, name of the project
projectDescription: lorem ipsum dolor sit amet  # STRING, description of the project
allBranches: true                               # BOOLEAN, should all branches automatically be added to MAYPAD
svnTrunkDirectory: trunk                        # STRING, OPTIONAL, required for SVN, directory of SVN trunk
svnBranchDirectory: branches                    # STRING, OPTIONAL, required for SVN, directory of SVN branches
svnTagsDirectory: tags                          # STRING, OPTIONAL, required for SVN, directory of SVN tags
repoWebsiteUrl: https://www.github.com/maxmustermann/cooles_projekt # STRING, OPTIONAL, link to the repository webpage

branches:                                       # ARRAY, configuration of managed branches
  - name: master                                # STRING, reference to the branch
    description: Lorem Ipsum                    # STRING, OPTIONAL, description of the branch
    members:                                    # ARRAY[STRING], OPTIONAL, project members
      - Max
      - Daniel
      - Lukas
      - Jonas
      - Julian
    mails:                                      # ARRAY[STRING], OPTIONAL, mails of project members
      - example.mail@gmail.com
      - mail.example@protonmail.com
    build:                                      # HASH, configuration of the build pipeline
      type: webhook                             # STRING, type of pipeline, currently only "webhook" available
      method: POST                              # STRING, method to call the webhook with
      url: https://greatBuild.com/12345abc      # STRING, url of the webhook
      headers:                                  # ARRAY[HASH], OPTIONAL, headers to be send with webhook
        - key: key1
          values:
            - value1
            - value2
        - key: key2
          values:
            - value1
            - value2
      body: "{}"                                # STRING, OPTIONAL, body to be send with webhook
    deployment:                                 # HASH, OPTIONAL, configuration of deployment
      name: Great-Deployment                    # STRING, name of deployment
      type: webhook                             # STRING, type of deployment, currently only "webhook" available
      method: POST                              # STRING, method to call webhook with
      url: https://deploy.maypad.de/54321abcd   # STRING, url of the webhook
    dependsOn:                                  # ARRAY[STRING], OPTIONAL, dependencies of the branch, in form "MAYPAD_PROJECT_ID:BRANCH"
      - 2:master
      - 5:master
```
Example:
```
projectName: Maven Test project
projectDescription: This is just a test project using maven and gitlabci on a gitlab
allBranches: true

branches:
  - name: master
    description: master specific description
    dependsOn:
      - 2:master
    members:
      - Max
      - Daniel
      - Lukas
      - Jonas
      - Julian
    mails:
      - contact@maypad.de
    build:
      type: webhook
      method: POST
      url: https://gitlab.maypad.de/api/v4/projects/23/ref/master/trigger/pipeline?token=7910c2d7acc27b5285ebaa2486c18
    deployment:
      type: webhook
      name: Prod-Umgebung
      url: https://deploy.maypad.de/mvn-test-project/fanvndfhjbvahhdfbvhadbf
```

## Adding your CI system to MAYPAD
In your project's `maypad.yaml` you can add hooks to your project's CI system. This enables MAYPAD to start builds and deployments from it's web interface and also allows builds for dependencies. Refer to the following instructions on how to create to webhook for your CI system:

### Gitlab CI
You can create a pipeline trigger via Settings -> CI/CD -> Pipeline triggers. Name it for example maypad and click on create. Refer to the "Use webhook" documentation and add the just created trigger token and the `REF_NAME` which is the name of the branch that is being configured in the `maypad.yaml`. Add this webhook to the project's configuration using the POST method. This could look like this:
```
build:
  type: webhook
  method: POST
  url: https://gitlab.maypad.de/api/v4/projects/23/ref/master/trigger/pipeline?token=7910c2d7acc27b5285ebaa2486c18
```

### Jenkins
You can trigger a Jenkins job using build triggers. On your configured project in Jenkins go to Configuration -> Build Triggers. Enable trigger builds remotely and refer to the documentation on the page for the trigger's URL. Add this webhook to the project's `maypad.yaml` using the POST method. This could look like this:
```
build:
  type: webhook
  method: POST
  url: https://jenkins.maypad.de/job/Example/build?token=7910c2d7acc27b5285ebaa2486c18
```

### Bamboo
You can trigger a Bamboo build using the following example. Adjust the webhook URL to your project. Note that supplying the username and password here is potentially a security issue. Consider sending the Login information via a `Basic-Authentication` header attribute.
```
build:
  type: webhook
  method: POST
  url: http://admin:password@bamboo-host:8085/rest/api/latest/queue/PLAN-KEY?os_authType=basic
```

## Adding MAYPAD hooks to your CI-Pipeline
To automatically update the build status based on the success or failure of your pipeline, MAYPAD provides two webhooks for each project that your pipeline should call appropriately. The webhook Urls are listed in the branch detail page at "Build Success Url" and "Build Failure Url". An example configuration for Gitlab-CI could look like this:
```
stages:
  ... # Your already existing stages
  - notify

... # Your already existing jobs

notify_success:
  stage: notify
  script:
    - curl https://demo.maypad.de/hooks/jImxzmYys2CndjavnKBo4zqq

notify_failure:
  stage: notify
  when: on_failure
  script:
    - curl https://demo.maypad.de/hooks/D4SiwrJpjepcl54UfQchjbU4
```
