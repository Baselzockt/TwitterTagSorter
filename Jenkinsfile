pipeline {
    agent any
    environment {
        JAVA_HOME = "data/tools/hudson.model.JDK/JDK_17.0.2"
    }
    tools { 
        maven 'Maven 3.8.4' 
        jdk 'JDK 17.0.2' 
    }
    stages {
            stage ('Initialize') {
                steps {
                    sh '''
                        echo "PATH = ${PATH}"
                        echo "M2_HOME = ${M2_HOME}"
                    '''
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
