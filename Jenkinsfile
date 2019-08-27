pipeline {
    agent {
        dockerfile {
            dir 'build'
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
