pipeline {
    agent any

    tools {
        jdk 'JDK 17'
        maven 'Maven 3.9.4'
    }

    stages {
        stage('Compilar proyecto') {
            steps {
                sh 'mvn clean install'
            }
        }
    }
}
