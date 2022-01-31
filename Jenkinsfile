pipeline {
  agent any
  stages {
    stage('Initialize') {
      steps {
        sh '''echo "PATH = ${PATH}"
echo "M2_HOME = ${M2_HOME}"
                    '''
      }
    }

    stage('Build') {
      steps {
        withMaven(jdk: 'JDK 17.0.2') {
          sh 'mvn install'
        }

      }
    }

  }
  tools {
    maven 'Maven 3.8.4'
    jdk 'JDK 17.0.2'
  }
}