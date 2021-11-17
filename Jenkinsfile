pipeline {
    agent any
    tools {
        gradle "gradle"
    }
    stages {
        stage('Gradle version') {
            steps {
                sh 'gradle -v'
            }
        }

       stage('Build'){
            steps{
                 sh "./gradlew clean -Ppublish assemble"
            }
       }
    }
}
