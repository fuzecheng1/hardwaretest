pipeline {
    agent any
    tools {
        gradle "gradle7.1"
    }
    stages {
        stage('Gradle') {
            steps {
                sh 'gradle -v'
            }
        }
    }
}
