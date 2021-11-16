pipeline {
    agent any
    tools {
        gradle "gradle"
    }
    stages {
        stage('Gradle') {
            steps {
                sh 'gradle -v'
            }
        }
    }
}
