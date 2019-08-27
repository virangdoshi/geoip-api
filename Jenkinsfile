pipeline {
    agent {
        dockerfile {
            dir 'build'
            args """
                  --group-add 999
                  --network=host
                  -v /etc/passwd:/etc/passwd
                  -v /etc/group:/etc/group
                  -v /var/run/docker.sock:/var/run/docker.sock
                  -v ${JENKINS_HOME}/.m2:/${JENKINS_HOME}/.m2
                  """
        }
    }

    options {
        ansiColor 'xterm'
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    stages {
        stage('Compile') {
            steps {
                sh './mvnw -B -Dstyle.color=always -Djansi.force=true clean compile'
            }
        }
        stage('Test') {
            steps {
                sh './mvnw -B -Dstyle.color=always -Djansi.force=true verify'
            }
            post {
                always {
                    junit 'target/**/*.xml'
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                sh "docker build ."
            }
        }
    }
}
