pipeline {
    agent any

    tools {
        jdk 'JDK 21'          
        maven 'Maven 3.9.9'
    }

    stages {
        stage('Compilación') {
            steps {
                bat 'mvn compile'
            }
        }

        stage('Pruebas') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Empaquetado') {
            steps {
                bat 'mvn package'
            }
        }

    }

    post {
        success {
            echo '✅ Build completado exitosamente'
        }
        failure {
            echo '❌ Hubo un error durante el pipeline'
        }
    }
}
