node {

    env.NODE_HOME=tool name: 'nodejs', type: 'nodejs'
    env.PATH="${env.NODE_HOME}/bin:${env.PATH}"

    def server = Artifactory.server 'art1'
    def rtNpm = Artifactory.newNpmBuild()
    def buildInfo

    stage ('Clone') {
        git url: 'https://github.com/gyzong1/pipeline-example.git'
    }

    stage ('Artifactory configuration') {
        rtNpm.deployer repo: 'npm-dev-local', server: server
        rtNpm.resolver repo: 'npm-virtual', server: server
        rtNpm.tool = 'nodejs' // Tool name from Jenkins configuration
        buildInfo = Artifactory.newBuildInfo()
    }


    stage ('Npm install') {
        rtNpm.install buildInfo: buildInfo, path: 'project-examples/npm-example'
    }

    stage ('Npm publish') {
        rtNpm.publish buildInfo: buildInfo, path: 'project-examples/npm-example'
    }

    stage ('Publish build info') {
        server.publishBuildInfo buildInfo
    }
    
    /*
    stage ('Copy and rename') {
        sh "curl -uadmin:password -X POST 'http://192.168.230.155:8081/artifactory/api/copy/npm-dev-local/npm-example/-/npm-example-0.0.3.tgz?to=/npm-local/npm-example/-/npm-example-0.0.3.${env.BUILD_NUMBER}.tgz'"
    }
    */
}
