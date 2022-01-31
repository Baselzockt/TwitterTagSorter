pipeline {
    agent any
    tools { 
        maven 'Maven 3.8.4' 
        jdk 'JDK 17.0.2' 
    }
    stages {
        stage('Initialization') {
            steps {
                sh 'echo %JAVA_HOME%'
                sh 'javac --version'
            }
        }
       stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true install' 
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml' 
                }
            }
        }
    }
}
