pipeline {
    agent {
        docker {
            image 'android-package:0.0.1'
            args '-p 3000:3000 -p 5000:5000'
        }
    }

    options{
        timeout(time: 20, unit: 'MINUTES')
    }
    environment {
        CI = 'true'
    }

     triggers {
            gitlab(triggerOnPush: true, triggerOnMergeRequest: false, branchFilterType: 'All')
        }

    stages {
        stage('Build') {
            steps {
                sh 'git config --global http.version HTTP/1.1'
                sh 'git config --global http.postBuffer 524288000'
                sh './gradlew assemble'
            }
        }
        stage('Test') {
            steps {
               echo 'test'
            }
        }
//         stage('Deliver for development') {
//             when {
//                 branch 'development'
//             }
//             steps {
//                 sh './jenkins/scripts/deliver-for-development.sh'
//                 input message: 'Finished using the web site? (Click "Proceed" to continue)'
//                 sh './jenkins/scripts/kill.sh'
//             }
//         }
//         stage('Deploy for production') {
//             when {
//                 branch 'production'
//             }
//             steps {
//                 sh './jenkins/scripts/deploy-for-production.sh'
//                 input message: 'Finished using the web site? (Click "Proceed" to continue)'
//                 sh './jenkins/scripts/kill.sh'
//             }
//         }
    }
}
