pipeline {
  agent any
  stages {
    stage('Initialize') {
      steps {
        sh '''
                        echo "PATH = ${PATH}"
                        echo "M2_HOME = ${M2_HOME}"
                    '''
      }
    }

    stage('Build') {
      post {
        success {
          junit 'target/surefire-reports/**/*.xml'
        }

      }
      steps {
        sh 'mvn -Dmaven.test.failure.ignore=true install'
      }
    }

  }
  tools {
    maven 'Maven 3.8.4'
    jdk 'JDK 17.0.2'
  }
  environment {
    JAVA_HOME = '/home/ubuntu/jenkins/data/tools/hudson.model.JDK/JDK_17.0.2'
  }
}