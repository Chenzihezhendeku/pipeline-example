node {
    
    //env.NODE_HOME=tool name: 'go', type: 'go'
    //env.PATH="/usr/local/go/bin:${env.PATH}"
    def DEPLOYREPO="pypi-virtual"
    
    stage('Prepare') {
        sh 'jfrog rt c art1 --url=http://192.168.230.155:8081/artifactory --user=admin --password=password'        // 此处使用域名不好使，具体原因待查
        sh 'jfrog rt use art1'
    }
    stage('SCM') {
        // cleanWs()
        sh 'ls'
        git([url: 'https://github.com/gyzong1/pipeline-example.git', branch: 'master'])
    }
    
    stage('Build') {
        dir('project-examples/python-example') {
          sh "jfrog rt pipi -r requirements.txt --build-name=${env.JOB_NAME} --build-number=${env.BUILD_NUMBER}"
        }
    }
    
    stage('Publish packages') {
        dir('project-examples/python-example') {
          sh "jfrog rt u dist/ ${DEPLOYREPO} --build-name=${env.JOB_NAME} --build-number=${env.BUILD_NUMBER}"
        }
    }

    stage('Install published package') {
        dir('project-examples/python-example') {
          sh "jfrog rt pip-install pythonProj"
        }
    }
    
    stage('Validate package') {
        dir('project-examples/python-example') {
          sh "pip show pythonProj"
        }
    }
    
    stage('Collect environment variables') {
        dir('project-examples/python-example') {
          sh "jfrog rt bce ${env.JOB_NAME} ${env.BUILD_NUMBER}"
        }
    }
    
    stage('Publish the build info') {
        dir('project-examples/python-example') {
          sh "jfrog rt bp ${env.JOB_NAME} ${env.BUILD_NUMBER}"
        }
    }
}
