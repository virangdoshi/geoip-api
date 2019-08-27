pipeline {
    agent {
        dockerfile {
            dir 'build'
            args '''
                  --group-add 999
                  --network=host
                  -v /etc/passwd:/etc/passwd
                  -v /etc/group:/etc/group
                  -v /var/run/docker.sock:/var/run/docker.sock
                  -v ${JENKINS_HOME}/.docker/config.json:${JENKINS_HOME}/.docker/config.json
                  -v ${JENKINS_HOME}/.m2:/${JENKINS_HOME}/.m2
                  '''
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
                sh '''
                    latest_tag='hub.s24.com/s24/geoip-api'
                    version_tag="${latest_tag}:$(git rev-parse --short HEAD)"
                    docker build -t "${latest_tag}" -t "${version_tag}" .
                    docker push "${latest_tag}"
                    docker push "${version_tag}"
                '''
            }
        }
    }
}
